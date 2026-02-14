package com.itbaizhan.farm_system.security;

import com.alibaba.fastjson2.JSON;
import com.itbaizhan.farm_common.exception.Busexception;
import com.itbaizhan.farm_common.exception.InvalidCaptchaException;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_common.result.CodeEnum;
import com.itbaizhan.farm_system.service.LoginInfoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * 登录失败处理类
 */
@Component
public class MyLoginFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private LoginInfoService loginInfoService;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        BaseResult result=null;
       // 判断异常类型
        if(exception instanceof InvalidCaptchaException){//验证码错误
          Busexception busexception =(Busexception) request.getAttribute("captchaError");
          CodeEnum codeEnum = busexception.getCodeEnum();
            result = new BaseResult(codeEnum.getCode(), codeEnum.getMessage(), null);
        }else{//账号密码错误
            result= new BaseResult(402, "用户名错误或密码错误", null);
        }
        loginInfoService.recordLoginInfo("Unknown", "1", result.getMessage(), request);
        response.setContentType("text/json;charset=utf-8");;
        response.getWriter().write(JSON.toJSONString( result));
    }
}
