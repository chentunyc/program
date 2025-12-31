package com.training.module.auth.service;

import com.training.module.auth.dto.LoginRequest;
import com.training.module.auth.dto.LoginUser;
import com.training.module.auth.vo.LoginVO;

/**
 * 认证服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param loginRequest 登录请求
     * @return 登录信息
     */
    LoginVO login(LoginRequest loginRequest);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    LoginUser getUserInfo();

    /**
     * 刷新Token
     *
     * @return 新Token
     */
    String refreshToken();
}
