<template>
  <div class="home-page">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="content-wrapper">
      <!-- 欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-glass">
          <h1 class="welcome-title">
            <span class="gradient-text">校园物品共享</span>
          </h1>
          <p class="welcome-subtitle">便捷借用，共享资源</p>
        </div>
      </div>

      <div class="main-grid">
        <!-- 左侧：热门物品 -->
        <div class="left-section">
          <div class="section-card">
            <div class="section-header">
              <div class="header-icon hot">
                <el-icon :size="24"><i-ep-fire /></el-icon>
              </div>
              <h2 class="section-title">热门物品</h2>
            </div>
            <div class="items-grid">
              <div 
                v-for="(item, index) in topItems" 
                :key="item.id"
                class="item-card"
                :style="cardStyle(index)"
                @mouseenter="hoveredCard = item.id"
                @mouseleave="hoveredCard = null"
                @click="goToItemDetail(item.id)"
              >
                <div class="card-shine" :class="{ active: hoveredCard === item.id }"></div>
                <div class="item-image-wrapper">
                  <img :src="item.imageUrl || 'https://via.placeholder.com/300x200'" class="item-image" alt="item">
                  <div class="image-overlay">
                    <div class="view-btn">
                      <el-icon><i-ep-view /></el-icon>
                      <span>查看</span>
                    </div>
                  </div>
                </div>
                <div class="item-content">
                  <h3 class="item-name">{{ item.name }}</h3>
                  <p class="item-description">{{ item.description }}</p>
                  <div class="item-meta">
                    <span class="status-badge" :class="item.status">
                      {{ item.status === 'available' ? '可借用' : '已借出' }}
                    </span>
                    <span class="borrow-count">
                      <el-icon><i-ep-shopping-bag /></el-icon>
                      {{ item.borrowCount }} 次借用
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="topItems.length === 0" class="empty-state">
              <el-icon :size="48"><i-ep-box /></el-icon>
              <p>暂无热门物品</p>
            </div>
          </div>
        </div>

        <!-- 右侧：功能菜单和公告 -->
        <div class="right-section">
          <!-- 功能菜单 -->
          <div class="section-card">
            <div class="section-header">
              <div class="header-icon menu">
                <el-icon :size="24"><i-ep-menu /></el-icon>
              </div>
              <h2 class="section-title">系统功能</h2>
            </div>
            <div class="menu-grid">
              <div 
                v-for="menu in menuItems" 
                :key="menu.index"
                class="menu-item"
                @click="handleMenuSelect(menu.index)"
              >
                <div class="menu-icon" :class="menu.color">
                  <el-icon :size="28"><component :is="menu.icon" /></el-icon>
                </div>
                <span class="menu-label">{{ menu.label }}</span>
                <div class="menu-arrow">
                  <el-icon><i-ep-arrow-right /></el-icon>
                </div>
              </div>
            </div>
          </div>

          <!-- 系统公告 -->
          <div class="section-card announcement-card">
            <div class="section-header">
              <div class="header-icon announcement">
                <el-icon :size="24"><i-ep-bell /></el-icon>
              </div>
              <h2 class="section-title">系统公告</h2>
            </div>
            <div class="announcement-content">
              <div class="announcement-item">
                <div class="announcement-icon">
                  <el-icon><i-ep-info-filled /></el-icon>
                </div>
                <p>欢迎使用校园物品共享与借用管理系统！</p>
              </div>
              <div class="announcement-item">
                <div class="announcement-icon">
                  <el-icon><i-ep-warning-filled /></el-icon>
                </div>
                <p>请遵守借用规则，爱护共享物品。</p>
              </div>
              <div class="announcement-item">
                <div class="announcement-icon">
                  <el-icon><i-ep-question-filled /></el-icon>
                </div>
                <p>如有问题，请联系管理员。</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- AI功能区域 -->
      <div class="ai-features-section">
        <div class="section-header">
          <div class="header-icon ai">
            <el-icon :size="32"><i-ep-data-analysis /></el-icon>
          </div>
          <h2 class="section-title">
            <span class="gradient-text">AI</span> 智能功能
          </h2>
        </div>
        
        <div class="ai-features-grid">
          <div class="ai-feature-card" @click="handleMenuSelect('ai/recommendations')">
            <div class="feature-icon recommendation">
              <el-icon :size="32"><i-ep-magic-stick /></el-icon>
            </div>
            <h3>智能推荐</h3>
            <p>基于历史借用记录和热门物品，为您推荐最适合的物品</p>
            <div class="feature-highlight">
              <span class="highlight-tag">个性化推荐</span>
              <span class="highlight-tag">热门推荐</span>
            </div>
          </div>
          
          <div class="ai-feature-card" @click="handleMenuSelect('ai/dashboard')">
            <div class="feature-icon analysis">
              <el-icon :size="32"><i-ep-data-analysis /></el-icon>
            </div>
            <h3>数据分析</h3>
            <p>智能分析平台数据，提供可视化报表和预测分析</p>
            <div class="feature-highlight">
              <span class="highlight-tag">使用率分析</span>
              <span class="highlight-tag">需求预测</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useItemStore } from '../store'

