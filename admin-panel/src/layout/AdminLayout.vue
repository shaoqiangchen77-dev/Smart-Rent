<template>
  <div class="shell">
    <div class="app-glow"></div>

    <!-- 一级图标 rail -->
    <aside class="rail">
      <div class="logo">智</div>
      <button
        v-for="g in NAV_GROUPS"
        :key="g.name"
        class="nav-i"
        :class="{ on: activeGroup === g.name }"
        :title="g.name"
        @click="onGroupClick(g)"
        v-html="svg(g.icon, 20)"
      ></button>
      <el-dropdown trigger="click" @command="onCommand">
        <div class="me" :title="userStore.userInfo?.username || '管理员'">
          {{ (userStore.userInfo?.username || 'A').slice(0, 1).toUpperCase() }}
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>{{ userStore.userInfo?.username || '管理员' }}</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </aside>

    <!-- 二级菜单 side -->
    <aside class="side">
      <h2>{{ activeGroup }} 中 心</h2>
      <nav class="menu">
        <button
          v-for="m in currentMenus"
          :key="m.path"
          :class="{ on: route.path === m.path }"
          @click="router.push(m.path)"
        >
          <span class="mi" v-html="svg(m.icon, 17)"></span>
          <span>{{ m.title }}</span>
        </button>
      </nav>
    </aside>

    <!-- 主区 -->
    <main class="main">
      <header class="topbar">
        <div>
          <div class="crumb">{{ activeGroup }}中心 / <b>{{ route.meta.title }}</b></div>
        </div>
        <div class="right">
          <div class="search">
            <span class="s-ic" v-html="svg('search', 16)"></span>
            <input v-model="keyword" placeholder="搜索房源 / 用户 / 订单…" @keyup.enter="onSearch" />
          </div>
          <button class="icon-btn" title="通知" @click="router.push('/message')">
            <span v-html="svg('bell', 18)"></span>
            <span v-if="unread > 0" class="badge">{{ unread > 99 ? '99+' : unread }}</span>
          </button>
          <button class="icon-btn" :title="isDark ? '切换日间模式' : '切换夜间模式'" @click="toggleTheme">
            <span v-html="isDark ? SUN : MOON"></span>
          </button>
        </div>
      </header>
      <div class="content view" :key="route.path">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getMessageList } from '@/api/message'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')
const unread = ref(0)

