package com.training.module.news.dto;

import lombok.Data;

/**
 * 新闻查询请求DTO
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Data
public class NewsQueryRequest {

    /**
     * 关键词(标题/内容)
     */
    private String keyword;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态:0-草稿,1-已发布,2-已下架
     */
    private Integer status;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 是否推荐
     */
    private Integer isRecommend;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
