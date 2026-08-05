<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Location } from '@element-plus/icons-vue'

const loading = ref(false)
const addresses = ref([])
const showDialog = ref(false)
const isEdit = ref(false)

const form = ref({
  id: null,
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

// 模拟地址数据
const mockAddresses = [
  {
    id: 1,
    name: '张三',
    phone: '13800138000',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detail: '科技园xxx大厦',
    isDefault: true
  }
]

// 加载地址列表
const loadAddresses = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    addresses.value = mockAddresses
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  }
  showDialog.value = true
}

// 打开编辑弹窗
const openEditDialog = (address) => {
  isEdit.value = true
  form.value = { ...address }
  showDialog.value = true
}

// 保存地址
const handleSave = async () => {
  if (!form.value.name || !form.value.phone || !form.value.detail) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    if (isEdit.value) {
      const idx = addresses.value.findIndex(a => a.id === form.value.id)
      if (idx > -1) {
        addresses.value[idx] = { ...form.value }
      }
      ElMessage.success('修改成功')
    } else {
      addresses.value.push({
        ...form.value,
        id: Date.now()
      })
      ElMessage.success('添加成功')
    }
    showDialog.value = false
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除地址
const handleDelete = async (address) => {
  try {
    await ElMessageBox.confirm('确定删除这个地址吗？', '提示')
    addresses.value = addresses.value.filter(a => a.id !== address.id)
    ElMessage.success('删除成功')
  } catch {}
}

// 设为默认
const setDefault = (address) => {
  addresses.value.forEach(a => {
    a.isDefault = a.id === address.id
  })
  ElMessage.success('设置成功')
}

onMounted(() => {
  loadAddresses()
})
</script>

<template>
  <div class="address-page p-4">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-xl font-bold">收货地址</h1>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">
        新增地址
      </el-button>
    </div>

    <div v-loading="loading" class="address-list space-y-4">
      <el-empty v-if="addresses.length === 0" description="暂无收货地址" />
      
      <div 
        v-for="address in addresses" 
        :key="address.id"
        class="address-card bg-white rounded-lg p-4 shadow-sm"
      >
        <div class="flex items-start gap-3">
          <el-icon class="text-xl text-gray-400 mt-1"><Location /></el-icon>
          <div class="flex-1">
            <div class="flex items-center gap-2">
              <span class="font-medium">{{ address.name }}</span>
              <span class="text-gray-500">{{ address.phone }}</span>
              <el-tag v-if="address.isDefault" size="small" type="danger">默认</el-tag>
            </div>
            <p class="text-gray-600 mt-1">
              {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
            </p>
          </div>
          <div class="flex gap-2">
            <el-button text :icon="Edit" @click="openEditDialog(address)">编辑</el-button>
            <el-button text type="danger" :icon="Delete" @click="handleDelete(address)">删除</el-button>
          </div>
        </div>
        <div v-if="!address.isDefault" class="mt-3 pt-3 border-t">
          <el-button text type="primary" @click="setDefault(address)">设为默认</el-button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑地址' : '新增地址'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区">
          <div class="flex gap-2">
            <el-input v-model="form.province" placeholder="省份" class="flex-1" />
            <el-input v-model="form.city" placeholder="城市" class="flex-1" />
            <el-input v-model="form.district" placeholder="区县" class="flex-1" />
          </div>
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="form.detail" type="textarea" :rows="2" placeholder="街道、门牌号等" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
