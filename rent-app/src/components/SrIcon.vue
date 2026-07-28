<template>
  <view class="sr-icon" :style="boxStyle" />
</template>

<script setup lang="ts">
import { computed } from 'vue'

/**
 * SrIcon —— 香槟金线条图标（对齐 docs/ui-prototype/*.html 的 ICON 表）
 * 小程序不支持内联 <svg>，改用 base64 SVG data-URI 作为 background-image，
 * 在微信真机/开发者工具均可稳定渲染。
 */
const props = withDefaults(
  defineProps<{
    name: string
    size?: number
    color?: string
  }>(),
  { size: 22, color: '#b08a3a' },
)

// 与原型 ICON 表逐字对应（线稿用 stroke；wechat 用 fill）
const PATHS: Record<string, string> = {
  home: '<path d="M3 11l9-7 9 7"/><path d="M5 10v9h14v-9"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M21 21l-4-4"/>',
  msg: '<path d="M4 5h16v11H8l-4 4z"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 4-6 8-6s8 2 8 6"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  cal: '<rect x="4" y="5" width="16" height="15" rx="2"/><path d="M4 9h16M8 3v4M16 3v4"/>',
  appt: '<rect x="4" y="5" width="16" height="15" rx="2"/><path d="M4 9h16M8 3v4M16 3v4"/>',
  users: '<circle cx="9" cy="8" r="3.5"/><path d="M3 20c0-3 3-5 6-5s6 2 6 5"/><path d="M16 6a3 3 0 0 1 0 6"/><path d="M18 20c0-2-1-4-3-5"/>',
  building: '<rect x="6" y="3" width="12" height="18" rx="1.5"/><path d="M9 7h.01M15 7h.01M9 11h.01M15 11h.01M9 15h.01M15 15h.01"/>',
  map: '<path d="M9 4 3 6v14l6-2 6 2 6-2V4l-6 2-6-2z"/><path d="M9 4v14M15 6v14"/>',
  heart: '<path d="M12 20s-7-4.5-9.5-9C1 8 2.5 4 6 4c2 0 3.2 1.2 4 2.3C10.8 5.2 12 4 14 4c3.5 0 5 4 3.5 7-2.5 4.5-9.5 9-9.5 9z"/>',
  star: '<path d="M12 4l2.3 4.7 5.2.8-4.7 4.6.9 5.1L12 16.9 7.4 18.2l.9-5.1L4.5 9.5l5.2-.8z"/>',
  doc: '<path d="M7 3h7l4 4v14H7z"/><path d="M14 3v4h4"/><path d="M10 12h6M10 15h6"/>',
  bill: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M3 10h18M7 14h4"/>',
  receipt: '<path d="M6 3h12v18l-2-1.5L14 21l-2-1.5L10 21l-2-1.5L6 21z"/><path d="M9 8h6M9 12h6"/>',
  gear: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  help: '<circle cx="12" cy="12" r="9"/><path d="M9.5 9a2.5 2.5 0 0 1 4.5 1.5c0 1.5-2 2-2 3.5"/><path d="M12 17h.01"/>',
  bell: '<path d="M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6z"/><path d="M10 20a2 2 0 0 0 4 0"/>',
  ai: '<path d="M12 3l1.8 4.7L18.5 9l-4.7 1.8L12 15l-1.8-4.2L5.5 9l4.7-1.3z"/><path d="M18 14l.9 2.3L21 17l-2.1.8L18 20l-.9-2.2L15 17l2.1-.7z"/>',
  chev: '<path d="M15 5l-7 7 7 7"/>',
  'chev-left': '<path d="M9 5l7 7-7 7"/>',
  'chev-right': '<path d="M15 5l-7 7 7 7"/>',
  check: '<path d="M5 13l4 4L19 7"/>',
  pin: '<path d="M12 21s-6-5.5-6-10a6 6 0 0 1 12 0c0 4.5-6 10-6 10z"/><circle cx="12" cy="11" r="2"/>',
  send: '<path d="M5 12l14-7-5 14-3-5z"/>',
  more: '<circle cx="5" cy="12" r="1.6"/><circle cx="12" cy="12" r="1.6"/><circle cx="19" cy="12" r="1.6"/>',
  close: '<path d="M6 6l12 12M18 6L6 18"/>',
  trend: '<path d="M4 19V5M4 19h16M8 15l3-4 3 2 4-6"/>',
  eye: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  eyeoff: '<path d="M2 12s3.5-7 10-7c1.7 0 3.2.4 4.6 1.1M22 12s-3.5 7-10 7c-1.7 0-3.2-.4-4.6-1.1"/><path d="M3 3l18 18"/><path d="M9.7 9.7a3 3 0 0 0 4.2 4.2"/>',
  lock: '<rect x="5" y="11" width="14" height="9" rx="2"/><path d="M8 11V8a4 4 0 0 1 8 0v3"/>',
  phone: '<path d="M5 4h3l2 5-2 1a12 12 0 0 0 5 5l1-2 5 2v3a2 2 0 0 1-2 2A16 16 0 0 1 3 6a2 2 0 0 1 2-2z"/>',
  wechat:
    // 官方微信 Logo（单色双气泡字形，Simple Icons 路径，viewBox 0 0 24 24）
    '<path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.565l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .545-.027.811-.05-.857-2.578-.401-4.773 1.273-6.438 1.636-1.636 3.982-2.53 6.56-2.53.234 0 .464.011.692.026-.643-3.393-3.868-5.985-7.617-5.985zM5.883 6.183a1.07 1.07 0 1 1 0 2.14 1.07 1.07 0 0 1 0-2.14zm5.724 0a1.07 1.07 0 1 1 0 2.14 1.07 1.07 0 0 1 0 2.14z"/><path d="M24 15.14c0-3.585-3.447-6.492-7.704-6.492-4.258 0-7.705 2.907-7.705 6.492 0 3.585 3.447 6.492 7.705 6.492.73 0 1.439-.1 2.097-.285a.566.566 0 0 1 .465.065l1.55.906a.28.28 0 0 0 .14.04c.122 0 .22-.099.22-.22 0-.06-.023-.12-.038-.177l-.366-1.396a.568.568 0 0 1-.196-.506c.922-1.26 1.535-2.806 1.535-4.51zM17.072 14.13a.872.872 0 1 1 0-1.744.872.872 0 0 1 0 1.744zm-3.812 0a.872.872 0 1 1 0-1.744.872.872 0 0 1 0 1.744z"/>',
}

const FILLED = new Set(['wechat'])

function buildUri(): string {
  const inner = PATHS[props.name] || ''
  const isFilled = FILLED.has(props.name)
  const fill = isFilled ? props.color : 'none'
  // 填充图标（如微信 Logo）不加描边，避免同色描边让细节变粗
  const stroke = isFilled ? 'none' : props.color
  const svg =
    `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='${fill}' ` +
    `stroke='${stroke}' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'>${inner}</svg>`
  return 'data:image/svg+xml,' + encodeURIComponent(svg)
}

const boxStyle = computed(() => ({
  width: props.size + 'rpx',
  height: props.size + 'rpx',
  backgroundImage: `url("${buildUri()}")`,
  backgroundSize: '100% 100%',
  backgroundRepeat: 'no-repeat',
  flex: '0 0 auto',
}))
</script>

<style scoped>
.sr-icon {
  display: inline-block;
}
</style>
