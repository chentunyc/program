-- ========================================
-- 资源中心模块 - 测试数据
-- 根据 database/schema.sql 中的表结构设计
-- 使用公开可用的测试资源URL
-- ========================================

USE `virtual_training_platform`;

-- ========================================
-- 清除旧资源数据（谨慎使用）
-- ========================================
DELETE FROM t_resource_document WHERE resource_id > 0;
DELETE FROM t_resource_audio WHERE resource_id > 0;
DELETE FROM t_resource_video WHERE resource_id > 0;
DELETE FROM t_resource_simulation WHERE resource_id > 0;
DELETE FROM t_resource WHERE id > 0;

-- 重置自增ID
ALTER TABLE t_resource AUTO_INCREMENT = 1;
ALTER TABLE t_resource_simulation AUTO_INCREMENT = 1;
ALTER TABLE t_resource_video AUTO_INCREMENT = 1;
ALTER TABLE t_resource_audio AUTO_INCREMENT = 1;
ALTER TABLE t_resource_document AUTO_INCREMENT = 1;

-- ========================================
-- 1. 虚拟仿真资源
-- ========================================
INSERT INTO `t_resource` (`resource_name`, `resource_type`, `description`, `cover_image`, `category`, `tags`, `uploader_id`, `file_size`, `download_count`, `view_count`, `is_shared`, `status`, `create_by`) VALUES
('电路原理虚拟仿真实验', 'SIMULATION', '通过虚拟仿真技术，模拟电子电路实验环境，支持元器件拖拽、电路连接、波形观测等功能，适用于电子工程专业基础课程教学。', 'https://picsum.photos/seed/circuit/400/300', '电子工程', '电路,仿真,实验,电子', 1, 52428800, 156, 1024, 1, 1, 1),
('化学反应动力学仿真', 'SIMULATION', '模拟各种化学反应过程，包括反应速率、平衡常数、温度影响等，支持3D分子模型展示，安全无风险地完成化学实验。', 'https://picsum.photos/seed/chemistry/400/300', '化学', '化学,反应,动力学,分子', 1, 104857600, 89, 756, 1, 1, 1),
('机械结构装配仿真', 'SIMULATION', '提供机械零部件三维展示与虚拟装配功能，支持爆炸图查看、装配顺序指导、公差配合分析等，适用于机械设计课程。', 'https://picsum.photos/seed/mechanical/400/300', '机械工程', '机械,装配,3D,CAD', 1, 209715200, 234, 1567, 1, 1, 1),
('PLC控制系统仿真', 'SIMULATION', '模拟西门子、三菱等主流PLC控制系统，支持梯形图编程、指令调试、HMI界面设计，适用于自动化控制课程实训。', 'https://picsum.photos/seed/plc/400/300', '自动化', 'PLC,自动化,控制系统,编程', 1, 78643200, 67, 489, 1, 1, 1),
('数控机床加工仿真', 'SIMULATION', '模拟数控车床、铣床加工过程，支持G代码编程、刀具路径模拟、碰撞检测，帮助学生掌握数控加工技术。', 'https://picsum.photos/seed/cnc/400/300', '机械工程', '数控,CNC,加工,制造', 1, 157286400, 112, 834, 1, 1, 1);

-- 虚拟仿真资源详情
INSERT INTO `t_resource_simulation` (`resource_id`, `simulation_url`, `technology`, `support_platform`, `min_requirement`) VALUES
(1, 'https://phet.colorado.edu/sims/html/circuit-construction-kit-dc/latest/circuit-construction-kit-dc_all.html', 'HTML5', 'PC, Web, Mobile', 'Chrome 80+/Firefox 75+, 4GB RAM'),
(2, 'https://phet.colorado.edu/sims/html/reactants-products-and-leftovers/latest/reactants-products-and-leftovers_all.html', 'HTML5', 'PC, Web, Mobile', 'Chrome 80+, 4GB RAM'),
(3, 'https://www.tinkercad.com/', 'WebGL', 'PC, Web', 'Windows 10/11, 8GB RAM, 支持WebGL'),
(4, 'https://www.plcsimulator.net/', 'HTML5 + JavaScript', 'PC, Web', 'Chrome 85+, 4GB RAM'),
(5, 'https://www.opendesk.cc/', 'WebGL', 'PC', 'Windows 10, 8GB RAM');

