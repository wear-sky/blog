<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  NCard,
  NForm,
  NFormItem,
  NInput,
  NButton,
  NText,
  NSpace,
  useMessage
} from 'naive-ui'

const router = useRouter()
const auth = useAuthStore()
const message = useMessage()

const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    message.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    await auth.login(form.value.username, form.value.password)
    await auth.fetchUser()
    message.success('登录成功')
    router.push('/')
  } catch (e) {
    message.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}
</script>

<template>
  <div class="auth-page">
    <NCard class="auth-card" :bordered="true">
      <div class="auth-header">
        <div class="header-icon">✿</div>
        <h1 class="auth-title">欢迎回来</h1>
        <NText depth="3" class="auth-subtitle">登录以继续你的旅程</NText>
      </div>

      <NForm class="auth-form" @submit.prevent="handleLogin">
        <NFormItem label="用户名">
          <NInput
            v-model:value="form.username"
            placeholder="输入用户名"
            size="large"
            round
            autocomplete="username"
          />
        </NFormItem>

        <NFormItem label="密码">
          <NInput
            v-model:value="form.password"
            type="password"
            placeholder="输入密码"
            size="large"
            round
            show-password-on="click"
            autocomplete="current-password"
            @keyup.enter="handleLogin"
          />
        </NFormItem>

        <NButton
          type="primary"
          block
          size="large"
          round
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </NButton>
      </NForm>

      <div class="auth-footer">
        <NText depth="3" class="footer-text">还没有账号？</NText>
        <NButton text type="primary" @click="goRegister">立即注册</NButton>
      </div>
    </NCard>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 64px);
  padding: 40px 28px;
  animation: fadeIn 0.5s ease;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  border-radius: 16px;
  box-shadow: 0 2px 20px rgba(61, 58, 51, 0.06);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.header-icon {
  font-size: 1.6rem;
  color: #5b8a6e;
  margin-bottom: 16px;
  opacity: 0.7;
}

.auth-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.8rem;
  font-weight: 600;
  color: #3d3a33;
  margin-bottom: 8px;
}

.auth-subtitle {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 0.92rem;
  font-style: italic;
}

.auth-form {
  margin-bottom: 8px;
}

.auth-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eeeae2;
}

.footer-text {
  font-size: 0.85rem;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>