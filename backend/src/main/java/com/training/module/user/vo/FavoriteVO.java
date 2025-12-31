package com.training.module.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏VO（包含资源详情）
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class FavoriteVO {

    /**
     * 收藏ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资源类型: RESOURCE, NEWS, NOTICE, COURSE, LAB
     */
    private String resourceType;

    /**
     * 资源类型名称
     */
    private String resourceTypeName;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 收藏时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // ===== 资源基本信息 =====

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 下载次数
     */
    private Integer downloadCount;
}
