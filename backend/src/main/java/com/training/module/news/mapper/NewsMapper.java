package com.training.module.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.module.news.dto.NewsQueryRequest;
import com.training.module.news.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 新闻Mapper接口
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {

    /**
     * 分页查询新闻列表
     *
     * @param page 分页对象
     * @param request 查询条件
     * @return 新闻分页列表
     */
    IPage<News> selectNewsPage(Page<News> page, @Param("req") NewsQueryRequest request);

    /**
     * 增加浏览次数
     *
     * @param id 新闻ID
     * @return 影响行数
     */
    @Update("UPDATE t_news SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
}
