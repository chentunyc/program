package com.training.module.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.role.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色编码查询角色ID
     *
     * @param roleCode 角色编码
     * @return 角色ID
     */
    @Select("SELECT id FROM t_role WHERE role_code = #{roleCode} AND status = 1 AND is_deleted = 0")
    Long selectIdByRoleCode(String roleCode);

    /**
     * 根据角色ID查询角色编码
     *
     * @param roleId 角色ID
     * @return 角色编码
     */
    @Select("SELECT role_code FROM t_role WHERE id = #{roleId} AND status = 1 AND is_deleted = 0")
    String selectRoleCodeById(@Param("roleId") Long roleId);
}
