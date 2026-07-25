<template>
  <div class="login-page" ref="page">
    <!-- 左侧大图 Hero -->
    <section class="login-hero" ref="heroEl">
      <div class="hero-slides" ref="heroSlides">
        <div class="hero-slide" :class="{ active: heroIdx === 0 }" style="background-image: url('/assets/hero1.png')"></div>
        <div class="hero-slide" :class="{ active: heroIdx === 1 }" style="background-image: url('/assets/hero2.png')"></div>
        <div class="hero-slide" :class="{ active: heroIdx === 2 }" style="background-image: url('/assets/hero3.png')"></div>
      </div>
      <div class="hero-overlay"></div>
      <div class="hero-cap">智居 ZHIGU · 智能租房平台</div>
      <div class="hero-content">
        <span class="hero-badge">✦ 运营后台 · OPERATION CONSOLE</span>
        <h1>让租房<br />更<em>智能</em>、更<em>安心</em></h1>
        <p>统一管理房源、租户与交易，结合 AI 智能匹配与实时风控，为运营团队提供一站式决策中枢。</p>
        <div class="hero-dots">
          <i v-for="n in 3" :key="n" :class="{ on: heroIdx === n - 1 }" @click="setHero(n - 1)"></i>
        </div>
      </div>
    </section>

    <!-- 右侧登录面板 -->
    <section class="login-panel">
      <div class="login-card" ref="cardRef">
        <div class="brand">
          <div class="mark">智</div>
          <div>
            <h2>智居运营后台</h2>
            <small>RENT OPERATION CONSOLE</small>
          </div>
        </div>
        <div class="welcome">欢迎回来</div>
        <div class="sub">请输入管理员账号以继续</div>

        <form @submit.prevent="onSubmit">
          <div class="field">
            <label>管理员账号</label>
            <div class="input-box" :class="{ focus: focusUser }">
              <span class="ic">◈</span>
              <input
                v-model="form.username"
                placeholder="如 admin"
                autocomplete="off"
                @focus="focusUser = true"
                @blur="focusUser = false"
                @keyup.enter="onSubmit"
              />
            </div>
          </div>

          <div class="field">
            <label>登录密码</label>
            <div class="input-box" :class="{ focus: focusPwd }">
              <span class="ic">⚷</span>
              <input
                v-model="form.password"
                :type="showPwd ? 'text' : 'password'"
                placeholder="请输入密码"
                @focus="focusPwd = true"
                @blur="focusPwd = false"
                @keyup.enter="onSubmit"
              />
              <button
                type="button"
                class="pw-toggle"
                :class="{ on: showPwd }"
                :title="showPwd ? '隐藏密码' : '显示密码'"
                @click="showPwd = !showPwd"
              >
                <svg v-if="showPwd" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
              </button>
            </div>
          </div>

          <div class="row">
            <label><input v-model="remember" type="checkbox" /> 记住我</label>
            <a href="#" onclick="return false">忘记密码？</a>
          </div>

          <button type="submit" class="btn-gold" :disabled="loading">
            <span class="shimmer"></span>
            <span>{{ loading ? '登 录 中…' : '登 录 →' }}</span>
          </button>
        </form>

        <div class="login-foot">受 Sa-Token 保护 · 登录即代表同意服务条款</div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const showPwd = ref(false)
const remember = ref(true)
const focusUser = ref(false)
const focusPwd = ref(false)
const heroIdx = ref(0)

const form = reactive({ username: 'admin', password: '123456' })

const heroEl = ref<HTMLElement>()
const heroSlides = ref<HTMLElement>()
const cardRef = ref<HTMLElement>()
let heroTimer: number | undefined

async function onSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form)
    if (!userStore.isAdmin) {
      ElMessage.error('仅管理员可登录后台')
      userStore.logout()
      return
    }
    router.push('/')
  } finally {
    loading.value = false
  }
}

function setHero(i: number) {
  if (heroTimer) {
    window.clearInterval(heroTimer)
    heroTimer = undefined
  }
  heroIdx.value = i
  startHeroTimer()
}

function startHeroTimer() {
  heroTimer = window.setInterval(() => {
    heroIdx.value = (heroIdx.value + 1) % 3
  }, 5000)
}

function onHeroMove(e: MouseEvent) {
  const el = heroEl.value
  const wrap = heroSlides.value
  if (!el || !wrap) return
  const r = el.getBoundingClientRect()
  const x = (e.clientX - r.left) / r.width - 0.5
  const y = (e.clientY - r.top) / r.height - 0.5
  wrap.style.transform = `scale(1.08) translate(${x * -22}px, ${y * -22}px)`
}

