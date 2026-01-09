package com.training.module.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.module.training.entity.UserProject;
import com.training.module.training.vo.StudentProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户项目关联Mapper
 * 对应表: t_user_project
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface UserProjectMapper extends BaseMapper<UserProject> {

    /**
     * 分页查询学生参与的项目
     */
    IPage<StudentProjectVO> selectUserProjectPage(
            Page<StudentProjectVO> page,
            @Param("userId") Long userId,
            @Param("projectName") String projectName,
            @Param("category") String category,
            @Param("difficulty") Integer difficulty,
            @Param("projectStatus") Integer projectStatus,
            @Param("status") Integer status
    );

    /**
     * 获取学生参与项目的详情
     */
    StudentProjectVO selectUserProjectDetail(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId
    );

    /**
     * 获取项目的所有参与学生列表
     */
    List<StudentProjectVO> selectProjectUsers(@Param("projectId") Long projectId);

    /**
     * 检查用户是否已加入项目
     */
    UserProject selectByUserAndProject(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId
    );
}
