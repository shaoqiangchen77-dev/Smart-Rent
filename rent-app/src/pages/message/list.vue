<template>
  <view class="message-page">
    <view class="sub-head" :style="{ paddingTop: (statusBar + 6) + 'px' }">
      <view class="back" @click="goBack">
        <SrIcon name="chev-left" :size="34" color="#b08a3a" />
      </view>
      <text class="tt">消息中心</text>
      <text class="read-link" @click="markAllRead">已读</text>
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
        class="li"
        :class="{ unread: !msg.isRead }"
        @click="onMsgClick(msg)"
      >
        <view class="av soft">
          <SrIcon :name="iconFor(msg.msgType)" :size="34" color="#b08a3a" />
        </view>
        <view class="c">
          <view class="t">{{ msg.title || typeMap[msg.msgType] || '消息通知' }}</view>
          <view class="s">{{ msg.content }}</view>
        </view>
        <view class="rt">{{ msg.createTime }}</view>
        <text v-if="!msg.isRead" class="badge">未读</text>
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
import { get, post } from '@/utils/request'
import { mockMessages } from '@/utils/mock-data'
import SrIcon from '@/components/SrIcon.vue'
import { useLoginGuard } from '@/composables/authGuard'

useLoginGuard()

const statusBar = uni.getSystemInfoSync().statusBarHeight || 20

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

// 消息类型对应的业务页面；无对应页面则弹窗展示详情
const routeMap: Record<string, string> = {
  appointment: '/pages/landlord/appointments',
  bill: '/pages/my/bill',
  contract: '/pages/my/contract',
}

function iconFor(type: string): string {
  const map: Record<string, string> = {
    system: 'gear',
    appointment: 'appt',
    contract: 'doc',
    bill: 'bill',
  }
  return map[type] || 'msg'
}

const filteredMessages = computed(() => {
  if (currentType.value === 'all') return messages.value
  return messages.value.filter((msg) => msg.msgType === currentType.value)
})

const unreadCount = computed(() => messages.value.filter((msg) => !msg.isRead).length)

async function markAllRead() {
  try {
    await post('/message/read-all')
    messages.value.forEach((m) => { m.isRead = 1 })
  } catch {
    // 失败仍做本地降级，保证 UI 反馈
    messages.value.forEach((m) => { m.isRead = 1 })
  }
}

async function onMsgClick(msg: Message) {
  if (!msg.isRead) {
    try {
      await post(`/message/read/${msg.id}`)
      msg.isRead = 1
    } catch {
      // 后端失败仍本地标记已读，避免阻塞交互
      msg.isRead = 1
    }
  }
  const url = routeMap[msg.msgType]
  if (url) {
    uni.navigateTo({ url })
  } else {
    uni.showModal({
      title: msg.title || typeMap[msg.msgType] || '消息通知',
      content: msg.content,
      showCancel: false,
    })
  }
}

function goBack() {
  uni.switchTab({ url: '/pages/index/index' })
}

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
  padding-bottom: 120rpx;
  background: var(--bg);
}

/* 子页头部（对齐原型 .sub-head） */
.sub-head {
  position: sticky;
  top: 0;
  z-index: 20;
  height: 92rpx;
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 0 20rpx;
  background: var(--glass-solid);
  border-bottom: 1rpx solid var(--line);
}
.sub-head .back {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: rotate(180deg);
}
.sub-head .tt {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 700;
  color: #1c1812;
}
.sub-head .read-link {
  color: var(--gold-2);
  font-size: 26rpx;
  font-weight: 600;
}

.tabs {
  display: flex;
  gap: 12rpx;
  padding: 22rpx 24rpx 10rpx;
}
.tab {
  padding: 12rpx 24rpx;
  border-radius: 999rpx;
  background: var(--glass);
  border: 1rpx solid var(--line);
  color: var(--txt-2);
  font-size: 24rpx;
}
.tab.active {
  background: var(--gold-2);
  border-color: var(--gold-2);
  color: #fff;
}

.message-list {
  padding: 12rpx 24rpx 0;
}
.li {
  position: relative;
  background: var(--glass);
  border: 1rpx solid var(--line);
  border-radius: 30rpx;
  padding: 26rpx;
  display: flex;
  gap: 22rpx;
  align-items: center;
  box-shadow: var(--shadow);
  margin-bottom: 18rpx;
}
.li.unread {
  border-color: var(--line-2);
  box-shadow: 0 12rpx 28rpx rgba(176, 138, 58, 0.1);
}
.li .av {
  width: 86rpx;
  height: 86rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 86rpx;
}
.li .c {
  flex: 1;
  min-width: 0;
}
.li .c .t {
  font-size: 28rpx;
  font-weight: 600;
  color: #241f18;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.li .c .s {
  font-size: 23rpx;
  color: var(--txt-3);
  margin-top: 6rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.li .rt {
  text-align: right;
  font-size: 22rpx;
  color: var(--txt-3);
  font-weight: 400;
  flex: 0 0 auto;
}
.li .badge {
  position: absolute;
  top: 22rpx;
  right: 24rpx;
  background: var(--bad);
  color: #fff;
  font-size: 18rpx;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  line-height: 1.4;
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
  border: 5rpx solid var(--txt-3);
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
  border-left: 5rpx solid var(--txt-3);
  border-bottom: 5rpx solid var(--txt-3);
  transform: rotate(-20deg);
  background: var(--bg);
}
.empty-title {
  color: #1c1812;
  font-size: 31rpx;
  font-weight: 760;
}
.empty-desc {
  max-width: 480rpx;
  margin-top: 12rpx;
  color: var(--txt-2);
  font-size: 24rpx;
  line-height: 1.6;
  text-align: center;
}
</style>
