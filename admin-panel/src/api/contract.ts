import request from '@/utils/request'

export function getContractList(params?: { page?: number; size?: number; status?: number }) {
  return request.get('/contract/landlord', { params })
}

export function getContractDetail(id: number) {
  return request.get(`/contract/${id}`)
}

export function signContract(id: number) {
  return request.post(`/contract/${id}/sign`)
}

export function terminateContract(id: number, reason: string) {
  return request.post(`/contract/${id}/terminate`, { reason })
}
