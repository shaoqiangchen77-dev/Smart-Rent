import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo, type UserInfo, type LoginParams } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 2)

  async function login(params: LoginParams) {
    logout()
    const res = await loginApi(params)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await fetchUserInfo()
  }

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, isLoggedIn, isAdmin, login, fetchUserInfo, logout }
})
