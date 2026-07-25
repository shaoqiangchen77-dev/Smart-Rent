import { get, post, del } from '@/utils/request'

export function collect(houseId: number) {
  return post(`/collection/${houseId}`)
}

export function uncollect(houseId: number) {
  return del(`/collection/${houseId}`)
}

export function isCollected(houseId: number) {
  return get(`/collection/check/${houseId}`)
}

export function getMyCollections() {
  return get('/collection/my')
}
