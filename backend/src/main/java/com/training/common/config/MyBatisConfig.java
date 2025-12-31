package com.training.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.training.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置类
 * Mapper通过@Mapper注解自动注册
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Configuration

public class MyBatisConfig {

    /**
     * MyBatis Plus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInner = new PaginationInnerInterceptor();
        paginationInner.setDbType(DbType.MYSQL);
        paginationInner.setMaxLimit(1000L);
        paginationInner.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInner);

        // 防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 元数据对象处理器 - 自动填充公共字段
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

                try {
                    Long userId = SecurityUtils.getUserId();
                    if (userId != null) {
                        this.strictInsertFill(metaObject, "createBy", Long.class, userId);
                        this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
                    }
                } catch (Exception e) {
                    log.debug("获取当前用户ID失败: {}", e.getMessage());
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

                try {
                    Long userId = SecurityUtils.getUserId();
                    if (userId != null) {
                        this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
                    }
                } catch (Exception e) {
                    log.debug("获取当前用户ID失败: {}", e.getMessage());
                }
            }
        };
    }
}
