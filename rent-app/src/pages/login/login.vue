<template>
  <view class="login-page">
    <view class="hero">
      <text class="brand">SmartRent</text>
      <text class="headline">住进更确定的日常</text>
      <text class="subtitle">收藏房源、预约看房、合同账单都在这里。</text>
    </view>

    <view class="form-card">
      <view class="tabs">
        <view class="tab" :class="{ active: !isRegister }" @click="isRegister = false">登录</view>
        <view class="tab" :class="{ active: isRegister }" @click="isRegister = true">注册</view>
      </view>

      <view class="input-group">
        <text class="input-mark">用</text>
        <input v-model="form.username" placeholder="请输入用户名" class="input" />
      </view>
      <view class="input-group">
        <text class="input-mark">密</text>
        <input v-model="form.password" placeholder="请输入密码" :password="!showPwd" class="input" />
        <text class="pwd-toggle" @click="showPwd = !showPwd">{{ showPwd ? '隐藏' : '显示' }}</text>
      </view>
      <view v-if="isRegister" class="input-group">
        <text class="input-mark">电</text>
        <input v-model="form.phone" placeholder="请输入手机号" type="number" class="input" />
      </view>

      <view v-if="isRegister" class="role-select">
        <view
          v-for="role in roleOptions"
          :key="role.value"
          class="role-item"
          :class="{ active: form.role === role.value }"
          @click="form.role = role.value"
        >
          <text class="role-title">{{ role.label }}</text>
          <text class="role-desc">{{ role.value === 0 ? '找房与租住管理' : '发布房源与处理预约' }}</text>
        </view>
      </view>

      <button class="btn-primary" :loading="submitting" @click="onSubmit">
        <text>{{ isRegister ? '创建账号' : '进入应用' }}</text>
      </button>

      <view class="divider">
        <view class="line"></view>
        <text>快捷方式</text>
        <view class="line"></view>
      </view>

      <button class="btn-wechat" @click="onWxLogin">
        <text>微信快捷登录</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { register } from '@/api/user'
import { post } from '@/utils/request'

const userStore = useUserStore()
const isRegister = ref(false)
const submitting = ref(false)
const showPwd = ref(false)

const form = reactive({
  username: '',
  password: '',
  phone: '',
  role: 0,
})

const roleOptions = [
  { label: '租客', value: 0 },
  { label: '房东', value: 1 },
]

async function onSubmit() {
  if (!form.username || !form.password) {
    uni.showToast({ title: '请填写用户名和密码', icon: 'none' }); return
  }
  submitting.value = true
  try {
    if (isRegister.value) {
      if (!form.phone) {
        uni.showToast({ title: '请填写手机号', icon: 'none' }); return
      }
      await register({ username: form.username, password: form.password, phone: form.phone, role: form.role })
      uni.showToast({ title: '注册成功，请登录', icon: 'success' })
      isRegister.value = false
    } else {
      await userStore.login({ username: form.username, password: form.password })
      uni.switchTab({ url: '/pages/index/index' })
    }
  } finally { submitting.value = false }
}

