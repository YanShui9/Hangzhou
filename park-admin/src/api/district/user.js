/**
 * 区县端-园区管理员管理 API
 * @module api/district/user
 * @author park-team
 */

import request from '@/utils/request'

/**
 * 获取园区管理员列表（分页）
 * @param {Object} params - 查询参数
 * @param {Number} [params.pageNum=1] - 页码
 * @param {Number} [params.pageSize=10] - 每页条数
 * @param {String} [params.username] - 用户名（模糊查询）
 * @param {String} [params.realName] - 真实姓名（模糊查询）
 * @param {Number} [params.status] - 账号状态：0=禁用, 1=启用
 * @returns {Promise} 返回分页数据
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
 * @param {Number} id - 园区管理员ID
 * @returns {Promise} 返回园区管理员信息
 */
export function getParkAdminById(id) {
  return request({
    url: `/api/district/users/${id}`,
    method: 'get'
  })
}

/**
 * 新增园区管理员
 * @param {Object} data - 园区管理员信息
 * @param {String} data.username - 用户名（必填）
 * @param {String} data.password - 密码（必填）
 * @param {String} [data.realName] - 真实姓名
 * @param {String} [data.phone] - 手机号
 * @param {Number} [data.status=1] - 账号状态：0=禁用, 1=启用
 * @returns {Promise} 返回操作结果
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
 * @param {Number} id - 园区管理员ID
 * @param {Object} data - 园区管理员信息
 * @param {String} [data.username] - 用户名
 * @param {String} [data.password] - 密码（可选，不填则保持原密码）
 * @param {String} [data.realName] - 真实姓名
 * @param {String} [data.phone] - 手机号
 * @param {Number} [data.status] - 账号状态：0=禁用, 1=启用
 * @returns {Promise} 返回操作结果
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
 * @param {Number} id - 园区管理员ID
 * @returns {Promise} 返回操作结果
 */
export function deleteParkAdmin(id) {
  return request({
    url: `/api/district/users/${id}`,
    method: 'delete'
  })
}

/**
 * 重置园区管理员密码
 * @param {Number} id - 园区管理员ID
 * @returns {Promise} 返回操作结果，密码将重置为 123456
 */
export function resetParkAdminPwd(id) {
  return request({
    url: `/api/district/users/${id}/reset-password`,
    method: 'post'
  })
}
