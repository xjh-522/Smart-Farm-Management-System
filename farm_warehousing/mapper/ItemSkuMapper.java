package com.itbaizhan.farm_warehousing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_warehousing.entity.ItemSku;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemSkuMapper extends BaseMapper<ItemSku> {
}
