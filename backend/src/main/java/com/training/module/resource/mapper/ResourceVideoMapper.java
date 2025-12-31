package com.training.module.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.resource.entity.ResourceVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 视频资源Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface ResourceVideoMapper extends BaseMapper<ResourceVideo> {

    /**
     * 根据资源ID查询视频资源详情
     *
     * @param resourceId 资源ID
     * @return 视频资源详情
     */
    @Select("SELECT * FROM t_resource_video WHERE resource_id = #{resourceId} AND is_deleted = 0")
    ResourceVideo selectByResourceId(@Param("resourceId") Long resourceId);
}
