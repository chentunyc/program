package com.training.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色枚举
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /**
     * 管理员
     */
    ADMIN("ADMIN", "管理员"),

    /**
     * 教师
     */
    TEACHER("TEACHER", "教师"),

    /**
     * 学生
     */
    STUDENT("STUDENT", "学生");

    /**
     * 角色编码
     */
    private final String code;

    /**
     * 角色名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static RoleEnum getByCode(String code) {
        for (RoleEnum roleEnum : values()) {
            if (roleEnum.getCode().equals(code)) {
                return roleEnum;
            }
        }
        return null;
    }
}
