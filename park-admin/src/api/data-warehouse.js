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

/** 下载附件（数据文件） */
export function downloadAttachment(fileUrl) {
  return request({
    url: fileUrl,
    method: 'get',
    responseType: 'blob'
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
