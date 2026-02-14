package com.itbaizhan.farm_common.exception;

import com.itbaizhan.farm_common.result.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Busexception extends RuntimeException {
    private CodeEnum codeEnum;
}
