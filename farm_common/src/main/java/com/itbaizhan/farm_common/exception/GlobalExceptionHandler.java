package com.itbaizhan.farm_common.exception;

import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_common.result.CodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public BaseResult<String> handleException(HttpServletRequest request, HttpServletResponse response, Exception e){
        e.printStackTrace();
        return BaseResult.error(CodeEnum.System_error,e.getMessage());
    }
    @ExceptionHandler(Busexception.class)
    public BaseResult<String> handleBusexception(Busexception e){
        return BaseResult.error(e.getCodeEnum());
    }
    @ExceptionHandler(AccessDeniedException.class)
    public void defaultExceptionHandler( AccessDeniedException e) throws AccessDeniedException {
       throw e;
    }

}
