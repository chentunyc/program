package com.training.module.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.training.entity.TrainingProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 实训项目Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface TrainingProjectMapper extends BaseMapper<TrainingProject> {

    /**
     * 获取最大项目编码
     *
     * @param prefix 编码前缀
     * @return 最大编码
     */
    @Select("SELECT project_code FROM t_training_project WHERE project_code LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY project_code DESC LIMIT 1")
    String selectMaxProjectCode(String prefix);

    /**
     * 更新任务总数
     *
     * @param projectId 项目ID
     * @param count 任务数量
     * @return 影响行数
     */
    @Update("UPDATE t_training_project SET total_tasks = #{count} WHERE id = #{projectId}")
    int updateTotalTasks(Long projectId, Integer count);

    /**
     * 更新参与人数
     *
     * @param projectId 项目ID
     * @param count 参与人数
     * @return 影响行数
     */
    @Update("UPDATE t_training_project SET member_count = #{count} WHERE id = #{projectId}")
    int updateMemberCount(Long projectId, Integer count);
}
