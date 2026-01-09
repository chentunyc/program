package com.training.module.training.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实训任务创建DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class TrainingTaskCreateDTO {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称不能超过200个字符")
    private String taskName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务权重(在项目总分100分中占的分数)
     */
    @NotNull(message = "任务权重不能为空")
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
}
