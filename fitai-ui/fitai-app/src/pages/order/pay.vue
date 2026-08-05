<template>
  <view class="page">
    <view v-if="loading" class="loading-wrap">
      <text>正在调起支付...</text>
    </view>
    <view v-else-if="error" class="error-wrap">
      <text class="error-text">{{ error }}</text>
      <view class="action-btn" @click="goBack">返回</view>
    </view>
    <!-- H5 使用 web-view 渲染支付表单 -->
    <web-view v-if="payFormUrl && !error" :src="payFormUrl" class="pay-webview"></web-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { createPayOrder } from '@/api/order'
import { paySeckillOrder } from '@/api/seckill'
import { getMobilePayReturnBaseUrl } from '@/utils/payReturnUrl'

const loading = ref(true)
const error = ref('')
const payFormUrl = ref('')
const routeOptions = ref({})

onLoad((options) => {
  routeOptions.value = options || {}
})

onMounted(async () => {
  const opts = routeOptions.value
  const orderNo = opts.orderNo ? decodeURIComponent(opts.orderNo) : ''
  const amount = opts.amount
  const orderType = opts.orderType || 'product'
  const subject = opts.subject ? decodeURIComponent(opts.subject || '') : 'FitAI订单'

  if (!orderNo || amount == null || amount === '') {
    error.value = '订单信息不完整'
    loading.value = false
    return
  }

  try {
    const returnBase = getMobilePayReturnBaseUrl()
    let res
    if (orderType === 'seckill') {
      res = await paySeckillOrder(orderNo, returnBase || undefined)
    } else {
      const ot = orderType === 'member' ? 'member' : 'product'
      res = await createPayOrder({
        orderNo,
        orderType: ot,
        amount: Number(amount),
        payType: 'alipay',
        subject: subject || 'FitAI订单',
        ...(returnBase ? { returnUrl: returnBase } : {})
      })
    }
    const data = res.data || {}

    if (data.payForm) {
      // #ifdef H5
      // H5: 注入支付表单并自动提交，跳转支付宝
      const div = document.createElement('div')
      div.innerHTML = data.payForm
      document.body.appendChild(div)
      const form = div.querySelector('form')
      if (form) {
        form.submit()
      } else {
        error.value = '支付表单异常'
      }
      // #endif

      // #ifndef H5
      // 非 H5（小程序/App）：使用 data URL 在 web-view 中打开
      const encoded = encodeURIComponent(data.payForm)
      payFormUrl.value = 'data:text/html;charset=utf-8,' + encoded
      // #endif
    } else {
      error.value = '获取支付信息失败'
    }
  } catch (e) {
    error.value = e.msg || '调起支付失败'
  }
  loading.value = false
})

function goBack() {
  uni.navigateBack()
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.loading-wrap,
.error-wrap {
  padding: 60px;
  text-align: center;
}

.error-text {
  color: #ef4444;
  font-size: 16px;
}

.action-btn {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 24px;
  background: #3b82f6;
  color: #fff;
  border-radius: 8px;
}

.pay-webview {
  width: 100%;
  height: 100vh;
}
</style>
