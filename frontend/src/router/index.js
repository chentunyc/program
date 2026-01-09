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
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: {
      title: '注册',
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
          requireAuth: false,  // 允许未登录访问
          showInPublic: true   // 未登录时也显示在菜单中
        }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/news/index.vue'),
        meta: {
          title: '新闻公告',
          icon: 'Bell',
          requireAuth: false,  // 允许未登录访问
          showInPublic: true   // 未登录时也显示在菜单中
        }
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('@/views/news/detail.vue'),
        meta: {
          title: '新闻详情',
          requireAuth: false,  // 允许未登录访问
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
      {
        path: 'training/:id',
        name: 'TrainingDetail',
        component: () => import('@/views/training/detail.vue'),
        meta: {
          title: '实训详情',
          requireAuth: true,
          hidden: true,
          excludeRoles: ['GUEST']
        }
      },
      // 平台管理 - 多角色可访问
      {
        path: 'admin',
        name: 'Admin',
        redirect: '/admin/users',
        meta: {
          title: '平台管理',
          icon: 'Setting',
          requireAuth: true,
          // 允许管理员、教师、资源管理员访问
          allowedRoles: ['ADMIN', 'TEACHER', 'DATA_ADMIN']
        },
        children: [
          // === 仅管理员可见 ===
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/users/index.vue'),
            meta: {
              title: '用户管理',
              icon: 'User',
              requireAuth: true,
              allowedRoles: ['ADMIN']
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
              allowedRoles: ['ADMIN']
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
              allowedRoles: ['ADMIN']
            }
          },
          {
            path: 'data',
            name: 'AdminData',
            component: () => import('@/views/admin/data/index.vue'),
            meta: {
              title: '数据管理',
              icon: 'DataAnalysis',
              requireAuth: true,
              allowedRoles: ['TEACHER']
            }
          },
          // === 管理员和教师可见 ===
          {
            path: 'training',
            name: 'AdminTraining',
            redirect: '/admin/training/project',
            meta: {
              title: '实训管理',
              icon: 'Notebook',
              requireAuth: true,
              allowedRoles: ['ADMIN', 'TEACHER']
            },
            children: [
              {
                path: 'project',
                name: 'AdminTrainingProject',
                component: () => import('@/views/admin/training/project/index.vue'),
                meta: {
                  title: '项目管理',
                  icon: 'FolderOpened',
                  requireAuth: true,
                  allowedRoles: ['ADMIN', 'TEACHER']
                }
              },
              {
                path: 'task',
                name: 'AdminTrainingTask',
                component: () => import('@/views/admin/training/task/index.vue'),
                meta: {
                  title: '任务管理',
                  icon: 'List',
                  requireAuth: true,
                  allowedRoles: ['ADMIN', 'TEACHER']
                }
              },
              {
                path: 'grading',
                name: 'AdminTrainingGrading',
                component: () => import('@/views/admin/training/grading/index.vue'),
                meta: {
                  title: '实训批改',
                  icon: 'EditPen',
                  requireAuth: true,
                  allowedRoles: ['TEACHER']
                }
              }
            ]
          },
          // === 资源管理子模块 ===
          {
            path: 'resource',
            name: 'AdminResource',
            redirect: '/admin/resource/edit',
            meta: {
              title: '资源管理',
              icon: 'Folder',
              requireAuth: true,
              allowedRoles: ['ADMIN', 'TEACHER', 'DATA_ADMIN']
            },
            children: [
              {
                path: 'edit',
                name: 'AdminResourceEdit',
                component: () => import('@/views/admin/resource/edit.vue'),
                meta: {
                  title: '资源编辑',
                  icon: 'Edit',
                  requireAuth: true,
                  allowedRoles: ['DATA_ADMIN']
                }
              },
              {
                path: 'audit',
                name: 'AdminResourceAudit',
                component: () => import('@/views/admin/resource/audit.vue'),
                meta: {
                  title: '资源审核',
                  icon: 'Checked',
                  requireAuth: true,
                  allowedRoles: ['DATA_ADMIN']
                }
              },
              {
                path: 'upload',
                name: 'AdminResourceUpload',
                component: () => import('@/views/admin/resource/upload.vue'),
                meta: {
                  title: '资源上传',
                  icon: 'Upload',
                  requireAuth: true,
                  allowedRoles: ['ADMIN', 'TEACHER']
                }
              }
            ]
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
 * 检查用户是否有权限访问路由（基于allowedRoles）
 */
const hasRoutePermission = (route, userStore) => {
  const allowedRoles = route.meta?.allowedRoles
  // 如果没有设置allowedRoles，则允许所有人访问
  if (!allowedRoles || allowedRoles.length === 0) {
    return true
  }
  // 检查用户是否拥有任一允许的角色
  return allowedRoles.some(role => userStore.hasRole(role))
}

/**
 * 全局前置守卫 - 权限验证
 */
router.beforeEach(async (to, from, next) => {
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
            // 检查allowedRoles权限
            if (!hasRoutePermission(to, userStore)) {
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
        // 检查allowedRoles权限
        if (!hasRoutePermission(to, userStore)) {
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
    // 不需要认证的页面
    if ((to.path === '/login' || to.path === '/register') && userStore.token) {
      // 已登录,访问登录页或注册页,跳转到首页
      next('/home')
    } else {
      // 如果用户已登录但还没有获取用户信息，尝试获取（用于显示正确的菜单）
      if (userStore.token && !userStore.userInfo) {
        try {
          await userStore.getUserInfo()
        } catch (error) {
          // 获取失败不影响页面访问，只是菜单可能不完整
          console.warn('获取用户信息失败，将以访客身份访问')
        }
      }
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
