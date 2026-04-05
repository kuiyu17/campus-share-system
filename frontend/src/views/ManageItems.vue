<template>
  <div class="manage-items-page">
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
              <el-icon :size="28"><i-ep-setting /></el-icon>
            </div>
            <div class="header-title">
              <h1>物品管理</h1>
              <p>管理您的共享物品</p>
            </div>
          </div>
          <div class="header-actions">
            <button class="add-btn" @click="addItem">
              <el-icon><i-ep-plus /></el-icon>
              <span>添加物品</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 物品管理卡片 -->
      <div class="manage-card">
        <div class="table-container">
          <el-table :data="items" style="width: 100%" class="custom-table">
            <el-table-column prop="id" label="物品ID" width="100" />
            <el-table-column label="图片" width="120">
              <template #default="scope">
                <el-image
                  style="width: 80px; height: 80px; border-radius: 8px"
                  :src="scope.row.imageUrl || 'https://via.placeholder.com/80'"
                  fit="cover"
                />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="物品名称" />
            <el-table-column prop="description" label="物品描述" />
            <el-table-column label="分类">
              <template #default="scope">
                {{ scope.row.category ? scope.row.category.name : '未分类' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button type="primary" size="small" @click="editItem(scope.row)" class="action-btn">编辑</el-button>
                <el-button type="danger" size="small" @click="deleteItem(scope.row.id)" class="action-btn">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="items.length === 0" class="empty-state">
            <div class="empty-animation">
              <el-icon :size="64"><i-ep-box /></el-icon>
            </div>
            <h3>暂无物品</h3>
            <p>点击添加物品按钮开始管理您的共享物品</p>
          </div>
        </div>
      </div>

      <!-- 添加/编辑物品弹窗 -->
      <el-dialog
        :title="dialogTitle"
        v-model="dialogVisible"
        width="600px"
        class="custom-dialog"
      >
        <el-form :model="form" label-width="80px" class="custom-form">
          <el-form-item label="物品图片">
            <el-upload
              class="avatar-uploader"
              action="/api/upload"
              name="file"
              :show-file-list="false"
              :on-success="handleImageSuccess"
              :before-upload="beforeImageUpload"
              :on-error="handleImageError"
            >
              <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="物品名称">
            <el-input v-model="form.name" class="custom-input" />
          </el-form-item>
          <el-form-item label="物品描述">
            <el-input type="textarea" v-model="form.description" class="custom-textarea" />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.categoryId" placeholder="请选择分类" class="custom-select">
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" placeholder="请选择状态" class="custom-select">
              <el-option label="可借用" value="available" />
              <el-option label="已借出" value="borrowed" />
              <el-option label="维护中" value="maintenance" />
            </el-select>
          </el-form-item>
          <el-form-item label="位置">
            <el-input v-model="form.location" class="custom-input" />
          </el-form-item>
          <el-form-item label="价值">
            <el-input v-model="form.value" class="custom-input" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
            <el-button type="primary" @click="saveItem" class="submit-btn">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { Plus } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const items = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({
  id: '',
  name: '',
  description: '',
  categoryId: '',
  status: 'available',
  location: '',
  value: '',
  imageUrl: ''
})

const addItem = () => {
  dialogTitle.value = '添加物品'
  form.value = {
    id: '',
    name: '',
    description: '',
    categoryId: '',
    status: 'available',
    location: '',
    value: '',
    imageUrl: ''
  }
  dialogVisible.value = true
}

const editItem = (item) => {
  dialogTitle.value = '编辑物品'
  form.value = {
    id: item.id,
    name: item.name,
    description: item.description,
    categoryId: item.category?.id || item.categoryId,
    status: item.status,
    location: item.location,
    value: item.value,
    imageUrl: item.imageUrl
  }
  dialogVisible.value = true
}

const handleImageSuccess = (response) => {
  // 确保 response 是对象，不是字符串
  const data = typeof response === 'string' ? JSON.parse(response) : response
  console.log('上传成功，返回的URL:', data.url)
  // 确保URL格式正确
  if (data.url && !data.url.startsWith('/')) {
    form.value.imageUrl = '/' + data.url
  } else {
    form.value.imageUrl = data.url
  }
  console.log('最终使用的URL:', form.value.imageUrl)
  ElMessage.success('图片上传成功')
}

const handleImageError = (error) => {
  console.error('图片上传失败:', error)
  ElMessage.error('图片上传失败，请稍后重试')
}

const beforeImageUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return true
}

const saveItem = async () => {
  try {
    if (userStore.user) {
      const itemData = {
        ...form.value,
        owner: {
          id: userStore.user.id
        }
      }
      let response
      if (form.value.id) {
        // 更新物品
        response = await axios.put('/api/items/update', itemData)
      } else {
        // 创建物品
        response = await axios.post('/api/items/create', itemData)
      }
      ElMessage.success('保存成功')
      dialogVisible.value = false
      await fetchItems()
    }
  } catch (error) {
    console.error('保存物品失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  }
}

const deleteItem = async (id) => {
  ElMessageBox.confirm('确定要删除这个物品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`/api/items/delete/${id}`)
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
      await fetchItems()
    } catch (error) {
      console.error('删除物品失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除'
    })
  })
}

const getStatusType = (status) => {
  switch (status) {
    case 'available': return 'success'
    case 'borrowed': return 'warning'
    case 'maintenance': return 'info'
    default: return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'available': return '可借用'
    case 'borrowed': return '已借出'
    case 'maintenance': return '维护中'
    default: return status
  }
}

const goBack = () => {
  router.replace('/')
}

const fetchItems = async () => {
  try {
    if (userStore.user) {
      let response
      if (userStore.user.role === 'admin') {
        // 管理员可以看到所有物品
        response = await axios.get('/api/items/findAll')
      } else {
        // 普通用户只能看到自己的物品
        response = await axios.get(`/api/items/findByOwner/${userStore.user.id}`)
      }
      items.value = response.data
      console.log('获取到的物品数据:', items.value)
    }
  } catch (error) {
    console.error('获取物品列表失败:', error)
    items.value = []
  }
}

const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/categories/findAll')
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categories.value = [
      { id: 1, name: '电子设备' },
      { id: 2, name: '书籍' },
      { id: 3, name: '运动器材' },
      { id: 4, name: '乐器' },
      { id: 5, name: '工具' },
      { id: 6, name: '其他' }
    ]
  }
}

