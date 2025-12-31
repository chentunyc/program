-- ====================================================
-- 虚拟仿真实训教学管理及资源共享云平台 - 数据库设计
-- MySQL 8.0+
-- ====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `virtual_training_platform`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `virtual_training_platform`;

-- ====================================================
-- 1. 用户和权限模块
-- ====================================================

-- 用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(200) NOT NULL COMMENT '密码(加密)',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `student_no` VARCHAR(50) DEFAULT NULL COMMENT '学号(学生)',
  `employee_no` VARCHAR(50) DEFAULT NULL COMMENT '工号(教师/管理员)',
  `gender` TINYINT DEFAULT NULL COMMENT '性别:0-未知,1-男,2-女',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_student_no` (`student_no`),
  KEY `idx_employee_no` (`employee_no`),
  KEY `idx_phone` (`phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码:STUDENT,TEACHER,ADMIN',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `permission_type` TINYINT NOT NULL COMMENT '权限类型:1-菜单,2-按钮',
  `menu_path` VARCHAR(200) DEFAULT NULL COMMENT '菜单路径',
  `menu_icon` VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ====================================================
-- 2. 新闻公告模块
-- ====================================================

-- 新闻资讯表
DROP TABLE IF EXISTS `t_news`;
CREATE TABLE `t_news` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` VARCHAR(200) NOT NULL COMMENT '新闻标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '新闻摘要',
  `content` TEXT NOT NULL COMMENT '新闻内容',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `author` VARCHAR(50) DEFAULT NULL COMMENT '作者',
  `source` VARCHAR(100) DEFAULT NULL COMMENT '来源',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
  `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶:0-否,1-是',
  `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐:0-否,1-是',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-草稿,1-已发布,2-已下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='新闻资讯表';

-- 公告通知表
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content` TEXT NOT NULL COMMENT '公告内容',
  `notice_type` TINYINT NOT NULL COMMENT '公告类型:1-系统公告,2-教学通知,3-活动公告',
  `level` TINYINT DEFAULT 1 COMMENT '重要级别:1-一般,2-重要,3-紧急',
  `target_role` VARCHAR(50) DEFAULT NULL COMMENT '目标角色(ALL/STUDENT/TEACHER/ADMIN,逗号分隔)',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶:0-否,1-是',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-草稿,1-已发布,2-已过期',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_notice_type` (`notice_type`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告通知表';

-- ====================================================
-- 3. 用户中心模块
-- ====================================================

-- 用户详细信息表
DROP TABLE IF EXISTS `t_user_profile`;
CREATE TABLE `t_user_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '院系/部门',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级',
  `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `introduction` TEXT DEFAULT NULL COMMENT '个人简介',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户详细信息表';

-- 用户收藏表
DROP TABLE IF EXISTS `t_user_favorite`;
CREATE TABLE `t_user_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型:NEWS,NOTICE,COURSE,RESOURCE,LAB',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_resource` (`user_id`, `resource_type`, `resource_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_resource` (`resource_type`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 用户消息表
DROP TABLE IF EXISTS `t_user_message`;
CREATE TABLE `t_user_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `sender_id` BIGINT DEFAULT NULL COMMENT '发送者ID(系统消息为NULL)',
  `message_type` TINYINT NOT NULL COMMENT '消息类型:1-系统消息,2-课程通知,3-实验提醒,4-审核通知',
  `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `link_url` VARCHAR(500) DEFAULT NULL COMMENT '关联链接',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读:0-未读,1-已读',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户消息表';

-- ====================================================
-- 4. 资源中心模块
-- ====================================================

-- 资源基础表
DROP TABLE IF EXISTS `t_resource`;
CREATE TABLE `t_resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name` VARCHAR(200) NOT NULL COMMENT '资源名称',
  `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型:SIMULATION,VIDEO,AUDIO,DOCUMENT',
  `description` TEXT DEFAULT NULL COMMENT '资源描述',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
  `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `is_shared` TINYINT DEFAULT 0 COMMENT '是否共享:0-否,1-是',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-待审核,1-已发布,2-已下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_category` (`category`),
  KEY `idx_uploader` (`uploader_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源基础表';

