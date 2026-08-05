<template>
  <el-dialog v-model="dialogVisible" title="修改密码" width="480px" draggable destroy-on-close @closed="resetForm">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="85px" status-icon>
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input
          v-model="formData.oldPassword"
          type="password"
          placeholder="请输入当前密码"
          show-password
          autocomplete="current-password"
        />
      </el-form-item>

      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="formData.newPassword"
          type="password"
          placeholder="请输入新密码（6-20位）"
          show-password
          autocomplete="new-password"
          @input="checkPasswordStrength"
        />
        <!-- 密码强度指示器 -->
        <div v-if="formData.newPassword" class="password-strength">
          <span class="strength-label">密码强度：</span>
          <div class="strength-bars">
            <div
              v-for="i in 3"
              :key="i"
              class="strength-bar"
              :class="{ active: passwordStrength >= i }"
              :style="{ backgroundColor: i <= passwordStrength ? strengthColor : '' }"
            />
          </div>
          <span class="strength-text" :style="{ color: strengthColor }">{{ strengthText }}</span>
        </div>
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="formData.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
          autocomplete="new-password"
        />
      </el-form-item>

      <!-- 密码要求提示 -->
      <div class="password-tips">
        <p class="tips-title">密码要求：</p>
        <ul class="tips-list">
          <li :class="{ valid: formData.newPassword?.length >= 6 }">
            <el-icon><Check v-if="formData.newPassword?.length >= 6" /><Close v-else /></el-icon>
            长度至少 6 位
          </li>
          <li :class="{ valid: hasNumber }">
            <el-icon><Check v-if="hasNumber" /><Close v-else /></el-icon>
            包含数字
          </li>
          <li :class="{ valid: hasLetter }">
            <el-icon><Check v-if="hasLetter" /><Close v-else /></el-icon>
            包含字母
          </li>
        </ul>
      </div>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from "vue";
import { ElMessage } from "element-plus";
import { Check, Close } from "@element-plus/icons-vue";

const dialogVisible = ref(false);
const loading = ref(false);
const formRef = ref(null);

const formData = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});

// 密码强度
const passwordStrength = ref(0);

// 检查密码是否包含数字
const hasNumber = computed(() => /\d/.test(formData.newPassword));

// 检查密码是否包含字母
const hasLetter = computed(() => /[a-zA-Z]/.test(formData.newPassword));

// 检查密码强度
const checkPasswordStrength = () => {
  const pwd = formData.newPassword;
  let strength = 0;

  if (pwd.length >= 6) strength++;
  if (pwd.length >= 10) strength++;
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++;
  if (/\d/.test(pwd)) strength++;
  if (/[!@#$%^&*(),.?":{}|<>]/.test(pwd)) strength++;

  // 归一化到 1-3
  if (strength <= 2) passwordStrength.value = 1;
  else if (strength <= 3) passwordStrength.value = 2;
  else passwordStrength.value = 3;
};

// 强度颜色
const strengthColor = computed(() => {
  const colors = ["", "#f56c6c", "#e6a23c", "#67c23a"];
  return colors[passwordStrength.value];
});

// 强度文字
const strengthText = computed(() => {
  const texts = ["", "弱", "中", "强"];
  return texts[passwordStrength.value];
});

// 验证新密码
const validateNewPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入新密码"));
  } else if (value.length < 6) {
    callback(new Error("密码长度不能少于 6 位"));
  } else if (value.length > 20) {
    callback(new Error("密码长度不能超过 20 位"));
  } else if (value === formData.oldPassword) {
    callback(new Error("新密码不能与当前密码相同"));
  } else {
    // 如果确认密码已填写，重新验证确认密码
    if (formData.confirmPassword) {
      formRef.value?.validateField("confirmPassword");
    }
    callback();
  }
};

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请再次输入新密码"));
  } else if (value !== formData.newPassword) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 表单验证规则
const rules = {
  oldPassword: [
    { required: true, message: "请输入当前密码", trigger: "blur" }
  ],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: "blur" }
  ]
};

// 打开弹窗
const openDialog = () => {
  dialogVisible.value = true;
};

// 重置表单
const resetForm = () => {
  formData.oldPassword = "";
  formData.newPassword = "";
  formData.confirmPassword = "";
  passwordStrength.value = 0;
  formRef.value?.resetFields();
};

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    // 模拟 API 请求
    await new Promise(resolve => setTimeout(resolve, 500));

    ElMessage.success("密码修改成功，请重新登录");
    dialogVisible.value = false;

    // 实际项目中这里应该调用 API 并跳转到登录页
  } finally {
    loading.value = false;
  }
};

defineExpose({ openDialog });
</script>

<style scoped>
.password-strength {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
}

.strength-label {
  color: #909399;
  margin-right: 8px;
}

.strength-bars {
  display: flex;
  gap: 4px;
  margin-right: 8px;
}

.strength-bar {
  width: 40px;
  height: 6px;
  background: #e4e7ed;
  border-radius: 3px;
  transition: background-color 0.3s;
}

.strength-text {
  font-weight: 500;
}

.password-tips {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 12px 16px;
  margin-top: 8px;
}

.tips-title {
  font-size: 13px;
  color: #606266;
  margin: 0 0 8px 0;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

.tips-list li .el-icon {
  font-size: 14px;
  color: #c0c4cc;
}

.tips-list li.valid {
  color: #67c23a;
}

.tips-list li.valid .el-icon {
  color: #67c23a;
}
</style>
