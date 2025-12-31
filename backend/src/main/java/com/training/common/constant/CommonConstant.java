package com.training.common.constant;

/**
 * 通用常量
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface CommonConstant {

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    Integer FAIL = 500;

    /**
     * 登录用户Redis Key
     */
    String LOGIN_USER_KEY = "login:user:";

    /**
     * Token过期时间(默认7天,单位:秒)
     */
    Long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60L;

    /**
     * 验证码Redis Key
     */
    String CAPTCHA_CODE_KEY = "captcha:code:";

    /**
     * 验证码过期时间(5分钟,单位:秒)
     */
    Long CAPTCHA_EXPIRE_TIME = 5 * 60L;

    /**
     * 参数缓存Redis Key
     */
    String SYS_CONFIG_KEY = "sys:config:";

    /**
     * 字典缓存Redis Key
     */
    String SYS_DICT_KEY = "sys:dict:";

    /**
     * 逻辑删除 - 未删除
     */
    Integer NOT_DELETED = 0;

    /**
     * 逻辑删除 - 已删除
     */
    Integer DELETED = 1;

    /**
     * 状态 - 禁用
     */
    Integer STATUS_DISABLED = 0;

    /**
     * 状态 - 正常
     */
    Integer STATUS_NORMAL = 1;

    /**
     * 是 - 标记
     */
    Integer YES = 1;

    /**
     * 否 - 标记
     */
    Integer NO = 0;

    /**
     * 超级管理员ID
     */
    Long SUPER_ADMIN_ID = 1L;

    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 成功消息
     */
    String SUCCESS_MSG = "操作成功";

    /**
     * 失败消息
     */
    String FAIL_MSG = "操作失败";
}
