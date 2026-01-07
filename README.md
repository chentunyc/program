# 虚拟仿真实训教学管理及资源共享云平台

## 项目简介

这是一个基于 Spring Boot + MyBatis + Vue 3 + ElementPlus 开发的虚拟仿真实训教学管理及资源共享云平台,旨在为学校提供一个集教学管理、资源共享、实训管理于一体的综合平台。


## 项目结构

```
program/
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

系统设计了五种用户角色:

### 1. 访客 (GUEST)
- 访问资源中心
- 访问个人中心
- 查看新闻公告

### 2. 学生 (STUDENT)
- 访客权限 +
- 参与课程学习和实训

### 3. 教师 (TEACHER)
- 学生权限 +
- 课程管理
- 实训任务发布
- 学生成绩管理
- 教学数据导出

### 4. 资源管理员 (DATA_ADMIN)
- 学生权限 +
- 资源审核

### 5. 管理员 (ADMIN)
- 全部权限
- 综合管理
