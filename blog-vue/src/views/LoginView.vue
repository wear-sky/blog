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
          class="submit-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </NButton>
      </NForm>

      <div class="auth-footer">
        <NText depth="3" class="footer-text">还没有账号？</NText>
        <NButton text type="primary" class="link-btn" @click="goRegister">立即注册</NButton>
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
  padding: 48px 32px;
  animation: fadeIn 0.5s ease;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(201, 168, 130, 0.08);
  border: 1px solid #EDE5DA;
  padding: 8px;
}

.auth-header {
  text-align: center;
  margin-bottom: 36px;
}

.header-icon {
  font-size: 1.6rem;
  color: #C9A882;
  margin-bottom: 16px;
  opacity: 0.8;
}

.auth-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.8rem;
  font-weight: 600;
  color: #3D3028;
  margin-bottom: 8px;
}

.auth-subtitle {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 0.92rem;
  font-style: italic;
  color: #A89888;
}

.auth-form {
  margin-bottom: 8px;
}

.submit-btn {
  height: 48px;
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  background: linear-gradient(135deg, #C9A882 0%, #D4B892 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(201, 168, 130, 0.25);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #D4B892 0%, #DFC8A2 100%);
  box-shadow: 0 6px 18px rgba(201, 168, 130, 0.35);
  transform: translateY(-1px);
}

.submit-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(201, 168, 130, 0.2);
}

.auth-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #F3EDE5;
}

.footer-text {
  font-size: 0.85rem;
  color: #A89888;
}

.link-btn {
  font-weight: 600;
  transition: color 0.2s ease;
}

.link-btn:hover {
  color: #D4B892;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>