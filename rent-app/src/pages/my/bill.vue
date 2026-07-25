<template>
  <view class="bill-page">
    <!-- 顶部装饰 -->
    <view class="header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">💰</text>
        <text class="header-title">我的账单</text>
      </view>
    </view>

    <!-- 状态筛选 -->
    <view class="filter-section">
      <scroll-view scroll-x class="filter-scroll">
        <view
          v-for="tab in tabs"
          :key="tab.value"
          :class="['filter-tag', { active: currentTab === tab.value }]"
          @click="currentTab = tab.value; loadList()"
        >
          <text>{{ tab.label }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 账单列表 -->
    <view class="bill-list">
      <view v-for="item in list" :key="item.id" class="bill-card">
        <view class="bill-header">
          <view class="bill-type-row">
            <view class="type-icon-wrap">
              <text class="bill-type-icon">{{ typeIcon[item.billType] || '📄' }}</text>
            </view>
            <text class="bill-type-name">{{ typeMap[item.billType] || item.billType }}</text>
          </view>
          <text :class="['bill-status', 'status-' + item.status]">{{ statusMap[item.status] }}</text>
        </view>
        <view class="bill-body">
          <text class="bill-amount">
            <text class="amount-symbol">¥</text>{{ item.amount }}
          </text>
        </view>
        <view class="bill-footer">
          <text class="bill-month">账单月份：{{ item.billMonth }}</text>
          <text class="bill-due">截止日：{{ item.dueDate }}</text>
        </view>
        <view v-if="item.status === 0" class="bill-action">
          <button class="pay-btn" @click="onPay(item.id)">立即支付</button>
        </view>
      </view>

      <view v-if="list.length === 0" class="empty">
        <text class="empty-icon">💰</text>
        <text class="empty-text">暂无账单</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get, post } from '@/utils/request'

interface Bill {
  id: number; billType: string; amount: number; billMonth: string;
  dueDate: string; status: number
}

const list = ref<Bill[]>([])
const currentTab = ref<number | undefined>(undefined)
const tabs = [
  { label: '全部', value: undefined },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '已逾期', value: 2 },
]
const statusMap: Record<number, string> = { 0: '待支付', 1: '已支付', 2: '已逾期', 3: '已作废' }
const typeMap: Record<string, string> = { rent: '房租', deposit: '押金', water: '水费', electric: '电费', property: '物业费' }
const typeIcon: Record<string, string> = { rent: '🏠', deposit: '🔑', water: '💧', electric: '⚡', property: '🏢' }

async function loadList() {
  const params: any = {}
  if (currentTab.value !== undefined) params.status = currentTab.value
  const res = await get<Bill[]>('/bill/my', params)
  list.value = res.data
}

async function onPay(id: number) {
  await post(`/bill/${id}/pay`, { payMethod: 'wechat' })
  uni.showToast({ title: '支付成功', icon: 'success' })
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.bill-page {
  min-height: 100vh;
  background: #f6f4ef;
}
.header {
  position: relative;
  height: 220rpx;
}
.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 220rpx;
  background: linear-gradient(135deg, #8a6a24 0%, #b08a3a 100%);
  border-radius: 0 0 48rpx 48rpx;
}
.header-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 220rpx;
  gap: 12rpx;
}
.header-icon {
  font-size: 48rpx;
}
.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2rpx;
}
.filter-section {
  padding: 20rpx 24rpx;
}
.filter-scroll {
  white-space: nowrap;
}
.filter-tag {
  display: inline-block;
  padding: 12rpx 28rpx;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  font-size: 24rpx;
  color: #666;
  margin-right: 14rpx;
  border: 1rpx solid rgba(255,255,255,0.5);
  transition: all 0.3s ease;
}
.filter-tag.active {
  background: linear-gradient(135deg, #b08a3a 0%, #8a6a24 100%);
  color: #fff;
  box-shadow: 0 4rpx 16rpx rgba(176,138,58,0.22);
  border-color: transparent;
  transform: scale(1.05);
}
.bill-list {
  padding: 0 24rpx;
}
.bill-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.04);
  transition: transform 0.2s ease;
}
.bill-card:active {
  transform: scale(0.98);
}
.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.bill-type-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.type-icon-wrap {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(176,138,58,0.10);
  border-radius: 14rpx;
}
.bill-type-icon {
  font-size: 24rpx;
}
.bill-type-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a1a2e;
}
.bill-status {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-weight: 500;
}
.status-0 { background: rgba(230,162,60,0.1); color: #e6a23c; }
.status-1 { background: rgba(103,194,58,0.1); color: #67c23a; }
.status-2 { background: rgba(245,108,108,0.1); color: #f56c6c; }
.status-3 { background: rgba(153,153,153,0.1); color: #999; }
.bill-body {
  margin-bottom: 12rpx;
}
.bill-amount {
  font-size: 48rpx;
  font-weight: 800;
  color: #ff6b35;
  font-family: 'DIN Alternate', 'Helvetica Neue', sans-serif;
}
.amount-symbol {
  font-size: 26rpx;
  font-weight: 600;
}
.bill-footer {
  display: flex;
  gap: 24rpx;
}
.bill-month, .bill-due {
  font-size: 22rpx;
  color: #bbb;
}
.bill-action {
  margin-top: 20rpx;
  text-align: right;
}
.pay-btn {
  display: inline-block;
  padding: 12rpx 36rpx;
  background: linear-gradient(135deg, #b08a3a 0%, #8a6a24 100%);
  color: #fff;
  font-size: 24rpx;
  font-weight: 600;
  border-radius: 24rpx;
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(176,138,58,0.22);
  transition: transform 0.2s ease;
}
.pay-btn:active {
  transform: scale(0.95);
}
.empty {
  text-align: center;
  padding: 120rpx 0;
}
.empty-icon {
  font-size: 80rpx;
  display: block;
  margin-bottom: 20rpx;
}
.empty-text {
  color: #999;
  font-size: 28rpx;
}
</style>
