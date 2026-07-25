<template>
  <div class="page">
    <div class="page-title serif grad-txt">
      <span class="pt-ic" v-html="svg(CAL_ICON, 22)"></span>预约管理
    </div>
    <div class="page-sub">租客看房预约的确认与履约</div>

    <div class="panel">
      <div class="toolbar">
        <span class="tb-label">状态</span>
        <button class="chip" :class="{ on: activeStatus === undefined }" @click="setStatus(undefined)">全部</button>
        <button class="chip" :class="{ on: activeStatus === 0 }" @click="setStatus(0)">待确认</button>
        <button class="chip" :class="{ on: activeStatus === 1 }" @click="setStatus(1)">已确认</button>
        <button class="chip" :class="{ on: activeStatus === 2 }" @click="setStatus(2)">已取消</button>
        <button class="chip" :class="{ on: activeStatus === 3 }" @click="setStatus(3)">已完成</button>
        <span class="spacer"></span>
        <button class="sr-btn" @click="load">↻ 刷新</button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="租客ID" width="100" />
        <el-table-column prop="houseId" label="房源ID" width="100" />
        <el-table-column prop="viewingTime" label="预约时间" width="180" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" @click="onConfirm(row)">确认</el-button>
            <el-button v-if="row.status === 0" size="small" @click="onCancel(row)">取消</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" @click="onComplete(row)">完成</el-button>
            <span v-if="row.status === 2 || row.status === 3" class="muted">—</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && tableData.length === 0" description="暂无预约数据" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAppointmentList,
  confirmAppointment,
  cancelAppointment,
  completeAppointment,
} from '@/api/order'

const CAL_ICON = '<rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4"/>'
function svg(path: string, size = 18) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${path}</svg>`
}

const loading = ref(false)
const allData = ref<any[]>([])
const tableData = ref<any[]>([])
const activeStatus = ref<number | undefined>(undefined)
const statusMap: Record<number, string> = { 0: '待确认', 1: '已确认', 2: '已取消', 3: '已完成' }
const statusType: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'info', 3: '' }

// 后端 /appointment/landlord 返回完整列表，前端做筛选展示
async function load() {
  loading.value = true
  try {
    const res = await getAppointmentList()
    allData.value = res.data || []
    applyFilter()
  } catch (e) {
    allData.value = []
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function applyFilter() {
  tableData.value =
    activeStatus.value === undefined ? allData.value : allData.value.filter((r) => r.status === activeStatus.value)
}

function setStatus(s: number | undefined) {
  activeStatus.value = s
  applyFilter()
}

async function onConfirm(row: any) {
  await confirmAppointment(row.id)
  ElMessage.success('已确认')
  load()
}

async function onCancel(row: any) {
  try {
    const { value } = await ElMessageBox.prompt('请输入取消原因（可选）', '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '返回',
      inputValue: '',
    })
    await cancelAppointment(row.id, value)
    ElMessage.success('已取消')
    load()
  } catch {
    /* 用户放弃 */
  }
}

async function onComplete(row: any) {
  await completeAppointment(row.id)
  ElMessage.success('已完成')
  load()
}

onMounted(load)
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
.muted {
  color: var(--txt-3);
}
</style>
