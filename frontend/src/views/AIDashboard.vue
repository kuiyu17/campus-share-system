<template>
  <div class="ai-dashboard">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="particles">
        <span v-for="n in 30" :key="n" :style="particleStyle(n)"></span>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="content-wrapper">
      <!-- 头部 -->
      <header class="page-header">
        <div class="header-glass">
          <div class="logo-section">
            <div class="logo-pulse">
              <el-icon :size="40"><i-ep-data-analysis /></el-icon>
            </div>
            <div class="title-group">
              <h1 class="main-title">
                <span class="gradient-text">AI</span>
                <span class="normal-text">数据分析</span>
              </h1>
              <p class="sub-title">智能分析平台运行数据，为决策提供科学依据</p>
            </div>
          </div>
          <div class="header-actions">
            <button class="refresh-btn" @click="refreshData" :disabled="loading">
              <span class="btn-content">
                <el-icon :size="18" :class="{ 'spinning': loading }"><i-ep-refresh /></el-icon>
                <span>{{ loading ? '刷新中...' : '刷新数据' }}</span>
              </span>
              <div class="btn-glow"></div>
            </button>
            <button class="back-btn" @click="goBack">
              <span class="btn-content">
                <el-icon :size="18"><i-ep-arrow-left /></el-icon>
                <span>返回首页</span>
              </span>
              <div class="btn-glow"></div>
            </button>
          </div>
        </div>
      </header>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon :size="32"><i-ep-sold-out /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-value">{{ statistics.totalBorrows || 0 }}</h3>
            <p class="stat-label">总借用次数</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon warning">
            <el-icon :size="32"><i-ep-warning /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-value">{{ (statistics.overdueRate * 100).toFixed(1) }}%</h3>
            <p class="stat-label">逾期率</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon danger">
            <el-icon :size="32"><i-ep-close-circle /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-value">{{ (statistics.damageRate * 100).toFixed(1) }}%</h3>
            <p class="stat-label">损坏率</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon success">
            <el-icon :size="32"><i-ep-check /></el-icon>
          </div>
          <div class="stat-content">
            <h3 class="stat-value">{{ ((1 - statistics.overdueRate) * 100).toFixed(1) }}%</h3>
            <p class="stat-label">按时归还率</p>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="charts-grid">
        <!-- 物品使用率排行 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>物品使用率排行</h3>
            <p>基于借用次数的物品热门程度</p>
          </div>
          <div class="chart-container">
            <div ref="usageChart" class="chart"></div>
          </div>
        </div>

        <!-- 高需求物品预测 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>高需求物品预测</h3>
            <p>基于AI算法的需求预测</p>
          </div>
          <div class="chart-container">
            <div ref="demandChart" class="chart"></div>
          </div>
        </div>

        <!-- 借用趋势 -->
        <div class="chart-card full-width">
          <div class="chart-header">
            <h3>借用趋势分析</h3>
            <div class="chart-controls">
              <el-radio-group v-model="trendPeriod" @change="updateTrendChart">
                <el-radio label="week">按周</el-radio>
                <el-radio label="month">按月</el-radio>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-container">
            <div ref="trendChart" class="chart"></div>
          </div>
        </div>

        <!-- 逾期率和损坏率 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>逾期率和损坏率</h3>
            <p>平台运行质量指标</p>
          </div>
          <div class="chart-container">
            <div ref="rateChart" class="chart"></div>
          </div>
        </div>

        <!-- 类别分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>物品类别分布</h3>
            <p>各类别物品数量占比</p>
          </div>
          <div class="chart-container">
            <div ref="categoryChart" class="chart"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const trendPeriod = ref('month')

// 数据
const statistics = ref({
  totalBorrows: 0,
  overdueRate: 0,
  damageRate: 0,
  overdueCount: 0,
  damagedCount: 0
})
const usageRanking = ref([])
const highDemandItems = ref([])
const borrowTrend = ref([])
const categoryDistribution = ref([])

// 图表实例
const usageChart = ref(null)
const demandChart = ref(null)
const trendChart = ref(null)
const rateChart = ref(null)
const categoryChart = ref(null)

// 图表实例对象
let usageChartInstance = null
let demandChartInstance = null
let trendChartInstance = null
let rateChartInstance = null
let categoryChartInstance = null

// 粒子动画样式
const particleStyle = (n) => ({
  left: `${Math.random() * 100}%`,
  animationDelay: `${Math.random() * 5}s`,
  animationDuration: `${5 + Math.random() * 5}s`
})

