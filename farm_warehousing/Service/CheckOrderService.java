package com.itbaizhan.farm_warehousing.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.farm_common.Utils.OrderNumberUtil;
import com.itbaizhan.farm_common.Utils.SecurityUtil;
import com.itbaizhan.farm_common.exception.Busexception;
import com.itbaizhan.farm_common.result.CodeEnum;
import com.itbaizhan.farm_warehousing.entity.*;
import com.itbaizhan.farm_warehousing.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 盘库单服务层
 */
@Service
@Transactional
public class CheckOrderService {
    @Autowired
    private CheckOrderMapper checkOrderMapper;
    @Autowired
    private CheckOrderDetailMapper checkOrderDetailMapper;
    @Autowired
    private OrderNumberUtil orderNumberUtil;
    @Autowired
    private ReceiptOrderMapper receiptOrderMapper;
    @Autowired
    private ReceiptOrderDetailMapper receiptOrderDetailMapper;
    @Autowired
    private ShipmentOrderMapper shipmentOrderMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;
    @Autowired
    private InventoryHistoryMapper inventoryHistoryMapper;

    /**
     * 查询盘库单分页列表
     *
     * @param pageNo           页码
     * @param pageSize         每页数量
     * @param checkOrderNo     盘库单号，可选
     * @param checkOrderStatus 盘库单状态，可选（-1作废 0未盘库 1已盘库）
     * @return 盘库单分页列表
     */
    public IPage<CheckOrder> selectCheckOrderPage(Integer pageNo, Integer pageSize, String checkOrderNo, Integer checkOrderStatus) {
        Page<CheckOrder> page = new Page<>(pageNo, pageSize);
        QueryWrapper<CheckOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(checkOrderNo)) {
            queryWrapper.like("check_order_no", checkOrderNo);
        }
        if (checkOrderStatus != null) {
            queryWrapper.eq("check_order_status", checkOrderStatus);
        }
        queryWrapper.orderByDesc("create_time");
        return checkOrderMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询盘库单
     *
     * @param id 盘库单ID
     * @return 盘库单详细信息，包含盘库单详情列表
     */
    public CheckOrder getCheckOrderById(Long id) {
        CheckOrder checkOrder = checkOrderMapper.selectById(id);
        if (checkOrder != null) {
            QueryWrapper<CheckOrderDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("check_order_id", id);
            List<CheckOrderDetail> checkOrderDetails = checkOrderDetailMapper.selectList(queryWrapper);
            checkOrder.setDetailList(checkOrderDetails);
        }
        return checkOrder;
    }

    /**
     * 添加盘库单
     *
     * @param checkOrder 盘库单信息
     * @return 盘库单号
     */
    public String addCheckOrder(CheckOrder checkOrder) {
        String userName = SecurityUtil.getUserName();
        checkOrder.setCreateBy(userName);
        checkOrder.setUpdateBy(userName);
        checkOrder.setCreateTime(LocalDateTime.now());
        checkOrder.setUpdateTime(LocalDateTime.now());

        // 设置初始状态
        checkOrder.setCheckOrderStatus(0); // 未盘库
        // 生成盘库单号
        String checkOrderNo = "PK" + orderNumberUtil.getOrderNumber();
        checkOrder.setCheckOrderNo(checkOrderNo);
        checkOrderMapper.insert(checkOrder);
        return checkOrderNo;
    }

