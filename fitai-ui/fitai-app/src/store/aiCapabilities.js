import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAiCapabilities } from '@/api/ai'

export const useAiCapabilitiesStore = defineStore('aiCapabilities', () => {
  const data = ref(null)
  const loaded = ref(false)

  async function load() {
    try {
      const res = await getAiCapabilities()
      data.value = res.data
    } catch (e) {
      data.value = null
    } finally {
      loaded.value = true
    }
  }

  return { data, loaded, load }
})
