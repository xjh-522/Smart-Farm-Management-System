package com.itbaizhan.farm_system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.farm_system.entity.LoginInfo;
import com.itbaizhan.farm_system.mapper.LoginInfoMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LoginInfoService {
    @Autowired
    private LoginInfoMapper loginInfoMapper;
    public void recordLoginInfo(String userName, String status, String msg, HttpServletRequest request){
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserName(userName);
        loginInfo.setIpaddr(getClientIp( request));
        loginInfo.setBrowser(getBrowser(request));
        loginInfo.setOs(getOs(request));
        loginInfo.setStatus(status);
        loginInfo.setMsg(msg);
        loginInfo.setLoginTime(LocalDateTime.now());
        loginInfoMapper.insert(loginInfo);
}
    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求对象
     * @return 客户端IP地址，如果无法获取则返回请求的远程地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 解析并获取客户端浏览器类型
     * @param request HTTP请求对象
     * @return 浏览器类型名称，包括：
     *     Chrome - Google Chrome浏览器
     *     Firefox - Mozilla Firefox浏览器
     *     Safari - Apple Safari浏览器
     *     Edge - Microsoft Edge浏览器
     *     Other - 其他浏览器
     *     Unknown - 无法识别的浏览器
     */
    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        return "Other";
    }

    /**
     * 解析并获取客户端操作系统类型
     * @param request HTTP请求对象
     * @return 操作系统类型名称，包括：
     *     Windows - Microsoft Windows系统
     *     macOS - Apple macOS系统
     *     Linux - Linux系统
     *     Android - Google Android系统
     *     iOS - Apple iOS系统
     *     Other - 其他操作系统
     *     Unknown - 无法识别的操作系统
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iOS";
        return "Other";
    }
    public Page<LoginInfo> selectLoginInfoList(Integer pageNum, Integer pageSize, String userName, String ipaddr,
                                                String  status) {
        Page<LoginInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<LoginInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(userName)) {
            queryWrapper.like("user_name", userName);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (StringUtils.hasText(ipaddr)) {
            queryWrapper.like("ipaddr", ipaddr);
        }
       queryWrapper.orderByDesc("login_time");
        return loginInfoMapper.selectPage(page, queryWrapper);
    }
    public LoginInfo selectLoginInfoById(Long infoId) {
        return loginInfoMapper.selectById(infoId);
    }

}
