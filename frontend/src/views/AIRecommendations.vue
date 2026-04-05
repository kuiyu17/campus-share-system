<template>
  <div class="ai-page">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="particles">
        <span v-for="n in 20" :key="n" :style="particleStyle(n)"></span>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="content-wrapper">
      <!-- 头部 -->
      <header class="page-header">
        <div class="header-glass">
          <div class="logo-section">
            <div class="logo-pulse">
              <el-icon :size="40"><i-ep-magic-stick /></el-icon>
            </div>
            <div class="title-group">
              <h1 class="main-title">
                <span class="gradient-text">AI</span>
                <span class="normal-text">智能推荐</span>
              </h1>
              <p class="sub-title">基于深度学习算法，为您精准匹配心仪物品</p>
            </div>
          </div>
          <div class="header-actions">
            <button class="refresh-btn" @click="updateRecommendations" :disabled="updating">
              <span class="btn-content">
                <el-icon :size="18" :class="{ 'spinning': updating }"><i-ep-refresh /></el-icon>
                <span>{{ updating ? '更新中...' : '刷新推荐' }}</span>
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

      <!-- 标签页 -->
      <div class="tabs-section">
        <div class="custom-tabs">
          <div 
            v-for="tab in tabs" 
            :key="tab.name"
            class="tab-item"
            :class="{ active: activeTab === tab.name }"
            @click="activeTab = tab.name"
          >
            <div class="tab-glass">
              <el-icon :size="20"><component :is="tab.icon" /></el-icon>
              <span>{{ tab.label }}</span>
              <div class="tab-indicator"></div>
            </div>
          </div>
        </div>

        <!-- 内容区 -->
        <div class="content-area">
          <transition-group name="fade-slide" tag="div" class="items-grid">
            <div 
              v-for="(item, index) in currentItems" 
              :key="item.id"
              class="recommend-card"
              :style="cardStyle(index)"
              @mouseenter="hoveredCard = item.id"
              @mouseleave="hoveredCard = null"
              @click="goToItemDetail(item.id)"
            >
              <!-- 卡片光效 -->
              <div class="card-shine" :class="{ active: hoveredCard === item.id }"></div>
              
              <!-- 图片区 -->
              <div class="card-image">
                <img :src="item.imageUrl || 'https://via.placeholder.com/400x250'" alt="item">
                <div class="image-shimmer"></div>
                
                <!-- 徽章 -->
                <div class="badge" v-if="activeTab === 'popular'">
                  <el-icon><i-ep-trophy /></el-icon>
                  <span>{{ item.borrowCount }} 次借用</span>
                </div>
                <div class="badge new" v-if="activeTab === 'new'">
                  <el-icon><i-ep-bell /></el-icon>
                  <span>NEW</span>
                </div>
                
                <!-- 悬浮遮罩 -->
                <div class="hover-mask">
                  <div class="view-btn">
                    <el-icon :size="24"><i-ep-view /></el-icon>
                    <span>查看详情</span>
                  </div>
                </div>
              </div>

              <!-- 内容区 -->
              <div class="card-content">
                <h3 class="item-name">{{ item.name }}</h3>
                <p class="item-desc">{{ item.description }}</p>
                
                <div class="item-tags">
                  <span class="tag category">{{ item.category?.name || '未分类' }}</span>
                  <span class="tag" :class="item.status">
                    {{ item.status === 'available' ? '可借用' : '已借出' }}
                  </span>
                </div>

                <div class="item-owner">
                  <div class="owner-avatar">
                    <img :src="item.owner?.avatar || 'https://via.placeholder.com/40'" alt="avatar">
                  </div>
                  <span class="owner-name">{{ item.owner?.name || '未知用户' }}</span>
                </div>
              </div>
            </div>
          </transition-group>

          <!-- 空状态 -->
          <div v-if="currentItems.length === 0" class="empty-state">
            <div class="empty-animation">
              <el-icon :size="80"><i-ep-box /></el-icon>
            </div>
            <h3>暂无推荐物品</h3>
            <p>{{ emptyText }}</p>
            <button class="action-btn" @click="handleEmptyAction">
              {{ emptyActionText }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAIStore } from '../store'
