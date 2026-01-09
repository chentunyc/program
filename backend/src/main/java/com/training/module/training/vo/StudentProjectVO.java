package com.training.module.training.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生参与项目VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class StudentProjectVO {

    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生用户名
     */
    private String studentUsername;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String projectDescription;

    /**
     * 项目封面
     */
    private String coverImage;

    /**
     * 项目类别
     */
    private String category;

    /**
     * 难度等级: 1-初级, 2-中级, 3-高级
     */
    private Integer difficulty;

    /**
     * 报名时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime enrollTime;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime completeTime;

    /**
     * 项目开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime projectStartTime;

    /**
     * 项目结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime projectEndTime;

    /**
     * 已完成任务数
     */
    private Integer completedTasks;

    /**
     * 总任务数
     */
    private Integer totalTasks;

    /**
     * 总分数
     */
    private BigDecimal totalScore;

    /**
     * 状态: 0-已报名, 1-进行中, 2-已完成, 3-已评分
     */
    private Integer status;

    /**
     * 项目状态: 0-未开始, 1-进行中, 2-已结束
     */
    private Integer projectStatus;

    /**
     * 进度百分比
     */
    private Integer progressPercent;
}
