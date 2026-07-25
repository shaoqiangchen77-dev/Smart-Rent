import request from '@/utils/request'

export function getAppointmentList(params?: { page?: number; size?: number; status?: number }) {
  return request.get('/appointment/landlord', { params })
}

export function confirmAppointment(id: number) {
  return request.post(`/appointment/${id}/confirm`)
}

export function cancelAppointment(id: number, reason?: string) {
  return request.post(`/appointment/${id}/cancel`, { reason })
}

export function completeAppointment(id: number) {
  return request.post(`/appointment/${id}/complete`)
}
