package com.training.module.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实训项目实体
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_training_project")
public class TrainingProject {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目封面
     */
    private String coverImage;

    /**
     * 项目负责人ID
     */
    private Long managerId;

    /**
     * 项目类别
     */
    private String category;

    /**
     * 难度等级: 1-初级, 2-中级, 3-高级
     */
    private Integer difficulty;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 最大参与人数
     */
    private Integer maxMembers;

    /**
     * 已参与人数
     */
    private Integer memberCount;

    /**
     * 任务总数
     */
    private Integer totalTasks;

    /**
     * 状态: 0-未开始, 1-进行中, 2-已结束
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
     * 负责人姓名 - 非数据库字段
     */
    @TableField(exist = false)
    private String managerName;
}
