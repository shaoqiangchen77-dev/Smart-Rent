<template>
  <view class="my-page">
    <!-- 头部：渐变 + 头像 + 昵称 + 数据 -->
    <view class="my-head" :style="{ paddingTop: (statusBar + 16) + 'px' }">
      <view class="u" @click="goEdit">
        <view class="av avatar-grad">
          <image v-if="avatarSrc && showAvatarImg" :key="avatarKey" :src="avatarSrc" class="av-img" mode="aspectFill" @error="showAvatarImg = false" />
          <text v-else>{{ initial }}</text>
        </view>
        <view class="nm">
          {{ nickname }}
          <text class="sm">{{ roleText }} · 实名已认证</text>
        </view>
        <SrIcon class="edit-ic" name="chev" :size="30" color="#9a9183" />
      </view>
      <view class="my-stats">
        <view class="s"><text class="mono stat-num">{{ stats.collect }}</text><text class="sm">收藏</text></view>
        <view class="s"><text class="mono stat-num">{{ stats.browse }}</text><text class="sm">浏览</text></view>
        <view class="s"><text class="mono stat-num">{{ stats.appoint }}</text><text class="sm">预约</text></view>
        <view class="s"><text class="mono stat-num">{{ stats.contract }}</text><text class="sm">合同</text></view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view v-if="userStore.isLandlord" class="menu-list">
        <view class="mi spotlight" @click="goPage('/pages/landlord/dashboard')">
          <SrIcon class="mi-ic" name="building" :size="40" color="#b08a3a" />
          <text class="mi-text">房东工作台</text>
          <SrIcon class="mi-arrow" name="chev" :size="34" color="#9a9183" />
        </view>
      </view>

      <view class="menu-list">
        <view class="mi spotlight" @click="goPage('/pages/my/collection')">
          <SrIcon class="mi-ic" name="heart" :size="40" color="#b08a3a" />
          <text class="mi-text">我的收藏</text>
          <text class="mi-rt">{{ stats.collect }} ›</text>
        </view>
        <view class="mi spotlight" @click="goPage('/pages/my/history')">
          <SrIcon class="mi-ic" name="cal" :size="40" color="#b08a3a" />
          <text class="mi-text">浏览历史</text>
          <text class="mi-rt">{{ stats.browse }} ›</text>
        </view>
        <view class="mi spotlight" @click="goPage('/pages/landlord/appointments')">
          <SrIcon class="mi-ic" name="appt" :size="40" color="#b08a3a" />
          <text class="mi-text">我的预约</text>
          <text class="mi-rt">{{ stats.appoint }} ›</text>
        </view>
      </view>

      <view class="menu-list">
        <view class="mi spotlight" @click="goPage('/pages/my/contract')">
          <SrIcon class="mi-ic" name="doc" :size="40" color="#b08a3a" />
          <text class="mi-text">我的合同</text>
          <text class="mi-rt">{{ stats.contract }} ›</text>
        </view>
        <view class="mi spotlight" @click="goPage('/pages/my/bill')">
          <SrIcon class="mi-ic" name="bill" :size="40" color="#b08a3a" />
          <text class="mi-text">账单中心</text>
          <text class="mi-rt">›</text>
        </view>
        <view class="mi spotlight" @click="goPage('/pages/my/preference')">
          <SrIcon class="mi-ic" name="gear" :size="40" color="#b08a3a" />
          <text class="mi-text">租房偏好</text>
          <text class="mi-rt">›</text>
        </view>
      </view>

      <view v-if="userStore.isLoggedIn" class="menu-list">
        <view class="mi spotlight logout" @click="onLogout">
          <SrIcon class="mi-ic" name="user" :size="40" color="#c75d5d" />
          <text class="mi-text logout-text">退出登录</text>
        </view>
      </view>
      <view v-else class="menu-list">
        <view class="mi spotlight" @click="goLogin">
          <SrIcon class="mi-ic" name="user" :size="40" color="#b08a3a" />
          <text class="mi-text">点击登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import SrIcon from '@/components/SrIcon.vue'
import { useLoginGuard } from '@/composables/authGuard'

