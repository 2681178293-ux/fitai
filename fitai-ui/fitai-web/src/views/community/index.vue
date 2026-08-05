<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeed, createPost, toggleLike, deletePost, getComments, createComment, deleteComment, toggleCommentLike } from '@/api/community'
import { UPLOAD_URLS } from '@/api/file'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ChatDotRound, Star, StarFilled, Delete, Plus, Picture, TrendCharts, Timer, Trophy, Close } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 上传相关
const uploadUrl = UPLOAD_URLS.community
const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}))
const uploadedImages = ref([])

const loading = ref(false)
const posts = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const hasMore = ref(true)

const showPostDialog = ref(false)
const postForm = ref({
  content: '',
  images: []
})
const posting = ref(false)

// 图片预览
const previewVisible = ref(false)
const previewImages = ref([])
const previewIndex = ref(0)

// 评论相关
const expandedComments = ref({}) // 展开评论的动态 ID
const commentsData = ref({}) // 评论数据缓存
const commentsLoading = ref({}) // 评论加载状态
const commentInputs = ref({}) // 评论输入框内容
const replyTo = ref({}) // 回复目标
const commentSubmitting = ref({}) // 评论提交状态

// 热门话题
const hotTopics = [
  { tag: '健身打卡', count: 1234 },
  { tag: '减脂分享', count: 892 },
  { tag: '力量训练', count: 756 },
  { tag: '跑步日记', count: 623 },
  { tag: '瑜伽生活', count: 512 },
  { tag: '健康饮食', count: 489 }
]

// 加载动态列表
const loadPosts = async (reset = false) => {
  if (reset) {
    pageNum.value = 1
    posts.value = []
  }
  
  loading.value = true
  try {
    const data = await getFeed({ pageNum: pageNum.value, pageSize: pageSize.value })
    const records = data?.records ?? []
    const totalNum = data?.total ?? 0
    if (reset) {
      posts.value = records
    } else {
      posts.value.push(...records)
    }
    total.value = totalNum
    hasMore.value = posts.value.length < totalNum
  } catch (error) {
    console.error('加载动态失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMore = () => {
  if (loading.value || !hasMore.value) return
  pageNum.value++
  loadPosts()
}

// 发布动态
const handlePost = async () => {
  if (!postForm.value.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  
  posting.value = true
  try {
    await createPost(postForm.value)
    ElMessage.success('发布成功')
    showPostDialog.value = false
    postForm.value = { content: '', images: [] }
    uploadedImages.value = []
    loadPosts(true)
  } catch (error) {
    ElMessage.error(error.message || '发布失败')
  } finally {
    posting.value = false
  }
}

// 点赞
const handleLike = async (post) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  try {
    await toggleLike(post.id)
    post.liked = !post.liked
    post.likeCount += post.liked ? 1 : -1
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除动态
const handleDelete = async (post) => {
  try {
    await ElMessageBox.confirm('确定删除这条动态吗？', '提示')
    await deletePost(post.id)
    ElMessage.success('删除成功')
    posts.value = posts.value.filter(p => p.id !== post.id)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// ==================== 评论功能 ====================

// 切换评论展开/收起
const toggleComments = async (postId) => {
  if (expandedComments.value[postId]) {
    expandedComments.value[postId] = false
  } else {
    expandedComments.value[postId] = true
    await loadComments(postId)
  }
}

// 加载评论
const loadComments = async (postId) => {
  if (commentsLoading.value[postId]) return
  
  commentsLoading.value[postId] = true
  try {
    const data = await getComments(postId)
    commentsData.value[postId] = data || []
  } catch (error) {
    console.error('加载评论失败:', error)
    commentsData.value[postId] = []
  } finally {
    commentsLoading.value[postId] = false
  }
}

// 发表评论
const handleComment = async (postId) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  const content = commentInputs.value[postId]?.trim()
  if (!content) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  commentSubmitting.value[postId] = true
  try {
    const reply = replyTo.value[postId]
    const newComment = await createComment(postId, {
      content,
      parentId: reply?.id || null,
      replyUserId: reply?.userId || null
    })
    
    // 添加到评论列表
    if (!commentsData.value[postId]) {
      commentsData.value[postId] = []
    }
    commentsData.value[postId].push(newComment)
    
    // 更新动态评论数
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.commentCount = (post.commentCount || 0) + 1
    }
    
    // 清空输入
    commentInputs.value[postId] = ''
    replyTo.value[postId] = null
    
    ElMessage.success('评论成功')
  } catch (error) {
    ElMessage.error(error.message || '评论失败')
  } finally {
    commentSubmitting.value[postId] = false
  }
}

// 设置回复目标
const setReplyTo = (postId, comment) => {
  replyTo.value[postId] = comment
  // 聚焦输入框
  nextTick(() => {
    const input = document.querySelector(`#comment-input-${postId}`)
    if (input) input.focus()
  })
}

// 取消回复
const cancelReply = (postId) => {
  replyTo.value[postId] = null
}

// 删除评论
const handleDeleteComment = async (postId, comment) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '提示')
    await deleteComment(comment.id)
    
    // 从列表中移除
    const comments = commentsData.value[postId]
    if (comments) {
      const index = comments.findIndex(c => c.id === comment.id)
      if (index > -1) {
        comments.splice(index, 1)
      }
    }
    
    // 更新动态评论数
    const post = posts.value.find(p => p.id === postId)
    if (post && post.commentCount > 0) {
      post.commentCount--
    }
    
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 评论点赞
const handleCommentLike = async (comment) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  try {
    await toggleCommentLike(comment.id)
    comment.liked = !comment.liked
    comment.likeCount = (comment.likeCount || 0) + (comment.liked ? 1 : -1)
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// ==================== 工具函数 ====================

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return date.toLocaleDateString()
}

// 打开发布弹窗
const openPostDialog = () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  showPostDialog.value = true
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    postForm.value.images.push(response.data)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
    const index = uploadedImages.value.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      uploadedImages.value.splice(index, 1)
    }
  }
}