/* ---------- 原型同款线性图标 ---------- */
const ICON: Record<string, string> = {
  dashboard: '<path d="M3 13h8V3H3zM13 21h8V11h-8zM13 3v6h8V3zM3 21h8v-6H3z"/>',
  house: '<path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-6 8-6s8 2 8 6"/>',
  deal: '<path d="M3 7h18v12H3z"/><path d="M3 11h18"/><path d="M7 15h4"/>',
  msg: '<path d="M4 5h16v11H8l-4 4z"/>',
  grid: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
  check: '<path d="M20 6L9 17l-5-5"/>',
  users: '<circle cx="9" cy="8" r="3.2"/><path d="M2.5 20c0-3.3 2.9-5 6.5-5s6.5 1.7 6.5 5"/><path d="M16 5.2A3.2 3.2 0 0 1 16 11M21.5 20c0-2.6-1.8-4.2-4.5-4.7"/>',
  calendar: '<rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4"/>',
  doc: '<path d="M6 2h8l4 4v16H6z"/><path d="M14 2v4h4M9 13h6M9 17h6"/>',
  chat: '<path d="M21 12a8 8 0 0 1-11.5 7.2L3 21l1.8-6.5A8 8 0 1 1 21 12z"/>',
  home2: '<path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M21 21l-4-4"/>',
  bell: '<path d="M18 8a6 6 0 1 0-12 0c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.7 21a2 2 0 0 1-3.4 0"/>',
}
function svg(name: string, size = 18) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${ICON[name] || ''}</svg>`
}
const SUN =
  '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4.2"/><path d="M12 2.5v2M12 19.5v2M2.5 12h2M19.5 12h2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>'
const MOON =
  '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8z"/></svg>'

/* ---------- 导航结构（原型 MENUS） ---------- */
interface MenuItem {
  title: string
  path: string
  icon: string
}
interface NavGroup {
  name: string
  icon: string
  menus: MenuItem[]
}
const NAV_GROUPS: NavGroup[] = [
  { name: '运营', icon: 'dashboard', menus: [{ title: '数据看板', path: '/dashboard', icon: 'grid' }] },
  {
    name: '房源',
    icon: 'house',
    menus: [
      { title: '房源列表', path: '/house', icon: 'home2' },
      { title: '房源审核', path: '/house/audit', icon: 'check' },
    ],
  },
  { name: '用户', icon: 'user', menus: [{ title: '用户列表', path: '/user', icon: 'users' }] },
  {
    name: '交易',
    icon: 'deal',
    menus: [
      { title: '预约管理', path: '/order', icon: 'calendar' },
      { title: '合同管理', path: '/contract', icon: 'doc' },
    ],
  },
  { name: '消息', icon: 'msg', menus: [{ title: '消息列表', path: '/message', icon: 'chat' }] },
]

const activeGroup = computed(() => {
  const g = NAV_GROUPS.find((x) => x.menus.some((m) => m.path === route.path))
  return g?.name || '运营'
})
const currentMenus = computed(() => NAV_GROUPS.find((g) => g.name === activeGroup.value)?.menus || [])

function onGroupClick(g: NavGroup) {
  router.push(g.menus[0].path)
}

function onSearch() {
  if (keyword.value.trim()) {
    router.push({ path: '/house', query: { kw: keyword.value.trim() } })
  }
}

/* ---------- 日 / 夜主题 ---------- */
const isDark = ref(false)
function applyTheme(dark: boolean) {
  isDark.value = dark
  document.documentElement.dataset.theme = dark ? 'dark' : ''
  try {
    localStorage.setItem('admin-theme', dark ? 'dark' : 'light')
  } catch {
    /* ignore */
  }
}
function toggleTheme() {
  applyTheme(!isDark.value)
}

function onCommand(cmd: string) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

onMounted(async () => {
  userStore.fetchUserInfo()
  try {
    applyTheme(localStorage.getItem('admin-theme') === 'dark')
  } catch {
    /* ignore */
  }
  try {
    const res: any = await getMessageList({ page: 1, size: 50 })
    const list = Array.isArray(res.data) ? res.data : res.data?.records || []
    unread.value = list.filter((m: any) => m.status === 0 || m.isRead === 0).length
  } catch {
    /* API not ready */
  }
})
</script>

<style scoped>
.shell {
  height: 100vh;
  position: relative;
  background: var(--bg);
}
.app-glow {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(60vw 60vw at 88% -10%, rgba(212, 175, 55, 0.1), transparent 60%),
    radial-gradient(50vw 50vw at -5% 100%, rgba(184, 128, 42, 0.08), transparent 55%);
}

/* rail */
.rail {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 84px;
  z-index: 20;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 18px 0;
  background: var(--glass);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border-right: 1px solid var(--line);
}
.rail .logo {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  background: var(--grad-gold);
  display: grid;
  place-items: center;
  font-weight: 800;
  color: #2a2113;
  margin-bottom: 26px;
  box-shadow: 0 8px 22px -8px rgba(168, 128, 40, 0.6);
}
.nav-i {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  color: var(--txt-3);
  margin-bottom: 10px;
  transition: 0.25s;
  position: relative;
  border: none;
  background: none;
  cursor: pointer;
}
.nav-i:hover {
  color: var(--gold-2);
  background: rgba(168, 128, 40, 0.1);
}
.nav-i.on {
  color: var(--gold-2);
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.22), rgba(184, 128, 42, 0.1));
  box-shadow: 0 8px 20px -10px rgba(168, 128, 40, 0.5);
}
.nav-i.on::before {
  content: '';
  position: absolute;
  left: -18px;
  top: 14px;
  bottom: 14px;
  width: 3px;
  border-radius: 3px;
  background: var(--grad-gold);
}
.rail .me {
  margin-top: auto;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: var(--grad-gold);
  display: grid;
  place-items: center;
  color: #2a2113;
  font-size: 13px;
  font-weight: 700;
  box-shadow: 0 6px 16px -8px rgba(168, 128, 40, 0.6);
  cursor: pointer;
}

/* side */
.side {
  position: fixed;
  left: 84px;
  top: 0;
  bottom: 0;
  width: 218px;
  background: var(--bg-2);
  border-right: 1px solid var(--line);
  padding: 26px 16px;
  z-index: 18;
}
.side h2 {
  font-size: 12px;
  letter-spacing: 5px;
  color: var(--txt-3);
  margin: 0 8px 18px;
  font-weight: 600;
}
.menu {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.menu button {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 14px;
  border-radius: 11px;
  color: var(--txt-2);
  font-size: 13.5px;
  transition: 0.2s;
  position: relative;
  border: none;
  background: none;
  cursor: pointer;
  font-family: inherit;
  text-align: left;
}
.menu button .mi {
  color: var(--txt-3);
  transition: 0.2s;
  flex-shrink: 0;
  display: inline-flex;
}
.menu button:hover {
  background: var(--surface);
  color: var(--txt);
}
.menu button:hover .mi {
  color: var(--gold-2);
}
.menu button.on {
  background: linear-gradient(90deg, rgba(168, 128, 40, 0.16), transparent);
  color: var(--gold-2);
  font-weight: 600;
}
.menu button.on .mi {
  color: var(--gold-2);
}

/* main */
.main {
  margin-left: 302px;
  height: 100vh;
  overflow-y: auto;
  padding: 0 36px 60px;
  position: relative;
  z-index: 1;
}
.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0 18px;
  background: linear-gradient(180deg, var(--bg) 68%, transparent);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
}
.crumb {
  font-size: 12px;
  color: var(--txt-3);
  letter-spacing: 1px;
}
.crumb b {
  color: var(--txt);
  font-weight: 600;
}
.topbar .right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.search {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--glass);
  border: 1px solid var(--line);
  padding: 9px 14px;
  border-radius: 12px;
  color: var(--txt-3);
  font-size: 13px;
  min-width: 210px;
  transition: 0.2s;
}
.search:focus-within {
  border-color: var(--line-strong);
  box-shadow: 0 0 0 4px rgba(184, 128, 42, 0.08);
}
.search .s-ic {
  display: inline-flex;
}
.search input {
  background: none;
  border: none;
  color: var(--txt);
  outline: none;
  font-size: 13px;
  width: 100%;
  font-family: inherit;
}
.icon-btn {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: var(--glass);
  border: 1px solid var(--line);
  display: grid;
  place-items: center;
  color: var(--gold-2);
  transition: 0.2s;
  cursor: pointer;
}
.icon-btn:hover {
  background: var(--surface-2);
  border-color: var(--line-strong);
  transform: translateY(-1px);
}
.icon-btn .badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: var(--bad);
  color: #fff;
  font-size: 10px;
  min-width: 16px;
  height: 16px;
  border-radius: 9px;
  display: grid;
  place-items: center;
  padding: 0 4px;
}
.content {
  position: relative;
}
</style>
