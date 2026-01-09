package com.training.module.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.training.entity.TrainingTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 实训任务Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface TrainingTaskMapper extends BaseMapper<TrainingTask> {

    /**
     * 统计项目的任务总数
     *
     * @param projectId 项目ID
     * @return 任务数量
     */
    @Select("SELECT COUNT(*) FROM t_training_task WHERE project_id = #{projectId} AND is_deleted = 0")
    Integer countTasksByProjectId(Long projectId);

    /**
     * 统计项目的权重总和
     *
     * @param projectId 项目ID
     * @return 权重总和
     */
    @Select("SELECT COALESCE(SUM(score_weight), 0) FROM t_training_task WHERE project_id = #{projectId} AND is_deleted = 0")
    BigDecimal sumWeightByProjectId(Long projectId);

    /**
     * 获取项目的最大排序序号
     *
     * @param projectId 项目ID
     * @return 最大排序序号
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM t_training_task WHERE project_id = #{projectId} AND is_deleted = 0")
    Integer getMaxSortOrder(Long projectId);
}
