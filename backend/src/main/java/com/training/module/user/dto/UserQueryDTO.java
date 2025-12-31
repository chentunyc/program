package com.training.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询请求DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryDTO {

    @Schema(description = "关键词(用户名/真实姓名/学号/工号)")
    private String keyword;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "状态:0-禁用,1-正常")
    private Integer status;

    @Schema(description = "当前页")
    private Integer current = 1;

    @Schema(description = "每页大小")
    private Integer size = 10;
}
