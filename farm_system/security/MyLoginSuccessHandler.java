package com.itbaizhan.farm_system.security;

import com.alibaba.fastjson2.JSON;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_system.service.LoginInfoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * 登录成功处理类
 */
@Component
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LoginInfoService loginInfoService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String name = authentication.getName();
        loginInfoService.recordLoginInfo(name,"0","登录成功",request);
        // 获取用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 生成JWT令牌
        String token = jwtUtil.generateToken(userDetails);
        BaseResult result = new BaseResult(200,"登录成功",token);

        response.setContentType("text/json;charset=utf-8");;
        response.getWriter().write(JSON.toJSONString( result));
    }
}
