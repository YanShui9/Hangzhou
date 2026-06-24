import request from '@/utils/request'

/**
 * 查询审核列表（待审核 + 已审核）
 * @param {Object} params 查询参数
 */
export function getAuditList(params) {
  return request({
    url: '/api/audits',
    method: 'get',
    params
  })
}

/**
 * 查询待审核列表
 * @param {Object} params 查询参数
 */
export function getPendingAuditList(params) {
  return request({
    url: '/api/audits/pending',
    method: 'get',
    params
  })
}

/**
 * 查询已审核列表
 * @param {Object} params 查询参数
 */
export function getAuditedList(params) {
  return request({
    url: '/api/audits/audited',
    method: 'get',
    params
  })
}

/**
 * 提交审核（通过/驳回）
 * @param {Object} data 审核数据 { evaluationId, action, opinion }
 */
export function submitAudit(data) {
  return request({
    url: '/api/audits',
    method: 'post',
    data
  })
}

/**
 * 查询某条评价记录的审核历史
 * @param {Number} evaluationId 评价记录ID
 */
export function getAuditHistory(evaluationId) {
  return request({
    url: `/api/audits/history/${evaluationId}`,
    method: 'get'
  })
}

/**
 * 查询评价记录详情
 * @param {Number} id 评价记录ID
 */
export function getEvaluationDetail(id) {
  return request({
    url: `/api/evaluations/${id}`,
    method: 'get'
  })
}

/**
 * 查询园区文件列表
 * @param {Number} parkId 园区ID
 */
export function getParkFiles(parkId) {
  return request({
    url: `/api/documents/park/${parkId}`,
    method: 'get'
  })
}

/**
 * 上传园区文件
 * @param {Number} parkId 园区ID
 * @param {FormData} formData 表单数据（包含file）
 */
export function uploadParkFile(parkId, formData) {
  return request({
    url: `/api/documents/park/${parkId}`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除园区文件
 * @param {Number} id 文件ID
 */
export function deleteParkFile(id) {
  return request({
    url: `/api/documents/${id}`,
    method: 'delete'
  })
}

/**
 * 获取文件预览URL
 * @param {Number} id 文件ID
 */
export function getFilePreviewUrl(id) {
  return `/api/documents/preview/${id}`
}
