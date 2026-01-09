package com.training.module.training.dto;

import lombok.Data;

/**
 * 学生实训查询DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class StudentTrainingQueryDTO {

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 项目名称(模糊搜索)
     */
    private String projectName;

    /**
     * 项目类别
     */
    private String category;

    /**
     * 难度等级
     */
    private Integer difficulty;

    /**
     * 项目状态: 0-未开始, 1-进行中, 2-已结束
     */
    private Integer projectStatus;

    /**
     * 学生参与状态: 0-已报名, 1-进行中, 2-已完成, 3-已评分
     */
    private Integer status;
}
