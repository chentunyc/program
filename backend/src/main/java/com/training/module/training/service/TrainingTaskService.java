package com.training.module.training.service;

import com.training.common.base.PageResult;
import com.training.module.training.dto.TrainingTaskCreateDTO;
import com.training.module.training.dto.TrainingTaskQueryDTO;
import com.training.module.training.dto.TrainingTaskUpdateDTO;
import com.training.module.training.vo.TrainingTaskVO;

import java.util.List;

/**
 * 实训任务服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface TrainingTaskService {

    /**
     * 分页查询任务列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<TrainingTaskVO> getTaskPage(TrainingTaskQueryDTO queryDTO);

    /**
     * 根据项目ID获取任务列表
     *
     * @param projectId 项目ID
     * @return 任务列表
     */
    List<TrainingTaskVO> getTasksByProjectId(Long projectId);

    /**
     * 根据ID获取任务详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    TrainingTaskVO getTaskById(Long id);

    /**
     * 创建任务
     *
     * @param createDTO 创建请求
     * @param userId 用户ID
     * @return 任务ID
     */
    Long createTask(TrainingTaskCreateDTO createDTO, Long userId);

    /**
     * 更新任务
     *
     * @param updateDTO 更新请求
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateTask(TrainingTaskUpdateDTO updateDTO, Long userId);

    /**
     * 删除任务
     *
     * @param id 任务ID
     * @return 是否成功
     */
    boolean deleteTask(Long id);

    /**
     * 批量删除任务
     *
     * @param ids 任务ID数组
     * @return 是否成功
     */
    boolean batchDeleteTask(Long[] ids);

    /**
     * 发布任务
     *
     * @param id 任务ID
     * @return 是否成功
     */
    boolean publishTask(Long id);

    /**
     * 取消发布任务
     *
     * @param id 任务ID
     * @return 是否成功
     */
    boolean unpublishTask(Long id);

    /**
     * 调整任务排序
     *
     * @param id 任务ID
     * @param sortOrder 新排序序号
     * @return 是否成功
     */
    boolean updateTaskSort(Long id, Integer sortOrder);
}
