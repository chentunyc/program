package com.training.module.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.module.training.entity.TrainingRecord;
import com.training.module.training.vo.TaskSubmissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实训记录Mapper
 * 对应表: t_training_record
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord> {

    /**
     * 获取学生在项目中的任务列表(含提交状态)
     */
    List<TaskSubmissionVO> selectUserTasksInProject(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId
    );

    /**
     * 获取任务提交详情
     */
    TaskSubmissionVO selectRecordDetail(@Param("id") Long id);

    /**
     * 分页查询待批改/已批改的提交记录(教师用)
     */
    IPage<TaskSubmissionVO> selectRecordsForGrading(
            Page<TaskSubmissionVO> page,
            @Param("projectId") Long projectId,
            @Param("taskId") Long taskId,
            @Param("userName") String userName,
            @Param("status") Integer status,
            @Param("managerId") Long managerId
    );

    /**
     * 获取用户在某任务的记录
     */
    TrainingRecord selectByUserAndTask(
            @Param("userId") Long userId,
            @Param("taskId") Long taskId
    );

    /**
     * 统计用户在项目中已完成(已提交)的任务数
     */
    int countCompletedTasks(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId
    );

    /**
     * 统计用户在项目中已评分的任务数
     */
    int countGradedTasks(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId
    );
}
