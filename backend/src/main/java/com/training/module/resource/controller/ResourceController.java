package com.training.module.resource.controller;

import com.training.common.base.PageResult;
import com.training.common.base.Result;
import com.training.common.utils.SecurityUtils;
import com.training.module.resource.dto.ResourceCreateDTO;
import com.training.module.resource.dto.ResourceQueryDTO;
import com.training.module.resource.dto.ResourceUpdateDTO;
import com.training.module.resource.service.ResourceService;
import com.training.module.resource.vo.ResourceDetailVO;
import com.training.module.resource.vo.ResourceStatsVO;
import com.training.module.resource.vo.ResourceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 资源管理控制器
 * 处理资源的增删改查请求
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "资源管理", description = "资源的增删改查、发布、下架等操作")
@RestController
@RequestMapping("/v1/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 分页查询资源列表
     */
    @Operation(summary = "分页查询资源列表")
    @GetMapping("/page")
    public Result<PageResult<ResourceVO>> getResourcePage(ResourceQueryDTO queryDTO) {
        log.info("分页查询资源列表: {}", queryDTO);
        PageResult<ResourceVO> pageResult = resourceService.getResourcePage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取资源详情
     */
    @Operation(summary = "获取资源详情")
    @GetMapping("/{id}")
    public Result<ResourceDetailVO> getResourceById(@PathVariable Long id) {
        log.info("获取资源详情: id={}", id);
        // 增加浏览次数
        resourceService.incrementViewCount(id);
        ResourceDetailVO detail = resourceService.getResourceById(id);
        return Result.success(detail);
    }

    /**
     * 创建资源
     */
    @Operation(summary = "创建资源")
    @PreAuthorize("hasAnyAuthority('resource:create', 'ADMIN', 'TEACHER', 'DATA_ADMIN')")
    @PostMapping
    public Result<Long> createResource(@Validated @RequestBody ResourceCreateDTO createDTO) {
        log.info("创建资源: {}", createDTO.getResourceName());
        Long userId = SecurityUtils.getUserId();
        Long resourceId = resourceService.createResource(createDTO, userId);
        return Result.success("创建成功", resourceId);
    }

    /**
     * 更新资源
     */
    @Operation(summary = "更新资源")
    @PreAuthorize("hasAnyAuthority('resource:edit', 'TEACHER', 'DATA_ADMIN')")
    @PutMapping
    public Result<?> updateResource(@Validated @RequestBody ResourceUpdateDTO updateDTO) {
        log.info("更新资源: id={}", updateDTO.getId());
        Long userId = SecurityUtils.getUserId();
        boolean success = resourceService.updateResource(updateDTO, userId);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除资源
     */
    @Operation(summary = "删除资源")
    @PreAuthorize("hasAnyAuthority('resource:delete', 'DATA_ADMIN')")
    @DeleteMapping("/{id}")
    public Result<?> deleteResource(@PathVariable Long id) {
        log.info("删除资源: id={}", id);
        boolean success = resourceService.deleteResource(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除资源
     */
    @Operation(summary = "批量删除资源")
    @PreAuthorize("hasAnyAuthority('resource:delete', 'DATA_ADMIN')")
    @DeleteMapping("/batch")
    public Result<?> batchDeleteResource(@RequestBody Long[] ids) {
        log.info("批量删除资源: ids={}", ids);
        boolean success = resourceService.batchDeleteResource(ids);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 发布资源
     */
    @Operation(summary = "发布资源")
    @PreAuthorize("hasAnyAuthority('resource:publish', 'DATA_ADMIN')")
    @PutMapping("/{id}/publish")
    public Result<?> publishResource(@PathVariable Long id) {
        log.info("发布资源: id={}", id);
        boolean success = resourceService.publishResource(id);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 下架资源
     */
    @Operation(summary = "下架资源")
    @PreAuthorize("hasAnyAuthority('resource:offline', 'DATA_ADMIN')")
    @PutMapping("/{id}/offline")
    public Result<?> offlineResource(@PathVariable Long id) {
        log.info("下架资源: id={}", id);
        boolean success = resourceService.offlineResource(id);
        return success ? Result.success("下架成功") : Result.error("下架失败");
    }

    /**
     * 更新资源共享状态
     */
    @Operation(summary = "更新资源共享状态")
    @PreAuthorize("hasAnyAuthority('resource:edit', 'DATA_ADMIN')")
    @PutMapping("/{id}/share")
    public Result<?> updateResourceShare(@PathVariable Long id, @RequestParam Integer isShared) {
        log.info("更新资源共享状态: id={}, isShared={}", id, isShared);
        boolean success = resourceService.updateResourceShare(id, isShared);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 获取资源统计信息
     */
    @Operation(summary = "获取资源统计信息")
    @GetMapping("/stats")
    public Result<ResourceStatsVO> getResourceStats() {
        log.info("获取资源统计信息");
        ResourceStatsVO stats = resourceService.getResourceStats();
        return Result.success(stats);
    }

    /**
     * 获取热门资源
     */
    @Operation(summary = "获取热门资源")
    @GetMapping("/hot")
    public Result<List<ResourceVO>> getHotResources(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门资源: limit={}", limit);
        List<ResourceVO> resources = resourceService.getHotResources(limit);
        return Result.success(resources);
    }

    /**
     * 获取最新资源
     */
    @Operation(summary = "获取最新资源")
    @GetMapping("/latest")
    public Result<List<ResourceVO>> getLatestResources(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取最新资源: limit={}", limit);
        List<ResourceVO> resources = resourceService.getLatestResources(limit);
        return Result.success(resources);
    }

    /**
     * 记录下载
     */
    @Operation(summary = "记录下载")
    @PostMapping("/{id}/download")
    public Result<?> recordDownload(@PathVariable Long id) {
        log.info("记录下载: id={}", id);
        resourceService.incrementDownloadCount(id);
        return Result.success("记录成功");
    }

    /**
     * 上传资源文件
     */
    @Operation(summary = "上传资源文件")
    @PreAuthorize("hasAnyAuthority('resource:create', 'ADMIN', 'TEACHER', 'DATA_ADMIN')")
    @PostMapping("/upload/file")
    public Result<String> uploadResourceFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("resourceType") String resourceType) {
        log.info("上传资源文件: type={}, filename={}", resourceType, file.getOriginalFilename());
        String filePath = resourceService.uploadResourceFile(file, resourceType);
        return Result.success("上传成功", filePath);
    }

    /**
     * 上传封面图片
     */
    @Operation(summary = "上传封面图片")
    @PreAuthorize("hasAnyAuthority('resource:create', 'ADMIN', 'TEACHER', 'DATA_ADMIN')")
    @PostMapping("/upload/cover")
    public Result<String> uploadCoverImage(@RequestParam("file") MultipartFile file) {
        log.info("上传封面图片: filename={}", file.getOriginalFilename());
        String coverUrl = resourceService.uploadCoverImage(file);
        return Result.success("上传成功", coverUrl);
    }
}
