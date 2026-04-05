<template>
  <div class="profile-page">
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
              <el-icon :size="28"><i-ep-user /></el-icon>
            </div>
            <div class="header-title">
              <h1>个人信息</h1>
              <p>管理您的个人资料和账户</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 个人信息卡片 -->
      <div class="profile-card">
        <div class="card-content">
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="custom-form">
            <div class="form-section">
              <h3 class="section-title">
                <el-icon><i-ep-user-filled /></el-icon>
                基本信息
              </h3>
              <div class="form-row">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="form.username" disabled class="custom-input" />
                </el-form-item>
                
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" placeholder="请输入姓名" class="custom-input" />
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱" class="custom-input" />
                </el-form-item>
                
                <el-form-item label="电话" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入电话" class="custom-input" />
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item label="角色">
                  <el-input v-model="form.role" disabled class="custom-input" />
                </el-form-item>
                
                <el-form-item label="信誉分">
                  <el-input v-model="form.creditScore" disabled class="custom-input" />
                </el-form-item>
              </div>
            </div>

            <div class="form-section">
              <h3 class="section-title">
                <el-icon><i-ep-lock /></el-icon>
                修改密码
              </h3>
              <div class="form-row">
                <el-form-item label="新密码" prop="newPassword">
                  <el-input 
                    v-model="form.newPassword" 
                    type="password" 
                    placeholder="不修改请留空"
                    show-password
                    class="custom-input"
                  />
                </el-form-item>
                
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input 
                    v-model="form.confirmPassword" 
                    type="password" 
                    placeholder="不修改请留空"
                    show-password
                    class="custom-input"
                  />
                </el-form-item>
              </div>
            </div>
            
            <div class="form-actions">
              <button class="submit-btn" @click="handleSubmit">
                <el-icon><i-ep-check /></el-icon>
                <span>保存修改</span>
              </button>
              <button class="reset-btn" @click="handleReset">
                <el-icon><i-ep-refresh /></el-icon>
                <span>重置</span>
              </button>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 账户安全卡片 -->
      <div class="security-card">
        <div class="card-header">
          <div class="header-icon danger">
            <el-icon :size="24"><i-ep-warning /></el-icon>
          </div>
          <div class="header-title">
            <h2>账户安全</h2>
            <p>注销账户将删除您的所有数据，此操作不可恢复</p>
          </div>
        </div>
        <div class="security-content">
          <div class="warning-text">
            <el-icon><i-ep-warning-filled /></el-icon>
            <span>注销账户前，请确保：</span>
            <ul>
              <li>所有借用的物品已归还</li>
              <li>所有借用请求已处理完毕</li>
              <li>您发布的物品未被借用</li>
            </ul>
          </div>
          <button class="delete-btn" @click="handleDeleteAccount">
            <el-icon><i-ep-delete /></el-icon>
            <span>注销账户</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)

const form = ref({
  id: '',
  username: '',
  name: '',
  email: '',
  phone: '',
  role: '',
  creditScore: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (form.value.newPassword && value !== form.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const goBack = () => {
  router.replace('/')
}

const initForm = () => {
  if (userStore.user) {
    form.value = {
      id: userStore.user.id,
      username: userStore.user.username,
      name: userStore.user.name || '',
      email: userStore.user.email || '',
      phone: userStore.user.phone || '',
      role: userStore.user.role || '',
      creditScore: userStore.user.creditScore || 100,
      newPassword: '',
      confirmPassword: ''
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const updateData = {
        id: form.value.id,
        username: form.value.username,
        name: form.value.name,
        email: form.value.email,
        phone: form.value.phone,
        role: form.value.role
      }
      
      // 如果填写了新密码，则添加密码字段
      if (form.value.newPassword) {
        updateData.password = form.value.newPassword
      }
      
      const success = await userStore.updateUserInfo(updateData)
      if (success) {
        ElMessage.success('个人信息修改成功')
        form.value.newPassword = ''
        form.value.confirmPassword = ''
      } else {
        ElMessage.error('修改失败，请稍后重试')
      }
    }
  })
}

const handleReset = () => {
  initForm()
  ElMessage.info('已重置为当前信息')
}

const handleDeleteAccount = () => {
  ElMessageBox.confirm(
    '确定要注销账户吗？此操作不可恢复，您的所有数据将被删除！',
    '警告',
    {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const result = await userStore.deleteAccount()
    if (result.success) {
      ElMessage.success('账户已注销')
      router.push('/login')
    } else {
      ElMessage.error(result.message || '注销失败，请稍后重试')
    }
  }).catch(() => {
    ElMessage.info('已取消注销')
  })
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.profile-page {
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
  max-width: 900px;
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

.header-icon.danger {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.header-title h1 {
  font-size: 24px;
  font-weight: 700;
  color: white;
  margin: 0 0 4px 0;
}

.header-title h2 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0 0 4px 0;
}

.header-title p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

/* 个人信息卡片 */
.profile-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.card-content {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 表单部分 */
.form-section {
  margin-bottom: 32px;
}

.form-section:last-of-type {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 自定义表单 */
.custom-form {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.custom-form .el-form-item__label) {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

/* 自定义输入框 */
.custom-input {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input__wrapper) {
  background: transparent;
  border: none;
  box-shadow: none;
}

:deep(.custom-input .el-input__inner) {
  color: white;
}

:deep(.custom-input .el-input__prefix),
:deep(.custom-input .el-input__suffix) {
  color: rgba(255, 255, 255, 0.5);
}

:deep(.custom-input.is-disabled .el-input__inner) {
  color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.submit-btn {
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

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
}

.reset-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

/* 账户安全卡片 */
.security-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.security-card .card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.security-content {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.warning-text {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 24px;
}

.warning-text .el-icon {
  color: #f5576c;
  margin-right: 8px;
  font-size: 18px;
}

.warning-text span {
  font-weight: 600;
  color: white;
}

.warning-text ul {
  margin: 12px 0 0 0;
  padding-left: 24px;
  color: rgba(255, 255, 255, 0.5);
}

.warning-text li {
  margin-bottom: 8px;
}

.delete-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.4);
}

.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(245, 87, 108, 0.6);
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
  
  .profile-card,
  .security-card {
    padding: 20px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .submit-btn,
  .reset-btn,
  .delete-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
