package com.training.module.system.controller;

import com.training.common.base.Result;
import com.training.module.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置控制器
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Slf4j
@Tag(name = "系统配置", description = "系统配置的查询和修改")
@RestController
@RequestMapping("/v1/system/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取所有系统配置
     */
    @Operation(summary = "获取所有系统配置")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public Result<Map<String, Map<String, Object>>> getAllConfig() {
        log.info("获取所有系统配置");
        Map<String, Map<String, Object>> config = systemConfigService.getAllConfig();
        return Result.success(config);
    }

    /**
     * 获取指定组的配置
     */
    @Operation(summary = "获取指定组的配置")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{group}")
    public Result<Map<String, Object>> getConfigByGroup(@PathVariable("group") String group) {
        log.info("获取配置组: {}", group);
        Map<String, Object> config = systemConfigService.getConfigByGroup(group);
        return Result.success(config);
    }

    /**
     * 保存指定组的配置
     */
    @Operation(summary = "保存指定组的配置")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{group}")
    public Result<?> saveConfigByGroup(@PathVariable("group") String group,
                                       @RequestBody Map<String, Object> config) {
        log.info("保存配置组: {}, 配置项数: {}", group, config.size());
        systemConfigService.saveConfigByGroup(group, config);
        return Result.success("配置保存成功");
    }

    /**
     * 获取系统统计信息
     */
    @Operation(summary = "获取系统统计信息")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        log.info("获取系统统计信息");
        Map<String, Object> statistics = systemConfigService.getStatistics();
        return Result.success(statistics);
    }
}
