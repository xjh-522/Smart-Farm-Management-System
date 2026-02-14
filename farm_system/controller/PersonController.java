package com.itbaizhan.farm_system.controller;

import com.itbaizhan.farm_common.Utils.SecurityUtil;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_system.entity.User;
import com.itbaizhan.farm_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personal")
public class PersonController {
    @Autowired
    private UserService userService;
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public BaseResult<User> getPersoninfo(){
        String currentUserName = SecurityUtil.getUserName();
        User user = userService.findByUserName(currentUserName);
        return BaseResult.ok(user);
    }
    /**
     * 修改当前用户信息
     */
    @PutMapping("/update")
    public BaseResult updateUserInfo(@RequestBody User user) {
        // 获取当前登录用户名
        user.setUserName(SecurityUtil.getUserName());
        // 调用service层处理业务逻辑
        userService.updatecurrentUserInfo(user);
        return BaseResult.ok();}

    @PutMapping("/updatePassword")
    public BaseResult updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
             ) {
        String currentUsername= SecurityUtil.getUserName();
        // 调用service层处理业务逻辑
        userService.updateUserPassword(currentPassword, newPassword, confirmPassword, currentUsername);
        return BaseResult.ok();
    }

}
