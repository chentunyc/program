package com.training.module.system.service;

import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author Training Team
 * @since 2025-01-01
 */
public interface SystemConfigService {

    /**
     * 获取指定组的所有配置
     * @param configGroup 配置组
     * @return 配置键值对
     */
    Map<String, Object> getConfigByGroup(String configGroup);

    /**
     * 获取所有配置（按组分类）
     * @return 所有配置
     */
    Map<String, Map<String, Object>> getAllConfig();

    /**
     * 保存指定组的配置
     * @param configGroup 配置组
     * @param config 配置键值对
     */
    void saveConfigByGroup(String configGroup, Map<String, Object> config);

    /**
     * 获取单个配置值
     * @param configGroup 配置组
     * @param configKey 配置键
     * @return 配置值
     */
    Object getConfigValue(String configGroup, String configKey);

    /**
     * 获取系统统计信息
     * @return 统计信息
     */
    Map<String, Object> getStatistics();
}
