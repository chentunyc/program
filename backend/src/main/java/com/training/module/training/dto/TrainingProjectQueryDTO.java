package com.training.module.training.dto;

import lombok.Data;

/**
 * 实训项目查询DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class TrainingProjectQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 关键词（项目名称/编码）
     */
    private String keyword;

    /**
     * 项目类别
     */
    private String category;

    /**
     * 难度等级
     */
    private Integer difficulty;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 负责人ID
     */
    private Long managerId;
}
