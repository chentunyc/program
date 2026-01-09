package com.training.module.training.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实训任务VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class TrainingTaskVO {

    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

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
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case 0: return "未发布";
            case 1: return "已发布";
            default: return "";
        }
    }
}
