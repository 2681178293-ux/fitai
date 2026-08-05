<template>
  <el-dialog v-model="dialogVisible" title="个人信息" width="550px" draggable destroy-on-close>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <!-- 头像上传 -->
      <el-form-item label="头像">
        <div class="avatar-upload">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            accept="image/*"
          >
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar-preview" />
            <div v-else class="avatar-placeholder">
              <el-icon :size="28"><Plus /></el-icon>
              <span>上传头像</span>
            </div>
            <div class="avatar-hover">
              <el-icon :size="20"><Camera /></el-icon>
              <span>更换头像</span>
            </div>
          </el-upload>
          <div class="avatar-tips">
            <p>支持 JPG、PNG 格式</p>
            <p>建议尺寸 200x200 像素</p>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="用户名">
        <el-input v-model="formData.username" disabled placeholder="用户名不可修改" />
      </el-form-item>

      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="formData.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="formData.gender">
          <el-radio :value="1">男</el-radio>
          <el-radio :value="2">女</el-radio>
          <el-radio :value="0">保密</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>

      <el-form-item label="个人简介">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="介绍一下自己吧..."
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">保存修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Camera } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();

const dialogVisible = ref(false);
const loading = ref(false);
const formRef = ref(null);

const formData = reactive({
  avatar: "",
  username: "",
  nickname: "",
  gender: 0,
  email: "",
  phone: "",
  description: ""
});

// 表单验证规则
const rules = {
  nickname: [
    { required: true, message: "请输入昵称", trigger: "blur" },
    { min: 2, max: 20, message: "昵称长度在 2 到 20 个字符", trigger: "blur" }
  ],
  email: [
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }
  ]
};

// 打开弹窗
const openDialog = () => {
  // 从 store 获取用户信息填充表单
  const userInfo = userStore.userInfo;
  formData.avatar = userInfo.avatar || new URL("@/assets/images/avatar.gif", import.meta.url).href;
  formData.username = userInfo.name || "admin";
  formData.nickname = userInfo.nickname || userInfo.name || "管理员";
  formData.gender = userInfo.gender ?? 0;
  formData.email = userInfo.email || "";
  formData.phone = userInfo.phone || "";
  formData.description = userInfo.description || "";
  dialogVisible.value = true;
};

// 头像上传处理
const handleAvatarChange = file => {
  const isImage = file.raw.type.startsWith("image/");
  const isLt2M = file.raw.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("只能上传图片文件！");
    return;
  }
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 2MB！");
    return;
  }

  // 预览图片
  const reader = new FileReader();
  reader.onload = e => {
    formData.avatar = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    // 模拟 API 请求
    await new Promise(resolve => setTimeout(resolve, 500));

    // 更新 store 中的用户信息
    userStore.setUserInfo({
      ...userStore.userInfo,
      nickname: formData.nickname,
      gender: formData.gender,
      email: formData.email,
      phone: formData.phone,
      description: formData.description
    });

    ElMessage.success("个人信息修改成功");
    dialogVisible.value = false;
  } finally {
    loading.value = false;
  }
};

defineExpose({ openDialog });
</script>

<style scoped>
.avatar-upload {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.avatar-uploader {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.avatar-uploader :deep(.el-upload) {
  width: 100%;
  height: 100%;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  background: #f5f7fa;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  color: #8c939d;
  font-size: 12px;
  gap: 4px;
}

.avatar-hover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 12px;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 50%;
}

.avatar-uploader:hover .avatar-hover {
  opacity: 1;
}

.avatar-tips {
  color: #909399;
  font-size: 12px;
  line-height: 1.8;
}

.avatar-tips p {
  margin: 0;
}
</style>
