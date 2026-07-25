<template>
  <view class="container">
    <view class="hero">
      <text class="hero-kicker">SmartRent</text>
      <text class="hero-title">找到适合日常生活的房子</text>
      <view class="search-card" @click="goSearch">
        <text class="search-icon">⌕</text>
        <text class="search-placeholder">搜索区域、地铁、小区或户型</text>
      </view>
      <view class="quick-row">
        <text v-for="item in quickTags" :key="item" class="quick-tag" @click="onFilter(item)">{{ item }}</text>
      </view>
      <view class="hero-agent" @click="goAgent">
        <view class="hero-agent-icon">AI</view>
        <view class="hero-agent-copy">
          <text class="hero-agent-title">智能找房助手</text>
          <text class="hero-agent-desc">一句话说需求，帮你整理区域、预算和房源选择</text>
        </view>
        <text class="hero-agent-arrow">›</text>
      </view>
    </view>

    <view class="filter-bar">
      <view
        v-for="item in filterOptions"
        :key="item.value"
        class="filter-item"
        :class="{ active: currentFilter === item.value }"
        @click="onFilter(item.value)"
      >
        <text>{{ item.label }}</text>
      </view>
    </view>

    <view class="section-title">
      <text>推荐房源</text>
      <text class="section-sub">{{ total || houseList.length }} 套在租</text>
    </view>

    <view class="house-list">
      <view v-for="house in houseList" :key="house.id" class="house-card" @click="goDetail(house.id)">
        <image class="house-image" :src="house.images?.[0] || '/static/default-house.png'" mode="aspectFill" />
        <view class="house-info">
          <view class="title-row">
            <text class="house-title">{{ house.title }}</text>
            <text v-if="house.avgRating > 0" class="rating">{{ house.avgRating }}分</text>
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
          <view class="bottom-row">
            <text class="price">¥{{ house.price }}<text class="unit">/月</text></text>
            <view class="card-actions">
              <text class="nav-chip" @click.stop="openHouseMap(house)">导航</text>
              <text class="views">{{ house.viewCount || 0 }} 次浏览</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="loading && houseList.length === 0" class="skeleton-list">
        <view v-for="i in 3" :key="i" class="skeleton-card">
          <view class="skeleton-image"></view>
          <view class="skeleton-content">
            <view class="skeleton-line long"></view>
            <view class="skeleton-line"></view>
            <view class="skeleton-line short"></view>
          </view>
        </view>
      </view>

      <view v-if="houseList.length === 0 && !loading" class="empty">
        <text class="empty-title">暂无匹配房源</text>
        <text class="empty-hint">换个筛选条件再看看。</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getHouseList, type HouseItem } from '@/api/house'
import { mockHouses } from '@/utils/mock-data'

