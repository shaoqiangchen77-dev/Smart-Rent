<template>
  <view class="detail-page">
    <view v-if="house" class="content">
      <swiper class="image-swiper" indicator-dots indicator-color="rgba(255,255,255,0.45)" indicator-active-color="#ffffff" autoplay circular>
        <swiper-item v-for="(img, idx) in house.images" :key="idx">
          <image :src="img" mode="aspectFill" class="swiper-image" />
        </swiper-item>
        <swiper-item v-if="!house.images?.length">
          <image src="/static/default-house.png" mode="aspectFill" class="swiper-image" />
        </swiper-item>
      </swiper>
      <view class="swiper-overlay"></view>

      <view class="info-card main-card">
        <view class="title-row">
          <text class="title">{{ house.title }}</text>
          <text class="rating">{{ house.avgRating || '4.8' }}分</text>
        </view>

        <view class="price-row">
          <view class="price-wrap">
            <text class="price-symbol">¥</text>
            <text class="price">{{ house.price }}</text>
            <text class="unit">/月</text>
          </view>
          <text class="rent-type">{{ house.rentType }}</text>
        </view>

        <view class="meta-chips">
          <view class="chip"><text>{{ house.houseType }}</text></view>
          <view v-if="house.areaSize" class="chip"><text>{{ house.areaSize }}m²</text></view>
          <view v-if="house.floor" class="chip"><text>{{ house.floor }}</text></view>
          <view v-if="house.decoration" class="chip"><text>{{ house.decoration }}</text></view>
        </view>

        <view class="address-line">
          <text>{{ house.area }}</text>
          <text v-if="house.subwayDistance">距地铁 {{ house.subwayDistance }}m</text>
        </view>
      </view>

      <view class="info-card location-card">
        <view class="section-head">
          <view>
            <text class="section-title">位置与路线</text>
            <text class="section-subtitle">像店铺导航一样，点“去这里”直接打开微信地图。</text>
          </view>
          <text class="map-link" @click="openNavigation('map')">地图</text>
        </view>

        <view class="location-info">
          <text class="address">{{ house.address }}</text>
          <view v-if="distance" class="distance-badge">
            <text>距你 {{ distance }}</text>
          </view>
        </view>

        <map
          v-if="hasLocation"
          class="house-map"
          :latitude="house.latitude || 0"
          :longitude="house.longitude || 0"
          :markers="markers"
          :scale="15"
          show-location
        />
        <view v-else class="map-placeholder">
          <text>该房源暂未配置坐标</text>
        </view>

        <view class="route-card" :class="{ disabled: !hasLocation }" @click="openNavigation('route')">
          <view class="route-copy">
            <text class="route-title">去这里</text>
            <text class="route-desc">查看路线、周边和导航</text>
          </view>
          <view class="route-action">
            <text>导航</text>
          </view>
        </view>

        <view class="route-modes">
          <text @click="openNavigation('walk')">步行</text>
          <text @click="openNavigation('bike')">骑行</text>
          <text @click="openNavigation('drive')">驾车</text>
        </view>

        <view class="commute-grid">
          <view class="commute-item">
            <text class="commute-value">{{ house.subwayDistance ? house.subwayDistance + 'm' : '约8分钟' }}</text>
            <text class="commute-label">到地铁</text>
          </view>
          <view class="commute-item">
            <text class="commute-value">25分钟</text>
            <text class="commute-label">通勤商圈</text>
          </view>
          <view class="commute-item">
            <text class="commute-value">{{ distance || '定位后显示' }}</text>
            <text class="commute-label">当前位置</text>
          </view>
        </view>

        <view v-if="nearbyInfo.length" class="nearby-info">
          <text class="nearby-title">周边参考</text>
          <view v-for="(item, idx) in nearbyInfo" :key="idx" class="nearby-item">
            <text class="nearby-name">{{ item.name }}</text>
            <text class="nearby-dist">{{ item.distance }}</text>
          </view>
        </view>
      </view>

      <view v-if="house.tags?.length" class="info-card">
        <view class="section-head compact">
          <text class="section-title">房源亮点</text>
        </view>
        <view class="tags">
          <text v-for="tag in house.tags" :key="tag" class="tag">{{ tag }}</text>
        </view>
      </view>

      <view v-if="house.facilities?.length" class="info-card">
        <view class="section-head compact">
          <text class="section-title">配套设施</text>
        </view>
        <view class="facilities">
          <text v-for="f in house.facilities" :key="f" class="facility">{{ f }}</text>
        </view>
      </view>

      <view v-if="house.description" class="info-card">
        <view class="section-head compact">
          <text class="section-title">房源描述</text>
        </view>
        <text class="description">{{ house.description }}</text>
      </view>

      <view class="bottom-space"></view>

      <view class="bottom-bar">
        <view class="action-btn" :class="{ collected: collected }" @click="onCollect">
          <text>{{ collected ? '已收藏' : '收藏' }}</text>
        </view>
        <view class="action-btn map-action" @click="openNavigation('bottom')">
          <text>去这里</text>
        </view>
        <view class="action-btn primary" @click="onAppointment">
          <text>预约看房</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getHouseDetail, type HouseItem } from '@/api/house'
