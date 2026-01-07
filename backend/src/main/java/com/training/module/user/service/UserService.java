package com.training.module.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.module.user.dto.*;
import com.training.module.user.vo.UserExtendedProfileVO;
import com.training.module.user.vo.UserProfileVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface UserService {

    /**
     * 获取当前用户资料
     *
     * @return 用户资料
     */
    UserProfileVO getCurrentUserProfile();

    /**
     * 更新当前用户资料
     *
     * @param updateDTO 更新请求
     * @return 更新后的用户资料
     */
    UserProfileVO updateCurrentUserProfile(UpdateUserProfileDTO updateDTO);

    /**
     * 根据ID获取用户资料
     *
     * @param userId 用户ID
     * @return 用户资料
     */
    UserProfileVO getUserProfileById(Long userId);

    /**
     * 修改密码
     *
     * @param changePasswordDTO 修改密码请求
     */
    void changePassword(ChangePasswordDTO changePasswordDTO);

    /**
     * 获取当前用户扩展信息
     *
     * @return 用户扩展信息
     */
    UserExtendedProfileVO getCurrentUserExtendedProfile();

    /**
     * 更新当前用户扩展信息
     *
     * @param updateDTO 更新请求
     * @return 更新后的用户扩展信息
     */
    UserExtendedProfileVO updateCurrentUserExtendedProfile(UpdateUserExtendedProfileDTO updateDTO);

    /**
     * 更新头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    String updateAvatar(MultipartFile file);

    // ==================== 管理员专用方法 ====================

    /**
     * 分页查询用户列表（管理员）
     *
     * @param queryDTO 查询条件
     * @return 用户分页数据
     */
    Page<UserProfileVO> getUserPage(UserQueryDTO queryDTO);

    /**
     * 创建用户（管理员）
     *
     * @param createDTO 创建请求
     * @return 用户ID
     */
    Long createUser(CreateUserDTO createDTO);

    /**
     * 更新用户信息（管理员）
     *
     * @param userId 用户ID
     * @param updateDTO 更新请求
     * @return 更新后的用户资料
     */
    UserProfileVO updateUser(Long userId, UpdateUserDTO updateDTO);

    /**
     * 删除用户（管理员）
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 批量删除用户（管理员）
     *
     * @param userIds 用户ID列表
     */
    void batchDeleteUsers(java.util.List<Long> userIds);

    /**
     * 重置用户密码（管理员）
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 检查员工编号是否可用（管理员）
     *
     * @param employeeNo 员工编号
     * @param excludeUserId 需要排除的用户ID（编辑时传入当前用户ID）
     * @return true 可用，false 已被使用
     */
    boolean checkEmployeeNo(String employeeNo, Long excludeUserId);
}
