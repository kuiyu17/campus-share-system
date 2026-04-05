<template>
  <div class="detail-page">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="content-wrapper">
      <!-- 加载状态 -->
      <div v-if="!item" class="loading-state">
        <el-icon :size="48" class="spinning"><i-ep-loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 物品详情 -->
      <template v-else>
        <!-- 返回按钮 -->
        <button class="back-btn" @click="goBack">
          <el-icon><i-ep-arrow-left /></el-icon>
          <span>返回列表</span>
        </button>

        <!-- 详情卡片 -->
        <div class="detail-card">
          <div class="detail-grid">
            <!-- 左侧图片 -->
            <div class="image-section">
              <div class="image-wrapper">
                <img :src="item.imageUrl || 'https://via.placeholder.com/400'" class="item-image" alt="item">
                <div class="image-badge" :class="item.status">
                  {{ item.status === 'available' ? '可借用' : '已借出' }}
                </div>
              </div>
            </div>

            <!-- 右侧信息 -->
            <div class="info-section">
              <div class="item-header">
                <h1 class="item-name">{{ item.name }}</h1>
                <div class="item-category">{{ item.category?.name || '未分类' }}</div>
              </div>

              <div class="info-grid">
                <div class="info-item">
                  <div class="info-icon owner">
                    <el-icon><i-ep-user /></el-icon>
                  </div>
                  <div class="info-content">
                    <span class="info-label">所有者</span>
                    <span class="info-value">{{ item.owner?.name }}</span>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon location">
                    <el-icon><i-ep-location /></el-icon>
                  </div>
                  <div class="info-content">
                    <span class="info-label">位置</span>
                    <span class="info-value">{{ item.location || '未设置' }}</span>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon value">
                    <el-icon><i-ep-money /></el-icon>
                  </div>
                  <div class="info-content">
                    <span class="info-label">价值</span>
                    <span class="info-value">¥{{ item.value || 0 }}</span>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon count">
                    <el-icon><i-ep-shopping-bag /></el-icon>
                  </div>
                  <div class="info-content">
                    <span class="info-label">借用次数</span>
                    <span class="info-value">{{ item.borrowCount }} 次</span>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon rating">
                    <el-icon><i-ep-star /></el-icon>
                  </div>
                  <div class="info-content">
                    <span class="info-label">评分</span>
                    <span class="info-value">{{ item.rating || '暂无' }} {{ item.rating ? '★' : '' }}</span>
                  </div>
                </div>
              </div>

              <div class="description-section">
                <h3>物品描述</h3>
                <p>{{ item.description || '暂无描述' }}</p>
              </div>

              <div class="action-section">
                <button 
                  class="borrow-btn" 
                  @click="openBorrowDialog" 
                  :disabled="item.status !== 'available'"
                  :class="{ disabled: item.status !== 'available' }"
                >
                  <el-icon><i-ep-document /></el-icon>
                  <span>{{ item.status === 'available' ? '申请借用' : '暂时不可借用' }}</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 评价区域 -->
        <div class="reviews-section">
          <div class="section-header">
            <div class="header-icon">
              <el-icon :size="24"><i-ep-chat-dot-round /></el-icon>
            </div>
            <h2>用户评价</h2>
          </div>
          
          <div v-if="reviews.length === 0" class="empty-reviews">
            <el-icon :size="48"><i-ep-chat-line-round /></el-icon>
            <p>暂无评价</p>
          </div>
          
          <div v-else class="reviews-list">
            <div v-for="review in reviews" :key="review.id" class="review-card">
              <div class="review-header">
                <div class="reviewer-info">
                  <div class="reviewer-avatar">
                    <img :src="review.user?.avatar || 'https://via.placeholder.com/40'" alt="avatar">
                  </div>
                  <span class="reviewer-name">{{ review.user?.name }}</span>
                </div>
                <div class="review-rating">
                  <span v-for="n in review.rating" :key="n" class="star">★</span>
                </div>
              </div>
              <p class="review-content">{{ review.comment }}</p>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 借用申请对话框 -->
    <el-dialog
      v-model="borrowDialogVisible"
      title="申请借用"
      width="500px"
      class="custom-dialog"
    >
      <el-form :model="borrowForm" :rules="borrowRules" ref="borrowFormRef" class="custom-form">
        <el-form-item label="借用物品" prop="itemName">
          <el-input v-model="borrowForm.itemName" disabled class="custom-input"></el-input>
        </el-form-item>
        <el-form-item label="预计归还时间" prop="expectedReturnDate">
          <el-date-picker
            v-model="borrowForm.expectedReturnDate"
            type="datetime"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="选择日期和时间"
            style="width: 100%"
            class="custom-date-picker"
          />
        </el-form-item>
        <el-form-item label="借用说明" prop="notes">
          <el-input
            v-model="borrowForm.notes"
            type="textarea"
            placeholder="请输入借用说明"
            class="custom-textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <button class="cancel-btn" @click="borrowDialogVisible = false">取消</button>
          <button class="submit-btn" @click="submitBorrowRequest">提交申请</button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useItemStore, useBorrowStore } from '../store'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const itemStore = useItemStore()
