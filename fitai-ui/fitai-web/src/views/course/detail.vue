<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseById, toggleLikeCourse, toggleFavoriteCourse, recordPlay, recordView } from '@/api/course'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const course = ref(null)
const loading = ref(true)
const likeLoading = ref(false)
const favoriteLoading = ref(false)
const hasRecordedPlay = ref(false)
const videoRef = ref(null)

const isLiked = computed(() => course.value?.userLiked || false)
const isFavorited = computed(() => course.value?.userFavorited || false)

onMounted(async () => {
  const id = route.params.id
  try {
    course.value = await getCourseById(id)
  } catch (e) {
    router.push('/course')
  } finally {
    loading.value = false
  }
})

// 视频播放事件处理（模板中使用 @play 绑定）
async function handleVideoPlay() {
  // 只记录一次
  if (hasRecordedPlay.value || !course.value) return
  hasRecordedPlay.value = true
  
  console.log('视频开始播放，记录播放次数')
  
  try {
    if (userStore.isLoggedIn) {
      await recordPlay(course.value.id)
    } else {
      await recordView(course.value.id)
    }
    // 更新本地观看次数
    course.value.viewCount++
    console.log('播放次数更新成功:', course.value.viewCount)
  } catch (e) {
    console.error('记录播放失败:', e)
    hasRecordedPlay.value = false // 失败时重置，允许重试
  }
}

// 点赞/取消点赞
async function handleLike() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  if (likeLoading.value) return
  likeLoading.value = true
  
  try {
    const liked = await toggleLikeCourse(course.value.id)
    course.value.userLiked = liked
    if (liked) {
      course.value.likeCount++
      ElMessage.success('点赞成功')
    } else {
      course.value.likeCount = Math.max(0, course.value.likeCount - 1)
      ElMessage.info('已取消点赞')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    likeLoading.value = false
  }
}

// 收藏/取消收藏
async function handleFavorite() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  if (favoriteLoading.value) return
  favoriteLoading.value = true
  try {
    const favorited = await toggleFavoriteCourse(course.value.id)
    course.value.userFavorited = favorited
    ElMessage.success(favorited ? '收藏成功' : '已取消收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

// 开始学习 - 滚动到视频并自动播放
function handleStartLearning() {
  // 滚动到视频区域
  window.scrollTo({ top: 0, behavior: 'smooth' })
  
  // 自动播放视频（播放时会触发 @play 事件记录播放次数）
  if (videoRef.value) {
    videoRef.value.play().catch(e => {
      // 浏览器可能阻止自动播放，忽略错误
      console.log('自动播放被阻止，请手动点击播放')
    })
  }
}
</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-6 pb-24 md:pb-6">
    <div v-if="loading" class="text-center py-16 text-gray-500">
      加载中...
    </div>
    
    <div v-else-if="course">
      <!-- 视频播放器 -->
      <div class="aspect-video bg-black rounded-xl overflow-hidden mb-6">
        <video
          v-if="course.videoUrl"
          ref="videoRef"
          :src="course.videoUrl"
          controls
          class="w-full h-full"
          @play="handleVideoPlay"
        />
        <div v-else class="w-full h-full flex items-center justify-center text-white">
          暂无视频
        </div>
      </div>
      
      <!-- 课程信息 -->
      <div class="bg-white rounded-xl p-6">
        <h1 class="text-2xl font-bold">{{ course.title }}</h1>
        
        <div class="flex flex-wrap gap-4 mt-4 text-sm text-gray-500">
          <span>{{ course.duration }}分钟</span>
          <span>{{ course.difficultyName }}</span>
          <span>{{ course.viewCount }}次播放</span>
          <span>{{ course.likeCount }}人点赞</span>
        </div>
        
        <div class="flex gap-4 mt-6 flex-wrap">
          <el-button type="primary" size="large" @click="handleStartLearning">开始学习</el-button>
          <el-button 
            size="large" 
            :type="isLiked ? 'danger' : 'default'"
            :loading="likeLoading"
            @click="handleLike"
          >
            {{ isLiked ? '❤️ 已点赞' : '👍 点赞' }}
          </el-button>
          <el-button 
            size="large" 
            :type="isFavorited ? 'warning' : 'default'"
            :loading="favoriteLoading"
            @click="handleFavorite"
          >
            {{ isFavorited ? '⭐ 已收藏' : '☆ 收藏' }}
          </el-button>
        </div>
        
        <div class="mt-8">
          <h2 class="text-lg font-medium mb-4">课程介绍</h2>
          <div class="text-gray-600 leading-relaxed prose prose-sm max-w-none" v-html="course.description"></div>
        </div>
      </div>
    </div>
  </div>
</template>
