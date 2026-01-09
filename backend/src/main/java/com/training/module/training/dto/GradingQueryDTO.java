package com.training.module.training.dto;

import lombok.Data;

/**
 * 教师批改查询DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class GradingQueryDTO {

    /**
     * 当前页
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
     * 任务ID
     */
    private Long taskId;

    /**
     * 学生姓名(模糊搜索)
     */
    private String studentName;

    /**
     * 提交状态: 1-已提交待批改, 2-已批改
     */
    private Integer status;
}
