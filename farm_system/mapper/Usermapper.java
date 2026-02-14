package com.itbaizhan.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_system.entity.Permission;
import com.itbaizhan.farm_system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Usermapper extends BaseMapper<User> {
    void deleteUserRole(@Param("userIds") List<Long> userId);

    List<Long> selectUserRole(@Param("userId") long userId);
    void addUserRole(@Param("userId") long userId, @Param("roleIds") List<Long> roleIds);
    List<Permission> selectPermissionByusername(@Param("username") String username);
}