-- ========================================
-- 2. 视频资源
-- ========================================
INSERT INTO `t_resource` (`resource_name`, `resource_type`, `description`, `cover_image`, `category`, `tags`, `uploader_id`, `file_size`, `download_count`, `view_count`, `is_shared`, `status`, `create_by`) VALUES
('Python编程从入门到精通', 'VIDEO', 'Python编程完整教程，涵盖基础语法、面向对象、文件操作、网络编程、数据分析等内容，适合零基础学习者系统学习。', 'https://picsum.photos/seed/python/400/300', '编程开发', 'Python,编程,入门,数据分析', 1, 524288000, 456, 3245, 1, 1, 1),
('数据结构与算法详解', 'VIDEO', '深入讲解常见数据结构（数组、链表、树、图）和经典算法（排序、查找、动态规划），配合LeetCode实战练习。', 'https://picsum.photos/seed/algorithm/400/300', '计算机科学', '算法,数据结构,面试,LeetCode', 1, 1073741824, 789, 5678, 1, 1, 1),
('机器学习实战教程', 'VIDEO', '从机器学习基础理论到实际项目应用，包括监督学习、无监督学习、深度学习入门，使用Python sklearn和TensorFlow。', 'https://picsum.photos/seed/ml/400/300', '人工智能', 'AI,机器学习,深度学习,TensorFlow', 1, 2147483648, 345, 2134, 1, 1, 1),
('Spring Boot微服务开发', 'VIDEO', 'Spring Boot框架完整开发教程，涵盖RESTful API设计、数据库集成、安全认证、微服务架构等企业级开发技术。', 'https://picsum.photos/seed/springboot/400/300', '后端开发', 'Java,Spring Boot,微服务,后端', 1, 838860800, 567, 4123, 1, 1, 1),
('Vue.js前端开发实战', 'VIDEO', 'Vue.js 3.0完整教程，包括组合式API、Pinia状态管理、Vue Router、Element Plus组件库等前端开发技术栈。', 'https://picsum.photos/seed/vuejs/400/300', '前端开发', 'Vue,前端,JavaScript,ElementPlus', 1, 629145600, 678, 4567, 1, 1, 1),
('电气工程基础实验', 'VIDEO', '电气工程专业实验课程录像，包括电路分析、电机控制、变压器实验等，配合理论讲解和操作演示。', 'https://picsum.photos/seed/electrical/400/300', '电气工程', '电气,电机,实验,电路', 1, 419430400, 123, 987, 0, 1, 1);

-- 视频资源详情（使用公开可用的示例视频）
INSERT INTO `t_resource_video` (`resource_id`, `video_url`, `duration`, `resolution`, `format`, `subtitle_url`) VALUES
(6, 'https://www.w3schools.com/html/mov_bbb.mp4', 10, '720p', 'mp4', NULL),
(7, 'https://www.w3schools.com/html/movie.mp4', 12, '720p', 'mp4', NULL),
(8, 'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4', 5, '720p', 'mp4', NULL),
(9, 'https://www.w3schools.com/html/mov_bbb.mp4', 10, '720p', 'mp4', NULL),
(10, 'https://www.w3schools.com/html/movie.mp4', 12, '720p', 'mp4', NULL),
(11, 'https://www.w3schools.com/html/mov_bbb.mp4', 10, '720p', 'mp4', NULL);

-- ========================================
-- 3. 音频资源
-- ========================================
INSERT INTO `t_resource` (`resource_name`, `resource_type`, `description`, `cover_image`, `category`, `tags`, `uploader_id`, `file_size`, `download_count`, `view_count`, `is_shared`, `status`, `create_by`) VALUES
('英语听力训练-初级篇', 'AUDIO', '适合英语初学者的听力练习材料，包含日常对话、短文朗读、听力理解练习，语速适中，配有文本对照。', 'https://picsum.photos/seed/english/400/300', '英语学习', '英语,听力,初级,口语', 1, 52428800, 234, 1567, 1, 1, 1),
('编程思维与方法论播客', 'AUDIO', '探讨软件开发中的思维方式和方法论，包括设计模式、重构技巧、代码质量等话题，适合开发者通勤收听。', 'https://picsum.photos/seed/podcast/400/300', '编程', '播客,编程思维,软件开发,设计模式', 1, 31457280, 89, 567, 1, 1, 1),
('计算机网络原理讲座', 'AUDIO', '计算机网络基础知识音频讲座，涵盖OSI模型、TCP/IP协议、网络安全等核心概念，适合碎片化学习。', 'https://picsum.photos/seed/network/400/300', '计算机网络', '网络,TCP/IP,协议,安全', 1, 78643200, 156, 823, 1, 1, 1),
('数学建模方法讲解', 'AUDIO', '数学建模竞赛指导音频，讲解常用建模方法、软件工具使用、论文写作技巧等内容。', 'https://picsum.photos/seed/math/400/300', '数学', '数学建模,竞赛,MATLAB,论文', 1, 41943040, 67, 345, 0, 1, 1);

