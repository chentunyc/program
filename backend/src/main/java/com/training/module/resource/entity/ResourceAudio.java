package com.training.module.resource.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 音频资源实体
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_resource_audio")
public class ResourceAudio {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 时长(秒)
     */
    private Integer duration;

    /**
     * 格式(mp3, wav等)
     */
    private String format;

    /**
     * 比特率
     */
    private String bitrate;

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
