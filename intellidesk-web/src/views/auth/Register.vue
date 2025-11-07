<template>
  <div class="register-container">
    <!-- 粒子背景 -->
    <Particles
      id="tsparticles"
      :particlesInit="particlesInit"
      :options="particlesOptions"
    />

    <!-- 注册卡片 -->
    <div class="register-card" ref="registerCard">
      <div class="register-header">
        <div class="logo-container">
          <el-icon :size="48" class="logo-icon">
            <Platform />
          </el-icon>
        </div>
        <h1 class="title">创建账号</h1>
        <p class="subtitle">加入 IntelliDesk 智能客服系统</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名（4-20个字符）"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码（6-20个字符）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="昵称"
            size="large"
            prefix-icon="Postcard"
            clearable
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱"
            size="large"
            prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="手机号"
            size="large"
            prefix-icon="Phone"
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            :loading="loading"
            type="primary"
            size="large"
            class="register-button"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span class="footer-text">已有账号？</span>
        <el-link type="primary" :underline="false" @click="goToLogin">
          立即登录
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { register } from '@/api/auth'
import { gsap } from 'gsap'
import type { Engine } from 'particles.vue3'

const router = useRouter()

const registerFormRef = ref<FormInstance>()
const registerCard = ref<HTMLElement>()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: '',
  phone: ''
})

// 自定义验证规则
const validateUsername = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else if (value.length < 4 || value.length > 20) {
    callback(new Error('用户名长度为4-20个字符'))
  } else {
    callback()
  }
}

const validatePassword = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度为6-20个字符'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [{ validator: validateUsername, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [{ validator: validatePhone, trigger: 'blur' }]
}

// 粒子配置
const particlesInit = async (engine: Engine) => {
  // 可以在这里加载额外的粒子效果
}

const particlesOptions = {
  background: {
    color: {
      value: '#0d1117'
    }
  },
  fpsLimit: 120,
  particles: {
    color: {
      value: '#67C23A'
    },
    links: {
      color: '#67C23A',
      distance: 150,
      enable: true,
      opacity: 0.3,
      width: 1
    },
    move: {
      direction: 'none' as const,
      enable: true,
      outModes: {
        default: 'bounce' as const
      },
      random: false,
      speed: 1,
      straight: false
    },
    number: {
      density: {
        enable: true,
        area: 800
      },
      value: 80
    },
    opacity: {
      value: 0.5
    },
    shape: {
      type: 'circle'
    },
    size: {
      value: { min: 1, max: 3 }
    }
  },
  detectRetina: true
}

// 注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { confirmPassword, ...registerData } = registerForm
        await register(registerData)
        ElMessage.success('注册成功，即将跳转到登录页面')
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      } catch (error: any) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

// 跳转登录
const goToLogin = () => {
  router.push('/login')
}

// 页面动画
onMounted(() => {
  if (registerCard.value) {
    gsap.from(registerCard.value, {
      duration: 0.8,
      y: -50,
      opacity: 0,
      ease: 'power3.out'
    })

    gsap.from(registerCard.value.querySelectorAll('.el-form-item'), {
      duration: 0.6,
      y: 20,
      opacity: 0,
      stagger: 0.08,
      delay: 0.3,
      ease: 'power2.out'
    })
  }
})
</script>

<style scoped>
.register-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

#tsparticles {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}

.register-card {
  position: relative;
  z-index: 10;
  width: 480px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 48px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.register-card:hover {
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.4);
  transform: translateY(-5px);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-container {
  display: inline-block;
  padding: 16px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 20px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

.logo-icon {
  color: white;
  display: block;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

.register-form {
  margin-top: 24px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.register-form :deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.15);
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  margin-top: 8px;
}

.register-button:hover {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(240, 147, 251, 0.3);
}

.register-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
}

.footer-text {
  color: #909399;
  margin-right: 8px;
}

.register-footer :deep(.el-link) {
  font-weight: 600;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 自定义滚动条 */
.register-card::-webkit-scrollbar {
  width: 6px;
}

.register-card::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-thumb {
  background: rgba(240, 147, 251, 0.5);
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-thumb:hover {
  background: rgba(240, 147, 251, 0.7);
}
</style>
