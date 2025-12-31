package com.training.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Select("SELECT r.role_code FROM t_role r " +
            "INNER JOIN t_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.is_deleted = 0")
    List<String> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Select("SELECT DISTINCT p.permission_code FROM t_permission p " +
            "INNER JOIN t_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN t_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1 AND p.is_deleted = 0")
    List<String> selectPermissionsByUserId(Long userId);
}
