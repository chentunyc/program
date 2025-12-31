package com.training.module.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.training.common.exception.BusinessException;
import com.training.common.utils.SecurityUtils;
import com.training.module.user.dto.UpdateUserProfileDTO;
import com.training.module.user.entity.User;
import com.training.module.user.mapper.UserMapper;
import com.training.module.user.service.UserService;
import com.training.module.user.vo.UserProfileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 * 遵循单一职责原则,专注于用户资料管理业务逻辑
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取当前用户资料
     */
    @Override
    public UserProfileVO getCurrentUserProfile() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        return getUserProfileById(userId);
    }

    /**
     * 更新当前用户资料
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfileVO updateCurrentUserProfile(UpdateUserProfileDTO updateDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 构建更新条件 - 仅更新非空字段,遵循YAGNI原则
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId);

        boolean hasUpdate = false;

        if (StrUtil.isNotBlank(updateDTO.getRealName())) {
            updateWrapper.set(User::getRealName, updateDTO.getRealName());
            hasUpdate = true;
        }

        if (updateDTO.getGender() != null) {
            if (updateDTO.getGender() < 0 || updateDTO.getGender() > 2) {
                throw new BusinessException("性别参数不正确");
            }
            updateWrapper.set(User::getGender, updateDTO.getGender());
            hasUpdate = true;
        }

        if (StrUtil.isNotBlank(updateDTO.getPhone())) {
            updateWrapper.set(User::getPhone, updateDTO.getPhone());
            hasUpdate = true;
        }

        if (StrUtil.isNotBlank(updateDTO.getEmail())) {
            updateWrapper.set(User::getEmail, updateDTO.getEmail());
            hasUpdate = true;
        }

        if (!hasUpdate) {
            throw new BusinessException("没有需要更新的数据");
        }

        // 执行更新
        int result = userMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新用户资料失败");
        }

        log.info("用户资料更新成功, userId: {}", userId);

        // 返回更新后的用户资料
        return getUserProfileById(userId);
    }

    /**
     * 根据ID获取用户资料
     */
    @Override
    public UserProfileVO getUserProfileById(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 查询用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户角色
        List<String> roles = userMapper.selectRolesByUserId(userId);

        // 转换为VO对象 - 手动映射,遵循KISS原则
        return convertToVO(user, roles);
    }

    /**
     * 转换User实体为UserProfileVO
     * 职责单一,便于维护和测试
     */
    private UserProfileVO convertToVO(User user, List<String> roles) {
        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setStudentNo(user.getStudentNo());
        vo.setEmployeeNo(user.getEmployeeNo());
        vo.setGender(user.getGender());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setLastLoginIp(user.getLastLoginIp());
        vo.setRoles(roles);
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
