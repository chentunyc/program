package com.training.module.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.base.Result;
import com.training.module.user.dto.*;
import com.training.module.user.service.UserService;
import com.training.module.user.vo.UserExtendedProfileVO;
import com.training.module.user.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Validated @RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("用户修改密码");
        userService.changePassword(changePasswordDTO);
        return Result.successMsg("密码修改成功");
    }

    /**
     * 获取当前用户扩展信息
     */
    @Operation(summary = "获取当前用户扩展信息")
    @GetMapping("/extended-profile")
    public Result<UserExtendedProfileVO> getCurrentUserExtendedProfile() {
        log.info("获取当前用户扩展信息");
        UserExtendedProfileVO profile = userService.getCurrentUserExtendedProfile();
        return Result.success(profile);
    }

    /**
     * 更新当前用户扩展信息
     */
    @Operation(summary = "更新当前用户扩展信息")
    @PutMapping("/extended-profile")
    public Result<UserExtendedProfileVO> updateCurrentUserExtendedProfile(
            @Validated @RequestBody UpdateUserExtendedProfileDTO updateDTO) {
        log.info("更新当前用户扩展信息: {}", updateDTO);
        UserExtendedProfileVO profile = userService.updateCurrentUserExtendedProfile(updateDTO);
        return Result.success("信息更新成功", profile);
    }

    /**
     * 上传头像
     */
    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("用户上传头像");
        String avatarUrl = userService.updateAvatar(file);
        return Result.success("头像上传成功", avatarUrl);
    }

    // ==================== 管理员专用接口 ====================

    /**
     * 分页查询用户列表（管理员）
     */
    @Operation(summary = "分页查询用户列表")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/page")
    public Result<Page<UserProfileVO>> getUserPage(@Validated UserQueryDTO queryDTO) {
        log.info("管理员查询用户列表: {}", queryDTO);
        Page<UserProfileVO> page = userService.getUserPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 创建用户（管理员）
     */
    @Operation(summary = "创建用户")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin")
    public Result<Long> createUser(@Validated @RequestBody CreateUserDTO createDTO) {
        log.info("管理员创建用户: {}", createDTO.getUsername());
        Long userId = userService.createUser(createDTO);
        return Result.success("用户创建成功", userId);
    }

    /**
     * 更新用户信息（管理员）
     */
    @Operation(summary = "更新用户信息")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/{userId}")
    public Result<UserProfileVO> updateUser(
            @PathVariable Long userId,
            @Validated @RequestBody UpdateUserDTO updateDTO) {
        log.info("管理员更新用户信息: userId={}", userId);
        UserProfileVO profile = userService.updateUser(userId, updateDTO);
        return Result.success("用户信息更新成功", profile);
    }

    /**
     * 删除用户（管理员）
     */
    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        log.info("管理员删除用户: userId={}", userId);
        userService.deleteUser(userId);
        return Result.successMsg("用户删除成功");
    }

    /**
     * 批量删除用户（管理员）
     */
    @Operation(summary = "批量删除用户")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/batch")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> userIds) {
        log.info("管理员批量删除用户: count={}", userIds.size());
        userService.batchDeleteUsers(userIds);
        return Result.successMsg("用户批量删除成功");
    }

    /**
     * 重置用户密码（管理员）
     */
    @Operation(summary = "重置用户密码")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/{userId}/reset-password")
    public Result<Void> resetPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        log.info("管理员重置用户密码: userId={}", userId);
        userService.resetPassword(userId, newPassword);
        return Result.successMsg("密码重置成功");
    }
}
