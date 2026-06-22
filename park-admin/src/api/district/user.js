import request from '@/utils/request'

/**
 * 获取园区管理员列表
 * @param {Object} params - 查询参数
 */
export function getParkAdminList(params) {
  return request({
    url: '/api/district/users',
    method: 'get',
    params
  })
}

/**
 * 获取园区管理员详情
 * @param {Number} id - 用户ID
 */
export function getParkAdminById(id) {
  return request({
    url: `/api/district/users/${id}`,
    method: 'get'
  })
}

/**
 * 新增园区管理员
 * @param {Object} data - 用户数据
 */
export function addParkAdmin(data) {
  return request({
    url: '/api/district/users',
    method: 'post',
    data
  })
}

/**
 * 修改园区管理员
 * @param {Number} id - 用户ID
 * @param {Object} data - 用户数据
 */
export function updateParkAdmin(id, data) {
  return request({
    url: `/api/district/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除园区管理员
 * @param {Number} id - 用户ID
 */
export function deleteParkAdmin(id) {
  return request({
    url: `/api/district/users/${id}`,
    method: 'delete'
  })
}

/**
 * 重置园区管理员密码
 * @param {Number} id - 用户ID
 */
export function resetParkAdminPwd(id) {
  return request({
    url: `/api/district/users/${id}/reset-password`,
    method: 'post'
  })
}
