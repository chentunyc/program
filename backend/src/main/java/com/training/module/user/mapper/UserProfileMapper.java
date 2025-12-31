package com.training.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户扩展信息Mapper
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    /**
     * 根据用户ID查询扩展信息
     */
    @Select("SELECT * FROM t_user_profile WHERE user_id = #{userId} AND is_deleted = 0")
    UserProfile selectByUserId(Long userId);
}