import { ElMessage } from 'element-plus'

const router = useRouter()
const aiStore = useAIStore()

const activeTab = ref('all')
const updating = ref(false)
const hoveredCard = ref(null)

const tabs = [
  { name: 'all', label: '综合推荐', icon: 'i-ep-star' },
  { name: 'personalized', label: '个性化', icon: 'i-ep-user' },
  { name: 'popular', label: '热门', icon: 'i-ep-trophy' },
  { name: 'new', label: '新品', icon: 'i-ep-bell' }
]

const recommendations = ref([])
const personalizedRecommendations = ref([])
const popularRecommendations = ref([])
const newRecommendations = ref([])

const currentItems = computed(() => {
  switch (activeTab.value) {
    case 'all': return recommendations.value
    case 'personalized': return personalizedRecommendations.value
    case 'popular': return popularRecommendations.value
    case 'new': return newRecommendations.value
    default: return []
  }
})

const emptyText = computed(() => {
  const texts = {
    all: '还没有为您生成推荐',
    personalized: '请先借用一些物品，我们将为您推荐同类物品',
    popular: '暂无热门物品',
    new: '暂无新品上架'
  }
  return texts[activeTab.value]
})

const emptyActionText = computed(() => {
  const texts = {
    all: '生成推荐',
    personalized: '去浏览物品',
    popular: '刷新看看',
    new: '刷新看看'
  }
  return texts[activeTab.value]
})

const particleStyle = (n) => ({
  left: `${Math.random() * 100}%`,
  animationDelay: `${Math.random() * 5}s`,
  animationDuration: `${5 + Math.random() * 5}s`
})

const cardStyle = (index) => ({
  animationDelay: `${index * 0.08}s`
})

const goToItemDetail = (id) => {
  router.push(`/item/detail/${id}`)
}

const goBack = () => {
  router.replace('/')
}

const updateRecommendations = async () => {
  updating.value = true
  try {
    const success = await aiStore.updateRecommendations()
    if (success) {
      ElMessage.success('推荐已更新')
      await fetchAllRecommendations()
    }
  } finally {
    updating.value = false
  }
}

const handleEmptyAction = () => {
  if (activeTab.value === 'personalized') {
    router.push('/items')
  } else {
    updateRecommendations()
  }
}

const fetchAllRecommendations = async () => {
  await aiStore.fetchRecommendations()
  recommendations.value = aiStore.recommendations
  
  await aiStore.fetchPersonalizedRecommendations()
  personalizedRecommendations.value = aiStore.personalizedRecommendations
  
  await aiStore.fetchPopularRecommendations()
  popularRecommendations.value = aiStore.popularRecommendations
  
  await aiStore.fetchNewRecommendations()
  newRecommendations.value = aiStore.newRecommendations
}

onMounted(() => {
  fetchAllRecommendations()
})
</script>

<style scoped>
.ai-page {
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
  bottom: -150px;
  right: -150px;
  animation-delay: -5s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(50px, -50px) scale(1.1); }
  50% { transform: translate(0, 50px) scale(0.9); }
  75% { transform: translate(-50px, -25px) scale(1.05); }
}

/* 粒子效果 */
.particles span {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: particle-rise linear infinite;
}

@keyframes particle-rise {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(1);
    opacity: 0;
  }
}

/* 主内容 */
.content-wrapper {
  position: relative;
  z-index: 1;
  padding: 40px;
  max-width: 1600px;
  margin: 0 auto;
}

/* 头部 */
.page-header {
  margin-bottom: 40px;
}

