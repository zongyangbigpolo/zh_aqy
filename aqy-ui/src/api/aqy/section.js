import request from '@/utils/request'

// 查询断面信息列表
export function listSection(query) {
  return request({
    url: '/aqy/section/list',
    method: 'get',
    params: query
  })
}
export function listTree(query) {
  return request({
    url: '/aqy/section/listTree',
    method: 'get',
    params: query
  })
}
// 查询断面信息详细
export function getSection(id) {
  return request({
    url: '/aqy/section/' + id,
    method: 'get'
  })
}

// 新增断面信息
export function addSection(data) {
  return request({
    url: '/aqy/section',
    method: 'post',
    data: data
  })
}

// 修改断面信息
export function updateSection(data) {
  return request({
    url: '/aqy/section',
    method: 'put',
    data: data
  })
}

// 删除断面信息
export function delSection(id) {
  return request({
    url: '/aqy/section/' + id,
    method: 'delete'
  })
}
