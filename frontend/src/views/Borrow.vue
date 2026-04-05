<template>
  <div class="borrow-page">
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
              <el-icon :size="28"><i-ep-takeaway-box /></el-icon>
            </div>
            <div class="header-title">
              <h1>借用管理</h1>
              <p>管理您的借用记录和请求</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 借用管理卡片 -->
      <div class="borrow-card">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="我的借用" name="myBorrows" class="tab-pane">
            <div class="table-container">
              <el-table :data="borrowRecords" style="width: 100%" class="custom-table">
                <el-table-column prop="item.name" label="借用物品" width="200" />
                <el-table-column label="借用日期" width="200">
                  <template #default="scope">
                    <el-date-picker
                      v-model="scope.row.borrowDate"
                      type="datetime"
                      format="YYYY-MM-DD HH:mm"
                      value-format="YYYY-MM-DD HH:mm"
                      disabled
                      class="custom-date-picker"
                      style="width: 100%"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="预计归还日期" width="200">
                  <template #default="scope">
                    <el-date-picker
                      v-model="scope.row.expectedReturnDate"
                      type="datetime"
                      format="YYYY-MM-DD HH:mm"
                      value-format="YYYY-MM-DD HH:mm"
                      disabled
                      class="custom-date-picker"
                      style="width: 100%"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="实际归还日期" width="200">
                  <template #default="scope">
                    <el-date-picker
                      v-model="scope.row.actualReturnDate"
                      type="datetime"
                      format="YYYY-MM-DD HH:mm"
                      value-format="YYYY-MM-DD HH:mm"
                      disabled
                      class="custom-date-picker"
                      style="width: 100%"
                    />
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="120">
                  <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="scope">
                    <el-button 
                      v-if="scope.row.status === 'borrowed'" 
                      type="primary" 
                      size="small" 
                      @click="returnItem(scope.row.id)"
                      class="action-btn"
                    >
                      归还
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="borrowRecords.length === 0" class="empty-state">
                <div class="empty-animation">
                  <el-icon :size="64"><i-ep-document /></el-icon>
                </div>
                <h3>暂无借用记录</h3>
                <p>您还没有任何借用记录</p>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="借用请求" name="requests" class="tab-pane">
            <div class="table-container">
              <el-table :data="pendingRequests" style="width: 100%" class="custom-table">
                <el-table-column prop="item.name" label="物品名称" width="200" />
                <el-table-column prop="borrower.name" label="借用人" width="120" />
                <el-table-column label="申请日期" width="200">
                  <template #default="scope">
                    <el-date-picker
                      v-model="scope.row.borrowDate"
                      type="datetime"
                      format="YYYY-MM-DD HH:mm"
                      value-format="YYYY-MM-DD HH:mm"
                      disabled
                      class="custom-date-picker"
                      style="width: 100%"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="预计归还日期" width="200">
                  <template #default="scope">
                    <el-date-picker
                      v-model="scope.row.expectedReturnDate"
                      type="datetime"
                      format="YYYY-MM-DD HH:mm"
                      value-format="YYYY-MM-DD HH:mm"
                      disabled
                      class="custom-date-picker"
                      style="width: 100%"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="借用说明">
                  <template #default="scope">
                    {{ scope.row.notes || '' }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="scope">
                    <el-button type="primary" size="small" @click="approveRequest(scope.row.id)" class="action-btn">同意</el-button>
                    <el-button type="danger" size="small" @click="rejectRequest(scope.row.id)" class="action-btn">拒绝</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="pendingRequests.length === 0" class="empty-state">
                <div class="empty-animation">
                  <el-icon :size="64"><i-ep-chat-dot-round /></el-icon>
                </div>
                <h3>暂无借用请求</h3>
                <p>还没有收到任何借用请求</p>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBorrowStore } from '../store'
import { ElMessage } from 'element-plus'

const router = useRouter()
const borrowStore = useBorrowStore()
const activeTab = ref('myBorrows')
const borrowRecords = ref([])
const pendingRequests = ref([])

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

const returnItem = async (id) => {
  const success = await borrowStore.returnItem(id)
  if (success) {
    ElMessage.success('归还成功')
    await fetchBorrowHistory()
  } else {
    ElMessage.error('归还失败，请稍后重试')
  }
}

const approveRequest = async (id) => {
  const success = await borrowStore.approveRequest(id)
  if (success) {
    ElMessage.success('批准成功')
    await fetchPendingRequests()
  } else {
    ElMessage.error('批准失败，请稍后重试')
  }
}

const rejectRequest = async (id) => {
  const success = await borrowStore.rejectRequest(id)
  if (success) {
    ElMessage.success('拒绝成功')
    await fetchPendingRequests()
  } else {
    ElMessage.error('拒绝失败，请稍后重试')
  }
}

const fetchBorrowHistory = async () => {
  await borrowStore.fetchBorrowHistory()
  // 按创建时间倒序排列，最新的显示在最上面
  borrowRecords.value = borrowStore.borrowRecords.sort((a, b) => {
    return new Date(b.createdAt) - new Date(a.createdAt)
  })
}

const fetchPendingRequests = async () => {
  await borrowStore.fetchPendingRequests()
  // 按创建时间倒序排列，最新的显示在最上面
  pendingRequests.value = borrowStore.pendingRequests.sort((a, b) => {
    return new Date(b.createdAt) - new Date(a.createdAt)
  })
}

