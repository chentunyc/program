package com.training.module.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实训任务实体
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_training_task")
public class TrainingTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务权重(在项目总分100分中占的分数)
     */
    private BigDecimal scoreWeight;

    /**
     * 时间限制(分钟)
     */
    private Integer timeLimit;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 状态: 0-未发布, 1-已发布
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
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 项目名称 - 非数据库字段
     */
    @TableField(exist = false)
    private String projectName;
}
