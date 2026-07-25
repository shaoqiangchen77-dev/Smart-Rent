<template>
  <view class="my-page">
    <!-- 用户头部 -->
    <view class="header">
      <view class="header-bg"></view>
      <view class="header-circles">
        <view class="circle c1"></view>
        <view class="circle c2"></view>
      </view>
      <view v-if="userStore.isLoggedIn" class="user-card" @click="goPage('/pages/my/preference')">
        <view class="avatar-wrap">
          <image class="avatar" :src="userStore.userInfo?.avatar || '/static/default-avatar.png'" />
          <view class="avatar-ring"></view>
        </view>
        <view class="user-info">
          <text class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</text>
          <view class="role-badge">
            <text>{{ userStore.userInfo?.role === 1 ? '房东' : '租客' }}</text>
          </view>
        </view>
        <text class="edit-arrow">></text>
      </view>
      <view v-else class="user-card" @click="goLogin">
        <view class="avatar-wrap">
          <image class="avatar" src="/static/default-avatar.png" />
          <view class="avatar-ring"></view>
        </view>
        <text class="login-hint">点击登录</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-list">
        <view v-if="userStore.isLandlord" class="menu-item landlord-entry" @click="goPage('/pages/landlord/dashboard')">
          <view class="menu-icon-wrap icon-landlord">
            <text class="menu-icon">住</text>
          </view>
          <view class="menu-copy">
            <text class="menu-text">房东工作台</text>
            <text class="menu-desc">房源、预约、带看进度</text>
          </view>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goPage('/pages/my/collection')">
          <view class="menu-icon-wrap icon-red">
            <text class="menu-icon">藏</text>
          </view>
          <text class="menu-text">我的收藏</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goPage('/pages/my/history')">
          <view class="menu-icon-wrap icon-blue">
            <text class="menu-icon">看</text>
          </view>
          <text class="menu-text">浏览记录</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goPage('/pages/my/contract')">
          <view class="menu-icon-wrap icon-green">
            <text class="menu-icon">约</text>
          </view>
          <text class="menu-text">我的合同</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @click="goPage('/pages/my/bill')">
          <view class="menu-icon-wrap icon-orange">
            <text class="menu-icon">账</text>
          </view>
          <text class="menu-text">我的账单</text>
          <text class="menu-arrow">></text>
        </view>
      </view>

      <view class="menu-list">
        <view class="menu-item" @click="goPage('/pages/my/preference')">
          <view class="menu-icon-wrap icon-purple">
            <text class="menu-icon">偏</text>
          </view>
          <text class="menu-text">偏好设置</text>
          <text class="menu-arrow">></text>
        </view>
      </view>

      <view v-if="userStore.isLoggedIn" class="menu-list">
        <view class="menu-item logout" @click="onLogout">
          <view class="menu-icon-wrap icon-gray">
            <text class="menu-icon">退</text>
          </view>
          <text class="menu-text logout-text">退出登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(() => {
  if (userStore.isLoggedIn) userStore.fetchUserInfo()
})

function goLogin() { uni.navigateTo({ url: '/pages/login/login' }) }

function goPage(url: string) {
  if (!userStore.isLoggedIn) { goLogin(); return }
  uni.navigateTo({ url })
}

function onLogout() {
  userStore.logout()
  uni.showToast({ title: '已退出登录', icon: 'success' })
}
</script>

<style scoped>
.my-page {
  min-height: 100vh;
  background: #f6f4ef;
}
.header {
  position: relative;
  padding: 0 30rpx 30rpx;
}
.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 320rpx;
  background: #1c1812;
  border-radius: 0 0 34rpx 34rpx;
}
.user-card {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 36rpx 28rpx;
  margin-top: 30rpx;
  box-shadow: 0 16rpx 36rpx rgba(28,24,18,0.12);
  border: 1rpx solid #e7e1d6;
}
.avatar-wrap {
  position: relative;
  margin-right: 24rpx;
}
.avatar {
  width: 110rpx;
  height: 110rpx;
  border-radius: 50%;
  position: relative;
  z-index: 1;
}
.avatar-ring {
  position: absolute;
  top: -6rpx;
  left: -6rpx;
  right: -6rpx;
  bottom: -6rpx;
  border-radius: 50%;
  background: #b08a3a;
  z-index: 0;
}
.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.nickname {
  font-size: 34rpx;
  font-weight: 700;
  color: #1c1812;
  margin-bottom: 8rpx;
  letter-spacing: 1rpx;
}
.role-badge {
  display: inline-block;
  padding: 6rpx 16rpx;
  background: #f4ecd6;
  color: #b08a3a;
  font-size: 22rpx;
  border-radius: 12rpx;
  border: 1rpx solid #c5ded9;
}
.edit-arrow {
  color: #ccc;
  font-size: 28rpx;
}
.login-hint {
  font-size: 32rpx;
  color: #666;
  font-weight: 500;
}
.menu-section {
  padding: 0 30rpx;
  margin-top: 20rpx;
}
.menu-list {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  border: 1rpx solid #e7e1d6;
  box-shadow: none;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
  transition: background 0.2s ease;
}
.menu-item:active {
  background: #f8f9fa;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-icon-wrap {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12rpx;
  margin-right: 20rpx;
}
.icon-red { background: #fff2e6; color: #c2410c; }
.icon-blue { background: #e7f0f7; color: #315c8a; }
.icon-green { background: #f4ecd6; color: #b08a3a; }
.icon-orange { background: #f5eddd; color: #8a6b38; }
.icon-purple { background: #eee9df; color: #5d5147; }
.icon-gray { background: #eeeeea; color: #6f7d79; }
.icon-landlord { background: #1c1812; color: #fff; }
.menu-icon {
  font-size: 24rpx;
  font-weight: 750;
}
.menu-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}
.menu-copy {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.menu-desc {
  margin-top: 6rpx;
  color: #7b8582;
  font-size: 22rpx;
}
.landlord-entry {
  background: #fbfaf7;
}
.menu-arrow {
  color: #ccc;
  font-size: 24rpx;
}
.logout-text {
  color: #ff4d4f;
}
</style>