const router = useRouter()
const itemStore = useItemStore()
const topItems = ref([])
const hoveredCard = ref(null)

const menuItems = [
  { index: 'items', label: '物品浏览', icon: 'i-ep-view', color: 'blue' },
  { index: 'borrow', label: '借用管理', icon: 'i-ep-document', color: 'green' },
  { index: 'manage/items', label: '物品管理', icon: 'i-ep-setting', color: 'orange' },
  { index: 'ai/recommendations', label: 'AI推荐', icon: 'i-ep-magic-stick', color: 'purple' }
]

const cardStyle = (index) => ({
  animationDelay: `${index * 0.1}s`
})

const goToItemDetail = (id) => {
  router.push(`/item/detail/${id}`)
}

const handleMenuSelect = (key) => {
  router.push(`/${key}`)
}

onMounted(async () => {
  const items = await itemStore.fetchTopBorrowedItems()
  topItems.value = items
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 50%, #16213e 100%);
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
  filter: blur(100px);
  opacity: 0.4;
  animation: float 20s infinite ease-in-out;
}

.orb-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -200px;
  left: -200px;
  animation-delay: 0s;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  bottom: -150px;
  right: -150px;
  animation-delay: -5s;
}

.orb-3 {
  width: 350px;
  height: 350px;
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

/* 内容包装器 */
.content-wrapper {
  position: relative;
  z-index: 1;
  padding: 30px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 30px;
}

.welcome-glass {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 30px 40px;
  text-align: center;
}

.welcome-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

/* 主网格布局 */
.main-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

/* 区域卡片 */
.section-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 24px;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.header-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.header-icon.hot {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.header-icon.menu {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.header-icon.announcement {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0;
}

/* 物品网格 */
.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.item-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  transform: translateY(20px);
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.item-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

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
  z-index: 1;
}

.card-shine.active {
  left: 100%;
}

.item-image-wrapper {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.item-card:hover .item-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.item-card:hover .image-overlay {
  opacity: 1;
}

.view-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: white;
  font-size: 14px;
}

.item-content {
  padding: 16px;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-description {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 12px 0;
  height: 36px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.available {
  background: rgba(103, 194, 58, 0.2);
  color: #67c23a;
}

.status-badge.borrowed {
  background: rgba(230, 162, 60, 0.2);
  color: #e6a23c;
}

.borrow-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: rgba(255, 255, 255, 0.4);
}

.empty-state p {
  margin-top: 16px;
  font-size: 14px;
}

/* 菜单网格 */
.menu-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

.menu-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.menu-icon.blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.menu-icon.green {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.menu-icon.orange {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.menu-icon.purple {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #764ba2;
}

.menu-label {
  flex: 1;
  font-size: 15px;
  color: white;
  font-weight: 500;
}

.menu-arrow {
  color: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.menu-item:hover .menu-arrow {
  color: white;
  transform: translateX(5px);
}

/* 公告卡片 */
.announcement-card {
  margin-bottom: 0;
}

.announcement-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 10px;
}

.announcement-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(79, 172, 254, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4facfe;
  flex-shrink: 0;
}

.announcement-item p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
}

/* AI功能区域 */
.ai-features-section {
  margin-top: 40px;
}

.header-icon.ai {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.ai-features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.ai-feature-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.ai-feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.2);
}

.ai-feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.ai-feature-card:hover::before {
  transform: scaleX(1);
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  color: white;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.feature-icon.recommendation {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.feature-icon.analysis {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.ai-feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0 0 12px 0;
}

.ai-feature-card p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.feature-highlight {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.highlight-tag {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 响应式 */
@media (max-width: 1024px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
  
  .items-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
  
  .ai-features-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px;
  }
  
  .welcome-title {
    font-size: 24px;
  }
  
  .items-grid {
    grid-template-columns: 1fr;
  }
  
  .ai-features-grid {
    grid-template-columns: 1fr;
  }
  
  .ai-feature-card {
    padding: 24px;
  }
  
  .feature-icon {
    width: 60px;
    height: 60px;
  }
}
</style>
