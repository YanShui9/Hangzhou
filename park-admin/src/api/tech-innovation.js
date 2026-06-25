import request from '@/utils/request'

/**
 * 科技创新 - 查询列表
 * @param {Number} evaluationId 评价记录ID
 */
export function getTechInnovationList(evaluationId) {
  return request({
    url: `/api/tech-innovations/list/${evaluationId}`,
    method: 'get'
  })
}

/**
 * 科技创新 - 新增
 * @param {Object} data
 */
export function addTechInnovation(data) {
  return request({
    url: '/api/tech-innovations',
    method: 'post',
    data
  })
}

/**
 * 科技创新 - 修改
 * @param {Object} data
 */
export function updateTechInnovation(data) {
  return request({
    url: '/api/tech-innovations',
    method: 'put',
    data
  })
}

/**
 * 科技创新 - 批量保存
 * @param {Number} evaluationId
 * @param {Array} data
 */
export function batchSaveTechInnovation(evaluationId, data) {
  return request({
    url: `/api/tech-innovations/batch-save/${evaluationId}`,
    method: 'post',
    data
  })
}

/**
 * 科技创新 - 删除
 * @param {Number} id
 */
export function deleteTechInnovation(id) {
  return request({
    url: `/api/tech-innovations/${id}`,
    method: 'delete'
  })
}

/**
 * 院所合作项目 - 查询列表
 * @param {Number} evaluationId
 */
export function getTechProjectList(evaluationId) {
  return request({
    url: `/api/tech-projects/list/${evaluationId}`,
    method: 'get'
  })
}

/**
 * 院所合作项目 - 新增
 */
export function addTechProject(data) {
  return request({
    url: '/api/tech-projects',
    method: 'post',
    data
  })
}

/**
 * 院所合作项目 - 修改
 */
export function updateTechProject(data) {
  return request({
    url: '/api/tech-projects',
    method: 'put',
    data
  })
}

/**
 * 院所合作项目 - 批量保存
 */
export function batchSaveTechProject(evaluationId, data) {
  return request({
    url: `/api/tech-projects/batch-save/${evaluationId}`,
    method: 'post',
    data
  })
}

/**
 * 院所合作项目 - 删除
 */
export function deleteTechProject(id) {
  return request({
    url: `/api/tech-projects/${id}`,
    method: 'delete'
  })
}

/**
 * 企业培育 - 查询列表
 * @param {Number} evaluationId
 */
export function getCultivationRecordList(evaluationId) {
  return request({
    url: `/api/cultivation-records/list/${evaluationId}`,
    method: 'get'
  })
}

/**
 * 企业培育 - 新增
 */
export function addCultivationRecord(data) {
  return request({
    url: '/api/cultivation-records',
    method: 'post',
    data
  })
}

/**
 * 企业培育 - 修改
 */
export function updateCultivationRecord(data) {
  return request({
    url: '/api/cultivation-records',
    method: 'put',
    data
  })
}

/**
 * 企业培育 - 批量保存
 */
export function batchSaveCultivationRecord(evaluationId, data) {
  return request({
    url: `/api/cultivation-records/batch-save/${evaluationId}`,
    method: 'post',
    data
  })
}

/**
 * 企业培育 - 删除
 */
export function deleteCultivationRecord(id) {
  return request({
    url: `/api/cultivation-records/${id}`,
    method: 'delete'
  })
}

/**
 * 文件上传
 * @param {FormData} formData
 */
export function uploadFile(formData) {
  return request({
    url: '/api/files/upload',
    method: 'post',
    data: formData
  })
}

/**
 * 删除文件
 * @param {Number} id
 */
export function deleteFile(id) {
  return request({
    url: `/api/files/${id}`,
    method: 'delete'
  })
}

/**
 * 下载模板文件
 * @param {String} templateName 模板文件名
 */
export function downloadTemplate(templateName) {
  return request({
    url: `/api/files/download/template/${encodeURIComponent(templateName)}`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 上传并解析产业发展数据
 * @param {File} file Excel文件
 * @param {Number} evaluationId 评价记录ID
 */
export function uploadIndustryDevelopmentData(file, evaluationId) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('evaluationId', evaluationId)
  return request({
    url: '/api/files/upload/industry-development',
    method: 'post',
    data: formData
  })
}
