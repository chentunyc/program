package com.training.module.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.base.PageResult;
import com.training.common.exception.BusinessException;
import com.training.module.training.dto.TrainingTaskCreateDTO;
import com.training.module.training.dto.TrainingTaskQueryDTO;
import com.training.module.training.dto.TrainingTaskUpdateDTO;
import com.training.module.training.entity.TrainingProject;
import com.training.module.training.entity.TrainingTask;
import com.training.module.training.mapper.TrainingProjectMapper;
import com.training.module.training.mapper.TrainingTaskMapper;
import com.training.module.training.service.TrainingTaskService;
import com.training.module.training.vo.TrainingTaskVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实训任务服务实现
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class TrainingTaskServiceImpl implements TrainingTaskService {

    @Autowired
    private TrainingTaskMapper taskMapper;

    @Autowired
    private TrainingProjectMapper projectMapper;

    @Override
    public PageResult<TrainingTaskVO> getTaskPage(TrainingTaskQueryDTO queryDTO) {
        Page<TrainingTask> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<TrainingTask> wrapper = new LambdaQueryWrapper<>();

        // 项目ID过滤
        if (queryDTO.getProjectId() != null) {
            wrapper.eq(TrainingTask::getProjectId, queryDTO.getProjectId());
        }

        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(TrainingTask::getTaskName, queryDTO.getKeyword());
        }

        // 状态过滤
        if (queryDTO.getStatus() != null) {
            wrapper.eq(TrainingTask::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(TrainingTask::getSortOrder)
                .orderByDesc(TrainingTask::getCreateTime);

        Page<TrainingTask> resultPage = taskMapper.selectPage(page, wrapper);

        List<TrainingTaskVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, resultPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<TrainingTaskVO> getTasksByProjectId(Long projectId) {
        LambdaQueryWrapper<TrainingTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingTask::getProjectId, projectId)
                .orderByAsc(TrainingTask::getSortOrder);

        List<TrainingTask> tasks = taskMapper.selectList(wrapper);
        return tasks.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public TrainingTaskVO getTaskById(Long id) {
        TrainingTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return convertToVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(TrainingTaskCreateDTO createDTO, Long userId) {
        // 检查项目是否存在
        TrainingProject project = projectMapper.selectById(createDTO.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 检查项目状态，只有未开始的项目才能添加任务
        if (project.getStatus() != 0) {
            throw new BusinessException("只有未开始的项目才能添加任务");
        }

        TrainingTask task = new TrainingTask();
        BeanUtils.copyProperties(createDTO, task);

        // 如果没有指定排序序号，自动设置
        if (task.getSortOrder() == null || task.getSortOrder() == 0) {
            Integer maxSort = taskMapper.getMaxSortOrder(createDTO.getProjectId());
            task.setSortOrder(maxSort + 1);
        }

        task.setStatus(0); // 未发布
        task.setCreateBy(userId);
        task.setUpdateBy(userId);

        taskMapper.insert(task);

        // 更新项目的任务总数
        Integer taskCount = taskMapper.countTasksByProjectId(createDTO.getProjectId());
        projectMapper.updateTotalTasks(createDTO.getProjectId(), taskCount);

        log.info("创建实训任务成功: id={}, projectId={}", task.getId(), createDTO.getProjectId());

        return task.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTask(TrainingTaskUpdateDTO updateDTO, Long userId) {
        TrainingTask existing = taskMapper.selectById(updateDTO.getId());
        if (existing == null) {
            throw new BusinessException("任务不存在");
        }

        // 检查项目状态
        TrainingProject project = projectMapper.selectById(existing.getProjectId());
        if (project != null && project.getStatus() == 2) {
            throw new BusinessException("项目已结束，无法修改任务");
        }

        TrainingTask task = new TrainingTask();
        BeanUtils.copyProperties(updateDTO, task);
        task.setUpdateBy(userId);

        int rows = taskMapper.updateById(task);
        log.info("更新实训任务: id={}, rows={}", updateDTO.getId(), rows);

        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTask(Long id) {
        TrainingTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 检查项目状态
        TrainingProject project = projectMapper.selectById(task.getProjectId());
        if (project != null && project.getStatus() != 0) {
            throw new BusinessException("只有未开始的项目才能删除任务");
        }

        int rows = taskMapper.deleteById(id);

        // 更新项目的任务总数
        if (rows > 0 && task.getProjectId() != null) {
            Integer taskCount = taskMapper.countTasksByProjectId(task.getProjectId());
            projectMapper.updateTotalTasks(task.getProjectId(), taskCount);
        }

        log.info("删除实训任务: id={}, rows={}", id, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteTask(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }

        // 检查所有任务的项目状态
        for (Long id : ids) {
            TrainingTask task = taskMapper.selectById(id);
            if (task != null) {
                TrainingProject project = projectMapper.selectById(task.getProjectId());
                if (project != null && project.getStatus() != 0) {
                    throw new BusinessException("任务ID=" + id + "所属项目已开始或结束，无法删除");
                }
            }
        }

        int rows = taskMapper.deleteBatchIds(Arrays.asList(ids));

        // 更新相关项目的任务总数
        for (Long id : ids) {
            TrainingTask task = taskMapper.selectById(id);
            if (task != null && task.getProjectId() != null) {
                Integer taskCount = taskMapper.countTasksByProjectId(task.getProjectId());
                projectMapper.updateTotalTasks(task.getProjectId(), taskCount);
            }
        }

        log.info("批量删除实训任务: ids={}, rows={}", ids, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishTask(Long id) {
        TrainingTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        TrainingTask update = new TrainingTask();
        update.setId(id);
        update.setStatus(1);

        int rows = taskMapper.updateById(update);
        log.info("发布实训任务: id={}", id);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unpublishTask(Long id) {
        TrainingTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        TrainingTask update = new TrainingTask();
        update.setId(id);
        update.setStatus(0);

        int rows = taskMapper.updateById(update);
        log.info("取消发布实训任务: id={}", id);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskSort(Long id, Integer sortOrder) {
        TrainingTask update = new TrainingTask();
        update.setId(id);
        update.setSortOrder(sortOrder);

        int rows = taskMapper.updateById(update);
        log.info("调整任务排序: id={}, sortOrder={}", id, sortOrder);
        return rows > 0;
    }

    /**
     * 转换为VO
     */
    private TrainingTaskVO convertToVO(TrainingTask task) {
        TrainingTaskVO vo = new TrainingTaskVO();
        BeanUtils.copyProperties(task, vo);

        // 获取项目名称
        if (task.getProjectId() != null) {
            TrainingProject project = projectMapper.selectById(task.getProjectId());
            if (project != null) {
                vo.setProjectName(project.getProjectName());
            }
        }

        return vo;
    }
}
