package com.itbaizhan.farm_warehousing.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.farm_warehousing.entity.InventoryDetail;
import com.itbaizhan.farm_warehousing.mapper.InventoryDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 库存详情Service
 */
@Service
@Transactional
public class InventoryDetailService {
    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;

    /**
     * 分页查询库存详情
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @param warehouseId 仓库ID
     * @param areaId 库区ID
     * @param orderNo 入库单号，模糊查询
     * @return 查询结果
     */
    public IPage<InventoryDetail> selectPage(Integer pageNo, Integer pageSize,
                                             Long warehouseId, Long areaId, String orderNo) {
        Page<InventoryDetail> page = new Page<>(pageNo,pageSize);
        QueryWrapper<InventoryDetail> queryWrapper = new QueryWrapper<>();
        if (warehouseId != null){
            queryWrapper.eq("warehouse_id",warehouseId);
        }
        if (areaId != null){
            queryWrapper.eq("area_id",areaId);
        }
        if (StringUtils.hasText(orderNo)){
            queryWrapper.like("order_no",orderNo);
        }
        queryWrapper.orderByDesc("create_time");
        return inventoryDetailMapper.selectPage(page,queryWrapper);
    }

    /**
     * 根据ID查询库存详情
     * @param id 库存详情ID
     * @return 库存详情
     */
    public InventoryDetail getInventoryDetailById(Long id) {
        return inventoryDetailMapper.selectById(id);
    }
}