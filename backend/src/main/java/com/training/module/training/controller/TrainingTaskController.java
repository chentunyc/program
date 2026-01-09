package com.training.module.training.controller;

import com.training.common.base.PageResult;
import com.training.common.base.Result;
import com.training.common.utils.SecurityUtils;
import com.training.module.training.dto.TrainingTaskCreateDTO;
import com.training.module.training.dto.TrainingTaskQueryDTO;
import com.training.module.training.dto.TrainingTaskUpdateDTO;
import com.training.module.training.service.TrainingTaskService;
import com.training.module.training.vo.TrainingTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实训任务管理控制器
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "实训任务管理", description = "实训任务的增删改查、发布等操作")
@RestController
@RequestMapping("/v1/admin/training/task")
public class TrainingTaskController {

    @Autowired
    private TrainingTaskService taskService;

    /**
     * 分页查询任务列表
     */
    @Operation(summary = "分页查询任务列表")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @GetMapping("/page")
    public Result<PageResult<TrainingTaskVO>> getTaskPage(TrainingTaskQueryDTO queryDTO) {
        log.info("分页查询实训任务: {}", queryDTO);
        PageResult<TrainingTaskVO> pageResult = taskService.getTaskPage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据项目ID获取任务列表
     */
    @Operation(summary = "根据项目ID获取任务列表")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @GetMapping("/list/{projectId}")
    public Result<List<TrainingTaskVO>> getTasksByProjectId(@PathVariable Long projectId) {
        log.info("获取项目的任务列表: projectId={}", projectId);
        List<TrainingTaskVO> tasks = taskService.getTasksByProjectId(projectId);
        return Result.success(tasks);
    }

    /**
     * 获取任务详情
     */
    @Operation(summary = "获取任务详情")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @GetMapping("/{id}")
    public Result<TrainingTaskVO> getTaskById(@PathVariable Long id) {
        log.info("获取实训任务详情: id={}", id);
        TrainingTaskVO task = taskService.getTaskById(id);
        return Result.success(task);
    }

    /**
     * 创建任务
     */
    @Operation(summary = "创建任务")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PostMapping
    public Result<Long> createTask(@Validated @RequestBody TrainingTaskCreateDTO createDTO) {
        log.info("创建实训任务: projectId={}, taskName={}", createDTO.getProjectId(), createDTO.getTaskName());
        Long userId = SecurityUtils.getUserId();
        Long taskId = taskService.createTask(createDTO, userId);
        return Result.success("创建成功", taskId);
    }

    /**
     * 更新任务
     */
    @Operation(summary = "更新任务")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PutMapping
    public Result<?> updateTask(@Validated @RequestBody TrainingTaskUpdateDTO updateDTO) {
        log.info("更新实训任务: id={}", updateDTO.getId());
        Long userId = SecurityUtils.getUserId();
        boolean success = taskService.updateTask(updateDTO, userId);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除任务
     */
    @Operation(summary = "删除任务")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @DeleteMapping("/{id}")
    public Result<?> deleteTask(@PathVariable Long id) {
        log.info("删除实训任务: id={}", id);
        boolean success = taskService.deleteTask(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除任务
     */
    @Operation(summary = "批量删除任务")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @DeleteMapping("/batch")
    public Result<?> batchDeleteTask(@RequestBody Long[] ids) {
        log.info("批量删除实训任务: ids={}", (Object) ids);
        boolean success = taskService.batchDeleteTask(ids);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 发布任务
     */
    @Operation(summary = "发布任务")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PutMapping("/{id}/publish")
    public Result<?> publishTask(@PathVariable Long id) {
        log.info("发布实训任务: id={}", id);
        boolean success = taskService.publishTask(id);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 取消发布任务
     */
    @Operation(summary = "取消发布任务")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PutMapping("/{id}/unpublish")
    public Result<?> unpublishTask(@PathVariable Long id) {
        log.info("取消发布实训任务: id={}", id);
        boolean success = taskService.unpublishTask(id);
        return success ? Result.success("取消发布成功") : Result.error("操作失败");
    }

    /**
     * 调整任务排序
     */
    @Operation(summary = "调整任务排序")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    @PutMapping("/{id}/sort")
    public Result<?> updateTaskSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        log.info("调整任务排序: id={}, sortOrder={}", id, sortOrder);
        boolean success = taskService.updateTaskSort(id, sortOrder);
        return success ? Result.success("调整成功") : Result.error("调整失败");
    }
}
