import request from '@/utils/request'

export function getAdminUserPage(params) {
  return request({
    url: '/api/admin-users',
    method: 'get',
    params
  })
}

export function getAdminUserById(id) {
  return request({
    url: `/api/admin-users/${id}`,
    method: 'get'
  })
}

export function saveAdminUser(data) {
  return request({
    url: '/api/admin-users',
    method: 'post',
    data
  })
}

export function deleteAdminUser(id) {
  return request({
    url: `/api/admin-users/${id}`,
    method: 'delete'
  })
}

export function resetAdminUserPassword(id) {
  return request({
    url: `/api/admin-users/${id}/reset-password`,
    method: 'post'
  })
}

export function downloadAdminUserTemplate() {
  return request({
    url: '/api/admin-users/template',
    method: 'get',
    responseType: 'blob'
  })
}

export function importAdminUser(formData) {
  return request({
    url: '/api/admin-users/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