-- 虚拟仿真资源表
DROP TABLE IF EXISTS `t_resource_simulation`;
CREATE TABLE `t_resource_simulation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `simulation_url` VARCHAR(500) NOT NULL COMMENT '仿真URL/启动地址',
  `technology` VARCHAR(100) DEFAULT NULL COMMENT '技术栈(Unity3D,WebGL等)',
  `support_platform` VARCHAR(100) DEFAULT NULL COMMENT '支持平台(PC,Mobile,Web)',
  `min_requirement` VARCHAR(500) DEFAULT NULL COMMENT '最低配置要求',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='虚拟仿真资源表';

-- 视频资源表
DROP TABLE IF EXISTS `t_resource_video`;
CREATE TABLE `t_resource_video` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `video_url` VARCHAR(500) NOT NULL COMMENT '视频URL',
  `duration` INT DEFAULT NULL COMMENT '时长(秒)',
  `resolution` VARCHAR(20) DEFAULT NULL COMMENT '分辨率(1080p,720p等)',
  `format` VARCHAR(20) DEFAULT NULL COMMENT '格式(mp4,avi等)',
  `subtitle_url` VARCHAR(500) DEFAULT NULL COMMENT '字幕文件URL',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频资源表';

-- 音频资源表
DROP TABLE IF EXISTS `t_resource_audio`;
CREATE TABLE `t_resource_audio` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `audio_url` VARCHAR(500) NOT NULL COMMENT '音频URL',
  `duration` INT DEFAULT NULL COMMENT '时长(秒)',
  `format` VARCHAR(20) DEFAULT NULL COMMENT '格式(mp3,wav等)',
  `bitrate` VARCHAR(20) DEFAULT NULL COMMENT '比特率',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音频资源表';

-- 文档资源表
DROP TABLE IF EXISTS `t_resource_document`;
CREATE TABLE `t_resource_document` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `document_url` VARCHAR(500) NOT NULL COMMENT '文档URL',
  `format` VARCHAR(20) DEFAULT NULL COMMENT '格式(pdf,doc,ppt等)',
  `page_count` INT DEFAULT NULL COMMENT '页数',
  `preview_url` VARCHAR(500) DEFAULT NULL COMMENT '预览URL',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档资源表';

-- ====================================================
-- 5. 实训中心模块
-- ====================================================

-- 课程表
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_code` VARCHAR(50) NOT NULL COMMENT '课程编码',
  `course_name` VARCHAR(200) NOT NULL COMMENT '课程名称',
  `description` TEXT DEFAULT NULL COMMENT '课程描述',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '课程封面',
  `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '开课院系',
  `credit` DECIMAL(3,1) DEFAULT NULL COMMENT '学分',
  `total_hours` INT DEFAULT NULL COMMENT '总学时',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '课程类别',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度等级:1-初级,2-中级,3-高级',
  `start_time` DATETIME DEFAULT NULL COMMENT '开课时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结课时间',
  `max_students` INT DEFAULT NULL COMMENT '最大学生数',
  `enrolled_count` INT DEFAULT 0 COMMENT '已报名人数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-未开课,1-进行中,2-已结课',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`course_code`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 实训任务表
DROP TABLE IF EXISTS `t_training_task`;
CREATE TABLE `t_training_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
  `description` TEXT DEFAULT NULL COMMENT '任务描述',
  `task_type` TINYINT NOT NULL COMMENT '任务类型:1-理论学习,2-实践操作,3-综合项目',
  `resource_id` BIGINT DEFAULT NULL COMMENT '关联资源ID',
  `lab_id` BIGINT DEFAULT NULL COMMENT '关联实验室ID',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度等级:1-初级,2-中级,3-高级',
  `total_score` INT DEFAULT 100 COMMENT '总分',
  `pass_score` INT DEFAULT 60 COMMENT '及格分',
  `time_limit` INT DEFAULT NULL COMMENT '时间限制(分钟)',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-未发布,1-进行中,2-已结束',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实训任务表';

