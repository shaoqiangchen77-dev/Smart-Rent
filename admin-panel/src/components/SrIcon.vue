<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  name: string
  size?: number | string
}>()

// 线性 SVG 图标集（Lucide/Feather 风格，对齐 docs 设计语言）
const ICONS: Record<string, string> = {
  home: 'M3 11l9-8 9 8|M5 10v10a1 1 0 001 1h3v-6h4v6h3a1 1 0 001-1V10',
  house: 'M3 11l9-8 9 8|M5 10v10a1 1 0 001 1h3v-6h4v6h3a1 1 0 001-1V10',
  search: 'M11 19a8 8 0 100-16 8 8 0 000 16z|M21 21l-4.3-4.3',
  message: 'M21 11.5a8.38 8.38 0 01-8.5 8.5 8.5 8.5 0 01-3.9-.9L3 21l1.9-5.6A8.5 8.5 0 1121 11.5z',
  chat: 'M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z',
  user: 'M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2|M12 11a4 4 0 100-8 4 4 0 000 8z',
  lock: 'M19 11H5a2 2 0 00-2 2v6a2 2 0 002 2h14a2 2 0 002-2v-6a2 2 0 00-2-2z|M7 11V7a5 5 0 0110 0v4',
  users: 'M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2|M9 11a4 4 0 100-8 4 4 0 000 8z|M23 21v-2a4 4 0 00-3-3.87|M16 3.13a4 4 0 010 7.75',
  dashboard: 'M3 12h4v9H3z|M10 3h4v18h-4z|M17 8h4v13h-4z',
  grid: 'M3 3h7v7H3z|M14 3h7v7h-7z|M14 14h7v7h-7z|M3 14h7v7H3z',
  check: 'M20 6L9 17l-5-5',
  calendar: 'M3 9h18M7 3v4M17 3v4M4 5h16a1 1 0 011 1v13a1 1 0 01-1 1H4a1 1 0 01-1-1V6a1 1 0 011-1z',
  doc: 'M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6z|M14 2v6h6',
  deal: 'M16 3h5v5|M21 3l-7 7|M8 21H3v-5|M3 21l7-7',
  bell: 'M18 8a6 6 0 10-12 0c0 7-3 9-3 9h18s-3-2-3-9|M13.7 21a2 2 0 01-3.4 0',
  settings: 'M12 15a3 3 0 100-6 3 3 0 000 6z|M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 11-2.83 2.83l-.06-.06a1.65 1.65 0 00-2.92.99 2 2 0 01-3.64 0 1.65 1.65 0 00-2.92-.99l-.06.06a2 2 0 11-2.83-2.83l.06-.06A1.65 1.65 0 004.6 15a2 2 0 010-3.3 1.65 1.65 0 00-.99-2.92 2 2 0 010-3.64 1.65 1.65 0 00.99-2.92l.06.06a2 2 0 112.83-2.83l.06.06A1.65 1.65 0 009 4.6a2 2 0 013.3 0 1.65 1.65 0 002.92.99 2 2 0 012.83 0 2 2 0 010 3.64 1.65 1.65 0 00.99 2.92 2 2 0 010 3.3z',
  plus: 'M12 5v14|M5 12h14',
  eye: 'M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z|M12 9a3 3 0 100 6 3 3 0 000-6z',
  eyeoff: 'M9.9 4.2A9.8 9.8 0 0112 4c7 0 10 8 10 8a18 18 0 01-2.2 3.2M6.1 6.1A18 18 0 002 12s3 8 10 8a9.6 9.6 0 004.1-.9|M9.5 9.5a3 3 0 004.2 4.2|M3 3l18 18',
  refresh: 'M23 4v6h-6|M1 20v-6h6|M3.5 9a9 9 0 0114.8-3.4L23 10|M1 14l4.7 4.4A9 9 0 0020.5 15',
  download: 'M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4|M7 10l5 5 5-5|M12 15V3',
  edit: 'M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7|M18.5 2.5a2.12 2.12 0 013 3L12 15l-4 1 1-4 9.5-9.5z',
  delete: 'M3 6h18|M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2',
  more: 'M12 13a1 1 0 100-2 1 1 0 000 2z|M19 13a1 1 0 100-2 1 1 0 000 2z|M5 13a1 1 0 100-2 1 1 0 000 2z',
  logout: 'M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4|M16 17l5-5-5-5|M21 12H9',
  spark: 'M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9z',
  star: 'M12 2l3 6.3 6.9 1-5 4.9 1.2 6.8L12 17.8 5.9 21l1.2-6.8-5-4.9 6.9-1z',
  location: 'M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z|M12 13a3 3 0 100-6 3 3 0 000 6z',
  filter: 'M22 3H2l8 9.5V19l4 2v-8.5z',
  trend: 'M3 17l6-6 4 4 8-8',
}

const d = computed(() => ICONS[props.name] || '')
const sz = computed(() =>
  props.size ? (typeof props.size === 'number' ? props.size + 'px' : props.size) : '18px'
)
</script>

<template>
  <svg
    class="sr-icon"
    :width="sz"
    :height="sz"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.8"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <path v-for="(p, i) in d.split('|')" :key="i" :d="p" />
  </svg>
</template>
