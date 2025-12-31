package com.training.module.news.controller;

import com.training.common.base.PageResult;
import com.training.common.base.Result;
import com.training.module.news.dto.NewsQueryRequest;
import com.training.module.news.dto.NewsSaveRequest;
import com.training.module.news.entity.News;
import com.training.module.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻控制器
 * 处理新闻的增删改查请求
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Slf4j
@Tag(name = "新闻管理", description = "新闻的增删改查、发布、置顶等操作")
@RestController
@RequestMapping("/v1/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 分页查询新闻列表
     */
    @Operation(summary = "分页查询新闻列表")
    @GetMapping("/page")
    public Result<PageResult<News>> getNewsPage(NewsQueryRequest request) {
        log.info("分页查询新闻列表: {}", request);
        PageResult<News> pageResult = newsService.getNewsPage(request);
        return Result.success(pageResult);
    }

    /**
     * 获取新闻详情
     */
    @Operation(summary = "获取新闻详情")
    @GetMapping("/{id}")
    public Result<News> getNewsById(@PathVariable Long id) {
        log.info("获取新闻详情: id={}", id);
        News news = newsService.getNewsById(id);
        return Result.success(news);
    }

    /**
     * 保存新闻(新增/更新)
     */
    @Operation(summary = "保存新闻")
    @PreAuthorize("hasAnyAuthority('news:create', 'news:edit', 'ADMIN')")
    @PostMapping("/save")
    public Result<Long> saveNews(@Validated @RequestBody NewsSaveRequest request) {
        log.info("保存新闻: {}", request.getTitle());
        Long newsId = newsService.saveNews(request);
        return Result.success("保存成功", newsId);
    }

    /**
     * 删除新闻
     */
    @Operation(summary = "删除新闻")
    @PreAuthorize("hasAnyAuthority('news:delete', 'ADMIN')")
    @DeleteMapping("/{id}")
    public Result<?> deleteNews(@PathVariable Long id) {
        log.info("删除新闻: id={}", id);
        boolean success = newsService.deleteNews(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除新闻
     */
    @Operation(summary = "批量删除新闻")
    @PreAuthorize("hasAnyAuthority('news:delete', 'ADMIN')")
    @DeleteMapping("/batch")
    public Result<?> batchDeleteNews(@RequestBody Long[] ids) {
        log.info("批量删除新闻: ids={}", ids);
        boolean success = newsService.batchDeleteNews(ids);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 发布新闻
     */
    @Operation(summary = "发布新闻")
    @PreAuthorize("hasAnyAuthority('news:publish', 'ADMIN')")
    @PutMapping("/{id}/publish")
    public Result<?> publishNews(@PathVariable Long id) {
        log.info("发布新闻: id={}", id);
        boolean success = newsService.publishNews(id);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 撤回新闻
     */
    @Operation(summary = "撤回新闻")
    @PreAuthorize("hasAnyAuthority('news:withdraw', 'ADMIN')")
    @PutMapping("/{id}/withdraw")
    public Result<?> withdrawNews(@PathVariable Long id) {
        log.info("撤回新闻: id={}", id);
        boolean success = newsService.withdrawNews(id);
        return success ? Result.success("撤回成功") : Result.error("撤回失败");
    }

    /**
     * 置顶/取消置顶
     */
    @Operation(summary = "置顶/取消置顶")
    @PreAuthorize("hasAnyAuthority('news:top', 'ADMIN')")
    @PutMapping("/{id}/top")
    public Result<?> toggleTop(@PathVariable Long id, @RequestParam Integer isTop) {
        log.info("置顶设置: id={}, isTop={}", id, isTop);
        boolean success = newsService.toggleTop(id, isTop);
        return success ? Result.success("设置成功") : Result.error("设置失败");
    }

    /**
     * 推荐/取消推荐
     */
    @Operation(summary = "推荐/取消推荐")
    @PreAuthorize("hasAnyAuthority('news:recommend', 'ADMIN')")
    @PutMapping("/{id}/recommend")
    public Result<?> toggleRecommend(@PathVariable Long id, @RequestParam Integer isRecommend) {
        log.info("推荐设置: id={}, isRecommend={}", id, isRecommend);
        boolean success = newsService.toggleRecommend(id, isRecommend);
        return success ? Result.success("设置成功") : Result.error("设置失败");
    }
}
