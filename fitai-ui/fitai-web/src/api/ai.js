import request from '@/utils/request'

const API_BASE = import.meta.env.VITE_API_BASE || '/api/pc'

/**
 * AI 对话
 */
export function chat(data) {
  return request.post('/ai/chat', data)
}

/**
 * 获取会话列表
 */
export function getConversations(type) {
  return request.get('/ai/conversations', { params: { type } })
}

/**
 * 获取会话详情
 */
export function getConversationDetail(id) {
  if (!id) return Promise.reject(new Error('会话ID不能为空'))
  return request.get(`/ai/conversations/${id}`)
}

/**
 * 删除会话
 */
export function deleteConversation(id) {
  if (!id) return Promise.reject(new Error('会话ID不能为空'))
  return request.delete(`/ai/conversations/${id}`)
}

/**
 * AI 训练点评
 */
export function reviewTraining(data) {
  return request.post('/ai/training/review', data)
}

/**
 * 知识库问答
 */
export function knowledgeQA(question) {
  return request.get('/ai/knowledge/qa', { params: { question } })
}

/**
 * 获取今日 AI 调用次数与剩余额度
 */
export function getAiUsage() {
  return request.get('/ai/usage')
}
