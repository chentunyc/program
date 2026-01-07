# 虚拟仿真实训教学管理平台 - 前端项目

## 项目简介

基于 Vue 3 + Vite + ElementPlus 开发的虚拟仿真实训教学管理平台前端项目。

## 项目结构

```
frontend/
├── public/                       # 静态资源
├── src/
│   ├── api/                      # API接口定义
│   │   ├── auth.js              # 认证接口
│   │   ├── news.js              # 新闻接口(待实现)
│   │   ├── user.js              # 用户接口(待实现)
│   │   └── ...
│   ├── assets/                   # 资源文件
│   │   ├── images/              # 图片
│   │   ├── icons/               # 图标
│   │   └── fonts/               # 字体
│   ├── components/               # 公共组件
│   │   ├── Layout/              # 布局组件(待实现)
│   │   ├── Common/              # 通用组件
│   │   └── Business/            # 业务组件
│   ├── router/                   # 路由配置
│   │   └── index.js             # 路由主文件
│   ├── store/                    # 状态管理
│   │   └── modules/
│   │       ├── user.js          # 用户状态
│   │       └── app.js           # 应用状态
│   ├── styles/                   # 样式文件
│   │   └── index.scss           # 全局样式
│   ├── utils/                    # 工具函数
│   │   ├── request.js           # axios封装
│   │   ├── auth.js              # 认证工具
│   │   └── validate.js          # 表单验证(待实现)
│   ├── views/                    # 页面组件
│   │   ├── login/               # 登录页(待实现)
│   │   ├── home/                # 首页(待实现)
│   │   ├── news/                # 新闻公告(待实现)
│   │   ├── user-center/         # 用户中心(待实现)
│   │   ├── resource/            # 资源中心(待实现)
│   │   ├── training/            # 实训中心(待实现)
│   │   ├── laboratory/          # 实验室(待实现)
│   │   └── share/               # 共享开放(待实现)
│   ├── App.vue                   # 根组件
│   └── main.js                   # 入口文件
├── index.html                    # HTML模板
├── vite.config.js               # Vite配置
├── package.json                 # 依赖配置
└── README.md                    # 项目说明

```
