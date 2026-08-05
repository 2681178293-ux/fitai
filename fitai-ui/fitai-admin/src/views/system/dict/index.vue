<template>
  <div class="card p-4">
    <!-- 字典类型管理 -->
    <div class="search-form mb-4">
      <el-form :model="typeSearch" inline>
        <el-form-item label="字典名称">
          <el-input v-model="typeSearch.dictName" placeholder="请输入" clearable class="w-40" />
        </el-form-item>
        <el-form-item label="字典类型">
          <el-input v-model="typeSearch.dictType" placeholder="请输入" clearable class="w-40" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadDictTypes">搜索</el-button>
          <el-button :icon="Refresh" @click="handleTypeReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="flex gap-4">
      <!-- 左侧：字典类型列表 -->
      <div class="w-1/2">
        <div class="flex justify-between items-center mb-2">
          <span class="font-bold">字典类型</span>
          <el-button type="primary" size="small" :icon="Plus" @click="handleAddType">新增类型</el-button>
        </div>
        <el-table :data="dictTypeList" border height="400" highlight-current-row @row-click="handleTypeClick">
          <el-table-column prop="dictName" label="字典名称" />
          <el-table-column prop="dictType" label="字典类型" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? "正常" : "禁用" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click.stop="handleEditType(row)">编辑</el-button>
              <el-popconfirm title="确定删除?" @confirm="handleDeleteType(row)">
                <template #reference>
                  <el-button type="danger" link size="small" @click.stop>删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 右侧：字典数据列表 -->
      <div class="w-1/2">
        <div class="flex justify-between items-center mb-2">
          <span class="font-bold">字典数据 - {{ currentDictType || "请选择字典类型" }}</span>
          <el-button type="primary" size="small" :icon="Plus" :disabled="!currentDictType" @click="handleAddData">新增数据</el-button>
        </div>
        <el-table :data="dictDataList" border height="400">
          <el-table-column prop="dictLabel" label="字典标签" />
          <el-table-column prop="dictValue" label="字典值" />
          <el-table-column prop="dictSort" label="排序" width="80" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? "正常" : "禁用" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditData(row)">编辑</el-button>
              <el-popconfirm title="确定删除?" @confirm="handleDeleteData(row)">
                <template #reference>
                  <el-button type="danger" link size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 字典类型弹窗 -->
    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="400px">
      <el-form :model="typeForm" label-width="80px">
        <el-form-item label="字典名称" required>
          <el-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" required>
          <el-input v-model="typeForm.dictType" placeholder="请输入字典类型" :disabled="!!typeForm.id" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="typeForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="typeForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="typeLoading" @click="handleTypeSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字典数据弹窗 -->
    <el-dialog v-model="dataDialogVisible" :title="dataDialogTitle" width="400px">
      <el-form :model="dataForm" label-width="80px">
        <el-form-item label="字典标签" required>
          <el-input v-model="dataForm.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典值" required>
          <el-input v-model="dataForm.dictValue" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dataForm.dictSort" :min="0" />
        </el-form-item>
        <el-form-item label="样式">
          <el-select v-model="dataForm.listClass" placeholder="请选择" clearable>
            <el-option label="默认" value="" />
            <el-option label="primary" value="primary" />
            <el-option label="success" value="success" />
            <el-option label="warning" value="warning" />
            <el-option label="danger" value="danger" />
            <el-option label="info" value="info" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="dataForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dataForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dataLoading" @click="handleDataSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="dict">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Search, Refresh } from "@element-plus/icons-vue";
import {
  getSysDictTypeList,
  addSysDictType,
  updateSysDictType,
  deleteSysDictType,
  getSysDictDataList,
  addSysDictData,
  updateSysDictData,
  deleteSysDictData
} from "@/api/modules/system";

const typeSearch = ref({ dictName: "", dictType: "" });
const dictTypeList = ref([]);
const dictDataList = ref([]);
const currentDictType = ref("");

