package com.training.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.common.exception.BusinessException;
import com.training.module.news.mapper.NewsMapper;
import com.training.module.system.entity.SystemConfig;
import com.training.module.system.mapper.SystemConfigMapper;
import com.training.module.system.service.SystemConfigService;
import com.training.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务实现类
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Slf4j
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NewsMapper newsMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getConfigByGroup(String configGroup) {
        List<SystemConfig> configs = systemConfigMapper.selectByGroup(configGroup);
        Map<String, Object> result = new HashMap<>();

        for (SystemConfig config : configs) {
            result.put(config.getConfigKey(), convertValue(config.getConfigValue(), config.getConfigType()));
        }

        return result;
    }

    @Override
    public Map<String, Map<String, Object>> getAllConfig() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        result.put("basic", getConfigByGroup("basic"));
        result.put("upload", getConfigByGroup("upload"));
        result.put("security", getConfigByGroup("security"));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfigByGroup(String configGroup, Map<String, Object> config) {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            SystemConfig existConfig = systemConfigMapper.selectByGroupAndKey(configGroup, key);

            if (existConfig != null) {
                // 更新现有配置
                String strValue = convertToString(value);
                String configType = determineType(value);

                LambdaUpdateWrapper<SystemConfig> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SystemConfig::getId, existConfig.getId())
                        .set(SystemConfig::getConfigValue, strValue)
                        .set(SystemConfig::getConfigType, configType);

                systemConfigMapper.update(null, updateWrapper);
                log.info("更新系统配置: group={}, key={}, value={}", configGroup, key, strValue);
            } else {
                // 新增配置
                SystemConfig newConfig = new SystemConfig();
                newConfig.setConfigGroup(configGroup);
                newConfig.setConfigKey(key);
                newConfig.setConfigValue(convertToString(value));
                newConfig.setConfigType(determineType(value));
                newConfig.setSortOrder(0);

                systemConfigMapper.insert(newConfig);
                log.info("新增系统配置: group={}, key={}", configGroup, key);
            }
        }
    }

    @Override
    public Object getConfigValue(String configGroup, String configKey) {
        SystemConfig config = systemConfigMapper.selectByGroupAndKey(configGroup, configKey);
        if (config == null) {
            return null;
        }
        return convertValue(config.getConfigValue(), config.getConfigType());
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 统计用户数
        LambdaQueryWrapper<com.training.module.user.entity.User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(com.training.module.user.entity.User::getIsDeleted, 0);
        Long totalUsers = userMapper.selectCount(userWrapper);
        stats.put("totalUsers", totalUsers);

        // 统计新闻数
        LambdaQueryWrapper<com.training.module.news.entity.News> newsWrapper = new LambdaQueryWrapper<>();
        newsWrapper.eq(com.training.module.news.entity.News::getIsDeleted, 0);
        Long totalNews = newsMapper.selectCount(newsWrapper);
        stats.put("totalNews", totalNews);

        // 模拟访问统计（实际项目中可以从 Redis 或日志系统获取）
        stats.put("todayVisits", 0);
        stats.put("totalVisits", 0);

        return stats;
    }

    /**
     * 将字符串值根据类型转换为对应的 Java 类型
     */
    private Object convertValue(String value, String type) {
        if (value == null) {
            return null;
        }

        try {
            switch (type) {
                case "number":
                    if (value.contains(".")) {
                        return Double.parseDouble(value);
                    }
                    return Long.parseLong(value);
                case "boolean":
                    return Boolean.parseBoolean(value);
                case "json":
                    return objectMapper.readValue(value, Object.class);
                default:
                    return value;
            }
        } catch (Exception e) {
            log.warn("配置值转换失败: value={}, type={}", value, type);
            return value;
        }
    }

    /**
     * 将对象转换为字符串存储
     */
    private String convertToString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        if (value instanceof List || value instanceof Map) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new BusinessException("配置值序列化失败");
            }
        }

        return String.valueOf(value);
    }

    /**
     * 根据值类型判断配置类型
     */
    private String determineType(Object value) {
        if (value instanceof Number) {
            return "number";
        }
        if (value instanceof Boolean) {
            return "boolean";
        }
        if (value instanceof List || value instanceof Map) {
            return "json";
        }
        return "string";
    }
}
