<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { chat, getConversations, getConversationDetail, getAiUsage } from '@/api/ai'
import { useUserStore } from '@/stores/user'
import { ChatDotRound, Close, Delete, Promotion, User, Cpu } from '@element-plus/icons-vue'

const userStore = useUserStore()

// 控制显示/隐藏
const isOpen = ref(false)
const isMinimized = ref(false)

// 会话相关
const conversations = ref([])
const currentConversationId = ref(null)
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)

// 调用配额
const usage = ref(null)

async function loadUsage() {
  if (!userStore.isLoggedIn) {
    usage.value = null
    return
  }
  try {
    usage.value = await getAiUsage()
  } catch (e) {
    usage.value = null
  }
}

// 加载会话列表
async function loadConversations() {
  if (!userStore.isLoggedIn) return
  try {
    conversations.value = await getConversations('chat')
    await loadUsage()
  } catch (e) {
    console.error('加载会话失败', e)
  }
}

// 加载会话详情
async function loadConversation(id) {
  if (!id) return  // 防止 id 为 undefined 时发送请求
  try {
    const data = await getConversationDetail(id)
    currentConversationId.value = id
    messages.value = data.messages || []
    await scrollToBottom()
  } catch (e) {
    ElMessage.error('加载会话失败')
  }
}

// 新建会话
function newConversation() {
  currentConversationId.value = null
  messages.value = []
}

// 发送消息
async function sendMessage() {
  if (!inputMessage.value.trim()) return
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: userMessage,
    createTime: new Date().toISOString()
  })
  await scrollToBottom()

  // 添加加载中的 AI 消息
  const loadingIndex = messages.value.length
  messages.value.push({
    role: 'assistant',
    content: '',
    isLoading: true,
    createTime: new Date().toISOString()
  })
  await scrollToBottom()

  isLoading.value = true

  try {
    const response = await chat({
      message: userMessage,
      conversationId: currentConversationId.value,
      type: 'chat'
    })

    // 更新 AI 回复
    messages.value[loadingIndex] = {
      role: 'assistant',
      content: response.content,
      createTime: new Date().toISOString()
    }

    // 更新会话 ID
    if (!currentConversationId.value && response.conversationId) {
      currentConversationId.value = response.conversationId
      loadConversations()
    }
  } catch (e) {
    messages.value[loadingIndex] = {
      role: 'assistant',
      content: '抱歉，AI 服务暂时不可用，请稍后再试。',
      isError: true,
      createTime: new Date().toISOString()
    }
  } finally {
    isLoading.value = false
    await scrollToBottom()
    await loadUsage()
  }
}

// 滚动到底部
async function scrollToBottom() {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化时间
function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 打开助手
function openChat() {
  isOpen.value = true
  isMinimized.value = false
  if (userStore.isLoggedIn) {
    loadConversations()
  }
}

// 关闭助手
function closeChat() {
  isOpen.value = false
}

// 最小化
function minimizeChat() {
  isMinimized.value = true
}

// 按回车发送
function handleKeyDown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    loadConversations()
  }
})
</script>