// 字典类型弹窗
const typeDialogVisible = ref(false);
const typeDialogTitle = ref("新增字典类型");
const typeForm = ref({});
const typeLoading = ref(false);

// 字典数据弹窗
const dataDialogVisible = ref(false);
const dataDialogTitle = ref("新增字典数据");
const dataForm = ref({});
const dataLoading = ref(false);

const loadDictTypes = async () => {
  try {
    const res = await getSysDictTypeList({
      pageNum: 1,
      pageSize: 100,
      ...typeSearch.value
    });
    dictTypeList.value = res.data?.records || [];
  } catch (error) {
    console.error("加载字典类型失败", error);
  }
};

const loadDictData = async dictType => {
  try {
    const res = await getSysDictDataList({
      pageNum: 1,
      pageSize: 100,
      dictType
    });
    dictDataList.value = res.data?.records || [];
  } catch (error) {
    console.error("加载字典数据失败", error);
  }
};

onMounted(() => {
  loadDictTypes();
});

const handleTypeReset = () => {
  typeSearch.value = { dictName: "", dictType: "" };
  loadDictTypes();
};

const handleTypeClick = row => {
  currentDictType.value = row.dictType;
  loadDictData(row.dictType);
};

// 字典类型操作
const handleAddType = () => {
  typeDialogTitle.value = "新增字典类型";
  typeForm.value = { status: 1 };
  typeDialogVisible.value = true;
};

const handleEditType = row => {
  typeDialogTitle.value = "编辑字典类型";
  typeForm.value = { ...row };
  typeDialogVisible.value = true;
};

const handleDeleteType = async row => {
  try {
    await deleteSysDictType(row.id);
    ElMessage.success("删除成功");
    loadDictTypes();
    if (currentDictType.value === row.dictType) {
      currentDictType.value = "";
      dictDataList.value = [];
    }
  } catch (error) {
    ElMessage.error("删除失败");
  }
};

const handleTypeSubmit = async () => {
  if (!typeForm.value.dictName || !typeForm.value.dictType) {
    ElMessage.warning("请填写完整信息");
    return;
  }
  typeLoading.value = true;
  try {
    if (typeForm.value.id) {
      await updateSysDictType(typeForm.value);
      ElMessage.success("编辑成功");
    } else {
      await addSysDictType(typeForm.value);
      ElMessage.success("新增成功");
    }
    typeDialogVisible.value = false;
    loadDictTypes();
  } catch (error) {
    ElMessage.error("操作失败");
  } finally {
    typeLoading.value = false;
  }
};

// 字典数据操作
const handleAddData = () => {
  dataDialogTitle.value = "新增字典数据";
  dataForm.value = { dictType: currentDictType.value, status: 1, dictSort: 0 };
  dataDialogVisible.value = true;
};

const handleEditData = row => {
  dataDialogTitle.value = "编辑字典数据";
  dataForm.value = { ...row };
  dataDialogVisible.value = true;
};

const handleDeleteData = async row => {
  try {
    await deleteSysDictData(row.id);
    ElMessage.success("删除成功");
    loadDictData(currentDictType.value);
  } catch (error) {
    ElMessage.error("删除失败");
  }
};

const handleDataSubmit = async () => {
  if (!dataForm.value.dictLabel || !dataForm.value.dictValue) {
    ElMessage.warning("请填写完整信息");
    return;
  }
  dataLoading.value = true;
  try {
    if (dataForm.value.id) {
      await updateSysDictData(dataForm.value);
      ElMessage.success("编辑成功");
    } else {
      await addSysDictData(dataForm.value);
      ElMessage.success("新增成功");
    }
    dataDialogVisible.value = false;
    loadDictData(currentDictType.value);
  } catch (error) {
    ElMessage.error("操作失败");
  } finally {
    dataLoading.value = false;
  }
};
</script>

<style scoped>
.card {
  background: var(--el-bg-color);
  border-radius: 4px;
}

.search-form {
  padding: 16px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 16px;
}
</style>