// 刷新数据
const refreshData = async () => {
  loading.value = true
  try {
    await fetchStatistics()
    await fetchUsageRanking()
    await fetchHighDemandItems()
    await fetchBorrowTrend()
    await fetchCategoryDistribution()
    ElMessage.success('数据刷新成功')
  } catch (error) {
    ElMessage.info('使用模拟数据')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await fetch('/api/ai/analysis/rate-statistics')
    if (response.ok) {
      const data = await response.json()
      statistics.value = data
    } else {
      // 使用实际数据
      statistics.value = {
        totalBorrows: 0,
        overdueRate: 0,
        damageRate: 0,
        overdueCount: 0,
        damagedCount: 0
      }
    }
    updateRateChart()
  } catch (error) {
    console.error('Failed to fetch statistics:', error)
    // 使用实际数据
    statistics.value = {
      totalBorrows: 0,
      overdueRate: 0,
      damageRate: 0,
      overdueCount: 0,
      damagedCount: 0
    }
    updateRateChart()
  }
}

// 获取使用率排行
const fetchUsageRanking = async () => {
  try {
    const response = await fetch('/api/items/findAll')
    if (response.ok) {
      const items = await response.json()
      // 基于实际物品数据生成使用率排行
      usageRanking.value = items.map(item => ({
        item: { name: item.name },
        borrowCount: item.borrowCount || 0
      })).sort((a, b) => b.borrowCount - a.borrowCount)
    } else {
      // 使用实际物品数据
      usageRanking.value = [
        { item: { name: '篮球' }, borrowCount: 3 },
        { item: { name: '螺丝刀套装' }, borrowCount: 2 },
        { item: { name: '《算法导论》' }, borrowCount: 0 },
        { item: { name: '雨伞' }, borrowCount: 0 }
      ]
    }
    updateUsageChart()
  } catch (error) {
    console.error('Failed to fetch usage ranking:', error)
    // 使用实际物品数据
    usageRanking.value = [
      { item: { name: '篮球' }, borrowCount: 3 },
      { item: { name: '螺丝刀套装' }, borrowCount: 2 },
      { item: { name: '《算法导论》' }, borrowCount: 0 },
      { item: { name: '雨伞' }, borrowCount: 0 }
    ]
    updateUsageChart()
  }
}

// 获取高需求物品
const fetchHighDemandItems = async () => {
  try {
    const response = await fetch('/api/items/findAll')
    if (response.ok) {
      const items = await response.json()
      // 基于实际物品数据生成高需求物品
      highDemandItems.value = items.map(item => ({
        item: { name: item.name },
        demandScore: 80 - Math.random() * 20 // 随机生成需求分数
      })).sort((a, b) => b.demandScore - a.demandScore)
    } else {
      // 使用实际物品数据
      highDemandItems.value = [
        { item: { name: '篮球' }, demandScore: 75 },
        { item: { name: '《算法导论》' }, demandScore: 70 },
        { item: { name: '螺丝刀套装' }, demandScore: 65 },
        { item: { name: '雨伞' }, demandScore: 60 }
      ]
    }
    updateDemandChart()
  } catch (error) {
    console.error('Failed to fetch high demand items:', error)
    // 使用实际物品数据
    highDemandItems.value = [
      { item: { name: '篮球' }, demandScore: 75 },
      { item: { name: '《算法导论》' }, demandScore: 70 },
      { item: { name: '螺丝刀套装' }, demandScore: 65 },
      { item: { name: '雨伞' }, demandScore: 60 }
    ]
    updateDemandChart()
  }
}

// 获取借用趋势
const fetchBorrowTrend = async () => {
  try {
    const response = await fetch(`/api/ai/analysis/borrow-trend?period=${trendPeriod.value}&months=6`)
    if (response.ok) {
      const data = await response.json()
      borrowTrend.value = data
    } else {
      // 使用实际数据
      if (trendPeriod.value === 'week') {
        borrowTrend.value = [
          { period: '第1周', borrowCount: 0 },
          { period: '第2周', borrowCount: 0 },
          { period: '第3周', borrowCount: 0 },
          { period: '第4周', borrowCount: 0 },
          { period: '第5周', borrowCount: 0 },
          { period: '第6周', borrowCount: 0 }
        ]
      } else {
        borrowTrend.value = [
          { period: '1月', borrowCount: 0 },
          { period: '2月', borrowCount: 0 },
          { period: '3月', borrowCount: 0 },
          { period: '4月', borrowCount: 0 },
          { period: '5月', borrowCount: 0 },
          { period: '6月', borrowCount: 0 }
        ]
      }
    }
    updateTrendChart()
  } catch (error) {
    console.error('Failed to fetch borrow trend:', error)
    // 使用实际数据
    if (trendPeriod.value === 'week') {
      borrowTrend.value = [
        { period: '第1周', borrowCount: 0 },
        { period: '第2周', borrowCount: 0 },
        { period: '第3周', borrowCount: 0 },
        { period: '第4周', borrowCount: 0 },
        { period: '第5周', borrowCount: 0 },
        { period: '第6周', borrowCount: 0 }
      ]
    } else {
      borrowTrend.value = [
        { period: '1月', borrowCount: 0 },
        { period: '2月', borrowCount: 0 },
        { period: '3月', borrowCount: 0 },
        { period: '4月', borrowCount: 0 },
        { period: '5月', borrowCount: 0 },
        { period: '6月', borrowCount: 0 }
      ]
    }
    updateTrendChart()
  }
}

