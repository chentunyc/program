package com.training.module.auth.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码key
     */
    private String codeKey;
}
