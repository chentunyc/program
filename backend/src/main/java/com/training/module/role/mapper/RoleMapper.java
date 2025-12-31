package com.training.module.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.training.module.role.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}