.header-glass {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 30px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo-pulse {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  animation: pulse 2s infinite;
  box-shadow: 0 0 30px rgba(102, 126, 234, 0.5);
}

@keyframes pulse {
  0%, 100% { transform: scale(1); box-shadow: 0 0 30px rgba(102, 126, 234, 0.5); }
  50% { transform: scale(1.05); box-shadow: 0 0 50px rgba(102, 126, 234, 0.8); }
}

.title-group {
  color: white;
}

.main-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.normal-text {
  color: white;
}

.sub-title {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

/* 刷新按钮 */
.refresh-btn {
  position: relative;
  padding: 14px 28px;
  background: transparent;
  border: 2px solid rgba(102, 126, 234, 0.5);
  border-radius: 12px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  border-color: #667eea;
  transform: translateY(-2px);
}

.btn-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.4) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  transition: all 0.5s ease;
  z-index: 1;
}

.refresh-btn:hover .btn-glow {
  width: 300px;
  height: 300px;
}

/* 返回按钮 */
.header-actions {
  display: flex;
  gap: 12px;
}

.back-btn {
  position: relative;
  padding: 14px 28px;
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.back-btn:hover {
  border-color: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.back-btn .btn-glow {
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
}

.back-btn:hover .btn-glow {
  width: 300px;
  height: 300px;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 标签页 */
.tabs-section {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.custom-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.tab-item {
  cursor: pointer;
  position: relative;
}

.tab-glass {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.tab-item:hover .tab-glass {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  transform: translateY(-2px);
}

.tab-item.active .tab-glass {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: white;
  border-radius: 3px;
  transform: translateX(-50%);
  transition: width 0.3s ease;
}

.tab-item.active .tab-indicator {
  width: 60%;
}

/* 内容区 */
.content-area {
  min-height: 400px;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* 推荐卡片 */
.recommend-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  animation: card-enter 0.6s ease forwards;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes card-enter {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.recommend-card:hover {
  transform: translateY(-8px) scale(1.02);
  border-color: rgba(102, 126, 234, 0.5);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4), 0 0 40px rgba(102, 126, 234, 0.2);
}

/* 卡片光效 */
.card-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    transparent
  );
  transition: left 0.5s ease;
  pointer-events: none;
  z-index: 10;
}

.card-shine.active {
  left: 100%;
}

/* 卡片图片 */
.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.recommend-card:hover .card-image img {
  transform: scale(1.1);
}

.image-shimmer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.1) 50%,
    transparent 100%
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 徽章 */
.badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 4px 15px rgba(245, 87, 108, 0.4);
  z-index: 3;
}

.badge.new {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
}

/* 悬浮遮罩 */
.hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 2;
}

.recommend-card:hover .hover-mask {
  opacity: 1;
}

.view-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 14px;
  transform: translateY(20px);
  transition: transform 0.3s ease;
}

.recommend-card:hover .view-btn {
  transform: translateY(0);
}

/* 卡片内容 */
.card-content {
  padding: 20px;
  color: white;
}

.item-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.item-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.tag.category {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.tag.available {
  background: rgba(46, 125, 50, 0.3);
  color: #81c784;
}

.tag.borrowed {
  background: rgba(198, 40, 40, 0.3);
  color: #ef5350;
}

/* 物品所有者 */
.item-owner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.owner-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(102, 126, 234, 0.5);
}

.owner-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.owner-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: white;
}

.empty-animation {
  color: rgba(255, 255, 255, 0.3);
  animation: bounce 2s infinite;
  margin-bottom: 20px;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.empty-state h3 {
  font-size: 24px;
  margin: 0 0 10px 0;
}

.empty-state p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 24px 0;
}

.action-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.6);
}

/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 响应式 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px;
  }
  
  .header-glass {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    padding: 20px;
  }
  
  .main-title {
    font-size: 24px;
  }
  
  .custom-tabs {
    justify-content: center;
  }
  
  .items-grid {
    grid-template-columns: 1fr;
  }
}
</style>
