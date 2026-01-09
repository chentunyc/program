package com.training.module.training.service;

import com.training.common.base.PageResult;
import com.training.module.training.dto.TrainingProjectCreateDTO;
import com.training.module.training.dto.TrainingProjectQueryDTO;
import com.training.module.training.dto.TrainingProjectUpdateDTO;
import com.training.module.training.vo.TrainingProjectVO;

import java.util.List;

/**
 * 实训项目服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface TrainingProjectService {

    /**
     * 分页查询项目列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<TrainingProjectVO> getProjectPage(TrainingProjectQueryDTO queryDTO);

    /**
     * 根据ID获取项目详情
     *
     * @param id 项目ID
     * @return 项目详情
     */
    TrainingProjectVO getProjectById(Long id);

    /**
     * 创建项目
     *
     * @param createDTO 创建请求
     * @param userId 用户ID
     * @return 项目ID
     */
    Long createProject(TrainingProjectCreateDTO createDTO, Long userId);

    /**
     * 更新项目
     *
     * @param updateDTO 更新请求
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateProject(TrainingProjectUpdateDTO updateDTO, Long userId);

    /**
     * 删除项目
     *
     * @param id 项目ID
     * @return 是否成功
     */
    boolean deleteProject(Long id);

    /**
     * 批量删除项目
     *
     * @param ids 项目ID数组
     * @return 是否成功
     */
    boolean batchDeleteProject(Long[] ids);

    /**
     * 开始项目
     *
     * @param id 项目ID
     * @return 是否成功
     */
    boolean startProject(Long id);

    /**
     * 结束项目
     *
     * @param id 项目ID
     * @return 是否成功
     */
    boolean endProject(Long id);

    /**
     * 获取教师负责的项目列表
     *
     * @param teacherId 教师ID
     * @return 项目列表
     */
    List<TrainingProjectVO> getProjectsByManager(Long teacherId);
}
