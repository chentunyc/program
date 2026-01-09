package com.training.module.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.base.PageResult;
import com.training.common.exception.BusinessException;
import com.training.module.training.dto.TrainingProjectCreateDTO;
import com.training.module.training.dto.TrainingProjectQueryDTO;
import com.training.module.training.dto.TrainingProjectUpdateDTO;
import com.training.module.training.entity.TrainingProject;
import com.training.module.training.mapper.TrainingProjectMapper;
import com.training.module.training.mapper.TrainingTaskMapper;
import com.training.module.training.service.TrainingProjectService;
import com.training.module.training.vo.TrainingProjectVO;
import com.training.module.user.entity.User;
import com.training.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实训项目服务实现
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class TrainingProjectServiceImpl implements TrainingProjectService {

    @Autowired
    private TrainingProjectMapper projectMapper;

    @Autowired
    private TrainingTaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<TrainingProjectVO> getProjectPage(TrainingProjectQueryDTO queryDTO) {
        Page<TrainingProject> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<TrainingProject> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(TrainingProject::getProjectName, queryDTO.getKeyword())
                    .or()
                    .like(TrainingProject::getProjectCode, queryDTO.getKeyword()));
        }

        // 条件过滤
        if (StringUtils.hasText(queryDTO.getCategory())) {
            wrapper.eq(TrainingProject::getCategory, queryDTO.getCategory());
        }
        if (queryDTO.getDifficulty() != null) {
            wrapper.eq(TrainingProject::getDifficulty, queryDTO.getDifficulty());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(TrainingProject::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getManagerId() != null) {
            wrapper.eq(TrainingProject::getManagerId, queryDTO.getManagerId());
        }

        wrapper.orderByDesc(TrainingProject::getCreateTime);

        Page<TrainingProject> resultPage = projectMapper.selectPage(page, wrapper);

        List<TrainingProjectVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, resultPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public TrainingProjectVO getProjectById(Long id) {
        TrainingProject project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        return convertToVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(TrainingProjectCreateDTO createDTO, Long userId) {
        TrainingProject project = new TrainingProject();
        BeanUtils.copyProperties(createDTO, project);

        // 生成项目编码
        String projectCode = generateProjectCode();
        project.setProjectCode(projectCode);

        // 设置负责人为当前用户
        project.setManagerId(userId);
        project.setStatus(0); // 未开始
        project.setMemberCount(0);
        project.setTotalTasks(0);
        project.setCreateBy(userId);
        project.setUpdateBy(userId);

        projectMapper.insert(project);
        log.info("创建实训项目成功: id={}, code={}", project.getId(), projectCode);

        return project.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProject(TrainingProjectUpdateDTO updateDTO, Long userId) {
        TrainingProject existing = projectMapper.selectById(updateDTO.getId());
        if (existing == null) {
            throw new BusinessException("项目不存在");
        }

        TrainingProject project = new TrainingProject();
        BeanUtils.copyProperties(updateDTO, project);
        project.setUpdateBy(userId);

        int rows = projectMapper.updateById(project);
        log.info("更新实训项目: id={}, rows={}", updateDTO.getId(), rows);

        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProject(Long id) {
        // 检查是否有任务
        Integer taskCount = taskMapper.countTasksByProjectId(id);
        if (taskCount > 0) {
            throw new BusinessException("该项目下存在任务，无法删除");
        }

        int rows = projectMapper.deleteById(id);
        log.info("删除实训项目: id={}, rows={}", id, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteProject(Long[] ids) {
        for (Long id : ids) {
            Integer taskCount = taskMapper.countTasksByProjectId(id);
            if (taskCount > 0) {
                throw new BusinessException("项目ID=" + id + "下存在任务，无法删除");
            }
        }

        int rows = projectMapper.deleteBatchIds(Arrays.asList(ids));
        log.info("批量删除实训项目: ids={}, rows={}", ids, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startProject(Long id) {
        TrainingProject project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (project.getStatus() != 0) {
            throw new BusinessException("只有未开始的项目才能开始");
        }

        // 检查权重是否为100
        BigDecimal totalWeight = taskMapper.sumWeightByProjectId(id);
        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new BusinessException("任务权重总和必须为100分，当前为" + totalWeight + "分");
        }

        TrainingProject update = new TrainingProject();
        update.setId(id);
        update.setStatus(1);
        if (project.getStartTime() == null) {
            update.setStartTime(LocalDateTime.now());
        }

        int rows = projectMapper.updateById(update);
        log.info("开始实训项目: id={}", id);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean endProject(Long id) {
        TrainingProject project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (project.getStatus() != 1) {
            throw new BusinessException("只有进行中的项目才能结束");
        }

        TrainingProject update = new TrainingProject();
        update.setId(id);
        update.setStatus(2);
        update.setEndTime(LocalDateTime.now());

        int rows = projectMapper.updateById(update);
        log.info("结束实训项目: id={}", id);
        return rows > 0;
    }

    @Override
    public List<TrainingProjectVO> getProjectsByManager(Long teacherId) {
        LambdaQueryWrapper<TrainingProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingProject::getManagerId, teacherId)
                .orderByDesc(TrainingProject::getCreateTime);

        List<TrainingProject> projects = projectMapper.selectList(wrapper);
        return projects.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 生成项目编码
     */
    private String generateProjectCode() {
        String prefix = "PRJ" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String maxCode = projectMapper.selectMaxProjectCode(prefix);

        int seq = 1;
        if (maxCode != null && maxCode.length() > prefix.length()) {
            try {
                seq = Integer.parseInt(maxCode.substring(prefix.length())) + 1;
            } catch (NumberFormatException e) {
                // 忽略
            }
        }

        return prefix + String.format("%04d", seq);
    }

    /**
     * 转换为VO
     */
    private TrainingProjectVO convertToVO(TrainingProject project) {
        TrainingProjectVO vo = new TrainingProjectVO();
        BeanUtils.copyProperties(project, vo);

        // 获取负责人姓名
        if (project.getManagerId() != null) {
            User manager = userMapper.selectById(project.getManagerId());
            if (manager != null) {
                vo.setManagerName(manager.getRealName());
            }
        }

        // 计算权重总和
        BigDecimal totalWeight = taskMapper.sumWeightByProjectId(project.getId());
        vo.setTotalWeight(totalWeight);

        return vo;
    }
}
