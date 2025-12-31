package com.training.module.resource.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源详情VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class ResourceDetailVO {

    /**
     * 资源ID
     */
    private Long id;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源类型: SIMULATION, VIDEO, AUDIO, DOCUMENT
     */
    private String resourceType;

    /**
     * 资源类型名称
     */
    private String resourceTypeName;

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
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 上传者名称
     */
    private String uploaderName;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件大小(格式化)
     */
    private String fileSizeFormat;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 是否共享: 0-否, 1-是
     */
    private Integer isShared;

    /**
     * 状态: 0-待审核, 1-已发布, 2-已下架
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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
     * 时长(格式化 HH:mm:ss)
     */
    private String durationFormat;

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
     * 音频时长(格式化 HH:mm:ss)
     */
    private String audioDurationFormat;

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
