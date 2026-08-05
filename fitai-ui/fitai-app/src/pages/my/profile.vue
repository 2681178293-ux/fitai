<template>
  <view class="page">
    <view v-if="loading" class="loading-wrap">
      <text>加载中...</text>
    </view>
    <view v-else-if="user" class="content">
      <view class="avatar-section" @click="chooseAvatar">
        <image :src="user.avatar || '/static/default-avatar.png'" class="avatar" mode="aspectFill" />
        <text class="avatar-tip">点击更换头像</text>
      </view>

      <view class="form-section">
        <view class="form-item">
          <text class="label">昵称</text>
          <input v-model="form.nickname" class="input" placeholder="请输入昵称" />
        </view>
        <view class="form-item">
          <text class="label">性别</text>
          <picker :range="genderOptions" range-key="label" @change="onGenderChange">
            <view class="picker">{{ genderOptions.find(g => g.value === form.gender)?.label || '请选择' }}</view>
          </picker>
        </view>
        <view class="form-item">
          <text class="label">生日</text>
          <picker mode="date" :value="form.birthday" @change="onBirthdayChange">
            <view class="picker">{{ form.birthday || '请选择' }}</view>
          </picker>
        </view>
        <view class="form-item">
          <text class="label">身高(cm)</text>
          <input v-model.number="form.height" type="digit" class="input" placeholder="请输入身高" />
        </view>
        <view class="form-item">
          <text class="label">体重(kg)</text>
          <input v-model.number="form.weight" type="digit" class="input" placeholder="请输入体重" />
        </view>
      </view>

      <view class="save-btn" @click="handleSave">
        {{ saving ? '保存中...' : '保存' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getUserInfo, updateUserInfo } from '@/api/user'

const userStore = useUserStore()
const loading = ref(true)
const saving = ref(false)
const user = ref(null)
const form = ref({
  nickname: '',
  avatar: '',
  gender: 0,
  birthday: '',
  height: null,
  weight: null
})

const genderOptions = [
  { value: 0, label: '未设置' },
  { value: 1, label: '男' },
  { value: 2, label: '女' }
]

onMounted(() => {
  loadUser()
})

async function loadUser() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  loading.value = true
  try {
    const res = await getUserInfo()
    user.value = res.data || {}
    form.value = {
      nickname: user.value.nickname || '',
      avatar: user.value.avatar || '',
      gender: user.value.gender ?? 0,
      birthday: user.value.birthday ? String(user.value.birthday).slice(0, 10) : '',
      height: user.value.height ?? null,
      weight: user.value.weight ?? null
    }
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  loading.value = false
}

function onGenderChange(e) {
  const idx = e.detail.value
  form.value.gender = genderOptions[idx].value
}

function onBirthdayChange(e) {
  form.value.birthday = e.detail.value
}

function chooseAvatar() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempPath = res.tempFilePaths[0]
      // 简化：可先上传到服务器，这里暂用本地路径（实际需调用上传接口）
      form.value.avatar = tempPath
      user.value = { ...user.value, avatar: tempPath }
      uni.showToast({ title: '请保存后生效', icon: 'none' })
    }
  })
}

async function handleSave() {
  if (saving.value) return
  saving.value = true
  try {
    await updateUserInfo({
      id: user.value.id,
      nickname: form.value.nickname,
      avatar: form.value.avatar,
      gender: form.value.gender,
      birthday: form.value.birthday || null,
      height: form.value.height,
      weight: form.value.weight
    })
    userStore.setUserInfo({ ...userStore.userInfo, ...form.value })
    uni.showToast({ title: '保存成功' })
  } catch (e) {
    uni.showToast({ title: e.msg || '保存失败', icon: 'none' })
  }
  saving.value = false
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.loading-wrap {
  padding: 60px;
  text-align: center;
  color: #999;
}

.content {
  padding: 16px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.avatar-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.form-section {
  background: #fff;
  border-radius: 12px;
  padding: 0 16px;
  margin-bottom: 24px;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f3f4f6;

  &:last-child {
    border-bottom: none;
  }
}

.label {
  width: 80px;
  font-size: 15px;
  color: #333;
}

.input {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.picker {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.save-btn {
  height: 48px;
  line-height: 48px;
  text-align: center;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: #fff;
  font-size: 16px;
  border-radius: 12px;
}
</style>
