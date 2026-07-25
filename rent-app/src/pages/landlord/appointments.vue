<template>
  <view class="page">
    <view v-if="appointments.length" class="list">
      <view v-for="item in appointments" :key="item.id" class="card">
        <view class="top">
          <text class="title">预约 #{{ item.id }}</text>
          <text class="status" :class="statusClass(item.status)">{{ statusText(item.status) }}</text>
        </view>
        <view class="row">
          <text class="label">看房时间</text>
          <text>{{ item.viewingTime || '-' }}</text>
        </view>
        <view class="row">
          <text class="label">联系电话</text>
          <text>{{ item.contactPhone || '-' }}</text>
        </view>
        <view class="actions" v-if="item.status === 0">
          <button class="ghost" @click="cancel(item.id)">婉拒</button>
          <button class="primary" @click="confirm(item.id)">确认</button>
        </view>
        <view class="actions" v-else-if="item.status === 1">
          <button class="primary" @click="complete(item.id)">完成带看</button>
        </view>
      </view>
    </view>
    <view v-else class="empty">
      <text class="empty-title">暂无预约</text>
      <text class="empty-desc">有租客提交看房申请后，会出现在这里。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { cancelAppointment, completeAppointment, confirmAppointment, getLandlordAppointments } from '@/api/appointment'

const appointments = ref<any[]>([])

async function loadData() {
  const res = await getLandlordAppointments()
  appointments.value = res.data || []
}

function statusText(status: number) {
  const map: Record<number, string> = {
    0: '待确认',
    1: '已确认',
    2: '已取消',
    3: '已完成',
  }
  return map[status] || '未知'
}

function statusClass(status: number) {
  return status === 1 ? 'confirmed' : status === 2 ? 'canceled' : status === 3 ? 'done' : 'pending'
}

async function confirm(id: number) {
  await confirmAppointment(id)
  uni.showToast({ title: '已确认', icon: 'success' })
  loadData()
}

function cancel(id: number) {
  uni.showModal({
    title: '婉拒预约',
    content: '确认后会通知租客重新选择时间。',
    success: async (res) => {
      if (!res.confirm) return
      await cancelAppointment(id, '当前时间不便看房')
      uni.showToast({ title: '已处理', icon: 'success' })
      loadData()
    },
  })
}

async function complete(id: number) {
  await completeAppointment(id)
  uni.showToast({ title: '已完成', icon: 'success' })
  loadData()
}

onShow(loadData)
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 24rpx;
  background: #f6f4ef;
}
.card {
  margin-bottom: 18rpx;
  padding: 26rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 16rpx;
}
.top,
.row,
.actions {
  display: flex;
  align-items: center;
}
.top {
  justify-content: space-between;
  margin-bottom: 20rpx;
}
.title {
  color: #1c1812;
  font-size: 31rpx;
  font-weight: 780;
}
.status {
  padding: 5rpx 14rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
}
.status.pending { color: #b45309; background: #fff2d7; }
.status.confirmed { color: #b08a3a; background: #f4ecd6; }
.status.canceled { color: #7b8582; background: #eee9df; }
.status.done { color: #315c8a; background: #e7f0f7; }
.row {
  justify-content: space-between;
  padding: 10rpx 0;
  color: #1f2a2e;
  font-size: 26rpx;
}
.label {
  color: #7b8582;
}
.actions {
  gap: 16rpx;
  margin-top: 22rpx;
}
button {
  flex: 1;
  height: 76rpx;
  line-height: 76rpx;
  border-radius: 12rpx;
  font-size: 27rpx;
}
.primary {
  background: #b08a3a;
  color: #fff;
}
.ghost {
  background: #f8f6f1;
  color: #8a6b38;
}
.empty {
  margin-top: 120rpx;
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
  margin-top: 12rpx;
  color: #7b8582;
  font-size: 25rpx;
}
</style>
