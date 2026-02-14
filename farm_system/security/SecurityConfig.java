package com.itbaizhan.farm_system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    @Autowired
    private CaptchaFilter captchaFilter;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private MyLoginSuccessHandler myLoginSuccessHandler;
    @Autowired
    private MyLoginFailureHandler myLoginFailureHandler;
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;
    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;
    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //登录
        http.formLogin(
                form -> {
                    form.usernameParameter("username")
                            .passwordParameter("password")
                            .loginProcessingUrl("/user/login")
                            .successHandler(myLoginSuccessHandler)
                            .failureHandler(myLoginFailureHandler);
                }
        );
        //权限控制
        http.authorizeHttpRequests(
                resp->{
                    resp.requestMatchers("/user/login","/captcha/generate").permitAll()
                            .anyRequest().authenticated();
                }

        );
        //登出
        http.logout(
                logout -> {
                    logout.logoutUrl("/user/logout")
                            .logoutSuccessHandler(myLogoutSuccessHandler);
//                            .invalidateHttpSession(true)
//                            .clearAuthentication( true);
                }
        );
        // 设置session管理策略为无状态
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        //异常控制
        http.exceptionHandling(
                exception -> {
                    exception.authenticationEntryPoint(myAuthenticationEntryPoint)//未登录处理
                            .accessDeniedHandler(myAccessDeniedHandler);//权限不足处理
                }
        );
        //禁用csrf
        http.csrf(csrf -> csrf.disable());
        //添加验证码过滤器
        http.addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        //跨域请求配置
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 允许所有来源
        corsConfiguration.addAllowedHeader("*"); // 允许所有请求头
        corsConfiguration.addAllowedMethod("*"); // 允许所有请求方法
        corsConfiguration.setAllowCredentials(false); // 不允许cookie跨域

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 应用于所有路径
        return source;

    }

}
