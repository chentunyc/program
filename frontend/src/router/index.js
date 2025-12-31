import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({ showSpinner: false })

/**
 * 路由配置
 * 采用懒加载方式提升性能
 */
const routes = [
  {
    path: '/',
    redirect: '/home',
    meta: {
      hidden: true
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requireAuth: false,
      hidden: true
    }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/home',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: {
          title: '首页',
          icon: 'HomeFilled',
          requireAuth: true
        }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/news/index.vue'),
        meta: {
          title: '新闻公告',
          icon: 'Bell',
          requireAuth: true
        }
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('@/views/news/detail.vue'),
        meta: {
          title: '新闻详情',
          requireAuth: true,
          hidden: true
        }
      },
      {
        path: 'user-center',
        name: 'UserCenter',
        component: () => import('@/views/user-center/index.vue'),
        meta: {
          title: '用户中心',
          icon: 'User',
          requireAuth: true
        }
      },
      {
        path: 'resource',
        name: 'Resource',
        component: () => import('@/views/resource/index.vue'),
        meta: {
          title: '资源中心',
          icon: 'Folder',
          requireAuth: true
        }
      },
      {
        path: 'resource/:id',
        name: 'ResourceDetail',
        component: () => import('@/views/resource/detail.vue'),
        meta: {
          title: '资源详情',
          requireAuth: true,
          hidden: true
        }
      },
      {
        path: 'training',
        name: 'Training',
        component: () => import('@/views/training/index.vue'),
        meta: {
          title: '实训中心',
          icon: 'Notebook',
          requireAuth: true
        }
      },
      {
        path: 'laboratory',
        name: 'Laboratory',
        component: () => import('@/views/laboratory/index.vue'),
        meta: {
          title: '实验室',
          icon: 'House',
          requireAuth: true
        }
      },
      {
        path: 'share',
        name: 'Share',
        component: () => import('@/views/share/index.vue'),
        meta: {
          title: '共享开放',
          icon: 'Share',
          requireAuth: true
        }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '404',
      requireAuth: false,
      hidden: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: {
      hidden: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

/**
 * 全局前置守卫 - 权限验证
 */
router.beforeEach((to, from, next) => {
  NProgress.start()

  // 设置页面标题
  document.title = to.meta.title
    ? `${to.meta.title} - 虚拟仿真实训教学管理平台`
    : '虚拟仿真实训教学管理平台'

  const userStore = useUserStore()

  // 如果需要认证
  if (to.meta.requireAuth) {
    if (userStore.token) {
      // 已登录
      if (!userStore.userInfo) {
        // 获取用户信息
        userStore
          .getUserInfo()
          .then(() => {
            next()
          })
          .catch(() => {
            ElMessage.error('获取用户信息失败')
            userStore.logout()
            next('/login')
          })
      } else {
        next()
      }
    } else {
      // 未登录,跳转到登录页
      ElMessage.warning('请先登录')
      next(`/login?redirect=${to.fullPath}`)
    }
  } else {
    // 不需要认证
    if (to.path === '/login' && userStore.token) {
      // 已登录,访问登录页,跳转到首页
      next('/home')
    } else {
      next()
    }
  }
})

/**
 * 全局后置钩子
 */
router.afterEach(() => {
  NProgress.done()
})

export default router
