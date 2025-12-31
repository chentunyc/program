package com.training.module.user.service;

import com.training.module.user.dto.ChangePasswordDTO;
import com.training.module.user.dto.UpdateUserExtendedProfileDTO;
import com.training.module.user.dto.UpdateUserProfileDTO;
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
}
