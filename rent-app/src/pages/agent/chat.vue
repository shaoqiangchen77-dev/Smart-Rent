<template>
  <view class="agent-page">
    <view class="hero">
      <view>
        <text class="kicker">SmartRent Agent</text>
        <text class="title">智能找房助手</text>
        <text class="subtitle">告诉我预算、通勤和居住偏好，我帮你整理找房思路。</text>
      </view>
      <view class="hero-side">
        <view class="agent-mark">AI</view>
        <text class="home-link" @click="goHome">首页</text>
      </view>
    </view>

    <view class="suggestions">
      <text
        v-for="item in suggestions"
        :key="item"
        class="suggestion"
        @click="sendQuick(item)"
      >
        {{ item }}
      </text>
    </view>

    <scroll-view class="chat-list" scroll-y :scroll-into-view="lastMessageId">
      <view
        v-for="(msg, index) in messages"
        :key="msg.id"
        :id="`msg-${index}`"
        class="message"
        :class="msg.role"
      >
        <view class="avatar">{{ msg.role === 'user' ? '我' : '助' }}</view>
        <view class="bubble">
          <text>{{ msg.content }}</text>
        </view>
      </view>
      <view v-if="loading" id="msg-loading" class="message assistant">
        <view class="avatar">助</view>
        <view class="bubble typing">
          <text>正在分析你的需求...</text>
        </view>
      </view>
    </scroll-view>

    <view class="input-bar">
      <input
        v-model="input"
        class="input"
        placeholder="例如：预算6000，想住海淀，通勤中关村"
        confirm-type="send"
        @confirm="sendMessage"
      />
      <button class="send-btn" :disabled="loading" @click="sendMessage">发送</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { chatWithAgent } from '@/api/agent'
import { mockHouses } from '@/utils/mock-data'

interface ChatMessage {
  id: number
  role: 'user' | 'assistant'
  content: string
}

const input = ref('')
const loading = ref(false)
const sessionId = ref('')
const messages = ref<ChatMessage[]>([
  {
    id: Date.now(),
    role: 'assistant',
    content: '你好，我是智能找房助手。你可以告诉我预算、区域、通勤地点、户型和入住时间，我会帮你梳理合适的选择。',
  },
])

const suggestions = ['6000以内海淀两居', '通勤中关村怎么选', '合租要注意什么', '帮我推荐近地铁房源']
const lastMessageId = computed(() => loading.value ? 'msg-loading' : `msg-${messages.value.length - 1}`)

onMounted(hideNativeTabBar)
onShow(hideNativeTabBar)

function hideNativeTabBar() {
  uni.hideTabBar({ animation: false })
}

function goHome() {
  uni.showTabBar({ animation: false })
  uni.switchTab({ url: '/pages/index/index' })
}

async function sendQuick(text: string) {
  input.value = text
  await sendMessage()
}

async function sendMessage() {
  const text = input.value.trim()
  if (!text || loading.value) return
  messages.value.push({ id: Date.now(), role: 'user', content: text })
  input.value = ''
  loading.value = true

  try {
    const res = await chatWithAgent({ message: text, sessionId: sessionId.value })
    sessionId.value = res.data.sessionId || sessionId.value
    messages.value.push({ id: Date.now() + 1, role: 'assistant', content: res.data.reply })
  } catch {
    messages.value.push({ id: Date.now() + 1, role: 'assistant', content: buildMockReply(text) })
  } finally {
    loading.value = false
  }
}

function buildMockReply(text: string) {
  const matched = mockHouses.filter((house) => {
    return [house.title, house.area, house.houseType, house.rentType, ...(house.tags || [])]
      .some((item) => text.includes(String(item)))
  })
  const houses = matched.length ? matched : mockHouses.slice(0, 2)
  const names = houses.map((house) => `「${house.title}」¥${house.price}/月`).join('、')
  return `我先按你的需求给一个预览建议：可以重点看 ${names}。建议再确认三个点：通勤时间是否接受、押金和付款周期、看房时检查采光和噪音。`
}
</script>

<style scoped>
.agent-page {
  height: 100vh;
  padding: 24rpx 24rpx 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f6f4ef;
}
.hero {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding: 30rpx;
  border-radius: 20rpx;
  background: #1c1812;
  color: #fffaf0;
}
.kicker,
.title,
.subtitle {
  display: block;
}
.kicker {
  color: #cdbb91;
  font-size: 23rpx;
  font-weight: 760;
}
.title {
  margin-top: 10rpx;
  font-size: 42rpx;
  font-weight: 850;
}
.subtitle {
  max-width: 500rpx;
  margin-top: 12rpx;
  color: #e8dcc0;
  font-size: 24rpx;
  line-height: 1.55;
}
.hero-side {
  flex: 0 0 100rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}
.agent-mark {
  width: 92rpx;
  height: 92rpx;
  flex: 0 0 92rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 24rpx;
  background: rgba(255,255,255,0.12);
  color: #d6c088;
  font-size: 30rpx;
  font-weight: 900;
}
.home-link {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255,255,255,0.12);
  color: #e8dcc0;
  font-size: 22rpx;
  font-weight: 800;
}
.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding: 22rpx 2rpx 10rpx;
}
.suggestion {
  padding: 12rpx 20rpx;
  border-radius: 999rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  color: #53615d;
  font-size: 24rpx;
}
.chat-list {
  flex: 1;
  height: 0;
  min-height: 0;
  padding-top: 16rpx;
}
.message {
  display: flex;
  gap: 14rpx;
  margin-bottom: 22rpx;
}
.message.user {
  flex-direction: row-reverse;
}
.avatar {
  width: 58rpx;
  height: 58rpx;
  flex: 0 0 58rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  background: #f4ecd6;
  color: #b08a3a;
  font-size: 23rpx;
  font-weight: 800;
}
.message.user .avatar {
  background: #1c1812;
  color: #fffaf0;
}
.bubble {
  max-width: 560rpx;
  padding: 18rpx 22rpx;
  border-radius: 18rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  color: #1f2a2e;
  font-size: 27rpx;
  line-height: 1.65;
}
.message.user .bubble {
  background: #b08a3a;
  border-color: #b08a3a;
  color: #fff;
}
.typing {
  color: #7b8582;
}
.input-bar {
  flex-shrink: 0;
  display: flex;
  gap: 14rpx;
  margin: 0 -24rpx;
  padding: 18rpx 24rpx calc(18rpx + env(safe-area-inset-bottom));
  background: rgba(255,255,255,0.96);
  border-top: 1rpx solid #e7e1d6;
  box-shadow: 0 -10rpx 26rpx rgba(31, 42, 39, 0.06);
}
.input {
  flex: 1;
  height: 82rpx;
  padding: 0 22rpx;
  border-radius: 14rpx;
  background: #f7f4ee;
  color: #1f2a2e;
  font-size: 26rpx;
}
.send-btn {
  width: 116rpx;
  height: 82rpx;
  line-height: 82rpx;
  margin: 0;
  border-radius: 14rpx;
  background: #b08a3a;
  color: #fff;
  font-size: 27rpx;
  font-weight: 760;
}
</style>
