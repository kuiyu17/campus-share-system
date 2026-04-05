<template>
  <div class="login-page">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="particles">
        <span v-for="n in 20" :key="n" :style="particleStyle(n)"></span>
      </div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-container">
      <div class="login-card">
        <div class="card-glow"></div>
        <div class="card-content">
          <!-- Logo -->
          <div class="logo-section">
            <div class="logo-pulse">
              <el-icon :size="48"><i-ep-user /></el-icon>
            </div>
            <h1 class="main-title">
              <span class="gradient-text">欢迎</span>
              <span class="normal-text">登录</span>
            </h1>
            <p class="sub-title">校园物品共享与借用管理系统</p>
          </div>

          <!-- 登录表单 -->
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
            <div class="login-type-tabs">
              <div 
                class="type-tab" 
                :class="{ active: loginType === 'username' }"
                @click="loginType = 'username'"
              >
                <el-icon><i-ep-user /></el-icon>
                <span>用户名</span>
              </div>
              <div 
                class="type-tab" 
                :class="{ active: loginType === 'phone' }"
                @click="loginType = 'phone'"
              >
                <el-icon><i-ep-phone /></el-icon>
                <span>手机号</span>
              </div>
            </div>

            <div class="input-group">
              <div class="input-wrapper">
                <el-icon class="input-icon"><i-ep-user /></el-icon>
                <el-input 
                  v-model="loginForm.account" 
                  :placeholder="loginType === 'username' ? '请输入用户名' : '请输入手机号'"
                  class="custom-input"
                ></el-input>
              </div>
            </div>

            <div class="input-group">
              <div class="input-wrapper">
                <el-icon class="input-icon"><i-ep-lock /></el-icon>
                <el-input 
                  type="password" 
                  v-model="loginForm.password" 
                  placeholder="请输入密码"
                  class="custom-input"
                  @keyup.enter="login"
                ></el-input>
              </div>
            </div>

            <button class="login-btn" @click="login" :disabled="loading">
              <span class="btn-content">
                <el-icon :size="20" v-if="loading" class="spinning"><i-ep-loading /></el-icon>
                <span>{{ loading ? '登录中...' : '立即登录' }}</span>
              </span>
              <div class="btn-glow"></div>
            </button>

            <div class="register-link">
              <span>还没有账号？</span>
              <a @click="goToRegister" class="link-text">立即注册</a>
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
const loginFormRef = ref(null)
const loginType = ref('username')
const loading = ref(false)
const loginForm = ref({
  account: '',
  password: ''
})

const particleStyle = (n) => ({
  left: `${Math.random() * 100}%`,
  animationDelay: `${Math.random() * 10}s`,
  animationDuration: `${10 + Math.random() * 10}s`
})

const validateAccount = (rule, value, callback) => {
  if (!value) {
    callback(new Error(loginType.value === 'username' ? '请输入用户名' : '请输入手机号'))
  } else if (loginType.value === 'phone' && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号格式'))
  } else {
    callback()
  }
}

const rules = {
  account: [
    { validator: validateAccount, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const login = async () => {
  if (loginFormRef.value) {
    await loginFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true
        const success = await userStore.login(loginForm.value.account, loginForm.value.password, loginType.value)
        loading.value = false
        if (success) {
          ElMessage.success('登录成功')
          router.push('/home')
        } else {
          ElMessage.error(loginType.value === 'username' ? '用户名或密码错误' : '手机号或密码错误')
        }
      }
    })
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 登录容器 */
.login-container {
  position: relative;
  z-index: 1;
  padding: 20px;
}

.login-card {
  width: 420px;
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

/* 登录类型切换 */
.login-type-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.type-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s ease;
}

.type-tab:hover {
  background: rgba(255, 255, 255, 0.1);
}

.type-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

/* 输入框 */
.input-group {
  margin-bottom: 20px;
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

/* 登录按钮 */
.login-btn {
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

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.login-btn:disabled {
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

.login-btn:hover .btn-glow {
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

/* 注册链接 */
.register-link {
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
@media (max-width: 480px) {
  .login-card {
    width: 100%;
    max-width: 360px;
  }
  
  .card-content {
    padding: 30px 24px;
  }
  
  .main-title {
    font-size: 24px;
  }
}
</style>
