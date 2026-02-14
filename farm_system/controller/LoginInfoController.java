package com.itbaizhan.farm_system.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_system.entity.LoginInfo;
import com.itbaizhan.farm_system.service.LoginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/loginInfo")
public class LoginInfoController {
    @Autowired
    private LoginInfoService loginInfoService;
    @GetMapping("/list")
    public BaseResult<Page<LoginInfo>> selectLoginInfoList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam( value = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(value = "userName", required = false)String userName,
                                               @RequestParam(value = "ipaddr", required = false) String ipaddr,
                                               @RequestParam(value = "status", defaultValue = "0")String status){
        Page<LoginInfo> page = loginInfoService.selectLoginInfoList(pageNum, pageSize, userName, ipaddr, status);
        return BaseResult.ok(page);
}
    @GetMapping("/getLoginInfoById")
    public BaseResult<LoginInfo> findById(@RequestParam("id") Long id){
        LoginInfo loginInfo = loginInfoService.selectLoginInfoById(id);
        return BaseResult.ok(loginInfo);
    }
}
