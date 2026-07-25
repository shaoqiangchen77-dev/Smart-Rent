<template>
  <div class="page">
    <div class="page-title serif grad-txt">
      <span class="pt-ic" v-html="svg(CHECK_ICON, 22)"></span>房源审核
      <span class="cnt" v-if="total > 0">{{ total }} 条待处理</span>
    </div>
    <div class="page-sub">房东提交的房源需审核通过后方可上架</div>

    <div class="panel">
      <div class="toolbar">
        <span class="tb-label">当前筛选</span>
        <button class="chip on">待审核</button>
        <span class="spacer"></span>
        <button class="sr-btn" @click="loadData">↻ 刷新</button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="area" label="区域" width="100" />
        <el-table-column prop="price" label="月租(元)" width="100" />
        <el-table-column prop="houseType" label="户型" width="80" />
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="onAudit(row.id, 1)">通过</el-button>
            <el-button type="danger" size="small" @click="onAudit(row.id, 2)">拒绝</el-button>
          </template>
        </el-table-column>
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
import { getHouseList, auditHouse, type HouseItem } from '@/api/house'
import { ElMessage, ElMessageBox } from 'element-plus'

const CHECK_ICON = '<path d="M20 6L9 17l-5-5"/>'
function svg(path: string, size = 18) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${path}</svg>`
}

const loading = ref(false)
const tableData = ref<HouseItem[]>([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, status: 0 })

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

async function onAudit(id: number, status: number) {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    let remark = ''
    if (status === 2) {
      const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝房源', {
        inputPlaceholder: '拒绝原因',
      })
      remark = value || ''
    }
    await auditHouse(id, status, remark)
    ElMessage.success(`已${action}`)
    loadData()
  } catch {
    /* cancelled */
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
.cnt {
  font-size: 12px;
  font-weight: 500;
  color: var(--warn);
  background: rgba(224, 164, 88, 0.14);
  padding: 3px 10px;
  border-radius: 20px;
  letter-spacing: 0.5px;
}
</style>
