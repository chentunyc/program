package com.training.module.news.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 新闻保存请求DTO
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Data
public class NewsSaveRequest {

    /**
     * 新闻ID(更新时必填)
     */
    private Long id;

    /**
     * 新闻标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 新闻摘要
     */
    private String summary;

    /**
     * 新闻内容
     */
    @NotBlank(message = "内容不能为空")
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
     * 分类
     */
    private String category;

    /**
     * 标签(逗号分隔)
     */
    private String tags;

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
    private LocalDateTime publishTime;

    /**
     * 状态:0-草稿,1-已发布,2-已下架
     */
    private Integer status;
}
