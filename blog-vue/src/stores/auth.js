import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login as loginApi, getUserInfo } from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(getToken())
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  async function login(username, password) {
    const tokenValue = await loginApi({ username, password })
    token.value = tokenValue
    setToken(tokenValue)
  }

  async function fetchUser() {
    if (!token.value) return
    try {
      user.value = await getUserInfo()
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = null
    user.value = null
    removeToken()
  }

  return { token, user, isLoggedIn, login, fetchUser, logout }
})