// 移除图片
const handleUploadRemove = (file) => {
  const url = file.response?.data || file.url
  const index = postForm.value.images.indexOf(url)
  if (index > -1) {
    postForm.value.images.splice(index, 1)
  }
}

// 预览图片
const handlePreview = (images, index) => {
  previewImages.value = images
  previewIndex.value = index
  previewVisible.value = true
}

// 获取图片网格样式
const getImageGridClass = (count) => {
  if (count === 1) return 'grid-cols-1'
  if (count === 2) return 'grid-cols-2'
  if (count === 4) return 'grid-cols-2'
  return 'grid-cols-3'
}

// 获取动态类型标签颜色
const getTypeTagType = (type) => {
  const types = {
    checkin: 'success',
    challenge: 'warning',
    normal: ''
  }
  return types[type] || ''
}

// 获取动态类型文字
const getTypeText = (type) => {
  const texts = {
    checkin: '打卡',
    challenge: '挑战',
    normal: ''
  }
  return texts[type] || ''
}

onMounted(() => {
  loadPosts()
})
</script>

<template>
  <div class="community-page">
    <div class="community-container">
      <!-- 左侧主内容区 -->
      <div class="main-content">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="header-info">
            <h1 class="page-title">
              <el-icon class="title-icon"><ChatDotRound /></el-icon>
              健身社区
            </h1>
            <p class="page-desc">分享训练日常，记录每一次进步</p>
          </div>
          <el-button type="primary" size="large" @click="openPostDialog" :icon="Plus" class="post-btn">
            发布动态
          </el-button>
        </div>

        <!-- 发布框快捷入口 -->
        <div class="quick-post-card" @click="openPostDialog" v-if="userStore.isLoggedIn">
          <el-avatar :src="userStore.userInfo?.avatar" :size="40">
            {{ userStore.userInfo?.nickname?.[0] || 'U' }}
          </el-avatar>
          <div class="quick-input">
            <span class="placeholder-text">分享你的健身日常...</span>
          </div>
          <div class="quick-actions">
            <el-icon><Picture /></el-icon>
          </div>
        </div>

        <!-- 动态列表 -->
        <div v-loading="loading && pageNum === 1" class="posts-list">
          <!-- 空状态 -->
          <div v-if="!loading && posts.length === 0" class="empty-state">
            <div class="empty-icon">📝</div>
            <h3>还没有动态</h3>
            <p>快来发布第一条动态，开启你的健身分享之旅吧！</p>
            <el-button type="primary" @click="openPostDialog">立即发布</el-button>
          </div>
          
          <!-- 动态卡片 -->
          <div v-for="post in posts" :key="post.id" class="post-card">
            <!-- 作者信息 -->
            <div class="post-header">
              <el-avatar :src="post.author?.avatar" :size="44" class="author-avatar">
                {{ post.author?.nickname?.[0] || 'U' }}
              </el-avatar>
              <div class="author-info">
                <div class="author-name">{{ post.author?.nickname || '健身用户' }}</div>
                <div class="post-meta">
                  <span class="post-time">{{ formatTime(post.createTime) }}</span>
                  <el-tag 
                    v-if="post.type && post.type !== 'normal'" 
                    :type="getTypeTagType(post.type)"
                    size="small"
                    class="post-type-tag"
                  >
                    {{ getTypeText(post.type) }}
                  </el-tag>
                </div>
              </div>
              <el-button 
                v-if="userStore.userInfo?.id === post.userId"
                type="danger" 
                text 
                size="small"
                :icon="Delete"
                @click="handleDelete(post)"
                class="delete-btn"
              />
            </div>

            <!-- 动态内容 -->
            <div class="post-content">
              <p class="content-text">{{ post.content }}</p>
            </div>

            <!-- 图片展示 -->
            <div 
              v-if="post.images?.length" 
              class="post-images"
              :class="getImageGridClass(post.images.length)"
            >
              <div 
                v-for="(img, idx) in post.images.slice(0, 9)" 
                :key="idx"
                class="image-item"
                :class="{ 'image-single': post.images.length === 1 }"
                @click="handlePreview(post.images, idx)"
              >
                <img :src="img" :alt="`图片${idx + 1}`" />
                <div v-if="idx === 8 && post.images.length > 9" class="image-more">
                  +{{ post.images.length - 9 }}
                </div>
              </div>
            </div>

            <!-- 互动区 -->
            <div class="post-actions">
              <button 
                class="action-btn"
                :class="{ 'liked': post.liked }"
                @click="handleLike(post)"
              >
                <el-icon>
                  <StarFilled v-if="post.liked" />
                  <Star v-else />
                </el-icon>
                <span>{{ post.likeCount || 0 }}</span>
              </button>
              <button 
                class="action-btn comment-btn"
                :class="{ 'active': expandedComments[post.id] }"
                @click="toggleComments(post.id)"
              >
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ expandedComments[post.id] ? '收起评论' : '评论' }} {{ post.commentCount || '' }}</span>
              </button>
            </div>

            <!-- 评论区 -->
            <div v-if="expandedComments[post.id]" class="comments-section">
              <!-- 评论输入框 -->
              <div class="comment-input-box">
                <!-- 回复提示 -->
                <div v-if="replyTo[post.id]" class="reply-hint">
                  <span>回复 @{{ replyTo[post.id].author?.nickname || '用户' }}</span>
                  <el-icon class="cancel-reply" @click="cancelReply(post.id)"><Close /></el-icon>
                </div>
                <div class="input-row">
                  <el-avatar :src="userStore.userInfo?.avatar" :size="32">
                    {{ userStore.userInfo?.nickname?.[0] || 'U' }}
                  </el-avatar>
                  <el-input
                    :id="`comment-input-${post.id}`"
                    v-model="commentInputs[post.id]"
                    :placeholder="replyTo[post.id] ? `回复 @${replyTo[post.id].author?.nickname}...` : '写下你的评论...'"
                    maxlength="200"
                    @keyup.enter="handleComment(post.id)"
                  />
                  <el-button 
                    type="primary" 
                    size="small"
                    :loading="commentSubmitting[post.id]"
                    @click="handleComment(post.id)"
                  >
                    发送
                  </el-button>
                </div>
              </div>

              <!-- 评论列表 -->
              <div v-loading="commentsLoading[post.id]" class="comments-list">
                <div v-if="!commentsLoading[post.id] && (!commentsData[post.id] || commentsData[post.id].length === 0)" class="no-comments">
                  暂无评论，快来抢沙发吧～
                </div>
                <div v-for="comment in commentsData[post.id]" :key="comment.id" class="comment-item">
                  <el-avatar :src="comment.author?.avatar" :size="32">
                    {{ comment.author?.nickname?.[0] || 'U' }}
                  </el-avatar>
                  <div class="comment-body">
                    <div class="comment-header">
                      <span class="comment-author">{{ comment.author?.nickname || '用户' }}</span>
                      <span v-if="comment.replyUser" class="reply-to">
                        回复 <span class="reply-name">@{{ comment.replyUser.nickname }}</span>
                      </span>
                      <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                    </div>
                    <div class="comment-content">{{ comment.content }}</div>
                    <div class="comment-actions">
                      <button 
                        class="comment-action-btn"
                        :class="{ 'liked': comment.liked }"
                        @click="handleCommentLike(comment)"
                      >
                        <el-icon>
                          <StarFilled v-if="comment.liked" />
                          <Star v-else />
                        </el-icon>
                        <span>{{ comment.likeCount || 0 }}</span>
                      </button>
                      <button class="comment-action-btn" @click="setReplyTo(post.id, comment)">
                        回复
                      </button>
                      <button 
                        v-if="userStore.userInfo?.id === comment.userId"
                        class="comment-action-btn delete"
                        @click="handleDeleteComment(post.id, comment)"
                      >
                        删除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载更多 -->
          <div v-if="posts.length > 0" class="load-more">
            <el-button v-if="hasMore" :loading="loading" @click="loadMore" text>
              {{ loading ? '加载中...' : '加载更多' }}
            </el-button>
            <span v-else class="no-more">— 没有更多了 —</span>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <div class="sidebar">
        <!-- 热门话题 -->
        <div class="sidebar-card">
          <div class="card-header">
            <el-icon class="header-icon"><TrendCharts /></el-icon>
            <span>热门话题</span>
          </div>
          <div class="topic-list">
            <div v-for="(topic, idx) in hotTopics" :key="idx" class="topic-item">
              <span class="topic-rank" :class="{ 'top3': idx < 3 }">{{ idx + 1 }}</span>
              <span class="topic-tag">#{{ topic.tag }}</span>
              <span class="topic-count">{{ topic.count }}</span>
            </div>
          </div>
        </div>

        <!-- 活跃达人 -->
        <div class="sidebar-card">
          <div class="card-header">
            <el-icon class="header-icon"><Trophy /></el-icon>
            <span>活跃达人</span>
          </div>
          <div class="user-list">
            <div class="user-item">
              <el-avatar src="https://api.dicebear.com/7.x/avataaars/svg?seed=fitness" :size="36" />
              <div class="user-info">
                <div class="user-name">健身达人</div>
                <div class="user-stats">发布 128 条动态</div>
              </div>
            </div>
            <div class="user-item">
              <el-avatar src="https://api.dicebear.com/7.x/avataaars/svg?seed=yoga" :size="36" />
              <div class="user-info">
                <div class="user-name">瑜伽小仙女</div>
                <div class="user-stats">发布 96 条动态</div>
              </div>
            </div>
            <div class="user-item">
              <el-avatar src="https://api.dicebear.com/7.x/avataaars/svg?seed=runner" :size="36" />
              <div class="user-info">
                <div class="user-name">跑步爱好者</div>
                <div class="user-stats">发布 85 条动态</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 打卡提醒 -->
        <div class="sidebar-card checkin-card">
          <div class="card-header">
            <el-icon class="header-icon"><Timer /></el-icon>
            <span>今日打卡</span>
          </div>
          <div class="checkin-content">
            <p>坚持健身，遇见更好的自己</p>
            <el-button type="primary" @click="openPostDialog" plain>
              立即打卡
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 发布动态弹窗 -->
    <el-dialog v-model="showPostDialog" title="发布动态" width="520px" class="post-dialog">
      <el-input
        v-model="postForm.content"
        type="textarea"
        :rows="5"
        placeholder="分享你的训练日常、健身心得、饮食搭配..."
        maxlength="500"
        show-word-limit
        class="post-textarea"
      />
      
      <div class="upload-section">
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          list-type="picture-card"
          :file-list="uploadedImages"
          :on-success="handleUploadSuccess"
          :on-remove="handleUploadRemove"
          :before-upload="beforeUpload"
          accept="image/*"
          :limit="9"
        >
          <el-icon><Picture /></el-icon>
        </el-upload>
        <div class="upload-tip">最多上传 9 张图片，每张不超过 5MB</div>
      </div>
      
      <template #footer>
        <el-button @click="showPostDialog = false">取消</el-button>
        <el-button type="primary" :loading="posting" @click="handlePost">
          发布动态
        </el-button>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="previewImages"
      :initial-index="previewIndex"
      @close="previewVisible = false"
    />
  </div>
