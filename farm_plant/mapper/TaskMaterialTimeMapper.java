package com.itbaizhan.farm_plant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_plant.entity.TaskMachineTime;
import com.itbaizhan.farm_plant.entity.TaskMaterialTime;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务农资用量Mapper
 */
@Mapper
public interface TaskMaterialTimeMapper extends BaseMapper<TaskMaterialTime> {
}