import { collect, uncollect } from '@/api/collection'
import { mockHouses } from '@/utils/mock-data'

const house = ref<HouseItem | null>(null)
const collected = ref(false)
const userLocation = ref<{ latitude: number; longitude: number } | null>(null)
const distance = ref('')
const nearbyInfo = ref<Array<{ name: string; distance: string }>>([])
const hasLocation = computed(() => !!house.value?.longitude && !!house.value?.latitude)

const markers = computed(() => {
  if (!hasLocation.value || !house.value) return []
  return [{
    id: 1,
    latitude: house.value.latitude || 0,
    longitude: house.value.longitude || 0,
    title: house.value.title,
    width: 28,
    height: 28,
  }]
})

onMounted(async () => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  const id = page.$page?.options?.id || page.options?.id
  if (!id) return

  try {
    const res = await getHouseDetail(Number(id))
    house.value = res.data
  } catch {
    house.value = mockHouses.find((item) => item.id === Number(id)) || mockHouses[0]
  }
  getUserLocation()
})

function getUserLocation() {
  uni.getLocation({
    type: 'gcj02',
    success: (res) => {
      userLocation.value = { latitude: res.latitude, longitude: res.longitude }
      if (house.value?.longitude && house.value?.latitude) {
        distance.value = calcDistance(res.latitude, res.longitude, house.value.latitude, house.value.longitude)
        loadNearbyInfo()
      }
    },
    fail: () => {
      loadNearbyInfo()
    },
  })
}

function calcDistance(lat1: number, lon1: number, lat2: number, lon2: number): string {
  const R = 6371000
  const dLat = toRad(lat2 - lat1)
  const dLon = toRad(lon2 - lon1)
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  const d = R * c
  if (d < 1000) return Math.round(d) + 'm'
  return (d / 1000).toFixed(1) + 'km'
}

function toRad(deg: number): number {
  return deg * (Math.PI / 180)
}

function loadNearbyInfo() {
  nearbyInfo.value = [
    { name: house.value?.subwayStation ? `${house.value.subwayStation}地铁站` : '附近地铁站', distance: house.value?.subwayDistance ? `${house.value.subwayDistance}m` : '待确认' },
    { name: '当前定位距离', distance: distance.value || '授权后显示' },
  ]
}

function openNavigation(mode: string) {
  if (!house.value?.longitude || !house.value?.latitude) {
    uni.showToast({ title: '暂无房源坐标', icon: 'none' })
    return
  }

  uni.openLocation({
    latitude: house.value.latitude,
    longitude: house.value.longitude,
    name: house.value.title,
    address: house.value.address,
    scale: 16,
    success: () => {
      console.log('open map', mode)
    },
  })
}