function onHeroLeave() {
  const wrap = heroSlides.value
  if (wrap) wrap.style.transform = 'scale(1.08) translate(0,0)'
}

function onCardMove(e: MouseEvent) {
  const card = cardRef.value
  if (!card) return
  const r = card.getBoundingClientRect()
  card.style.setProperty('--mx', e.clientX - r.left + 'px')
  card.style.setProperty('--my', e.clientY - r.top + 'px')
}

onMounted(() => {
  startHeroTimer()
  heroEl.value?.addEventListener('mousemove', onHeroMove)
  heroEl.value?.addEventListener('mouseleave', onHeroLeave)
  cardRef.value?.addEventListener('mousemove', onCardMove)
})

onBeforeUnmount(() => {
  if (heroTimer) window.clearInterval(heroTimer)
  heroEl.value?.removeEventListener('mousemove', onHeroMove)
  heroEl.value?.removeEventListener('mouseleave', onHeroLeave)
  cardRef.value?.removeEventListener('mousemove', onCardMove)
})
</script>

<style scoped>
.login-page {
  position: fixed;
  inset: 0;
  display: flex;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ---------- 左侧 Hero ---------- */
.login-hero {
  position: relative;
  flex: 1.25;
  overflow: hidden;
  background: #0b0e15;
}
.hero-slides {
  position: absolute;
  inset: -4%;
  transform: scale(1.08);
  transition: transform 0.25s ease-out;
  will-change: transform;
}
.hero-slide {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transition: opacity 1.3s ease;
}
.hero-slide.active {
  opacity: 1;
}
.hero-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background:
    linear-gradient(180deg, rgba(8, 11, 17, 0.15) 0%, rgba(8, 11, 17, 0.05) 38%, rgba(8, 11, 17, 0.78) 100%),
    linear-gradient(90deg, rgba(8, 11, 17, 0.35), transparent 60%);
}
.hero-cap {
  position: absolute;
  top: 6vh;
  right: 5vw;
  z-index: 2;
  color: rgba(245, 239, 225, 0.6);
  font-size: 12px;
  letter-spacing: 2px;
  text-align: right;
}
.hero-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  padding: 0 5vw 7vh;
  color: #f5efe1;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 15px;
  border-radius: 30px;
  border: 1px solid rgba(231, 207, 142, 0.4);
  background: rgba(231, 207, 142, 0.08);
  color: #e7cf8e;
  font-size: 12px;
  letter-spacing: 2px;
}
.hero-content h1 {
  font-size: min(44px, 4.4vw);
  line-height: 1.18;
  margin: 22px 0 16px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 4px 24px rgba(0, 0, 0, 0.4);
}
.hero-content h1 em {
  font-style: normal;
  background: linear-gradient(120deg, #e7cf8e, #c9a227);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.hero-content p {
  color: rgba(245, 239, 225, 0.78);
  font-size: 14.5px;
  max-width: 420px;
  line-height: 1.8;
}
.hero-dots {
  display: flex;
  gap: 9px;
  margin-top: 30px;
}
.hero-dots i {
  width: 30px;
  height: 4px;
  border-radius: 4px;
  background: rgba(245, 239, 225, 0.28);
  transition: 0.4s;
  cursor: pointer;
}
.hero-dots i.on {
  background: linear-gradient(135deg, #b08a3a, #d6c088);
  width: 46px;
}

/* ---------- 右侧面板：奶油背景 ---------- */
.login-panel {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f3ec;
  background-image:
    radial-gradient(120% 80% at 10% 10%, rgba(214, 192, 136, 0.12), transparent 50%),
    radial-gradient(90% 90% at 90% 90%, rgba(176, 138, 58, 0.06), transparent 40%);
  padding: 30px;
}

/* ---------- 深色半透明玻璃登录卡 ---------- */
.login-card {
  position: relative;
  width: 100%;
  max-width: 380px;
  padding: 38px 34px;
  border-radius: 24px;
  overflow: hidden;
  background: rgba(45, 40, 33, 0.78);
  backdrop-filter: blur(24px) saturate(140%);
  -webkit-backdrop-filter: blur(24px) saturate(140%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow:
    0 30px 70px -22px rgba(24, 18, 10, 0.55),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  color: #f5efe1;
  opacity: 0;
  transform: translateY(26px);
  animation: rise 0.9s cubic-bezier(0.2, 0.8, 0.2, 1) 0.25s forwards;
}
@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(26px);
  }
  to {
    opacity: 1;
    transform: none;
  }
}
.login-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 24px;
  pointer-events: none;
  z-index: 0;
  background: radial-gradient(360px circle at var(--mx, 50%) var(--my, 16%), rgba(214, 192, 136, 0.14), transparent 60%);
  transition: background 0.15s ease-out;
}
.login-card > * {
  position: relative;
  z-index: 1;
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 26px;
}
.brand .mark {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  background: linear-gradient(135deg, #b08a3a, #d6c088);
  display: grid;
  place-items: center;
  font-weight: 800;
  color: #2a2113;
  font-size: 20px;
  box-shadow: 0 8px 22px -8px rgba(168, 128, 40, 0.5);
}
.brand h2 {
  font-size: 17px;
  letter-spacing: 2px;
  color: #f5efe1;
}
.brand small {
  display: block;
  color: #d6c088;
  font-size: 10px;
  letter-spacing: 3px;
  margin-top: 2px;
}
.welcome {
  font-size: 22px;
  font-weight: 700;
  color: #f5efe1;
  margin-bottom: 6px;
}
.sub {
  color: rgba(245, 239, 225, 0.55);
  font-size: 13px;
  margin-bottom: 26px;
}
.field {
  position: relative;
  margin-bottom: 16px;
}
.field label {
  display: block;
  font-size: 12px;
  color: rgba(245, 239, 225, 0.65);
  letter-spacing: 1px;
  margin: 0 0 7px 2px;
}
.input-box {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 13px;
  padding: 0 14px;
  transition: 0.25s;
}
.input-box.focus {
  border-color: rgba(214, 192, 136, 0.75);
  background: rgba(255, 255, 255, 0.12);
  box-shadow: 0 0 0 4px rgba(214, 192, 136, 0.12);
}
.input-box .ic {
  color: #d6c088;
  flex: none;
  font-size: 15px;
  line-height: 1;
}
.field input {
  flex: 1;
  width: 100%;
  padding: 14px 0;
  background: none;
  border: none;
  outline: none;
  color: #f5efe1;
  font-size: 14px;
}
.field input::placeholder {
  color: rgba(245, 239, 225, 0.38);
}
.pw-toggle {
  width: 34px;
  height: 34px;
  flex: none;
  border: none;
  background: none;
  color: rgba(245, 239, 225, 0.5);
  cursor: pointer;
  display: grid;
  place-items: center;
  border-radius: 9px;
  transition: 0.2s;
}
.pw-toggle:hover {
  color: #d6c088;
  background: rgba(214, 192, 136, 0.12);
}
.pw-toggle.on {
  color: #d6c088;
}
.pw-toggle svg {
  width: 19px;
  height: 19px;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 22px;
  font-size: 12px;
  color: rgba(245, 239, 225, 0.65);
}
.row label {
  display: flex;
  gap: 7px;
  align-items: center;
  cursor: pointer;
}
.row input[type='checkbox'] {
  width: 14px;
  height: 14px;
  accent-color: #b08a3a;
}
.row a {
  color: #d6c088;
  text-decoration: none;
}
.btn-gold {
  width: 100%;
  padding: 15px;
  border-radius: 13px;
  background: linear-gradient(135deg, #b08a3a, #d6c088);
  color: #2a2113;
  font-weight: 700;
  letter-spacing: 3px;
  font-size: 14px;
  transition: 0.25s;
  position: relative;
  overflow: hidden;
  border: none;
  cursor: pointer;
}
.btn-gold:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 16px 34px -12px rgba(168, 128, 40, 0.6);
}
.btn-gold:disabled {
  opacity: 0.75;
  cursor: default;
  transform: none;
}
.btn-gold .shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(110deg, transparent 30%, rgba(255, 255, 255, 0.55) 50%, transparent 70%);
  transform: translateX(-120%);
  animation: shim 2.6s ease-in-out infinite;
}
@keyframes shim {
  0%,
  60% {
    transform: translateX(-120%);
  }
  100% {
    transform: translateX(120%);
  }
}
.login-foot {
  margin-top: 18px;
  text-align: center;
  color: rgba(245, 239, 225, 0.4);
  font-size: 11px;
  letter-spacing: 1px;
}

@media (max-width: 880px) {
  .login-hero {
    display: none;
  }
  .login-panel {
    flex: 1;
  }
}
</style>