const borrowStore = useBorrowStore()

const itemId = computed(() => route.params.id)
const item = ref(null)
const reviews = ref([])
const borrowDialogVisible = ref(false)
const borrowFormRef = ref(null)
const borrowForm = ref({
  itemName: '',
  expectedReturnDate: '',
  notes: ''
})

const borrowRules = {
  expectedReturnDate: [
    { required: true, message: '请选择预计归还日期', trigger: 'blur' }
  ]
}

const openBorrowDialog = () => {
  borrowForm.value.itemName = item.value.name
  borrowDialogVisible.value = true
}

const submitBorrowRequest = async () => {
  if (borrowFormRef.value) {
    await borrowFormRef.value.validate(async (valid) => {
      if (valid) {
        const success = await borrowStore.createBorrowRequest(
          item.value.id,
          borrowForm.value.expectedReturnDate,
          borrowForm.value.notes
        )
        if (success) {
          ElMessage.success('借用申请提交成功')
          borrowDialogVisible.value = false
          await itemStore.fetchItemById(itemId.value)
          item.value = itemStore.currentItem
        } else {
          ElMessage.error('借用申请提交失败，请稍后重试')
        }
      }
    })
  }
}

const goBack = () => {
  router.replace('/items')
}

onMounted(async () => {
  await itemStore.fetchItemById(itemId.value)
  item.value = itemStore.currentItem
  reviews.value = []
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a3e 50%, #16213e 100%);
  position: relative;
  overflow-x: hidden;
  padding: 30px;
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
  max-width: 1200px;
  margin: 0 auto;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: rgba(255, 255, 255, 0.5);
}

.loading-state p {
  margin-top: 16px;
  font-size: 16px;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 返回按钮 */
.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(-5px);
}

/* 详情卡片 */
.detail-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 32px;
  margin-bottom: 30px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 40px;
}

/* 图片区域 */
.image-section {
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-wrapper {
  position: relative;
  width: 100%;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.item-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.image-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.image-badge.available {
  background: rgba(103, 194, 58, 0.9);
  color: white;
}

.image-badge.borrowed {
  background: rgba(230, 162, 60, 0.9);
  color: white;
}

/* 信息区域 */
.info-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.item-name {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0;
}

.item-category {
  padding: 6px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: white;
  font-size: 14px;
  font-weight: 500;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
}

.info-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.info-icon.owner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.info-icon.location {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.info-icon.value {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.info-icon.count {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.info-icon.rating {
  background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

/* 描述区域 */
.description-section {
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
}

.description-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 12px 0;
}

.description-section p {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  margin: 0;
}

/* 操作区域 */
.action-section {
  margin-top: auto;
}

.borrow-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.borrow-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.borrow-btn.disabled {
  background: rgba(255, 255, 255, 0.1);
  cursor: not-allowed;
}

/* 评价区域 */
.reviews-section {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 32px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-header .header-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.empty-reviews {
  text-align: center;
  padding: 60px 20px;
  color: rgba(255, 255, 255, 0.4);
}

.empty-reviews p {
  margin-top: 16px;
  font-size: 16px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.reviewer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reviewer-name {
  font-size: 15px;
  font-weight: 600;
  color: white;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-rating .star {
  color: #f6d365;
  font-size: 18px;
}

.review-content {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  margin: 0;
}

/* 对话框样式 */
:deep(.custom-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px 24px;
}

:deep(.custom-dialog .el-dialog__title) {
  color: white;
  font-weight: 600;
}

:deep(.custom-dialog .el-dialog__body) {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 24px;
}

:deep(.custom-dialog .el-dialog__footer) {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 16px 24px;
}

:deep(.custom-form .el-form-item__label) {
  color: rgba(255, 255, 255, 0.7);
}

:deep(.custom-input .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

:deep(.custom-input .el-input__inner) {
  color: white !important;
}

:deep(.custom-date-picker .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

:deep(.custom-textarea .el-textarea__inner) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  color: white !important;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-btn {
  padding: 10px 24px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.submit-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

/* 响应式 */
@media (max-width: 968px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .item-image {
    height: 300px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .detail-page {
    padding: 20px;
  }
  
  .detail-card {
    padding: 20px;
  }
  
  .item-name {
    font-size: 24px;
  }
}
</style>
