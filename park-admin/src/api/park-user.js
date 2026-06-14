import request from '@/utils/request'

/**
 * 园区账号 API
 * 说明：此处接口路径为前端预留的占位，后端同学可按需调整
 */

/** 园区账号列表（分页） */
export function getParkUserPage(params) {
  return request({
    url: '/api/park-users',
    method: 'get',
    params
  })
}

/** 园区账号详情 */
export function getParkUserById(id) {
  return request({
    url: `/api/park-users/${id}`,
    method: 'get'
  })
}

/** 新增园区账号 */
export function saveParkUser(data) {
  return request({
    url: '/api/park-users',
    method: 'post',
    data
  })
}

/** 修改园区账号 */
export function updateParkUser(data) {
  return request({
    url: `/api/park-users/${data.id}`,
    method: 'put',
    data
  })
}

/** 删除园区账号 */
export function deleteParkUser(id) {
  return request({
    url: `/api/park-users/${id}`,
    method: 'delete'
  })
}

/** 批量删除园区账号 */
export function batchDeleteParkUser(ids) {
  return request({
    url: '/api/park-users/batch',
    method: 'delete',
    data: { ids }
  })
}

/** 重置密码 */
export function resetParkUserPassword(id) {
  return request({
    url: `/api/park-users/${id}/reset-password`,
    method: 'post'
  })
}

/** 启用/禁用账号 */
export function toggleParkUserStatus(id, status) {
  return request({
    url: `/api/park-users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/** 下载模板 */
export function downloadParkUserTemplate() {
  return request({
    url: '/api/park-users/template',
    method: 'get',
    responseType: 'blob'
  })
}

/** 批量导入园区账号 */
export function importParkUser(formData) {
  return request({
    url: '/api/park-users/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 导出园区账号 */
export function exportParkUser(params) {
  return request({
    url: '/api/park-users/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
