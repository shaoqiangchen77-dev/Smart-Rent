<template>
  <div class="page">
    <div class="page-title serif grad-txt">
      <span class="pt-ic" v-html="svg(CHAT_ICON, 22)"></span>消息管理
    </div>
    <div class="page-sub">站内通知与消息推送记录</div>

    <div class="panel">
      <div class="toolbar">
        <span class="tb-label">类型</span>
        <button class="chip" :class="{ on: activeType === undefined }" @click="setType(undefined)">全部</button>
        <button class="chip" :class="{ on: activeType === 'system' }" @click="setType('system')">系统</button>
        <button class="chip" :class="{ on: activeType === 'appointment' }" @click="setType('appointment')">预约</button>
        <button class="chip" :class="{ on: activeType === 'contract' }" @click="setType('contract')">合同</button>
        <button class="chip" :class="{ on: activeType === 'bill' }" @click="setType('bill')">账单</button>
        <span class="spacer"></span>
        <button class="sr-btn" @click="loadMessages">↻ 刷新</button>
        <button class="sr-btn primary" @click="showSendDialog">+ 发送消息</button>
      </div>

      <el-table :data="messageList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="msgType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTagMap[row.msgType]">{{ typeMap[row.msgType] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverId" label="接收用户ID" width="120" />
        <el-table-column prop="isRead" label="已读" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'success' : 'info'">{{ row.isRead ? '已读' : '未读' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="applyPage"
      />
    </div>

    <el-dialog v-model="dialogVisible" title="发送消息" width="500px">
      <el-form :model="sendForm" label-width="100px">
        <el-form-item label="接收用户">
          <el-input v-model="sendForm.receiverId" placeholder="用户ID，留空发送给全部用户" />
        </el-form-item>
        <el-form-item label="消息类型">
          <el-select v-model="sendForm.msgType">
            <el-option label="系统通知" value="system" />
            <el-option label="预约通知" value="appointment" />
            <el-option label="合同通知" value="contract" />
            <el-option label="账单通知" value="bill" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="sendForm.title" placeholder="消息标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="sendForm.content" type="textarea" :rows="4" placeholder="消息内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendMessage">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { getMessageList } from '@/api/message'

const CHAT_ICON = '<path d="M21 12a8 8 0 0 1-11.5 7.2L3 21l1.8-6.5A8 8 0 1 1 21 12z"/>'
function svg(path: string, size = 18) {
  return `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${path}</svg>`
}

interface Message {
  id: number
  senderId: number | null
  receiverId: number
  msgType: string
  title: string
  content: string
  isRead: number
  createTime: string
}

const allMessages = ref<Message[]>([])
const messageList = ref<Message[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = 20
const total = ref(0)
const activeType = ref<string | undefined>(undefined)
const dialogVisible = ref(false)

const sendForm = ref({
  receiverId: '',
  msgType: 'system',
  title: '',
  content: '',
})

const typeMap: Record<string, string> = {
  system: '系统',
  appointment: '预约',
  contract: '合同',
  bill: '账单',
  chat: '聊天',
}

const typeTagMap: Record<string, string> = {
  system: '',
  appointment: 'warning',
  contract: 'success',
  bill: 'danger',
  chat: 'info',
}

// 后端 /message/list 返回完整列表，前端做分页展示
function applyPage() {
  const start = (page.value - 1) * pageSize
  messageList.value = allMessages.value.slice(start, start + pageSize)
}

async function loadMessages() {
  loading.value = true
  try {
    const res = await getMessageList()
    const list = (res.data || []).filter((m: Message) => activeType.value === undefined || m.msgType === activeType.value)
    allMessages.value = list
    total.value = allMessages.value.length
    page.value = 1
    applyPage()
  } finally {
    loading.value = false
  }
}

function setType(t: string | undefined) {
  activeType.value = t
  loadMessages()
}

function showSendDialog() {
  sendForm.value = { receiverId: '', msgType: 'system', title: '', content: '' }
  dialogVisible.value = true
}

async function sendMessage() {
  if (!sendForm.value.title || !sendForm.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  try {
    await request.post('/message/send', sendForm.value)
    ElMessage.success('发送成功')
    dialogVisible.value = false
    loadMessages()
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

onMounted(loadMessages)
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
