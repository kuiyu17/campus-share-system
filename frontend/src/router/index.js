import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/items',
    name: 'Items',
    component: () => import('../views/Items.vue')
  },
  {
    path: '/item/detail/:id',
    name: 'ItemDetail',
    component: () => import('../views/ItemDetail.vue')
  },
  {
    path: '/borrow',
    name: 'Borrow',
    component: () => import('../views/Borrow.vue')
  },
  {
    path: '/borrow/history',
    name: 'BorrowHistory',
    component: () => import('../views/BorrowHistory.vue')
  },
  {
    path: '/manage/items',
    name: 'ManageItems',
    component: () => import('../views/ManageItems.vue')
  },
  {
    path: '/manage/requests',
    name: 'ManageRequests',
    component: () => import('../views/ManageRequests.vue')
  },
  { path: '/ai/recommendations', name: 'AIRecommendations', component: () => import('../views/AIRecommendations.vue') },
  { path: '/ai/dashboard', name: 'AIDashboard', component: () => import('../views/AIDashboard.vue') },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router