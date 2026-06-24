import request from '@/utils/request'

/**
 * 区域（区县）API
 */

/** 获取全部区域列表 */
export function getDistrictList() {
  return request({
    url: '/api/districts',
    method: 'get'
  })
}
