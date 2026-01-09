package com.training.module.training.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 提交任务DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class SubmitTaskDTO {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 提交内容(文本答案或描述)
     */
    private String content;

    /**
     * 附件URL(学生上传的文件)
     */
    private String attachmentUrl;

    /**
     * 耗时(分钟)
     */
    private Integer timeSpent;
}
