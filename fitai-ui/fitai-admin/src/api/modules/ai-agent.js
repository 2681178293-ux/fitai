import http from "@/api";

/**
 * @name AI Agent / 工具 / 数据集 / 评测 / RAG评测 / 护栏 / 向量索引 管理模块
 * 独立文件，避免与 ai.js 并行改动冲突。路径、方法、字段与后端控制器严格一致。
 */

// ==================== 复用：模型 / 提示词 下拉选项 ====================
export const getModelOptionsApi = () => http.get(`/admin/ai/model/list`);
export const getPromptOptionsApi = params => http.get(`/admin/ai/prompt/list`, params);

// ==================== AI Agent（/admin/ai/agent） ====================
export const getAgentPageApi = params => http.get(`/admin/ai/agent/page`, params);
export const getAgentDetailApi = agentId => http.get(`/admin/ai/agent/${agentId}`);
export const addAgentApi = data => http.post(`/admin/ai/agent`, data);
export const updateAgentApi = (agentId, data) => http.put(`/admin/ai/agent/${agentId}`, data);
export const deleteAgentApi = agentIds => http.delete(`/admin/ai/agent/${agentIds}`);
export const changeAgentStatusApi = (agentId, status) =>
  http.put(`/admin/ai/agent/${agentId}/status?status=${status}`);
export const runAgentApi = (agentId, data) =>
  http.post(`/admin/ai/agent/${agentId}/run`, data, { cancel: false, timeout: 180000 });

// ==================== AI 工具（/admin/ai/tool） ====================
export const getToolPageApi = params => http.get(`/admin/ai/tool/page`, params);
export const getToolOptionsApi = () => http.get(`/admin/ai/tool/options`);
export const getToolDetailApi = toolId => http.get(`/admin/ai/tool/${toolId}`);
export const addToolApi = data => http.post(`/admin/ai/tool`, data);
export const updateToolApi = (toolId, data) => http.put(`/admin/ai/tool/${toolId}`, data);
export const deleteToolApi = toolIds => http.delete(`/admin/ai/tool/${toolIds}`);
export const changeToolStatusApi = (toolId, status) =>
  http.put(`/admin/ai/tool/${toolId}/status?status=${status}`);

// ==================== 数据集（/admin/ai/dataset） ====================
export const getDatasetPageApi = params => http.get(`/admin/ai/dataset/page`, params);
export const getDatasetOptionsApi = datasetType => http.get(`/admin/ai/dataset/options`, { datasetType });
export const getDatasetDetailApi = datasetId => http.get(`/admin/ai/dataset/${datasetId}`);
export const addDatasetApi = data => http.post(`/admin/ai/dataset`, data);
export const updateDatasetApi = data => http.put(`/admin/ai/dataset`, data);
export const deleteDatasetApi = ids => http.delete(`/admin/ai/dataset/${ids}`);
// 数据集行
export const getDatasetRowPageApi = params => http.get(`/admin/ai/dataset/row/page`, params);
export const addDatasetRowApi = data => http.post(`/admin/ai/dataset/row`, data);
export const updateDatasetRowApi = data => http.put(`/admin/ai/dataset/row`, data);
export const deleteDatasetRowApi = (ids, datasetId) =>
  http.delete(`/admin/ai/dataset/row/${ids}`, { datasetId });

// ==================== 模型评测（/admin/ai/evaluation） ====================
export const getEvaluationPageApi = params => http.get(`/admin/ai/evaluation/page`, params);
export const getEvaluationDetailApi = evaluationId => http.get(`/admin/ai/evaluation/${evaluationId}`);
export const addEvaluationApi = data => http.post(`/admin/ai/evaluation`, data);
export const updateEvaluationApi = data => http.put(`/admin/ai/evaluation`, data);
export const deleteEvaluationApi = ids => http.delete(`/admin/ai/evaluation/${ids}`);
export const runEvaluationApi = evaluationId => http.post(`/admin/ai/evaluation/${evaluationId}/run`);

// ==================== RAG 评测记录（/admin/ai/rag-eval） ====================
export const runRagEvalApi = data =>
  http.post(`/admin/ai/rag-eval/run`, data, { cancel: false, timeout: 300000 });
export const getRagEvalPageApi = params => http.get(`/admin/ai/rag-eval/page`, params);
export const getRagEvalDetailApi = evalId => http.get(`/admin/ai/rag-eval/${evalId}`);
export const addRagEvalApi = data => http.post(`/admin/ai/rag-eval`, data);
export const deleteRagEvalApi = ids => http.delete(`/admin/ai/rag-eval/${ids}`);

// ==================== 内容护栏（/admin/ai/guardrail） ====================
export const getGuardrailPageApi = params => http.get(`/admin/ai/guardrail/page`, params);
export const getGuardrailDetailApi = guardrailId => http.get(`/admin/ai/guardrail/${guardrailId}`);
export const addGuardrailApi = data => http.post(`/admin/ai/guardrail`, data);
export const updateGuardrailApi = (guardrailId, data) => http.put(`/admin/ai/guardrail/${guardrailId}`, data);
export const changeGuardrailStatusApi = (guardrailId, status) =>
  http.put(`/admin/ai/guardrail/${guardrailId}/status?status=${status}`);
export const deleteGuardrailApi = ids => http.delete(`/admin/ai/guardrail/${ids}`);
export const checkGuardrailApi = data => http.post(`/admin/ai/guardrail/check`, data, { cancel: false });

// ==================== 向量索引（/admin/ai/vector-index） ====================
export const getVectorIndexPageApi = params => http.get(`/admin/ai/vector-index/page`, params);
export const getVectorIndexOptionsApi = () => http.get(`/admin/ai/vector-index/options`);
export const getVectorIndexDetailApi = indexId => http.get(`/admin/ai/vector-index/${indexId}`);
export const addVectorIndexApi = data => http.post(`/admin/ai/vector-index`, data);
export const updateVectorIndexApi = data => http.put(`/admin/ai/vector-index`, data);
export const rebuildVectorIndexApi = indexId => http.put(`/admin/ai/vector-index/${indexId}/rebuild`);
export const syncVectorIndexApi = indexId => http.put(`/admin/ai/vector-index/${indexId}/sync`);
export const deleteVectorIndexApi = indexIds => http.delete(`/admin/ai/vector-index/${indexIds}`);
