package com.training.module.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.exception.BusinessException;
import com.training.common.utils.SecurityUtils;
import com.training.module.role.mapper.RoleMapper;
import com.training.module.user.dto.*;
import com.training.module.user.entity.User;
import com.training.module.user.entity.UserProfile;
import com.training.module.user.entity.UserRole;
import com.training.module.user.mapper.UserMapper;
import com.training.module.user.mapper.UserProfileMapper;
import com.training.module.user.mapper.UserRoleMapper;
import com.training.module.user.service.UserService;
import com.training.module.user.vo.UserExtendedProfileVO;
import com.training.module.user.vo.UserProfileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.avatar-path:/avatars}")
    private String avatarPath;

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
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("当前密码不正确");
        }

        // 新密码不能与旧密码相同
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            throw new BusinessException("新密码不能与当前密码相同");
        }

        // 更新密码
        String encodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                .set(User::getPassword, encodedPassword);

        int result = userMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("密码修改失败");
        }

        log.info("用户密码修改成功, userId: {}", userId);
    }

    /**
     * 获取当前用户扩展信息
     */
    @Override
    public UserExtendedProfileVO getCurrentUserExtendedProfile() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 查询用户基本信息获取头像
        User user = userMapper.selectById(userId);

        // 查询扩展信息
        UserProfile profile = userProfileMapper.selectByUserId(userId);

        // 转换为VO
        UserExtendedProfileVO vo = new UserExtendedProfileVO();
        vo.setUserId(userId);
        vo.setAvatar(user != null ? user.getAvatar() : null);

        if (profile != null) {
            vo.setDepartment(profile.getDepartment());
            vo.setMajor(profile.getMajor());
            vo.setClassName(profile.getClassName());
            vo.setGrade(profile.getGrade());
            vo.setBirthDate(profile.getBirthDate() != null ? profile.getBirthDate().toString() : null);
            vo.setIdCard(profile.getIdCard());
            vo.setAddress(profile.getAddress());
            vo.setIntroduction(profile.getIntroduction());
        }

        return vo;
    }

    /**
     * 更新当前用户扩展信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserExtendedProfileVO updateCurrentUserExtendedProfile(UpdateUserExtendedProfileDTO updateDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 查询是否已有扩展信息
        UserProfile profile = userProfileMapper.selectByUserId(userId);

        if (profile == null) {
            // 创建新记录
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setCreateTime(LocalDateTime.now());
        }

        // 更新字段
        if (StrUtil.isNotBlank(updateDTO.getDepartment())) {
            profile.setDepartment(updateDTO.getDepartment());
        }
        if (StrUtil.isNotBlank(updateDTO.getMajor())) {
            profile.setMajor(updateDTO.getMajor());
        }
        if (StrUtil.isNotBlank(updateDTO.getClassName())) {
            profile.setClassName(updateDTO.getClassName());
        }
        if (StrUtil.isNotBlank(updateDTO.getGrade())) {
            profile.setGrade(updateDTO.getGrade());
        }
        if (StrUtil.isNotBlank(updateDTO.getBirthDate())) {
            profile.setBirthDate(LocalDate.parse(updateDTO.getBirthDate()));
        }
        if (StrUtil.isNotBlank(updateDTO.getIdCard())) {
            profile.setIdCard(updateDTO.getIdCard());
        }
        if (StrUtil.isNotBlank(updateDTO.getAddress())) {
            profile.setAddress(updateDTO.getAddress());
        }
        if (StrUtil.isNotBlank(updateDTO.getIntroduction())) {
            profile.setIntroduction(updateDTO.getIntroduction());
        }

        profile.setUpdateTime(LocalDateTime.now());

        // 保存或更新
        if (profile.getId() == null) {
            userProfileMapper.insert(profile);
            log.info("创建用户扩展信息, userId: {}", userId);
        } else {
            userProfileMapper.updateById(profile);
            log.info("更新用户扩展信息, userId: {}", userId);
        }

        return getCurrentUserExtendedProfile();
    }

    /**
     * 更新头像
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateAvatar(MultipartFile file) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }

        // 验证文件大小 (最大2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过2MB");
        }

        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 创建目录
            Path dirPath = Paths.get(uploadPath, avatarPath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 生成访问URL
            String avatarUrl = avatarPath + "/" + filename;

            // 更新用户头像
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, userId)
                    .set(User::getAvatar, avatarUrl);

            int result = userMapper.update(null, updateWrapper);
            if (result <= 0) {
                // 删除已上传的文件
                Files.deleteIfExists(filePath);
                throw new BusinessException("头像更新失败");
            }

            log.info("用户头像更新成功, userId: {}, avatar: {}", userId, avatarUrl);

            return avatarUrl;
        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusinessException("头像上传失败: " + e.getMessage());
        }
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

    // ==================== 管理员专用方法 ====================

    /**
     * 分页查询用户列表（管理员）
     */
    @Override
    public Page<UserProfileVO> getUserPage(UserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getEmployeeNo, keyword)
            );
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        // 排序
        queryWrapper.orderByDesc(User::getCreateTime);

        // 分页查询
        Page<User> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);

        // 转换为VO
        Page<UserProfileVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserProfileVO> voList = userPage.getRecords().stream()
                .map(user -> {
                    List<String> roles = userMapper.selectRolesByUserId(user.getId());
                    return convertToVO(user, roles);
                })
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 创建用户（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(CreateUserDTO createDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, createDTO.getUsername());
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(createDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setRealName(createDTO.getRealName());
        user.setEmployeeNo(createDTO.getEmployeeNo());
        user.setStatus(1); // 默认正常状态

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("创建用户失败");
        }

        // 分配角色
        if (createDTO.getRoleIds() != null && !createDTO.getRoleIds().isEmpty()) {
            saveUserRoles(user.getId(), createDTO.getRoleIds());
        }

        log.info("管理员创建用户成功, userId: {}, username: {}", user.getId(), user.getUsername());

        return user.getId();
    }

    /**
     * 更新用户信息（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfileVO updateUser(Long userId, UpdateUserDTO updateDTO) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId);

        boolean hasUpdate = false;

        if (StrUtil.isNotBlank(updateDTO.getRealName())) {
            updateWrapper.set(User::getRealName, updateDTO.getRealName());
            hasUpdate = true;
        }

        if (StrUtil.isNotBlank(updateDTO.getEmployeeNo())) {
            updateWrapper.set(User::getEmployeeNo, updateDTO.getEmployeeNo());
            hasUpdate = true;
        }

        if (updateDTO.getGender() != null) {
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

        if (updateDTO.getStatus() != null) {
            updateWrapper.set(User::getStatus, updateDTO.getStatus());
            hasUpdate = true;
        }

        if (hasUpdate) {
            int result = userMapper.update(null, updateWrapper);
            if (result <= 0) {
                throw new BusinessException("更新用户信息失败");
            }
        }

        // 更新角色
        if (updateDTO.getRoleIds() != null) {
            // 删除旧角色
            userRoleMapper.deleteByUserId(userId);
            // 添加新角色
            if (!updateDTO.getRoleIds().isEmpty()) {
                saveUserRoles(userId, updateDTO.getRoleIds());
            }
        }

        log.info("管理员更新用户信息成功, userId: {}", userId);

        return getUserProfileById(userId);
    }

    /**
     * 删除用户（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不能删除管理员自己
        Long currentUserId = SecurityUtils.getUserId();
        if (userId.equals(currentUserId)) {
            throw new BusinessException("不能删除当前登录用户");
        }

        // 逻辑删除用户
        int result = userMapper.deleteById(userId);
        if (result <= 0) {
            throw new BusinessException("删除用户失败");
        }

        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);

        log.info("管理员删除用户成功, userId: {}", userId);
    }

    /**
     * 批量删除用户（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("用户ID列表不能为空");
        }

        // 不能删除管理员自己
        Long currentUserId = SecurityUtils.getUserId();
        if (userIds.contains(currentUserId)) {
            throw new BusinessException("不能删除当前登录用户");
        }

        // 批量删除用户
        int result = userMapper.deleteBatchIds(userIds);
        if (result <= 0) {
            throw new BusinessException("批量删除用户失败");
        }

        // 删除用户角色关联
        userIds.forEach(userId -> userRoleMapper.deleteByUserId(userId));

        log.info("管理员批量删除用户成功, count: {}", result);
    }

    /**
     * 重置用户密码（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 更新密码
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                .set(User::getPassword, encodedPassword);

        int result = userMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("重置密码失败");
        }

        log.info("管理员重置用户密码成功, userId: {}", userId);
    }

    /**
     * 保存用户角色关联
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        List<UserRole> userRoles = new ArrayList<>();
        Long currentUserId = SecurityUtils.getUserId();

        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(LocalDateTime.now());
            userRole.setCreateBy(currentUserId);
            userRoles.add(userRole);
        }

        if (!userRoles.isEmpty()) {
            userRoleMapper.insertBatch(userRoles);
        }
    }
}
