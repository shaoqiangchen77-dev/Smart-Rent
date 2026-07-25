import { get, post } from '@/utils/request'

export function createAppointment(data: {
  houseId: number
  landlordId: number
  viewingTime: string
  contactPhone?: string
  remark?: string
}) {
  return post('/appointment', data)
}

export function getMyAppointments() {
  return get('/appointment/my')
}

export function getLandlordAppointments() {
  return get('/appointment/landlord')
}

export function confirmAppointment(id: number) {
  return post(`/appointment/${id}/confirm`)
}

export function cancelAppointment(id: number, reason?: string) {
  return post(`/appointment/${id}/cancel`, { reason })
}

export function completeAppointment(id: number) {
  return post(`/appointment/${id}/complete`)
}
