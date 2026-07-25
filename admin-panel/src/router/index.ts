import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据看板' },
      },
      {
        path: 'user',
        name: 'UserList',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'house',
        name: 'HouseList',
        component: () => import('@/views/house/index.vue'),
        meta: { title: '房源管理' },
      },
      {
        path: 'house/audit',
        name: 'HouseAudit',
        component: () => import('@/views/house/audit.vue'),
        meta: { title: '房源审核' },
      },
      {
        path: 'order',
        name: 'OrderList',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理' },
      },
      {
        path: 'message',
        name: 'MessageManage',
        component: () => import('@/views/message/index.vue'),
        meta: { title: '消息管理' },
      },
      {
        path: 'contract',
        name: 'ContractList',
        component: () => import('@/views/contract/index.vue'),
        meta: { title: '合同管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')

  if (!to.meta.public && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
