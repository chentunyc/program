USE virtual_training_platform;

-- 清除可能存在的测试数据（可选）
DELETE FROM t_resource_simulation WHERE id = 1;
DELETE FROM t_resource_video WHERE id = 1;
DELETE FROM t_resource_audio WHERE id = 1;
DELETE FROM t_resource_document WHERE id = 1;
DELETE FROM t_resource WHERE id IN (1, 2, 3, 4);

-- 虚拟仿真资源（第1类）
INSERT INTO t_resource (id, resource_name, resource_type, description, cover_image, category, tags, uploader_id, file_size, download_count, view_count, is_shared, status, create_time, update_time, is_deleted) VALUES
(1, '化学实验室虚拟仿真系统', 'SIMULATION', '完整的化学实验虚拟仿真，包含多种实验场景', '/images/resources/simulation/chem-lab.png', '化学实验', '虚拟仿真,化学,实验', 2, 1024000000, 156, 1250, 0, 1, NOW(), NOW(), 0);

INSERT INTO t_resource_simulation (id, resource_id, simulation_url, technology, support_platform, min_requirement) VALUES
(1, 1, '/simulations/chem-lab.html', 'Unity3D, WebGL', 'PC, Web', 'CPU: i5, RAM: 8GB, GPU: GTX1050');

-- 视频资源（第2类）
INSERT INTO t_resource (id, resource_name, resource_type, description, cover_image, category, tags, uploader_id, file_size, download_count, view_count, is_shared, status, create_time, update_time, is_deleted) VALUES
(2, 'Java编程基础教程', 'VIDEO', '完整的Java编程入门视频教程', '/images/resources/video/java-course.png', '编程教学', '视频教程,Java,编程', 3, 2048000000, 234, 1890, 1, 1, NOW(), NOW(), 0);

-- 修正：移除不存在的 has_subtitle 字段
INSERT INTO t_resource_video (id, resource_id, video_url, duration, resolution, format) VALUES
(1, 2, '/videos/java-course.mp4', 3600, '1920x1080', 'MP4');

-- 音频资源（第3类）
INSERT INTO t_resource (id, resource_name, resource_type, description, cover_image, category, tags, uploader_id, file_size, download_count, view_count, is_shared, status, create_time, update_time, is_deleted) VALUES
(3, '英语听力训练', 'AUDIO', '专业英语听力训练音频材料', '/images/resources/audio/english-listening.png', '语言学习', '音频,英语,听力', 2, 256000000, 89, 670, 1, 1, NOW(), NOW(), 0);

-- 修正：移除不存在的 sample_rate 字段
INSERT INTO t_resource_audio (id, resource_id, audio_url, duration, format, bitrate) VALUES
(1, 3, '/audios/english-listening.mp3', 1800, 'MP3', 128);

-- 文档资源（第4类）
INSERT INTO t_resource (id, resource_name, resource_type, description, cover_image, category, tags, uploader_id, file_size, download_count, view_count, is_shared, status, create_time, update_time, is_deleted) VALUES
(4, '软件工程导论讲义', 'DOCUMENT', '软件工程基础概念和原理讲义', '/images/resources/document/software-engineering.png', '软件工程', '文档,讲义,软件工程', 2, 25600000, 145, 890, 1, 1, NOW(), NOW(), 0);

-- 修正：移除不存在的 word_count 字段
INSERT INTO t_resource_document (id, resource_id, document_url, format, page_count, preview_url) VALUES
(1, 4, '/documents/software-engineering.pdf', 'PDF', 156, '/documents/software-engineering.pdf');