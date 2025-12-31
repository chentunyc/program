package com.training.module.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.training.common.base.Result;
import com.training.module.user.dto.FavoriteDTO;
import com.training.module.user.dto.FavoriteQueryDTO;
import com.training.module.user.service.UserFavoriteService;
import com.training.module.user.vo.FavoriteStatsVO;
import com.training.module.user.vo.FavoriteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户收藏控制器
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Tag(name = "用户收藏", description = "收藏管理接口")
@RestController
@RequestMapping("/v1/user/favorites")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 添加收藏
     */
    @Operation(summary = "添加收藏")
    @PostMapping
    public Result<Long> addFavorite(@Validated @RequestBody FavoriteDTO favoriteDTO) {
        log.info("添加收藏: {}", favoriteDTO);
        Long id = userFavoriteService.addFavorite(favoriteDTO);
        return Result.success("收藏成功", id);
    }

    /**
     * 取消收藏
     */
    @Operation(summary = "取消收藏")
    @DeleteMapping("/{resourceType}/{resourceId}")
    public Result<Void> removeFavorite(
            @Parameter(description = "资源类型") @PathVariable String resourceType,
            @Parameter(description = "资源ID") @PathVariable Long resourceId) {
        log.info("取消收藏: {} - {}", resourceType, resourceId);
        userFavoriteService.removeFavorite(resourceType, resourceId);
        return Result.successMsg("取消收藏成功");
    }

    /**
     * 检查是否已收藏
     */
    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{resourceType}/{resourceId}")
    public Result<Boolean> checkFavorite(
            @Parameter(description = "资源类型") @PathVariable String resourceType,
            @Parameter(description = "资源ID") @PathVariable Long resourceId) {
        boolean isFavorited = userFavoriteService.isFavorited(resourceType, resourceId);
        return Result.success(isFavorited);
    }

    /**
     * 分页查询收藏列表
     */
    @Operation(summary = "分页查询收藏列表")
    @GetMapping
    public Result<IPage<FavoriteVO>> getFavoritePage(FavoriteQueryDTO queryDTO) {
        log.info("查询收藏列表: {}", queryDTO);
        IPage<FavoriteVO> page = userFavoriteService.getFavoritePage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取收藏统计
     */
    @Operation(summary = "获取收藏统计")
    @GetMapping("/stats")
    public Result<FavoriteStatsVO> getFavoriteStats() {
        FavoriteStatsVO stats = userFavoriteService.getFavoriteStats();
        return Result.success(stats);
    }
}
