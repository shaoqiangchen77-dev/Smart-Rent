<template>
  <view class="message-page">
    <view class="top">
      <view>
        <text class="eyebrow">通知收件箱</text>
        <text class="title">消息中心</text>
      </view>
      <view class="unread-pill">
        <text>{{ unreadCount }}</text>
        <text>未读</text>
      </view>
    </view>

    <view class="tabs">
      <text
        v-for="item in tabs"
        :key="item.value"
        class="tab"
        :class="{ active: currentType === item.value }"
        @click="currentType = item.value"
      >
        {{ item.label }}
      </text>
    </view>

    <view class="message-list">
      <view
        v-for="msg in filteredMessages"
        :key="msg.id"
        class="message-card"
        :class="{ unread: !msg.isRead }"
      >
        <view class="type-mark" :class="msg.msgType">
          <text>{{ typeShort[msg.msgType] || '通' }}</text>
        </view>
        <view class="message-body">
          <view class="message-head">
            <text class="msg-title">{{ msg.title || typeMap[msg.msgType] || '消息通知' }}</text>
            <text class="msg-time">{{ msg.createTime }}</text>
          </view>
          <text class="msg-content">{{ msg.content }}</text>
          <view class="msg-foot">
            <text>{{ typeMap[msg.msgType] || msg.msgType }}</text>
            <text v-if="!msg.isRead" class="dot-text">新消息</text>
          </view>
        </view>
      </view>

      <view v-if="filteredMessages.length === 0" class="empty">
        <view class="bubble"></view>
        <text class="empty-title">暂无消息</text>
        <text class="empty-desc">系统通知、预约提醒和账单消息会在这里显示。</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { get } from '@/utils/request'
import { mockMessages } from '@/utils/mock-data'

interface Message {
  id: number
  msgType: string
  title: string | null
  content: string
  isRead: number
  createTime: string
}

const messages = ref<Message[]>(mockMessages)
const currentType = ref('all')
const tabs = [
  { label: '全部', value: 'all' },
  { label: '预约', value: 'appointment' },
  { label: '账单', value: 'bill' },
  { label: '系统', value: 'system' },
]

const typeMap: Record<string, string> = {
  system: '系统通知',
  appointment: '预约提醒',
  contract: '合同通知',
  bill: '账单提醒',
}

const typeShort: Record<string, string> = {
  system: '系',
  appointment: '约',
  contract: '合',
  bill: '账',
}

const filteredMessages = computed(() => {
  if (currentType.value === 'all') return messages.value
  return messages.value.filter((msg) => msg.msgType === currentType.value)
})

const unreadCount = computed(() => messages.value.filter((msg) => !msg.isRead).length)

onMounted(async () => {
  try {
    const res = await get<Message[]>('/message/list', { page: 1, size: 50 })
    messages.value = res.data?.length ? res.data : mockMessages
  } catch {
    messages.value = mockMessages
  }
})
</script>

<style scoped>
.message-page {
  min-height: 100vh;
  padding: 28rpx 24rpx 120rpx;
  background: #f6f4ef;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx;
  background: #1c1812;
  border-radius: 20rpx;
}
.eyebrow,
.title {
  display: block;
}
.eyebrow {
  color: #cdbb91;
  font-size: 24rpx;
  font-weight: 700;
}
.title {
  margin-top: 8rpx;
  color: #fffaf0;
  font-size: 42rpx;
  font-weight: 820;
}
.unread-pill {
  min-width: 112rpx;
  height: 112rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 18rpx;
  background: rgba(255,255,255,0.1);
  color: #fffaf0;
}
.unread-pill text:first-child {
  font-size: 36rpx;
  font-weight: 820;
}
.unread-pill text:last-child {
  margin-top: 2rpx;
  font-size: 22rpx;
  color: #e8dcc0;
}
.tabs {
  display: flex;
  gap: 12rpx;
  padding: 22rpx 4rpx 10rpx;
}
.tab {
  padding: 12rpx 24rpx;
  border-radius: 999rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  color: #53615d;
  font-size: 24rpx;
}
.tab.active {
  background: #b08a3a;
  border-color: #b08a3a;
  color: #fff;
}
.message-list {
  margin-top: 12rpx;
}
.message-card {
  position: relative;
  display: flex;
  gap: 18rpx;
  padding: 22rpx;
  margin-bottom: 18rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 16rpx;
}
.message-card.unread {
  border-color: #e4d2a8;
  box-shadow: 0 12rpx 28rpx rgba(176,138,58,0.08);
}
.type-mark {
  width: 64rpx;
  height: 64rpx;
  flex: 0 0 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14rpx;
  background: #f4ecd6;
  color: #b08a3a;
  font-size: 26rpx;
  font-weight: 800;
}
.type-mark.bill {
  background: #f5eddd;
  color: #8a6b38;
}
.type-mark.appointment {
  background: #fff2e6;
  color: #c2410c;
}
.message-body {
  flex: 1;
  min-width: 0;
}
.message-head {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  align-items: center;
}
.msg-title {
  flex: 1;
  color: #1f2a2e;
  font-size: 29rpx;
  font-weight: 760;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.msg-time {
  color: #9aa29f;
  font-size: 21rpx;
}
.msg-content {
  display: block;
  margin-top: 12rpx;
  color: #64706d;
  font-size: 25rpx;
  line-height: 1.55;
}
.msg-foot {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
  color: #7b8582;
  font-size: 22rpx;
}
.dot-text {
  color: #b0791f;
  font-weight: 700;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 40rpx;
}
.bubble {
  width: 92rpx;
  height: 68rpx;
  border: 5rpx solid #9aa29f;
  border-radius: 34rpx;
  position: relative;
  margin-bottom: 28rpx;
}
.bubble::after {
  content: "";
  position: absolute;
  left: 22rpx;
  bottom: -16rpx;
  width: 24rpx;
  height: 24rpx;
  border-left: 5rpx solid #9aa29f;
  border-bottom: 5rpx solid #9aa29f;
  transform: rotate(-20deg);
  background: #f6f4ef;
}
.empty-title {
  color: #1c1812;
  font-size: 31rpx;
  font-weight: 760;
}
.empty-desc {
  max-width: 480rpx;
  margin-top: 12rpx;
  color: #7b8582;
  font-size: 24rpx;
  line-height: 1.6;
  text-align: center;
}
</style>
