import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// 样式
import './styles/index.scss'

// 创建应用
const app = createApp(App)

// Pinia状态管理
app.use(createPinia())

// 路由
app.use(router)

// ElementPlus
app.use(ElementPlus, {
  locale: zhCn,
  size: 'default'
})

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 挂载应用
app.mount('#app')
