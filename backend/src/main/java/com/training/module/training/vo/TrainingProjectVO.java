package com.training.module.training.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实训项目VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class TrainingProjectVO {

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
     * 负责人姓名
     */
    private String managerName;

    /**
     * 项目类别
     */
    private String category;

    /**
     * 难度等级: 1-初级, 2-中级, 3-高级
     */
    private Integer difficulty;

    /**
     * 难度名称
     */
    private String difficultyName;

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
     * 权重总和
     */
    private BigDecimal totalWeight;

    /**
     * 状态: 0-未开始, 1-进行中, 2-已结束
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 获取难度名称
     */
    public String getDifficultyName() {
        if (difficulty == null) return "";
        switch (difficulty) {
            case 1: return "初级";
            case 2: return "中级";
            case 3: return "高级";
            default: return "";
        }
    }

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case 0: return "未开始";
            case 1: return "进行中";
            case 2: return "已结束";
            default: return "";
        }
    }
}
