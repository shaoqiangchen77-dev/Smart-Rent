import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import App from './App.vue'
import router from './router'
import SrIcon from './components/SrIcon.vue'
import './style.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.component('SrIcon', SrIcon)
app.mount('#app')

// Card Spotlight：所有 .sr-spot 元素跟随鼠标聚光（docs 设计语言）
document.addEventListener('mousemove', (e: MouseEvent) => {
  const t = e.target as HTMLElement | null
  const el = t?.closest?.('.sr-spot') as HTMLElement | null
  if (el) {
    const r = el.getBoundingClientRect()
    el.style.setProperty('--mx', e.clientX - r.left + 'px')
    el.style.setProperty('--my', e.clientY - r.top + 'px')
  }
})