async function onCollect() {
  if (!house.value) return
  try {
    if (collected.value) {
      await uncollect(house.value.id)
      collected.value = false
      uni.showToast({ title: '已取消收藏', icon: 'success' })
    } else {
      await collect(house.value.id)
      collected.value = true
      uni.showToast({ title: '收藏成功', icon: 'success' })
    }
  } catch {
    collected.value = !collected.value
    uni.showToast({ title: collected.value ? '收藏成功' : '已取消收藏', icon: 'success' })
  }
}

function onAppointment() {
  if (!house.value) return
  uni.navigateTo({ url: `/pages/house/appointment?houseId=${house.value.id}&landlordId=${house.value.landlordId}` })
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f6f4ef;
}

.image-swiper {
  width: 100%;
  height: 520rpx;
}

.swiper-image {
  width: 100%;
  height: 100%;
}

.swiper-overlay {
  position: absolute;
  top: 390rpx;
  left: 0;
  right: 0;
  height: 140rpx;
  background: linear-gradient(180deg, rgba(246, 244, 239, 0), #f6f4ef);
  z-index: 1;
  pointer-events: none;
}

.info-card {
  position: relative;
  z-index: 2;
  margin: 18rpx 24rpx;
  padding: 28rpx;
  border-radius: 18rpx;
  background: #fff;
  border: 1rpx solid #e7e1d6;
  box-shadow: 0 10rpx 26rpx rgba(31, 42, 39, 0.04);
}

.main-card {
  margin-top: -54rpx;
}

.title-row {
  display: flex;
  gap: 16rpx;
  align-items: flex-start;
}

.title {
  flex: 1;
  color: #1c1812;
  font-size: 38rpx;
  line-height: 1.35;
  font-weight: 900;
  letter-spacing: 0;
}

.rating {
  padding: 8rpx 12rpx;
  border-radius: 999rpx;
  background: #fff4df;
  color: #b0791f;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 18rpx;
}

.price-wrap {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  margin-right: 2rpx;
  color: #b0791f;
  font-size: 26rpx;
  font-weight: 900;
}

.price {
  color: #b0791f;
  font-size: 50rpx;
  font-weight: 900;
}

.unit {
  margin-left: 4rpx;
  color: #8b918e;
  font-size: 24rpx;
}

.rent-type {
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  background: #f4ecd6;
  color: #b08a3a;
  border: 1rpx solid #e4d2a8;
  font-size: 23rpx;
  font-weight: 800;
}

.meta-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 18rpx;
}

.chip {
  padding: 9rpx 16rpx;
  border-radius: 10rpx;
  background: #f5f1e9;
  color: #5d665f;
  font-size: 23rpx;
  font-weight: 700;
}

.address-line {
  display: flex;
  flex-wrap: wrap;
  gap: 18rpx;
  margin-top: 18rpx;
  color: #69736f;
  font-size: 24rpx;
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  align-items: flex-start;
  margin-bottom: 18rpx;
}

.section-head.compact {
  margin-bottom: 16rpx;
}

.section-title,
.section-subtitle {
  display: block;
}

.section-title {
  color: #1c1812;
  font-size: 31rpx;
  line-height: 1.35;
  font-weight: 900;
  letter-spacing: 0;
}

.section-subtitle {
  margin-top: 6rpx;
  color: #8a928e;
  font-size: 22rpx;
  line-height: 1.45;
}

.map-link {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: #edf7f3;
  color: #b08a3a;
  font-size: 23rpx;
  font-weight: 900;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 16rpx;
}

.address {
  flex: 1;
  color: #5b6662;
  font-size: 25rpx;
  line-height: 1.5;
}

.distance-badge {
  padding: 7rpx 14rpx;
  border-radius: 999rpx;
  background: #fff4df;
  color: #b0791f;
  font-size: 21rpx;
  font-weight: 900;
  white-space: nowrap;
}

.house-map,
.map-placeholder {
  width: 100%;
  height: 340rpx;
  border-radius: 18rpx;
  overflow: hidden;
  margin-bottom: 16rpx;
}

