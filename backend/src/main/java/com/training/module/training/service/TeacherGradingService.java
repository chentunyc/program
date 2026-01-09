package com.training.module.training.service;

import com.training.common.base.PageResult;
import com.training.module.training.dto.GradeSubmissionDTO;
import com.training.module.training.dto.GradingQueryDTO;
import com.training.module.training.vo.StudentProjectVO;
import com.training.module.training.vo.TaskSubmissionVO;
import com.training.module.training.vo.TrainingProjectVO;

import java.util.List;

/**
 * 教师批改评分服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface TeacherGradingService {

    /**
     * 获取教师负责的项目列表
     */
    List<TrainingProjectVO> getMyProjects(Long teacherId);

    /**
     * 获取项目的学生列表(含进度)
     */
    List<StudentProjectVO> getProjectStudents(Long projectId, Long teacherId);

    /**
     * 分页查询待批改/已批改的提交记录
     */
    PageResult<TaskSubmissionVO> getSubmissions(GradingQueryDTO queryDTO, Long teacherId);

    /**
     * 获取提交详情
     */
    TaskSubmissionVO getSubmissionDetail(Long submissionId, Long teacherId);

    /**
     * 批改评分
     */
    boolean gradeSubmission(GradeSubmissionDTO gradeDTO, Long teacherId);

    /**
     * 批量评分
     */
    boolean batchGradeSubmissions(List<GradeSubmissionDTO> gradeDTOs, Long teacherId);

    /**
     * 获取学生在项目中的所有提交记录
     */
    List<TaskSubmissionVO> getStudentSubmissions(Long projectId, Long studentId, Long teacherId);

    /**
     * 完成学生项目评分(计算总分)
     */
    boolean completeStudentGrading(Long projectId, Long studentId, Long teacherId);
}
