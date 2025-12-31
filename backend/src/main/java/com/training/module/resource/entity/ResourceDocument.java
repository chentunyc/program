package com.training.module.resource.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档资源实体
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_resource_document")
public class ResourceDocument {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 文档URL
     */
    private String documentUrl;

    /**
     * 格式(pdf, doc, ppt等)
     */
    private String format;

    /**
     * 页数
     */
    private Integer pageCount;

    /**
     * 预览URL
     */
    private String previewUrl;

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
