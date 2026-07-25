<template>
  <view class="search-page">
    <view class="search-shell">
      <view class="search-bar">
        <text class="search-mark">⌕</text>
        <input
          v-model="keyword"
          placeholder="区域 / 小区 / 地铁 / 户型"
          class="search-input"
          focus
          confirm-type="search"
          @confirm="doSearch"
        />
        <text v-if="keyword" class="clear-btn" @click="clearKeyword">×</text>
      </view>
      <button class="search-btn" @click="doSearch">搜索</button>
    </view>

    <view class="quick-panel" v-if="!searched">
      <view class="panel-head">
        <text class="panel-title">常用搜索</text>
        <text class="panel-sub">更快找到合适房源</text>
      </view>
      <view class="quick-tags">
        <text v-for="item in quickKeywords" :key="item" class="quick-tag" @click="quickSearch(item)">{{ item }}</text>
      </view>
    </view>

    <view v-if="searched" class="result-head">
      <view>
        <text class="result-title">搜索结果</text>
        <text class="result-sub">{{ results.length }} 套匹配房源</text>
      </view>
      <text class="result-keyword">“{{ keyword }}”</text>
    </view>

    <view class="result-list">
      <view v-for="house in displayResults" :key="house.id" class="house-card" @click="goDetail(house.id)">
        <image class="house-image" :src="house.images?.[0] || '/static/default-house.png'" mode="aspectFill" />
        <view class="house-info">
          <view class="title-row">
            <text class="house-title">{{ house.title }}</text>
            <text class="price">¥{{ house.price }}<text class="unit">/月</text></text>
          </view>
          <text class="address">{{ house.area }} · {{ house.address }}</text>
          <view class="meta-row">
            <text>{{ house.houseType }}</text>
            <text v-if="house.areaSize">{{ house.areaSize }}m²</text>
            <text>{{ house.rentType }}</text>
          </view>
          <view class="tag-row" v-if="house.tags?.length">
            <text v-for="tag in house.tags.slice(0, 3)" :key="tag" class="tag">{{ tag }}</text>
          </view>
        </view>
      </view>

      <view v-if="searched && displayResults.length === 0" class="empty">
        <view class="empty-art">
          <view class="roof"></view>
          <view class="house-body"></view>
          <view class="door"></view>
        </view>
        <text class="empty-title">没有找到相关房源</text>
        <text class="empty-desc">换个区域、小区名，或试试“两居”“近地铁”。</text>
      </view>

      <view v-if="!searched" class="preview-note">
        <text>示例房源</text>
        <text>当前展示本地预览数据</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { searchHouse, type HouseItem } from '@/api/house'
import { mockHouses } from '@/utils/mock-data'

const keyword = ref('')
const results = ref<HouseItem[]>([])
const searched = ref(false)
const quickKeywords = ['近地铁', '两居', '整租', '有电梯', '海淀区', '可短租']
const displayResults = computed(() => searched.value ? results.value : mockHouses)

async function doSearch() {
  if (!keyword.value.trim()) return
  try {
    const res = await searchHouse(keyword.value.trim())
    const data = Array.isArray(res.data) ? res.data as any : res.data.records
    results.value = data.length ? data : filterMockHouses(keyword.value.trim())
  } catch {
    results.value = filterMockHouses(keyword.value.trim())
  } finally {
    searched.value = true
  }
}

function filterMockHouses(value: string) {
  const text = value.toLowerCase()
  return mockHouses.filter((house) => {
    return [house.title, house.area, house.address, house.houseType, house.rentType, ...(house.tags || [])]
      .some((item) => String(item || '').toLowerCase().includes(text))
  })
}

function quickSearch(value: string) {
  keyword.value = value
  doSearch()
}