</template>

<style scoped>
.community-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  padding: 24px;
}

.community-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 24px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.title-icon {
  color: #8b5cf6;
  font-size: 28px;
}

.page-desc {
  color: #6b7280;
  margin: 4px 0 0;
  font-size: 14px;
}

.post-btn {
  border-radius: 20px;
  padding: 12px 24px;
}

/* 快捷发布框 */
.quick-post-card {
  background: white;
  border-radius: 16px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.quick-post-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.quick-input {
  flex: 1;
  background: #f3f4f6;
  border-radius: 20px;
  padding: 10px 16px;
}

.placeholder-text {
  color: #9ca3af;
  font-size: 14px;
}

.quick-actions {
  color: #10b981;
  font-size: 24px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  color: #1f2937;
  margin-bottom: 8px;
}

.empty-state p {
  color: #6b7280;
  margin-bottom: 20px;
}

/* 动态卡片 */
.post-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.post-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.author-avatar {
  border: 2px solid #e5e7eb;
}

.author-info {
  flex: 1;
}

.author-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 15px;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}

.post-time {
  font-size: 13px;
  color: #9ca3af;
}

.post-type-tag {
  border-radius: 4px;
}

.delete-btn {
  opacity: 0;
  transition: opacity 0.3s;
}

.post-card:hover .delete-btn {
  opacity: 1;
}

