# 虚拟仿真实训教学管理及资源共享云平台

## 项目简介

这是一个基于 Spring Boot + MyBatis + Vue 3 + ElementPlus 开发的虚拟仿真实训教学管理及资源共享云平台,旨在为学校提供一个集教学管理、资源共享、实训管理于一体的综合平台。

## 技术架构

### 后端技术栈

- **Spring Boot** 3.2.1 - 应用框架
- **MyBatis Plus** 3.5.5 - ORM框架
- **Spring Security** - 安全框架
- **JWT** - Token认证
- **MySQL** 8.0+ - 数据库
- **Redis** - 缓存
- **Druid** - 数据库连接池
- **Knife4j** - API文档

### 前端技术栈

- **Vue 3** - 前端框架
- **Vite** - 构建工具
- **ElementPlus** - UI组件库
- **Pinia** - 状态管理
- **Vue Router** - 路由管理
- **Axios** - HTTP客户端
- **ECharts** - 数据可视化

## 项目结构

```
virtual-training-platform/
├── database/                     # 数据库文件
│   └── schema.sql               # 数据库建表脚本
├── backend/                      # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/training/
│   │   │   │       ├── common/  # 公共模块
│   │   │   │       └── module/  # 业务模块
│   │   │   └── resources/
│   │   │       ├── mapper/      # MyBatis映射文件
│   │   │       └── application.yml
│   │   └── test/
│   ├── pom.xml
│   └── README.md
├── frontend/                     # 前端项目
│   ├── src/
│   │   ├── api/                 # API接口
│   │   ├── components/          # 组件
│   │   ├── router/              # 路由
│   │   ├── store/               # 状态管理
│   │   ├── styles/              # 样式
│   │   ├── utils/               # 工具函数
│   │   ├── views/               # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
├── 项目设计文档.md               # 详细设计文档
└── README.md                     # 本文件
```

## 用户角色

系统设计了三种用户角色:

### 1. 学生 (STUDENT)
- 访问所有共享资源
- 参与课程学习和实训
- 预约实验室
- 查看新闻公告

### 2. 教师 (TEACHER)
- 学生权限 +
- 课程管理
- 实训任务发布
- 学生成绩管理
- 教学数据分析
- 实验室申请

### 3. 管理员 (ADMIN)
- 全部权限
- 用户管理
- 系统配置
- 资源审核
- 数据统计

## 核心功能模块

### 阶段一:基础功能(已规划)

#### 1. 认证授权
- [x] 数据库设计
- [ ] 用户登录/登出
- [ ] JWT Token认证
- [ ] 角色权限控制

#### 2. 首页
- [x] 数据库设计
- [ ] 快捷入口
- [ ] 数据概览
- [ ] 最新动态

#### 3. 新闻公告
- [x] 数据库设计
- [ ] 公告通知列表
- [ ] 新闻资讯列表
- [ ] 详情查看

#### 4. 用户中心
- [x] 数据库设计
- [ ] 个人信息管理
- [ ] 我的收藏
- [ ] 我的课程
- [ ] 我的实验
- [ ] 消息通知

#### 5. 资源中心
- [x] 数据库设计
- [ ] 虚拟仿真资源
- [ ] 视频资源
- [ ] 音频资源
- [ ] 文档资源
- [ ] 资源下载

#### 6. 实训中心
- [x] 数据库设计
- [ ] 课程列表
- [ ] 实训任务
- [ ] 实训记录
- [ ] 成绩查看

#### 7. 实验室
- [x] 数据库设计
- [ ] 实验室列表
- [ ] 在线预约
- [ ] 使用记录

#### 8. 共享开放
- [x] 数据库设计
- [ ] 资源共享
- [ ] 实验室开放

### 阶段二:教师功能(预留接口)

- 设备监控
- 资源监控
- 教学监控
- 数据概览
- 实训管理
- 教学计划

### 阶段三:管理员功能(预留接口)

- 综合管理
- 系统信息
- 数据字典
- 系统日志
- 资源管理

## 数据库设计

已完成完整的数据库设计,包含30+张表:

### 核心表(已创建)

- **用户权限**: t_user, t_role, t_permission, t_user_role, t_role_permission
- **新闻公告**: t_news, t_notice
- **用户中心**: t_user_profile, t_user_favorite, t_user_message
- **资源中心**: t_resource, t_resource_simulation, t_resource_video, t_resource_audio, t_resource_document
- **实训中心**: t_course, t_training_task, t_training_record, t_user_course
- **实验室**: t_laboratory, t_lab_reservation, t_lab_usage_record
- **共享开放**: t_shared_lab
- **教师功能**: t_device_monitor, t_resource_monitor, t_teaching_monitor等(预留)
- **管理员功能**: t_data_dict, t_system_log, t_platform_setting等(预留)