function clearKeyword() {
  keyword.value = ''
  searched.value = false
  results.value = []
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/house/detail?id=${id}` })
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  padding: 24rpx 24rpx 48rpx;
  background: #f6f4ef;
}
.search-shell {
  display: flex;
  align-items: center;
  gap: 14rpx;
  padding: 18rpx;
  background: #1c1812;
  border-radius: 20rpx;
}
.search-bar {
  flex: 1;
  height: 78rpx;
  display: flex;
  align-items: center;
  padding: 0 22rpx;
  background: #fff;
  border-radius: 14rpx;
}
.search-mark {
  margin-right: 12rpx;
  color: #b08a3a;
  font-size: 34rpx;
  font-weight: 800;
}
.search-input {
  flex: 1;
  height: 78rpx;
  color: #1f2a2e;
  font-size: 27rpx;
}
.clear-btn {
  width: 42rpx;
  height: 42rpx;
  line-height: 38rpx;
  text-align: center;
  border-radius: 50%;
  background: #eee9df;
  color: #7b8582;
  font-size: 34rpx;
}
.search-btn {
  width: 112rpx;
  height: 78rpx;
  line-height: 78rpx;
  margin: 0;
  border-radius: 14rpx;
  background: #b0791f;
  color: #fff;
  font-size: 27rpx;
  font-weight: 750;
}
.quick-panel {
  margin-top: 22rpx;
  padding: 28rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 18rpx;
}
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 20rpx;
}
.panel-title {
  color: #1c1812;
  font-size: 31rpx;
  font-weight: 780;
}
.panel-sub {
  color: #7b8582;
  font-size: 23rpx;
}
.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
}
.quick-tag {
  padding: 13rpx 22rpx;
  border-radius: 999rpx;
  background: #f7f4ee;
  color: #53615d;
  font-size: 25rpx;
}
.result-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 6rpx 18rpx;
}
.result-title,
.result-sub {
  display: block;
}
.result-title {
  color: #1c1812;
  font-size: 32rpx;
  font-weight: 800;
}
.result-sub,
.result-keyword {
  margin-top: 6rpx;
  color: #7b8582;
  font-size: 23rpx;
}
.result-list {
  margin-top: 18rpx;
}
.house-card {
  display: flex;
  gap: 18rpx;
  padding: 16rpx;
  margin-bottom: 18rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 16rpx;
}
.house-card:active {
  transform: scale(0.99);
}
.house-image {
  width: 218rpx;
  height: 186rpx;
  border-radius: 12rpx;
  background: #e9e3d9;
}
.house-info {
  flex: 1;
  min-width: 0;
}
.title-row,
.meta-row,
.tag-row {
  display: flex;
  align-items: center;
}
.title-row {
  gap: 12rpx;
}
.house-title {
  flex: 1;
  color: #1f2a2e;
  font-size: 29rpx;
  font-weight: 760;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.price {
  color: #b0791f;
  font-size: 28rpx;
  font-weight: 820;
}
.unit {
  font-size: 20rpx;
  font-weight: 500;
}
.address {
  display: block;
  margin-top: 10rpx;
  color: #7b8582;
  font-size: 23rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta-row {
  gap: 10rpx;
  margin-top: 12rpx;
  color: #53615d;
  font-size: 22rpx;
}
.meta-row text {
  padding: 4rpx 10rpx;
  background: #f7f4ee;
  border-radius: 6rpx;
}
.tag-row {
  gap: 8rpx;
  margin-top: 12rpx;
}
.tag {
  padding: 4rpx 10rpx;
  border-radius: 6rpx;
  background: #f4ecd6;
  color: #b08a3a;
  font-size: 21rpx;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 96rpx 34rpx 40rpx;
}
.empty-soft {
  padding-top: 110rpx;
}
.empty-art {
  position: relative;
  width: 112rpx;
  height: 96rpx;
  margin-bottom: 26rpx;
}
.roof {
  position: absolute;
  left: 20rpx;
  top: 0;
  width: 70rpx;
  height: 70rpx;
  background: #b0791f;
  transform: rotate(45deg);
  border-radius: 8rpx;
}
.house-body {
  position: absolute;
  left: 18rpx;
  bottom: 0;
  width: 76rpx;
  height: 58rpx;
  background: #fff;
  border: 5rpx solid #1c1812;
  border-radius: 8rpx;
}
.door {
  position: absolute;
  left: 48rpx;
  bottom: 5rpx;
  width: 18rpx;
  height: 28rpx;
  background: #b08a3a;
  border-radius: 4rpx 4rpx 0 0;
}
.empty-title {
  color: #1c1812;
  font-size: 31rpx;
  font-weight: 760;
}
.preview-note {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8rpx 6rpx 18rpx;
  color: #7b8582;
  font-size: 23rpx;
}
.empty-desc {
  max-width: 520rpx;
  margin-top: 12rpx;
  color: #7b8582;
  font-size: 24rpx;
  line-height: 1.6;
  text-align: center;
}
</style>
