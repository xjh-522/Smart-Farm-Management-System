package com.itbaizhan.farm_warehousing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_warehousing.entity.ReceiptOrder;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库单数据访问层
 */
@Mapper
public interface ReceiptOrderMapper  extends BaseMapper<ReceiptOrder> {
}
