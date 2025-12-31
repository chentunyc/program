package com.training.common.exception;

import com.training.common.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各类异常,返回标准化响应
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常: URI={}, Message={}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = extractValidationErrorMessage(e.getBindingResult());
        log.error("参数校验异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = extractValidationErrorMessage(e.getBindingResult());
        log.error("参数绑定异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 访问拒绝异常(权限不足)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("访问拒绝: URI={}, Message={}", request.getRequestURI(), e.getMessage());
        return Result.error(403, "权限不足,无法访问该资源");
    }

    /**
     * 认证失败异常(用户名或密码错误)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result<?> handleBadCredentialsException(BadCredentialsException e) {
        log.error("认证失败: {}", e.getMessage());
        return Result.error(401, "用户名或密码错误");
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: URI={}, Message={}", request.getRequestURI(), e.getMessage(), e);
        return Result.error("系统繁忙,请稍后再试");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: URI={}, Message={}", request.getRequestURI(), e.getMessage(), e);
        return Result.error("系统异常,请联系管理员");
    }

    /**
     * 提取参数校验错误消息
     */
    private String extractValidationErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
    }
}
