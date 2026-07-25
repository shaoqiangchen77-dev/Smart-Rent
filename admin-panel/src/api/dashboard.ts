import request from '@/utils/request'

export interface DashboardStats {
  userCount: number
  houseCount: number
  orderCount: number
  todayNewUsers: number
  todayNewHouses: number
}

export const getDashboardStats = () =>
  request.get('/admin/dashboard')

export const getUserTrend = (days: number) =>
  request.get('/admin/trend/user', { params: { days } })

export const getHouseTrend = (days: number) =>
  request.get('/admin/trend/house', { params: { days } })
