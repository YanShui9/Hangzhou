import request from '@/utils/request'

/**
 * 区县账号 API
 * 说明：此处接口路径为前端预留的占位，后端同学可按需调整
 */

/** 区县账号列表（分页） */
export function getDistrictUserPage(params) {
  return request({
    url: '/api/district-users',
    method: 'get',
    params
  })
}

/** 区县账号详情 */
export function getDistrictUserById(id) {
  return request({
    url: `/api/district-users/${id}`,
    method: 'get'
  })
}

/** 新增区县账号 */
export function saveDistrictUser(data) {
  return request({
    url: '/api/district-users',
    method: 'post',
    data
  })
}

/** 修改区县账号 */
export function updateDistrictUser(data) {
  return request({
    url: `/api/district-users/${data.id}`,
    method: 'put',
    data
  })
}

/** 删除区县账号 */
export function deleteDistrictUser(id) {
  return request({
    url: `/api/district-users/${id}`,
    method: 'delete'
  })
}

/** 批量删除区县账号 */
export function batchDeleteDistrictUser(ids) {
  return request({
    url: '/api/district-users/batch',
    method: 'delete',
    data: { ids }
  })
}

/** 重置密码 */
export function resetDistrictUserPassword(id) {
  return request({
    url: `/api/users/${id}/reset-password`,
    method: 'post'
  })
}

/** 启用/禁用账号 */
export function toggleDistrictUserStatus(id, status) {
  return request({
    url: `/api/district-users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/** 下载模板 */
export function downloadDistrictUserTemplate() {
  return request({
    url: '/api/district-users/template',
    method: 'get',
    responseType: 'blob'
  })
}

/** 批量导入区县账号 */
export function importDistrictUser(formData) {
  return request({
    url: '/api/district-users/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 导出区县账号 */
export function exportDistrictUser(params) {
  return request({
    url: '/api/district-users/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
