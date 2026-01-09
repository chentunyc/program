package com.training.module.training.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实训项目创建DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class TrainingProjectCreateDTO {

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称不能超过200个字符")
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
     * 项目类别
     */
    private String category;

    /**
     * 难度等级: 1-初级, 2-中级, 3-高级
     */
    private Integer difficulty = 1;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 最大参与人数
     */
    private Integer maxMembers;
}
