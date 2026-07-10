<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { NSpace, NButton, NAvatar, NDropdown, NText, NInput, NIcon } from 'naive-ui'
import {
  CreateOutline,
  LogOutOutline,
  PersonOutline,
  SearchOutline
} from '@vicons/ionicons5'

const router = useRouter()
const auth = useAuthStore()

const searchKeyword = ref('')

function handleSearch() {
  const q = searchKeyword.value.trim()
  if (!q) return
  router.push({ path: '/search', query: { q } })
}

onMounted(() => {
  if (auth.isLoggedIn) {
    auth.fetchUser()
  }
})

const displayName = computed(() => {
  return auth.user?.nickname || auth.user?.username || ''
})

const userInitial = computed(() => {
  return displayName.value.charAt(0).toUpperCase() || 'U'
})

const userMenuOptions = computed(() => [
  {
    label: displayName.value,
    key: 'name',
    disabled: true,
    icon: () => h(PersonOutline)
  },
  { type: 'divider', key: 'd1' },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h(LogOutOutline)
  }
])

function handleUserMenu(key) {
  if (key === 'logout') {
    auth.logout()
    router.push('/')
  }
}

function goHome() {
  router.push('/')
}

function goCreate() {
  router.push('/blog/create')
}

function goLogin() {
  router.push('/login')
}

function goRegister() {
  router.push('/register')
}
</script>

<template>
  <div class="navbar-inner">
    <div class="navbar-brand" @click="goHome">
      <NText class="brand-icon">✦</NText>
      <NText class="brand-text" tag="span">暖光笔记</NText>
    </div>

    <div class="navbar-search">
      <NInput
        v-model:value="searchKeyword"
        placeholder="搜索..."
        size="small"
        round
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <NIcon :component="SearchOutline" :size="16" />
        </template>
      </NInput>
    </div>

    <NSpace align="center" :size="8">
      <template v-if="auth.isLoggedIn">
        <NButton quaternary size="small" @click="goCreate">
          <template #icon>
            <NIcon :component="CreateOutline" />
          </template>
          写博客
        </NButton>
        <NDropdown
          :options="userMenuOptions"
          trigger="click"
          @select="handleUserMenu"
        >
          <NButton quaternary size="small" class="user-btn">
            <NAvatar
              :size="26"
              round
              :style="{ backgroundColor: '#C9A882', color: '#fff', fontSize: '12px', fontWeight: '700' }"
            >
              {{ userInitial }}
            </NAvatar>
            <span class="user-name">{{ displayName }}</span>
          </NButton>
        </NDropdown>
      </template>
      <template v-else>
        <NButton quaternary size="small" @click="goLogin">登录</NButton>
        <NButton type="primary" size="small" round @click="goRegister">注册</NButton>
      </template>
    </NSpace>
  </div>
</template>

<style scoped>
.navbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  max-width: 960px;
  margin: 0 auto;
  padding: 0 32px;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.brand-icon {
  font-size: 1.3rem;
  opacity: 0.8;
  color: #C9A882;
}

.brand-text {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.4rem;
  font-weight: 600;
  color: #3D3028;
  letter-spacing: 0.06em;
}

.user-btn {
  gap: 6px;
}

.user-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: #3D3028;
}

.navbar-search {
  flex: 1;
  max-width: 240px;
  margin: 0 24px;
}
</style>