<template>
  <view class="collection-page">
    <view class="header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">❤️</text>
        <text class="header-title">我的收藏</text>
      </view>
    </view>

    <view class="list-section">
      <view v-for="item in list" :key="item.id" class="collection-card" @click="goDetail(item.houseId)">
        <view class="card-body">
          <view class="icon-wrap">
            <text class="house-icon">🏠</text>
          </view>
          <view class="house-info">
            <text class="house-title">房源 #{{ item.houseId }}</text>
            <text class="collect-time">收藏于 {{ item.createTime }}</text>
          </view>
          <text class="card-arrow">></text>
        </view>
      </view>

      <view v-if="list.length === 0" class="empty">
        <text class="empty-icon">❤️</text>
        <text class="empty-text">暂无收藏</text>
        <text class="empty-hint">浏览房源时点击收藏按钮即可添加</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

interface Collection { id: number; houseId: number; createTime: string }

const list = ref<Collection[]>([])

onMounted(async () => {
  const res = await get<Collection[]>('/collection/my')
  list.value = res.data
})

function goDetail(houseId: number) {
  uni.navigateTo({ url: `/pages/house/detail?id=${houseId}` })
}
</script>

<style scoped>
.collection-page { min-height: 100vh; background: #f6f4ef; }
.header { position: relative; height: 220rpx; }
.header-bg { position: absolute; top: 0; left: 0; right: 0; height: 220rpx; background: linear-gradient(135deg, #8a6a24 0%, #b08a3a 100%); border-radius: 0 0 48rpx 48rpx; }
.header-content { position: relative; z-index: 1; display: flex; align-items: center; justify-content: center; height: 220rpx; gap: 12rpx; }
.header-icon { font-size: 48rpx; }
.header-title { font-size: 36rpx; font-weight: 700; color: #fff; letter-spacing: 2rpx; }
.list-section { padding: 20rpx 24rpx; margin-top: -20rpx; position: relative; z-index: 2; }
.collection-card { background: #fff; border-radius: 24rpx; margin-bottom: 16rpx; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.04); overflow: hidden; transition: transform 0.2s ease; }
.collection-card:active { transform: scale(0.98); }
.card-body { display: flex; align-items: center; padding: 28rpx; }
.icon-wrap { width: 56rpx; height: 56rpx; display: flex; align-items: center; justify-content: center; background: rgba(176,138,58,0.10); border-radius: 16rpx; margin-right: 20rpx; }
.house-icon { font-size: 28rpx; }
.house-info { flex: 1; }
.house-title { font-size: 28rpx; font-weight: 600; color: #1a1a2e; display: block; margin-bottom: 6rpx; }
.collect-time { font-size: 22rpx; color: #bbb; }
.card-arrow { font-size: 24rpx; color: #ccc; }
.empty { text-align: center; padding: 120rpx 0; }
.empty-icon { font-size: 80rpx; display: block; margin-bottom: 20rpx; }
.empty-text { color: #999; font-size: 28rpx; display: block; }
.empty-hint { color: #bbb; font-size: 24rpx; margin-top: 8rpx; }
</style>
