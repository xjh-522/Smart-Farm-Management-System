package com.itbaizhan.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Permissionmapper extends BaseMapper<Permission> {
    void deleteRolePermissionsByPermissionIds(List<Long> Ids);

}
