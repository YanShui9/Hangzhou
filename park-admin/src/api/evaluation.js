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
 * 区县审核通过（状态从 待区县审 改为 待市局审）
 * @param {Number} id 评价记录ID
 */
export function districtPassEvaluation(id) {
  return request({
    url: `/api/evaluations/${id}/district-pass`,
    method: 'post'
  })
}

/**
 * 区县审核驳回（状态从 待区县审 改为 驳回）
 * @param {Number} id 评价记录ID
 */
export function districtRejectEvaluation(id) {
  return request({
    url: `/api/evaluations/${id}/district-reject`,
    method: 'post'
  })
}

/**
 * 检查园区当年是否已提交评价
 * @param {Object} params 参数对象 { parkId, year }
 */
export function checkSubmittedEvaluation(params) {
  return request({
    url: '/api/evaluations/check-submitted',
    method: 'get',
    params
  })
}

/**
 * 删除评价记录
 * @param {Number} id 评价记录ID
 */
export function deleteEvaluation(id) {
  return request({
    url: `/api/evaluations/${id}`,
    method: 'delete'
  })
}

/**
 * 修改参评状态（区县端使用）
 * @param {Number} id 评价记录ID
 * @param {Number} status 参评状态：1=参评, 0=不参评
 */
export function updateEvaluationStatus(id, status) {
  return request({
    url: `/api/evaluations/${id}/status?status=${status}`,
    method: 'put'
  })
}

/**
 * 查询园区评价汇总表（简版）分页列表
 * @param {Object} params 查询参数
 */
export function getParkEvaluationList(params) {
  return request({
    url: '/api/evaluations/park',
    method: 'get',
    params
  })
}

/**
 * 导出园区评价汇总表（简版）
 * @param {Object} params 查询参数
 * @returns {Promise<Blob>}
 */
export function exportParkEvaluationList(params) {
  return request({
    url: '/api/evaluations/park/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
