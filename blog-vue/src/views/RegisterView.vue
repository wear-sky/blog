<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  NCard,
  NForm,
  NFormItem,
  NInput,
  NButton,
  NText,
  NGrid,
  NGi,
  useMessage
} from 'naive-ui'
import { register } from '@/api/user'

const router = useRouter()
const message = useMessage()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})
const loading = ref(false)

async function handleRegister() {
  if (!form.value.username || !form.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  if (form.value.password.length < 6 || form.value.password.length > 20) {
    message.warning('密码长度需在 6-20 个字符之间')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    message.warning('两次输入的密码不一致')
    return
  }
  if (form.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    message.warning('邮箱格式不正确')
    return
  }

  loading.value = true
  try {
    const data = {
      username: form.value.username,
      password: form.value.password
    }
    if (form.value.nickname) data.nickname = form.value.nickname
    if (form.value.email) data.email = form.value.email

    await register(data)
    message.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    message.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}

function goLogin() {
  router.push('/login')
}
</script>

<template>
  <div class="auth-page">
    <NCard class="auth-card" :bordered="true">
      <div class="auth-header">
        <div class="header-icon">✿</div>
        <h1 class="auth-title">创建账号</h1>
        <NText depth="3" class="auth-subtitle">开始书写属于你的故事</NText>
      </div>

      <NForm class="auth-form" @submit.prevent="handleRegister">
        <NFormItem label="用户名">
          <NInput
            v-model:value="form.username"
            placeholder="选择一个用户名"
            size="large"
            round
            autocomplete="username"
          />
        </NFormItem>

        <NGrid :cols="2" :x-gap="12">
          <NGi>
            <NFormItem label="密码">
              <NInput
                v-model:value="form.password"
                type="password"
                placeholder="6-20 个字符"
                size="large"
                round
                show-password-on="click"
                autocomplete="new-password"
              />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="确认密码">
              <NInput
                v-model:value="form.confirmPassword"
                type="password"
                placeholder="再次输入"
                size="large"
                round
                show-password-on="click"
                autocomplete="new-password"
              />
            </NFormItem>
          </NGi>
        </NGrid>

        <NGrid :cols="2" :x-gap="12">
          <NGi>
            <NFormItem label="昵称">
              <NInput
                v-model:value="form.nickname"
                placeholder="选填"
                size="large"
                round
              />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="邮箱">
              <NInput
                v-model:value="form.email"
                placeholder="选填"
                size="large"
                round
                autocomplete="email"
              />
            </NFormItem>
          </NGi>
        </NGrid>

        <NButton
          type="primary"
          block
          size="large"
          round
          :loading="loading"
          @click="handleRegister"
        >
          注册
        </NButton>
      </NForm>

      <div class="auth-footer">
        <NText depth="3" class="footer-text">已有账号？</NText>
        <NButton text type="primary" @click="goLogin">去登录</NButton>
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
  max-width: 480px;
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
