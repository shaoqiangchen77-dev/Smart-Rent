<template>
  <view class="review-page">
    <view class="header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">⭐</text>
        <text class="header-title">评价房源</text>
        <text class="header-subtitle">分享您的真实租房体验</text>
      </view>
    </view>

    <view class="form-section">
      <view class="form-card">
        <view class="card-header">
          <view class="card-icon-wrap"><text class="card-icon">⭐</text></view>
          <text class="card-title">整体评分</text>
        </view>
        <view class="rating-section">
          <view class="rating-row">
            <text
              v-for="i in 5"
              :key="i"
              :class="['star', { active: i <= rating }]"
              @click="rating = i"
            >★</text>
          </view>
          <text class="rating-text">{{ ratingTexts[rating - 1] }}</text>
        </view>
      </view>

      <view class="form-card">
        <view class="card-header">
          <view class="card-icon-wrap"><text class="card-icon">📝</text></view>
          <text class="card-title">评价内容</text>
        </view>
        <view class="textarea-group">
          <textarea
            v-model="content"
            placeholder="分享您的租房体验，如房屋质量、周边环境、房东服务等..."
            class="form-textarea"
            maxlength="500"
          />
          <text class="char-count">{{ content.length }}/500</text>
        </view>
      </view>

      <button class="submit-btn" :loading="loading" @click="onSubmit">
        <text>提交评价</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { post } from '@/utils/request'

const houseId = ref(0)
const rating = ref(5)
const content = ref('')
const loading = ref(false)
const ratingTexts = ['非常差', '较差', '一般', '满意', '非常满意']

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  const opts = page.$page?.options || page.options
  houseId.value = Number(opts.houseId || 0)
})

async function onSubmit() {
  if (rating.value < 1) {
    uni.showToast({ title: '请选择评分', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await post('/review', {
      houseId: houseId.value,
      rating: rating.value,
      content: content.value,
    })
    uni.showToast({ title: '评价成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.review-page { min-height: 100vh; background: #f6f4ef; }
.header { position: relative; height: 280rpx; }
.header-bg { position: absolute; top: 0; left: 0; right: 0; height: 280rpx; background: linear-gradient(135deg, #8a6a24 0%, #b08a3a 100%); border-radius: 0 0 48rpx 48rpx; }
.header-content { position: relative; z-index: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 280rpx; }
.header-icon { font-size: 56rpx; margin-bottom: 12rpx; }
.header-title { font-size: 36rpx; font-weight: 700; color: #fff; letter-spacing: 2rpx; }
.header-subtitle { font-size: 22rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; }
.form-section { padding: 0 24rpx; margin-top: -30rpx; position: relative; z-index: 2; }
.form-card { background: #fff; border-radius: 24rpx; padding: 28rpx; margin-bottom: 20rpx; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.04); }
.card-header { display: flex; align-items: center; margin-bottom: 20rpx; }
.card-icon-wrap { width: 48rpx; height: 48rpx; display: flex; align-items: center; justify-content: center; background: rgba(176,138,58,0.10); border-radius: 14rpx; margin-right: 12rpx; }
.card-icon { font-size: 24rpx; }
.card-title { font-size: 28rpx; font-weight: 600; color: #1a1a2e; }
.rating-section { text-align: center; }
.rating-row { display: flex; justify-content: center; gap: 20rpx; margin-bottom: 12rpx; }
.star { font-size: 56rpx; color: #ddd; transition: all 0.3s ease; }
.star.active { color: #ff9900; transform: scale(1.1); }
.rating-text { font-size: 26rpx; color: #ff9900; font-weight: 600; }
.textarea-group { background: #f5f7fa; border-radius: 16rpx; padding: 20rpx; position: relative; }
.form-textarea { font-size: 28rpx; background: transparent; height: 200rpx; width: 100%; }
.char-count { position: absolute; bottom: 12rpx; right: 16rpx; font-size: 20rpx; color: #ccc; }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: linear-gradient(135deg, #b08a3a 0%, #8a6a24 100%); color: #fff; font-size: 32rpx; font-weight: 700; border-radius: 24rpx; border: none; margin-top: 20rpx; box-shadow: 0 8rpx 24rpx rgba(176,138,58,0.22); letter-spacing: 4rpx; transition: transform 0.2s ease; }
.submit-btn:active { transform: scale(0.96); }
</style>
