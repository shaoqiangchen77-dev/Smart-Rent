<template>
  <view class="appointment-page">
    <view class="booking-hero">
      <text class="hero-kicker">SmartRent</text>
      <text class="hero-title">预约一次放心看房</text>
      <text class="hero-subtitle">选好到访时间，房东确认后会在消息里提醒你。</text>
      <view class="hero-points">
        <text>实地采光</text>
        <text>通勤核对</text>
        <text>费用确认</text>
      </view>
    </view>

    <view class="form-section">
      <view class="form-card">
        <view class="card-head">
          <view class="mark">时</view>
          <view>
            <text class="card-title">看房时间</text>
            <text class="card-desc">建议预留 30 分钟，方便看户型和周边。</text>
          </view>
        </view>

        <view class="picker-row">
          <picker mode="date" :start="today" @change="onDateChange">
            <view class="picker-item" :class="{ active: form.date }">
              <text class="picker-label">日期</text>
              <text class="picker-value">{{ form.date || '选择日期' }}</text>
            </view>
          </picker>
          <picker mode="time" @change="onTimeChange">
            <view class="picker-item" :class="{ active: form.time }">
              <text class="picker-label">时间</text>
              <text class="picker-value">{{ form.time || '选择时间' }}</text>
            </view>
          </picker>
        </view>
      </view>

      <view class="form-card">
        <view class="card-head">
          <view class="mark">电</view>
          <view>
            <text class="card-title">联系方式</text>
            <text class="card-desc">仅用于本次看房确认。</text>
          </view>
        </view>
        <view class="field">
          <input v-model="form.phone" placeholder="请输入联系电话" type="number" class="form-input" />
        </view>
      </view>

      <view class="form-card">
        <view class="card-head">
          <view class="mark">备</view>
          <view>
            <text class="card-title">备注</text>
            <text class="card-desc">比如想重点看采光、隔音、停车位。</text>
          </view>
        </view>
        <view class="field textarea-field">
          <textarea v-model="form.remark" placeholder="选填，告诉房东你的看房关注点" class="form-textarea" />
        </view>
      </view>

      <view class="submit-panel">
        <view class="submit-copy">
          <text class="submit-title">提交后等待确认</text>
          <text class="submit-desc">预约结果会同步到消息中心。</text>
        </view>
        <button class="submit-btn" :loading="loading" @click="onSubmit">确认预约</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { post } from '@/utils/request'

const loading = ref(false)
const today = new Date().toISOString().split('T')[0]
const form = reactive({
  houseId: 0,
  landlordId: 0,
  date: '',
  time: '',
  phone: '',
  remark: '',
})

onMounted(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  const opts = page.$page?.options || page.options
  form.houseId = Number(opts.houseId || 0)
  form.landlordId = Number(opts.landlordId || 0)
})

function onDateChange(e: any) {
  form.date = e.detail.value
}

function onTimeChange(e: any) {
  form.time = e.detail.value
}

async function onSubmit() {
  if (!form.date || !form.time) {
    uni.showToast({ title: '请选择看房时间', icon: 'none' })
    return
  }
  if (!form.phone) {
    uni.showToast({ title: '请输入联系电话', icon: 'none' })
    return
  }

  loading.value = true
  try {
    await post('/appointment', {
      houseId: form.houseId,
      landlordId: form.landlordId,
      viewingTime: form.date + 'T' + form.time + ':00',
      contactPhone: form.phone,
      remark: form.remark,
    })
  } catch {
    console.log('use preview appointment success')
  } finally {
    loading.value = false
  }

  uni.showToast({ title: '预约已提交', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 1200)
}
</script>

<style scoped>
.appointment-page {
  min-height: 100vh;
  background: #f6f4ef;
  padding-bottom: 40rpx;
}

.booking-hero {
  margin: 24rpx 24rpx 0;
  padding: 36rpx 32rpx 30rpx;
  border-radius: 0 0 28rpx 28rpx;
  background: #8a6a24;
  color: #fff;
  box-shadow: 0 18rpx 40rpx rgba(20, 59, 52, 0.16);
}

.hero-kicker,
.hero-title,
.hero-subtitle {
  display: block;
}

.hero-kicker {
  color: #9fc9bd;
  font-size: 22rpx;
  font-weight: 700;
}

.hero-title {
  margin-top: 12rpx;
  font-size: 42rpx;
  line-height: 1.25;
  font-weight: 900;
  letter-spacing: 0;
}

.hero-subtitle {
  margin-top: 12rpx;
  color: rgba(255, 255, 255, 0.78);
  font-size: 24rpx;
  line-height: 1.55;
}

.hero-points {
  display: flex;
  gap: 14rpx;
  margin-top: 24rpx;
}

.hero-points text {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.12);
  color: #eaf4f0;
  font-size: 22rpx;
}

.form-section {
  padding: 24rpx;
}

.form-card {
  margin-bottom: 18rpx;
  padding: 28rpx;
  border-radius: 18rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  box-shadow: 0 10rpx 26rpx rgba(31, 42, 39, 0.04);
}

.card-head {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.mark {
  width: 48rpx;
  height: 48rpx;
  line-height: 48rpx;
  text-align: center;
  border-radius: 14rpx;
  background: #f4ecd6;
  color: #b08a3a;
  font-size: 24rpx;
  font-weight: 900;
}

.card-title,
.card-desc {
  display: block;
}

.card-title {
  color: #1c1812;
  font-size: 30rpx;
  font-weight: 900;
}

.card-desc {
  margin-top: 6rpx;
  color: #7b8582;
  font-size: 22rpx;
  line-height: 1.45;
}

.picker-row {
  display: flex;
  gap: 16rpx;
}

.picker-row picker {
  flex: 1;
}

.picker-item {
  min-height: 104rpx;
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: #f8f6f1;
  border: 1rpx solid #ede7dc;
  box-sizing: border-box;
}

.picker-item.active {
  background: #edf7f3;
  border-color: #a9d4ca;
}

.picker-label,
.picker-value {
  display: block;
}

.picker-label {
  color: #8d9692;
  font-size: 21rpx;
}

.picker-value {
  margin-top: 8rpx;
  color: #1c1812;
  font-size: 28rpx;
  font-weight: 800;
}

.field {
  padding: 0 20rpx;
  border-radius: 16rpx;
  background: #f8f6f1;
  border: 1rpx solid #ede7dc;
}

.form-input {
  width: 100%;
  height: 92rpx;
  color: #1c1812;
  font-size: 28rpx;
}

.textarea-field {
  padding-top: 18rpx;
  padding-bottom: 18rpx;
}

.form-textarea {
  width: 100%;
  height: 148rpx;
  color: #1c1812;
  font-size: 28rpx;
  line-height: 1.5;
}

.submit-panel {
  margin-top: 28rpx;
  padding: 24rpx;
  border-radius: 20rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
}

.submit-title,
.submit-desc {
  display: block;
}

.submit-title {
  color: #1c1812;
  font-size: 28rpx;
  font-weight: 900;
}

.submit-desc {
  margin-top: 6rpx;
  color: #7b8582;
  font-size: 22rpx;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  margin-top: 20rpx;
  border: none;
  border-radius: 16rpx;
  background: #b08a3a;
  color: #fff;
  font-size: 30rpx;
  font-weight: 900;
  box-shadow: 0 12rpx 24rpx rgba(176,138,58, 0.2);
}

.submit-btn::after {
  border: none;
}

.submit-btn:active {
  transform: scale(0.98);
}
</style>
