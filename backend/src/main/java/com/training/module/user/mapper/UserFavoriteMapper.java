package com.training.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.module.user.entity.UserFavorite;
import com.training.module.user.vo.FavoriteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户收藏Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 分页查询用户收藏（带资源详情）
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @param resourceType 资源类型（可选）
     * @return 分页结果
     */
    IPage<FavoriteVO> selectFavoritePage(
            Page<FavoriteVO> page,
            @Param("userId") Long userId,
            @Param("resourceType") String resourceType
    );

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 收藏记录，不存在返回null
     */
    UserFavorite selectByUserAndResource(
            @Param("userId") Long userId,
            @Param("resourceType") String resourceType,
            @Param("resourceId") Long resourceId
    );

    /**
     * 统计用户收藏数量
     *
     * @param userId 用户ID
     * @param resourceType 资源类型（可选）
     * @return 收藏数量
     */
    Long countByUserIdAndType(
            @Param("userId") Long userId,
            @Param("resourceType") String resourceType
    );
}
