package com.itbaizhan.farm_plant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_plant.entity.TaskEmployeeTime;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人工工时表Mapper接口
 */
@Mapper
public interface TaskEmployTimeMapper extends BaseMapper<TaskEmployeeTime> {
}
