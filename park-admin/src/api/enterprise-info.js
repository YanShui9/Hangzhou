import request from '@/utils/request'

/**
 * 系统 - 企业信息 API
 * 说明：此处接口路径为前端预留的占位，后端同学可按需调整
 * 与业务模块 `/api/enterprise` 区分，本模块面向系统管理场景
 */

/** 企业信息列表（分页）
 *  params 支持：
 *    - keyword: 关键字（企业名称/信用代码/联系人等模糊搜索）
 *    - districtId: 所属区域ID
 *    - parkId: 所属园区ID
 *    - status: 企业状态（如：参评/未参评等）
 *    - pageNum / pageSize
 */
export function getEnterpriseInfoPage(params) {
  return request({
    url: '/api/system/enterprise-info',
    method: 'get',
    params
  })
}

/** 企业信息详情 */
export function getEnterpriseInfoById(id) {
  return request({
    url: `/api/system/enterprise-info/${id}`,
    method: 'get'
  })
}

/** 新增企业信息 */
export function saveEnterpriseInfo(data) {
  return request({
    url: '/api/system/enterprise-info',
    method: 'post',
    data
  })
}

/** 修改企业信息 */
export function updateEnterpriseInfo(data) {
  return request({
    url: `/api/system/enterprise-info/${data.id}`,
    method: 'put',
    data
  })
}

/** 删除企业信息 */
export function deleteEnterpriseInfo(id) {
  return request({
    url: `/api/system/enterprise-info/${id}`,
    method: 'delete'
  })
}

/** 批量删除 */
export function batchDeleteEnterpriseInfo(ids) {
  return request({
    url: '/api/system/enterprise-info/batch',
    method: 'delete',
    data: { ids }
  })
}

/** 下载模板 */
export function downloadEnterpriseInfoTemplate() {
  return request({
    url: '/api/system/enterprise-info/template',
    method: 'get',
    responseType: 'blob'
  })
}

/** 上传导入 */
export function importEnterpriseInfo(formData) {
  return request({
    url: '/api/system/enterprise-info/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 导出 */
export function exportEnterpriseInfo(params) {
  return request({
    url: '/api/system/enterprise-info/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