useLoginGuard()

const statusBar = uni.getSystemInfoSync().statusBarHeight || 20
const userStore = useUserStore()
const defaultAvatar = '/static/default-avatar.png'

// 原型演示数据（毕业设计 UI 对齐用）
const stats = { collect: 6, browse: 23, appoint: 2, contract: 1 }

const nickname = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '租客')
const initial = computed(() => (nickname.value || '客').slice(0, 1))
const roleText = computed(() => (userStore.userInfo?.role === 1 ? '房东' : '租客'))
const avatarSrc = computed(() => {
  const a = userStore.userInfo?.avatar
  return a && a !== '' ? a : ''
})

// 控制头像显示：加载失败时回退到文字 initial
const showAvatarImg = ref(true)
// 每次显示页面强制刷新 <image>，跳过微信图片缓存（之前 403 可能被缓存）
const avatarKey = ref(Date.now())

async function refreshUser() {
  avatarKey.value = Date.now()
  showAvatarImg.value = true
  if (userStore.isLoggedIn) {
    await userStore.fetchUserInfo()
  }
}

onMounted(refreshUser)
onShow(refreshUser)

function goLogin() {
  uni.navigateTo({ url: '/pages/login/login' })
}
function goPage(url: string) {
  if (!userStore.isLoggedIn) {
    goLogin()
    return
  }
  uni.navigateTo({ url })
}
function goEdit() {
  if (!userStore.isLoggedIn) {
    goLogin()
    return
  }
  uni.navigateTo({ url: '/pages/user/edit' })
}
function onLogout() {
  userStore.logout()
  uni.showToast({ title: '已退出登录', icon: 'success' })
}
</script>

<style scoped>
.my-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40rpx;
}
.my-head {
  background: radial-gradient(120% 90% at 82% 0%, #f1e7cd, transparent 60%),
    linear-gradient(160deg, #fbf7ee, #f1e9d8);
  padding: 40rpx 30rpx;
}
.my-head .u {
  display: flex;
  align-items: center;
  gap: 26rpx;
}
.av {
  width: 112rpx;
  height: 112rpx;
  border-radius: 34rpx;
}
.av-img {
  width: 100%;
  height: 100%;
  border-radius: inherit;
}
.my-head .nm {
  font-size: 36rpx;
  font-weight: 700;
  color: var(--txt);
}
.edit-ic {
  flex: 0 0 auto;
  transform: rotate(180deg);
  margin-left: 4rpx;
  opacity: 0.75;
}
.my-head .nm .sm {
  display: block;
  color: var(--txt-3);
  font-size: 24rpx;
  margin-top: 8rpx;
  font-weight: 400;
}
.my-stats {
  display: flex;
  margin-top: 40rpx;
  background: var(--glass);
  border: 1rpx solid var(--line);
  border-radius: 28rpx;
  overflow: hidden;
}
.my-stats .s {
  flex: 1;
  text-align: center;
  padding: 26rpx 0;
}
.my-stats .s + .s {
  border-left: 1rpx solid var(--line);
}
.my-stats .s .stat-num {
  font-size: 36rpx;
  display: block;
  color: var(--txt);
  font-weight: 800;
}
.my-stats .s .sm {
  font-size: 22rpx;
  color: var(--txt-3);
}

.menu-section {
  padding: 28rpx 30rpx 0;
}
.menu-list {
  background: var(--glass);
  border: 1rpx solid var(--line);
  border-radius: 32rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  box-shadow: var(--shadow);
}
.mi {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 30rpx 28rpx;
  border-bottom: 1rpx solid var(--line);
}
.mi:last-child {
  border-bottom: none;
}
.mi:active {
  background: var(--glass-2);
}
.mi-ic {
  flex: 0 0 auto;
}
.mi-text {
  flex: 1;
  font-size: 28rpx;
  color: var(--txt);
  font-weight: 500;
}
.mi-rt {
  color: var(--txt-3);
  font-size: 26rpx;
}
.mi-arrow {
  flex: 0 0 auto;
  transform: rotate(180deg);
}
.logout-text {
  color: #c75d5d;
}
</style>
