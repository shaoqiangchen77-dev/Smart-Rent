import { get, post } from '@/utils/request'

export interface AgentChatResult {
  reply: string
  sessionId: string
}

export interface AgentDialog {
  id: number
  sessionId: string
  role: 'user' | 'assistant' | 'system'
  content: string
  createTime: string
}

export function chatWithAgent(data: { message: string; sessionId?: string }) {
  return post<AgentChatResult>('/agent/chat', data)
}

export function getAgentHistory(sessionId: string) {
  return get<AgentDialog[]>('/agent/history', { sessionId })
}

export function getAgentSessions() {
  return get<string[]>('/agent/sessions')
}
