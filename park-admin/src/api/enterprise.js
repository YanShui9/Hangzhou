import request from '@/utils/request'

/**
 * 获取企业分页列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getEnterpriseList(params) {
  return request({
    url: '/api/enterprises',
    method: 'get',
    params
  })
}

/**
 * 获取企业详情
 * @param {Number} id 企业ID
 * @returns {Promise}
 */
export function getEnterpriseDetail(id) {
  return request({
    url: `/api/enterprises/${id}`,
    method: 'get'
  })
}

/**
 * 新增企业
 * @param {Object} data 企业信息
 * @returns {Promise}
 */
export function saveEnterprise(data) {
  return request({
    url: '/api/enterprises',
    method: 'post',
    data
  })
}

/**
 * 修改企业
 * @param {Object} data 企业信息（必须包含id字段）
 * @returns {Promise}
 */
export function updateEnterprise(data) {
  return request({
    url: `/api/enterprises/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 删除企业
 * @param {Number} id 企业ID
 * @returns {Promise}
 */
export function deleteEnterprise(id) {
  return request({
    url: `/api/enterprises/${id}`,
    method: 'delete'
  })
}
