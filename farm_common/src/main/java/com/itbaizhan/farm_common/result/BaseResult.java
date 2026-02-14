package com.itbaizhan.farm_common.result;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseResult <T>{
    private Integer code;
    private String message;
    private T data;
    public static <T> BaseResult<T> ok() {
        return new BaseResult(CodeEnum.Success.getCode(), CodeEnum.Success.getMessage(), null);
    }
    public static <T> BaseResult<T> ok(T data){
        return new BaseResult<>(CodeEnum.Success.getCode(),CodeEnum.Success.getMessage(),data);
    }

    public static <T> BaseResult<T> error(CodeEnum codeEnum){
        return new BaseResult<>(codeEnum.getCode(),codeEnum.getMessage(),null);
    }
    public static <T> BaseResult<T> error(CodeEnum codeEnum,T data){
        return new BaseResult<>(codeEnum.getCode(),codeEnum.getMessage(),data);
    }

}
