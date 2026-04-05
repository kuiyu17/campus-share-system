<template>
  <div class="register-page">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="particles">
        <span v-for="n in 20" :key="n" :style="particleStyle(n)"></span>
      </div>
    </div>

    <!-- 注册卡片 -->
    <div class="register-container">
      <div class="register-card">
        <div class="card-glow"></div>
        <div class="card-content">
          <!-- Logo -->
          <div class="logo-section">
            <div class="logo-pulse">
              <el-icon :size="48"><i-ep-user-plus /></el-icon>
            </div>
            <h1 class="main-title">
              <span class="gradient-text">创建</span>
              <span class="normal-text">账号</span>
            </h1>
            <p class="sub-title">加入校园物品共享社区</p>
          </div>

          <!-- 注册表单 -->
          <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
            <div class="form-row">
              <div class="input-group">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><i-ep-user /></el-icon>
                  <el-input 
                    v-model="registerForm.username" 
                    placeholder="请输入用户名"
                    class="custom-input"
                  ></el-input>
                </div>
              </div>

              <div class="input-group">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><i-ep-lock /></el-icon>
                  <el-input 
                    type="password"
                    v-model="registerForm.password" 
                    placeholder="请输入密码"
                    class="custom-input"
                  ></el-input>
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="input-group">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><i-ep-user-filled /></el-icon>
                  <el-input 
                    v-model="registerForm.name" 
                    placeholder="请输入姓名"
                    class="custom-input"
                  ></el-input>
                </div>
              </div>

              <div class="input-group">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><i-ep-phone /></el-icon>
                  <el-input 
                    v-model="registerForm.phone" 
                    placeholder="请输入手机号"
                    class="custom-input"
                  ></el-input>
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="input-group">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><i-ep-message /></el-icon>
                  <el-input 
                    v-model="registerForm.email" 
                    placeholder="请输入邮箱（选填）"
                    class="custom-input"
                  ></el-input>
                </div>
              </div>

              <div class="input-group">
                <div class="input-wrapper role-select">
                  <el-icon class="input-icon"><i-ep-suitcase /></el-icon>
                  <el-select v-model="registerForm.role" placeholder="选择角色" class="custom-select">
                    <el-option label="学生" value="student">
                      <el-icon><i-ep-reading /></el-icon>
                      <span>学生</span>
                    </el-option>
                    <el-option label="老师" value="teacher">
                      <el-icon><i-ep-school /></el-icon>
                      <span>老师</span>
                    </el-option>
                  </el-select>
                </div>
              </div>
            </div>

            <button class="register-btn" @click="register" :disabled="loading">
              <span class="btn-content">
                <el-icon :size="20" v-if="loading" class="spinning"><i-ep-loading /></el-icon>
                <span>{{ loading ? '注册中...' : '立即注册' }}</span>
              </span>
              <div class="btn-glow"></div>
            </button>

            <div class="login-link">
              <span>已有账号？</span>
              <a @click="goToLogin" class="link-text">立即登录</a>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)
const registerForm = ref({
  username: '',
  password: '',
  name: '',
  email: '',
  phone: '',
  role: 'student'
})

const particleStyle = (n) => ({
  left: `${Math.random() * 100}%`,
  animationDelay: `${Math.random() * 10}s`,
  animationDuration: `${10 + Math.random() * 10}s`
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const register = async () => {
  if (registerFormRef.value) {
    await registerFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true
        const success = await userStore.register(registerForm.value)
        loading.value = false
        if (success) {
          ElMessage.success('注册成功')
          router.push('/login')
        } else {
          ElMessage.error('注册失败，请稍后重试')
        }
      }
    })
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
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
  width: 300px;
  height: 300px;
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

/* 注册容器 */
.register-container {
  position: relative;
  z-index: 1;
}

.register-card {
  width: 600px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

.card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.3) 0%, transparent 70%);
  animation: rotate 10s linear infinite;
  pointer-events: none;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.card-content {
  position: relative;
  z-index: 1;
  padding: 40px;
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 32px;
}

.logo-pulse {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  animation: pulse 2s infinite;
  box-shadow: 0 0 30px rgba(102, 126, 234, 0.5);
}

@keyframes pulse {
  0%, 100% { transform: scale(1); box-shadow: 0 0 30px rgba(102, 126, 234, 0.5); }
  50% { transform: scale(1.05); box-shadow: 0 0 50px rgba(102, 126, 234, 0.8); }
}

.main-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
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
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

/* 表单布局 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.input-group {
  width: 100%;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 18px;
  z-index: 1;
}

:deep(.custom-input .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  padding-left: 44px !important;
}

:deep(.custom-input .el-input__inner) {
  color: white !important;
  height: 48px;
}

:deep(.custom-input .el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

:deep(.custom-input .el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.3) !important;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  border-color: #667eea !important;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
}

/* 角色选择 */
.role-select {
  width: 100%;
}

:deep(.custom-select .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  padding-left: 44px !important;
}

:deep(.custom-select .el-input__inner) {
  color: white !important;
  height: 48px;
}

:deep(.custom-select .el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

:deep(.custom-select .el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.3) !important;
}

:deep(.custom-select .el-input__wrapper.is-focus) {
  border-color: #667eea !important;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) !important;
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  position: relative;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 0;
  height: 0;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  transition: all 0.5s ease;
  z-index: 0;
}

.register-btn:hover .btn-glow {
  width: 400px;
  height: 400px;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 登录链接 */
.login-link {
  text-align: center;
  margin-top: 24px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
}

.link-text {
  color: #667eea;
  cursor: pointer;
  margin-left: 4px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.link-text:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 768px) {
  .register-card {
    width: 100%;
    max-width: 500px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .card-content {
    padding: 30px 24px;
  }
  
  .main-title {
    font-size: 24px;
  }
}
</style>