.map-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #edf1ef;
  color: #7b8582;
  font-size: 24rpx;
}

.route-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 14rpx;
  padding: 22rpx;
  border-radius: 18rpx;
  background: #8a6a24;
  box-shadow: 0 12rpx 26rpx rgba(20, 59, 52, 0.15);
}

.route-card.disabled {
  opacity: 0.55;
}

.route-copy {
  flex: 1;
}

.route-title,
.route-desc {
  display: block;
}

.route-title {
  color: #fff;
  font-size: 31rpx;
  font-weight: 900;
}

.route-desc {
  margin-top: 6rpx;
  color: rgba(255, 255, 255, 0.72);
  font-size: 22rpx;
}

.route-action {
  width: 92rpx;
  height: 56rpx;
  line-height: 56rpx;
  text-align: center;
  border-radius: 999rpx;
  background: #f3c36b;
  color: #1c1812;
  font-size: 23rpx;
  font-weight: 900;
}

.route-modes {
  display: flex;
  gap: 12rpx;
  margin-bottom: 18rpx;
}

.route-modes text {
  flex: 1;
  height: 58rpx;
  line-height: 58rpx;
  text-align: center;
  border-radius: 999rpx;
  background: #f8f6f1;
  color: #5d665f;
  border: 1rpx solid #ede7dc;
  font-size: 23rpx;
  font-weight: 800;
}

.commute-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.commute-item {
  padding: 16rpx 10rpx;
  border-radius: 15rpx;
  background: #f8f6f1;
}

.commute-value,
.commute-label {
  display: block;
  text-align: center;
}

.commute-value {
  color: #1c1812;
  font-size: 24rpx;
  font-weight: 900;
}

.commute-label {
  margin-top: 6rpx;
  color: #7b8582;
  font-size: 20rpx;
}

.nearby-info {
  padding: 20rpx;
  border-radius: 16rpx;
  background: #fbfaf7;
  border: 1rpx solid #ede7dc;
}

.nearby-title {
  display: block;
  margin-bottom: 10rpx;
  color: #1c1812;
  font-size: 25rpx;
  font-weight: 900;
}

.nearby-item {
  display: flex;
  justify-content: space-between;
  padding: 9rpx 0;
  font-size: 23rpx;
}

.nearby-name {
  color: #65706b;
}

.nearby-dist {
  color: #b08a3a;
  font-weight: 900;
}

.tags,
.facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.tag,
.facility {
  padding: 9rpx 18rpx;
  border-radius: 999rpx;
  font-size: 23rpx;
  font-weight: 800;
}

.tag {
  background: #f4ecd6;
  color: #b08a3a;
  border: 1rpx solid #e4d2a8;
}

.facility {
  background: #f5f1e9;
  color: #5d665f;
}

.description {
  display: block;
  color: #5d665f;
  font-size: 27rpx;
  line-height: 1.75;
}

.bottom-space {
  height: 170rpx;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  gap: 14rpx;
  padding: 18rpx 24rpx;
  padding-bottom: calc(18rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.94);
  border-top: 1rpx solid #e7e1d6;
  box-shadow: 0 -10rpx 28rpx rgba(31, 42, 39, 0.08);
}

.action-btn {
  min-width: 124rpx;
  height: 78rpx;
  line-height: 78rpx;
  text-align: center;
  border-radius: 16rpx;
  background: #f5f1e9;
  color: #5d665f;
  font-size: 26rpx;
  font-weight: 900;
}

.action-btn.collected {
  background: #fff1f1;
  color: #c24141;
}

.map-action {
  background: #f4ecd6;
  color: #b08a3a;
}

.action-btn.primary {
  flex: 1;
  background: #b08a3a;
  color: #fff;
  box-shadow: 0 12rpx 24rpx rgba(176,138,58, 0.2);
}

.action-btn:active,
.route-card:active,
.route-modes text:active {
  transform: scale(0.98);
}
</style>