-- 实训记录表
DROP TABLE IF EXISTS `t_training_record`;
CREATE TABLE `t_training_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `task_id` BIGINT NOT NULL COMMENT '任务ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `duration` INT DEFAULT NULL COMMENT '用时(分钟)',
  `score` DECIMAL(5,2) DEFAULT NULL COMMENT '得分',
  `submit_content` TEXT DEFAULT NULL COMMENT '提交内容',
  `submit_attachment` VARCHAR(500) DEFAULT NULL COMMENT '提交附件URL',
  `teacher_comment` TEXT DEFAULT NULL COMMENT '教师评语',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-进行中,1-已提交,2-已评分',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实训记录表';

-- 用户课程关联表
DROP TABLE IF EXISTS `t_user_course`;
CREATE TABLE `t_user_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `enroll_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `progress` INT DEFAULT 0 COMMENT '学习进度(%)',
  `total_score` DECIMAL(5,2) DEFAULT NULL COMMENT '总成绩',
  `status` TINYINT DEFAULT 1 COMMENT '状态:1-学习中,2-已完成,3-已退出',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户课程关联表';

-- ====================================================
-- 6. 实验室模块
-- ====================================================

-- 实验室表
DROP TABLE IF EXISTS `t_laboratory`;
CREATE TABLE `t_laboratory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '实验室ID',
  `lab_code` VARCHAR(50) NOT NULL COMMENT '实验室编码',
  `lab_name` VARCHAR(200) NOT NULL COMMENT '实验室名称',
  `description` TEXT DEFAULT NULL COMMENT '实验室描述',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '所在位置',
  `building` VARCHAR(100) DEFAULT NULL COMMENT '所在楼栋',
  `room_number` VARCHAR(50) DEFAULT NULL COMMENT '房间号',
  `capacity` INT DEFAULT NULL COMMENT '容纳人数',
  `area` DECIMAL(10,2) DEFAULT NULL COMMENT '面积(平方米)',
  `manager_id` BIGINT DEFAULT NULL COMMENT '管理员ID',
  `equipment_list` TEXT DEFAULT NULL COMMENT '设备清单',
  `open_time` VARCHAR(200) DEFAULT NULL COMMENT '开放时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-停用,1-正常,2-维护中',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lab_code` (`lab_code`),
  KEY `idx_manager_id` (`manager_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验室表';

-- 实验室预约表
DROP TABLE IF EXISTS `t_lab_reservation`;
CREATE TABLE `t_lab_reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `lab_id` BIGINT NOT NULL COMMENT '实验室ID',
  `user_id` BIGINT NOT NULL COMMENT '预约用户ID',
  `reservation_date` DATE NOT NULL COMMENT '预约日期',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME NOT NULL COMMENT '结束时间',
  `purpose` VARCHAR(500) DEFAULT NULL COMMENT '使用目的',
  `people_count` INT DEFAULT 1 COMMENT '使用人数',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待审核,1-已批准,2-已拒绝,3-已取消,4-已完成',
  `approve_by` BIGINT DEFAULT NULL COMMENT '审批人ID',
  `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  `approve_comment` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
  `check_in_time` DATETIME DEFAULT NULL COMMENT '签到时间',
  `check_out_time` DATETIME DEFAULT NULL COMMENT '签退时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_reservation_date` (`reservation_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验室预约表';

