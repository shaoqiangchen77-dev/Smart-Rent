<template>
  <view class="navbar" :style="{ background: bg }">
    <!-- 状态栏占位：与下方导航行、页头同色 → 一体连续，不突兀 -->
    <view class="navbar-status" :style="{ height: statusBarHeight + 'px' }"></view>
    <view class="navbar-row" :style="{ height: navHeight + 'px' }">
      <view v-if="showBack" class="navbar-back" @click="onBack">
        <SrIcon name="chev-left" :size="44" color="#b08a3a" />
      </view>
      <text v-if="title" class="navbar-title">{{ title }}</text>
      <view v-if="showHome" class="navbar-home" @click="onHome">
        <SrIcon name="home" :size="40" color="#b08a3a" />
      </view>
      <slot name="right" />
    </view>
  </view>
</template>

<script setup lang="ts">
import SrIcon from './SrIcon.vue'

const props = withDefaults(
  defineProps<{
    title?: string
    showBack?: boolean
    showHome?: boolean
    bg?: string
  }>(),
  {
    title: '',
    showBack: false,
    showHome: false,
    bg: 'linear-gradient(160deg,#fbf7ee,#f1e9d8)',
  },
)

const statusBarHeight = uni.getSystemInfoSync().statusBarHeight || 20
const navHeight = 44

function onBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) uni.navigateBack()
  else uni.switchTab({ url: '/pages/index/index' })
}
function onHome() {
  uni.switchTab({ url: '/pages/index/index' })
}
</script>

<style scoped>
.navbar {
  width: 100%;
}
.navbar-status {
  width: 100%;
}
.navbar-row {
  display: flex;
  align-items: center;
  position: relative;
  padding: 0 24rpx;
}
.navbar-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  color: var(--txt);
  font-size: 32rpx;
  font-weight: 600;
  max-width: 60%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.navbar-back,
.navbar-home {
  width: 60rpx;
  height: 100%;
  display: flex;
  align-items: center;
}
.navbar-back-icon,
.navbar-home-icon {
  color: #b08a3a;
  font-size: 48rpx;
  line-height: 1;
}
</style>
