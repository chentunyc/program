package com.training.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 管理员更新用户请求DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "更新用户请求")
public class UpdateUserDTO {

    @Schema(description = "真实姓名")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    @Schema(description = "编号(学号/工号/访客ID)")
    private String employeeNo;

    @Schema(description = "性别:0-未知,1-男,2-女")
    private Integer gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态:0-禁用,1-正常")
    private Integer status;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