onMounted(() => {
  fetchItems()
  fetchCategories()
})
</script>

<style scoped>
.manage-items-page {
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

/* 添加按钮 */
.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
}

/* 物品管理卡片 */
.manage-card {
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

/* 操作按钮 */
.action-btn {
  margin-right: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
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

/* 自定义弹窗 - 深色主题 */
:deep(.custom-dialog) {
  background: linear-gradient(135deg, #1a1a3e 0%, #16213e 100%);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

:deep(.custom-dialog .el-dialog__header) {
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px 24px;
}

:deep(.custom-dialog .el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

:deep(.custom-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.custom-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: white;
}

:deep(.custom-dialog .el-dialog__body) {
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  padding: 24px;
}

:deep(.custom-dialog .el-dialog__footer) {
  background: rgba(255, 255, 255, 0.05);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 16px 24px;
}

/* 表单样式 */
.custom-form {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.custom-form .el-form-item) {
  margin-bottom: 20px;
}

:deep(.custom-form .el-form-item__label) {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  font-size: 14px;
}

/* 输入框样式 - 深色背景 */
:deep(.custom-input .el-input__wrapper),
:deep(.custom-textarea .el-textarea__inner),
:deep(.custom-select .el-select__wrapper) {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  box-shadow: none;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input__wrapper:hover),
:deep(.custom-textarea .el-textarea__inner:hover),
:deep(.custom-select .el-select__wrapper:hover) {
  border-color: rgba(102, 126, 234, 0.5);
}

:deep(.custom-input .el-input__wrapper.is-focus),
:deep(.custom-textarea .el-textarea__inner:focus),
:deep(.custom-select .el-select__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

:deep(.custom-input .el-input__inner),
:deep(.custom-textarea .el-textarea__inner) {
  color: white;
  background: transparent;
}

:deep(.custom-input .el-input__inner::placeholder),
:deep(.custom-textarea .el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

:deep(.custom-select .el-select__inner) {
  color: white;
}

:deep(.custom-select .el-select__placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

:deep(.custom-input .el-input__prefix),
:deep(.custom-input .el-input__suffix) {
  color: rgba(255, 255, 255, 0.5);
}

/* 下拉菜单样式 */
:deep(.custom-select .el-select-dropdown) {
  background: rgba(30, 30, 60, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

:deep(.custom-select .el-select-dropdown__item) {
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

:deep(.custom-select .el-select-dropdown__item:hover) {
  background: rgba(102, 126, 234, 0.3);
}

:deep(.custom-select .el-select-dropdown__item.selected) {
  background: rgba(102, 126, 234, 0.5);
  color: white;
}

/* 按钮样式 */
.cancel-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
}

/* 头像上传 */
.avatar-uploader {
  border: 1px dashed rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  width: 178px;
  height: 178px;
  background: rgba(255, 255, 255, 0.05);
}

.avatar-uploader:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: rgba(255, 255, 255, 0.3);
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
  border-radius: 12px;
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
  
  .manage-card {
    padding: 20px;
  }
  
  :deep(.custom-table .el-table__header th),
  :deep(.custom-table .el-table__body td) {
    padding: 12px;
    font-size: 12px;
  }
  
  .add-btn {
    padding: 10px 20px;
  }
  
  .action-btn {
    font-size: 12px;
    padding: 4px 8px;
  }
}
</style>
