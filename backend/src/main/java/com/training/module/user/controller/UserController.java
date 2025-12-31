package com.training.module.user.controller;

import com.training.common.base.Result;
import com.training.module.user.dto.UpdateUserProfileDTO;
import com.training.module.user.service.UserService;
import com.training.module.user.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 处理用户资料相关请求
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "用户管理", description = "用户资料查询和更新")
@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户资料
     */
    @Operation(summary = "获取当前用户资料")
    @GetMapping("/profile")
    public Result<UserProfileVO> getCurrentUserProfile() {
        log.info("获取当前用户资料");
        UserProfileVO profile = userService.getCurrentUserProfile();
        return Result.success(profile);
    }

    /**
     * 更新当前用户资料
     */
    @Operation(summary = "更新当前用户资料")
    @PutMapping("/profile")
    public Result<UserProfileVO> updateCurrentUserProfile(@Validated @RequestBody UpdateUserProfileDTO updateDTO) {
        log.info("更新当前用户资料: {}", updateDTO);
        UserProfileVO profile = userService.updateCurrentUserProfile(updateDTO);
        return Result.success("资料更新成功", profile);
    }

    /**
     * 根据ID获取用户资料
     */
    @Operation(summary = "根据ID获取用户资料")
    @GetMapping("/profile/{userId}")
    public Result<UserProfileVO> getUserProfileById(@PathVariable Long userId) {
        log.info("获取用户资料, userId: {}", userId);
        UserProfileVO profile = userService.getUserProfileById(userId);
        return Result.success(profile);
    }
}
