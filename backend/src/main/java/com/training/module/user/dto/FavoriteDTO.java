package com.training.module.user.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 收藏操作DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class FavoriteDTO {

    /**
     * 资源类型: RESOURCE, NEWS, NOTICE, COURSE, LAB
     */
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;

    /**
     * 资源ID
     */
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;
}
