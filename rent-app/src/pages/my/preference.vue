<template>
  <view class="preference-page">
    <AppNavbar :show-back="true" title="租房偏好" />

    <view class="form-section">
      <view class="form-row">
        <view class="ic"><SrIcon name="pin" :size="36" color="#b08a3a" /></view>
        <text class="lbl">偏好区域</text>
        <view class="ctrl">
          <input v-model="form.preferredArea" placeholder="如：海淀区,朝阳区" placeholder-class="input-ph" class="form-input" />
        </view>
      </view>

      <view class="form-row">
        <view class="ic"><SrIcon name="trend" :size="36" color="#b08a3a" /></view>
        <text class="lbl">预算范围</text>
        <view class="ctrl">
          <input v-model="form.minBudget" placeholder="最低" type="number" placeholder-class="input-ph" class="form-input mini" />
          <text class="sep">—</text>
          <input v-model="form.maxBudget" placeholder="最高" type="number" placeholder-class="input-ph" class="form-input mini" />
          <text class="unit">元/月</text>
        </view>
      </view>

      <view class="form-row">
        <view class="ic"><SrIcon name="home" :size="36" color="#b08a3a" /></view>
        <text class="lbl">偏好户型</text>
        <view class="ctrl">
          <picker :range="typeOptions" @change="onTypeChange">
            <view class="picker-val" :class="{ active: form.preferredType }">
              <text>{{ form.preferredType || '请选择户型' }}</text>
              <SrIcon name="chev-right" :size="30" color="#9a9183" />
            </view>
          </picker>
        </view>
      </view>

      <view class="form-row">
        <view class="ic"><SrIcon name="star" :size="36" color="#b08a3a" /></view>
        <text class="lbl">偏好装修</text>
        <view class="ctrl">
          <picker :range="decorationOptions" @change="onDecorationChange">
            <view class="picker-val" :class="{ active: form.preferredDecoration }">
              <text>{{ form.preferredDecoration || '请选择装修' }}</text>
              <SrIcon name="chev-right" :size="30" color="#9a9183" />
            </view>
          </picker>
        </view>
      </view>

      <view class="form-row">
        <view class="ic"><SrIcon name="map" :size="36" color="#b08a3a" /></view>
        <text class="lbl">地铁距离</text>
        <view class="ctrl">
          <input v-model="form.preferredSubwayDistance" placeholder="如：1000" type="number" placeholder-class="input-ph" class="form-input" />
          <text class="unit">米</text>
        </view>
      </view>

      <button class="save-btn" :loading="loading" @click="onSave">
        <text>保存偏好</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import AppNavbar from '@/components/app-navbar.vue'
import SrIcon from '@/components/SrIcon.vue'
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
.preference-page { min-height: 100vh; background: var(--bg); }
.form-section { padding: 24rpx; position: relative; z-index: 2; }
.form-row { display: flex; align-items: center; gap: 20rpx; background: var(--glass); border: 1rpx solid var(--line); border-radius: 30rpx; padding: 26rpx; box-shadow: var(--shadow); margin-bottom: 20rpx; }
.form-row .ic { width: 56rpx; height: 56rpx; border-radius: 18rpx; background: var(--glass-2); display: flex; align-items: center; justify-content: center; flex: 0 0 auto; }
.form-row .lbl { font-size: 28rpx; color: var(--txt); font-weight: 600; flex: 0 0 auto; width: 130rpx; }
.form-row .ctrl { flex: 1; display: flex; align-items: center; justify-content: flex-end; gap: 12rpx; min-width: 0; }
.form-input { flex: 1; text-align: right; font-size: 28rpx; background: transparent; color: var(--txt); }
.form-input.mini { flex: 0 0 100rpx; text-align: center; }
.input-ph { color: var(--txt-3); }
.sep { color: var(--txt-3); font-size: 28rpx; }
.unit { font-size: 24rpx; color: var(--txt-2); flex: 0 0 auto; }
.picker-val { display: flex; align-items: center; gap: 10rpx; font-size: 28rpx; color: var(--txt-3); }
.picker-val.active { color: var(--txt); }
.save-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: var(--grad); color: #2a2113; font-size: 32rpx; font-weight: 700; border-radius: 16rpx; border: none; margin-top: 20rpx; box-shadow: 0 12rpx 24rpx rgba(176,138,58,0.2); letter-spacing: 4rpx; transition: transform 0.2s ease; }
.save-btn:active { transform: scale(0.96); }
</style>
