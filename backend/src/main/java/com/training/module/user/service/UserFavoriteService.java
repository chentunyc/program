package com.training.module.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.training.module.user.dto.FavoriteDTO;
import com.training.module.user.dto.FavoriteQueryDTO;
import com.training.module.user.vo.FavoriteStatsVO;
import com.training.module.user.vo.FavoriteVO;

/**
 * 用户收藏服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface UserFavoriteService {

    /**
     * 添加收藏
     *
     * @param favoriteDTO 收藏信息
     * @return 收藏ID
     */
    Long addFavorite(FavoriteDTO favoriteDTO);

    /**
     * 取消收藏
     *
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    void removeFavorite(String resourceType, Long resourceId);

    /**
     * 检查是否已收藏
     *
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 是否已收藏
     */
    boolean isFavorited(String resourceType, Long resourceId);

    /**
     * 分页查询当前用户收藏
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<FavoriteVO> getFavoritePage(FavoriteQueryDTO queryDTO);

    /**
     * 获取当前用户收藏统计
     *
     * @return 收藏统计
     */
    FavoriteStatsVO getFavoriteStats();
}
