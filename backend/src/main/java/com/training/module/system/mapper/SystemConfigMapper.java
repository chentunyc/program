package com.training.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.system.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置 Mapper 接口
 *
 * @author Training Team
 * @since 2025-01-01
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置组查询配置列表
     */
    @Select("SELECT * FROM t_system_config WHERE config_group = #{configGroup} AND is_deleted = 0 ORDER BY sort_order")
    List<SystemConfig> selectByGroup(@Param("configGroup") String configGroup);

    /**
     * 根据配置组和键查询配置
     */
    @Select("SELECT * FROM t_system_config WHERE config_group = #{configGroup} AND config_key = #{configKey} AND is_deleted = 0")
    SystemConfig selectByGroupAndKey(@Param("configGroup") String configGroup, @Param("configKey") String configKey);
}
