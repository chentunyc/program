package com.training.module.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.resource.entity.ResourceDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文档资源Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface ResourceDocumentMapper extends BaseMapper<ResourceDocument> {

    /**
     * 根据资源ID查询文档资源详情
     *
     * @param resourceId 资源ID
     * @return 文档资源详情
     */
    @Select("SELECT * FROM t_resource_document WHERE resource_id = #{resourceId} AND is_deleted = 0")
    ResourceDocument selectByResourceId(@Param("resourceId") Long resourceId);
}
