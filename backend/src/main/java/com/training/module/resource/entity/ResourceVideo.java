package com.training.module.resource.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频资源实体
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_resource_video")
public class ResourceVideo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源ID
     */
    private Long resourceId;

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
     * 格式(mp4, avi等)
     */
    private String format;

    /**
     * 字幕文件URL
     */
    private String subtitleUrl;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}
