package com.training.module.resource.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 虚拟仿真资源实体
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_resource_simulation")
public class ResourceSimulation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源ID
     */
    private Long resourceId;

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
