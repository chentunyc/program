package com.training.module.training.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实训任务更新DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingTaskUpdateDTO extends TrainingTaskCreateDTO {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long id;

    /**
     * 状态: 0-未发布, 1-已发布
     */
    private Integer status;
}
