import request from '@/utils/request'

export interface HouseItem {
  id: number
  landlordId: number
  title: string
  area: string
  address: string
  price: number
  houseType: string
  rentType: string
  areaSize: number | null
  status: number
  viewCount: number
  collectCount: number
  avgRating: number
  createTime: string
}

export const getHouseList = (params: { page: number; size: number; status?: number; area?: string }) =>
  request.get('/house/list', { params })

export const auditHouse = (id: number, status: number, remark?: string) =>
  request.post(`/house/${id}/audit`, { status, remark })

export const getHouseDetail = (id: number) =>
  request.get(`/house/${id}`)
