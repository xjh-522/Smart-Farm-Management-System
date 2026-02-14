package com.itbaizhan.farm_system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itbaizhan.farm_common.Utils.SecurityUtil;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_system.entity.User;
import com.itbaizhan.farm_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 根据id查询用户
     */
    @GetMapping("/getUserById")
    public BaseResult<User> findById(Long id){
        User user = userService.findById(id);
        return BaseResult.ok( user);
    }
    /**
     * 分页查询用户
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('/user/list')")
    public BaseResult<IPage<User>> getUserList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "status", required = false) String status) {
        IPage<User> result = userService.findUserPage(pageNum, pageSize, userName, status);
        return BaseResult.ok(result);
    }
    /**
     * 添加用户
     */
    @PostMapping("/addUser")
    public BaseResult addUser(@RequestBody User user){
        userService.addUser(user);
        return BaseResult.ok();
    }
    /**
     * 修改用户信息
     */
    @PutMapping("/updateUser")
    public BaseResult updateUser(@RequestBody User user){
        userService.updateUser(user);
        return BaseResult.ok();
    }
    /**
     * 修改密码
     */
    @PutMapping("/resetPassword")
    public BaseResult changePassword( Long id,String password){
         userService.ChangePassword(id, password);
         return BaseResult.ok();
    }
    /**
     * 修改用户状态
     */
    @PutMapping("/changestatus")
    public BaseResult changeStatus( Long id,String status){
        userService.ChangeStatus(id, status);
        return BaseResult.ok();
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/deleteUser")
    public BaseResult deleteUser(@RequestParam("ids")String ids){
        List<Long> collect = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        userService.deleteUser(collect);
        return BaseResult.ok();
    }

    /**
     * 修改用户权限
     */
    @PutMapping("/assignPermission")
    public BaseResult assignPermission(@RequestParam("userId")Long userId,@RequestParam(value = "roleIds",required = false)List< Long> roleIds){
        userService.AssignPermission(userId, roleIds);
        return BaseResult.ok();
    }

    @PutMapping("/assignRoles")
    public BaseResult assignRoles(@RequestParam("userId")Long userId,@RequestParam(value = "roleIds",required = false)List< Long> roleIds){
        userService.AssignRoles(userId, roleIds);
        return BaseResult.ok();
    }

    @GetMapping("/getUsername")
    public BaseResult<String> getUsername() {
        String username = SecurityUtil.getUserName();
        return BaseResult.ok(username);
    }

}