    /**
     * 修改盘库单
     *
     * @param checkOrder 盘库单信息
     * @return 修改结果
     */
    public boolean updateCheckOrder(CheckOrder checkOrder) {
        if (checkOrder.getCheckOrderStatus() == 1) {
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_ALL_READY);
        }
        String userName = SecurityUtil.getUserName();
        checkOrder.setUpdateBy(userName);
        checkOrder.setUpdateTime(LocalDateTime.now());
        return checkOrderMapper.updateById(checkOrder) > 0;
    }

    /**
     * 删除盘库单
     *
     * @param id 盘库单id
     * @return 操作结果
     */
    public boolean deleteCheckOrder(Long id) {
        // 检查盘库单状态
        CheckOrder checkOrder = checkOrderMapper.selectById(id);
        if (checkOrder.getCheckOrderStatus() == 1) {
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_ALL_READY);
        }
        // 删除盘库单详情
        QueryWrapper<CheckOrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("check_order_id", id);
        checkOrderDetailMapper.delete(queryWrapper);
        // 删除盘库单
        return checkOrderMapper.deleteById(id) > 0;
    }

    /**
     * 盘库单详情
     *
     * @param id
     * @return
     */
    public boolean completeCheck(Long id) {
        CheckOrder checkOrder = this.getCheckOrderById(id);
        if (checkOrder == null) {
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_NOT_EXIST);
        }
        if (checkOrder.getCheckOrderStatus() == 1) {
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_ALL_READY);
        }
        List<CheckOrderDetail> detailList = checkOrder.getDetailList();
        if (detailList == null || detailList.isEmpty()) {
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_DETAIL_ISNULL);
        }
        // 拆分出 盘盈入库 和 盘亏出库 的盘库单详情
        List<CheckOrderDetail> profitList = new ArrayList<>(); // 盘盈列表
        List<CheckOrderDetail> lossListList = new ArrayList<>(); // 盘亏列表
        for (CheckOrderDetail detail : detailList) {
            // 盘点数量（实际数量） - 库存数量（系统数量） = 变化数量
            BigDecimal diff = detail.getCheckQuantity().subtract(detail.getQuantity());
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                // 盘盈：变化数量 > 0
                profitList.add(detail);
            } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                // 盘亏：变化数量 < 0
                lossListList.add(detail);
            }
            // 相等的不处理
        }
        // 生成盘盈入库单
        profitInbound(checkOrder, profitList);

        // 生成盘亏出库单
        lossOutbound(checkOrder, lossListList);

        // 更新库存
        updateInventory(checkOrder);

        // 更新库存详情
        updateInventoryDetail(checkOrder);

        // 保存库存操作历史记录
        saveInventoryHistory(checkOrder);

        // 更新盘库单状态
        checkOrder.setCheckOrderStatus(1); // 1:已盘库
        checkOrder.setUpdateTime(LocalDateTime.now());
        checkOrder.setUpdateBy(SecurityUtil.getUserName());
        return checkOrderMapper.updateById(checkOrder) > 0;
    }
    //生成盘盈入库单
    private void profitInbound(CheckOrder checkOrder, List<CheckOrderDetail> detailList) {
        ReceiptOrder receiptOrder = new ReceiptOrder();
        receiptOrder.setReceiptOrderNo("RK"+ orderNumberUtil.getOrderNumber());
        receiptOrder.setReceiptOrderType(2);
        receiptOrder.setReceiptOrderStatus(1);
        receiptOrder.setWarehouseId(checkOrder.getWarehouseId());
        receiptOrder.setAreaId(checkOrder.getAreaId());
        receiptOrder.setRemark(checkOrder.getCheckOrderNo());
        // 入库单备注盘库单号
        String userName = SecurityUtil.getUserName();
        receiptOrder.setCreateBy(userName);
        receiptOrder.setUpdateBy(userName);
        receiptOrder.setCreateTime(LocalDateTime.now());
        receiptOrder.setUpdateTime(LocalDateTime.now());
        receiptOrder.setReceiptOrderStatus(1); // 已入库
        //计算总入库 数量
        BigDecimal quantitysum = BigDecimal.ZERO;
        for (CheckOrderDetail detail : detailList){
            BigDecimal quantity = detail.getCheckQuantity().subtract(detail.getQuantity());
            quantitysum = quantitysum.add(quantity);
        }
        receiptOrder.setTotalQuantity(quantitysum);
        receiptOrderMapper.insert(receiptOrder);

        // 生成入库单详情
        for (CheckOrderDetail detail : detailList) {
            ReceiptOrderDetail receiptOrderDetail = new ReceiptOrderDetail();
            receiptOrderDetail.setReceiptOrderId(receiptOrder.getId());
            receiptOrderDetail.setQuantity(detail.getCheckQuantity().subtract(detail.getQuantity()));
            receiptOrderDetail.setSkuId(detail.getSkuId());
            receiptOrderDetail.setWarehouseId(detail.getWarehouseId());
            receiptOrderDetail.setAreaId(detail.getAreaId());
            receiptOrderDetail.setProductionDate(detail.getProductionDate());
            receiptOrderDetail.setExpirationDate(detail.getExpirationDate());

            receiptOrderDetail.setCreateBy(userName);
            receiptOrderDetail.setUpdateBy(userName);
            receiptOrderDetail.setCreateTime(LocalDateTime.now());
            receiptOrderDetail.setUpdateTime(LocalDateTime.now());
            receiptOrderDetailMapper.insert(receiptOrderDetail);
        }
}   //生成盘亏出库单
    private void lossOutbound(CheckOrder checkOrder, List<CheckOrderDetail> detailList){
        //生成盘亏出库单
        ShipmentOrder shipmentOrder = new ShipmentOrder();
        shipmentOrder.setShipmentOrderNo("CK"+orderNumberUtil.getOrderNumber());
        shipmentOrder.setShipmentOrderType(2);
        shipmentOrder.setWarehouseId(checkOrder.getWarehouseId());
        shipmentOrder.setAreaId(checkOrder.getAreaId());
        shipmentOrder.setRemark(checkOrder.getCheckOrderNo()); // 出库单备注盘库单号
        String userName = SecurityUtil.getUserName();
        shipmentOrder.setCreateBy(userName);
        shipmentOrder.setUpdateBy(userName);
        shipmentOrder.setCreateTime(LocalDateTime.now());
        shipmentOrder.setUpdateTime(LocalDateTime.now());
        shipmentOrder.setShipmentOrderStatus(1); // 已出库
        // 计算总出库数量
        BigDecimal quantitySum = BigDecimal.ZERO;
        for (CheckOrderDetail detail : detailList){
            BigDecimal quantity = detail.getCheckQuantity().subtract(detail.getQuantity()).negate();
            quantitySum = quantitySum.add(quantity);
        }
        shipmentOrder.setTotalQuantity(quantitySum);

        shipmentOrderMapper.insert(shipmentOrder);
    }
    //更新库存单
    public void updateInventory(CheckOrder checkOrder){

        List<CheckOrderDetail> detailList = checkOrder.getDetailList();
        for(CheckOrderDetail detail:detailList){
            QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("Warehouse_id",detail.getWarehouseId())
                    .eq("Area_id",detail.getAreaId())
                    .eq("Sku_id",detail.getSkuId());
            Inventory inventory = inventoryMapper.selectOne(queryWrapper);
            inventory.setQuantity(detail.getCheckQuantity());
            inventory.setUpdateBy(SecurityUtil.getUserName());
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.updateById(inventory);
        }
    }
    public void updateInventoryDetail(CheckOrder checkOrder){
        List<CheckOrderDetail> detailList = checkOrder.getDetailList();
        for (CheckOrderDetail detail : detailList) {
            QueryWrapper<InventoryDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("Warehouse_id",detail.getWarehouseId())
                    .eq("Area_id",detail.getAreaId())
                    .eq("Sku_id",detail.getSkuId());
            InventoryDetail inventoryDetail = inventoryDetailMapper.selectOne(queryWrapper);
            inventoryDetail.setRemainQuantity(detail.getQuantity());
            String userName = SecurityUtil.getUserName();
            inventoryDetail.setUpdateBy(userName);
            inventoryDetail.setUpdateTime(LocalDateTime.now());
            inventoryDetailMapper.updateById(inventoryDetail);
        }
    }
    // 保存库存操作历史记录
    public void  saveInventoryHistory(CheckOrder checkOrder){
        List<CheckOrderDetail> detailList = checkOrder.getDetailList();
        for(CheckOrderDetail detail:detailList){
            InventoryHistory inventoryHistory = new InventoryHistory();
            inventoryHistory.setOrderType(4); // 4:盘库
            inventoryHistory.setWarehouseId(detail.getWarehouseId());
            inventoryHistory.setAreaId(detail.getAreaId());
            inventoryHistory.setSkuId(detail.getSkuId());
            // 盘点数量（实际数量） - 库存数量（系统数量） = 变化数量
            inventoryHistory.setQuantity(detail.getCheckQuantity().subtract(detail.getQuantity()));
            inventoryHistory.setProductionDate(detail.getProductionDate());
            inventoryHistory.setExpirationDate(detail.getExpirationDate());
            inventoryHistory.setOrderId(checkOrder.getId());
            inventoryHistory.setOrderNo(checkOrder.getCheckOrderNo());

            String userName = SecurityUtil.getUserName();
            inventoryHistory.setCreateBy(userName);
            inventoryHistory.setUpdateBy(userName);
            inventoryHistory.setCreateTime(LocalDateTime.now());
            inventoryHistory.setUpdateTime(LocalDateTime.now());
            inventoryHistoryMapper.insert(inventoryHistory);
        }

        }

    }


