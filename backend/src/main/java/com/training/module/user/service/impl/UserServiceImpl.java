package com.training.module.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.training.common.exception.BusinessException;
import com.training.common.utils.SecurityUtils;
import com.training.module.user.dto.ChangePasswordDTO;
import com.training.module.user.dto.UpdateUserExtendedProfileDTO;
import com.training.module.user.dto.UpdateUserProfileDTO;
import com.training.module.user.entity.User;
import com.training.module.user.entity.UserProfile;
import com.training.module.user.mapper.UserMapper;
import com.training.module.user.mapper.UserProfileMapper;
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
import java.util.List;
import java.util.UUID;

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
