<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { House, VideoPlay, PictureFilled, ShoppingCart, User, TrendCharts, List, Bowl, UserFilled } from '@element-plus/icons-vue'
import AiChatWidget from '@/components/AiChatWidget.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

// 导航菜单（动作库放训练前，保证 /training/exercise 高亮动作库）
const navItems = [
  { path: '/', name: '首页', icon: House },
  { path: '/training/exercise', name: '动作库', icon: List },
  { path: '/training', name: '训练', icon: TrendCharts },
  { path: '/course', name: '课程', icon: VideoPlay },
  { path: '/coach', name: '私教', icon: UserFilled },
  { path: '/diet', name: '饮食', icon: Bowl },
  { path: '/community', name: '社区', icon: PictureFilled },
  { path: '/mall', name: '商城', icon: ShoppingCart }
]

const activeNav = computed(() => {
  const path = route.path
  if (path === '/') return '/'
  const nav = navItems.find(item => path.startsWith(item.path) && item.path !== '/')
  return nav?.path || '/'
})

function handleNavClick(path) {
  router.push(path)
}

function goToLogin() {
  router.push({ name: 'Login' })
}

function goToProfile() {
  router.push({ name: 'Profile' })
}

function goToCart() {
  router.push({ name: 'Cart' })
}

async function handleLogout() {
  await userStore.doLogout()
  router.push('/')
}
</script>

<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <header class="sticky top-0 z-50 bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <!-- Logo -->
        <div class="flex items-center gap-8">
          <div class="text-2xl font-bold text-emerald-500 cursor-pointer" @click="handleNavClick('/')">
            FitAI
          </div>
          
          <!-- 导航菜单 -->
          <nav class="hidden md:flex gap-1">
            <button
              v-for="item in navItems"
              :key="item.path"
              @click="handleNavClick(item.path)"
              class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
              :class="activeNav === item.path 
                ? 'bg-emerald-50 text-emerald-600' 
                : 'text-gray-600 hover:bg-gray-100'"
            >
              {{ item.name }}
            </button>
          </nav>
        </div>
        
        <!-- 右侧操作区 -->
        <div class="flex items-center gap-4">
          <!-- 购物车 -->
          <el-badge :value="cartStore.totalCount" :hidden="!cartStore.totalCount" :max="99">
            <el-button :icon="ShoppingCart" circle @click="goToCart" />
          </el-badge>
          
          <!-- 用户信息 -->
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <div class="flex items-center gap-2 cursor-pointer">
                <el-avatar :size="32" :src="userStore.avatar" />
                <span class="hidden md:inline text-sm">{{ userStore.nickname }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="goToLogin">登录</el-button>
          </template>
        </div>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="flex-1">
      <router-view />
    </main>
    
    <!-- 底部 -->
    <footer class="bg-gray-800 text-gray-400 py-8">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <p class="text-sm">FitAI 智能健身平台 - 让运动更科学</p>
        <p class="text-xs mt-2">仅供学习演示，非商业用途</p>
      </div>
    </footer>
    
    <!-- 移动端底部导航 -->
    <nav class="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 z-50">
      <div class="flex justify-around py-2">
        <button
          v-for="item in navItems"
          :key="item.path"
          @click="handleNavClick(item.path)"
          class="flex flex-col items-center gap-1 px-4 py-1"
          :class="activeNav === item.path ? 'text-emerald-500' : 'text-gray-500'"
        >
          <component :is="item.icon" class="w-5 h-5" />
          <span class="text-xs">{{ item.name }}</span>
        </button>
        <button
          @click="userStore.isLoggedIn ? goToProfile() : goToLogin()"
          class="flex flex-col items-center gap-1 px-4 py-1"
          :class="route.name === 'Profile' ? 'text-emerald-500' : 'text-gray-500'"
        >
          <User class="w-5 h-5" />
          <span class="text-xs">我的</span>
        </button>
      </div>
    </nav>

    <!-- AI 对话助手 -->
    <AiChatWidget />
  </div>
</template>
