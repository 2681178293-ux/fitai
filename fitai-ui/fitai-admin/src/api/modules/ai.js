import http from "@/api";

/**
 * @name AI管理模块
 */

// ==================== 知识库 ====================
export const getKnowledgePageApi = params => http.get(`/admin/ai/knowledge/page`, params);
export const addKnowledgeApi = data => http.post(`/admin/ai/knowledge`, data);
export const updateKnowledgeApi = (id, data) => http.put(`/admin/ai/knowledge/${id}`, data);
export const deleteKnowledgeApi = id => http.delete(`/admin/ai/knowledge/${id}`);
export const rebuildKnowledgeVectorApi = () => http.post(`/admin/ai/knowledge/rebuild-vector`);

/** multipart：file + title/category/tags/source */
export const importKnowledgeFileApi = formData =>
  http.post(`/admin/ai/knowledge/import-file`, formData, { cancel: false });

/** multipart：仅 file，解析纯文本不入库（预填表单） */
export const extractKnowledgeFileApi = formData =>
  http.post(`/admin/ai/knowledge/extract-file`, formData, {
    cancel: false,
    timeout: 120000
  });

/** RAG 多轮评测，耗时长 */
export const evaluateKnowledgeRagApi = data =>
  http.post(`/admin/ai/knowledge/rag/evaluate`, data, { cancel: false, timeout: 180000 });

// ==================== 对话记录 ====================
export const getConversationsPageApi = params => http.get(`/admin/ai/conversations/page`, params);
export const getConversationMessagesApi = id => http.get(`/admin/ai/conversations/${id}/messages`);

// ==================== 统计 ====================
export const getAiStatsApi = () => http.get(`/admin/ai/stats`);

// ==================== 模型配置 ====================
export const getModelListApi = () => http.get(`/admin/ai/model/list`);
export const getModelByIdApi = id => http.get(`/admin/ai/model/${id}`);
export const addModelApi = data => http.post(`/admin/ai/model`, data);
export const updateModelApi = (id, data) => http.put(`/admin/ai/model/${id}`, data);
export const deleteModelApi = id => http.delete(`/admin/ai/model/${id}`);

// ==================== 提示词配置 ====================
export const getPromptListApi = params => http.get(`/admin/ai/prompt/list`, params);
export const getPromptByIdApi = id => http.get(`/admin/ai/prompt/${id}`);
export const addPromptApi = data => http.post(`/admin/ai/prompt`, data);
export const updatePromptApi = (id, data) => http.put(`/admin/ai/prompt/${id}`, data);
export const deletePromptApi = id => http.delete(`/admin/ai/prompt/${id}`);
