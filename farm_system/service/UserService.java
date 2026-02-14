package com.itbaizhan.farm_system.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.farm_common.exception.Busexception;
import com.itbaizhan.farm_common.result.CodeEnum;
import com.itbaizhan.farm_system.entity.Permission;
import com.itbaizhan.farm_system.entity.Role;
import com.itbaizhan.farm_system.entity.User;
import com.itbaizhan.farm_system.mapper.Permissionmapper;
import com.itbaizhan.farm_system.mapper.Rolemapper;
import com.itbaizhan.farm_system.mapper.Usermapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private Usermapper usermapper;
    @Autowired
    private Rolemapper rolemapper;
    @Autowired
    private Permissionmapper permissionmapper;
    @Autowired
    private PasswordEncoder encoder;

    //根据id查询用户
    public User findById(Long id) {

        User user = usermapper.selectById(id);
        if (user != null) {
            // 查询用户的角色ID列表
            List<Long> roleIds = usermapper.selectUserRole(id);
            if (roleIds != null && !roleIds.isEmpty()) {
                // 查询角色详细信息
                List<Role> roles = rolemapper.selectBatchIds(roleIds);
                // 为每个角色查询权限信息
                for (Role role : roles) {
                    List<Long> permissionIds = rolemapper.findRolepermission(role.getRoleId());
                    if (permissionIds != null && !permissionIds.isEmpty()) {
                        List<Permission> permissions = permissionmapper.selectBatchIds(permissionIds);
                        role.setPermissions(permissions);
                    }
                }
                user.setRoles(roles);
            }
        }
        return user;
/**
 * 分页查询用户
 * @param page 当前页
 * @param size 每页大小
 * @param userName 用户名
 * @param status 状态
 * @return 分页结果
 */
    }

    public IPage<User> findUserPage(int page, int size, String userName, String status) {
        Page<User> pageObj = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(userName)) {
            queryWrapper.like("user_name", userName)
                    .or()
                    .like("nick_name", userName);
        }

        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return usermapper.selectPage(pageObj, queryWrapper);
    }

    // 添加用户
    public Boolean addUser(User user) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_name", user.getUserName());
        User user1 = usermapper.selectOne(userQueryWrapper);
        if (user1 != null) {
            throw new Busexception(CodeEnum.Userexist_error);
        }
        user.setCreateTime(LocalDateTime.now());
        user.setStatus("0");
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456"); // 默认密码
        }
        String password = user.getPassword();
        password = encoder.encode(password);
        user.setPassword(password);
        return usermapper.insert(user) > 0;
    }

    //修改用户信息
    public Boolean updateUser(User user) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_name", user.getUserName()).ne("user_id", user.getUserId());
        User user1 = usermapper.selectOne(userQueryWrapper);
        if (user1 != null) {
            throw new Busexception(CodeEnum.Userexist_error);
        }
        user.setUpdateTime(LocalDateTime.now());
        if (!StringUtils.hasText(user.getPassword())) {
            String password = user.getPassword();
            password = encoder.encode(password);
            user.setPassword(password);
        }else{
            user.setPassword(null);
        }
        return usermapper.updateById(user) > 0;
    }

    //修改密码
    public Boolean ChangePassword(Long id, String password) {
        User user = new User();
        user.setUserId(id);
        password = encoder.encode(password);
        user.setPassword(password);
        user.setUpdateTime(LocalDateTime.now());
        user.setPwdUpdateDate(LocalDateTime.now());
        return usermapper.updateById(user) > 0;
    }

    //修改用户状态
    public Boolean ChangeStatus(Long id, String status) {
        User user = new User();
        user.setUserId(id);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        return usermapper.updateById(user) > 0;
    }

    //批量删除用户
    public Boolean deleteUser(List<Long> ids) {
        usermapper.deleteUserRole(ids);
        return usermapper.deleteBatchIds(ids) > 0;
    }

    //修改用户权限
    public Boolean AssignPermission(Long userId, List<Long> RolEId) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        usermapper.deleteUserRole(userIds);
        if (RolEId != null && !RolEId.isEmpty()) {
            usermapper.addUserRole(userId, RolEId);
        }
        return true;
    }

    //分配用户角色
    public boolean AssignRoles(Long userId, List<Long> roleIds) {
        // 先删除用户的所有角色
        List<Long> userIds = new ArrayList();
        userIds.add(userId);
        usermapper.deleteUserRole(userIds);
        // 如果roleIds不为空，则插入新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            usermapper.addUserRole(userId, roleIds);
        }
        return true;
    }
    //根据用户名查询用户
    public User findByUserName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return usermapper.selectOne(queryWrapper);
    }

    //根据用户名查询用户所有权限
    public List<Permission> findPermissionsByUserName(String userName) {
        return usermapper.selectPermissionByusername(userName);
    }

    public Boolean updatecurrentUserInfo(User user) {
        User currentUser = findByUserName(user.getUserName());
        if(currentUser == null){
            throw new Busexception(CodeEnum.UserNotExist_error);
        }
        if (user.getNickName() != null) {
            currentUser.setNickName(user.getNickName());
        }
        if (user.getPhonenumber() != null) {
            currentUser.setPhonenumber(user.getPhonenumber());
        }
        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getSex() != null) {
            currentUser.setSex(user.getSex());
        }
        currentUser.setUpdateBy(user.getUserName());
        currentUser.setUpdateTime(LocalDateTime.now());
        return usermapper.updateById(currentUser) > 0;
    }

    public Boolean updateUserPassword(String currentPassword, String newPassword,String confirmPassword,String currentUsername) {
        //参数校验
        if (currentPassword == null || currentPassword.trim().isEmpty()
                || newPassword == null || newPassword.trim().isEmpty()){
            throw new Busexception(CodeEnum.Password_null_error);
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new Busexception(CodeEnum.PERSONAL_PASSWORD_NOT_MATCH);
        }
        //根据用户名查询用户
        User user = findByUserName(currentUsername);
        if (user == null) {
            throw new Busexception(CodeEnum.UserNotExist_error);
        }
        //验证当前密码是否正确
        if (!encoder.matches(currentPassword, user.getPassword())) {
            throw new Busexception(CodeEnum.Password_error);
        }
        //更新密码
        String encodePassword = encoder.encode(newPassword);
        user.setPassword(encodePassword);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(currentUsername);
        user.setPwdUpdateDate(LocalDateTime.now());
        return usermapper.updateById(user) > 0;

    }

}



