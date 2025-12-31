package com.training.module.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.resource.entity.ResourceSimulation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 虚拟仿真资源Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface ResourceSimulationMapper extends BaseMapper<ResourceSimulation> {

    /**
     * 根据资源ID查询仿真资源详情
     *
     * @param resourceId 资源ID
     * @return 仿真资源详情
     */
    @Select("SELECT * FROM t_resource_simulation WHERE resource_id = #{resourceId} AND is_deleted = 0")
    ResourceSimulation selectByResourceId(@Param("resourceId") Long resourceId);
}
