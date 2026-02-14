package com.itbaizhan.farm_system.security;

import com.itbaizhan.farm_common.exception.Busexception;
import com.itbaizhan.farm_common.exception.InvalidCaptchaException;
import com.itbaizhan.farm_common.result.CodeEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class CaptchaFilter extends OncePerRequestFilter {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MyLoginFailureHandler loginFailureHandler;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("/user/login".equals(request.getRequestURI())) {
            try {
                String captcha = request.getParameter("captchaCode");
                String SessionId = request.getSession().getId();
                if (StringUtils.isEmpty(captcha)) {
                    throw new Busexception(CodeEnum.CAPTCHA_ISNULL);
                }
                String key = "captcha:" + SessionId;
                String correctCode = redisTemplate.opsForValue().get(key);
                if (!captcha.equalsIgnoreCase(correctCode)) {
                    throw new Busexception(CodeEnum.CAPTCHA_ERROR);
                }

            } catch (Busexception e) {
                request.setAttribute("captchaError", e);
                loginFailureHandler.onAuthenticationFailure(request, response, new InvalidCaptchaException(e.getMessage()));
                return;
            }



        }
        filterChain.doFilter(request, response);
    }
}

