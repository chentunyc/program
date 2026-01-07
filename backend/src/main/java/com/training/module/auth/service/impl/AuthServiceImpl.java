package com.training.module.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.training.common.constant.CommonConstant;
import com.training.common.exception.BusinessException;
import com.training.common.utils.JwtUtils;
import com.training.common.utils.RedisUtils;
import com.training.common.utils.SecurityUtils;
import com.training.module.auth.dto.LoginRequest;
import com.training.module.auth.dto.LoginUser;
import com.training.module.auth.dto.RegisterRequest;
import com.training.module.auth.service.AuthService;
import com.training.module.auth.vo.LoginVO;
import com.training.module.role.mapper.RoleMapper;
import com.training.module.user.entity.User;
import com.training.module.user.entity.UserRole;
import com.training.module.user.mapper.UserMapper;
import com.training.module.user.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 * 遵循单一职责原则,专注于认证业务逻辑
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 登录
     */
    @Override
    public LoginVO login(LoginRequest loginRequest) {
        // 用户认证
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            log.error("用户认证失败: {}", e.getMessage());
            throw new BusinessException("用户名或密码错误");
        }

        // 获取认证用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 生成Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());
        String token = jwtUtils.generateToken(claims);

        // 设置登录信息
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + jwtUtils.getExpiration() * 1000);
        loginUser.setIpaddr(getRequestIp());

        // 将用户信息存入Redis
        String redisKey = CommonConstant.LOGIN_USER_KEY + token;
        redisUtils.set(redisKey, loginUser, jwtUtils.getExpiration());

        // 更新用户最后登录时间和IP
        updateUserLoginInfo(loginUser.getUserId(), getRequestIp());

        // 构造返回对象
        return new LoginVO(
                token,
                loginUser.getUserId(),
                loginUser.getUsername(),
                loginUser.getRealName(),
                loginUser.getAvatar(),
                loginUser.getRoles(),
                loginUser.getPermissions()
        );
    }

    /**
     * 登出
     */
    @Override
    public void logout() {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && StrUtil.isNotBlank(loginUser.getToken())) {
                // 从Redis删除用户信息
                String redisKey = CommonConstant.LOGIN_USER_KEY + loginUser.getToken();
                redisUtils.del(redisKey);
            }
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public LoginUser getUserInfo() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 刷新Token
     */
    @Override
    public String refreshToken() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("用户未登录");
        }

        // 生成新Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());
        String newToken = jwtUtils.generateToken(claims);

        // 删除旧Token
        String oldRedisKey = CommonConstant.LOGIN_USER_KEY + loginUser.getToken();
        redisUtils.del(oldRedisKey);

        // 更新用户信息
        loginUser.setToken(newToken);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + jwtUtils.getExpiration() * 1000);

        // 存储新Token
        String newRedisKey = CommonConstant.LOGIN_USER_KEY + newToken;
        redisUtils.set(newRedisKey, loginUser, jwtUtils.getExpiration());

        return newToken;
    }

    /**
     * 注册新用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterRequest request) {
        // 验证手机号和邮箱至少填写一个
        if (StrUtil.isBlank(request.getPhone()) && StrUtil.isBlank(request.getEmail())) {
            throw new BusinessException("手机号和邮箱至少填写一个");
        }

        // 检查用户名是否已存在
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已被使用
        if (StrUtil.isNotBlank(request.getPhone()) && userMapper.countByPhone(request.getPhone()) > 0) {
            throw new BusinessException("手机号已被注册");
        }

        // 检查邮箱是否已被使用
        if (StrUtil.isNotBlank(request.getEmail()) && userMapper.countByEmail(request.getEmail()) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        // 验证角色是否存在
        Long roleId = roleMapper.selectIdByRoleCode(request.getRoleCode());
        if (roleId == null) {
            throw new BusinessException("角色不存在");
        }

        // 不允许自行注册管理员
        if ("ADMIN".equals(request.getRoleCode())) {
            throw new BusinessException("无法注册管理员账号");
        }

        // 生成员工编号
        String employeeNo = generateEmployeeNo(request.getRoleCode());

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmployeeNo(employeeNo);
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1); // 默认正常状态
        user.setGender(0); // 默认未知

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("注册失败");
        }

        // 分配角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(roleId);
        userRole.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(userRole);

        log.info("用户注册成功, userId: {}, username: {}, employeeNo: {}",
                user.getId(), user.getUsername(), employeeNo);

        return user.getId();
    }

    /**
     * 检查用户名是否可用
     */
    @Override
    public boolean checkUsername(String username) {
        return userMapper.countByUsername(username) == 0;
    }

    /**
     * 生成员工编号
     * 规则: 角色前缀 + 3位序号
     * A = ADMIN, T = TEACHER, S = STUDENT, G = GUEST, D = DATA_ADMIN
     */
    private String generateEmployeeNo(String roleCode) {
        // 获取角色前缀
        String prefix = getRolePrefix(roleCode);

        // 查询当前最大编号
        String maxNo = userMapper.selectMaxEmployeeNo(prefix);

        int nextNo = 1;
        if (StrUtil.isNotBlank(maxNo) && maxNo.length() > 1) {
            try {
                // 提取数字部分
                String numPart = maxNo.substring(1);
                nextNo = Integer.parseInt(numPart) + 1;
            } catch (NumberFormatException e) {
                log.warn("解析编号失败: {}", maxNo);
            }
        }

        // 格式化为3位数字
        return String.format("%s%03d", prefix, nextNo);
    }

    /**
     * 获取角色对应的编号前缀
     */
    private String getRolePrefix(String roleCode) {
        switch (roleCode) {
            case "ADMIN":
                return "A";
            case "TEACHER":
                return "T";
            case "STUDENT":
                return "S";
            case "GUEST":
                return "G";
            case "DATA_ADMIN":
                return "D";
            default:
                return "U"; // 未知角色默认前缀
        }
    }

    /**
     * 更新用户最后登录信息
     */
    private void updateUserLoginInfo(Long userId, String ipAddr) {
        try {
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, userId)
                    .set(User::getLastLoginTime, LocalDateTime.now())
                    .set(User::getLastLoginIp, ipAddr);
            userMapper.update(null, updateWrapper);
        } catch (Exception e) {
            log.error("更新用户登录信息失败: {}", e.getMessage());
        }
    }

    /**
     * 获取请求IP地址
     */
    private String getRequestIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                // 处理多个IP的情况,取第一个
                if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
                    ip = ip.split(",")[0];
                }
                return ip;
            }
        } catch (Exception e) {
            log.error("获取IP地址失败: {}", e.getMessage());
        }
        return "unknown";
    }
}
