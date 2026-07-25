import { get, post } from '@/utils/request'

export interface Message {
  id: number
  senderId: number | null
  receiverId: number
  msgType: string
  title: string | null
  content: string
  bizType: string | null
  bizId: number | null
  isRead: number
  createTime: string
}

export const getMessageList = (page = 1, size = 20) =>
  get<Message[]>('/message/list', { page, size })

export const getUnreadCount = () =>
  get<number>('/message/unread-count')

export const markAsRead = (id: number) =>
  post(`/message/read/${id}`)

export const markAllAsRead = () =>
  post('/message/read-all')
