USE virtual_training_platform;

-- 初始化系统配置数据
INSERT INTO `t_system_config` (`config_group`, `config_key`, `config_value`, `config_type`, `description`, `sort_order`) VALUES
-- 基本配置
('basic', 'siteName', '虚拟仿真实训教学管理及资源共享云平台', 'string', '站点名称', 1),
('basic', 'siteLogo', '/logo.png', 'string', '站点Logo', 2),
('basic', 'siteDescription', '专业的虚拟仿真实训教学管理平台，提供资源共享、实训管理等功能', 'string', '站点描述', 3),
('basic', 'icpNumber', '粤ICP备XXXXXXXX号', 'string', 'ICP备案号', 4),
('basic', 'copyright', '© 2024 虚拟仿真实训平台 版权所有', 'string', '版权信息', 5),
('basic', 'supportEmail', 'support@training.com', 'string', '客服邮箱', 6),
('basic', 'supportPhone', '400-123-4567', 'string', '客服电话', 7),
-- 上传配置
('upload', 'uploadPath', './uploads', 'string', '上传路径', 1),
('upload', 'allowedTypes', 'image/jpeg,image/png,image/gif,application/pdf', 'string', '允许的文件类型', 2),
('upload', 'maxFileSize', '10', 'number', '最大文件大小(MB)', 3),
('upload', 'imageCompression', 'true', 'boolean', '图片压缩', 4),
-- 安全配置
('security', 'minPasswordLength', '6', 'number', '密码最小长度', 1),
('security', 'passwordRequirements', '["lowercase","number"]', 'json', '密码复杂度要求', 2),
('security', 'sessionTimeout', '120', 'number', '会话超时时间(分钟)', 3),
('security', 'loginLockEnabled', 'true', 'boolean', '登录失败锁定', 4),
('security', 'maxLoginAttempts', '5', 'number', '失败次数阈值', 5);
