package com.training.module.training.controller;

import com.training.common.base.PageResult;
import com.training.common.base.Result;
import com.training.common.exception.BusinessException;
import com.training.common.utils.SecurityUtils;
import com.training.module.training.dto.StudentTrainingQueryDTO;
import com.training.module.training.dto.SubmitTaskDTO;
import com.training.module.training.service.StudentTrainingService;
import com.training.module.training.vo.StudentProjectVO;
import com.training.module.training.vo.TaskSubmissionVO;
import com.training.module.training.vo.TrainingProjectVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 学生实训中心控制器
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "学生实训中心", description = "学生查看、参与和完成实训项目")
@RestController
@RequestMapping("/v1/training")
public class StudentTrainingController {

    @Autowired
    private StudentTrainingService studentTrainingService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/uploads}")
    private String urlPrefix;

    /**
     * 获取可报名的项目列表
     */
    @Operation(summary = "获取可报名的项目列表")
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/available")
    public Result<PageResult<TrainingProjectVO>> getAvailableProjects(StudentTrainingQueryDTO queryDTO) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生获取可报名项目列表: studentId={}", studentId);
        PageResult<TrainingProjectVO> result = studentTrainingService.getAvailableProjects(queryDTO, studentId);
        return Result.success(result);
    }

    /**
     * 获取我参与的项目列表
     */
    @Operation(summary = "获取我参与的项目列表")
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/my-projects")
    public Result<PageResult<StudentProjectVO>> getMyProjects(StudentTrainingQueryDTO queryDTO) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生获取已参与项目列表: studentId={}", studentId);
        PageResult<StudentProjectVO> result = studentTrainingService.getMyProjects(queryDTO, studentId);
        return Result.success(result);
    }

    /**
     * 报名参加项目
     */
    @Operation(summary = "报名参加项目")
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/enroll/{projectId}")
    public Result<?> enrollProject(@PathVariable Long projectId) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生报名项目: studentId={}, projectId={}", studentId, projectId);
        boolean success = studentTrainingService.enrollProject(projectId, studentId);
        return success ? Result.success("报名成功") : Result.error("报名失败");
    }

    /**
     * 获取项目详情(学生视角)
     */
    @Operation(summary = "获取项目详情")
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/project/{projectId}")
    public Result<StudentProjectVO> getProjectDetail(@PathVariable Long projectId) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生获取项目详情: studentId={}, projectId={}", studentId, projectId);
        StudentProjectVO detail = studentTrainingService.getProjectDetail(projectId, studentId);
        return Result.success(detail);
    }

    /**
     * 获取项目任务列表
     */
    @Operation(summary = "获取项目任务列表")
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/project/{projectId}/tasks")
    public Result<List<TaskSubmissionVO>> getProjectTasks(@PathVariable Long projectId) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生获取项目任务列表: studentId={}, projectId={}", studentId, projectId);
        List<TaskSubmissionVO> tasks = studentTrainingService.getProjectTasks(projectId, studentId);
        return Result.success(tasks);
    }

    /**
     * 获取任务详情
     */
    @Operation(summary = "获取任务详情")
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/task/{taskId}")
    public Result<TaskSubmissionVO> getTaskDetail(@PathVariable Long taskId) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生获取任务详情: studentId={}, taskId={}", studentId, taskId);
        TaskSubmissionVO detail = studentTrainingService.getTaskDetail(taskId, studentId);
        return Result.success(detail);
    }

    /**
     * 提交任务
     */
    @Operation(summary = "提交任务")
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/task/submit")
    public Result<?> submitTask(@Validated @RequestBody SubmitTaskDTO submitDTO) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生提交任务: studentId={}, taskId={}", studentId, submitDTO.getTaskId());
        boolean success = studentTrainingService.submitTask(submitDTO, studentId);
        return success ? Result.success("提交成功") : Result.error("提交失败");
    }

    /**
     * 开始项目
     */
    @Operation(summary = "开始项目")
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/project/{projectId}/start")
    public Result<?> startProject(@PathVariable Long projectId) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生开始项目: studentId={}, projectId={}", studentId, projectId);
        boolean success = studentTrainingService.startProject(projectId, studentId);
        return success ? Result.success("开始成功") : Result.error("操作失败");
    }

    /**
     * 上传实训报告文件
     */
    @Operation(summary = "上传实训报告文件")
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/upload/report")
    public Result<String> uploadReport(@RequestParam("file") MultipartFile file) {
        Long studentId = SecurityUtils.getUserId();
        log.info("学生上传实训报告: studentId={}, fileName={}", studentId, file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        String lowerName = originalFilename.toLowerCase();
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".doc") && !lowerName.endsWith(".docx")) {
            throw new BusinessException("只支持上传 PDF、DOC、DOCX 格式的文件");
        }

        // 限制文件大小 (20MB)
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过20MB");
        }

        try {
            // 处理上传路径（与其他上传保持一致）
            Path basePath = Paths.get(uploadPath);
            if (!basePath.isAbsolute()) {
                basePath = Paths.get(System.getProperty("user.dir")).resolve(uploadPath).normalize();
            }

            // 生成文件存储路径
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;

            // 创建目录
            Path targetDir = basePath.resolve("training-reports").resolve(dateFolder);
            Files.createDirectories(targetDir);

            // 保存文件
            Path targetPath = targetDir.resolve(newFileName);
            file.transferTo(targetPath.toFile());

            // 返回访问URL
            String fileUrl = urlPrefix + "/training-reports/" + dateFolder + "/" + newFileName;
            log.info("实训报告上传成功: {}", fileUrl);

            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
}
