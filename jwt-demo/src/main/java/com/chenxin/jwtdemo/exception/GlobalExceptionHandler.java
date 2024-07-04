package com.chenxin.jwtdemo.exception;

import com.chenxin.jwtdemo.common.BaseResponse;
import com.chenxin.jwtdemo.common.ErrorCode;
import com.chenxin.jwtdemo.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author fangchenxin
 * @description 全局异常处理器
 * @date 2024/4/20 15:22
 * @modify
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinesssException.class)
    public BaseResponse businessExceptionHandler(BusinesssException e) {
        log.error("businessException", e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("runTimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }

}
