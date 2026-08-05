<template>
  <el-dropdown trigger="click">
    <div class="avatar">
      <img src="@/assets/images/avatar.gif" alt="avatar" />
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <!-- 用户信息头部 -->
        <div class="user-header">
          <img src="@/assets/images/avatar.gif" alt="avatar" class="user-avatar" />
          <div class="user-info">
            <span class="user-name">{{ username }}</span>
            <span class="user-role">管理员</span>
          </div>
        </div>
        <el-dropdown-item @click="openDialog('infoRef')">
          <el-icon><User /></el-icon>{{ $t("header.personalData") }}
        </el-dropdown-item>
        <el-dropdown-item @click="openDialog('passwordRef')">
          <el-icon><Edit /></el-icon>{{ $t("header.changePassword") }}
        </el-dropdown-item>
        <el-dropdown-item divided @click="logout">
          <el-icon><SwitchButton /></el-icon>{{ $t("header.logout") }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <!-- infoDialog -->
  <InfoDialog ref="infoRef"></InfoDialog>
  <!-- passwordDialog -->
  <PasswordDialog ref="passwordRef"></PasswordDialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { LOGIN_URL } from "@/config";
import { useRouter } from "vue-router";
import { logoutApi } from "@/api/modules/login";
import { useUserStore } from "@/stores/modules/user";
import { ElMessageBox, ElMessage } from "element-plus";
import InfoDialog from "./InfoDialog.vue";
import PasswordDialog from "./PasswordDialog.vue";

const router = useRouter();
const userStore = useUserStore();
const username = computed(() => userStore.userInfo.name);

// 退出登录
const logout = () => {
  ElMessageBox.confirm("您是否确认退出登录?", "温馨提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async () => {
    // 1.执行退出登录接口
    await logoutApi();

    // 2.清除 Token
    userStore.setToken("");

    // 3.重定向到登陆页
    router.replace(LOGIN_URL);
    ElMessage.success("退出登录成功！");
  });
};

// 打开修改密码和个人信息弹窗
const infoRef = ref(null);
const passwordRef = ref(null);
const openDialog = ref => {
  if (ref == "infoRef") infoRef.value?.openDialog();
  if (ref == "passwordRef") passwordRef.value?.openDialog();
};
</script>

<style scoped>
.avatar {
  width: 40px;
  height: 40px;
  overflow: hidden;
  cursor: pointer;
  border-radius: 50%;
  border: 2px solid #e5e7eb;
  transition: border-color 0.3s;
}
.avatar:hover {
  border-color: var(--el-color-primary);
}
.avatar img {
  width: 100%;
  height: 100%;
}

/* 下拉菜单用户头部样式 */
.user-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  min-width: 180px;
}
.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 12px;
  border: 2px solid #e5e7eb;
}
.user-info {
  display: flex;
  flex-direction: column;
}
.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}
.user-role {
  font-size: 12px;
  color: #6b7280;
}
</style>
