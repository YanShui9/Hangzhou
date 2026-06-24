import request from '@/utils/request'

/**
 * 数据仓库 API
 * 说明：此处接口路径为前端预留的占位，后端同学可按需调整
 */

/** 数据仓库列表（分页） */
export function getDataWarehousePage(params) {
  return request({
    url: '/api/data-warehouse',
    method: 'get',
    params
  })
}

/** 数据仓库详情 */
export function getDataWarehouseById(id) {
  return request({
    url: `/api/data-warehouse/${id}`,
    method: 'get'
  })
}

/** 新增数据仓库（上传数据文件） */
export function saveDataWarehouse(formData) {
  return request({
    url: '/api/data-warehouse',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 修改数据仓库 */
export function updateDataWarehouse(formData) {
  return request({
    url: `/api/data-warehouse/${formData.get ? formData.get('id') : ''}`,
    method: 'put',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 删除数据仓库 */
export function deleteDataWarehouse(id) {
  return request({
    url: `/api/data-warehouse/${id}`,
    method: 'delete'
  })
}

/** 下载指定模板 */
export function downloadTemplate(templateKey) {
  return request({
    url: `/api/data-warehouse/template/${templateKey}`,
    method: 'get',
    responseType: 'blob'
  })
}

/** 预览文件（获取文件流） */
export function previewFile(id) {
  return request({
    url: `/api/data-warehouse/${id}/preview`,
    method: 'get',
    responseType: 'blob'
  })
}
