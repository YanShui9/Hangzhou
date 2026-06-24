import request from '@/utils/request'

export function getOperationQuarterList(parkId, year) {
  return request({
    url: `/api/operation-quarter/list/${parkId}/${year}`,
    method: 'get'
  })
}

export function getOperationQuarterById(id) {
  return request({
    url: `/api/operation-quarter/${id}`,
    method: 'get'
  })
}

export function getOperationQuarter(parkId, year, quarter) {
  return request({
    url: '/api/operation-quarter/query',
    method: 'get',
    params: { parkId, year, quarter }
  })
}

export function saveOperationQuarter(data) {
  return request({
    url: '/api/operation-quarter/save-or-update',
    method: 'post',
    data
  })
}

export function deleteOperationQuarter(id) {
  return request({
    url: `/api/operation-quarter/${id}`,
    method: 'delete'
  })
}

export function downloadEnterpriseListTemplate() {
  return request({
    url: '/api/operation-quarter/download/template/enterprise-list',
    method: 'get',
    responseType: 'blob'
  })
}

export function uploadAndParseEnterpriseList(parkId, file) {
  const formData = new FormData()
  formData.append('parkId', parkId)
  formData.append('file', file)
  return request({
    url: '/api/operation-quarter/upload/enterprise-list',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
