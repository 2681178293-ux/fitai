<template>
  <div class="p-5">
    <div class="mb-4">
      <h1 class="text-xl font-semibold text-gray-800">RAG 质量评估</h1>
      <p class="mt-1 text-sm text-gray-500">
        基于当前知识库做一次检索增强问答，并由模型对「相关性、事实性、正确性、忠实度」等维度打分（会多次调用大模型，耗时与费用较高）。
      </p>
    </div>
    <el-card shadow="never" class="mb-4 max-w-3xl">
      <el-form label-position="top">
        <el-form-item label="测试问题" required>
          <el-input v-model="question" type="textarea" :rows="4" placeholder="输入要向知识库提问的内容" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="评测项">
          <el-checkbox v-model="runSpring">Spring AI（相关性、事实性）</el-checkbox>
          <el-checkbox v-model="runAlibaba" class="ml-4">阿里云评估（答案相关性、正确性、忠实度）</el-checkbox>
        </el-form-item>
        <el-button type="primary" :loading="loading" :disabled="!question.trim()" @click="runEvaluate">开始评估</el-button>
      </el-form>
    </el-card>

    <el-card v-if="result" shadow="never" class="result-card">
      <template #header><span>评估结果</span></template>
      <div class="mb-4">
        <div class="text-sm text-gray-500">检索片段数：{{ result.retrievedChunkCount ?? 0 }}</div>
        <div v-if="result.retrievedTextPreviews?.length" class="mt-2">
          <div class="text-sm font-medium text-gray-700">检索片段预览</div>
          <ul class="mt-1 list-inside list-disc text-xs text-gray-600">
            <li v-for="(t, i) in result.retrievedTextPreviews" :key="i" class="mb-1">{{ t }}</li>
          </ul>
        </div>
      </div>
      <div class="mb-2 text-sm font-medium text-gray-700">模型回答</div>
      <el-input :model-value="result.answer" type="textarea" :rows="6" readonly class="mb-6" />

      <el-row :gutter="16">
        <el-col v-for="block in metricBlocks" :key="block.key" :xs="24" :sm="12" :md="8" class="mb-4">
          <el-card shadow="hover" class="h-full">
            <div class="text-sm font-medium text-gray-800">{{ block.label }}</div>
            <div v-if="result[block.key]" class="mt-2 space-y-1 text-xs">
              <div>
                <el-tag :type="result[block.key].pass ? 'success' : 'danger'" size="small">
                  {{ result[block.key].pass ? "通过" : "未通过" }}
                </el-tag>
                <span class="ml-2 text-gray-500">score: {{ formatScore(result[block.key].score) }}</span>
              </div>
              <div v-if="result[block.key].feedback" class="text-gray-600">反馈：{{ result[block.key].feedback }}</div>
              <div v-if="result[block.key].skipReason" class="text-amber-700">说明：{{ result[block.key].skipReason }}</div>
            </div>
            <div v-else class="mt-2 text-xs text-gray-400">无数据</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="aiRagEvaluate">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { evaluateKnowledgeRagApi } from "@/api/modules/ai";

const question = ref("");
const runSpring = ref(true);
const runAlibaba = ref(true);
const loading = ref(false);
const result = ref(null);

const metricBlocks = reactive([
  { key: "relevancy", label: "相关性（Spring AI）" },
  { key: "factChecking", label: "事实性（Spring AI）" },
  { key: "answerRelevancyAlibaba", label: "答案相关性（阿里云）" },
  { key: "answerCorrectnessAlibaba", label: "答案正确性（阿里云）" },
  { key: "answerFaithfulnessAlibaba", label: "忠实度（阿里云）" }
]);

function formatScore(s) {
  if (s == null || Number.isNaN(Number(s))) return "-";
  return Number(s).toFixed(4);
}

async function runEvaluate() {
  const q = question.value.trim();
  if (!q) {
    ElMessage.warning("请输入问题");
    return;
  }
  loading.value = true;
  result.value = null;
  try {
    const res = await evaluateKnowledgeRagApi({
      question: q,
      runSpringAiEvaluators: runSpring.value,
      runAlibabaEvaluators: runAlibaba.value
    });
    result.value = res.data || null;
    if (!result.value) ElMessage.warning("未返回评估数据");
    else ElMessage.success("评估完成");
  } catch (e) {
    ElMessage.error(e?.msg || e?.message || "评估失败");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.result-card {
  max-width: 1200px;
}
</style>
