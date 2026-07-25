<template>
  <view class="preference-page">
    <view class="header">
      <view class="header-bg"></view>
      <view class="header-content">
        <text class="header-icon">⚙️</text>
        <text class="header-title">偏好设置</text>
        <text class="header-subtitle">设置您的租房偏好，获得更精准的推荐</text>
      </view>
    </view>

    <view class="form-section">
      <view class="form-card">
        <view class="card-header">
          <view class="card-icon-wrap"><text class="card-icon">📍</text></view>
          <text class="card-title">偏好区域</text>
        </view>
        <view class="input-group">
          <input v-model="form.preferredArea" placeholder="如：海淀区,朝阳区" class="form-input" />
        </view>
      </view>

      <view class="form-card">
        <view class="card-header">
          <view class="card-icon-wrap"><text class="card-icon">💰</text></view>
          <text class="card-title">预算范围</text>
        </view>
        <view class="budget-row">
          <view class="budget-input-group">
            <input v-model="form.minBudget" placeholder="最低" type="number" class="form-input" />
          </view>
          <text class="budget-sep">—</text>
          <view class="budget-input-group">
            <input v-model="form.maxBudget" placeholder="最高" type="number" class="form-input" />
          </view>
          <text class="budget-unit">元/月</text>
        </view>
      </view>

      <view class="form-card">
        <view class="card-header">
          <view class="card-icon-wrap"><text class="card-icon">🏠</text></view>
          <text class="card-title">偏好户型</text>
        </view>
        <picker :range="typeOptions" @change="onTypeChange">
          <view class="picker-item" :class="{ active: form.preferredType }">
            <text>{{ form.preferredType || '请选择户型' }}</text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>

      <view class="form-card">
        <view class="card-header">
          <view class="card-icon-wrap"><text class="card-icon">🎨</text></view>
          <text class="card-title">偏好装修</text>
        </view>
        <picker :range="decorationOptions" @change="onDecorationChange">
          <view class="picker-item" :class="{ active: form.preferredDecoration }">
            <text>{{ form.preferredDecoration || '请选择装修' }}</text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>

      <view class="form-card">
        <view class="card-header">
          <view class="card-icon-wrap"><text class="card-icon">🚇</text></view>
          <text class="card-title">最大地铁距离</text>
        </view>
        <view class="input-group">
          <input v-model="form.preferredSubwayDistance" placeholder="如：1000" type="number" class="form-input" />
          <text class="input-suffix">米</text>
        </view>
      </view>

      <button class="save-btn" :loading="loading" @click="onSave">
        <text>保存偏好</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'

const loading = ref(false)
const typeOptions = ['一居', '两居', '三居', '四居及以上']
const decorationOptions = ['毛坯', '简装', '精装']

const form = reactive({
  preferredArea: '',
  minBudget: '',
  maxBudget: '',
  preferredType: '',
  preferredDecoration: '',
  preferredSubwayDistance: '',
})

onMounted(async () => {
  try {
    const res = await get<any>('/preference/my')
    if (res.data) {
      Object.assign(form, {
        preferredArea: res.data.preferredArea || '',
        minBudget: res.data.minBudget || '',
        maxBudget: res.data.maxBudget || '',
        preferredType: res.data.preferredType || '',
        preferredDecoration: res.data.preferredDecoration || '',
        preferredSubwayDistance: res.data.preferredSubwayDistance || '',
      })
    }
  } catch { /* ignore */ }
})

function onTypeChange(e: any) { form.preferredType = typeOptions[e.detail.value] }
function onDecorationChange(e: any) { form.preferredDecoration = decorationOptions[e.detail.value] }

async function onSave() {
  loading.value = true
  try {
    await post('/preference', {
      preferredArea: form.preferredArea || null,
      minBudget: form.minBudget ? Number(form.minBudget) : null,
      maxBudget: form.maxBudget ? Number(form.maxBudget) : null,
      preferredType: form.preferredType || null,
      preferredDecoration: form.preferredDecoration || null,
      preferredSubwayDistance: form.preferredSubwayDistance ? Number(form.preferredSubwayDistance) : null,
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.preference-page { min-height: 100vh; background: #f6f4ef; }
.header { position: relative; height: 280rpx; }
.header-bg { position: absolute; top: 0; left: 0; right: 0; height: 280rpx; background: linear-gradient(135deg, #8a6a24 0%, #b08a3a 100%); border-radius: 0 0 48rpx 48rpx; }
.header-content { position: relative; z-index: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 280rpx; }
.header-icon { font-size: 56rpx; margin-bottom: 12rpx; }
.header-title { font-size: 36rpx; font-weight: 700; color: #fff; letter-spacing: 2rpx; }
.header-subtitle { font-size: 22rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; }
.form-section { padding: 0 24rpx; margin-top: -30rpx; position: relative; z-index: 2; }
.form-card { background: #fff; border-radius: 24rpx; padding: 28rpx; margin-bottom: 20rpx; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.04); }
.card-header { display: flex; align-items: center; margin-bottom: 16rpx; }
.card-icon-wrap { width: 48rpx; height: 48rpx; display: flex; align-items: center; justify-content: center; background: rgba(176,138,58,0.10); border-radius: 14rpx; margin-right: 12rpx; }
.card-icon { font-size: 24rpx; }
.card-title { font-size: 28rpx; font-weight: 600; color: #1a1a2e; }
.input-group { display: flex; align-items: center; background: #f5f7fa; border-radius: 16rpx; padding: 20rpx; }
.form-input { flex: 1; font-size: 28rpx; background: transparent; }
.input-suffix { font-size: 24rpx; color: #bbb; margin-left: 8rpx; }
.budget-row { display: flex; align-items: center; gap: 16rpx; }
.budget-input-group { flex: 1; background: #f5f7fa; border-radius: 16rpx; padding: 20rpx; }
.budget-sep { color: #ccc; font-size: 28rpx; }
.budget-unit { font-size: 24rpx; color: #bbb; }
.picker-item { display: flex; justify-content: space-between; align-items: center; background: #f5f7fa; border-radius: 16rpx; padding: 20rpx; font-size: 28rpx; color: #bbb; }
.picker-item.active { color: #333; }
.picker-arrow { color: #ccc; font-size: 24rpx; }
.save-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: linear-gradient(135deg, #b08a3a 0%, #8a6a24 100%); color: #fff; font-size: 32rpx; font-weight: 700; border-radius: 24rpx; border: none; margin-top: 20rpx; box-shadow: 0 8rpx 24rpx rgba(176,138,58,0.22); letter-spacing: 4rpx; transition: transform 0.2s ease; }
.save-btn:active { transform: scale(0.96); }
</style>
