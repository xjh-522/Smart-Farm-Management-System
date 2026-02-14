package com.itbaizhan.farm_warehousing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_warehousing.entity.InventoryDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存详情数据访问层
 */
@Mapper
public interface InventoryDetailMapper extends BaseMapper<InventoryDetail> {
}
