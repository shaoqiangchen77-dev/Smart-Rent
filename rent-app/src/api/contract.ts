import { get, post } from '@/utils/request'

export function getMyContracts() {
  return get('/contract/my')
}

export function getContractDetail(id: number) {
  return get(`/contract/${id}`)
}

export function signContract(id: number) {
  return post(`/contract/${id}/sign`)
}
