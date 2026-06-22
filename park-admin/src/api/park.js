import request from '@/utils/request'

/**
 * 获取园区分页列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getParkList(params) {
  return request({
    url: '/api/parks',
    method: 'get',
    params
  })
}

/**
 * 获取园区详情
 * @param {Number} id 园区ID
 * @returns {Promise}
 */
export function getParkDetail(id) {
  return request({
    url: `/api/parks/${id}`,
    method: 'get'
  })
}

/**
 * 获取园区详情（别名）
 * @param {Number} id 园区ID
 * @returns {Promise}
 */
export function getParkById(id) {
  return getParkDetail(id)
}

/**
 * 新增园区
 * @param {Object} data 园区信息
 * @returns {Promise}
 */
export function savePark(data) {
  return request({
    url: '/api/parks',
    method: 'post',
    data
  })
}

/**
 * 新增园区（别名）
 * @param {Object} data 园区信息
 * @returns {Promise}
 */
export function addPark(data) {
  return savePark(data)
}

/**
 * 修改园区
 * @param {Object} data 园区信息（必须包含id字段）
 * @returns {Promise}
 */
export function updatePark(data) {
  return request({
    url: `/api/parks/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 删除园区
 * @param {Number} id 园区ID
 * @returns {Promise}
 */
export function deletePark(id) {
  return request({
    url: `/api/parks/${id}`,
    method: 'delete'
  })
}

/**
 * 获取园区统计数据
 * @param {Number} id 园区ID
 * @returns {Promise}
 */
export function getParkStats(id) {
  return request({
    url: `/api/parks/${id}/stats`,
    method: 'get'
  })
}
