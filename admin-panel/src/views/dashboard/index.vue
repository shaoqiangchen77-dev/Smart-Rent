<template>
  <div class="dashboard">
    <!-- 问候条 -->
    <div class="hero-band">
      <div class="hb-l">
        <div class="hi">✦ {{ greeting }}，{{ userStore.userInfo?.username || '管理员' }}</div>
        <h2 class="serif">运营总览 · 平台今日脉搏</h2>
        <p>数据实时刷新 · 最近同步 {{ syncTime }}</p>
      </div>
      <div class="hb-r">
        <button class="sr-btn" @click="reload">↻ 刷新数据</button>
        <button class="sr-btn primary" @click="router.push('/house/audit')">✓ 去审核</button>
      </div>
    </div>

    <!-- bento KPI -->
    <div class="bento">
      <div class="tile sr-spot" v-for="k in kpis" :key="k.label">
        <div class="k"><span class="ti" v-html="k.icon"></span>{{ k.label }}</div>
        <div class="v mono">{{ k.value }}</div>
        <div class="delta" :class="k.deltaClass">{{ k.delta }}</div>
        <svg class="spark" width="64" height="26">
          <polyline :points="k.spark" fill="none" :stroke="k.color" stroke-width="1.8" opacity="0.75" />
        </svg>
      </div>
    </div>

    <!-- 趋势 + 分布 -->
    <div class="grid-2">
      <div class="panel sr-spot">
        <div class="panel-h">
          <h3>用户 & 房源增长趋势</h3>
          <div class="seg">
            <button :class="{ on: rangeDays === 7 }" @click="setRange(7)">7天</button>
            <button :class="{ on: rangeDays === 30 }" @click="setRange(30)">30天</button>
          </div>
        </div>
        <div ref="trendRef" class="trend-chart"></div>
      </div>
      <div class="panel sr-spot">
        <div class="panel-h"><h3>房源类型分布</h3></div>
        <div class="donut-wrap">
          <div class="donut-c">
            <div ref="donutRef" style="width: 130px; height: 130px"></div>
            <div class="ctr">
              <b class="mono">{{ stats.houseCount }}</b><span>套房源</span>
            </div>
          </div>
          <div class="donut-leg">
            <div class="li" v-for="d in donutData" :key="d.name">
              <span><i :style="{ background: d.color }"></i>{{ d.name }}</span>
              <b class="mono">{{ d.value }}</b>
            </div>
            <div v-if="donutData.length === 0" class="li empty">暂无房源数据</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 精选房源 -->
    <div class="panel sr-spot" style="margin-bottom: 18px">
      <div class="panel-h">
        <h3>精选房源 · 实景</h3>
        <button class="chip" @click="router.push('/house')">查看全部 →</button>
      </div>
      <div class="feat-grid">
        <div class="feat-card" v-for="(h, i) in featHouses" :key="h.id" @click="router.push('/house')">
          <img :src="FEAT_IMGS[i % FEAT_IMGS.length]" :alt="h.title" />
          <span class="price">¥{{ h.price }}/月</span>
          <div class="ov"></div>
          <div class="txt">
            <b>{{ h.title }}</b>
            <span>{{ h.rentType || h.houseType }} · {{ h.areaSize ? h.areaSize + '㎡' : h.area }}</span>
          </div>
        </div>
        <el-empty v-if="featHouses.length === 0" description="暂无房源" style="grid-column: 1 / -1" />
      </div>
    </div>

    <!-- 预约 & 待审核 -->
    <div class="grid-2">
      <div class="panel sr-spot">
        <div class="panel-h">
          <h3>最新看房预约</h3>
          <button class="chip" @click="router.push('/order')">查看全部 →</button>
        </div>
        <div class="feed">
          <div class="it" v-for="a in latestAppointments" :key="a.id">
            <div class="av">约</div>
            <div style="flex: 1">
              <div class="t">租客 #{{ a.userId }} 预约房源 #{{ a.houseId }}</div>
              <div class="s">{{ a.viewingTime || a.createTime }}</div>
            </div>
            <span class="tag" :class="apptTagClass[a.status] || 't-mut'">{{ apptStatusMap[a.status] || '—' }}</span>
          </div>
          <el-empty v-if="latestAppointments.length === 0" description="暂无预约" :image-size="60" />
        </div>
      </div>
      <div class="panel sr-spot">
        <div class="panel-h">
          <h3>待审核房源</h3>
          <button class="chip" @click="router.push('/house/audit')">去审核 →</button>
        </div>
        <div class="feed">
          <div class="it" v-for="h in pendingHouses" :key="h.id">
            <div class="av">☉</div>
            <div style="flex: 1">
              <div class="t">{{ h.title }}</div>
              <div class="s">{{ h.area }} · ¥{{ h.price }}/月</div>
            </div>
            <span class="tag t-warn">待审核</span>
          </div>
          <el-empty v-if="pendingHouses.length === 0" description="暂无待审核房源" :image-size="60" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getDashboardStats, getUserTrend, getHouseTrend, type DashboardStats } from '@/api/dashboard'
