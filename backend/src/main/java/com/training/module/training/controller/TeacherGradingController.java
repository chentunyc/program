package com.training.module.training.controller;

import com.training.common.base.PageResult;
import com.training.common.base.Result;
import com.training.common.utils.SecurityUtils;
import com.training.module.training.dto.GradeSubmissionDTO;
import com.training.module.training.dto.GradingQueryDTO;
import com.training.module.training.service.TeacherGradingService;
import com.training.module.training.vo.StudentProjectVO;
import com.training.module.training.vo.TaskSubmissionVO;
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
 * 教师批改评分控制器
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "教师批改评分", description = "教师批改学生提交的实训任务")
@RestController
@RequestMapping("/v1/admin/training/grading")
public class TeacherGradingController {

    @Autowired
    private TeacherGradingService teacherGradingService;

    /**
     * 获取我负责的项目列表
     */
    @Operation(summary = "获取我负责的项目列表")
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/my-projects")
    public Result<List<TrainingProjectVO>> getMyProjects() {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师获取负责的项目列表: teacherId={}", teacherId);
        List<TrainingProjectVO> projects = teacherGradingService.getMyProjects(teacherId);
        return Result.success(projects);
    }

    /**
     * 获取项目的学生列表
     */
    @Operation(summary = "获取项目的学生列表")
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/project/{projectId}/students")
    public Result<List<StudentProjectVO>> getProjectStudents(@PathVariable Long projectId) {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师获取项目学生列表: teacherId={}, projectId={}", teacherId, projectId);
        List<StudentProjectVO> students = teacherGradingService.getProjectStudents(projectId, teacherId);
        return Result.success(students);
    }

    /**
     * 分页查询待批改/已批改的提交记录
     */
    @Operation(summary = "查询提交记录")
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/submissions")
    public Result<PageResult<TaskSubmissionVO>> getSubmissions(GradingQueryDTO queryDTO) {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师查询提交记录: teacherId={}", teacherId);
        PageResult<TaskSubmissionVO> result = teacherGradingService.getSubmissions(queryDTO, teacherId);
        return Result.success(result);
    }

    /**
     * 获取提交详情
     */
    @Operation(summary = "获取提交详情")
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/submission/{submissionId}")
    public Result<TaskSubmissionVO> getSubmissionDetail(@PathVariable Long submissionId) {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师获取提交详情: teacherId={}, submissionId={}", teacherId, submissionId);
        TaskSubmissionVO detail = teacherGradingService.getSubmissionDetail(submissionId, teacherId);
        return Result.success(detail);
    }

    /**
     * 批改评分
     */
    @Operation(summary = "批改评分")
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/grade")
    public Result<?> gradeSubmission(@Validated @RequestBody GradeSubmissionDTO gradeDTO) {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师批改评分: teacherId={}, submissionId={}", teacherId, gradeDTO.getSubmissionId());
        boolean success = teacherGradingService.gradeSubmission(gradeDTO, teacherId);
        return success ? Result.success("评分成功") : Result.error("评分失败");
    }

    /**
     * 批量评分
     */
    @Operation(summary = "批量评分")
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/grade/batch")
    public Result<?> batchGradeSubmissions(@Validated @RequestBody List<GradeSubmissionDTO> gradeDTOs) {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师批量评分: teacherId={}, count={}", teacherId, gradeDTOs.size());
        boolean success = teacherGradingService.batchGradeSubmissions(gradeDTOs, teacherId);
        return success ? Result.success("批量评分成功") : Result.error("批量评分失败");
    }

    /**
     * 获取学生在项目中的所有提交记录
     */
    @Operation(summary = "获取学生提交记录")
    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/project/{projectId}/student/{studentId}/submissions")
    public Result<List<TaskSubmissionVO>> getStudentSubmissions(
            @PathVariable Long projectId,
            @PathVariable Long studentId) {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师获取学生提交记录: teacherId={}, projectId={}, studentId={}", teacherId, projectId, studentId);
        List<TaskSubmissionVO> submissions = teacherGradingService.getStudentSubmissions(projectId, studentId, teacherId);
        return Result.success(submissions);
    }

    /**
     * 完成学生项目评分(计算总分)
     */
    @Operation(summary = "完成学生评分")
    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/project/{projectId}/student/{studentId}/complete")
    public Result<?> completeStudentGrading(
            @PathVariable Long projectId,
            @PathVariable Long studentId) {
        Long teacherId = SecurityUtils.getUserId();
        log.info("教师完成学生评分: teacherId={}, projectId={}, studentId={}", teacherId, projectId, studentId);
        boolean success = teacherGradingService.completeStudentGrading(projectId, studentId, teacherId);
        return success ? Result.success("评分完成") : Result.error("操作失败");
    }
}
