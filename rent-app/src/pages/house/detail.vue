<template>
  <view class="detail-page">
    <AppNavbar :show-back="true" title="房源详情" />

    <view v-if="house" class="content">
      <view class="hero-wrap">
        <swiper
          class="image-swiper"
          indicator-dots
          indicator-color="rgba(255,255,255,0.45)"
          indicator-active-color="#ffffff"
          autoplay
          circular
          @change="onSwiperChange"
        >
          <swiper-item v-for="(img, idx) in house.images" :key="idx">
            <image :src="img" mode="aspectFill" class="swiper-image" />
          </swiper-item>
          <swiper-item v-if="!house.images?.length">
            <image src="/static/house/g1.jpg" mode="aspectFill" class="swiper-image" />
          </swiper-item>
        </swiper>
        <view class="swiper-overlay"></view>

        <view class="fav" :class="{ on: collected }" @click="onCollect">
          <SrIcon name="heart" :size="32" :color="collected ? '#c75d5d' : '#b08a3a'" />
        </view>
        <view v-if="house.images?.length" class="chip">{{ current + 1 }} / {{ house.images.length }}</view>
      </view>

      <view class="info-card main-card">
        <view class="title-row">
          <text class="title serif">{{ house.title }}</text>
          <text class="rating">{{ house.avgRating || '4.8' }}分</text>
        </view>

        <view class="price-box">
          <view class="price-wrap">
            <text class="price-symbol">¥</text>
            <text class="price mono">{{ house.price }}</text>
            <text class="unit">/月</text>
          </view>
          <view class="sc">押一付三<br />含物业</view>
        </view>

        <view class="address-line">
          <SrIcon name="pin" :size="26" color="#b08a3a" />
          <text>{{ house.area }}</text>
          <text v-if="house.subwayDistance">距地铁 {{ house.subwayDistance }}m</text>
        </view>

        <view class="meta-chips">
          <text v-if="house.houseType" class="gold-pill">{{ house.houseType }}</text>
          <text v-if="house.areaSize" class="gold-pill">{{ house.areaSize }}m²</text>
          <text v-if="house.floor" class="gold-pill">{{ house.floor }}</text>
          <text v-if="house.decoration" class="gold-pill">{{ house.decoration }}</text>
        </view>
      </view>

      <view class="info-card">
        <SecHeader title="户型亮点" />
        <view class="feat">
          <view v-for="(f, i) in features" :key="i" class="f">
            <SrIcon :name="f.icon" :size="34" color="#b08a3a" />
            <text class="t">{{ f.text }}</text>
          </view>
        </view>
      </view>

      <view class="info-card">
        <SecHeader title="位置与路线" link-text="地图 ›" @link="openNavigation('map')" />
        <text class="section-subtitle">像店铺导航一样，点“去这里”直接打开微信地图。</text>

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

        <view class="route-card spotlight" :class="{ disabled: !hasLocation }" @click="openNavigation('route')">
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
        <SecHeader title="房源亮点" />
        <view class="tags">
          <text v-for="tag in house.tags" :key="tag" class="tag">{{ tag }}</text>
        </view>
      </view>

      <view v-if="house.facilities?.length" class="info-card">
        <SecHeader title="配套设施" />
        <view class="facilities">
          <text v-for="f in house.facilities" :key="f" class="facility">{{ f }}</text>
        </view>
      </view>

      <view v-if="house.description" class="info-card">
        <SecHeader title="房源描述" />
        <text class="description">{{ house.description }}</text>
      </view>

      <view class="info-card">
        <SecHeader title="房东信息" />
        <view class="li landlord">
          <view class="av">
            <SrIcon name="user" :size="36" color="#2a2113" />
          </view>
          <view class="c">
            <view class="t">房东</view>
            <view class="s">房源发布者 · 实名认证</view>
          </view>
          <view class="rt" @click="contactLandlord">联系 ›</view>
        </view>
      </view>

      <view class="info-card">
        <SecHeader title="AI 智能评估" />
        <text class="ai-eval">综合地段、租金与配套评估：该房源性价比较好，低于同板块均价约 8%，推荐指数 ★★★★☆，建议预约实地看房进一步核实采光与隔音。</text>
      </view>

      <view class="bottom-space"></view>

      <view class="bottom-bar">
        <view class="bb-fav" :class="{ collected }" @click="onCollect">
          <SrIcon name="heart" :size="36" :color="collected ? '#c75d5d' : '#b08a3a'" />
        </view>
        <view class="bb-price">
          <text class="mono">{{ house.price }}</text>
          <text class="sm">/月</text>
        </view>
        <view class="btns">
          <view class="ghost" @click="goAI">问 AI</view>
          <view class="gold" @click="onAppointment">预约看房</view>
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
import AppNavbar from '@/components/app-navbar.vue'
import SrIcon from '@/components/SrIcon.vue'
import SecHeader from '@/components/SecHeader.vue'
import { openAi } from '@/composables/useAiChat'

