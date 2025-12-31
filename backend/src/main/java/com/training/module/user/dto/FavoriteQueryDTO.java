package com.training.module.user.dto;

import lombok.Data;

/**
 * 收藏查询DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class FavoriteQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 资源类型（可选）: RESOURCE, NEWS, NOTICE, COURSE, LAB
     */
    private String resourceType;
}
