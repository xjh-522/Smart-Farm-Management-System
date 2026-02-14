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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移库单服务层
 */
@Service
@Transactional
public class MovementOrderService {
    @Autowired
    private MovementOrderMapper movementOrderMapper;
    @Autowired
    private MovementOrderDetailMapper movementOrderDetailMapper;
    @Autowired
    private OrderNumberUtil orderNumberUtil;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;
    @Autowired
    private InventoryHistoryMapper inventoryHistoryMapper;

    /**
     * 查询移库单分页列表
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @param movementOrderNo 移库单号，可选
     * @param movementOrderStatus 移库单状态，可选（0待移库 1已移库）
     * @return 移库单分页列表
     */
    public IPage<MovementOrder> selectMovementOrderPage(Integer pageNo, Integer pageSize, String movementOrderNo, Integer movementOrderStatus){
        Page<MovementOrder> page = new Page<>(pageNo,pageSize);
        QueryWrapper<MovementOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(movementOrderNo)){
            queryWrapper.like("movement_order_no",movementOrderNo);
        }
        if (movementOrderStatus != null){
            queryWrapper.eq("movement_order_status",movementOrderStatus);
        }
        queryWrapper.orderByDesc("create_time");
        return movementOrderMapper.selectPage(page,queryWrapper);
    }

    /**
     * 根据ID查询移库单
     * @param id 移库单ID
     * @return 移库单详细信息，包含移库单详情列表
     */
    public MovementOrder getMovementOrderById(Long id){
        MovementOrder movementOrder = movementOrderMapper.selectById(id);
        if (movementOrder != null){
            QueryWrapper<MovementOrderDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("movement_order_id",id);
            List<MovementOrderDetail> movementOrderDetails = movementOrderDetailMapper.selectList(queryWrapper);
            movementOrder.setDetailList(movementOrderDetails);
        }
        return movementOrder;
    }

    /**
     * 添加移库单
     * @param movementOrder 移库单信息
     * @return 移库单号
     */
    public String addMovementOrder(MovementOrder movementOrder){
        String userName = SecurityUtil.getUserName();
        movementOrder.setCreateBy(userName);
        movementOrder.setUpdateBy(userName);
        movementOrder.setCreateTime(LocalDateTime.now());
        movementOrder.setUpdateTime(LocalDateTime.now());

        // 设置初始状态
        movementOrder.setMovementOrderStatus(0); // 待移库
        // 生成移库单号
        String movementOrderNo = "YK" + orderNumberUtil.getOrderNumber();
        movementOrder.setMovementOrderNo(movementOrderNo);
        movementOrderMapper.insert(movementOrder);
        return movementOrderNo;
    }

    /**
     * 修改移库单
     * @param movementOrder 移库单信息
     * @return 修改结果
     */
    public boolean updateMovementOrder(MovementOrder movementOrder){
        if (movementOrder.getMovementOrderStatus() == 1){
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_ALL_READY);
        }
        String userName = SecurityUtil.getUserName();
        movementOrder.setUpdateBy(userName);
        movementOrder.setUpdateTime(LocalDateTime.now());
        return movementOrderMapper.updateById(movementOrder) > 0;
    }

    /**
     * 删除移库单
     * @param id 移库单id
     * @return 操作结果
     */
    public boolean deleteMovementOrder(Long id){
        // 检查移库单状态
        MovementOrder movementOrder = movementOrderMapper.selectById(id);
        if (movementOrder.getMovementOrderStatus() == 1){
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_ALL_READY);
        }
        // 删除移库单详情
        QueryWrapper<MovementOrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("movement_order_id",id);
        movementOrderDetailMapper.delete(queryWrapper);
        // 删除移库单
        return movementOrderMapper.deleteById(id) > 0;
    }
    /**
     * 完成移库操作
     * @param id 移库单id
     */
    public boolean completeMovementOrder(Long id) {
        //检查移库单状态
        MovementOrder movementOrder = this.getMovementOrderById(id);
        if(movementOrder==null){
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_NOT_EXIST);
        }
        if(movementOrder.getMovementOrderStatus()==1){
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_ALL_READY);
        }
        //合并移库单详情
        mergeMovementOrderDetail(movementOrder);

        //验证移库单详情
        validateMovementOrderDetail(movementOrder);
        //修改库存
        updateInventory(movementOrder);
        //修改库存详情
        updateInventoryDetail(movementOrder);
        //更新移库单状态
        movementOrder.setMovementOrderStatus(1);
        //保存库存历史记录
        saveInventoryHistory(movementOrder);
        String userName = SecurityUtil.getUserName();
        movementOrder.setUpdateBy(userName);
        movementOrder.setUpdateTime(LocalDateTime.now());
        return movementOrderMapper.updateById(movementOrder) > 0;
    }
    //合并移库单详情
    private void mergeMovementOrderDetail(MovementOrder movementOrder) {
        Map<String, MovementOrderDetail> detailMap = new HashMap<>();
        List<MovementOrderDetail> detailList = movementOrder.getDetailList();
        for(MovementOrderDetail orderDetail : detailList){
            String key = orderDetail.getSourceWarehouseId() + "_" + orderDetail.getSourceAreaId()
                    + "_" + orderDetail.getTargetWarehouseId() + "_" + orderDetail.getTargetAreaId()
                    + "_" + orderDetail.getSkuId();
            if(detailMap.containsKey(key)){
                MovementOrderDetail movementOrderDetail = detailMap.get(key);
                movementOrderDetail.setQuantity(movementOrderDetail.getQuantity().add(orderDetail.getQuantity()));
            }else{
                detailMap.put(key,orderDetail);
            }
        }
        movementOrder.setDetailList(new ArrayList<>(detailMap.values()));
    }
    //验证移动单详情
    private void validateMovementOrderDetail(MovementOrder movementOrder){
        // 是否有详情
        List<MovementOrderDetail> detailList = movementOrder.getDetailList();
        if (detailList == null || detailList.isEmpty()){
            throw new Busexception(CodeEnum.WAREHOUSING_ORDER_DETAIL_ISNULL);
        }

        // 遍历详情，检查库存是否充足
        for (MovementOrderDetail orderDetail : detailList) {
            // 检查库存是否充足
            QueryWrapper<Inventory> inventoryQueryWrapper = new QueryWrapper<>();
            inventoryQueryWrapper.eq("warehouse_id",orderDetail.getSourceWarehouseId())
                    .eq("area_id",orderDetail.getSourceAreaId())
                    .eq("sku_id",orderDetail.getSkuId());
            Inventory inventory = inventoryMapper.selectOne(inventoryQueryWrapper);
            if (inventory == null || inventory.getQuantity().compareTo(orderDetail.getQuantity()) < 0){
                throw new Busexception(CodeEnum.WAREHOUSING_INSUFFICIENT);
            }

            // 检查库存详情是否充足
            InventoryDetail inventoryDetail = inventoryDetailMapper.selectById(orderDetail.getInventoryDetailId());
            if (inventoryDetail == null || inventoryDetail.getRemainQuantity().compareTo(orderDetail.getQuantity()) < 0){
                throw new Busexception(CodeEnum.WAREHOUSING_INSUFFICIENT);
            }
        }
    }
    //修改库存
    private void updateInventory(MovementOrder movementOrder) {
        List<MovementOrderDetail> detailList = movementOrder.getDetailList();
        for (MovementOrderDetail detail : detailList) {
            // 1. 减少源仓库库存
            QueryWrapper<Inventory> sourceQueryWrapper = new QueryWrapper<>();
            sourceQueryWrapper.eq("warehouse_id",detail.getSourceWarehouseId());
            sourceQueryWrapper.eq("area_id",detail.getSourceAreaId());
            sourceQueryWrapper.eq("sku_id",detail.getSkuId());
            Inventory sourceInventory = inventoryMapper.selectOne(sourceQueryWrapper);
           if(sourceInventory != null){
               sourceInventory.setQuantity(sourceInventory.getQuantity().subtract(detail.getQuantity()));
               String username = SecurityUtil.getUserName();
               sourceInventory.setUpdateBy(username);
               sourceInventory.setUpdateTime(LocalDateTime.now());
               inventoryMapper.updateById(sourceInventory);
           }else {
               throw new Busexception(CodeEnum.WAREHOUSING_INSUFFICIENT);
           }
           //2.增加目标仓库库存
            QueryWrapper<Inventory> TargetQueryWrapper = new QueryWrapper<>();
           TargetQueryWrapper.eq("warehouse_id", detail.getTargetWarehouseId());
           TargetQueryWrapper.eq("area_id", detail.getTargetAreaId());
           TargetQueryWrapper.eq("sku_id", detail.getSkuId());
           Inventory Targetinventory = inventoryMapper.selectOne(TargetQueryWrapper);
           if(Targetinventory != null){
               Targetinventory.setQuantity(Targetinventory.getQuantity().add(detail.getQuantity()));
               String username = SecurityUtil.getUserName();
               Targetinventory.setUpdateBy(username);
               Targetinventory.setUpdateTime(LocalDateTime.now());
               inventoryMapper.updateById(Targetinventory);
           }else{
               Inventory targetInventory = new Inventory();
               targetInventory.setWarehouseId(detail.getTargetWarehouseId());
               targetInventory.setAreaId(detail.getTargetAreaId());
               targetInventory.setSkuId(detail.getSkuId());
               targetInventory.setQuantity(detail.getQuantity());
               String userName = SecurityUtil.getUserName();
               targetInventory.setCreateBy(userName);
               targetInventory.setUpdateBy(userName);
               targetInventory.setCreateTime(LocalDateTime.now());
               targetInventory.setUpdateTime(LocalDateTime.now());
               inventoryMapper.insert(targetInventory);
           }
        }
    }
    //修改库存详情
    private void updateInventoryDetail(MovementOrder movementOrder) {
        List<MovementOrderDetail> detailList = movementOrder.getDetailList();
        for (MovementOrderDetail detail : detailList) {
            // 1.减少源仓库库存详情数量
            InventoryDetail inventoryDetail = inventoryDetailMapper.selectById(detail.getInventoryDetailId());
            inventoryDetail.setRemainQuantity(inventoryDetail.getRemainQuantity().subtract(detail.getQuantity()));
            String username = SecurityUtil.getUserName();
            inventoryDetail.setUpdateBy(username);
            inventoryDetail.setUpdateTime(LocalDateTime.now());
            inventoryDetailMapper.updateById(inventoryDetail);
            // 2.增加目标仓库库存详情数量
            // 如果目标仓库存在相同入库单号的库存详情，则累加数量，否则新增一条库存详情
            QueryWrapper<InventoryDetail> targetQueryWrapper = new QueryWrapper<>();
            targetQueryWrapper.eq("warehouse_id",detail.getTargetWarehouseId())
                    .eq("area_id",detail.getTargetAreaId())
                    .eq("sku_id",detail.getSkuId())
                    .eq("order_no",inventoryDetail.getOrderNo());
            InventoryDetail inventoryDetailTarget = inventoryDetailMapper.selectOne(targetQueryWrapper);
            if (inventoryDetailTarget == null){
                inventoryDetailTarget = new InventoryDetail();
                inventoryDetailTarget.setWarehouseId(detail.getTargetWarehouseId());
                inventoryDetailTarget.setAreaId(detail.getTargetAreaId());
                inventoryDetailTarget.setSkuId(detail.getSkuId());
                inventoryDetailTarget.setQuantity(detail.getQuantity());
                inventoryDetailTarget.setRemainQuantity(detail.getQuantity());
                inventoryDetailTarget.setExpirationDate(inventoryDetail.getExpirationDate());
                inventoryDetailTarget.setProductionDate(inventoryDetail.getProductionDate());

                // 新增的库存详情，入库单ID，入库单单号，金额和之前的库存详情一致
                inventoryDetailTarget.setReceiptOrderId(inventoryDetail.getReceiptOrderId());
                inventoryDetailTarget.setOrderNo(inventoryDetail.getOrderNo());
                inventoryDetailTarget.setAmount(inventoryDetail.getAmount());

                inventoryDetailTarget.setCreateBy(username);
                inventoryDetailTarget.setUpdateBy(username);
                inventoryDetailTarget.setCreateTime(LocalDateTime.now());
                inventoryDetailTarget.setUpdateTime(LocalDateTime.now());
                inventoryDetailMapper.insert(inventoryDetailTarget);
            }else {
                // 累加数量
                inventoryDetailTarget.setQuantity(inventoryDetailTarget.getQuantity().add(detail.getQuantity()));
                inventoryDetailTarget.setRemainQuantity(inventoryDetailTarget.getRemainQuantity().add(detail.getQuantity()));

                inventoryDetailTarget.setUpdateBy(username);
                inventoryDetailTarget.setUpdateTime(LocalDateTime.now());
                inventoryDetailMapper.updateById(inventoryDetailTarget);
            }
        }
        }
        //保存库存历史记录
    private  void saveInventoryHistory(MovementOrder movementOrder) {
        List<MovementOrderDetail> detailList = movementOrder.getDetailList();
        for (MovementOrderDetail detail : detailList) {
            // 移库时的原始库存详情
            InventoryDetail inventoryDetail = inventoryDetailMapper.selectById(detail.getInventoryDetailId());

            // 移库时的出库流水
            InventoryHistory inventoryHistoryOut = new InventoryHistory();
            inventoryHistoryOut.setWarehouseId(detail.getSourceWarehouseId());
            inventoryHistoryOut.setAreaId(detail.getSourceAreaId());
            inventoryHistoryOut.setSkuId(detail.getSkuId());
            inventoryHistoryOut.setQuantity(detail.getQuantity().negate()); // 负数
            inventoryHistoryOut.setProductionDate(detail.getProductionDate());
            inventoryHistoryOut.setExpirationDate(detail.getExpirationDate());
            inventoryHistoryOut.setAmount(inventoryDetail.getAmount());
            inventoryHistoryOut.setOrderId(movementOrder.getId());
            inventoryHistoryOut.setOrderNo(movementOrder.getMovementOrderNo());
            inventoryHistoryOut.setOrderType(3); // 移库
            inventoryHistoryOut.setRemark(movementOrder.getRemark());

            String userName = SecurityUtil.getUserName();
            inventoryHistoryOut.setCreateBy(userName);
            inventoryHistoryOut.setUpdateBy(userName);
            inventoryHistoryOut.setCreateTime(LocalDateTime.now());
            inventoryHistoryOut.setUpdateTime(LocalDateTime.now());

            inventoryHistoryMapper.insert(inventoryHistoryOut);

            // 移库时的入库流水
            InventoryHistory inventoryHistoryIn = new InventoryHistory();
            inventoryHistoryIn.setWarehouseId(detail.getTargetWarehouseId());
            inventoryHistoryIn.setAreaId(detail.getTargetAreaId());
            inventoryHistoryIn.setSkuId(detail.getSkuId());
            inventoryHistoryIn.setQuantity(detail.getQuantity()); // 正数
            inventoryHistoryIn.setProductionDate(detail.getProductionDate());
            inventoryHistoryIn.setExpirationDate(detail.getExpirationDate());
            inventoryHistoryIn.setAmount(inventoryDetail.getAmount());
            inventoryHistoryIn.setOrderId(movementOrder.getId());
            inventoryHistoryIn.setOrderNo(movementOrder.getMovementOrderNo());
            inventoryHistoryIn.setOrderType(3); // 移库
            inventoryHistoryIn.setRemark(movementOrder.getRemark());

            inventoryHistoryIn.setCreateBy(userName);
            inventoryHistoryIn.setUpdateBy(userName);
            inventoryHistoryIn.setCreateTime(LocalDateTime.now());
            inventoryHistoryIn.setUpdateTime(LocalDateTime.now());

            inventoryHistoryMapper.insert(inventoryHistoryIn);
        }
        }


    }




