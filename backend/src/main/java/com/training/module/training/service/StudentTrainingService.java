package com.training.module.training.service;

import com.training.common.base.PageResult;
import com.training.module.training.dto.StudentTrainingQueryDTO;
import com.training.module.training.dto.SubmitTaskDTO;
import com.training.module.training.vo.StudentProjectVO;
import com.training.module.training.vo.TaskSubmissionVO;
import com.training.module.training.vo.TrainingProjectVO;

import java.util.List;

/**
 * 学生实训服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface StudentTrainingService {

    /**
     * 获取可报名的项目列表(进行中且未报名的项目)
     */
    PageResult<TrainingProjectVO> getAvailableProjects(StudentTrainingQueryDTO queryDTO, Long studentId);

    /**
     * 获取学生已参与的项目列表
     */
    PageResult<StudentProjectVO> getMyProjects(StudentTrainingQueryDTO queryDTO, Long studentId);

    /**
     * 报名参加项目
     */
    boolean enrollProject(Long projectId, Long studentId);

    /**
     * 获取学生在项目中的详情
     */
    StudentProjectVO getProjectDetail(Long projectId, Long studentId);

    /**
     * 获取学生在项目中的任务列表(含完成状态)
     */
    List<TaskSubmissionVO> getProjectTasks(Long projectId, Long studentId);

    /**
     * 获取任务详情(含学生提交信息)
     */
    TaskSubmissionVO getTaskDetail(Long taskId, Long studentId);

    /**
     * 提交任务
     */
    boolean submitTask(SubmitTaskDTO submitDTO, Long studentId);

    /**
     * 开始项目(首次开始任务时调用)
     */
    boolean startProject(Long projectId, Long studentId);
}
