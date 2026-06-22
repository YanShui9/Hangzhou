import request from '@/utils/request'

// 区县账号管理
export function getDistrictAccountPage(params) {
  return request({
    url: '/api/system/district-accounts',
    method: 'get',
    params
  })
}

export function saveDistrictAccount(data) {
  return request({
    url: '/api/system/district-accounts',
    method: data.id ? 'put' : 'post',
    data
  })
}

export function deleteDistrictAccount(id) {
  return request({
    url: `/api/system/district-accounts/${id}`,
    method: 'delete'
  })
}

export function resetDistrictAccountPwd(id) {
  return request({
    url: `/api/system/district-accounts/${id}/reset-pwd`,
    method: 'post'
  })
}

// 园区账号管理
export function getParkAccountPage(params) {
  return request({
    url: '/api/system/park-accounts',
    method: 'get',
    params
  })
}

export function saveParkAccount(data) {
  return request({
    url: '/api/system/park-accounts',
    method: data.id ? 'put' : 'post',
    data
  })
}

export function deleteParkAccount(id) {
  return request({
    url: `/api/system/park-accounts/${id}`,
    method: 'delete'
  })
}

// 数据仓库
export function getDataWarehousePage(params) {
  return request({
    url: '/api/system/data-warehouse',
    method: 'get',
    params
  })
}

export function saveDataWarehouse(data) {
  return request({
    url: '/api/system/data-warehouse',
    method: data.id ? 'put' : 'post',
    data
  })
}

export function deleteDataWarehouse(id) {
  return request({
    url: `/api/system/data-warehouse/${id}`,
    method: 'delete'
  })
}

// 企业信息管理
export function getEnterpriseInfoPage(params) {
  return request({
    url: '/api/system/enterprise-info',
    method: 'get',
    params
  })
}

export function getEnterpriseInfoById(id) {
  return request({
    url: `/api/system/enterprise-info/${id}`,
    method: 'get'
  })
}

export function saveEnterpriseInfo(data) {
  return request({
    url: '/api/system/enterprise-info',
    method: data.id ? 'put' : 'post',
    data
  })
}