const goBack = () => {
  router.replace('/')
}

onMounted(async () => {
  await fetchBorrowHistory()
  await fetchPendingRequests()
})
</script>

<style scoped>
.borrow-page {
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

/* 借用管理卡片 */
.borrow-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

/* 标签页 */
.custom-tabs {
  background: transparent;
}

:deep(.custom-tabs .el-tabs__header) {
  margin-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.custom-tabs .el-tabs__nav) {
  margin-left: 0;
}

:deep(.custom-tabs .el-tabs__item) {
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
  font-weight: 500;
  padding: 0 20px;
  height: 48px;
  line-height: 48px;
  margin-right: 24px;
}

:deep(.custom-tabs .el-tabs__item:hover) {
  color: white;
}

:deep(.custom-tabs .el-tabs__item.is-active) {
  color: white;
  font-weight: 600;
}

:deep(.custom-tabs .el-tabs__active-bar) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 3px;
}

/* 表格容器 */
.table-container {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 自定义表格 - 完全去除白色背景 */
.custom-table {
  background: transparent !important;
  --el-table-bg-color: transparent !important;
  --el-table-header-bg-color: transparent !important;
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.05) !important;
}

:deep(.custom-table .el-table) {
  background: transparent !important;
  border: none !important;
}

:deep(.custom-table .el-table__inner-wrapper) {
  background: transparent !important;
}

:deep(.custom-table .el-table__header-wrapper) {
  background: transparent !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.custom-table .el-table__header) {
  background: transparent !important;
}

:deep(.custom-table .el-table__header th) {
  background: transparent !important;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 600;
  font-size: 14px;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.custom-table .el-table__body-wrapper) {
  background: transparent !important;
}

:deep(.custom-table .el-table__body) {
  background: transparent !important;
}

:deep(.custom-table .el-table__body tr) {
  background: transparent !important;
}

:deep(.custom-table .el-table__body td) {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  background: transparent !important;
}

:deep(.custom-table .el-table__body tr:hover > td) {
  background: rgba(255, 255, 255, 0.05) !important;
}

:deep(.custom-table .el-table__row) {
  background: transparent !important;
}

:deep(.custom-table .el-table__row:hover) {
  background: rgba(255, 255, 255, 0.05) !important;
}

:deep(.custom-table .el-table__empty-block) {
  background: transparent !important;
  border: none !important;
}

:deep(.custom-table .el-table__empty-text) {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 额外的白色背景覆盖 */
:deep(.custom-table .el-table--enable-row-transition .el-table__body td) {
  background: transparent !important;
}

:deep(.custom-table .el-table--enable-row-transition .el-table__body tr:hover td) {
  background: rgba(255, 255, 255, 0.05) !important;
}

/* 更多白色背景覆盖 */
:deep(.custom-table .el-table__row) {
  background-color: transparent !important;
}

:deep(.custom-table .el-table__row:nth-child(even)) {
  background-color: transparent !important;
}

:deep(.custom-table .el-table__row:nth-child(odd)) {
  background-color: transparent !important;
}

:deep(.custom-table .el-table__body tr) {
  background-color: transparent !important;
}

:deep(.custom-table .el-table__body tr td) {
  background-color: transparent !important;
}

:deep(.custom-table .el-table__body-wrapper) {
  background-color: transparent !important;
}

:deep(.custom-table .el-table__content) {
  background-color: transparent !important;
}

/* 针对Element Plus表格的所有可能白色背景 */
:deep(.custom-table) {
  --el-table-tr-bg-color: transparent !important;
  --el-table-header-bg-color: transparent !important;
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.05) !important;
  --el-table-fixed-left-column: transparent !important;
  --el-table-fixed-right-column: transparent !important;
}

:deep(.custom-table .el-table__fixed) {
  background: transparent !important;
}

:deep(.custom-table .el-table__fixed-right) {
  background: transparent !important;
}

:deep(.custom-table .el-table__fixed-left) {
  background: transparent !important;
}

:deep(.custom-table .el-table__fixed-header-wrapper) {
  background: transparent !important;
}

:deep(.custom-table .el-table__fixed-body-wrapper) {
  background: transparent !important;
}

:deep(.custom-table .el-table__fixed-footer-wrapper) {
  background: transparent !important;
}

:deep(.custom-table .el-table__placeholder) {
  background: transparent !important;
}

:deep(.custom-table .el-table__append-wrapper) {
  background: transparent !important;
}

:deep(.custom-table .el-table__prepend-wrapper) {
  background: transparent !important;
}

/* 自定义日期选择器 */
:deep(.custom-date-picker .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

:deep(.custom-date-picker .el-input__inner) {
  color: white !important;
}

/* 操作按钮 */
.action-btn {
  margin-right: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
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
  
  .borrow-card {
    padding: 20px;
  }
  
  :deep(.custom-table .el-table__header th),
  :deep(.custom-table .el-table__body td) {
    padding: 12px;
    font-size: 12px;
  }
  
  :deep(.custom-tabs .el-tabs__item) {
    font-size: 14px;
    padding: 0 12px;
    margin-right: 12px;
  }
}
</style>