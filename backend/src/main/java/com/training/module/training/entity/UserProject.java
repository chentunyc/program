package com.training.module.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户项目关联实体（学生参与项目）
 * 对应表: t_user_project
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_user_project")
public class UserProject {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（学生）
     */
    private Long userId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime joinTime;

    /**
     * 完成进度(%)
     */
    private Integer progress;

    /**
     * 已完成任务数
     */
    private Integer completedTasks;

    /**
     * 总成绩
     */
    private BigDecimal totalScore;

    /**
     * 状态: 1-进行中, 2-已完成, 3-已退出
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
     * 项目名称
     */
    @TableField(exist = false)
    private String projectName;

    /**
     * 项目总任务数
     */
    @TableField(exist = false)
    private Integer totalTasks;
}
