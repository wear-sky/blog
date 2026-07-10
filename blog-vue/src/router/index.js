import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue')
  },
  {
    path: '/blog/create',
    name: 'BlogCreate',
    component: () => import('@/views/BlogEditorView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/blog/:id/edit',
    name: 'BlogEdit',
    component: () => import('@/views/BlogEditorView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/blog/:id',
    name: 'BlogDetail',
    component: () => import('@/views/BlogDetailView.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/SearchView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !getToken()) {
    next('/login')
  } else {
    next()
  }
})

export default router