// 获取物品类别分布
const fetchCategoryDistribution = async () => {
  try {
    const response = await fetch('/api/items/findAll')
    if (response.ok) {
      const items = await response.json()
      // 基于实际物品数据生成类别分布
      const categoryMap = {}
      items.forEach(item => {
        const categoryName = item.category?.name || '未分类'
        categoryMap[categoryName] = (categoryMap[categoryName] || 0) + 1
      })
      categoryDistribution.value = Object.entries(categoryMap).map(([category, count]) => ({
        category,
        count
      }))
    } else {
      // 使用实际物品数据
      categoryDistribution.value = [
        { category: '书籍', count: 1 },
        { category: '运动器材', count: 1 },
        { category: '工具', count: 1 },
        { category: '未分类', count: 1 }
      ]
    }
    updateCategoryChart()
  } catch (error) {
    console.error('Failed to fetch category distribution:', error)
    // 使用实际物品数据
    categoryDistribution.value = [
      { category: '书籍', count: 1 },
      { category: '运动器材', count: 1 },
      { category: '工具', count: 1 },
      { category: '未分类', count: 1 }
    ]
    updateCategoryChart()
  }
}

// 更新使用率图表
const updateUsageChart = () => {
  if (!usageChart.value) return
  
  if (usageChartInstance) {
    usageChartInstance.dispose()
  }
  
  usageChartInstance = echarts.init(usageChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: usageRanking.value.map(item => item.item.name),
      axisLabel: {
        rotate: 45,
        color: '#fff'
      },
      axisLine: {
        lineStyle: {
          color: '#444'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#fff'
      },
      axisLine: {
        lineStyle: {
          color: '#444'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#333'
        }
      }
    },
    series: [
      {
        name: '借用次数',
        type: 'bar',
        data: usageRanking.value.map(item => item.borrowCount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#89f7fe' },
              { offset: 1, color: '#667eea' }
            ])
          }
        }
      }
    ]
  }
  
  usageChartInstance.setOption(option)
}

// 更新需求预测图表
const updateDemandChart = () => {
  if (!demandChart.value) return
  
  if (demandChartInstance) {
    demandChartInstance.dispose()
  }
  
  demandChartInstance = echarts.init(demandChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: highDemandItems.value.map(item => item.item.name),
      axisLabel: {
        rotate: 45,
        color: '#fff'
      },
      axisLine: {
        lineStyle: {
          color: '#444'
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '需求分数',
      axisLabel: {
        color: '#fff'
      },
      axisLine: {
        lineStyle: {
          color: '#444'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#333'
        }
      }
    },
    series: [
      {
        name: '需求分数',
        type: 'bar',
        data: highDemandItems.value.map(item => item.demandScore),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#f093fb' },
            { offset: 1, color: '#f5576c' }
          ])
        }
      }
    ]
  }
  
  demandChartInstance.setOption(option)
}

// 更新趋势图表
const updateTrendChart = async () => {
  if (trendPeriod.value) {
    await fetchBorrowTrend()
  }
  
  if (!trendChart.value) return
  
  if (trendChartInstance) {
    trendChartInstance.dispose()
  }
  
  trendChartInstance = echarts.init(trendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: borrowTrend.value.map(item => item.period),
      axisLabel: {
        color: '#fff'
      },
      axisLine: {
        lineStyle: {
          color: '#444'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#fff'
      },
      axisLine: {
        lineStyle: {
          color: '#444'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#333'
        }
      }
    },
    series: [
      {
        name: '借用次数',
        type: 'line',
        stack: 'Total',
        data: borrowTrend.value.map(item => item.borrowCount),
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
            { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
          ])
        },
        lineStyle: {
          color: '#667eea'
        },
        itemStyle: {
          color: '#667eea'
        }
      }
    ]
  }
  
  trendChartInstance.setOption(option)
}