/* 动态内容 */
.post-content {
  margin-bottom: 16px;
}

.content-text {
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 15px;
}

/* 图片展示 */
.post-images {
  display: grid;
  gap: 8px;
  margin-bottom: 16px;
}

.post-images.grid-cols-1 {
  grid-template-columns: 1fr;
}

.post-images.grid-cols-2 {
  grid-template-columns: repeat(2, 1fr);
}

.post-images.grid-cols-3 {
  grid-template-columns: repeat(3, 1fr);
}

.image-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  aspect-ratio: 1;
}

.image-item.image-single {
  aspect-ratio: 16/9;
  max-height: 400px;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.image-item:hover img {
  transform: scale(1.05);
}

.image-more {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: 600;
}

/* 互动区 */
.post-actions {
  display: flex;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  background: #f9fafb;
  border-radius: 20px;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.action-btn.liked {
  background: #fef2f2;
  color: #ef4444;
}

.action-btn.liked:hover {
  background: #fee2e2;
}

.action-btn.active {
  background: #eff6ff;
  color: #3b82f6;
}

.action-btn.comment-btn {
  border: 1px solid #e5e7eb;
}

.action-btn.comment-btn:hover {
  border-color: #3b82f6;
  background: #eff6ff;
  color: #3b82f6;
}

/* 评论区 */
.comments-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
}

.comment-input-box {
  margin-bottom: 16px;
}

.reply-hint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: #eff6ff;
  border-radius: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #3b82f6;
}

