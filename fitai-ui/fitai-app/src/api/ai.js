import http from '@/utils/request'

/**
 * AI对话
 */
export function chat(data) {
  return http.post('/ai/chat', data)
}

/**
 * 获取会话列表
 */
export function getConversations(type) {
  return http.get('/ai/conversations', { type })
}

/**
 * 获取会话详情
 */
export function getConversationDetail(id) {
  return http.get(`/ai/conversations/${id}`)
}

/**
 * 删除会话
 */
export function deleteConversation(id) {
  return http.delete(`/ai/conversations/${id}`)
}

/**
 * 训练点评
 */
export function reviewTraining(data) {
  return http.post('/ai/training/review', data)
}

/**
 * 知识库问答
 */
export function knowledgeQA(question) {
  return http.get('/ai/knowledge/qa', { question })
}

/**
 * 获取本月 AI Token 已用/总额度（自然月，与会员加赠汇总）
 */
export function getAiUsage() {
  return http.get('/ai/usage')
}

/**
 * AI 能力清单（入口、限流场景等）
 */
export function getAiCapabilities() {
  return http.get('/ai/capabilities')
}
