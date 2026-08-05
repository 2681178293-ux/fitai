<script setup>
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAiCapabilitiesStore } from '@/stores/aiCapabilities'

const route = useRoute()

function loadCapabilitiesIfNeeded() {
  if (route.path === '/login' || route.path === '/register') {
    return
  }
  useAiCapabilitiesStore().load()
}

onMounted(() => {
  loadCapabilitiesIfNeeded()
})

watch(
  () => route.path,
  () => {
    loadCapabilitiesIfNeeded()
  }
)
</script>

<template>
  <router-view />
</template>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
