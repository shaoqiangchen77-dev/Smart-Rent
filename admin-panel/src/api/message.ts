import request from '@/utils/request'

export function getMessageList(params?: { page?: number; size?: number }) {
  return request.get('/message/list', { params })
}

export function getUnreadCount() {
  return request.get('/message/unread-count')
}

export function markAsRead(id: number) {
  return request.post(`/message/read/${id}`)
}

export function markAllAsRead() {
  return request.post('/message/read-all')
}
