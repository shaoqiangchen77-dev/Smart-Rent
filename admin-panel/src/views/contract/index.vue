<template>
  <div class="page">
    <div class="page-title serif grad-txt">
      <span class="pt-ic" v-html="svg(DOC_ICON, 22)"></span>合同管理
    </div>
    <div class="page-sub">平台租约合同签订与履约状态</div>

    <div class="panel">
      <div class="toolbar">
        <span class="tb-label">状态</span>
        <button class="chip" :class="{ on: activeStatus === undefined }" @click="setStatus(undefined)">全部</button>
        <button class="chip" :class="{ on: activeStatus === 0 }" @click="setStatus(0)">待生效</button>
        <button class="chip" :class="{ on: activeStatus === 1 }" @click="setStatus(1)">生效中</button>
        <button class="chip" :class="{ on: activeStatus === 2 }" @click="setStatus(2)">已到期</button>
        <button class="chip" :class="{ on: activeStatus === 3 }" @click="setStatus(3)">已终止</button>
        <span class="spacer"></span>
        <button class="sr-btn" @click="loadContracts">↻ 刷新</button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="contractNo" label="合同编号" width="200" />
        <el-table-column prop="userId" label="租客ID" width="100" />
        <el-table-column prop="houseId" label="房源ID" width="100" />
        <el-table-column prop="monthlyRent" label="月租(元)" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="applyPage"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getContractList } from '@/api/contract'

const DOC_ICON = '<path d="M6 2h8l4 4v16H6z"/><path d="M14 2v4h4M9 13h6M9 17h6"/>'
function svg(path: string, size = 18) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${path}</svg>`
}

const loading = ref(false)
const allData = ref<any[]>([])
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const activeStatus = ref<number | undefined>(undefined)
const statusMap: Record<number, string> = { 0: '待生效', 1: '生效中', 2: '已到期', 3: '已终止' }
const statusType: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }

// 后端 /contract/landlord 返回完整列表，前端做分页展示
function applyPage() {
  const start = (page.value - 1) * pageSize
  tableData.value = allData.value.slice(start, start + pageSize)
}

async function loadContracts() {
  loading.value = true
  try {
    const res = await getContractList()
    const list = (res.data || []).filter((c: any) => activeStatus.value === undefined || c.status === activeStatus.value)
    allData.value = list
    total.value = allData.value.length
    page.value = 1
    applyPage()
  } finally {
    loading.value = false
  }
}

function setStatus(v: number | undefined) {
  activeStatus.value = v
  loadContracts()
}

onMounted(loadContracts)
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
