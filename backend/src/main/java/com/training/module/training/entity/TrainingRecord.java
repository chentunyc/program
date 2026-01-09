package com.training.module.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实训记录实体（学生参与任务的记录）
 * 对应表: t_training_record
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_training_record")
public class TrainingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 用户ID（学生）
     */
    private Long userId;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;

    /**
     * 用时(分钟)
     */
    private Integer duration;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 提交附件URL
     */
    private String submitAttachment;

    /**
     * 教师评语
     */
    private String teacherComment;

    /**
     * 状态: 0-进行中, 1-已提交, 2-已评分
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    // ========== 非数据库字段 ==========

    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户账号
     */
    @TableField(exist = false)
    private String username;

    /**
     * 任务名称
     */
    @TableField(exist = false)
    private String taskName;

    /**
     * 任务权重分数
     */
    @TableField(exist = false)
    private BigDecimal scoreWeight;

    /**
     * 项目ID
     */
    @TableField(exist = false)
    private Long projectId;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    private String projectName;
}
