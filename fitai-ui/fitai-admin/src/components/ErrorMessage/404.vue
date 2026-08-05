<template>
  <div class="not-container">
    <img src="@/assets/images/404.png" class="not-img" alt="404" />
    <div class="not-detail">
      <h2>404</h2>
      <h4>抱歉，您访问的页面不存在~🤷‍♂️🤷‍♀️</h4>
      <el-button type="primary" @click="router.back"> 返回上一页 </el-button>
    </div>
  </div>
</template>

<script setup name="404">
import { useRouter } from "vue-router";
const router = useRouter();
</script>

<style scoped>
.not-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}
.not-img {
  margin-right: 120px;
}
.not-detail {
  display: flex;
  flex-direction: column;
}
.not-detail h2,
.not-detail h4 {
  padding: 0;
  margin: 0;
}
.not-detail h2 {
  font-size: 60px;
  color: var(--el-text-color-primary);
}
.not-detail h4 {
  margin: 30px 0 20px;
  font-size: 19px;
  font-weight: normal;
  color: var(--el-text-color-regular);
}
.not-detail .el-button {
  width: 100px;
}
</style>
