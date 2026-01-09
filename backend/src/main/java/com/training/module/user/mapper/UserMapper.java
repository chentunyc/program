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
     * 获取指定前缀的最大编号（包括已删除的记录，因为唯一索引不区分）
     *
     * @param prefix 编号前缀(如 A, T, S, G, D)
     * @return 最大编号，如 S001
     */
    @Select("SELECT employee_no FROM t_user WHERE employee_no LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY employee_no DESC LIMIT 1")
    String selectMaxEmployeeNo(String prefix);

    /**
     * 检查用户名是否存在（包括已删除的用户，因为数据库唯一索引不区分）
     *
     * @param username 用户名
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE username = #{username}")
    Long countByUsername(String username);

    /**
     * 检查手机号是否已被使用（包括已删除的用户）
     *
     * @param phone 手机号
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE phone = #{phone}")
    Long countByPhone(String phone);

    /**
     * 检查邮箱是否已被使用（包括已删除的用户）
     *
     * @param email 邮箱
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE email = #{email}")
    Long countByEmail(String email);

    /**
     * 检查员工编号是否已被使用（包括已删除的用户，因为数据库唯一索引不区分）
     *
     * @param employeeNo 员工编号
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE employee_no = #{employeeNo}")
    Long countByEmployeeNo(String employeeNo);

    /**
     * 检查员工编号是否已被使用（排除指定用户，用于更新时验证）
     *
     * @param employeeNo 员工编号
     * @param excludeUserId 需要排除的用户ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE employee_no = #{employeeNo} AND id != #{excludeUserId}")
    Long countByEmployeeNoExcludeUser(String employeeNo, Long excludeUserId);
}
