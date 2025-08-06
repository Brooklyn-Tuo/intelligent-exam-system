import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import ChangePasswordView from '../views/ChangePasswordView.vue'
import DashboardView from '@/views/DashboardView.vue'
import Layout from '@/layout/Layout.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/change-password', component: ChangePasswordView },
  { path: '/dashboard', component: () => import('@/views/DashboardView.vue') },
  {
    path: '/',
    component: Layout,
    children: [
      { path: '/dashboard', component: DashboardView },
      // 其他子路由可继续添加
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
