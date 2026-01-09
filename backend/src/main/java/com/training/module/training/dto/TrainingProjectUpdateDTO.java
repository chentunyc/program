package com.training.module.training.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实训项目更新DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingProjectUpdateDTO extends TrainingProjectCreateDTO {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long id;

    /**
     * 状态: 0-未开始, 1-进行中, 2-已结束
     */
    private Integer status;
}
