<template>
  <div class="app">
    <el-container>
      <el-header v-if="userStore.isLoggedIn">
        <div class="header-content">
          <h1 class="system-title">校园物品共享与借用管理系统</h1>
          <div class="user-info">
            <div class="user-name" @click="goToProfile">
              <el-icon><i-ep-user /></el-icon>
              <span>{{ userStore.user?.name }}</span>
            </div>
            <div class="logout-btn" @click="logout">
              <el-icon><i-ep-switch-button /></el-icon>
              <span>退出</span>
            </div>
          </div>
        </div>
        <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
          <el-menu-item index="home" class="menu-item">
            <el-icon class="menu-icon"><i-ep-home /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="items" class="menu-item">
            <el-icon class="menu-icon"><i-ep-grid /></el-icon>
            <span>物品浏览</span>
          </el-menu-item>
          <el-menu-item index="borrow" class="menu-item">
            <el-icon class="menu-icon"><i-ep-takeaway-box /></el-icon>
            <span>借用管理</span>
          </el-menu-item>
          <el-menu-item index="manage/items" class="menu-item">
            <el-icon class="menu-icon"><i-ep-setting /></el-icon>
            <span>物品管理</span>
          </el-menu-item>
          <el-sub-menu index="ai" class="menu-item">
            <template #title>
              <el-icon class="menu-icon"><i-ep-magic-stick /></el-icon>
              <span>AI功能</span>
            </template>
            <el-menu-item index="ai/recommendations">
              <el-icon><i-ep-star /></el-icon>
              <span>AI推荐</span>
            </el-menu-item>
            <el-menu-item index="ai/dashboard">
              <el-icon><i-ep-data-analysis /></el-icon>
              <span>数据分析</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from './store'

const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('home')

const handleSelect = (key) => {
  router.push(`/${key}`)
  activeIndex.value = key
}

const logout = () => {
  userStore.logout()
  localStorage.removeItem('rawPassword')
  router.push('/login')
}

const goToProfile = () => {
  router.push('/profile')
}

onMounted(() => {
  // 检查当前路由并设置activeIndex
  const path = router.currentRoute.value.path
  if (path === '/') {
    activeIndex.value = 'home'
  } else {
    activeIndex.value = path.substring(1)
  }
})
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  height: 70px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 50;
}

.system-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: glow 3s ease-in-out infinite alternate;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  position: relative;
  z-index: 100;
}

.user-name {
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  font-weight: 500;
  pointer-events: auto;
  user-select: none;
}

.user-name:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
}

.logout-btn {
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 14px;
  font-weight: 500;
  pointer-events: auto;
  user-select: none;
}

.logout-btn:hover {
  background: rgba(245, 87, 108, 0.8);
  border-color: rgba(245, 87, 108, 0.5);
  transform: translateY(-2px);
}

.el-menu-demo {
  background: linear-gradient(135deg, #5a6fe8 0%, #6a489f 100%);
  color: white;
  border-bottom: none;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.menu-item {
  color: white !important;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transition: width 0.3s ease;
}

.menu-item:hover::before {
  width: 100%;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.15) !important;
  transform: translateY(-2px);
}

.menu-item.is-active {
  color: #ffffff !important;
  background-color: rgba(255, 255, 255, 0.2) !important;
  font-weight: 600;
}

.menu-item.is-active::before {
  width: 100%;
}

.menu-icon {
  margin-right: 8px;
  font-size: 16px;
}

.el-main {
  padding: 30px;
  min-height: calc(100vh - 140px);
}

@keyframes glow {
  from {
    text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  }
  to {
    text-shadow: 0 0 20px rgba(255, 255, 255, 0.8), 0 0 30px rgba(102, 126, 234, 0.5);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 20px;
    height: 60px;
  }
  
  .system-title {
    font-size: 18px;
  }
  
  .el-main {
    padding: 20px;
  }
  
  .menu-item {
    font-size: 12px;
  }
  
  .menu-icon {
    font-size: 14px;
    margin-right: 4px;
  }
}
</style>