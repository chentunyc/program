package com.training.module.training.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.base.PageResult;
import com.training.common.exception.BusinessException;
import com.training.module.training.dto.GradeSubmissionDTO;
import com.training.module.training.dto.GradingQueryDTO;
import com.training.module.training.entity.UserProject;
import com.training.module.training.entity.TrainingRecord;
import com.training.module.training.entity.TrainingProject;
import com.training.module.training.entity.TrainingTask;
import com.training.module.training.mapper.UserProjectMapper;
import com.training.module.training.mapper.TrainingRecordMapper;
import com.training.module.training.mapper.TrainingProjectMapper;
import com.training.module.training.mapper.TrainingTaskMapper;
import com.training.module.training.service.TeacherGradingService;
import com.training.module.training.service.TrainingProjectService;
import com.training.module.training.vo.StudentProjectVO;
import com.training.module.training.vo.TaskSubmissionVO;
import com.training.module.training.vo.TrainingProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师批改评分服务实现
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class TeacherGradingServiceImpl implements TeacherGradingService {

    @Autowired
    private TrainingProjectService projectService;

    @Autowired
    private TrainingProjectMapper projectMapper;

    @Autowired
    private TrainingTaskMapper taskMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private TrainingRecordMapper trainingRecordMapper;

    @Override
    public List<TrainingProjectVO> getMyProjects(Long teacherId) {
        return projectService.getProjectsByManager(teacherId);
    }

    @Override
    public List<StudentProjectVO> getProjectStudents(Long projectId, Long teacherId) {
        // 验证教师是否是项目负责人
        TrainingProject project = projectMapper.selectById(projectId);
        if (project == null || project.getIsDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }
        if (!project.getManagerId().equals(teacherId)) {
            throw new BusinessException("您不是该项目的负责人");
        }

        return userProjectMapper.selectProjectUsers(projectId);
    }

    @Override
    public PageResult<TaskSubmissionVO> getSubmissions(GradingQueryDTO queryDTO, Long teacherId) {
        Page<TaskSubmissionVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        IPage<TaskSubmissionVO> submissionPage = trainingRecordMapper.selectRecordsForGrading(
                page,
                queryDTO.getProjectId(),
                queryDTO.getTaskId(),
                queryDTO.getStudentName(),
                queryDTO.getStatus(),
                teacherId
        );

        return PageResult.of(submissionPage.getRecords(), submissionPage.getTotal(),
                queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public TaskSubmissionVO getSubmissionDetail(Long submissionId, Long teacherId) {
        TaskSubmissionVO vo = trainingRecordMapper.selectRecordDetail(submissionId);
        if (vo == null) {
            throw new BusinessException("提交记录不存在");
        }

        // 验证教师权限
        TrainingProject project = projectMapper.selectById(vo.getProjectId());
        if (project == null || !project.getManagerId().equals(teacherId)) {
            throw new BusinessException("您无权查看该提交记录");
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeSubmission(GradeSubmissionDTO gradeDTO, Long teacherId) {
        TrainingRecord record = trainingRecordMapper.selectById(gradeDTO.getSubmissionId());
        if (record == null || record.getIsDeleted() == 1) {
            throw new BusinessException("提交记录不存在");
        }

        if (record.getStatus() != 1) {
            throw new BusinessException("该提交未处于待批改状态");
        }

        // 获取任务信息（包含项目ID）
        TrainingTask task = taskMapper.selectById(record.getTaskId());
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 验证教师权限
        TrainingProject project = projectMapper.selectById(task.getProjectId());
        if (project == null || !project.getManagerId().equals(teacherId)) {
            throw new BusinessException("您无权批改该提交");
        }

        // 计算实际得分 (输入分数 * 任务权重 / 100)
        BigDecimal actualScore = gradeDTO.getScore()
                .multiply(task.getScoreWeight())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        record.setScore(actualScore);
        record.setTeacherComment(gradeDTO.getFeedback());
        record.setStatus(2); // 已批改

        return trainingRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchGradeSubmissions(List<GradeSubmissionDTO> gradeDTOs, Long teacherId) {
        for (GradeSubmissionDTO dto : gradeDTOs) {
            gradeSubmission(dto, teacherId);
        }
        return true;
    }

    @Override
    public List<TaskSubmissionVO> getStudentSubmissions(Long projectId, Long studentId, Long teacherId) {
        // 验证教师权限
        TrainingProject project = projectMapper.selectById(projectId);
        if (project == null || !project.getManagerId().equals(teacherId)) {
            throw new BusinessException("您无权查看该学生的提交记录");
        }

        return trainingRecordMapper.selectUserTasksInProject(studentId, projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeStudentGrading(Long projectId, Long studentId, Long teacherId) {
        // 验证教师权限
        TrainingProject project = projectMapper.selectById(projectId);
        if (project == null || !project.getManagerId().equals(teacherId)) {
            throw new BusinessException("您无权操作");
        }

        // 获取学生项目记录
        UserProject up = userProjectMapper.selectByUserAndProject(studentId, projectId);
        if (up == null) {
            throw new BusinessException("学生未参加该项目");
        }

        // 检查是否所有任务都已批改
        int gradedCount = trainingRecordMapper.countGradedTasks(studentId, projectId);
        if (project.getTotalTasks() != null && gradedCount < project.getTotalTasks()) {
            throw new BusinessException("还有任务未批改完成");
        }

        // 计算总分
        List<TaskSubmissionVO> submissions = trainingRecordMapper.selectUserTasksInProject(studentId, projectId);
        BigDecimal totalScore = submissions.stream()
                .filter(s -> s.getScore() != null)
                .map(TaskSubmissionVO::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 更新学生项目记录
        up.setTotalScore(totalScore);
        up.setStatus(2); // 已完成（status: 1-进行中, 2-已完成, 3-已退出）

        return userProjectMapper.updateById(up) > 0;
    }
}
