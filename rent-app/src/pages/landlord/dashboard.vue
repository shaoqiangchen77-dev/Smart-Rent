<template>
  <view class="page">
    <view class="top">
      <view>
        <text class="eyebrow">房东工作台</text>
        <text class="title">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '你好' }}</text>
      </view>
      <button class="publish-btn" @click="goPublish">发布</button>
    </view>

    <view class="metrics">
      <view class="metric">
        <text class="metric-num">{{ houses.length }}</text>
        <text class="metric-label">房源</text>
      </view>
      <view class="metric">
        <text class="metric-num">{{ onlineCount }}</text>
        <text class="metric-label">已上架</text>
      </view>
      <view class="metric">
        <text class="metric-num">{{ pendingAppointments }}</text>
        <text class="metric-label">待确认</text>
      </view>
    </view>

    <view class="section-head">
      <text>我的房源</text>
      <text class="link" @click="loadData">刷新</text>
    </view>

    <view v-if="houses.length" class="house-list">
      <view v-for="house in houses" :key="house.id" class="house-card">
        <image class="cover" :src="house.images?.[0] || '/static/default-house.png'" mode="aspectFill" />
        <view class="house-body">
          <view class="house-line">
            <text class="house-title">{{ house.title }}</text>
            <text class="status" :class="statusClass(house.status)">{{ statusText(house.status) }}</text>
          </view>
          <text class="meta">{{ house.area }} · {{ house.houseType }} · {{ house.areaSize || '-' }}m²</text>
          <view class="bottom">
            <text class="price">¥{{ house.price }}/月</text>
            <view class="actions">
              <text v-if="house.status !== 1" @click="publish(house.id)">上架</text>
              <text v-if="house.status === 1" @click="offline(house.id)">下架</text>
              <text class="danger" @click="remove(house.id)">删除</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-else class="empty">
      <text class="empty-title">还没有房源</text>
      <text class="empty-desc">先发布一套房源，审核通过后就能被租客看到。</text>
      <button class="empty-btn" @click="goPublish">发布第一套房</button>
    </view>

    <view class="appointment-entry" @click="goAppointments">
      <view>
        <text class="entry-title">看房预约</text>
        <text class="entry-desc">确认时间、完成带看、沉淀成交线索</text>
      </view>
      <text class="entry-arrow">></text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { deleteHouse, getHouseList, offlineHouse, publishHouse, type HouseItem } from '@/api/house'
import { getLandlordAppointments } from '@/api/appointment'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const houses = ref<HouseItem[]>([])
const appointments = ref<any[]>([])
const loading = ref(false)

const onlineCount = computed(() => houses.value.filter((item) => item.status === 1).length)
const pendingAppointments = computed(() => appointments.value.filter((item) => item.status === 0).length)

async function loadData() {
  if (loading.value) return
  loading.value = true
  try {
    if (!userStore.userInfo) await userStore.fetchUserInfo()
    const landlordId = userStore.userInfo?.id
    if (!landlordId) return
    const [houseRes, appointmentRes] = await Promise.all([
      getHouseList({ page: 1, size: 50, landlordId }),
      getLandlordAppointments(),
    ])
    houses.value = houseRes.data.records
    appointments.value = appointmentRes.data || []
  } finally {
    loading.value = false
  }
}

function statusText(status: number) {
  return status === 1 ? '已上架' : status === 2 ? '已下架' : '待审核'
}

function statusClass(status: number) {
  return status === 1 ? 'online' : status === 2 ? 'offline' : 'pending'
}

async function publish(id: number) {
  await publishHouse(id)
  uni.showToast({ title: '已上架', icon: 'success' })
  loadData()
}

async function offline(id: number) {
  await offlineHouse(id)
  uni.showToast({ title: '已下架', icon: 'success' })
  loadData()
}

function remove(id: number) {
  uni.showModal({
    title: '删除房源',
    content: '删除后租客将无法再看到该房源。',
    success: async (res) => {
      if (!res.confirm) return
      await deleteHouse(id)
      uni.showToast({ title: '已删除', icon: 'success' })
      loadData()
    },
  })
}

function goPublish() {
  uni.navigateTo({ url: '/pages/landlord/publish' })
}

function goAppointments() {
  uni.navigateTo({ url: '/pages/landlord/appointments' })
}

onShow(loadData)
onPullDownRefresh(() => loadData().finally(() => uni.stopPullDownRefresh()))
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 28rpx 28rpx 56rpx;
  background: #f6f4ef;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0 28rpx;
}
.eyebrow {
  display: block;
  font-size: 24rpx;
  color: #6f7d79;
  margin-bottom: 8rpx;
}
.title {
  display: block;
  font-size: 44rpx;
  color: #1c1812;
  font-weight: 800;
}
.publish-btn,
.empty-btn {
  margin: 0;
  height: 72rpx;
  line-height: 72rpx;
  padding: 0 28rpx;
  border-radius: 12rpx;
  background: #b08a3a;
  color: #fff;
  font-size: 28rpx;
}
.metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
  margin-bottom: 30rpx;
}
.metric {
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 16rpx;
  padding: 22rpx;
}
.metric-num {
  display: block;
  color: #1c1812;
  font-size: 42rpx;
  font-weight: 800;
}
.metric-label {
  color: #77827f;
  font-size: 23rpx;
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18rpx;
  color: #1c1812;
  font-size: 30rpx;
  font-weight: 700;
}
.link {
  color: #b08a3a;
  font-size: 24rpx;
  font-weight: 600;
}
.house-card {
  display: flex;
  gap: 18rpx;
  padding: 16rpx;
  margin-bottom: 18rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 16rpx;
}
.cover {
  width: 176rpx;
  height: 148rpx;
  border-radius: 12rpx;
  background: #ece8df;
}
.house-body {
  flex: 1;
  min-width: 0;
}
.house-line,
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12rpx;
}
.house-title {
  flex: 1;
  color: #1f2a2e;
  font-size: 29rpx;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.status {
  padding: 4rpx 12rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
}
.status.online { color: #b08a3a; background: #f4ecd6; }
.status.offline { color: #8a6b38; background: #f5eddd; }
.status.pending { color: #b45309; background: #fff2d7; }
.meta {
  display: block;
  margin: 14rpx 0 20rpx;
  color: #7b8582;
  font-size: 23rpx;
}
.price {
  color: #b0791f;
  font-weight: 800;
  font-size: 28rpx;
}
.actions {
  display: flex;
  gap: 18rpx;
  color: #b08a3a;
  font-size: 24rpx;
  font-weight: 650;
}
.danger {
  color: #c2410c;
}
.empty,
.appointment-entry {
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 16rpx;
}
.empty {
  padding: 42rpx 28rpx;
  text-align: center;
}
.empty-title,
.empty-desc {
  display: block;
}
.empty-title {
  color: #1c1812;
  font-size: 32rpx;
  font-weight: 750;
}
.empty-desc {
  margin: 12rpx 0 24rpx;
  color: #7b8582;
  font-size: 24rpx;
}
.empty-btn {
  display: inline-block;
}
.appointment-entry {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24rpx;
  padding: 28rpx;
}
.entry-title,
.entry-desc {
  display: block;
}
.entry-title {
  color: #1c1812;
  font-size: 30rpx;
  font-weight: 750;
}
.entry-desc {
  margin-top: 8rpx;
  color: #7b8582;
  font-size: 24rpx;
}
.entry-arrow {
  color: #98a19d;
}
</style>
