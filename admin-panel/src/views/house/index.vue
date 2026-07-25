<template>
  <div class="page">
    <div class="page-title serif grad-txt">
      <span class="pt-ic" v-html="svg(HOUSE_ICON, 22)"></span>房源列表
    </div>
    <div class="page-sub">全部房源信息、状态与浏览数据</div>

    <div class="panel">
      <div class="toolbar">
        <span class="tb-label">状态</span>
        <button class="chip" :class="{ on: query.status === undefined }" @click="setStatus(undefined)">全部</button>
        <button class="chip" :class="{ on: query.status === 0 }" @click="setStatus(0)">待审核</button>
        <button class="chip" :class="{ on: query.status === 1 }" @click="setStatus(1)">已上架</button>
        <button class="chip" :class="{ on: query.status === 2 }" @click="setStatus(2)">已下架</button>
        <button class="chip" :class="{ on: query.status === 3 }" @click="setStatus(3)">已租出</button>
        <span class="spacer"></span>
        <button class="sr-btn" @click="loadData">↻ 刷新</button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="area" label="区域" width="100" />
        <el-table-column prop="price" label="月租(元)" width="100" />
        <el-table-column prop="houseType" label="户型" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="avgRating" label="评分" width="80" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getHouseList, type HouseItem } from '@/api/house'

const HOUSE_ICON = '<path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10"/>'
function svg(path: string, size = 18) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${path}</svg>`
}

const loading = ref(false)
const tableData = ref<HouseItem[]>([])
const total = ref(0)
const statusMap: Record<number, string> = { 0: '待审核', 1: '已上架', 2: '已下架', 3: '已租出' }
const statusType: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }

const query = reactive({ page: 1, size: 10, status: undefined as number | undefined })

function setStatus(v: number | undefined) {
  query.status = v
  query.page = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const res = await getHouseList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
}
.pt-ic {
  display: inline-flex;
  color: var(--gold-2);
}
</style>