.cancel-reply {
  cursor: pointer;
  &:hover {
    color: #1d4ed8;
  }
}

.input-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.input-row .el-input {
  flex: 1;
}

.comments-list {
  min-height: 60px;
}

.no-comments {
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
  padding: 20px 0;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 4px;
}

.comment-author {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.reply-to {
  font-size: 13px;
  color: #6b7280;
}

.reply-name {
  color: #3b82f6;
}

.comment-time {
  font-size: 12px;
  color: #9ca3af;
}

.comment-content {
  color: #374151;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.comment-action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: none;
  background: none;
  color: #9ca3af;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.3s;
}

.comment-action-btn:hover {
  color: #6b7280;
}

.comment-action-btn.liked {
  color: #ef4444;
}

.comment-action-btn.delete:hover {
  color: #ef4444;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 20px;
}

.no-more {
  color: #9ca3af;
  font-size: 14px;
}

/* 侧边栏 */
.sidebar-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
  font-size: 15px;
}

.header-icon {
  color: #8b5cf6;
  font-size: 18px;
}

/* 热门话题 */
.topic-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.topic-rank {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #9ca3af;
}

.topic-rank.top3 {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  color: white;
  border-radius: 4px;
}

.topic-tag {
  flex: 1;
  color: #374151;
  font-size: 14px;
}

.topic-count {
  color: #9ca3af;
  font-size: 12px;
}

/* 活跃达人 */
.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: 500;
  color: #1f2937;
  font-size: 14px;
}

.user-stats {
  color: #9ca3af;
  font-size: 12px;
  margin-top: 2px;
}

/* 打卡提醒 */
.checkin-card {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  color: white;
}

.checkin-card .card-header {
  color: white;
}

.checkin-card .header-icon {
  color: white;
}

.checkin-content {
  text-align: center;
}

.checkin-content p {
  margin-bottom: 16px;
  opacity: 0.9;
  font-size: 14px;
}

.checkin-content .el-button {
  background: white;
  color: #8b5cf6;
  border: none;
}

.checkin-content .el-button:hover {
  background: #f9fafb;
}

/* 发布弹窗 */
.post-dialog :deep(.el-dialog__body) {
  padding-top: 16px;
}

.post-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  padding: 16px;
  font-size: 15px;
  line-height: 1.6;
}

.upload-section {
  margin-top: 16px;
}

.upload-tip {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 8px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .community-page {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .post-btn {
    width: 100%;
  }
  
  .post-card {
    padding: 16px;
  }
  
  .post-images.grid-cols-3 {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
