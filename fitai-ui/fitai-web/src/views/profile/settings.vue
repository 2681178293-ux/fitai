<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUserInfo } from '@/api/auth'
import { uploadAvatar } from '@/api/file'
import { User, Phone, Edit, Key, Bell, Lock, Camera } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const avatarUploading = ref(false)

const form = ref({
  nickname: '',
  phone: '',
  gender: 0,
  birthday: '',
  height: null,
  weight: null
})

const showPasswordDialog = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载用户信息
const loadUserInfo = () => {
  if (userStore.userInfo) {
    form.value = {
      nickname: userStore.userInfo.nickname || '',
      phone: userStore.userInfo.phone || '',
      gender: userStore.userInfo.gender ?? 0,
      birthday: userStore.userInfo.birthday || '',
      height: userStore.userInfo.height ?? null,
      weight: userStore.userInfo.weight ?? null
    }
  }
}

// 保存基本信息
const handleSave = async () => {
  const id = userStore.userInfo?.id
  if (!id) {
    ElMessage.error('用户信息不完整，请重新登录')
    return
  }
  loading.value = true
  try {
    const { phone, ...rest } = form.value
    await updateUserInfo({ id, ...rest })
    ElMessage.success('保存成功')
    userStore.fetchUserInfo()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    loading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }
  
  try {
    // TODO: 调用后端 API
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (error) {
    ElMessage.error(error.message || '修改失败')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定退出登录吗？', '提示')
    userStore.logout()
    router.push('/login')
  } catch {}
}

// 注销账号
const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm('注销后账号将无法恢复，确定注销吗？', '警告', {
      type: 'warning',
      confirmButtonText: '确定注销',
      confirmButtonClass: 'el-button--danger'
    })
    // TODO: 调用注销 API
    ElMessage.success('账号已注销')
    userStore.logout()
    router.push('/login')
  } catch {}
}

// 头像上传
const handleAvatarChange = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB')
    return
  }

  avatarUploading.value = true
  try {
    const avatarUrl = await uploadAvatar(file)
    const id = userStore.userInfo?.id
    if (!id) {
      ElMessage.error('用户信息不完整，请重新登录')
      return
    }
    await updateUserInfo({ id, avatar: avatarUrl })
    userStore.fetchUserInfo()
    ElMessage.success('头像更新成功')
  } catch (error) {
    ElMessage.error(error.message || '上传失败')
  } finally {
    avatarUploading.value = false
    event.target.value = ''
  }
}

onMounted(async () => {
  if (!userStore.userInfo?.id) {
    await userStore.fetchUserInfo()
  }
  loadUserInfo()
})
</script>

<template>
  <div class="settings-page p-4 max-w-2xl mx-auto">
    <h1 class="text-xl font-bold mb-6">账号设置</h1>

    <!-- 头像设置 -->
    <div class="section bg-white rounded-lg p-4 mb-4 shadow-sm">
      <h2 class="font-medium mb-4 flex items-center gap-2">
        <el-icon><Camera /></el-icon>
        头像设置
      </h2>
      <div class="flex items-center gap-4">
        <div class="avatar-wrapper" :class="{ 'is-loading': avatarUploading }">
          <el-avatar :size="80" :src="userStore.avatar">
            {{ userStore.nickname?.[0] || 'U' }}
          </el-avatar>
          <label class="avatar-upload-mask">
            <input type="file" accept="image/*" @change="handleAvatarChange" :disabled="avatarUploading" />
            <el-icon><Camera /></el-icon>
          </label>
        </div>
        <div class="text-sm text-gray-500">
          <p>点击头像更换</p>
          <p>支持 jpg、png 格式，大小不超过 2MB</p>
        </div>
      </div>
    </div>

    <!-- 基本信息 -->
    <div class="section bg-white rounded-lg p-4 mb-4 shadow-sm">
      <h2 class="font-medium mb-4 flex items-center gap-2">
        <el-icon><User /></el-icon>
        基本信息
      </h2>
      
      <el-form :model="form" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :value="0">未设置</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model="form.birthday" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="身高(cm)">
          <el-input-number v-model="form.height" :min="100" :max="250" />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :min="30" :max="200" :precision="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 账号安全 -->
    <div class="section bg-white rounded-lg p-4 mb-4 shadow-sm">
      <h2 class="font-medium mb-4 flex items-center gap-2">
        <el-icon><Lock /></el-icon>
        账号安全
      </h2>
      
      <div class="space-y-4">
        <div class="flex justify-between items-center py-2 border-b">
          <div>
            <div class="font-medium">手机号</div>
            <div class="text-sm text-gray-500">{{ form.phone || '未绑定' }}</div>
          </div>
          <el-button text type="primary">修改</el-button>
        </div>
        
        <div class="flex justify-between items-center py-2 border-b">
          <div>
            <div class="font-medium">登录密码</div>
            <div class="text-sm text-gray-500">已设置</div>
          </div>
          <el-button text type="primary" @click="showPasswordDialog = true">修改</el-button>
        </div>
      </div>
    </div>

    <!-- 其他操作 -->
    <div class="section bg-white rounded-lg p-4 shadow-sm">
      <div class="space-y-2">
        <el-button class="w-full" @click="handleLogout">退出登录</el-button>
        <el-button class="w-full" type="danger" text @click="handleDeleteAccount">
          注销账号
        </el-button>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.avatar-wrapper {
  position: relative;
  display: inline-block;
}

.avatar-wrapper.is-loading {
  opacity: 0.6;
  pointer-events: none;
}

.avatar-upload-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  opacity: 0;
  cursor: pointer;
  transition: opacity 0.3s;
  color: white;
  font-size: 24px;
}

.avatar-wrapper:hover .avatar-upload-mask {
  opacity: 1;
}

.avatar-upload-mask input {
  display: none;
}
</style>
