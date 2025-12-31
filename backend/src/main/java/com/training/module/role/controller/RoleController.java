package com.training.module.role.controller;

import com.training.common.base.Result;
import com.training.module.role.entity.Role;
import com.training.module.role.mapper.RoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "角色管理", description = "角色查询接口")
@RestController
@RequestMapping("/v1/role")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 获取所有角色列表
     */
    @Operation(summary = "获取所有角色列表")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/list")
    public Result<List<Role>> getRoleList() {
        log.info("管理员查询角色列表");
        List<Role> roles = roleMapper.selectList(null);
        return Result.success(roles);
    }
}
