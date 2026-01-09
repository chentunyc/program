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
  `employee_no` VARCHAR(50) NOT NULL COMMENT '编号(学号/工号/访客ID)',
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
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
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

-- 实训项目表
DROP TABLE IF EXISTS `t_training_project`;
CREATE TABLE `t_training_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_code` VARCHAR(50) NOT NULL COMMENT '项目编码',
  `project_name` VARCHAR(200) NOT NULL COMMENT '项目名称',
  `description` TEXT DEFAULT NULL COMMENT '项目描述',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '项目封面',
  `manager_id` BIGINT DEFAULT NULL COMMENT '项目负责人ID',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '项目类别',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度等级:1-初级,2-中级,3-高级',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `max_members` INT DEFAULT NULL COMMENT '最大参与人数',
  `member_count` INT DEFAULT 0 COMMENT '已参与人数',
  `total_tasks` INT DEFAULT 0 COMMENT '任务总数',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-未开始,1-进行中,2-已结束',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_code` (`project_code`),
  KEY `idx_manager_id` (`manager_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实训项目表';

-- 实训任务表
DROP TABLE IF EXISTS `t_training_task`;
CREATE TABLE `t_training_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
  `description` TEXT DEFAULT NULL COMMENT '任务描述',
  `task_type` TINYINT NOT NULL DEFAULT 1 COMMENT '任务类型:1-理论学习,2-实践操作,3-综合项目',
  `resource_id` BIGINT DEFAULT NULL COMMENT '关联资源ID',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度等级:1-初级,2-中级,3-高级',
  `total_score` INT DEFAULT 100 COMMENT '总分',
  `pass_score` INT DEFAULT 60 COMMENT '及格分',
  `time_limit` INT DEFAULT NULL COMMENT '时间限制(分钟)',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-未发布,1-已发布',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_resource_id` (`resource_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实训任务表';

-- 实训记录表（学生参与任务的记录）
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
  UNIQUE KEY `uk_task_user` (`task_id`, `user_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实训记录表';

-- 用户项目关联表（学生参与项目）
DROP TABLE IF EXISTS `t_user_project`;
CREATE TABLE `t_user_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `progress` INT DEFAULT 0 COMMENT '完成进度(%)',
  `completed_tasks` INT DEFAULT 0 COMMENT '已完成任务数',
  `total_score` DECIMAL(5,2) DEFAULT NULL COMMENT '总成绩',
  `status` TINYINT DEFAULT 1 COMMENT '状态:1-进行中,2-已完成,3-已退出',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_project` (`user_id`, `project_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户项目关联表';

-- ====================================================
-- 6. 管理员功能模块(预留)
-- ====================================================

-- 系统配置表
DROP TABLE IF EXISTS `t_system_config`;
CREATE TABLE `t_system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_group` VARCHAR(50) NOT NULL COMMENT '配置组:basic,upload,security',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT DEFAULT NULL COMMENT '配置值',
  `config_type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '值类型:string,number,boolean,json',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_key` (`config_group`, `config_key`),
  KEY `idx_config_group` (`config_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
