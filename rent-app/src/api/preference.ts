import { get, post } from '@/utils/request'

export function getMyPreference() {
  return get('/preference/my')
}

export function savePreference(data: {
  preferredArea?: string
  minBudget?: number
  maxBudget?: number
  preferredType?: string
  preferredDecoration?: string
  preferredSubwayDistance?: number
  preferredOrientation?: string
  needElevator?: number
}) {
  return post('/preference', data)
}
