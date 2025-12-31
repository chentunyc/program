package com.training.module.user.vo;

import lombok.Data;

/**
 * 收藏统计VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class FavoriteStatsVO {

    /**
     * 总收藏数
     */
    private Long totalCount;

    /**
     * 资源收藏数
     */
    private Long resourceCount;

    /**
     * 新闻收藏数
     */
    private Long newsCount;

    /**
     * 公告收藏数
     */
    private Long noticeCount;

    /**
     * 课程收藏数
     */
    private Long courseCount;

    /**
     * 实验室收藏数
     */
    private Long labCount;
}
