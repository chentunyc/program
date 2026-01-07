-- ===================================
-- 虚拟仿真实训平台 - 修复版测试数据初始化脚本
-- ===================================

USE virtual_training_platform;

-- ===================================
-- 1. 清空现有数据
-- ===================================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE t_user_role;
TRUNCATE TABLE t_role_permission;
TRUNCATE TABLE t_user;
TRUNCATE TABLE t_role;
TRUNCATE TABLE t_permission;

SET FOREIGN_KEY_CHECKS = 1;

-- ===================================
-- 2. 插入角色数据（修复版）
-- ===================================
INSERT INTO t_role (id, role_name, role_code, description, sort_order, status, is_deleted, create_time, update_time) VALUES
(1, '超级管理员', 'ADMIN', '系统超级管理员，拥有所有权限', 1, 1, 0, NOW(), NOW()),
(2, '教师', 'TEACHER', '教师角色，可以管理课程和实验', 2, 1, 0, NOW(), NOW()),
(3, '学生', 'STUDENT', '学生角色，可以学习课程和完成实验', 3, 1, 0, NOW(), NOW()),
(4, '访客','GUEST',  '访客用户,只能访问首页、新闻公告、个人中心和资源中心',4, 1,0, NOW(), NOW()),
(5, '资料管理员','DATA_ADMIN',  '资料管理员,拥有资料审核权限',5, 1,0, NOW(), NOW());
-- ===================================
-- 3. 插入权限数据（修复版）
-- ===================================
INSERT INTO t_permission (id, permission_name, permission_code, permission_type, parent_id, menu_path, menu_icon, sort_order, status, is_deleted, create_time, update_time) VALUES
-- 系统管理权限 (permission_type: 1-菜单, 2-按钮)
(1, '系统管理', 'system:manage', 1, 0, '/system', 'system', 1, 1, 0, NOW(), NOW()),
(2, '用户管理', 'system:user:view', 1, 1, '/system/user', 'user', 1, 1, 0, NOW(), NOW()),
(3, '用户新增', 'system:user:add', 2, 2, NULL, NULL, 1, 1, 0, NOW(), NOW()),
(4, '用户编辑', 'system:user:edit', 2, 2, NULL, NULL, 2, 1, 0, NOW(), NOW()),
(5, '角色管理', 'system:role:view', 1, 1, '/system/role', 'team', 2, 1, 0, NOW(), NOW()),

-- 课程管理权限
(10, '课程管理', 'course:manage', 1, 0, '/course', 'read', 2, 1, 0, NOW(), NOW()),
(11, '课程查看', 'course:view', 2, 10, NULL, NULL, 1, 1, 0, NOW(), NOW()),
(12, '课程创建', 'course:create', 2, 10, NULL, NULL, 2, 1, 0, NOW(), NOW()),
(13, '课程编辑', 'course:edit', 2, 10, NULL, NULL, 3, 1, 0, NOW(), NOW()),

-- 实验管理权限
(20, '实验管理', 'lab:manage', 1, 0, '/lab', 'experiment', 3, 1, 0, NOW(), NOW()),
(21, '实验查看', 'lab:view', 2, 20, NULL, NULL, 1, 1, 0, NOW(), NOW()),
(22, '实验创建', 'lab:create', 2, 20, NULL, NULL, 2, 1, 0, NOW(), NOW());

-- ===================================
-- 4. 插入用户数据（修复版）
-- ===================================
-- 密码说明（已使用BCrypt加密）：
-- admin123  ->  $2a$10$5t6I9xR.1qywJjtpDmU/G.bLIU0UEvCWwa6EYldgyQhyE3Yy8oy7G
-- teacher123 -> $2a$12$20QmUSDtJaX.ehG8/2BA7.E8dyJgUG7gQKGO5yCrD1.8s1OHWv0fS
-- student123 -> $2a$12$q86RfaXgVNLZ31xLV2NXjeErQC77xMnkUz8xVmMzwQesxumcZzVFC
-- guest123 -> $2a$12$gTDrsYz/wp.R8veyFy79sOZzsDGjgNiTgEBv074Ja1k5ief.SkBV.
-- dataadmin123 ->$2a$12$EmQNlYs0di9It9kNlgj/feOeIwQokPMVWjVa5Y/PweBgJTyuT8l0y
INSERT INTO t_user (id, username, password, real_name, employee_no, gender, phone, email, avatar, status, is_deleted, create_time, update_time, last_login_time) VALUES
(1, 'admin', '$2a$10$5t6I9xR.1qywJjtpDmU/G.bLIU0UEvCWwa6EYldgyQhyE3Yy8oy7G', '系统管理员', 'A001', 1, '13800138000', 'admin@example.com', NULL, 1, 0, NOW(), NOW(), NULL),
(2, 'teacher', '$2a$12$20QmUSDtJaX.ehG8/2BA7.E8dyJgUG7gQKGO5yCrD1.8s1OHWv0fS', '张老师', 'T001', 1, '13800138001', 'teacher@example.com', NULL, 1, 0, NOW(), NOW(), NULL),
(3, 'student', '$2a$12$q86RfaXgVNLZ31xLV2NXjeErQC77xMnkUz8xVmMzwQesxumcZzVFC', '李同学', 'S001', 2, '13800138002', 'student@example.com', NULL, 1, 0, NOW(), NOW(), NULL),
(4, 'guest', '$2a$12$gTDrsYz/wp.R8veyFy79sOZzsDGjgNiTgEBv074Ja1k5ief.SkBV.', '李访客', 'G001', 2, '13800138002', 'student@example.com', NULL, 1, 0, NOW(), NOW(), NULL),
(5, 'dataadmin', '$2a$12$EmQNlYs0di9It9kNlgj/feOeIwQokPMVWjVa5Y/PweBgJTyuT8l0y', '李资料管理员', 'D001', 2, '13800138002', 'student@example.com', NULL, 1, 0, NOW(), NOW(), NULL);

-- ===================================
-- 5. 分配角色权限
-- ===================================
INSERT INTO t_role_permission (role_id, permission_id) VALUES
-- 管理员：所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 10), (1, 11), (1, 12), (1, 13), (1, 20), (1, 21), (1, 22),
-- 教师：课程和实验管理
(2, 10), (2, 11), (2, 12), (2, 13), (2, 20), (2, 21), (2, 22),
-- 学生：查看权限
(3, 10), (3, 11), (3, 20), (3, 21);

-- ===================================
-- 6. 分配用户角色
-- ===================================
INSERT INTO t_user_role (user_id, role_id) VALUES
                                               (1, 1),  -- admin 是管理员
                                               (2, 2),  -- teacher 是教师
                                               (3, 3),  -- student 是学生
                                               (4, 4),  -- guest 是访客
                                               (5, 5);  -- dataadmin 是资源管理员