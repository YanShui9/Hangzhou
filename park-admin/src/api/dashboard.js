import request from '@/utils/request'

/**
 * 获取统计数据
 * @returns {Promise}
 */
export function getStats() {
  return request({
    url: '/api/dashboard/stats',
    method: 'get'
  })
}

/**
 * 获取园区排名
 * @param {Object} params 查询参数
 * @param {Number} params.limit 返回数量，默认 10
 * @returns {Promise}
 */
export function getTopParks(params) {
  return request({
    url: '/api/dashboard/top-parks',
    method: 'get',
    params
  })
}

/**
 * 获取月度统计数据
 * @param {Object} params 查询参数
 * @param {Number} params.year 年份，默认当前年
 * @returns {Promise}
 */
export function getMonthlyStats(params) {
  return request({
    url: '/api/dashboard/monthly-stats',
    method: 'get',
    params
  })
}
