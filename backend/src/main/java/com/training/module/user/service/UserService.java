package com.training.module.user.service;

import com.training.module.user.dto.UpdateUserProfileDTO;
import com.training.module.user.vo.UserProfileVO;

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
}
