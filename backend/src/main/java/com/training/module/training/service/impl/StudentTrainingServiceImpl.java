package com.training.module.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.base.PageResult;
import com.training.common.exception.BusinessException;
import com.training.module.training.dto.StudentTrainingQueryDTO;
import com.training.module.training.dto.SubmitTaskDTO;
import com.training.module.training.entity.UserProject;
import com.training.module.training.entity.TrainingRecord;
import com.training.module.training.entity.TrainingProject;
import com.training.module.training.entity.TrainingTask;
import com.training.module.training.mapper.UserProjectMapper;
import com.training.module.training.mapper.TrainingRecordMapper;
import com.training.module.training.mapper.TrainingProjectMapper;
import com.training.module.training.mapper.TrainingTaskMapper;
import com.training.module.training.service.StudentTrainingService;
import com.training.module.training.vo.StudentProjectVO;
import com.training.module.training.vo.TaskSubmissionVO;
import com.training.module.training.vo.TrainingProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生实训服务实现
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class StudentTrainingServiceImpl implements StudentTrainingService {

    @Autowired
    private TrainingProjectMapper projectMapper;

    @Autowired
    private TrainingTaskMapper taskMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private TrainingRecordMapper trainingRecordMapper;

    @Override
    public PageResult<TrainingProjectVO> getAvailableProjects(StudentTrainingQueryDTO queryDTO, Long studentId) {
        Page<TrainingProject> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 查询进行中的项目
        LambdaQueryWrapper<TrainingProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingProject::getStatus, 1) // 进行中
                .eq(TrainingProject::getIsDeleted, 0);

        if (queryDTO.getProjectName() != null && !queryDTO.getProjectName().isEmpty()) {
            wrapper.like(TrainingProject::getProjectName, queryDTO.getProjectName());
        }
        if (queryDTO.getCategory() != null && !queryDTO.getCategory().isEmpty()) {
            wrapper.eq(TrainingProject::getCategory, queryDTO.getCategory());
        }
        if (queryDTO.getDifficulty() != null) {
            wrapper.eq(TrainingProject::getDifficulty, queryDTO.getDifficulty());
        }

        wrapper.orderByDesc(TrainingProject::getCreateTime);

        IPage<TrainingProject> projectPage = projectMapper.selectPage(page, wrapper);

        // 转换为VO并过滤已报名的项目
        List<TrainingProjectVO> voList = projectPage.getRecords().stream()
                .filter(project -> {
                    // 检查学生是否已报名
                    UserProject up = userProjectMapper.selectByUserAndProject(studentId, project.getId());
                    return up == null;
                })
                .map(project -> {
                    TrainingProjectVO vo = new TrainingProjectVO();
                    BeanUtils.copyProperties(project, vo);
                    return vo;
                })
                .toList();

        return PageResult.of(voList, projectPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<StudentProjectVO> getMyProjects(StudentTrainingQueryDTO queryDTO, Long studentId) {
        Page<StudentProjectVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        IPage<StudentProjectVO> projectPage = userProjectMapper.selectUserProjectPage(
                page,
                studentId,
                queryDTO.getProjectName(),
                queryDTO.getCategory(),
                queryDTO.getDifficulty(),
                queryDTO.getProjectStatus(),
                queryDTO.getStatus()
        );

        return PageResult.of(projectPage.getRecords(), projectPage.getTotal(),
                queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enrollProject(Long projectId, Long studentId) {
        // 检查项目是否存在且进行中
        TrainingProject project = projectMapper.selectById(projectId);
        if (project == null || project.getIsDeleted() == 1) {
            throw new BusinessException("项目不存在");
        }
        if (project.getStatus() != 1) {
            throw new BusinessException("项目未开始或已结束，无法报名");
        }

        // 检查是否已报名
        UserProject existing = userProjectMapper.selectByUserAndProject(studentId, projectId);
        if (existing != null) {
            throw new BusinessException("您已报名该项目");
        }

        // 检查人数限制
        if (project.getMaxMembers() != null && project.getMemberCount() >= project.getMaxMembers()) {
            throw new BusinessException("项目人数已满");
        }

        // 创建报名记录
        UserProject userProject = new UserProject();
        userProject.setUserId(studentId);
        userProject.setProjectId(projectId);
        userProject.setJoinTime(LocalDateTime.now());
        userProject.setCompletedTasks(0);
        userProject.setProgress(0);
        userProject.setStatus(1); // 进行中

        int result = userProjectMapper.insert(userProject);

        // 更新项目参与人数
        if (result > 0) {
            project.setMemberCount(project.getMemberCount() == null ? 1 : project.getMemberCount() + 1);
            projectMapper.updateById(project);
        }

        return result > 0;
    }

    @Override
    public StudentProjectVO getProjectDetail(Long projectId, Long studentId) {
        StudentProjectVO vo = userProjectMapper.selectUserProjectDetail(studentId, projectId);
        if (vo == null) {
            throw new BusinessException("您未参加该项目");
        }
        return vo;
    }

    @Override
    public List<TaskSubmissionVO> getProjectTasks(Long projectId, Long studentId) {
        // 验证学生已参加该项目
        UserProject up = userProjectMapper.selectByUserAndProject(studentId, projectId);
        if (up == null) {
            throw new BusinessException("您未参加该项目");
        }

        return trainingRecordMapper.selectUserTasksInProject(studentId, projectId);
    }

    @Override
    public TaskSubmissionVO getTaskDetail(Long taskId, Long studentId) {
        // 获取任务信息
        TrainingTask task = taskMapper.selectById(taskId);
        if (task == null || task.getIsDeleted() == 1) {
            throw new BusinessException("任务不存在");
        }

        // 验证学生已参加该项目
        UserProject up = userProjectMapper.selectByUserAndProject(studentId, task.getProjectId());
        if (up == null) {
            throw new BusinessException("您未参加该项目");
        }

        // 获取提交记录
        TrainingRecord record = trainingRecordMapper.selectByUserAndTask(studentId, taskId);

        TaskSubmissionVO vo = new TaskSubmissionVO();
        vo.setTaskId(task.getId());
        vo.setTaskName(task.getTaskName());
        vo.setTaskDescription(task.getDescription());
        vo.setScoreWeight(task.getScoreWeight());
        vo.setTimeLimit(task.getTimeLimit());
        vo.setTaskAttachmentUrl(task.getAttachmentUrl());
        vo.setProjectId(task.getProjectId());
        vo.setSortOrder(task.getSortOrder());

        if (record != null) {
            vo.setId(record.getId());
            vo.setStudentId(record.getUserId());
            vo.setAttachmentUrl(record.getSubmitAttachment());
            vo.setSubmitTime(record.getSubmitTime());
            vo.setTimeSpent(record.getDuration());
            vo.setScore(record.getScore());
            vo.setFeedback(record.getTeacherComment());
            vo.setStatus(record.getStatus());
        } else {
            vo.setStatus(0); // 未提交
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitTask(SubmitTaskDTO submitDTO, Long studentId) {
        // 验证任务
        TrainingTask task = taskMapper.selectById(submitDTO.getTaskId());
        if (task == null || task.getIsDeleted() == 1) {
            throw new BusinessException("任务不存在");
        }
        if (task.getStatus() != 1) {
            throw new BusinessException("任务未发布");
        }
        if (!task.getProjectId().equals(submitDTO.getProjectId())) {
            throw new BusinessException("任务与项目不匹配");
        }

        // 验证学生已参加该项目
        UserProject up = userProjectMapper.selectByUserAndProject(studentId, submitDTO.getProjectId());
        if (up == null) {
            throw new BusinessException("您未参加该项目");
        }

        // 检查项目是否进行中
        TrainingProject project = projectMapper.selectById(submitDTO.getProjectId());
        if (project == null || project.getStatus() != 1) {
            throw new BusinessException("项目未开始或已结束");
        }

        // 查找或创建提交记录
        TrainingRecord record = trainingRecordMapper.selectByUserAndTask(studentId, submitDTO.getTaskId());
        boolean isNew = (record == null);

        if (isNew) {
            record = new TrainingRecord();
            record.setUserId(studentId);
            record.setTaskId(submitDTO.getTaskId());
        } else if (record.getStatus() == 2) {
            throw new BusinessException("任务已批改，无法重新提交");
        }

        record.setSubmitAttachment(submitDTO.getAttachmentUrl());
        record.setDuration(submitDTO.getTimeSpent());
        record.setSubmitTime(LocalDateTime.now());
        record.setStatus(1); // 已提交待批改

        int result;
        if (isNew) {
            result = trainingRecordMapper.insert(record);
        } else {
            result = trainingRecordMapper.updateById(record);
        }

        // 更新学生项目进度
        if (result > 0 && isNew) {
            int completedTasks = trainingRecordMapper.countCompletedTasks(studentId, submitDTO.getProjectId());
            up.setCompletedTasks(completedTasks);

            // 计算进度百分比
            int totalTasks = project.getTotalTasks() != null ? project.getTotalTasks() : 0;
            if (totalTasks > 0) {
                up.setProgress(completedTasks * 100 / totalTasks);
            }

            // 检查是否全部完成
            if (totalTasks > 0 && completedTasks >= totalTasks) {
                up.setStatus(2); // 已完成
            }

            userProjectMapper.updateById(up);
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startProject(Long projectId, Long studentId) {
        UserProject up = userProjectMapper.selectByUserAndProject(studentId, projectId);
        if (up == null) {
            throw new BusinessException("您未参加该项目");
        }

        // 项目已经在进行中
        return true;
    }
}