<template>
  <!-- 悬浮按钮 -->
  <div 
    v-if="!isOpen || isMinimized" 
    class="chat-fab"
    @click="openChat"
  >
    <el-icon :size="24"><Cpu /></el-icon>
    <span class="fab-label">AI 助手</span>
  </div>

  <!-- 聊天窗口 -->
  <Transition name="chat-window">
    <div v-if="isOpen && !isMinimized" class="chat-window">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <el-icon :size="20" class="ai-icon"><Cpu /></el-icon>
          <span class="header-title">AI 健身助手</span>
        </div>
        <div class="header-actions">
          <div v-if="usage" class="usage-text">
            本月 Token {{ usage.used ?? 0 }} / {{ usage.limit ?? 0 }}（剩余 {{ usage.remaining ?? 0 }}）
            <span v-if="usage.vipActive" class="text-amber-700"> · VIP加赠 {{ usage.vipBonus }}</span>
          </div>
          <el-button text circle @click="newConversation">
            <el-icon><Delete /></el-icon>
          </el-button>
          <el-button text circle @click="minimizeChat">
            <span class="minimize-icon">—</span>
          </el-button>
          <el-button text circle @click="closeChat">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 消息区域 -->
      <div ref="messagesContainer" class="chat-messages">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-icon">
            <el-icon :size="48"><Cpu /></el-icon>
          </div>
          <h3>你好！我是你的 AI 健身助手</h3>
          <p>我可以帮你：</p>
          <ul>
            <li>生成个性化训练计划</li>
            <li>解答健身相关问题</li>
            <li>提供饮食营养建议</li>
            <li>分析训练数据并给出改进建议</li>
          </ul>
          <div class="quick-questions">
            <el-button size="small" round @click="inputMessage = '如何快速减脂？'">如何快速减脂？</el-button>
            <el-button size="small" round @click="inputMessage = '新手增肌训练建议'">新手增肌训练建议</el-button>
            <el-button size="small" round @click="inputMessage = '推荐一些核心训练动作'">推荐核心训练动作</el-button>
          </div>
        </div>

        <!-- 消息列表 -->
        <div 
          v-for="(msg, index) in messages" 
          :key="index"
          class="message-item"
          :class="msg.role"
        >
          <div class="message-avatar">
            <el-avatar v-if="msg.role === 'user'" :size="32" :src="userStore.userInfo?.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <el-avatar v-else :size="32" class="ai-avatar">
              <el-icon><Cpu /></el-icon>
            </el-avatar>
          </div>
          <div class="message-content">
            <div v-if="msg.isLoading" class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
            <div v-else class="message-text" :class="{ error: msg.isError }">
              {{ msg.content }}
            </div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="输入你的问题..."
          :disabled="isLoading"
          @keydown="handleKeyDown"
        />
        <el-button 
          type="primary" 
          :icon="Promotion"
          :loading="isLoading"
          @click="sendMessage"
        >
          发送
        </el-button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
/* 悬浮按钮 */
.chat-fab {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
  z-index: 1000;
}

.chat-fab:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.5);
  width: auto;
  padding: 0 20px;
}

.fab-label {
  display: none;
  font-size: 12px;
  margin-top: 2px;
  white-space: nowrap;
}

.chat-fab:hover .fab-label {
  display: block;
}

/* 聊天窗口 */
.chat-window {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 380px;
  height: 560px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 1001;
}

/* 头部 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-icon {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px;
  border-radius: 8px;
}

.header-title {
  font-weight: 600;
  font-size: 16px;
}

.header-actions .el-button {
  color: white;
}

.usage-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  margin-right: 8px;
}

.minimize-icon {
  font-weight: bold;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8fafc;
}

/* 欢迎消息 */
.welcome-message {
  text-align: center;
  padding: 24px 16px;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: white;
}

.welcome-message h3 {
  font-size: 18px;
  color: #1f2937;
  margin-bottom: 8px;
}

.welcome-message p {
  color: #6b7280;
  margin-bottom: 8px;
}

.welcome-message ul {
  text-align: left;
  color: #374151;
  padding-left: 24px;
  margin-bottom: 16px;
}

.welcome-message li {
  margin: 4px 0;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-content {
  max-width: 75%;
}

.message-text {
  background: white;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  color: #374151;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  white-space: pre-wrap;
  word-break: break-word;
}

.message-item.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-text.error {
  background: #fef2f2;
  color: #dc2626;
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
  text-align: right;
}

.message-item.user .message-time {
  text-align: left;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 12px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #9ca3af;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

/* 输入区域 */
.chat-input {
  display: flex;
  gap: 8px;
  padding: 12px;
  border-top: 1px solid #e5e7eb;
  background: white;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-input .el-button {
  align-self: flex-end;
}

/* 动画 */
.chat-window-enter-active,
.chat-window-leave-active {
  transition: all 0.3s ease;
}

.chat-window-enter-from,
.chat-window-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-window {
    right: 0;
    bottom: 0;
    width: 100%;
    height: 100%;
    border-radius: 0;
  }

  .chat-fab {
    right: 16px;
    bottom: 80px;
  }
}
</style>
