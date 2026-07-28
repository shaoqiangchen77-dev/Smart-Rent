<template>
  <view class="nav-wrap">
    <web-view v-if="mapUrl" :src="mapUrl" @error="onError" />
    <view v-else class="nav-fallback">
      <text class="fallback-text">暂无坐标信息，无法导航</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const lat = ref('')
const lng = ref('')
const fromlat = ref('')
const fromlng = ref('')
const name = ref('')
const address = ref('')
const mapUrl = ref('')

onLoad((opts: any) => {
  lat.value = opts.lat || ''
  lng.value = opts.lng || ''
  fromlat.value = opts.fromlat || ''
  fromlng.value = opts.fromlng || ''
  name.value = opts.name ? decodeURIComponent(opts.name) : ''
  address.value = opts.address ? decodeURIComponent(opts.address) : ''

  if (lat.value && lng.value) {
    const to = encodeURIComponent(name.value || address.value || '目的地')
    let url = `https://apis.map.qq.com/uri/v1/routeplan?type=drive&to=${to}&tocoord=${lat.value},${lng.value}&referer=smartrent`
    if (fromlat.value && fromlng.value) {
      url += `&from=${encodeURIComponent('我的位置')}&fromcoord=${fromlat.value},${fromlng.value}`
    }
    mapUrl.value = url
  }
})

function onError() {
  uni.showToast({ title: '地图加载失败，请检查网络', icon: 'none' })
}
</script>

<style scoped>
.nav-wrap {
  width: 100%;
  height: 100vh;
}
.nav-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
.fallback-text {
  color: #999;
  font-size: 28rpx;
}
</style>