const house = ref<HouseItem | null>(null)
const collected = ref(false)
const userLocation = ref<{ latitude: number; longitude: number } | null>(null)
const distance = ref('')
const nearbyInfo = ref<Array<{ name: string; distance: string }>>([])
const hasLocation = computed(() => !!house.value?.longitude && !!house.value?.latitude)
const current = ref(0)

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

const features = computed(() => {
  const h = house.value
  if (!h) return []
  const list: { icon: string; text: string }[] = []
  if (h.houseType) list.push({ icon: 'home', text: h.houseType })
  if (h.areaSize) list.push({ icon: 'building', text: h.areaSize + 'm²' })
  if (h.floor) list.push({ icon: 'pin', text: h.floor })
  if (h.decoration) list.push({ icon: 'star', text: h.decoration })
  if (h.subwayDistance) list.push({ icon: 'map', text: '近地铁' })
  return list
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

  const params: string[] = [
    `lat=${house.value.latitude}`,
    `lng=${house.value.longitude}`,
    `name=${encodeURIComponent(house.value.title || '')}`,
    `address=${encodeURIComponent(house.value.address || '')}`,
  ]
  if (userLocation.value) {
    params.push(`fromlat=${userLocation.value.latitude}`)
    params.push(`fromlng=${userLocation.value.longitude}`)
  }
  uni.navigateTo({ url: `/pages/house/navigation?${params.join('&')}` })
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

function goAI() {
  openAi()
}

function contactLandlord() {
  uni.switchTab({ url: '/pages/message/list' })
}

function onSwiperChange(e: any) {
  current.value = e.detail.current
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: var(--bg);
}

.hero-wrap {
  position: relative;
  width: 100%;
  height: 520rpx;
}
.image-swiper {
  width: 100%;
  height: 100%;
}
.swiper-image {
  width: 100%;
  height: 100%;
}
.swiper-overlay {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 160rpx;
  background: linear-gradient(180deg, rgba(246, 244, 239, 0), var(--bg));
  z-index: 1;
  pointer-events: none;
}
.fav {
  position: absolute;
  top: 28rpx;
  right: 24rpx;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 253, 248, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 5;
}
.chip {
  position: absolute;
  bottom: 28rpx;
  right: 24rpx;
  background: rgba(255, 253, 248, 0.85);
  color: var(--txt);
  font-size: 22rpx;
  padding: 8rpx 18rpx;
  border-radius: 12rpx;
  z-index: 3;
}

.info-card {
  position: relative;
  z-index: 2;
  margin: 18rpx 24rpx;
  padding: 28rpx;
  border-radius: 18rpx;
  background: var(--glass);
  border: 1rpx solid var(--line);
  box-shadow: var(--shadow);
}
.main-card {
  margin-top: -54rpx;
}
.section-subtitle {
  display: block;
  margin: -6rpx 0 18rpx;
  color: var(--txt-3);
  font-size: 22rpx;
  line-height: 1.45;
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
  font-weight: 800;
}
.rating {
  padding: 8rpx 12rpx;
  border-radius: 999rpx;
  background: #f4ecd6;
  color: var(--gold-2);
  font-size: 22rpx;
  font-weight: 800;
  white-space: nowrap;
}

.price-box {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  background: var(--glass-solid);
  border: 1rpx solid var(--line);
  border-radius: 16rpx;
  padding: 22rpx;
  margin: 18rpx 0;
  box-shadow: var(--shadow);
}
.price-wrap {
  display: flex;
  align-items: baseline;
}
.price-symbol {
  margin-right: 2rpx;
  color: var(--gold-2);
  font-size: 26rpx;
  font-weight: 800;
}
.price {
  color: var(--gold-2);
  font-size: 50rpx;
  font-weight: 800;
}
.unit {
  margin-left: 4rpx;
  color: var(--txt-3);
  font-size: 24rpx;
}
.sc {
  font-size: 22rpx;
  color: var(--txt-2);
  text-align: right;
  line-height: 1.5;
}

.address-line {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10rpx;
  color: var(--txt-2);
  font-size: 24rpx;
}
.address-line .gap {
  margin: 0 4rpx;
}

.meta-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 18rpx;
}
.meta-chips .gold-pill {
  font-size: 22rpx;
}

/* 户型亮点 feat 网格 */
.feat {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14rpx;
}
.feat .f {
  background: var(--glass-solid);
  border: 1rpx solid var(--line);
  border-radius: 16rpx;
  padding: 22rpx 10rpx;
  text-align: center;
  box-shadow: var(--shadow);
}
.feat .f .t {
  display: block;
  font-size: 22rpx;
  color: var(--txt-2);
  margin-top: 12rpx;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 16rpx;
}
.address {
  flex: 1;
  color: var(--txt-2);
  font-size: 25rpx;
  line-height: 1.5;
}
.distance-badge {
  padding: 7rpx 14rpx;
  border-radius: 999rpx;
  background: #f4ecd6;
  color: var(--gold-2);
  font-size: 21rpx;
  font-weight: 800;
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
  background: var(--bg-2);
  color: var(--txt-3);
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
  background: var(--grad);
  box-shadow: 0 12rpx 26rpx rgba(176, 138, 58, 0.22);
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
  font-weight: 800;
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
  font-weight: 800;
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
  background: var(--bg-2);
  color: var(--txt-2);
  border: 1rpx solid var(--line);
  font-size: 23rpx;
  font-weight: 700;
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
  background: var(--bg-2);
}
.commute-value,
.commute-label {
  display: block;
  text-align: center;
}
.commute-value {
  color: #1c1812;
  font-size: 24rpx;
  font-weight: 800;
}
.commute-label {
  margin-top: 6rpx;
  color: var(--txt-3);
  font-size: 20rpx;
}
.nearby-info {
  padding: 20rpx;
  border-radius: 16rpx;
  background: var(--glass-solid);
  border: 1rpx solid var(--line);
}
.nearby-title {
  display: block;
  margin-bottom: 10rpx;
  color: #1c1812;
  font-size: 25rpx;
  font-weight: 800;
}
.nearby-item {
  display: flex;
  justify-content: space-between;
  padding: 9rpx 0;
  font-size: 23rpx;
}
.nearby-name {
  color: var(--txt-2);
}
.nearby-dist {
  color: var(--gold-2);
  font-weight: 800;
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
  color: var(--gold-2);
  border: 1rpx solid #e4d2a8;
}
.facility {
  background: var(--bg-2);
  color: var(--txt-2);
}
.description {
  display: block;
  color: var(--txt-2);
  font-size: 27rpx;
  line-height: 1.75;
}

/* 房东信息 .li */
.li.landlord {
  padding: 22rpx 24rpx;
}
.li.landlord .av {
  width: 80rpx;
  height: 80rpx;
  border-radius: 24rpx;
  background: var(--grad);
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 80rpx;
}
.li.landlord .c {
  flex: 1;
  min-width: 0;
}
.li.landlord .c .t {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--txt);
}
.li.landlord .c .s {
  font-size: 22rpx;
  color: var(--txt-3);
  margin-top: 6rpx;
}
.li.landlord .rt {
  color: var(--gold-2);
  font-size: 26rpx;
  font-weight: 700;
  flex: 0 0 auto;
}