import { getHouseList, type HouseItem } from '@/api/house'
import { getAppointmentList } from '@/api/order'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()

const stats = ref<DashboardStats>({
  userCount: 0,
  houseCount: 0,
  orderCount: 0,
  todayNewUsers: 0,
  todayNewHouses: 0,
})
const featHouses = ref<HouseItem[]>([])
const pendingHouses = ref<HouseItem[]>([])
const latestAppointments = ref<any[]>([])
const donutData = ref<{ name: string; value: number; color: string }[]>([])
const rangeDays = ref(7)
const syncTime = ref('')

const FEAT_IMGS = ['/assets/g1.png', '/assets/g2.png', '/assets/g3.png']
const DONUT_COLORS = ['#a8842f', '#c2a05a', '#b08a3a', '#8a7a45', '#d6c088']

const apptStatusMap: Record<number, string> = { 0: '待确认', 1: '已确认', 2: '已取消', 3: '已完成' }
const apptTagClass: Record<number, string> = { 0: 't-warn', 1: 't-info', 2: 't-mut', 3: 't-ok' }

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 12) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

/* ---------- KPI ---------- */
function icon(path: string) {
  return `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${path}</svg>`
}
const userSpark = ref('0,20 13,14 26,17 39,8 52,12 64,4')
const houseSpark = ref('0,18 13,16 26,12 39,14 52,8 64,6')
function toSpark(values: number[]) {
  if (!values.length) return '0,20 64,20'
  const max = Math.max(...values, 1)
  const min = Math.min(...values)
  const span = max - min || 1
  const step = 64 / Math.max(values.length - 1, 1)
  return values.map((v, i) => `${Math.round(i * step)},${Math.round(22 - ((v - min) / span) * 18)}`).join(' ')
}
const kpis = computed(() => [
  {
    label: '用户总数',
    value: stats.value.userCount.toLocaleString(),
    delta: `▲ 今日 +${stats.value.todayNewUsers}`,
    deltaClass: 'up',
    color: '#b8902a',
    spark: userSpark.value,
    icon: icon('<circle cx="9" cy="8" r="3.2"/><path d="M2.5 20c0-3.3 2.9-5 6.5-5s6.5 1.7 6.5 5"/><path d="M16 5.2A3.2 3.2 0 0 1 16 11M21.5 20c0-2.6-1.8-4.2-4.5-4.7"/>'),
  },
  {
    label: '房源总数',
    value: stats.value.houseCount.toLocaleString(),
    delta: `▲ 今日 +${stats.value.todayNewHouses}`,
    deltaClass: 'up',
    color: '#c9a227',
    spark: houseSpark.value,
    icon: icon('<path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10"/>'),
  },
  {
    label: '预约订单',
    value: stats.value.orderCount.toLocaleString(),
    delta: '累计预约',
    deltaClass: '',
    color: '#c2883a',
    spark: '0,16 13,12 26,15 39,10 52,13 64,8',
    icon: icon('<rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4"/>'),
  },
  {
    label: '待处理审核',
    value: String(pendingHouses.value.length),
    delta: pendingHouses.value.length > 0 ? '需关注' : '已清空',
    deltaClass: pendingHouses.value.length > 0 ? 'down' : 'up',
    color: '#c75d5d',
    spark: '0,10 13,14 26,10 39,16 52,12 64,18',
    icon: icon('<path d="M20 6L9 17l-5-5"/>'),
  },
])

/* ---------- 图表 ---------- */
const trendRef = ref<HTMLDivElement>()
const donutRef = ref<HTMLDivElement>()
let trendChart: echarts.ECharts | null = null
let donutChart: echarts.ECharts | null = null

