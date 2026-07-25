import { del, get, post } from '@/utils/request'

export interface HouseItem {
  id: number
  landlordId: number
  title: string
  description: string | null
  area: string
  address: string
  longitude: number | null
  latitude: number | null
  price: number
  houseType: string
  rentType: string
  areaSize: number | null
  floor: string | null
  decoration: string | null
  orientation: string | null
  subwayDistance: number | null
  subwayStation: string | null
  hasElevator: number
  hasParking: number
  facilities: string[] | null
  status: number
  viewCount: number
  collectCount: number
  avgRating: number
  reviewCount: number
  images: string[]
  tags: string[]
}

export interface HouseQuery {
  page?: number
  size?: number
  area?: string
  houseType?: string
  rentType?: string
  minPrice?: number
  maxPrice?: number
  keyword?: string
  status?: number
  landlordId?: number
}

export interface HouseCreateParams {
  title: string
  description?: string
  area: string
  address: string
  price: number
  houseType: string
  rentType?: string
  areaSize?: number
  floor?: string
  decoration?: string
  orientation?: string
  subwayDistance?: number
  subwayStation?: string
  hasElevator?: number
  hasParking?: number
  facilities?: string[]
  tags?: string[]
  images?: string[]
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export const getHouseList = (params: HouseQuery) => get<PageResult<HouseItem>>('/house/list', params)
export const getHouseDetail = (id: number) => get<HouseItem>(`/house/${id}`)
export const searchHouse = (keyword: string, params?: HouseQuery) => get<PageResult<HouseItem>>('/house/search', { keyword, ...params })
export const createHouse = (data: HouseCreateParams) => post<number>('/house', data)
export const publishHouse = (id: number) => post(`/house/${id}/publish`)
export const offlineHouse = (id: number) => post(`/house/${id}/offline`)
export const deleteHouse = (id: number) => del(`/house/${id}`)
