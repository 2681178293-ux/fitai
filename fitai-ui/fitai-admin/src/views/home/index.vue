<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" style="--accent-color: #3B82F6">
        <div class="stat-icon">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ animatedUsers }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>

      <div class="stat-card" style="--accent-color: #10B981">
        <div class="stat-icon">
          <el-icon :size="28"><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ animatedRevenue.toLocaleString() }}</div>
          <div class="stat-label">销售总额</div>
        </div>
      </div>

      <div class="stat-card" style="--accent-color: #F59E0B">
        <div class="stat-icon">
          <el-icon :size="28"><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ animatedOrders }}</div>
          <div class="stat-label">订单数量</div>
        </div>
      </div>

      <div class="stat-card" style="--accent-color: #8B5CF6">
        <div class="stat-icon">
          <el-icon :size="28"><VideoPlay /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ animatedCourses }}</div>
          <div class="stat-label">课程总数</div>
        </div>
      </div>
    </div>

    <p class="client-stats-hint">以下为 C 端用户经 /api/pc、/api/mobile 前缀登录的汇总（来自登录日志）。PC 与移动「去重人数」之和可能大于实际人数，因同一账号可双端登录；「双端都登录过」为去重交集。</p>
    <div class="stats-row client-stats-row">
      <div class="stat-card" style="--accent-color: #2563EB">
        <div class="stat-icon">
          <el-icon :size="26"><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ clientStats.pcDistinctUsers }}</div>
          <div class="stat-label">PC 路径去重用户</div>
        </div>
      </div>
      <div class="stat-card" style="--accent-color: #059669">
        <div class="stat-icon">
          <el-icon :size="26"><Cellphone /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ clientStats.mobileDistinctUsers }}</div>
          <div class="stat-label">移动端路径去重用户</div>
        </div>
      </div>
      <div class="stat-card" style="--accent-color: #7C3AED">
        <div class="stat-icon">
          <el-icon :size="26"><Connection /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ clientStats.bothEndsUsers }}</div>
          <div class="stat-label">双端均登录过</div>
        </div>
      </div>
      <div class="stat-card" style="--accent-color: #EA580C">
        <div class="stat-icon">
          <el-icon :size="26"><DataLine /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ clientStats.pcLoginTimes }}</div>
          <div class="stat-label">PC 路径登录次数</div>
        </div>
      </div>
      <div class="stat-card" style="--accent-color: #DB2777">
        <div class="stat-icon">
          <el-icon :size="26"><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ clientStats.mobileLoginTimes }}</div>
          <div class="stat-label">移动端路径登录次数</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <h3>订单与销售趋势</h3>
          <el-radio-group v-model="chartPeriod" size="small">
            <el-radio-button value="week">本周</el-radio-button>
            <el-radio-button value="month">本月</el-radio-button>
            <el-radio-button value="year">本年</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="lineChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <h3>订单状态分布</h3>
        </div>
        <div ref="pieChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 底部区域 -->
    <div class="bottom-row">
      <!-- 快捷入口 -->
      <div class="quick-card">
        <div class="card-header">
          <h3>快捷入口</h3>
        </div>
        <div class="quick-links">
          <div class="quick-item" @click="$router.push('/user/list')">
            <div class="quick-icon" style="background: #EFF6FF; color: #3B82F6">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <span>用户管理</span>
          </div>
          <div class="quick-item" @click="$router.push('/course/list')">
            <div class="quick-icon" style="background: #F0FDF4; color: #10B981">
              <el-icon :size="24"><VideoPlay /></el-icon>
            </div>
            <span>课程管理</span>
          </div>
          <div class="quick-item" @click="$router.push('/order/list')">
            <div class="quick-icon" style="background: #FEF3C7; color: #F59E0B">
              <el-icon :size="24"><Document /></el-icon>
            </div>
            <span>订单管理</span>
          </div>
          <div class="quick-item" @click="$router.push('/product/list')">
            <div class="quick-icon" style="background: #F3E8FF; color: #8B5CF6">
              <el-icon :size="24"><Goods /></el-icon>
            </div>
            <span>商品管理</span>
          </div>
        </div>
      </div>

      <!-- 最近动态 -->
      <div class="activity-card">
        <div class="card-header">
          <h3>最近动态</h3>
          <el-button text type="primary">查看全部</el-button>
        </div>
        <div class="activity-list">
          <div v-for="(item, index) in activities" :key="index" class="activity-item">
            <div class="activity-dot" :style="{ background: item.color }"></div>
            <div class="activity-content">
              <span class="activity-text">{{ item.content }}</span>
              <span class="activity-time">{{ item.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="home">
import { ref, onMounted, onUnmounted, watch } from "vue";
import * as echarts from "echarts";
import {
  User,
  Money,
  Document,
  VideoPlay,
  Goods,
  Monitor,
  Cellphone,
  Connection,
  DataLine,
  TrendCharts
} from "@element-plus/icons-vue";
import { getDashboardStatsApi, getDashboardTrendApi, getDashboardActivitiesApi, getDashboardDistributionApi } from "@/api/modules/dashboard";

// 统计数据
const animatedUsers = ref(0);
const animatedRevenue = ref(0);
const animatedOrders = ref(0);
const animatedCourses = ref(0);

const clientStats = ref({
  pcDistinctUsers: 0,
  mobileDistinctUsers: 0,
  bothEndsUsers: 0,
  pcLoginTimes: 0,
  mobileLoginTimes: 0
});

// 数字动画
const animateNumber = (targetRef, target, duration = 1500) => {
  const startTime = Date.now();
  const animate = () => {
    const elapsed = Date.now() - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const easeOut = 1 - Math.pow(1 - progress, 3);
    const val = typeof target === "number" ? target : Number(target) || 0;
    targetRef.value = Math.floor(val * easeOut);
    if (progress < 1) {
      requestAnimationFrame(animate);
    }
  };
  animate();
};

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getDashboardStatsApi();
    const d = res?.data || {};
    const userCount = Number(d.userCount) || 0;
    const orderCount = Number(d.orderCount) || 0;
    const salesAmount = Number(d.salesAmount) || 0;
    const courseCount = Number(d.courseCount) || 0;
    animateNumber(animatedUsers, userCount);
    animateNumber(animatedRevenue, salesAmount);
    animateNumber(animatedOrders, orderCount);
    animateNumber(animatedCourses, courseCount);

    const c = d.clientLoginStats || {};
    clientStats.value = {
      pcDistinctUsers: Number(c.pcDistinctUsers) || 0,
      mobileDistinctUsers: Number(c.mobileDistinctUsers) || 0,
      bothEndsUsers: Number(c.bothEndsUsers) || 0,
      pcLoginTimes: Number(c.pcLoginTimes) || 0,
      mobileLoginTimes: Number(c.mobileLoginTimes) || 0
    };
  } catch (e) {
    console.error("加载仪表盘统计失败:", e);
  }
};