详细设计见: `database/schema.sql`

## 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+

### 1. 初始化数据库

```bash
mysql -u root -p < database/schema.sql
```

### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端访问地址: http://localhost:8080/api
API文档地址: http://localhost:8080/api/doc.html

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端访问地址: http://localhost:5173

### 4. 默认账号

- 用户名: admin
- 密码: admin123

## 开发规范

### 后端规范

1. **包结构**: 按业务模块划分(auth, user, news, resource等)
2. **命名规范**: 遵循阿里巴巴Java开发手册
3. **API规范**: RESTful API设计
4. **响应格式**: 统一使用Result封装
5. **异常处理**: 全局异常处理器统一处理

### 前端规范

1. **组件命名**: PascalCase
2. **文件命名**: kebab-case
3. **样式**: 使用SCSS + scoped
4. **状态管理**: Pinia
5. **请求封装**: 统一使用axios实例

### 数据库规范

1. **表命名**: t_模块_实体 (如: t_user_info)
2. **字段命名**: 小写下划线 (如: user_name)
3. **公共字段**: id, create_time, update_time, create_by, update_by, is_deleted
4. **逻辑删除**: 使用is_deleted字段

## 设计模式应用

### SOLID原则

- ✅ **单一职责**: 每个类/组件只负责单一功能
- ✅ **开闭原则**: 使用接口和抽象类支持扩展
- ✅ **里氏替换**: 确保子类可替换父类
- ✅ **接口隔离**: 细粒度接口设计
- ✅ **依赖倒置**: 面向接口编程

### DRY原则

- ✅ BaseEntity统一公共字段
- ✅ Result统一响应格式
- ✅ 全局异常处理
- ✅ MyBatis Plus自动填充

### KISS原则

- ✅ 清晰的代码结构
- ✅ 简洁的命名
- ✅ 最小化复杂度

### YAGNI原则

- ✅ 仅实现当前需要的功能
- ✅ 预留扩展接口但不过度设计

## 项目进度

### 已完成 ✅

- [x] 项目架构设计
- [x] 完整数据库设计(含预留表)
- [x] 后端基础框架搭建
- [x] 前端基础框架搭建
- [x] 公共基础类
- [x] 统一响应格式
- [x] 全局异常处理
- [x] 跨域配置
- [x] JWT工具类
- [x] 路由权限守卫
- [x] Axios请求封装

### 待实现 📝

- [ ] Spring Security配置
- [ ] 用户认证模块(登录/注册/登出)
- [ ] 布局组件(导航/侧边栏)
- [ ] 新闻公告模块
- [ ] 用户中心模块
- [ ] 资源中心模块
- [ ] 实训中心模块
- [ ] 实验室模块
- [ ] 共享开放模块

## 下一步计划

1. **实现用户认证模块**
   - 后端: AuthController, UserService, Spring Security配置
   - 前端: 登录页面, 权限指令

2. **实现布局组件**
   - 顶部导航栏
   - 侧边菜单
   - 面包屑导航

3. **实现新闻公告模块**
   - 列表展示
   - 详情查看
   - 分类筛选

4. **逐步实现其他业务模块**

## 注意事项

### 安全相关

1. 生产环境必须修改JWT密钥
2. 数据库密码使用环境变量管理
3. 关闭生产环境的DEBUG日志
4. Redis配置密码

### 性能优化

1. 合理使用Redis缓存
2. 数据库索引优化
3. 前端路由懒加载
4. 图片资源优化

### 代码质量

1. 遵循代码规范
2. 编写单元测试
3. Code Review
4. 及时更新文档

## 文档说明

- `项目设计文档.md`: 详细的架构设计和模块说明
- `backend/README.md`: 后端项目说明
- `frontend/README.md`: 前端项目说明
- `database/schema.sql`: 数据库建表脚本(含注释)

## 贡献指南

1. Fork 本仓库
2. 创建特性分支: `git checkout -b feature/your-feature`
3. 提交更改: `git commit -m 'Add some feature'`
4. 推送分支: `git push origin feature/your-feature`
5. 提交 Pull Request

## 许可证

MIT License

## 联系方式

- 项目负责人: Training Team
- 邮箱: dev@example.com

---

**项目当前状态**: 基础架构搭建完成,进入功能开发阶段

**最后更新**: 2024-01-01
