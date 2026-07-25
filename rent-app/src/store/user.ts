import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo, type UserInfo, type LoginParams } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isLandlord = computed(() => userInfo.value?.role === 1)
  const isAdmin = computed(() => userInfo.value?.role === 2)

  async function login(params: LoginParams) {
    const res = await loginApi(params)
    token.value = res.data.token
    uni.setStorageSync('token', res.data.token)
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
    uni.removeStorageSync('token')
  }

  return { token, userInfo, isLoggedIn, isLandlord, isAdmin, login, fetchUserInfo, logout }
})
