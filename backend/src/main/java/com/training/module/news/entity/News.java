package com.training.module.news.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.training.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 新闻实体类
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_news")
public class News extends BaseEntity {

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 新闻摘要
     */
    private String summary;

    /**
     * 新闻内容
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者
     */
    private String author;

    /**
     * 来源
     */
    private String source;

    /**
     * 分类(系统公告/教学新闻/科研动态/通知公告/行业资讯)
     */
    private String category;

    /**
     * 标签(逗号分隔)
     */
    private String tags;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 是否置顶:0-否,1-是
     */
    private Integer isTop;

    /**
     * 是否推荐:0-否,1-是
     */
    private Integer isRecommend;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;

    /**
     * 状态:0-草稿,1-已发布,2-已下架
     */
    private Integer status;
}
