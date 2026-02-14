package com.itbaizhan.farm_plant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_plant.entity.TaskBatch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 种植批次Mapper接口
 */
@Mapper
public interface TaskBatchMapper extends BaseMapper<TaskBatch> {
}