// 图表
const chartPeriod = ref("week");
const lineChartRef = ref();
const pieChartRef = ref();
const trendData = ref({ dates: [], orderCounts: [], salesAmounts: [] });
let lineChart = null;
let pieChart = null;

// 加载趋势数据
const loadTrend = async () => {
  try {
    const res = await getDashboardTrendApi({ period: chartPeriod.value });
    const d = res?.data || {};
    trendData.value = {
      dates: d.dates || [],
      orderCounts: d.orderCounts || [],
      salesAmounts: d.salesAmounts || []
    };
    initLineChart();
  } catch (e) {
    console.error("加载趋势数据失败:", e);
  }
};

// 活动数据（从 API 加载）
const activities = ref([]);
const loadActivities = async () => {
  try {
    const res = await getDashboardActivitiesApi();
    activities.value = (res?.data || []).map(({ content, time, color }) => ({ content, time, color }));
  } catch (e) {
    console.error("加载最近动态失败:", e);
    activities.value = [];
  }
};

// 初始化折线图
const initLineChart = () => {
  if (!lineChartRef.value) return;
  if (!lineChart) lineChart = echarts.init(lineChartRef.value);
  const { dates, orderCounts, salesAmounts } = trendData.value;
  const option = {
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(255, 255, 255, 0.95)",
      borderColor: "#E5E7EB",
      textStyle: { color: "#374151" }
    },
    legend: {
      data: ["订单量", "销售额"],
      bottom: 0
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "15%",
      top: "10%",
      containLabel: true
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: dates.length ? dates : ["--"],
      axisLine: { lineStyle: { color: "#E5E7EB" } },
      axisLabel: { color: "#6B7280" }
    },
    yAxis: {
      type: "value",
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: "#F3F4F6" } },
      axisLabel: { color: "#6B7280" }
    },
    series: [
      {
        name: "订单量",
        type: "line",
        smooth: true,
        symbol: "circle",
        symbolSize: 6,
        lineStyle: { width: 3, color: "#3B82F6" },
        itemStyle: { color: "#3B82F6" },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(59, 130, 246, 0.25)" },
            { offset: 1, color: "rgba(59, 130, 246, 0.02)" }
          ])
        },
        data: orderCounts.length ? orderCounts : [0]
      },
      {
        name: "销售额",
        type: "line",
        smooth: true,
        symbol: "circle",
        symbolSize: 6,
        lineStyle: { width: 3, color: "#10B981" },
        itemStyle: { color: "#10B981" },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(16, 185, 129, 0.25)" },
            { offset: 1, color: "rgba(16, 185, 129, 0.02)" }
          ])
        },
        data: salesAmounts.length ? salesAmounts : [0]
      }
    ]
  };
  lineChart.setOption(option);
};

