import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { title: '注册' },
  },
  {
    path: '/goods',
    name: 'GoodsList',
    component: () => import('@/views/GoodsListView.vue'),
    meta: { title: '商品列表' },
  },
  {
    path: '/goods/:id',
    name: 'GoodsDetail',
    component: () => import('@/views/GoodsDetailView.vue'),
    meta: { title: '商品详情' },
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/CartView.vue'),
    meta: { title: '购物车', requiresAuth: true },
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/views/OrderListView.vue'),
    meta: { title: '我的订单', requiresAuth: true },
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/UserCenterView.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/FavoritesView.vue'),
    meta: { title: '我的收藏', requiresAuth: true },
  },
  {
    path: '/seller',
    name: 'SellerCenter',
    component: () => import('@/views/SellerCenterView.vue'),
    meta: { title: '卖家中心', requiresAuth: true, requiresSeller: true },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/AdminView.vue'),
    meta: { title: '后台管理', requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/cultural',
    name: 'CulturalContent',
    component: () => import('@/views/CulturalContentView.vue'),
    meta: { title: '文化资讯' },
  },
  {
    path: '/cultural-content/:id',
    name: 'CulturalContentDetail',
    component: () => import('@/views/CulturalContentDetailView.vue'),
    meta: { title: '资讯详情' },
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/AboutView.vue'),
    meta: { title: '关于我们' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || '豫见好物'} - 河南文创产品销售平台`
  
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  
  // 检查是否需要卖家权限
  if (to.meta.requiresSeller) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.role !== 'seller') {
      next({ name: 'Home' })
      return
    }
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.role !== 'admin') {
      next({ name: 'Home' })
      return
    }
  }
  
  next()
})

export default router
