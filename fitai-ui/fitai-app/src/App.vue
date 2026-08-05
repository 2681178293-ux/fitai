<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from './store/user'
import { useAiCapabilitiesStore } from './store/aiCapabilities'

onLaunch(() => {
  console.log('FitAI App Launch')
  const userStore = useUserStore()
  userStore.checkLogin()
  useAiCapabilitiesStore().load()
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* 主题色 */
:root {
  --primary-color: #3b82f6;
  --success-color: #22c55e;
  --warning-color: #f59e0b;
  --danger-color: #ef4444;
  --text-primary: #1f2937;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;
  --border-color: #e5e7eb;
  --bg-primary: #ffffff;
  --bg-secondary: #f3f4f6;
}
</style>