-- 音频资源详情（使用公开可用的示例音频）
INSERT INTO `t_resource_audio` (`resource_id`, `audio_url`, `duration`, `format`, `bitrate`) VALUES
(12, 'https://www.w3schools.com/html/horse.mp3', 2, 'mp3', '128kbps'),
(13, 'https://www.w3schools.com/html/horse.mp3', 2, 'mp3', '128kbps'),
(14, 'https://www.w3schools.com/html/horse.mp3', 2, 'mp3', '128kbps'),
(15, 'https://www.w3schools.com/html/horse.mp3', 2, 'mp3', '128kbps');

-- ========================================
-- 4. 文档资源
-- ========================================
INSERT INTO `t_resource` (`resource_name`, `resource_type`, `description`, `cover_image`, `category`, `tags`, `uploader_id`, `file_size`, `download_count`, `view_count`, `is_shared`, `status`, `create_by`) VALUES
('Spring Boot开发实战指南', 'DOCUMENT', 'Spring Boot框架完整开发手册，涵盖项目搭建、配置管理、数据库集成、安全认证、部署运维等企业级开发实践。', 'https://picsum.photos/seed/springdoc/400/300', '后端开发', 'Spring Boot,Java,后端,微服务', 1, 10485760, 345, 2134, 1, 1, 1),
('Vue.js最佳实践手册', 'DOCUMENT', 'Vue.js项目开发最佳实践文档，包括项目结构设计、组件设计规范、性能优化、测试策略等内容。', 'https://picsum.photos/seed/vuedoc/400/300', '前端开发', 'Vue,前端,JavaScript,最佳实践', 1, 5242880, 234, 1567, 1, 1, 1),
('MySQL数据库设计规范', 'DOCUMENT', '企业级MySQL数据库设计规范与最佳实践，包括命名规范、索引优化、SQL编写规范、分库分表策略等。', 'https://picsum.photos/seed/mysqldoc/400/300', '数据库', 'MySQL,数据库,设计规范,优化', 1, 3145728, 178, 1234, 1, 1, 1),
('Linux系统管理手册', 'DOCUMENT', 'Linux系统管理完整指南，涵盖用户管理、文件系统、网络配置、Shell脚本、系统监控等运维知识。', 'https://picsum.photos/seed/linuxdoc/400/300', '运维', 'Linux,系统管理,Shell,运维', 1, 8388608, 267, 1890, 1, 1, 1),
('机器学习算法原理讲义', 'DOCUMENT', '机器学习核心算法原理讲解，包括线性回归、决策树、SVM、神经网络等算法的数学推导和代码实现。', 'https://picsum.photos/seed/mldoc/400/300', '人工智能', 'AI,机器学习,算法,深度学习', 1, 15728640, 456, 2789, 1, 1, 1),
('电子电路设计手册', 'DOCUMENT', '电子电路设计基础教材，包括模拟电路、数字电路、PCB设计、常用芯片手册等内容，适合电子工程专业学生。', 'https://picsum.photos/seed/circuitdoc/400/300', '电子工程', '电路设计,PCB,模拟电路,数字电路', 1, 20971520, 189, 1456, 0, 1, 1);

-- 文档资源详情（使用公开可用的示例PDF）
INSERT INTO `t_resource_document` (`resource_id`, `document_url`, `format`, `page_count`, `preview_url`) VALUES
(16, 'https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf', 'pdf', 5, 'https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf'),
(17, 'https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf', 'pdf', 5, NULL),
(18, 'https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf', 'pdf', 5, NULL),
(19, 'https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf', 'pdf', 5, NULL),
(20, 'https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf', 'pdf', 5, NULL),
(21, 'https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf', 'pdf', 5, NULL);

-- ========================================
-- 验证数据
-- ========================================
SELECT resource_type, COUNT(*) as count FROM t_resource WHERE is_deleted = 0 GROUP BY resource_type;
