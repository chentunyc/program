package com.training.module.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.training.common.enums.RoleEnum;
import com.training.module.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户信息
 * 实现Spring Security的UserDetails接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态:0-禁用,1-正常
     */
    private Integer status;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * Token
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP
     */
    private String ipaddr;

    /**
     * 用户对象
     */
    @JsonIgnore
    private User user;

    /**
     * 从User对象构造LoginUser
     */
    public LoginUser(User user) {
        this.user = user;
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.realName = user.getRealName();
        this.avatar = user.getAvatar();
        this.status = user.getStatus();
        this.roles = user.getRoles();
        this.permissions = user.getPermissions();
    }

    /**
     * 获取权限列表
     * 包含角色和权限
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        java.util.Set<GrantedAuthority> authorities = new java.util.HashSet<>();

        // 添加角色（角色也作为权限）
        if (roles != null && !roles.isEmpty()) {
            authorities.addAll(roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }

        // 添加权限
        if (permissions != null && !permissions.isEmpty()) {
            authorities.addAll(permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否未过期
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否可用
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return status != null && status == 1;
    }

    /**
     * 是否为管理员
     */
    public boolean isAdmin() {
        return roles != null && roles.contains(RoleEnum.ADMIN.getCode());
    }

    /**
     * 是否为教师
     */
    public boolean isTeacher() {
        return roles != null && roles.contains(RoleEnum.TEACHER.getCode());
    }

    /**
     * 是否为学生
     */
    public boolean isStudent() {
        return roles != null && roles.contains(RoleEnum.STUDENT.getCode());
    }

    /**
     * 是否为访客
     */
    public boolean isGuest() {
        return roles != null && roles.contains(RoleEnum.GUEST.getCode());
    }

    /**
     * 是否为资料管理员
     */
    public boolean isDataAdmin() {
        return roles != null && roles.contains(RoleEnum.DATA_ADMIN.getCode());
    }
}
