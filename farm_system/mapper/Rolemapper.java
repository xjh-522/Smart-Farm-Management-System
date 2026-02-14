package com.itbaizhan.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.farm_system.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Rolemapper extends BaseMapper<Role> {
    /**
     * 根据角色id查询权限id
     * @param roleId
     * @return
     */
    List<Long> findRolepermission (@Param("roleId") long roleId);

    /**
     * 根据角色id删除角色权限关系
     * @param roleIds
     */
    void deleteRolePermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据角色id删除角色权限关系
     * @param roleIds
     */
    void deleteUserRolesByRoleIds(@Param("roleIds") List<Long> roleIds);


    /**
     * 根据角色id查询用户角色id
     * @param roleId
     */
    List<Long> selectRoleUserIds(@Param("roleId") long roleId);

    /**
     * 根据角色id查询角色权限id
     * @param roleId
     */
     List< Long> selectRolePermissionIds(@Param("roleId") long roleId);

     /**
     * 添加角色权限
     * @param roleId
      * @param permissionIds
      * @return
      /**/
     void insertRolePermissions(@Param("roleId") long roleId, @Param("permissionIds") List<Long> permissionIds);
     /**
     * 批量删除用户角色
     * @param roleId
     * @param userIds
     */
   void deleteUserRolesByRoleIdAndUserIds(@Param("roleId") long roleId, @Param("userIds") List<Long> userIds);
/**
 * 查询用户角色是否存在
 * @param roleId
 * @param userId
 * /**/
   int countUserRoleExists(@Param("roleId")long roleId, @Param("userId")long userId);
/**
 * 添加用户角色
 * @param userId
 * @param roleId
 */
   void  insertUserRole(@Param("userId") long userId, @Param("roleId") long roleId);
}
