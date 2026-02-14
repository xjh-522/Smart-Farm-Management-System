package com.itbaizhan.farm_warehousing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_warehousing.entity.InventoryHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存记录数据访问层
 */
@Mapper
public interface InventoryHistoryMapper extends BaseMapper<InventoryHistory> {
}
