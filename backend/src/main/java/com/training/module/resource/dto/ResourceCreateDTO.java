package com.training.module.resource.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 资源创建DTO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class ResourceCreateDTO {

    /**
     * 资源名称
     */
    @NotBlank(message = "资源名称不能为空")
    private String resourceName;

    /**
     * 资源类型: SIMULATION, VIDEO, AUDIO, DOCUMENT
     */
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;

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
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 是否共享: 0-否, 1-是
     */
    private Integer isShared = 0;

    // ===== 虚拟仿真资源特有字段 =====
    /**
     * 仿真URL/启动地址
     */
    private String simulationUrl;

    /**
     * 技术栈(Unity3D, WebGL等)
     */
    private String technology;

    /**
     * 支持平台(PC, Mobile, Web)
     */
    private String supportPlatform;

    /**
     * 最低配置要求
     */
    private String minRequirement;

    // ===== 视频资源特有字段 =====
    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 时长(秒)
     */
    private Integer duration;

    /**
     * 分辨率(1080p, 720p等)
     */
    private String resolution;

    /**
     * 视频格式(mp4, avi等)
     */
    private String videoFormat;

    /**
     * 字幕文件URL
     */
    private String subtitleUrl;

    // ===== 音频资源特有字段 =====
    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 音频时长(秒)
     */
    private Integer audioDuration;

    /**
     * 音频格式(mp3, wav等)
     */
    private String audioFormat;

    /**
     * 比特率
     */
    private String bitrate;

    // ===== 文档资源特有字段 =====
    /**
     * 文档URL
     */
    private String documentUrl;

    /**
     * 文档格式(pdf, doc, ppt等)
     */
    private String documentFormat;

    /**
     * 页数
     */
    private Integer pageCount;

    /**
     * 预览URL
     */
    private String previewUrl;
}