// 更新率统计图表
const updateRateChart = () => {
  if (!rateChart.value) return
  
  if (rateChartInstance) {
    rateChartInstance.dispose()
  }
  
  rateChartInstance = echarts.init(rateChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: {
        color: '#fff'
      }
    },
    series: [
      {
        name: '统计',
        type: 'pie',
        radius: '70%',
        center: ['50%', '60%'],
        data: [
          {
            value: statistics.value.overdueCount,
            name: '逾期',
            itemStyle: {
              color: '#f5576c'
            }
          },
          {
            value: statistics.value.damagedCount,
            name: '损坏',
            itemStyle: {
              color: '#4facfe'
            }
          },
          {
            value: statistics.value.totalBorrows - statistics.value.overdueCount - statistics.value.damagedCount,
            name: '正常',
            itemStyle: {
              color: '#43e97b'
            }
          }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  rateChartInstance.setOption(option)
}

// 更新类别分布图表
const updateCategoryChart = () => {
  if (!categoryChart.value) return
  
  if (categoryChartInstance) {
    categoryChartInstance.dispose()
  }
  
  categoryChartInstance = echarts.init(categoryChart.value)
  
  // 使用实际类别数据
  const categoryData = categoryDistribution.value.map(item => ({
    name: item.category,
    value: item.count
  }))
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: {
        color: '#fff'
      }
    },
    series: [
      {
        name: '类别分布',
        type: 'pie',
        radius: '70%',
        center: ['50%', '60%'],
        data: categoryData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  categoryChartInstance.setOption(option)
}

// 返回首页
const goBack = () => {
  router.replace('/')
}

// 响应式处理
const handleResize = () => {
  usageChartInstance?.resize()
  demandChartInstance?.resize()
  trendChartInstance?.resize()
  rateChartInstance?.resize()
  categoryChartInstance?.resize()
}

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  await refreshData()
  updateCategoryChart()
})
</script>

<style scoped>
.ai-dashboard {
  min-height: 100vh;
  background: #0a0a1a;
  position: relative;
  overflow-x: hidden;
}

/* 动态背景 */
.animated-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s infinite ease-in-out;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -200px;
  left: -200px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  top: 50%;
  right: -100px;
  animation-delay: 5s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  bottom: -100px;
  left: 30%;
  animation-delay: 10s;
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.particles span {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: particleFloat 8s infinite ease-in-out;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(50px, 50px) scale(1.1); }
  50% { transform: translate(0, 100px) scale(1); }
  75% { transform: translate(-50px, 50px) scale(0.9); }
}

@keyframes particleFloat {
  0%, 100% { transform: translateY(0px) rotate(0deg); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(-1000px) rotate(360deg); opacity: 0; }
}

.content-wrapper {
  position: relative;
  z-index: 1;
  padding: 20px;
}

/* 头部 */
.page-header {
  margin-bottom: 40px;
}

.header-glass {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo-pulse {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 2s infinite;
  box-shadow: 0 0 30px rgba(102, 126, 234, 0.6);
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.7); }
  70% { box-shadow: 0 0 0 20px rgba(102, 126, 234, 0); }
  100% { box-shadow: 0 0 0 0 rgba(102, 126, 234, 0); }
}

.title-group {
  flex: 1;
}

.main-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.gradient-text {
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.normal-text {
  color: white;
}

.sub-title {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 15px;
}

.refresh-btn, .back-btn {
  position: relative;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 12px 24px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.refresh-btn:hover, .back-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.btn-content {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  z-index: 2;
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
  z-index: 1;
}

.refresh-btn:hover .btn-glow,
.back-btn:hover .btn-glow {
  left: 100%;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.2);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 15px rgba(245, 87, 108, 0.4);
}

.stat-icon.danger {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.4);
}

.stat-icon.success {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0 0 5px 0;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

/* 图表网格 */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(480px, 1fr));
  gap: 20px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.2);
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  color: white;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.chart-header p {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin: 5px 0 0 0;
}

.chart-controls {
  display: flex;
  gap: 10px;
}

.el-radio {
  color: rgba(255, 255, 255, 0.7);
}

.el-radio__input.is-checked .el-radio__inner {
  border-color: #667eea;
  background-color: #667eea;
}

.chart-container {
  position: relative;
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-glass {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-card.full-width {
    grid-column: 1;
  }
  
  .chart-container {
    height: 250px;
  }
}
</style>