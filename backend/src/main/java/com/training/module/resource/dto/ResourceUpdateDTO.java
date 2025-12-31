package com.training.module.resource.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 资源更新DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class ResourceUpdateDTO {

    /**
     * 资源ID
     */
    @NotNull(message = "资源ID不能为空")
    private Long id;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签(逗号分隔)
     */
    private String tags;

    /**
     * 是否共享: 0-否, 1-是
     */
    private Integer isShared;

    /**
     * 状态: 0-待审核, 1-已发布, 2-已下架
     */
    private Integer status;

    // ===== 虚拟仿真资源特有字段 =====
    private String simulationUrl;
    private String technology;
    private String supportPlatform;
    private String minRequirement;

    // ===== 视频资源特有字段 =====
    private String videoUrl;
    private Integer duration;
    private String resolution;
    private String videoFormat;
    private String subtitleUrl;

    // ===== 音频资源特有字段 =====
    private String audioUrl;
    private Integer audioDuration;
    private String audioFormat;
    private String bitrate;

    // ===== 文档资源特有字段 =====
    private String documentUrl;
    private String documentFormat;
    private Integer pageCount;
    private String previewUrl;
}
