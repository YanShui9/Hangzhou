import request from '@/utils/request'

/**
 * 查询评价记录分页列表
 * @param {Object} params 查询参数
 */
export function getEvaluationPage(params) {
  return request({
    url: '/api/evaluations',
    method: 'get',
    params
  })
}

/**
 * 查询评价记录详情
 * @param {Number} id 评价记录ID
 */
export function getEvaluationById(id) {
  return request({
    url: `/api/evaluations/${id}`,
    method: 'get'
  })
}

/**
 * 新增评价记录
 * @param {Object} data 评价记录数据
 */
export function addEvaluation(data) {
  return request({
    url: '/api/evaluations',
    method: 'post',
    data
  })
}

/**
 * 修改评价记录
 * @param {Object} data 评价记录数据
 */
export function updateEvaluation(data) {
  return request({
    url: '/api/evaluations',
    method: 'put',
    data
  })
}

/**
 * 提交评价（状态从 draft 改为 submitted）
 * @param {Number} id 评价记录ID
 */
export function submitEvaluation(id) {
  return request({
    url: `/api/evaluations/${id}/submit`,
    method: 'post'
  })
}

/**
 * 导出评价记录为Excel
 * @param {Object} params 查询参数
 */
export function exportEvaluations(params) {
  return request({
    url: '/api/evaluations/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 更新评价记录的参评状态
 * @param {Number} id 评价记录ID
 * @param {Number} status 目标状态：0=不参评, 1=参评
 */
export function updateEvaluationStatus(id, status) {
  return request({
    url: `/api/evaluations/${id}/status?status=${status}`,
    method: 'put'
  })
}
