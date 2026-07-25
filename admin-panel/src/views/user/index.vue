<template>
  <div class="page">
    <div class="page-title serif grad-txt">
      <span class="pt-ic" v-html="svg(USER_ICON, 22)"></span>用户管理
    </div>
    <div class="page-sub">平台注册用户、角色与状态一览</div>

    <div class="panel">
      <div class="toolbar">
        <span class="tb-label">角色</span>
        <button class="chip" :class="{ on: query.role === undefined }" @click="setRole(undefined)">全部</button>
        <button class="chip" :class="{ on: query.role === 0 }" @click="setRole(0)">租客</button>
        <button class="chip" :class="{ on: query.role === 1 }" @click="setRole(1)">房东</button>
        <button class="chip" :class="{ on: query.role === 2 }" @click="setRole(2)">管理员</button>
        <span class="tb-sep"></span>
        <span class="tb-label">状态</span>
        <button class="chip" :class="{ on: query.status === undefined }" @click="setStatus(undefined)">全部</button>
        <button class="chip" :class="{ on: query.status === 1 }" @click="setStatus(1)">正常</button>
        <button class="chip" :class="{ on: query.status === 0 }" @click="setStatus(0)">禁用</button>
        <span class="spacer"></span>
        <button class="sr-btn" @click="loadData">↻ 刷新</button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'success' : row.role === 2 ? 'danger' : ''">
              {{ roleMap[row.role] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="(val: boolean) => onStatusChange(row.id, val)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
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
import { getUserList, updateUserStatus, type UserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

const USER_ICON =
  '<circle cx="9" cy="8" r="3.2"/><path d="M2.5 20c0-3.3 2.9-5 6.5-5s6.5 1.7 6.5 5"/><path d="M16 5.2A3.2 3.2 0 0 1 16 11M21.5 20c0-2.6-1.8-4.2-4.5-4.7"/>'
function svg(path: string, size = 18) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${path}</svg>`
}

const loading = ref(false)
const tableData = ref<UserInfo[]>([])
const total = ref(0)
const roleMap: Record<number, string> = { 0: '租客', 1: '房东', 2: '管理员' }

const query = reactive({ page: 1, size: 10, role: undefined as number | undefined, status: undefined as number | undefined })

function setRole(v: number | undefined) {
  query.role = v
  query.page = 1
  loadData()
}
function setStatus(v: number | undefined) {
  query.status = v
  query.page = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function onStatusChange(id: number, val: boolean) {
  await updateUserStatus(id, val ? 1 : 0)
  ElMessage.success('操作成功')
  loadData()
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
