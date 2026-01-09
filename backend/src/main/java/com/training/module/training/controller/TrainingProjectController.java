package com.training.module.training.controller;

import com.training.common.base.PageResult;
import com.training.common.base.Result;
import com.training.common.utils.SecurityUtils;
import com.training.module.training.dto.TrainingProjectCreateDTO;
import com.training.module.training.dto.TrainingProjectQueryDTO;
import com.training.module.training.dto.TrainingProjectUpdateDTO;
import com.training.module.training.service.TrainingProjectService;
import com.training.module.training.vo.TrainingProjectVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实训项目管理控制器
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "实训项目管理", description = "实训项目的增删改查、开始、结束等操作")
@RestController
@RequestMapping("/v1/admin/training/project")
public class TrainingProjectController {

    @Autowired
    private TrainingProjectService projectService;

    /**
     * 分页查询项目列表
     */
    @Operation(summary = "分页查询项目列表")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @GetMapping("/page")
    public Result<PageResult<TrainingProjectVO>> getProjectPage(TrainingProjectQueryDTO queryDTO) {
        log.info("分页查询实训项目: {}", queryDTO);
        PageResult<TrainingProjectVO> pageResult = projectService.getProjectPage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取项目详情
     */
    @Operation(summary = "获取项目详情")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @GetMapping("/{id}")
    public Result<TrainingProjectVO> getProjectById(@PathVariable Long id) {
        log.info("获取实训项目详情: id={}", id);
        TrainingProjectVO project = projectService.getProjectById(id);
        return Result.success(project);
    }

    /**
     * 创建项目
     */
    @Operation(summary = "创建项目")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PostMapping
    public Result<Long> createProject(@Validated @RequestBody TrainingProjectCreateDTO createDTO) {
        log.info("创建实训项目: {}", createDTO.getProjectName());
        Long userId = SecurityUtils.getUserId();
        Long projectId = projectService.createProject(createDTO, userId);
        return Result.success("创建成功", projectId);
    }

    /**
     * 更新项目
     */
    @Operation(summary = "更新项目")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PutMapping
    public Result<?> updateProject(@Validated @RequestBody TrainingProjectUpdateDTO updateDTO) {
        log.info("更新实训项目: id={}", updateDTO.getId());
        Long userId = SecurityUtils.getUserId();
        boolean success = projectService.updateProject(updateDTO, userId);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除项目
     */
    @Operation(summary = "删除项目")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @DeleteMapping("/{id}")
    public Result<?> deleteProject(@PathVariable Long id) {
        log.info("删除实训项目: id={}", id);
        boolean success = projectService.deleteProject(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除项目
     */
    @Operation(summary = "批量删除项目")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @DeleteMapping("/batch")
    public Result<?> batchDeleteProject(@RequestBody Long[] ids) {
        log.info("批量删除实训项目: ids={}", (Object) ids);
        boolean success = projectService.batchDeleteProject(ids);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 开始项目
     */
    @Operation(summary = "开始项目")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PutMapping("/{id}/start")
    public Result<?> startProject(@PathVariable Long id) {
        log.info("开始实训项目: id={}", id);
        boolean success = projectService.startProject(id);
        return success ? Result.success("项目已开始") : Result.error("操作失败");
    }

    /**
     * 结束项目
     */
    @Operation(summary = "结束项目")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PutMapping("/{id}/end")
    public Result<?> endProject(@PathVariable Long id) {
        log.info("结束实训项目: id={}", id);
        boolean success = projectService.endProject(id);
        return success ? Result.success("项目已结束") : Result.error("操作失败");
    }

    /**
     * 获取当前教师负责的项目列表
     */
    @Operation(summary = "获取我的项目列表")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/my")
    public Result<List<TrainingProjectVO>> getMyProjects() {
        Long userId = SecurityUtils.getUserId();
        log.info("获取教师负责的项目列表: teacherId={}", userId);
        List<TrainingProjectVO> projects = projectService.getProjectsByManager(userId);
        return Result.success(projects);
    }
}