.ai-eval {
  display: block;
  color: var(--gold-2);
  font-size: 25rpx;
  line-height: 1.7;
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
  align-items: center;
  gap: 14rpx;
  padding: 18rpx 24rpx;
  padding-bottom: calc(18rpx + env(safe-area-inset-bottom));
  background: var(--glass-solid);
  border-top: 1rpx solid var(--line);
  box-shadow: 0 -10rpx 28rpx rgba(60, 45, 20, 0.08);
}
.bb-fav {
  width: 78rpx;
  height: 78rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  background: var(--glass);
  border: 1rpx solid var(--line);
  flex: 0 0 auto;
}
.bb-fav.collected {
  background: #fff1f1;
  border-color: #f0c9c9;
}
.bb-price {
  flex: 0 0 auto;
  display: flex;
  align-items: baseline;
  color: var(--gold-2);
  font-size: 36rpx;
  font-weight: 800;
}
.bb-price .sm {
  font-size: 20rpx;
  color: var(--txt-3);
  font-weight: 400;
  margin-left: 2rpx;
}
.btns {
  flex: 1;
  display: flex;
  gap: 12rpx;
}
.btns .ghost,
.btns .gold {
  flex: 1;
  height: 78rpx;
  line-height: 78rpx;
  text-align: center;
  border-radius: 16rpx;
  font-size: 27rpx;
  font-weight: 800;
}
.btns .ghost {
  background: var(--glass);
  border: 1rpx solid var(--line);
  color: var(--txt);
}
.btns .gold {
  background: var(--grad);
  color: #2a2113;
  box-shadow: 0 12rpx 24rpx rgba(176, 138, 58, 0.2);
}
.bb-fav:active,
.btns .ghost:active,
.btns .gold:active {
  transform: scale(0.98);
}
</style>
