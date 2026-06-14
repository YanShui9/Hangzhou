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
