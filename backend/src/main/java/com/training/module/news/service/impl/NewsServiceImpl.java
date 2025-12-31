package com.training.module.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.base.PageResult;
import com.training.common.exception.BusinessException;
import com.training.module.news.dto.NewsQueryRequest;
import com.training.module.news.dto.NewsSaveRequest;
import com.training.module.news.entity.News;
import com.training.module.news.mapper.NewsMapper;
import com.training.module.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 新闻服务实现类
 * 遵循单一职责原则,专注于新闻业务逻辑
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Slf4j
@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 分页查询新闻列表
     */
    @Override
    public PageResult<News> getNewsPage(NewsQueryRequest request) {
        Page<News> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<News> newsPage = newsMapper.selectNewsPage(page, request);
        return PageResult.from(newsPage);
    }

    /**
     * 根据ID获取新闻详情
     */
    @Override
    public News getNewsById(Long id) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }

        // 增加浏览次数
        newsMapper.incrementViewCount(id);

        return news;
    }

    /**
     * 保存新闻(新增/更新)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveNews(NewsSaveRequest request) {
        News news = new News();
        BeanUtil.copyProperties(request, news);

        // 设置默认值
        if (news.getViewCount() == null) {
            news.setViewCount(0);
        }
        if (news.getIsTop() == null) {
            news.setIsTop(0);
        }
        if (news.getIsRecommend() == null) {
            news.setIsRecommend(0);
        }
        if (news.getStatus() == null) {
            news.setStatus(0); // 默认为草稿
        }

        if (news.getId() == null) {
            // 新增
            newsMapper.insert(news);
        } else {
            // 更新
            News existingNews = newsMapper.selectById(news.getId());
            if (existingNews == null) {
                throw new BusinessException("新闻不存在");
            }
            newsMapper.updateById(news);
        }

        return news.getId();
    }

    /**
     * 删除新闻
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNews(Long id) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }

        return newsMapper.deleteById(id) > 0;
    }

    /**
     * 批量删除新闻
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteNews(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException("请选择要删除的新闻");
        }

        return newsMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    /**
     * 发布新闻
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishNews(Long id) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }

        LambdaUpdateWrapper<News> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(News::getId, id)
                .set(News::getStatus, 1)
                .set(News::getPublishTime, LocalDateTime.now());

        return newsMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 撤回新闻
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawNews(Long id) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }

        LambdaUpdateWrapper<News> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(News::getId, id)
                .set(News::getStatus, 2);

        return newsMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 置顶/取消置顶
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleTop(Long id, Integer isTop) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        if (isTop == null || (isTop != 0 && isTop != 1)) {
            throw new BusinessException("置顶参数错误");
        }

        LambdaUpdateWrapper<News> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(News::getId, id)
                .set(News::getIsTop, isTop);

        return newsMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 推荐/取消推荐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleRecommend(Long id, Integer isRecommend) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        if (isRecommend == null || (isRecommend != 0 && isRecommend != 1)) {
            throw new BusinessException("推荐参数错误");
        }

        LambdaUpdateWrapper<News> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(News::getId, id)
                .set(News::getIsRecommend, isRecommend);

        return newsMapper.update(null, updateWrapper) > 0;
    }
}
