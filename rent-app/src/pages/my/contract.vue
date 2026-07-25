<template>
  <view class="contract-page">
    <view class="header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">📄</text>
        <text class="header-title">我的合同</text>
      </view>
    </view>

    <view class="contract-list">
      <view v-for="item in list" :key="item.id" class="contract-card" @click="goDetail(item.id)">
        <view class="card-header">
          <view class="contract-no-row">
            <text class="contract-label">合同编号</text>
            <text class="contract-no">{{ item.contractNo }}</text>
          </view>
          <text :class="['status-badge', 'status-' + item.status]">{{ statusMap[item.status] }}</text>
        </view>
        <view class="card-body">
          <view class="info-row">
            <text class="info-label">月租金</text>
            <text class="info-value price">
              <text class="price-s">¥</text>{{ item.monthlyRent }}
            </text>
          </view>
          <view class="info-row">
            <text class="info-label">押金</text>
            <text class="info-value">{{ item.deposit }}元</text>
          </view>
          <view class="info-row">
            <text class="info-label">租期</text>
            <text class="info-value">{{ item.startDate }} 至 {{ item.endDate }}</text>
          </view>
        </view>
        <view class="card-footer">
          <text class="detail-hint">查看详情</text>
          <text class="detail-arrow">></text>
        </view>
      </view>

      <view v-if="list.length === 0" class="empty">
        <text class="empty-icon">📄</text>
        <text class="empty-text">暂无合同</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

interface Contract {
  id: number; contractNo: string; monthlyRent: number; deposit: number;
  startDate: string; endDate: string; status: number
}

const list = ref<Contract[]>([])
const statusMap: Record<number, string> = { 0: '待生效', 1: '生效中', 2: '已到期', 3: '已终止' }

onMounted(async () => {
  const res = await get<Contract[]>('/contract/my')
  list.value = res.data
})

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/my/contract-detail?id=${id}` })
}
</script>

<style scoped>
.contract-page { min-height: 100vh; background: #f6f4ef; }
.header { position: relative; height: 220rpx; }
.header-bg { position: absolute; top: 0; left: 0; right: 0; height: 220rpx; background: linear-gradient(135deg, #8a6a24 0%, #b08a3a 100%); border-radius: 0 0 48rpx 48rpx; }
.header-content { position: relative; z-index: 1; display: flex; align-items: center; justify-content: center; height: 220rpx; gap: 12rpx; }
.header-icon { font-size: 48rpx; }
.header-title { font-size: 36rpx; font-weight: 700; color: #fff; letter-spacing: 2rpx; }
.contract-list { padding: 20rpx 24rpx; margin-top: -20rpx; position: relative; z-index: 2; }
.contract-card { background: #fff; border-radius: 24rpx; margin-bottom: 24rpx; overflow: hidden; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.04); transition: transform 0.2s ease; }
.contract-card:active { transform: scale(0.98); }
.card-header { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 28rpx; border-bottom: 1rpx solid #f5f5f5; }
.contract-label { font-size: 20rpx; color: #bbb; margin-bottom: 4rpx; }
.contract-no { font-size: 28rpx; font-weight: 600; color: #1a1a2e; font-family: monospace; }
.status-badge { font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 16rpx; font-weight: 500; }
.status-0 { background: rgba(230,162,60,0.1); color: #e6a23c; }
.status-1 { background: rgba(103,194,58,0.1); color: #67c23a; }
.status-2 { background: rgba(153,153,153,0.1); color: #999; }
.status-3 { background: rgba(245,108,108,0.1); color: #f56c6c; }
.card-body { padding: 20rpx 28rpx; }
.info-row { display: flex; justify-content: space-between; align-items: center; padding: 10rpx 0; }
.info-label { font-size: 24rpx; color: #bbb; }
.info-value { font-size: 26rpx; color: #333; }
.info-value.price { font-size: 32rpx; font-weight: 800; color: #ff6b35; font-family: 'DIN Alternate', 'Helvetica Neue', sans-serif; }
.price-s { font-size: 22rpx; font-weight: 600; }
.card-footer { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 28rpx; background: #f8f9fa; }
.detail-hint { font-size: 24rpx; color: #b08a3a; font-weight: 500; }
.detail-arrow { font-size: 24rpx; color: #b08a3a; }
.empty { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
.empty-text { color: #999; font-size: 28rpx; }
</style>
