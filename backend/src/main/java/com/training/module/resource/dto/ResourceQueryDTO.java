package com.training.module.resource.dto;

import lombok.Data;

/**
 * 资源查询DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class ResourceQueryDTO {

    /**
     * 资源类型: SIMULATION, VIDEO, AUDIO, DOCUMENT
     */
    private String resourceType;

    /**
     * 分类
     */
    private String category;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 状态: 0-待审核, 1-已发布, 2-已下架
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式: asc/desc
     */
    private String sortOrder;
}
