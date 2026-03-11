import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getCurrentUser } from '@/api/modules/auth'
import { ElMessage } from 'element-plus'

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
    path: '/order/confirm',
    name: 'OrderConfirm',
    component: () => import('@/views/OrderConfirmView.vue'),
    meta: { title: '订单确认', requiresAuth: true },
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetailView.vue'),
    meta: { title: '订单详情', requiresAuth: true },
  },
  {
    path: '/order/pay',
    name: 'OrderPay',
    component: () => import('@/views/OrderPayView.vue'),
    meta: { title: '订单支付', requiresAuth: true },
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/UserCenterView.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/user-center',
    name: 'UserCenterAlias',
    component: () => import('@/views/UserCenterView.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/FavoriteView.vue'),
    meta: { title: '我的收藏', requiresAuth: true },
  },
  {
    path: '/seller',
    name: 'SellerCenter',
    component: () => import('@/views/SellerCenterView.vue'),
    meta: { title: '卖家中心', requiresAuth: true, requiresSeller: true },
  },
  {
    path: '/seller/apply',
    name: 'SellerApply',
    component: () => import('@/views/SellerApplyView.vue'),
    meta: { title: '申请入驻', requiresAuth: true },
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
router.beforeEach(async (to, from, next) => {
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
    try {
      // 刷新用户信息
      const userStore = useUserStore()
      const response = await getCurrentUser()
      console.log('路由守卫：API 响应', response)
      
      const userData = response.data
      console.log('路由守卫：用户数据', userData)
      console.log('路由守卫：用户角色', userData?.role)
      
      if (userData) {
        // 更新 store 中的用户信息
        userStore.setUserInfo(userData)
        console.log('路由守卫：用户信息已刷新', userData)
      }
      
      // 检查角色
      if (userData?.role !== 'seller') {
        console.warn('路由守卫：角色不是 seller，当前角色:', userData?.role)
        ElMessage.warning('您的账号还不是卖家，请先申请入驻')
        next({ name: 'SellerApply' })
        return
      }
      
      console.log('路由守卫：角色验证通过，允许访问')
    } catch (error) {
      console.error('路由守卫：获取用户信息失败:', error)
      ElMessage.error('获取用户信息失败，请重新登录')
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
