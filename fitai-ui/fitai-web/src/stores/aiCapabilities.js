import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

/**
 * GET /ai/capabilities：能力开关与入口说明，供设置页或 AI 相关页展示
 */
export const useAiCapabilitiesStore = defineStore('aiCapabilities', () => {
  const data = ref(null)
  const loaded = ref(false)

  async function load() {
    try {
      data.value = await request.get('/ai/capabilities')
    } catch (e) {
      data.value = null
    } finally {
      loaded.value = true
    }
  }

  return { data, loaded, load }
})
