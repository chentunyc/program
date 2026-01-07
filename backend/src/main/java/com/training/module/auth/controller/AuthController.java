package com.training.module.auth.controller;

import com.training.common.base.Result;
import com.training.module.auth.dto.LoginRequest;
import com.training.module.auth.dto.LoginUser;
import com.training.module.auth.dto.RegisterRequest;
import com.training.module.auth.service.AuthService;
import com.training.module.auth.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理登录、登出、注册、获取用户信息等请求
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "认证管理", description = "用户登录、登出、注册、获取用户信息")
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("用户登录: {}", loginRequest.getUsername());
        LoginVO loginVO = authService.login(loginRequest);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(@Validated @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册: {}, 角色: {}", registerRequest.getUsername(), registerRequest.getRoleCode());
        Long userId = authService.register(registerRequest);
        return Result.success("注册成功", userId);
    }

    /**
     * 检查用户名是否可用
     */
    @Operation(summary = "检查用户名是否可用")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean available = authService.checkUsername(username);
        return Result.success(available ? "用户名可用" : "用户名已存在", available);
    }

    /**
     * 登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<?> logout() {
        log.info("用户登出");
        authService.logout();
        return Result.success("登出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user-info")
    public Result<LoginUser> getUserInfo() {
        LoginUser loginUser = authService.getUserInfo();
        return Result.success(loginUser);
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh-token")
    public Result<String> refreshToken() {
        String newToken = authService.refreshToken();
        return Result.success("Token刷新成功", newToken);
    }
}
