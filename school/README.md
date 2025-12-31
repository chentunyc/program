# 虚拟仿真实训教学管理平台 - 前端项目

## 项目简介

基于 Vue 3 + Vite + ElementPlus 开发的虚拟仿真实训教学管理平台前端项目。

## 技术栈

- **Vue 3**: 渐进式JavaScript框架
- **Vite**: 新一代前端构建工具
- **ElementPlus**: Vue 3组件库
- **Pinia**: Vue 3状态管理
- **Vue Router**: 路由管理
- **Axios**: HTTP请求库
- **SCSS**: CSS预处理器
- **ECharts**: 数据可视化

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

## 快速开始

### 1. 安装依赖

```bash
npm install
# 或
yarn install
# 或
pnpm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问: http://localhost:5173

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产版本

```bash
npm run preview
```

## 开发规范

### 1. 组件命名

- 组件文件名使用 PascalCase: `UserCard.vue`
- 组件名称使用 PascalCase: `<UserCard />`
- 路由名称使用 PascalCase: `name: 'UserCenter'`

### 2. API请求

所有API请求统一放在 `src/api` 目录下,按模块划分:

```javascript
// src/api/user.js
import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/v1/users',
    method: 'get',
    params
  })
}
```

### 3. 路由配置

```javascript
{
  path: '/user',
  name: 'User',
  component: () => import('@/views/user/index.vue'),
  meta: {
    title: '用户管理',
    icon: 'User',
    requireAuth: true  // 需要认证
  }
}
```

### 4. 状态管理

使用 Pinia 进行状态管理:

```javascript
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)

  const setUserInfo = (info) => {
    userInfo.value = info
  }

  return {
    userInfo,
    setUserInfo
  }
})
```

### 5. 样式规范

- 使用 SCSS 编写样式
- 使用 scoped 限定组件样式作用域
- 公共样式放在 `src/styles` 目录

```vue
<style lang="scss" scoped>
.user-card {
  padding: 20px;

  &__header {
    font-size: 18px;
    font-weight: bold;
  }
}
</style>
```

## 环境变量

创建 `.env.development` 和 `.env.production` 文件:

```env
# .env.development
VITE_API_BASE_URL=/api

# .env.production
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## 功能模块

### 已完成

- [x] 项目基础架构
- [x] 路由配置
- [x] 状态管理(Pinia)
- [x] Axios请求封装
- [x] 权限路由守卫
- [x] ElementPlus集成

### 待实现

- [ ] 登录页面
- [ ] 布局组件(顶部导航、侧边栏、面包屑)
- [ ] 首页
- [ ] 新闻公告模块
- [ ] 用户中心模块
- [ ] 资源中心模块
- [ ] 实训中心模块
- [ ] 实验室模块
- [ ] 共享开放模块
- [ ] 权限指令
- [ ] 文件上传组件
- [ ] 富文本编辑器

## 核心特性

### 1. 自动导入

使用 `unplugin-auto-import` 和 `unplugin-vue-components` 自动导入:

- Vue API: `ref`, `computed`, `watch` 等
- Vue Router API: `useRouter`, `useRoute`
- ElementPlus 组件

### 2. 路由权限

基于 `meta.requireAuth` 字段控制页面访问权限。

### 3. 请求拦截

- 自动添加 Token
- 统一错误处理
- 响应数据格式化

### 4. 响应式设计

支持桌面端和移动端自适应。

## 常见问题

### 1. 依赖安装失败

尝试切换npm镜像:

```bash
npm config set registry https://registry.npmmirror.com
```

### 2. 开发服务器启动失败

检查端口是否被占用,修改 `vite.config.js` 中的 `port` 配置。

### 3. 请求跨域

确保 `vite.config.js` 中的代理配置正确。

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 性能优化

- 路由懒加载
- 组件按需引入
- 代码分割
- Gzip压缩
- 图片懒加载

## 联系方式

- 项目负责人: Training Team
- 邮箱: dev@example.com