// 饼图数据（从 API 加载）
const distributionData = ref([]);
const loadDistribution = async () => {
  try {
    const res = await getDashboardDistributionApi();
    distributionData.value = res?.data || [];
    initPieChart();
  } catch (e) {
    console.error("加载订单分布失败:", e);
    distributionData.value = [];
    initPieChart();
  }
};

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return;
  if (!pieChart) pieChart = echarts.init(pieChartRef.value);
  const data = distributionData.value.length
    ? distributionData.value
    : [{ value: 0, name: "暂无数据", itemStyle: { color: "#6B7280" } }];
  const option = {
    tooltip: {
      trigger: "item",
      backgroundColor: "rgba(255, 255, 255, 0.95)",
      borderColor: "#E5E7EB",
      textStyle: { color: "#374151" }
    },
    legend: {
      orient: "vertical",
      right: "5%",
      top: "center"
    },
    series: [
      {
        type: "pie",
        radius: ["45%", "70%"],
        center: ["35%", "50%"],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: "#fff",
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: "bold" }
        },
        data
      }
    ]
  };
  pieChart.setOption(option);
};

// 窗口大小变化
const handleResize = () => {
  lineChart?.resize();
  pieChart?.resize();
};

onMounted(() => {
  loadStats();
  loadTrend();
  loadActivities();
  loadDistribution();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  lineChart?.dispose();
  pieChart?.dispose();
});

// 监听图表周期变化
watch(chartPeriod, () => {
  loadTrend();
});
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background: #F5F7FA;
  min-height: 100%;
}

/* 统计卡片行 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.client-stats-hint {
  font-size: 12px;
  color: #6b7280;
  margin: 0 0 12px;
  line-height: 1.5;
}

.client-stats-row {
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  margin-bottom: 24px;
}

.client-stats-row .stat-value {
  font-size: 20px;
}

.client-stats-row .stat-icon {
  width: 48px;
  height: 48px;
}

.client-stats-row .stat-card {
  padding: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: color-mix(in srgb, var(--accent-color) 10%, transparent);
  color: var(--accent-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #6B7280;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 13px;
  font-weight: 500;
}

.stat-trend.up {
  color: #10B981;
}

.stat-trend.down {
  color: #EF4444;
}

/* 图表行 */
.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card, .quick-card, .activity-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
  margin: 0;
}

.chart-container {
  height: 280px;
}

/* 底部行 */
.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 快捷入口 */
.quick-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}

.quick-item:hover {
  background: #F9FAFB;
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-item span {
  font-size: 13px;
  color: #4B5563;
}

/* 活动列表 */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.activity-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-text {
  font-size: 14px;
  color: #374151;
}

.activity-time {
  font-size: 12px;
  color: #9CA3AF;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-row {
    grid-template-columns: 1fr;
  }
  .bottom-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  .quick-links {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
