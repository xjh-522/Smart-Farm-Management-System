package com.itbaizhan.farm_common.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static String getUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null&&authentication.getName()!=null){
            return authentication.getName();
        }else {
            return "Unknow";
        }

    }
}
