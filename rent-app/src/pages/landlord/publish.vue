<template>
  <view class="page">
    <view class="form-card">
      <view class="field">
        <text class="label">房源标题</text>
        <input v-model="form.title" class="input" placeholder="例：近地铁南向两居，采光好" />
      </view>
      <view class="grid">
        <view class="field">
          <text class="label">租金</text>
          <input v-model.number="form.price" class="input" type="number" placeholder="月租" />
        </view>
        <view class="field">
          <text class="label">面积</text>
          <input v-model.number="form.areaSize" class="input" type="number" placeholder="m²" />
        </view>
      </view>
      <view class="grid">
        <view class="field">
          <text class="label">区域</text>
          <input v-model="form.area" class="input" placeholder="如 海淀区" />
        </view>
        <view class="field">
          <text class="label">户型</text>
          <input v-model="form.houseType" class="input" placeholder="如 两居" />
        </view>
      </view>
      <view class="field">
        <text class="label">详细地址</text>
        <input v-model="form.address" class="input" placeholder="小区、楼栋或附近地标" />
      </view>
      <view class="grid">
        <view class="field">
          <text class="label">出租方式</text>
          <picker :range="rentTypes" @change="form.rentType = rentTypes[Number($event.detail.value)]">
            <view class="picker">{{ form.rentType }}</view>
          </picker>
        </view>
        <view class="field">
          <text class="label">装修</text>
          <input v-model="form.decoration" class="input" placeholder="精装/简装" />
        </view>
      </view>
      <view class="field">
        <text class="label">亮点标签</text>
        <input v-model="tagText" class="input" placeholder="用逗号分隔，如 近地铁,可短租" />
      </view>
      <view class="field">
        <text class="label">配套设施</text>
        <input v-model="facilityText" class="input" placeholder="用逗号分隔，如 空调,洗衣机,电梯" />
      </view>
      <view class="field">
        <text class="label">房源描述</text>
        <textarea v-model="form.description" class="textarea" placeholder="写清通勤、采光、楼层、周边生活配套等真实信息" />
      </view>
      <button class="submit" :loading="submitting" @click="submit">提交审核</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { createHouse } from '@/api/house'

const rentTypes = ['整租', '合租']
const submitting = ref(false)
const tagText = ref('')
const facilityText = ref('')

const form = reactive({
  title: '',
  description: '',
  area: '',
  address: '',
  price: undefined as number | undefined,
  houseType: '',
  rentType: '整租',
  areaSize: undefined as number | undefined,
  decoration: '',
})

function splitText(value: string) {
  return value.split(/[,，]/).map((item) => item.trim()).filter(Boolean)
}

async function submit() {
  if (!form.title || !form.area || !form.address || !form.price || !form.houseType) {
    uni.showToast({ title: '请补全必填信息', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await createHouse({
      ...form,
      tags: splitText(tagText.value),
      facilities: splitText(facilityText.value),
      images: [],
    })
    uni.showToast({ title: '已提交审核', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 500)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  padding: 24rpx;
  background: #f6f4ef;
}
.form-card {
  padding: 26rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 16rpx;
}
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
}
.field {
  margin-bottom: 22rpx;
}
.label {
  display: block;
  margin-bottom: 10rpx;
  color: #53615d;
  font-size: 24rpx;
  font-weight: 650;
}
.input,
.picker,
.textarea {
  width: 100%;
  min-height: 78rpx;
  padding: 0 20rpx;
  border-radius: 12rpx;
  background: #f8f6f1;
  color: #1f2a2e;
  font-size: 27rpx;
  box-sizing: border-box;
}
.picker {
  line-height: 78rpx;
}
.textarea {
  height: 180rpx;
  padding: 18rpx 20rpx;
  line-height: 1.6;
}
.submit {
  height: 88rpx;
  line-height: 88rpx;
  margin-top: 8rpx;
  border-radius: 12rpx;
  background: #b08a3a;
  color: #fff;
  font-size: 30rpx;
  font-weight: 750;
}
</style>
