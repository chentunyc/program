package com.training.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户扩展信息视图对象
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户扩展信息")
public class UserExtendedProfileVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "院系/部门")
    private String department;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "出生日期")
    private String birthDate;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "个人简介")
    private String introduction;
}
