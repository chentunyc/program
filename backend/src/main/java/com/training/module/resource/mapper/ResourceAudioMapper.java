package com.training.module.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.resource.entity.ResourceAudio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 音频资源Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface ResourceAudioMapper extends BaseMapper<ResourceAudio> {

    /**
     * 根据资源ID查询音频资源详情
     *
     * @param resourceId 资源ID
     * @return 音频资源详情
     */
    @Select("SELECT * FROM t_resource_audio WHERE resource_id = #{resourceId} AND is_deleted = 0")
    ResourceAudio selectByResourceId(@Param("resourceId") Long resourceId);
}
