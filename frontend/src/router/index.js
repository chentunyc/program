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
          requireAuth: true,
          // 访客不可访问实训中心
          excludeRoles: ['GUEST']
        }
      },
      // 管理员专用路由
      {
        path: 'admin',
        name: 'Admin',
        redirect: '/admin/users',
        meta: {
          title: '平台管理',
          icon: 'Setting',
          requireAuth: true,
          requireAdmin: true
        },
        children: [
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/users/index.vue'),
            meta: {
              title: '用户管理',
              icon: 'User',
              requireAuth: true,
              requireAdmin: true
            }
          },
          {
            path: 'news',
            name: 'AdminNews',
            component: () => import('@/views/admin/news/index.vue'),
            meta: {
              title: '新闻管理',
              icon: 'Tickets',
              requireAuth: true,
              requireAdmin: true
            }
          },
          {
            path: 'settings',
            name: 'AdminSettings',
            component: () => import('@/views/admin/settings/index.vue'),
            meta: {
              title: '平台设置',
              icon: 'Tools',
              requireAuth: true,
              requireAdmin: true
            }
          }
        ]
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
 * 检查用户是否被排除在路由之外
 */
const isExcludedByRole = (route, userStore) => {
  const excludeRoles = route.meta?.excludeRoles
  if (!excludeRoles || excludeRoles.length === 0) {
    return false
  }
  // 如果用户拥有任一被排除的角色，则返回true（被排除）
  return excludeRoles.some(role => userStore.hasRole(role))
}

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
            // 检查管理员权限
            if (to.meta.requireAdmin && !userStore.isAdmin()) {
              ElMessage.error('您没有权限访问该页面')
              next('/home')
            } else if (isExcludedByRole(to, userStore)) {
              // 检查是否被角色排除
              ElMessage.error('您没有权限访问该页面')
              next('/home')
            } else {
              next()
            }
          })
          .catch(() => {
            ElMessage.error('获取用户信息失败')
            userStore.logout()
            next('/login')
          })
      } else {
        // 检查管理员权限
        if (to.meta.requireAdmin && !userStore.isAdmin()) {
          ElMessage.error('您没有权限访问该页面')
          next('/home')
        } else if (isExcludedByRole(to, userStore)) {
          // 检查是否被角色排除
          ElMessage.error('您没有权限访问该页面')
          next('/home')
        } else {
          next()
        }
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
