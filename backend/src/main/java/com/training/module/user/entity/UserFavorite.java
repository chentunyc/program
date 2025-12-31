package com.training.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@TableName("t_user_favorite")
public class UserFavorite {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资源类型: NEWS, NOTICE, COURSE, RESOURCE, LAB
     */
    private String resourceType;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
