import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'training',
        name: 'Training',
        component: () => import('@/views/training/index.vue')
      },
      {
        path: 'training/plan/:id',
        name: 'TrainingPlan',
        component: () => import('@/views/training/plan.vue')
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('@/views/course/index.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'course/:id',
        name: 'CourseDetail',
        component: () => import('@/views/course/detail.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'coach',
        name: 'Coach',
        component: () => import('@/views/coach/index.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'coach/my-booking',
        name: 'MyBooking',
        component: () => import('@/views/coach/my-booking.vue')
      },
      {
        path: 'coach/:id',
        name: 'CoachDetail',
        component: () => import('@/views/coach/detail.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'mall',
        name: 'Mall',
        component: () => import('@/views/mall/index.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'mall/product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/mall/product.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/mall/cart.vue')
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/mall/order.vue')
      },
      {
        path: 'order/confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/mall/order-confirm.vue')
      },
      {
        path: 'order/seckill/:orderNo',
        name: 'SeckillOrderDetail',
        component: () => import('@/views/mall/order-seckill-detail.vue')
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/mall/order-detail.vue')
      },
      {
        path: 'mall/order-result',
        name: 'OrderResult',
        component: () => import('@/views/mall/order-result.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'seckill',
        name: 'Seckill',
        component: () => import('@/views/seckill/index.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'seckill/:id',
        name: 'SeckillDetail',
        component: () => import('@/views/seckill/detail.vue')
      },
      {
        path: 'diet',
        name: 'Diet',
        component: () => import('@/views/diet/index.vue')
      },
      {
        path: 'community',
        name: 'Community',
        component: () => import('@/views/community/index.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue')
      },
      {
        path: 'profile/address',
        name: 'Address',
        component: () => import('@/views/profile/address.vue')
      },
      {
        path: 'profile/settings',
        name: 'Settings',
        component: () => import('@/views/profile/settings.vue')
      },
      {
        path: 'profile/favorites',
        name: 'Favorites',
        component: () => import('@/views/profile/favorites.vue')
      },
      {
        path: 'course/history',
        name: 'CourseHistory',
        component: () => import('@/views/course/history.vue')
      },
      {
        path: 'training/history',
        name: 'TrainingHistory',
        component: () => import('@/views/training/history.vue')
      },
      {
        path: 'training/exercise',
        name: 'ExerciseList',
        component: () => import('@/views/training/exercise-list.vue')
      },
      {
        path: 'training/exercise/:id',
        name: 'ExerciseDetail',
        component: () => import('@/views/training/exercise-detail.vue')
      },
      {
        path: 'challenge',
        name: 'Challenge',
        component: () => import('@/views/challenge/index.vue'),
        meta: { noAuth: true }
      },
      {
        path: 'challenge/:id',
        name: 'ChallengeDetail',
        component: () => import('@/views/challenge/detail.vue')
      },
      {
        path: 'ranking',
        name: 'Ranking',
        component: () => import('@/views/ranking/index.vue'),
        meta: { noAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 不需要登录的页面直接放行
  if (to.meta.noAuth) {
    next()
    return
  }
  
  // 需要登录但未登录，跳转登录页
  if (!userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  next()
})

export default router
