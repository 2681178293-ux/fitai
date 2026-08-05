<template>
  <view class="ai-page">
    <!-- 消息列表 -->
    <scroll-view 
      scroll-y 
      class="message-list" 
      :scroll-top="scrollTop"
      :scroll-into-view="scrollToId"
    >
      <view class="welcome" v-if="messages.length === 0">
        <image src="/static/ai-avatar.png" class="ai-avatar" mode="aspectFill" />
        <text class="welcome-title">Hi，我是小智</text>
        <text class="welcome-desc">您的AI健身助手，有什么可以帮您的？</text>
        
        <view class="quick-questions">
          <view 
            class="question-item" 
            v-for="(q, i) in quickQuestions" 
            :key="i"
            @click="sendQuickQuestion(q)"
          >
            {{ q }}
          </view>
        </view>
      </view>

      <view 
        v-for="(msg, index) in messages" 
        :key="index"
        :id="'msg-' + index"
        :class="['message-item', msg.role]"
      >
        <image 
          :src="msg.role === 'user' ? userAvatar : '/static/ai-avatar.png'" 
          class="avatar" 
          mode="aspectFill"
        />
        <view class="message-content">
          <text class="message-text">{{ msg.content }}</text>
        </view>
      </view>

      <!-- 加载中 -->
      <view class="message-item assistant" v-if="loading">
        <image src="/static/ai-avatar.png" class="avatar" mode="aspectFill" />
        <view class="message-content">
          <view class="typing-indicator">
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="usage-bar" v-if="usage">
      <text class="usage-text">
        本月 Token {{ usage.used ?? 0 }} / {{ usage.limit ?? 0 }}（剩余 {{ usage.remaining ?? 0 }}）
        <text v-if="usage.vipActive" class="vip-hint"> · VIP加赠 {{ usage.vipBonus }}</text>
      </text>
    </view>

    <view class="input-area">
      <view class="input-wrapper">
        <input 
          v-model="inputText" 
          type="text" 
          placeholder="输入您的问题..."
          confirm-type="send"
          @confirm="sendMessage"
          class="input"
        />
        <button 
          class="send-btn" 
          :disabled="!inputText.trim() || loading"
          @click="sendMessage"
        >
          <text class="iconfont icon-send"></text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { chat, getAiUsage } from '@/api/ai'

const userStore = useUserStore()
const userAvatar = computed(() => userStore.userInfo?.avatar || '/static/default-avatar.png')

const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const scrollTop = ref(0)
const scrollToId = ref('')
const conversationId = ref(null)

const usage = ref(null)

async function loadUsage() {
  try {
    const res = await getAiUsage()
    usage.value = res.data ?? res
  } catch (e) {
    usage.value = null
  }
}

const quickQuestions = [
  '我想减脂，该怎么训练？',
  '帮我制定一周训练计划',
  '今天吃什么比较健康？',
  '深蹲的正确姿势是什么？'
]

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: text
  })
  inputText.value = ''
  
  // 滚动到底部
  scrollToBottom()

  loading.value = true
  try {
    const res = await chat({
      conversationId: conversationId.value,
      message: text,
      type: 'chat'
    })

    // 保存会话ID
    if (!conversationId.value && res.data.conversationId) {
      conversationId.value = res.data.conversationId
    }

    // 添加AI回复
    messages.value.push({
      role: 'assistant',
      content: res.data.content
    })

    scrollToBottom()
    await loadUsage()
  } catch (e) {
    console.error('发送消息失败', e)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，我暂时无法回答，请稍后再试。'
    })
  } finally {
    loading.value = false
  }
}

function sendQuickQuestion(question) {
  inputText.value = question
  sendMessage()
}

function scrollToBottom() {
  nextTick(() => {
    scrollToId.value = 'msg-' + (messages.value.length - 1)
  })
}

// 初始化时加载一次配额
loadUsage()
</script>

<style lang="scss" scoped>
.ai-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.message-list {
  flex: 1;
  padding: 16px;
  padding-bottom: 80px;
}

.usage-bar {
  padding: 6px 16px;
  background-color: #eef2ff;
}

.usage-text {
  font-size: 12px;
  color: #4b5563;
}
.vip-hint {
  color: #b45309;
}

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
}

.ai-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 16px;
}

.welcome-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.welcome-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
}

.quick-questions {
  width: 100%;
}

.question-item {
  background-color: #fff;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #3b82f6;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-item {
  display: flex;
  margin-bottom: 16px;
  
  &.user {
    flex-direction: row-reverse;
    
    .message-content {
      background-color: #3b82f6;
      color: #fff;
      border-radius: 16px 4px 16px 16px;
    }
  }
  
  &.assistant {
    .message-content {
      background-color: #fff;
      border-radius: 4px 16px 16px 16px;
    }
  }
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  margin: 0 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-text {
  font-size: 15px;
  line-height: 1.5;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.dot {
  width: 8px;
  height: 8px;
  background-color: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
  
  &:nth-child(2) {
    animation-delay: 0.2s;
  }
  
  &:nth-child(3) {
    animation-delay: 0.4s;
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-4px);
  }
}

.input-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background-color: #fff;
  border-top: 1px solid #e5e7eb;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.input {
  flex: 1;
  height: 44px;
  padding: 0 16px;
  background-color: #f3f4f6;
  border-radius: 22px;
  font-size: 15px;
}

.send-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  
  &[disabled] {
    opacity: 0.5;
  }
}

.icon-send {
  color: #fff;
  font-size: 20px;
}
</style>
