package com.training.module.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.module.resource.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 资源Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 分页查询资源（带条件）
     *
     * @param page 分页对象
     * @param resourceType 资源类型
     * @param category 分类
     * @param keyword 关键词
     * @param status 状态
     * @return 分页结果
     */
    IPage<Resource> selectResourcePage(
            Page<Resource> page,
            @Param("resourceType") String resourceType,
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("status") Integer status
    );

    /**
     * 根据资源类型统计数量
     *
     * @param resourceType 资源类型
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_resource WHERE resource_type = #{resourceType} AND is_deleted = 0 AND status = 1")
    Long countByType(@Param("resourceType") String resourceType);

    /**
     * 获取热门资源
     *
     * @param limit 数量限制
     * @return 资源列表
     */
    @Select("SELECT * FROM t_resource WHERE is_deleted = 0 AND status = 1 ORDER BY view_count DESC LIMIT #{limit}")
    List<Resource> selectHotResources(@Param("limit") Integer limit);

    /**
     * 获取最新资源
     *
     * @param limit 数量限制
     * @return 资源列表
     */
    @Select("SELECT * FROM t_resource WHERE is_deleted = 0 AND status = 1 ORDER BY create_time DESC LIMIT #{limit}")
    List<Resource> selectLatestResources(@Param("limit") Integer limit);

    /**
     * 增加浏览次数
     *
     * @param id 资源ID
     * @return 影响行数
     */
    @Update("UPDATE t_resource SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加下载次数
     *
     * @param id 资源ID
     * @return 影响行数
     */
    @Update("UPDATE t_resource SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(@Param("id") Long id);
}
