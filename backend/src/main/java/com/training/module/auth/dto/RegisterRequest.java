package com.training.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度为3-50个字符")
    @Schema(description = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    @Schema(description = "密码", required = true)
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    @Schema(description = "真实姓名/昵称", required = true)
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号(与邮箱至少填一个)")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Schema(description = "邮箱(与手机号至少填一个)")
    private String email;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(STUDENT|TEACHER|GUEST|DATA_ADMIN)$", message = "角色类型不正确")
    @Schema(description = "角色类型: STUDENT/TEACHER/GUEST/DATA_ADMIN", required = true)
    private String roleCode;
}
