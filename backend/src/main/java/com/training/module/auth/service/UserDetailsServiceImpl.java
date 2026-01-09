package com.training.module.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.training.common.exception.BusinessException;
import com.training.module.auth.dto.LoginUser;
import com.training.module.user.entity.User;
import com.training.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户详情服务实现
 * 实现Spring Security的UserDetailsService接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名加载用户信息
     *
     * @param username 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.error("登录用户:{} 不存在", username);
            throw new UsernameNotFoundException("用户不存在");
        }

        // 查询用户角色
        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        user.setRoles(roles);

        // 权限列表设置为空（当前系统仅基于角色进行权限控制）
        user.setPermissions(java.util.Collections.emptyList());

        // 构造LoginUser
        return new LoginUser(user);
    }
}
