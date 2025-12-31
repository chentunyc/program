package com.training.module.news.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.training.common.base.PageResult;
import com.training.module.news.dto.NewsQueryRequest;
import com.training.module.news.dto.NewsSaveRequest;
import com.training.module.news.entity.News;

/**
 * 新闻服务接口
 *
 * @author Training Team
 * @since 2025-01-01
 */
public interface NewsService {

    /**
     * 分页查询新闻列表
     *
     * @param request 查询条件
     * @return 新闻分页列表
     */
    PageResult<News> getNewsPage(NewsQueryRequest request);

    /**
     * 根据ID获取新闻详情
     *
     * @param id 新闻ID
     * @return 新闻详情
     */
    News getNewsById(Long id);

    /**
     * 保存新闻(新增/更新)
     *
     * @param request 保存请求
     * @return 新闻ID
     */
    Long saveNews(NewsSaveRequest request);

    /**
     * 删除新闻
     *
     * @param id 新闻ID
     * @return 是否成功
     */
    boolean deleteNews(Long id);

    /**
     * 批量删除新闻
     *
     * @param ids 新闻ID列表
     * @return 是否成功
     */
    boolean batchDeleteNews(Long[] ids);

    /**
     * 发布新闻
     *
     * @param id 新闻ID
     * @return 是否成功
     */
    boolean publishNews(Long id);

    /**
     * 撤回新闻
     *
     * @param id 新闻ID
     * @return 是否成功
     */
    boolean withdrawNews(Long id);

    /**
     * 置顶/取消置顶
     *
     * @param id 新闻ID
     * @param isTop 是否置顶:0-否,1-是
     * @return 是否成功
     */
    boolean toggleTop(Long id, Integer isTop);

    /**
     * 推荐/取消推荐
     *
     * @param id 新闻ID
     * @param isRecommend 是否推荐:0-否,1-是
     * @return 是否成功
     */
    boolean toggleRecommend(Long id, Integer isRecommend);
}