async function onWxLogin() {
  submitting.value = true
  try {
    // #ifdef MP-WEIXIN
    const loginRes: any = await new Promise((resolve, reject) => { uni.login({ success: resolve, fail: reject }) })
    const res: any = await post('/user/wx-login', { code: loginRes.code })
    uni.setStorageSync('token', res.data.token)
    await userStore.fetchUserInfo()
    uni.switchTab({ url: '/pages/index/index' })
    // #endif
    // #ifndef MP-WEIXIN
    uni.showToast({ title: '请在微信小程序中使用', icon: 'none' })
    // #endif
  } finally { submitting.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 28rpx 28rpx 60rpx;
  background: #f6f4ef;
}
.hero {
  min-height: 380rpx;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 42rpx 34rpx;
  border-radius: 24rpx;
  background: linear-gradient(150deg, #241d12 0%, #1c1812 55%, #2a2214 100%);
  position: relative;
  overflow: hidden;
}
.hero::before {
  content: "";
  position: absolute;
  right: -110rpx;
  top: -120rpx;
  width: 400rpx;
  height: 400rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(214,192,136,0.4), rgba(214,192,136,0));
}
.hero::after {
  content: "";
  position: absolute;
  left: 36rpx;
  top: 48rpx;
  width: 160rpx;
  height: 160rpx;
  border-radius: 28rpx;
  border: 2rpx solid rgba(255,255,255,0.12);
  transform: rotate(10deg);
}
.brand,
.headline,
.subtitle {
  position: relative;
  z-index: 1;
  display: block;
}
.brand {
  color: #cdbb91;
  font-size: 24rpx;
  font-weight: 760;
}
.headline {
  margin-top: 14rpx;
  color: #fffaf0;
  font-size: 48rpx;
  font-weight: 850;
  line-height: 1.18;
}
.subtitle {
  margin-top: 14rpx;
  color: #e8dcc0;
  font-size: 25rpx;
  line-height: 1.6;
}
.form-card {
  margin-top: -34rpx;
  padding: 34rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 20rpx;
  position: relative;
  z-index: 2;
  box-shadow: 0 18rpx 42rpx rgba(31,42,46,0.08);
}
.tabs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12rpx;
  padding: 8rpx;
  margin-bottom: 28rpx;
  background: #f7f4ee;
  border-radius: 14rpx;
}
.tab {
  height: 66rpx;
  line-height: 66rpx;
  text-align: center;
  border-radius: 11rpx;
  color: #7b8582;
  font-size: 28rpx;
  font-weight: 700;
}
.tab.active {
  background: #fff;
  color: #b08a3a;
  box-shadow: 0 8rpx 18rpx rgba(31,42,46,0.06);
}
.input-group {
  display: flex;
  align-items: center;
  height: 86rpx;
  padding: 0 20rpx;
  margin-bottom: 18rpx;
  background: #f8f6f1;
  border: 1rpx solid transparent;
  border-radius: 14rpx;
}
.input-group:focus-within {
  border-color: #e4d2a8;
  background: #fff;
}
.input-mark {
  width: 48rpx;
  height: 48rpx;
  line-height: 48rpx;
  margin-right: 14rpx;
  border-radius: 12rpx;
  background: #f4ecd6;
  color: #b08a3a;
  text-align: center;
  font-size: 22rpx;
  font-weight: 800;
}
.input {
  flex: 1;
  height: 84rpx;
  color: #1f2a2e;
  font-size: 28rpx;
}
.pwd-toggle {
  flex: 0 0 auto;
  padding: 0 6rpx;
  color: #b08a3a;
  font-size: 24rpx;
  font-weight: 700;
}
.role-select {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  margin-bottom: 18rpx;
}
.role-item {
  padding: 18rpx;
  border-radius: 14rpx;
  background: #f8f6f1;
  border: 2rpx solid transparent;
}
.role-item.active {
  border-color: #b08a3a;
  background: #f4ecd6;
}
.role-title,
.role-desc {
  display: block;
}
.role-title {
  color: #1c1812;
  font-size: 27rpx;
  font-weight: 780;
}
.role-desc {
  margin-top: 8rpx;
  color: #7b8582;
  font-size: 21rpx;
  line-height: 1.4;
}
.btn-primary,
.btn-wechat {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 14rpx;
  font-size: 30rpx;
  font-weight: 780;
}
.btn-primary {
  margin-top: 10rpx;
  background: linear-gradient(135deg, #b08a3a, #d6c088);
  color: #1c1812;
  box-shadow: 0 12rpx 26rpx rgba(176,138,58,0.32);
}
.btn-primary:active {
  transform: scale(0.985);
}
.divider {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin: 30rpx 0;
  color: #9aa29f;
  font-size: 22rpx;
}
.line {
  flex: 1;
  height: 1rpx;
  background: #e7e1d6;
}
.btn-wechat {
  background: #1c1812;
  color: #fffaf0;
}
.btn-wechat:active {
  transform: scale(0.985);
}
</style>
