package com.training.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.training.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体类
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_system_config")
public class SystemConfig extends BaseEntity {

    /**
     * 配置组:basic,upload,security
     */
    private String configGroup;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 值类型:string,number,boolean,json
     */
    private String configType;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;
}
