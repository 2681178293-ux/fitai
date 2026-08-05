<template>
  <view class="page">
    <view v-if="loading" class="hint">加载中...</view>
    <template v-else>
      <view v-if="list.length === 0" class="hint">暂无收货地址，请新增</view>
      <view v-for="row in list" :key="String(row.id)" class="card">
        <view class="card-top">
          <view class="card-main">
            <view class="row-name">
              <text class="name">{{ row.name }}</text>
              <text class="phone">{{ row.phone }}</text>
              <text v-if="row.isDefault" class="tag">默认</text>
            </view>
            <text class="full">{{ row.province }}{{ row.city }}{{ row.district }}{{ row.detail }}</text>
          </view>
        </view>
        <view class="card-actions">
          <text v-if="!row.isDefault" class="link" @click="onSetDefault(row)">设为默认</text>
          <text class="link" @click="openEdit(row)">编辑</text>
          <text class="link danger" @click="onDelete(row)">删除</text>
        </view>
      </view>
    </template>

    <view class="fab" @click="openAdd">+ 新增地址</view>

    <view v-if="showForm" class="mask" @click="closeForm">
      <view class="sheet" @click.stop>
        <text class="sheet-title">{{ editingId ? '编辑地址' : '新增地址' }}</text>
        <scroll-view scroll-y class="sheet-body">
          <view class="field">
            <text class="label">收货人</text>
            <input v-model="form.name" class="input" placeholder="姓名" />
          </view>
          <view class="field">
            <text class="label">手机号</text>
            <input v-model="form.phone" class="input" type="number" maxlength="11" placeholder="11位手机号" />
          </view>
          <view class="field">
            <text class="label">省</text>
            <input v-model="form.province" class="input" placeholder="省份" />
          </view>
          <view class="field">
            <text class="label">市</text>
            <input v-model="form.city" class="input" placeholder="城市" />
          </view>
          <view class="field">
            <text class="label">区</text>
            <input v-model="form.district" class="input" placeholder="区县" />
          </view>
          <view class="field">
            <text class="label">详细地址</text>
            <textarea v-model="form.detail" class="textarea" placeholder="街道、门牌号" />
          </view>
          <view class="field row between">
            <text class="label nomargin">设为默认地址</text>
            <switch :checked="form.isDefault" @change="(e) => (form.isDefault = e.detail.value)" />
          </view>
        </scroll-view>
        <view class="sheet-footer">
          <view class="btn ghost" @click="closeForm">取消</view>
          <view class="btn primary" @click="submitForm">保存</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { listAddress, addAddress, updateAddress, deleteAddress, setAddressDefault } from '@/api/address'
import { getToken } from '@/utils/authStorage'

const loading = ref(true)
const list = ref([])
const showForm = ref(false)
const editingId = ref(null)
const form = ref(emptyForm())

function emptyForm() {
  return {
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  }
}

async function loadList() {
  if (!getToken()) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  loading.value = true
  try {
    const res = await listAddress()
    list.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function openAdd() {
  editingId.value = null
  form.value = emptyForm()
  showForm.value = true
}

function openEdit(row) {
  editingId.value = row.id
  form.value = {
    name: row.name || '',
    phone: row.phone || '',
    province: row.province || '',
    city: row.city || '',
    district: row.district || '',
    detail: row.detail || '',
    isDefault: !!row.isDefault
  }
  showForm.value = true
}

function closeForm() {
  showForm.value = false
}

async function submitForm() {
  const f = form.value
  if (!f.name || !f.phone || !f.province || !f.city || !f.district || !f.detail) {
    uni.showToast({ title: '请填写完整', icon: 'none' })
    return
  }
  try {
    const payload = {
      name: f.name.trim(),
      phone: f.phone.trim(),
      province: f.province.trim(),
      city: f.city.trim(),
      district: f.district.trim(),
      detail: f.detail.trim(),
      isDefault: !!f.isDefault
    }
    if (editingId.value) {
      await updateAddress(editingId.value, payload)
      uni.showToast({ title: '已保存' })
    } else {
      await addAddress(payload)
      uni.showToast({ title: '已添加' })
    }
    closeForm()
    await loadList()
  } catch (e) {
    uni.showToast({ title: e.msg || '保存失败', icon: 'none' })
  }
}

async function onDelete(row) {
  const ok = await new Promise((resolve) => {
    uni.showModal({
      title: '删除地址',
      content: '确定删除该收货地址？',
      success: (r) => resolve(r.confirm)
    })
  })
  if (!ok) return
  try {
    await deleteAddress(row.id)
    uni.showToast({ title: '已删除' })
    await loadList()
  } catch (e) {
    uni.showToast({ title: e.msg || '删除失败', icon: 'none' })
  }
}

async function onSetDefault(row) {
  try {
    await setAddressDefault(row.id)
    uni.showToast({ title: '已设为默认' })
    await loadList()
  } catch (e) {
    uni.showToast({ title: e.msg || '操作失败', icon: 'none' })
  }
}

onMounted(() => {
  if (!getToken()) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  loadList()
})

onShow(() => {
  if (getToken()) {
    loadList()
  }
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 12px;
  padding-bottom: 88px;
  background: #f5f5f5;
}
.hint {
  text-align: center;
  padding: 40px;
  color: #999;
}
.card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
}
.card-top {
  display: flex;
  justify-content: space-between;
}
.row-name {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}
.name {
  font-weight: 600;
  font-size: 16px;
  color: #111;
}
.phone {
  font-size: 14px;
  color: #666;
}
.tag {
  font-size: 11px;
  color: #ef4444;
  border: 1px solid #fecaca;
  padding: 2px 6px;
  border-radius: 4px;
}
.full {
  font-size: 14px;
  color: #374151;
  line-height: 1.5;
}
.card-actions {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}
.link {
  font-size: 14px;
  color: #2563eb;
}
.link.danger {
  color: #ef4444;
}
.fab {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: 24px;
  height: 48px;
  line-height: 48px;
  text-align: center;
  background: #2563eb;
  color: #fff;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
}
.mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.sheet {
  width: 100%;
  max-height: 85vh;
  background: #fff;
  border-radius: 16px 16px 0 0;
  display: flex;
  flex-direction: column;
}
.sheet-title {
  padding: 16px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid #f3f4f6;
}
.sheet-body {
  max-height: 55vh;
  padding: 12px 16px;
}
.field {
  margin-bottom: 12px;
}
.field.row {
  display: flex;
  align-items: center;
}
.label {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 6px;
}
.input {
  width: 100%;
  height: 44px;
  padding: 0 12px;
  background: #f9fafb;
  border-radius: 8px;
  font-size: 15px;
}
.textarea {
  width: 100%;
  min-height: 72px;
  padding: 10px 12px;
  background: #f9fafb;
  border-radius: 8px;
  font-size: 15px;
}
.field.row.between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.label.nomargin {
  margin-bottom: 0;
}
.sheet-footer {
  display: flex;
  gap: 12px;
  padding: 12px 16px 24px;
  border-top: 1px solid #f3f4f6;
}
.btn {
  flex: 1;
  height: 44px;
  line-height: 44px;
  text-align: center;
  border-radius: 10px;
  font-size: 15px;
}
.btn.ghost {
  background: #f3f4f6;
  color: #374151;
}
.btn.primary {
  background: #2563eb;
  color: #fff;
}
</style>
