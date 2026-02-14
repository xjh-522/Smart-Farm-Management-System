package com.itbaizhan.farm_system.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.farm_common.exception.Busexception;
import com.itbaizhan.farm_common.result.CodeEnum;
import com.itbaizhan.farm_system.entity.Permission;
import com.itbaizhan.farm_system.mapper.Permissionmapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PermissionService {
    @Autowired
    private Permissionmapper permissionMapper;
    /**
     * 获取权限列表
     * @param page
     * @param pageSize
     * @param permissionName
     * @return
     */
    public IPage<Permission> getPermissionList(int page, int pageSize, String permissionName) {
        Page<Permission> pageObj = new Page<>(page, pageSize);
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(permissionName)) {
            queryWrapper.like("permission_name", permissionName);
        }
        queryWrapper.orderByAsc("permission_id");
        return permissionMapper.selectPage(pageObj, queryWrapper);
    }
    /**
     * 根据id查询权限信息
     *
     * @param permissionId
     * @return
     */
    public Permission SelectById(Long permissionId) {
        return permissionMapper.selectById(permissionId);
    }
    /**
     * 修改权限信息
     *
     * @param permission
     * @return
     */
    public Boolean UpdatePermission(Permission permission) {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_name", permission.getPermissionName())
                .ne("permission_id", permission.getPermissionId());
        Permission existPermission = permissionMapper.selectOne(queryWrapper);
        if (existPermission != null) {
            throw new Busexception(CodeEnum.SYS_PERMISSION_EXIST);
        }
        permission.setUpdateTime(LocalDateTime.now());
        return permissionMapper.updateById(permission) > 0;
    }

    /**
     * 添加权限
     */
    public Boolean AddPermission(Permission permission) {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_name", permission.getPermissionName());
        Permission existPermission = permissionMapper.selectOne(queryWrapper);
        if (existPermission != null) {
            throw new Busexception(CodeEnum.SYS_PERMISSION_EXIST);
        }
        permission.setUpdateTime(LocalDateTime.now());
        return permissionMapper.insert(permission) > 0;
    }

    /**
     * 删除权限
     */
    public Boolean DeletePermission(List<Long> Ids) {
        permissionMapper.deleteRolePermissionsByPermissionIds(Ids);
        // 删除权限
        return permissionMapper.deleteBatchIds(Ids) > 0;
    }
    /**
     * 查询所有权限
     */
    public List<Permission> getAllPermissions() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("permission_id");
        return permissionMapper.selectList(queryWrapper);
    }

    /**
     * 获取权限选择列表
     */
    public List<Permission> getPermissionSelectList() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("permission_id");
        queryWrapper.select("permission_id", "permission_name");
        return permissionMapper.selectList(queryWrapper);
    }
}