async function loadTrend() {
  try {
    const [userRes, houseRes] = await Promise.all([getUserTrend(rangeDays.value), getHouseTrend(rangeDays.value)])
    userSpark.value = toSpark(userRes.data?.values || [])
    houseSpark.value = toSpark(houseRes.data?.values || [])
    if (!trendRef.value) return
    if (!trendChart) trendChart = echarts.init(trendRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis', backgroundColor: '#241f18', borderWidth: 0, textStyle: { color: '#efe9dc', fontSize: 12 } },
      legend: { top: 0, right: 0, itemWidth: 9, itemHeight: 9, icon: 'rect', textStyle: { color: '#6b6459', fontSize: 12 } },
      grid: { left: 8, right: 12, top: 30, bottom: 4, containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: userRes.data?.dates || [],
        axisLabel: { color: '#9a9183', fontSize: 11 },
        axisLine: { lineStyle: { color: 'rgba(168,128,40,0.2)' } },
        axisTick: { show: false },
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#9a9183', fontSize: 11 },
        splitLine: { lineStyle: { color: 'rgba(168,128,40,0.1)' } },
      },
      series: [
        {
          name: '用户',
          data: userRes.data?.values || [],
          type: 'line',
          smooth: true,
          symbol: 'none',
          lineStyle: { color: '#b08a3a', width: 2.2 },
          itemStyle: { color: '#b08a3a' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(176,138,58,0.22)' },
              { offset: 1, color: 'rgba(176,138,58,0.02)' },
            ]),
          },
        },
        {
          name: '房源',
          data: houseRes.data?.values || [],
          type: 'line',
          smooth: true,
          symbol: 'none',
          lineStyle: { color: '#c9a227', width: 2.2, type: 'dashed' },
          itemStyle: { color: '#c9a227' },
        },
      ],
    })
  } catch {
    /* API not ready */
  }
}

function renderDonut() {
  if (!donutRef.value || donutData.value.length === 0) return
  if (!donutChart) donutChart = echarts.init(donutRef.value)
  donutChart.setOption({
    series: [
      {
        type: 'pie',
        radius: ['68%', '88%'],
        avoidLabelOverlap: false,
        label: { show: false },
        data: donutData.value.map((d) => ({
          name: d.name,
          value: d.value,
          itemStyle: { color: d.color, borderRadius: 4, borderColor: 'rgba(255,253,248,0.9)', borderWidth: 2 },
        })),
      },
    ],
  })
}

function setRange(days: number) {
  rangeDays.value = days
  loadTrend()
}

/* ---------- 数据装配 ---------- */
async function reload() {
  syncTime.value = new Date().toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  try {
    const res = await getDashboardStats()
    stats.value = res.data
  } catch {
    /* API not ready */
  }
  try {
    // 房源列表：精选 + 类型分布（/house/list 为分页接口 → res.data.records）
    const res: any = await getHouseList({ page: 1, size: 100 })
    const list: HouseItem[] = res.data?.records || []
    featHouses.value = list.slice(0, 3)
    const byType = new Map<string, number>()
    list.forEach((h) => {
      const key = h.rentType || h.houseType || '其他'
      byType.set(key, (byType.get(key) || 0) + 1)
    })
    donutData.value = [...byType.entries()]
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([name, value], i) => ({ name, value, color: DONUT_COLORS[i % DONUT_COLORS.length] }))
    await nextTick()
    renderDonut()
  } catch {
    /* API not ready */
  }
  try {
    // 待审核房源（status=0）
    const res: any = await getHouseList({ page: 1, size: 3, status: 0 })
    pendingHouses.value = res.data?.records || []
  } catch {
    /* API not ready */
  }
  try {
    // 最新预约（/appointment/landlord 返回纯 List → res.data）
    const res: any = await getAppointmentList()
    latestAppointments.value = (res.data || []).slice(0, 3)
  } catch {
    /* API not ready */
  }
  loadTrend()
}

function handleResize() {
  trendChart?.resize()
  donutChart?.resize()
}

