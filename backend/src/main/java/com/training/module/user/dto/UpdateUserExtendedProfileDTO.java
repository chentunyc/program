package com.training.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户扩展信息请求DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "更新用户扩展信息请求")
public class UpdateUserExtendedProfileDTO {

    @Schema(description = "院系/部门")
    @Size(max = 100, message = "院系/部门长度不能超过100个字符")
    private String department;

    @Schema(description = "专业")
    @Size(max = 100, message = "专业长度不能超过100个字符")
    private String major;

    @Schema(description = "班级")
    @Size(max = 50, message = "班级长度不能超过50个字符")
    private String className;

    @Schema(description = "年级")
    @Size(max = 20, message = "年级长度不能超过20个字符")
    private String grade;

    @Schema(description = "出生日期")
    private String birthDate;

    @Schema(description = "身份证号")
    @Size(max = 18, message = "身份证号长度不能超过18个字符")
    private String idCard;

    @Schema(description = "地址")
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;

    @Schema(description = "个人简介")
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String introduction;
}
