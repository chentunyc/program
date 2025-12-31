package com.training.common.utils;

import com.training.common.exception.BusinessException;
import com.training.module.auth.dto.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security工具类
 * 用于获取当前登录用户信息
 *
 * @author Training Team
 * @since 2024-01-01
 */
public class SecurityUtils {

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BusinessException("获取用户信息失败");
        }
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        try {
            return getLoginUser().getUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否为管理员
     */
    public static boolean isAdmin() {
        try {
            return getLoginUser().isAdmin();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否为教师
     */
    public static boolean isTeacher() {
        try {
            return getLoginUser().isTeacher();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否为学生
     */
    public static boolean isStudent() {
        try {
            return getLoginUser().isStudent();
        } catch (Exception e) {
            return false;
        }
    }
}