onMounted(() => {
  reload()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  trendChart?.dispose()
  donutChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* ---------- hero band ---------- */
.hero-band {
  position: relative;
  overflow: hidden;
  border-radius: 18px;
  padding: 22px 26px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  background: linear-gradient(120deg, rgba(212, 175, 55, 0.16), rgba(255, 253, 248, 0.7));
  border: 1px solid var(--line);
}
:root[data-theme='dark'] .hero-band {
  background: linear-gradient(120deg, rgba(200, 160, 80, 0.12), rgba(34, 31, 24, 0.7));
}
.hero-band::after {
  content: '';
  position: absolute;
  right: -30px;
  top: -30px;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(212, 175, 55, 0.25), transparent 70%);
  pointer-events: none;
}
.hb-l {
  position: relative;
  z-index: 1;
}
.hb-l .hi {
  font-size: 13px;
  color: var(--gold);
  letter-spacing: 1px;
}
.hb-l h2 {
  font-size: 22px;
  font-weight: 700;
  margin-top: 6px;
  letter-spacing: 0.5px;
}
.hb-l p {
  font-size: 12.5px;
  color: var(--txt-2);
  margin-top: 6px;
}
.hb-r {
  position: relative;
  z-index: 1;
  display: flex;
  gap: 10px;
}

/* ---------- bento ---------- */
.bento {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 18px;
}
.tile {
  position: relative;
  overflow: hidden;
  border-radius: var(--r);
  padding: 20px;
  transition: 0.3s;
  background: var(--glass);
  backdrop-filter: blur(16px) saturate(140%);
  -webkit-backdrop-filter: blur(16px) saturate(140%);
  border: 1px solid var(--line);
}
.tile:hover {
  transform: translateY(-4px);
  border-color: var(--line-strong);
  box-shadow: var(--shadow);
}
.tile .k {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--txt-3);
  font-size: 12px;
  letter-spacing: 1px;
}
.tile .k .ti {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  display: grid;
  place-items: center;
  background: rgba(184, 128, 42, 0.12);
  color: var(--gold);
}
.tile .v {
  font-size: 30px;
  font-weight: 700;
  margin-top: 14px;
  letter-spacing: 0.5px;
}
.tile .delta {
  font-size: 12px;
  margin-top: 6px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--txt-3);
}
.tile .delta.up {
  color: var(--ok);
}
.tile .delta.down {
  color: var(--bad);
}
.tile .spark {
  position: absolute;
  right: 14px;
  bottom: 12px;
  opacity: 0.85;
}

/* ---------- grid ---------- */
.grid-2 {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 18px;
  margin-bottom: 18px;
}
.trend-chart {
  width: 100%;
  height: 230px;
}
.seg {
  display: flex;
  gap: 4px;
  background: var(--bg-2);
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 3px;
}
.seg button {
  padding: 6px 13px;
  border-radius: 8px;
  font-size: 12px;
  color: var(--txt-2);
  transition: 0.2s;
  border: none;
  background: none;
  cursor: pointer;
  font-family: inherit;
}
.seg button.on {
  background: var(--grad-gold);
  color: #2a2113;
  font-weight: 600;
}

/* ---------- donut ---------- */
.donut-wrap {
  display: flex;
  align-items: center;
  gap: 22px;
}
.donut-c {
  position: relative;
  width: 130px;
  height: 130px;
  flex: none;
}
.donut-c .ctr {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  text-align: center;
  pointer-events: none;
}
.donut-c .ctr b {
  font-size: 20px;
  display: block;
}
.donut-c .ctr span {
  font-size: 11px;
  color: var(--txt-3);
  display: block;
}
.donut-leg {
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 13px;
  flex: 1;
}
.donut-leg .li {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.donut-leg .li.empty {
  color: var(--txt-3);
  justify-content: center;
}
.donut-leg .li i {
  display: inline-block;
  width: 9px;
  height: 9px;
  border-radius: 2px;
  margin-right: 8px;
}

/* ---------- 精选房源 ---------- */
.feat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}
.feat-card {
  position: relative;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid var(--line);
  aspect-ratio: 4/3;
  cursor: pointer;
  transition: 0.3s;
}
.feat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow);
}
.feat-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: 0.5s;
}
.feat-card:hover img {
  transform: scale(1.06);
}
.feat-card .ov {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 42%, rgba(20, 15, 8, 0.82));
}
.feat-card .txt {
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: 12px;
  color: #fff;
}
.feat-card .txt b {
  font-size: 14px;
  font-weight: 600;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.feat-card .txt span {
  font-size: 11.5px;
  color: rgba(255, 255, 255, 0.78);
}
.feat-card .price {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(20, 15, 8, 0.6);
  backdrop-filter: blur(4px);
  color: #e7cf8e;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 20px;
  z-index: 1;
}

/* ---------- feed ---------- */
.feed {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.feed .it {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  transition: 0.2s;
  align-items: center;
}
.feed .it:hover {
  background: rgba(184, 128, 42, 0.07);
}
.feed .av {
  width: 34px;
  height: 34px;
  border-radius: 9px;
  background: var(--surface-2);
  display: grid;
  place-items: center;
  color: var(--gold-2);
  font-size: 13px;
  flex-shrink: 0;
}
.feed .it .t {
  font-size: 13px;
  color: var(--txt);
}
.feed .it .s {
  font-size: 11.5px;
  color: var(--txt-3);
  margin-top: 2px;
}

@media (max-width: 1100px) {
  .bento {
    grid-template-columns: repeat(2, 1fr);
  }
  .grid-2 {
    grid-template-columns: 1fr;
  }
  .feat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
