<template>
  <view class="detail-page" v-if="detail">
    <view class="header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">📄</text>
        <text class="header-title">合同详情</text>
      </view>
    </view>

    <view class="content-section">
      <view class="info-card">
        <text class="card-label">合同编号</text>
        <text class="card-value contract-no">{{ detail.contractNo }}</text>
        <text :class="['status-badge', 'status-' + detail.status]">{{ statusMap[detail.status] }}</text>
      </view>

      <view class="info-card">
        <text class="section-title">租金信息</text>
        <view class="detail-row">
          <text class="row-label">月租金</text>
          <text class="row-value price"><text class="price-s">¥</text>{{ detail.monthlyRent }}</text>
        </view>
        <view class="detail-row">
          <text class="row-label">押金</text>
          <text class="row-value">{{ detail.deposit }}元</text>
        </view>
        <view class="detail-row">
          <text class="row-label">付款周期</text>
          <text class="row-value">{{ detail.paymentCycle }}</text>
        </view>
        <view class="detail-row">
          <text class="row-label">每月付款日</text>
          <text class="row-value">{{ detail.payDay }}号</text>
        </view>
      </view>

      <view class="info-card">
        <text class="section-title">租期信息</text>
        <view class="detail-row">
          <text class="row-label">起始日期</text>
          <text class="row-value">{{ detail.startDate }}</text>
        </view>
        <view class="detail-row">
          <text class="row-label">结束日期</text>
          <text class="row-value">{{ detail.endDate }}</text>
        </view>
        <view class="detail-row" v-if="detail.signTime">
          <text class="row-label">签约时间</text>
          <text class="row-value">{{ detail.signTime }}</text>
        </view>
      </view>

      <view v-if="detail.status === 0" class="action-area">
        <button class="sign-btn" @click="onSign">签署合同</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get, post } from '@/utils/request'

const detail = ref<any>(null)
const statusMap: Record<number, string> = { 0: '待生效', 1: '生效中', 2: '已到期', 3: '已终止' }

onMounted(async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  const id = page.$page?.options?.id || page.options?.id
  const res = await get(`/contract/${id}`)
  detail.value = res.data
})

async function onSign() {
  await post(`/contract/${detail.value.id}/sign`)
  uni.showToast({ title: '签约成功', icon: 'success' })
  detail.value.status = 1
}
</script>

<style scoped>
.detail-page { min-height: 100vh; background: #f6f4ef; }
.header { position: relative; height: 220rpx; }
.header-bg { position: absolute; top: 0; left: 0; right: 0; height: 220rpx; background: linear-gradient(135deg, #8a6a24 0%, #b08a3a 100%); border-radius: 0 0 48rpx 48rpx; }
.header-content { position: relative; z-index: 1; display: flex; align-items: center; justify-content: center; height: 220rpx; gap: 12rpx; }
.header-icon { font-size: 48rpx; }
.header-title { font-size: 36rpx; font-weight: 700; color: #fff; letter-spacing: 2rpx; }
.content-section { padding: 0 24rpx; margin-top: -20rpx; position: relative; z-index: 2; }
.info-card { background: #fff; border-radius: 24rpx; padding: 28rpx; margin-bottom: 20rpx; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.04); position: relative; }
.card-label { font-size: 20rpx; color: #bbb; display: block; margin-bottom: 8rpx; }
.card-value { font-size: 30rpx; color: #1a1a2e; font-weight: 600; }
.contract-no { font-family: monospace; }
.status-badge { position: absolute; top: 28rpx; right: 28rpx; font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 16rpx; font-weight: 500; }
.status-0 { background: rgba(230,162,60,0.1); color: #e6a23c; }
.status-1 { background: rgba(103,194,58,0.1); color: #67c23a; }
.status-2 { background: rgba(153,153,153,0.1); color: #999; }
.status-3 { background: rgba(245,108,108,0.1); color: #f56c6c; }
.section-title { font-size: 28rpx; font-weight: 700; color: #1a1a2e; margin-bottom: 16rpx; display: block; letter-spacing: 1rpx; }
.detail-row { display: flex; justify-content: space-between; align-items: center; padding: 14rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.detail-row:last-child { border-bottom: none; }
.row-label { font-size: 24rpx; color: #bbb; }
.row-value { font-size: 26rpx; color: #333; }
.row-value.price { font-size: 36rpx; font-weight: 800; color: #ff6b35; font-family: 'DIN Alternate', 'Helvetica Neue', sans-serif; }
.price-s { font-size: 22rpx; font-weight: 600; }
.action-area { margin-top: 40rpx; }
.sign-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: linear-gradient(135deg, #b08a3a 0%, #8a6a24 100%); color: #fff; font-size: 32rpx; font-weight: 700; border-radius: 24rpx; border: none; box-shadow: 0 8rpx 24rpx rgba(176,138,58,0.22); letter-spacing: 4rpx; transition: transform 0.2s ease; }
.sign-btn:active { transform: scale(0.96); }
</style>
