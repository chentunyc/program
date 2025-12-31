package com.training.common.config;

import cn.hutool.core.util.StrUtil;
import com.training.common.constant.CommonConstant;
import com.training.common.utils.JwtUtils;
import com.training.common.utils.RedisUtils;
import com.training.module.auth.dto.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * 在每个请求中验证Token并设置SecurityContext
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 过滤器核心逻辑
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 从请求头获取Token
        String token = getTokenFromRequest(request);

        // 如果Token存在且有效
        if (StrUtil.isNotBlank(token) && jwtUtils.validateToken(token)) {
            // 从Redis获取用户信息
            String redisKey = CommonConstant.LOGIN_USER_KEY + token;
            LoginUser loginUser = (LoginUser) redisUtils.get(redisKey);

            if (loginUser != null) {
                // 刷新Token过期时间(活动续期)
                redisUtils.expire(redisKey, jwtUtils.getExpiration());

                // 创建认证对象
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置到SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                log.debug("用户 {} 认证成功", loginUser.getUsername());
            } else {
                log.warn("Token有效但Redis中无用户信息: {}", token);
            }
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从Header获取Token
        String bearerToken = request.getHeader(jwtUtils.getHeader());

        // 去除Bearer前缀
        if (StrUtil.isNotBlank(bearerToken)) {
            return jwtUtils.extractToken(bearerToken);
        }

        return null;
    }
}
