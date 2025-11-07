<template>
  <div class="login-container">
    <!-- 粒子背景 -->
    <Particles
      id="tsparticles"
      :particlesInit="particlesInit"
      :options="particlesOptions"
    />

    <!-- 登录卡片 -->
    <div class="login-card" ref="loginCard">
      <div class="login-header">
        <div class="logo-container">
          <el-icon :size="48" class="logo-icon">
            <Platform />
          </el-icon>
        </div>
        <h1 class="title">IntelliDesk</h1>
        <p class="subtitle">智能客服与工单系统</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            :loading="loading"
            type="primary"
            size="large"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span class="footer-text">还没有账号？</span>
        <el-link type="primary" :underline="false" @click="goToRegister">
          立即注册
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { gsap } from 'gsap'
import type { Engine } from 'particles.vue3'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loginCard = ref<HTMLElement>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
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
      value: '#409EFF'
    },
    links: {
      color: '#409EFF',
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

// 登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.loginAction(loginForm)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error: any) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

// 跳转注册
const goToRegister = () => {
  router.push('/register')
}

// 页面动画
onMounted(() => {
  if (loginCard.value) {
    gsap.from(loginCard.value, {
      duration: 0.8,
      y: -50,
      opacity: 0,
      ease: 'power3.out'
    })

    gsap.from(loginCard.value.querySelectorAll('.el-form-item'), {
      duration: 0.6,
      y: 20,
      opacity: 0,
      stagger: 0.1,
      delay: 0.3,
      ease: 'power2.out'
    })
  }
})
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

#tsparticles {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}

.login-card {
  position: relative;
  z-index: 10;
  width: 420px;
  padding: 48px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.login-card:hover {
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.4);
  transform: translateY(-5px);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  display: inline-block;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

.login-form {
  margin-top: 32px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  margin-top: 8px;
}

.login-button:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
}

.footer-text {
  color: #909399;
  margin-right: 8px;
}

.login-footer :deep(.el-link) {
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
</style>
