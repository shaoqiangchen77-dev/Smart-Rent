import { get, post } from '@/utils/request'

export function getMyBills(status?: number) {
  const params: any = {}
  if (status !== undefined) params.status = status
  return get('/bill/my', params)
}

export function getBillDetail(id: number) {
  return get(`/bill/${id}`)
}

export function payBill(id: number, payMethod: string) {
  return post(`/bill/${id}/pay`, { payMethod })
}
