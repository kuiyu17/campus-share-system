<template>
  <div class="borrow-history-page">
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
              <el-icon :size="28"><i-ep-history /></el-icon>
            </div>
            <div class="header-title">
              <h1>借用历史</h1>
              <p>查看您的历史借用记录</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 借用历史卡片 -->
      <div class="history-card">
        <div class="table-container">
          <el-table :data="borrowRecords" style="width: 100%" class="custom-table">
            <el-table-column prop="id" label="记录ID" width="100" />
            <el-table-column prop="item.name" label="物品名称" />
            <el-table-column prop="borrowDate" label="借用日期" />
            <el-table-column prop="actualReturnDate" label="归还日期" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="borrowRecords.length === 0" class="empty-state">
            <div class="empty-animation">
              <el-icon :size="64"><i-ep-document /></el-icon>
            </div>
            <h3>暂无借用记录</h3>
            <p>您还没有任何借用历史</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBorrowStore } from '../store'

const router = useRouter()
const borrowStore = useBorrowStore()
const borrowRecords = ref([])

const getStatusType = (status) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'approved': return 'info'
    case 'borrowed': return 'primary'
    case 'returned': return 'success'
    case 'overdue': return 'danger'
    case 'rejected': return 'danger'
    default: return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'pending': return '待审核'
    case 'approved': return '已批准'
    case 'borrowed': return '已借出'
    case 'returned': return '已归还'
    case 'overdue': return '已逾期'
    case 'rejected': return '已拒绝'
    default: return status
  }
}

const goBack = () => {
  router.replace('/')
}

onMounted(async () => {
  await borrowStore.fetchBorrowHistory()
  // 按创建时间倒序排列，最新的显示在最上面
  borrowRecords.value = borrowStore.borrowRecords.sort((a, b) => {
    return new Date(b.createdAt) - new Date(a.createdAt)
  })
})
</script>

<style scoped>
.borrow-history-page {
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

/* 借用历史卡片 */
.history-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

/* 表格容器 */
.table-container {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 自定义表格 */
.custom-table {
  background: transparent;
}

:deep(.custom-table .el-table__header-wrapper) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.custom-table .el-table__header th) {
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 600;
  font-size: 14px;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.custom-table .el-table__body tr) {
  background: transparent;
}

:deep(.custom-table .el-table__body td) {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

:deep(.custom-table .el-table__body tr:hover > td) {
  background: rgba(255, 255, 255, 0.05);
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
  color: rgba(255, 255, 255, 0.3);
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
  
  .history-card {
    padding: 20px;
  }
  
  :deep(.custom-table .el-table__header th),
  :deep(.custom-table .el-table__body td) {
    padding: 12px;
    font-size: 12px;
  }
}
</style>