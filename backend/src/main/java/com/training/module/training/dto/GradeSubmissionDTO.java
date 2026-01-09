package com.training.module.training.dto;

import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 评分DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class GradeSubmissionDTO {

    /**
     * 提交记录ID
     */
    @NotNull(message = "提交记录ID不能为空")
    private Long submissionId;

    /**
     * 得分(0-100或按任务权重)
     */
    @NotNull(message = "得分不能为空")
    @DecimalMin(value = "0", message = "得分不能小于0")
    @DecimalMax(value = "100", message = "得分不能大于100")
    private BigDecimal score;

    /**
     * 教师评语
     */
    private String feedback;
}
