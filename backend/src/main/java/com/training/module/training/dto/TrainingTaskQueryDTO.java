package com.training.module.training.dto;

import lombok.Data;

/**
 * 实训任务查询DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class TrainingTaskQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 关键词（任务名称）
     */
    private String keyword;

    /**
     * 状态
     */
    private Integer status;
}
