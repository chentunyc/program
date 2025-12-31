package com.training.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.exception.BusinessException;
import com.training.common.utils.SecurityUtils;
import com.training.module.user.dto.FavoriteDTO;
import com.training.module.user.dto.FavoriteQueryDTO;
import com.training.module.user.entity.UserFavorite;
import com.training.module.user.mapper.UserFavoriteMapper;
import com.training.module.user.service.UserFavoriteService;
import com.training.module.user.vo.FavoriteStatsVO;
import com.training.module.user.vo.FavoriteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户收藏服务实现类
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addFavorite(FavoriteDTO favoriteDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 检查是否已收藏
        UserFavorite existing = userFavoriteMapper.selectByUserAndResource(
                userId, favoriteDTO.getResourceType(), favoriteDTO.getResourceId());
        if (existing != null) {
            throw new BusinessException("您已经收藏过该资源");
        }

        // 创建收藏记录
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setResourceType(favoriteDTO.getResourceType());
        favorite.setResourceId(favoriteDTO.getResourceId());
        favorite.setCreateTime(LocalDateTime.now());

        userFavoriteMapper.insert(favorite);
        log.info("用户 {} 收藏了资源 {} - {}", userId, favoriteDTO.getResourceType(), favoriteDTO.getResourceId());

        return favorite.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(String resourceType, Long resourceId) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 删除收藏记录
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getResourceType, resourceType)
                .eq(UserFavorite::getResourceId, resourceId);

        int deleted = userFavoriteMapper.delete(wrapper);
        if (deleted == 0) {
            throw new BusinessException("收藏记录不存在");
        }

        log.info("用户 {} 取消收藏资源 {} - {}", userId, resourceType, resourceId);
    }

    @Override
    public boolean isFavorited(String resourceType, Long resourceId) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }

        UserFavorite favorite = userFavoriteMapper.selectByUserAndResource(userId, resourceType, resourceId);
        return favorite != null;
    }

    @Override
    public IPage<FavoriteVO> getFavoritePage(FavoriteQueryDTO queryDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Page<FavoriteVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return userFavoriteMapper.selectFavoritePage(page, userId, queryDTO.getResourceType());
    }

    @Override
    public FavoriteStatsVO getFavoriteStats() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        FavoriteStatsVO statsVO = new FavoriteStatsVO();

        // 统计各类型收藏数量
        statsVO.setTotalCount(userFavoriteMapper.countByUserIdAndType(userId, null));
        statsVO.setResourceCount(userFavoriteMapper.countByUserIdAndType(userId, "RESOURCE"));
        statsVO.setNewsCount(userFavoriteMapper.countByUserIdAndType(userId, "NEWS"));
        statsVO.setNoticeCount(userFavoriteMapper.countByUserIdAndType(userId, "NOTICE"));
        statsVO.setCourseCount(userFavoriteMapper.countByUserIdAndType(userId, "COURSE"));
        statsVO.setLabCount(userFavoriteMapper.countByUserIdAndType(userId, "LAB"));

        return statsVO;
    }
}
