package com.training.module.resource.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源列表VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class ResourceVO {

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
}
