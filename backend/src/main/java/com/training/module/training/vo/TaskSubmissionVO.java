package com.training.module.training.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务提交VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class TaskSubmissionVO {

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
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDescription;

    /**
     * 任务权重分数
     */
    private BigDecimal scoreWeight;

    /**
     * 时间限制(分钟)
     */
    private Integer timeLimit;

    /**
     * 任务附件URL
     */
    private String taskAttachmentUrl;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 提交内容
     */
    private String content;

    /**
     * 提交附件URL
     */
    private String attachmentUrl;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;

    /**
     * 耗时(分钟)
     */
    private Integer timeSpent;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 教师评语
     */
    private String feedback;

    /**
     * 评分教师ID
     */
    private Long gradedBy;

    /**
     * 评分教师姓名
     */
    private String graderName;

    /**
     * 评分时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gradedTime;

    /**
     * 状态: 0-未提交, 1-已提交待批改, 2-已批改
     */
    private Integer status;

    /**
     * 任务排序序号
     */
    private Integer sortOrder;
}