const houseList = ref<HouseItem[]>([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const currentFilter = ref('')

const quickTags = ['近地铁', '可短租', '有电梯']
const filterOptions = [
  { label: '整租', value: '整租' },
  { label: '合租', value: '合租' },
  { label: '一居', value: '一居' },
  { label: '两居', value: '两居' },
  { label: '三居', value: '三居' },
]

async function loadHouseList(reset = false) {
  if (loading.value) return
  loading.value = true
  if (reset) {
    page.value = 1
    houseList.value = []
  }
  try {
    const params: any = { page: page.value, size: 10 }
    if (currentFilter.value) {
      if (['整租', '合租'].includes(currentFilter.value)) {
        params.rentType = currentFilter.value
      } else if (['一居', '两居', '三居'].includes(currentFilter.value)) {
        params.houseType = currentFilter.value
      } else {
        params.keyword = currentFilter.value
      }
    }
    const res = await getHouseList(params)
    const records = res.data.records?.length ? res.data.records : filterMockHouses(currentFilter.value)
    houseList.value = reset ? records : [...houseList.value, ...records]
    total.value = res.data.total || records.length
  } catch {
    const records = filterMockHouses(currentFilter.value)
    houseList.value = reset ? records : [...houseList.value, ...records]
    total.value = records.length
  } finally {
    loading.value = false
  }
}

function filterMockHouses(value: string) {
  if (!value) return mockHouses
  return mockHouses.filter((house) => {
    return [house.title, house.area, house.address, house.houseType, house.rentType, ...(house.tags || [])]
      .some((item) => String(item || '').includes(value))
  })
}

function onFilter(value: string) {
  currentFilter.value = currentFilter.value === value ? '' : value
  loadHouseList(true)
}

function goSearch() {
  uni.navigateTo({ url: '/pages/house/search' })
}

function goAgent() {
  uni.switchTab({ url: '/pages/agent/chat' })
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/house/detail?id=${id}` })
}

function openHouseMap(house: HouseItem) {
  if (!house.longitude || !house.latitude) {
    uni.showToast({ title: '暂无房源坐标', icon: 'none' })
    return
  }
  uni.openLocation({
    latitude: house.latitude,
    longitude: house.longitude,
    name: house.title,
    address: house.address,
    scale: 16,
  })
}

onMounted(() => loadHouseList(true))
onPullDownRefresh(() => { loadHouseList(true).finally(() => uni.stopPullDownRefresh()) })
onReachBottom(() => { if (houseList.value.length < total.value) { page.value++; loadHouseList() } })
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f6f4ef;
  padding-bottom: 36rpx;
}
.hero {
  position: relative;
  padding: 40rpx 30rpx 30rpx;
  background: linear-gradient(150deg, #241d12 0%, #1c1812 55%, #2a2214 100%);
  border-radius: 0 0 34rpx 34rpx;
  overflow: hidden;
}
.hero::after {
  content: '';
  position: absolute;
  top: -120rpx;
  right: -80rpx;
  width: 360rpx;
  height: 360rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(214,192,136,0.35), rgba(214,192,136,0));
  pointer-events: none;
}
.hero-kicker,
.hero-title {
  display: block;
  position: relative;
}
.hero-kicker {
  color: #d6c088;
  font-size: 24rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
}
.hero-title {
  margin-top: 8rpx;
  color: #fffaf0;
  font-size: 42rpx;
  font-weight: 820;
  line-height: 1.24;
}
.search-card {
  display: flex;
  align-items: center;
  gap: 14rpx;
  height: 82rpx;
  margin-top: 28rpx;
  padding: 0 22rpx;
  background: #fff;
  border-radius: 14rpx;
}
.search-icon {
  color: #b08a3a;
  font-size: 34rpx;
  font-weight: 800;
}
.search-placeholder {
  color: #7b8582;
  font-size: 27rpx;
}
.quick-row {
  display: flex;
  gap: 14rpx;
  margin-top: 18rpx;
}
.quick-tag {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255,255,255,0.1);
  color: #e8dcc0;
  font-size: 23rpx;
}
.hero-agent {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 22rpx;
  padding: 18rpx;
  border-radius: 16rpx;
  background: rgba(255,255,255,0.1);
  border: 1rpx solid rgba(255,255,255,0.14);
}
.hero-agent-icon {
  width: 60rpx;
  height: 60rpx;
  flex: 0 0 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14rpx;
  background: linear-gradient(135deg, #b08a3a, #d6c088);
  color: #1c1812;
  font-size: 22rpx;
  font-weight: 900;
}
.hero-agent-copy {
  flex: 1;
  min-width: 0;
}
.hero-agent-title,
.hero-agent-desc {
  display: block;
}
.hero-agent-title {
  color: #fffaf0;
  font-size: 27rpx;
  font-weight: 800;
}
.hero-agent-desc {
  margin-top: 4rpx;
  color: #e8dcc0;
  font-size: 22rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hero-agent-arrow {
  color: #fffaf0;
  font-size: 42rpx;
  line-height: 1;
}
.filter-bar {
  display: flex;
  gap: 12rpx;
  padding: 24rpx 30rpx 14rpx;
  overflow-x: auto;
  white-space: nowrap;
}
.filter-item {
  padding: 13rpx 24rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  border-radius: 999rpx;
  color: #53615d;
  font-size: 25rpx;
}
.filter-item.active {
  background: #b08a3a;
  color: #fff;
  border-color: #b08a3a;
}
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 12rpx 30rpx 18rpx;
  color: #1c1812;
  font-size: 34rpx;
  font-weight: 800;
}
.section-sub {
  color: #7b8582;
  font-size: 24rpx;
  font-weight: 500;
}
.house-list {
  padding: 0 24rpx;
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
  width: 220rpx;
  height: 188rpx;
  border-radius: 12rpx;
  background: #e9e3d9;
}
.house-info {
  flex: 1;
  min-width: 0;
}
.title-row,
.bottom-row,
.meta-row,
.tag-row {
  display: flex;
  align-items: center;
}
.title-row {
  gap: 10rpx;
}
.house-title {
  flex: 1;
  color: #1f2a2e;
  font-size: 30rpx;
  font-weight: 760;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.rating {
  color: #b45309;
  background: #fff2d7;
  border-radius: 999rpx;
  padding: 4rpx 10rpx;
  font-size: 20rpx;
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
  font-size: 23rpx;
}
.meta-row text {
  padding: 4rpx 10rpx;
  background: #f7f4ee;
  border-radius: 6rpx;
}
.tag-row {
  gap: 8rpx;
  margin-top: 12rpx;
  overflow: hidden;
}
.tag {
  padding: 4rpx 10rpx;
  border-radius: 6rpx;
  background: #f4ecd6;
  color: #b08a3a;
  font-size: 21rpx;
}
.bottom-row {
  justify-content: space-between;
  margin-top: 14rpx;
}
.price {
  color: #b0791f;
  font-size: 31rpx;
  font-weight: 820;
}
.unit {
  font-size: 21rpx;
  font-weight: 500;
}
.views {
  color: #9aa29f;
  font-size: 21rpx;
}
.card-actions {
  display: flex;
  align-items: center;
  gap: 10rpx;
}
.nav-chip {
  height: 44rpx;
  line-height: 44rpx;
  padding: 0 16rpx;
  border-radius: 999rpx;
  background: #f4ecd6;
  color: #b08a3a;
  border: 1rpx solid #e4d2a8;
  font-size: 21rpx;
  font-weight: 800;
}
.nav-chip:active {
  transform: scale(0.96);
}
.skeleton-card {
  display: flex;
  gap: 18rpx;
  padding: 16rpx;
  margin-bottom: 18rpx;
  background: #fff;
  border-radius: 16rpx;
}
.skeleton-image,
.skeleton-line {
  background: linear-gradient(90deg, #eee8dd 25%, #f7f4ee 50%, #eee8dd 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}
.skeleton-image {
  width: 220rpx;
  height: 188rpx;
  border-radius: 12rpx;
}
.skeleton-content {
  flex: 1;
  padding-top: 10rpx;
}
.skeleton-line {
  width: 58%;
  height: 24rpx;
  border-radius: 999rpx;
  margin-bottom: 22rpx;
}
.skeleton-line.long { width: 84%; }
.skeleton-line.short { width: 42%; }
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
.empty {
  padding: 110rpx 0;
  text-align: center;
}
.empty-title,
.empty-hint {
  display: block;
}
.empty-title {
  color: #1c1812;
  font-size: 32rpx;
  font-weight: 750;
}
.empty-hint {
  margin-top: 12rpx;
  color: #7b8582;
  font-size: 25rpx;
}
</style>
