import request from '@/utils/request'

/**
 * 数据大屏 API（全部对接后端真实数据，无 mock）
 */

/**
 * 获取数据大屏市级汇总统计
 * @param {Number} year 评价年度
 * @returns {Promise}
 */
export function getBigScreenStats(year) {
  return request({
    url: '/api/dashboard/big-screen-stats',
    method: 'get',
    params: { year }
  })
}

/**
 * 获取各区县园区分布数据
 * @param {Number} year 评价年度（保留参数兼容现有调用）
 * @returns {Promise}
 */
export function getDistrictData(year) {
  return request({
    url: '/api/dashboard/district-data',
    method: 'get',
    params: { year }
  })
}

/**
 * 获取园区评价分析数据（数据大屏第二个标签页）
 * @param {Number} year 评价年度
 * @returns {Promise}
 */
export function getEvaluationAnalysis(year) {
  return request({
    url: '/api/dashboard/evaluation-analysis',
    method: 'get',
    params: { year }
  })
}
