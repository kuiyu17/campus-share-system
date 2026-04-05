<template>
  <div class="items-page">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="content-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-glass">
          <div class="header-left">
            <button class="back-btn" @click="goBack">
              <el-icon><i-ep-arrow-left /></el-icon>
              <span>返回首页</span>
            </button>
            <div class="header-icon">
              <el-icon :size="28"><i-ep-view /></el-icon>
            </div>
            <div class="header-title">
              <h1>物品浏览</h1>
              <p>探索校园共享物品</p>
            </div>
          </div>
          <div class="search-box">
            <el-icon class="search-icon"><i-ep-search /></el-icon>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索物品名称..."
              class="custom-search"
              @keyup.enter="searchItems"
            >
              <template #append>
                <button class="search-btn" @click="searchItems">
                  <el-icon><i-ep-search /></el-icon>
                </button>
              </template>
            </el-input>
          </div>
        </div>
      </div>

      <!-- 物品网格 -->
      <div class="items-section">
        <div class="items-grid">
          <div 
            v-for="(item, index) in items" 
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
                  <span>查看详情</span>
                </div>
              </div>
              <div class="category-badge">{{ item.category?.name || '未分类' }}</div>
            </div>
            <div class="item-content">
              <h3 class="item-name">{{ item.name }}</h3>
              <p class="item-description">{{ item.description }}</p>
              <div class="item-footer">
                <div class="owner-info">
                  <div class="owner-avatar">
                    <img :src="item.owner?.avatar || 'https://via.placeholder.com/32'" alt="avatar">
                  </div>
                  <span class="owner-name">{{ item.owner?.name || '未知用户' }}</span>
                </div>
                <span class="status-badge" :class="item.status">
                  {{ item.status === 'available' ? '可借用' : '已借出' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="items.length === 0" class="empty-state">
          <div class="empty-animation">
            <el-icon :size="80"><i-ep-box /></el-icon>
          </div>
          <h3>暂无物品</h3>
          <p>还没有人发布物品，快来成为第一个吧！</p>
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
const items = ref([])
const searchKeyword = ref('')
const hoveredCard = ref(null)

const cardStyle = (index) => ({
  animationDelay: `${index * 0.05}s`
})

const goToItemDetail = (id) => {
  router.push(`/item/detail/${id}`)
}

const searchItems = async () => {
  if (searchKeyword.value) {
    await itemStore.searchItems(searchKeyword.value)
    items.value = itemStore.items
  } else {
    await itemStore.fetchItems()
    items.value = itemStore.items
  }
}

const goBack = () => {
  router.replace('/')
}

onMounted(async () => {
  await itemStore.fetchItems()
  items.value = itemStore.items
})
</script>

<style scoped>
.items-page {
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

/* 页面头部 */
.page-header {
  margin-bottom: 30px;
}

.header-glass {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 返回按钮 */
.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(-3px);
}

.header-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.header-title h1 {
  font-size: 24px;
  font-weight: 700;
  color: white;
  margin: 0 0 4px 0;
}

.header-title p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

/* 搜索框 */
.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-icon {
  color: rgba(255, 255, 255, 0.5);
  font-size: 20px;
}

:deep(.custom-search .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  width: 300px;
}

:deep(.custom-search .el-input__inner) {
  color: white !important;
  height: 44px;
}

:deep(.custom-search .el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

:deep(.custom-search .el-input-group__append) {
  background: transparent !important;
  border: none !important;
  padding: 0 !important;
}

.search-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

/* 物品网格 */
.items-section {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 24px;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
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
  height: 180px;
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

.category-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 6px 12px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  color: white;
  font-size: 12px;
  font-weight: 500;
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
  margin: 0 0 16px 0;
  height: 36px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.owner-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.owner-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.owner-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.owner-name {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.status-badge {
  padding: 4px 12px;
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

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: rgba(255, 255, 255, 0.4);
}

.empty-animation {
  animation: bounce 2s infinite;
  margin-bottom: 20px;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.empty-state h3 {
  font-size: 20px;
  color: white;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  margin: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px;
  }
  
  .header-glass {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }
  
  .header-left {
    flex-direction: column;
  }
  
  :deep(.custom-search .el-input__wrapper) {
    width: 100%;
  }
  
  .items-grid {
    grid-template-columns: 1fr;
  }
}
</style>
