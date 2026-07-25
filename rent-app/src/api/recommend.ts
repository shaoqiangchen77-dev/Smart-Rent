import { get } from '@/utils/request'

export function getHotHouses() {
  return get('/recommend/hot')
}

export function getRecommendByArea(area: string) {
  return get('/recommend/by-area', { area })
}
