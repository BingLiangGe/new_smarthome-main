package com.lj.iot.common.base.config;

import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.base.vo.CommonResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DuplicateKeyException;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionConfig {

    @ExceptionHandler(DuplicateKeyException.class)
    public Object duplicateKeyException(HttpServletRequest request, DuplicateKeyException e) {
        log.error("GlobalExceptionHandler.duplicateKeyException.error={}", e);
        return handle(request,
                CommonResultVo.builder().code(CodeConstant.FAILURE).msg("数据已存在，请查证").build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object httpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error("GlobalExceptionHandler.httpMessageNotReadableException.error={}", e);
        return handle(request,
                CommonResultVo.builder().code(CodeConstant.FAILURE).msg("参数不能为空").build());
    }

    @ExceptionHandler(BindException.class)
    public Object bindException(HttpServletRequest request, BindException e) {
        log.error("GlobalExceptionHandler.bindException.error={}", e);
        return handle(request,
                CommonResultVo.builder().code(CodeConstant.FAILURE).msg(e.getBindingResult().getFieldError().getDefaultMessage()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error("GlobalExceptionHandler.methodArgumentNotValidException.error={}", e);
        return handle(request,
                CommonResultVo.builder().code(CodeConstant.FAILURE).msg(e.getBindingResult().getFieldError().getDefaultMessage()).build());
    }


    @ExceptionHandler(CommonException.class)
    public Object commonException(HttpServletRequest request, CommonException e) {
        log.error("GlobalExceptionHandler.commonException.error={}", e.getMsg(), e);
        return handle(request,
                CommonResultVo.builder().code(e.getCode()).msg(e.getMsg()).build());
    }


    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(HttpServletRequest request, Exception e) {
        log.error("GlobalExceptionHandler.exceptionHandler", e);
        return handle(request,
                CommonResultVo.builder().code(CodeConstant.FAILURE).msg("系统繁忙 !").build());
    }

    private Object handle(HttpServletRequest request, CommonResultVo<?> commonResultVo) {
        return commonResultVo;
    }
}