-- 实验室使用记录表
DROP TABLE IF EXISTS `t_lab_usage_record`;
CREATE TABLE `t_lab_usage_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `lab_id` BIGINT NOT NULL COMMENT '实验室ID',
  `reservation_id` BIGINT DEFAULT NULL COMMENT '预约ID',
  `user_id` BIGINT NOT NULL COMMENT '使用用户ID',
  `use_date` DATE NOT NULL COMMENT '使用日期',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `duration` INT DEFAULT NULL COMMENT '使用时长(分钟)',
  `purpose` VARCHAR(500) DEFAULT NULL COMMENT '使用目的',
  `feedback` TEXT DEFAULT NULL COMMENT '使用反馈',
  `equipment_status` VARCHAR(500) DEFAULT NULL COMMENT '设备状态记录',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_use_date` (`use_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验室使用记录表';

-- ====================================================
-- 7. 共享开放模块
-- ====================================================

-- 共享资源表(引用资源中心资源,标记为共享)
-- 使用 t_resource 表的 is_shared 字段标识

-- 共享实验室表(引用实验室,标记为对外开放)
DROP TABLE IF EXISTS `t_shared_lab`;
CREATE TABLE `t_shared_lab` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `lab_id` BIGINT NOT NULL COMMENT '实验室ID',
  `share_type` TINYINT NOT NULL COMMENT '共享类型:1-校内共享,2-校际共享,3-社会开放',
  `share_start_time` DATETIME NOT NULL COMMENT '共享开始时间',
  `share_end_time` DATETIME DEFAULT NULL COMMENT '共享结束时间',
  `open_days` VARCHAR(100) DEFAULT NULL COMMENT '开放日期(1-7,逗号分隔)',
  `open_time_slots` VARCHAR(500) DEFAULT NULL COMMENT '开放时段(JSON格式)',
  `max_appointments` INT DEFAULT NULL COMMENT '最大预约数/天',
  `is_need_approve` TINYINT DEFAULT 1 COMMENT '是否需要审批:0-否,1-是',
  `description` TEXT DEFAULT NULL COMMENT '共享说明',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-停止共享,1-正常共享',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lab_id` (`lab_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='共享实验室表';

-- ====================================================
-- 8. 教师功能模块(预留)
-- ====================================================

-- 设备监控表
DROP TABLE IF EXISTS `t_device_monitor`;
CREATE TABLE `t_device_monitor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '监控ID',
  `lab_id` BIGINT NOT NULL COMMENT '实验室ID',
  `device_name` VARCHAR(200) NOT NULL COMMENT '设备名称',
  `device_code` VARCHAR(100) DEFAULT NULL COMMENT '设备编码',
  `device_status` TINYINT NOT NULL COMMENT '设备状态:0-离线,1-正常,2-异常,3-维护中',
  `cpu_usage` DECIMAL(5,2) DEFAULT NULL COMMENT 'CPU使用率(%)',
  `memory_usage` DECIMAL(5,2) DEFAULT NULL COMMENT '内存使用率(%)',
  `disk_usage` DECIMAL(5,2) DEFAULT NULL COMMENT '磁盘使用率(%)',
  `network_status` TINYINT DEFAULT NULL COMMENT '网络状态:0-断开,1-正常',
  `last_check_time` DATETIME DEFAULT NULL COMMENT '最后检查时间',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_device_status` (`device_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备监控表';

-- 资源监控表
DROP TABLE IF EXISTS `t_resource_monitor`;
CREATE TABLE `t_resource_monitor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '监控ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `access_count` INT DEFAULT 0 COMMENT '访问次数',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `error_count` INT DEFAULT 0 COMMENT '错误次数',
  `avg_response_time` INT DEFAULT NULL COMMENT '平均响应时间(毫秒)',
  `storage_size` BIGINT DEFAULT NULL COMMENT '存储大小(字节)',
  `last_access_time` DATETIME DEFAULT NULL COMMENT '最后访问时间',
  `monitor_date` DATE NOT NULL COMMENT '监控日期',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_date` (`resource_id`, `monitor_date`),
  KEY `idx_monitor_date` (`monitor_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源监控表';

-- 教学监控表
DROP TABLE IF EXISTS `t_teaching_monitor`;
CREATE TABLE `t_teaching_monitor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '监控ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `monitor_date` DATE NOT NULL COMMENT '监控日期',
  `active_students` INT DEFAULT 0 COMMENT '活跃学生数',
  `task_completion_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '任务完成率(%)',
  `avg_score` DECIMAL(5,2) DEFAULT NULL COMMENT '平均分数',
  `online_duration` INT DEFAULT NULL COMMENT '在线时长(分钟)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_date` (`course_id`, `monitor_date`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_monitor_date` (`monitor_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学监控表';

-- 教学计划表
DROP TABLE IF EXISTS `t_teaching_plan`;
CREATE TABLE `t_teaching_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `plan_name` VARCHAR(200) NOT NULL COMMENT '计划名称',
  `semester` VARCHAR(50) DEFAULT NULL COMMENT '学期',
  `week_number` INT DEFAULT NULL COMMENT '周次',
  `content` TEXT DEFAULT NULL COMMENT '教学内容',
  `objective` TEXT DEFAULT NULL COMMENT '教学目标',
  `method` VARCHAR(500) DEFAULT NULL COMMENT '教学方法',
  `resource_ids` VARCHAR(500) DEFAULT NULL COMMENT '关联资源ID(逗号分隔)',
  `homework` TEXT DEFAULT NULL COMMENT '作业安排',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-计划中,1-进行中,2-已完成',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划表';

-- 过程结果表
DROP TABLE IF EXISTS `t_process_result`;
CREATE TABLE `t_process_result` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '结果ID',
  `task_id` BIGINT NOT NULL COMMENT '任务ID',
  `user_id` BIGINT NOT NULL COMMENT '学生ID',
  `process_data` TEXT DEFAULT NULL COMMENT '过程数据(JSON格式)',
  `step_records` TEXT DEFAULT NULL COMMENT '步骤记录',
  `answer_records` TEXT DEFAULT NULL COMMENT '答题记录',
  `operation_logs` TEXT DEFAULT NULL COMMENT '操作日志',
  `score_details` TEXT DEFAULT NULL COMMENT '评分明细(JSON格式)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_user` (`task_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='过程结果表';

-- 实验室申请表(教师申请实验室使用)
DROP TABLE IF EXISTS `t_lab_application`;
CREATE TABLE `t_lab_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `teacher_id` BIGINT NOT NULL COMMENT '申请教师ID',
  `lab_id` BIGINT NOT NULL COMMENT '实验室ID',
  `course_id` BIGINT DEFAULT NULL COMMENT '关联课程ID',
  `apply_date` DATE NOT NULL COMMENT '申请日期',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME NOT NULL COMMENT '结束时间',
  `purpose` VARCHAR(500) NOT NULL COMMENT '申请用途',
  `student_count` INT DEFAULT NULL COMMENT '预计学生人数',
  `equipment_requirement` TEXT DEFAULT NULL COMMENT '设备需求',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待审核,1-已批准,2-已拒绝,3-已取消',
  `approve_by` BIGINT DEFAULT NULL COMMENT '审批人ID',
  `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  `approve_comment` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_date` (`apply_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验室申请表';

-- ====================================================
-- 9. 管理员功能模块(预留)
-- ====================================================

-- 数据字典表
DROP TABLE IF EXISTS `t_data_dict`;
CREATE TABLE `t_data_dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
  `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
  `dict_label` VARCHAR(200) NOT NULL COMMENT '字典标签',
  `dict_value` VARCHAR(200) NOT NULL COMMENT '字典值',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-停用,1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`dict_type`, `dict_code`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';

-- 系统日志表
DROP TABLE IF EXISTS `t_system_log`;
CREATE TABLE `t_system_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `log_type` TINYINT NOT NULL COMMENT '日志类型:1-登录日志,2-操作日志,3-异常日志',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `operation` VARCHAR(200) DEFAULT NULL COMMENT '操作内容',
  `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
  `params` TEXT DEFAULT NULL COMMENT '请求参数',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `location` VARCHAR(100) DEFAULT NULL COMMENT 'IP归属地',
  `browser` VARCHAR(100) DEFAULT NULL COMMENT '浏览器',
  `os` VARCHAR(100) DEFAULT NULL COMMENT '操作系统',
  `status` TINYINT DEFAULT 1 COMMENT '状态:0-失败,1-成功',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `execute_time` INT DEFAULT NULL COMMENT '执行时长(毫秒)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_log_type` (`log_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 平台设置表
DROP TABLE IF EXISTS `t_platform_setting`;
CREATE TABLE `t_platform_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `setting_key` VARCHAR(100) NOT NULL COMMENT '设置键',
  `setting_value` TEXT DEFAULT NULL COMMENT '设置值',
  `setting_type` VARCHAR(50) DEFAULT NULL COMMENT '设置类型:STRING,NUMBER,BOOLEAN,JSON',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '设置描述',
  `group_name` VARCHAR(50) DEFAULT NULL COMMENT '分组名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_setting_key` (`setting_key`),
  KEY `idx_group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台设置表';

-- 仿真设备表
DROP TABLE IF EXISTS `t_simulation_device`;
CREATE TABLE `t_simulation_device` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `device_code` VARCHAR(100) NOT NULL COMMENT '设备编码',
  `device_name` VARCHAR(200) NOT NULL COMMENT '设备名称',
  `device_type` VARCHAR(100) DEFAULT NULL COMMENT '设备类型',
  `manufacturer` VARCHAR(200) DEFAULT NULL COMMENT '生产厂商',
  `model` VARCHAR(100) DEFAULT NULL COMMENT '型号',
  `lab_id` BIGINT DEFAULT NULL COMMENT '所属实验室ID',
  `purchase_date` DATE DEFAULT NULL COMMENT '购置日期',
  `purchase_price` DECIMAL(12,2) DEFAULT NULL COMMENT '购置价格',
  `warranty_period` INT DEFAULT NULL COMMENT '质保期(月)',
  `service_life` INT DEFAULT NULL COMMENT '使用年限',
  `maintenance_cycle` INT DEFAULT NULL COMMENT '维护周期(天)',
  `last_maintenance_date` DATE DEFAULT NULL COMMENT '最后维护日期',
  `next_maintenance_date` DATE DEFAULT NULL COMMENT '下次维护日期',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-报废,1-正常,2-故障,3-维修中',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_code` (`device_code`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仿真设备表';

-- 师资信息表
DROP TABLE IF EXISTS `t_teacher_info`;
CREATE TABLE `t_teacher_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(50) DEFAULT NULL COMMENT '职称',
  `degree` VARCHAR(50) DEFAULT NULL COMMENT '学位',
  `research_direction` VARCHAR(500) DEFAULT NULL COMMENT '研究方向',
  `achievements` TEXT DEFAULT NULL COMMENT '教学成果',
  `projects` TEXT DEFAULT NULL COMMENT '科研项目',
  `publications` TEXT DEFAULT NULL COMMENT '发表论文',
  `awards` TEXT DEFAULT NULL COMMENT '获奖情况',
  `teaching_years` INT DEFAULT NULL COMMENT '教学年限',
  `course_count` INT DEFAULT 0 COMMENT '授课门数',
  `student_count` INT DEFAULT 0 COMMENT '指导学生数',
  `rating` DECIMAL(3,2) DEFAULT NULL COMMENT '教学评分(5分制)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='师资信息表';

-- ====================================================
-- 初始化基础数据
-- ====================================================

-- 插入角色数据
INSERT INTO `t_role` (`id`, `role_code`, `role_name`, `description`, `sort_order`, `create_by`) VALUES
(1, 'ADMIN', '管理员', '系统管理员,拥有所有权限', 1, 1),
(2, 'TEACHER', '教师', '教师用户,拥有教学管理权限', 2, 1),
(3, 'STUDENT', '学生', '学生用户,拥有学习相关权限', 3, 1);

-- 插入默认管理员用户 (密码: admin123, 需要加密后替换)
INSERT INTO `t_user` (`id`, `username`, `password`, `real_name`, `employee_no`, `gender`, `phone`, `email`, `create_by`) VALUES
(1, 'admin', '$2a$12$dJGfkwUNyTHacQhMsA.QzeT16wE4WzdBzi7DxDkhnneNrHHqzZb1q', '系统管理员', 'ADMIN001', 1, '13800138000', 'admin@example.com', 1);

-- 插入管理员角色关联
INSERT INTO `t_user_role` (`user_id`, `role_id`, `create_by`) VALUES
(1, 1, 1);

-- 插入数据字典示例数据
INSERT INTO `t_data_dict` (`dict_type`, `dict_code`, `dict_label`, `dict_value`, `sort_order`, `create_by`) VALUES
('user_gender', 'UNKNOWN', '未知', '0', 1, 1),
('user_gender', 'MALE', '男', '1', 2, 1),
('user_gender', 'FEMALE', '女', '2', 3, 1),
('user_status', 'DISABLED', '禁用', '0', 1, 1),
('user_status', 'NORMAL', '正常', '1', 2, 1),
('resource_type', 'SIMULATION', '虚拟仿真', 'SIMULATION', 1, 1),
('resource_type', 'VIDEO', '视频资源', 'VIDEO', 2, 1),
('resource_type', 'AUDIO', '音频资源', 'AUDIO', 3, 1),
('resource_type', 'DOCUMENT', '文档资源', 'DOCUMENT', 4, 1);
