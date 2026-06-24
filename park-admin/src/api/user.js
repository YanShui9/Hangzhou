import request from '@/utils/request'

/**
 * 用户管理 API
 */

/** 用户列表（分页） */
export function getUserPage(params) {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}

/** 用户详情 */
export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

/** 新增用户 */
export function saveUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

/** 修改用户 */
export function updateUser(data) {
  return request({
    url: `/api/users/${data.id}`,
    method: 'put',
    data
  })
}

/** 删除用户 */
export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

/** 重置密码 */
export function resetPassword(id) {
  return request({
    url: `/api/users/${id}/reset-password`,
    method: 'post'
  })
}
