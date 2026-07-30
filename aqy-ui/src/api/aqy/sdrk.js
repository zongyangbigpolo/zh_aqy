import request from '@/utils/request'

// 查询山东仁科位移数据列表
export function listWy(query) {
  return request({
    url: '/aqy/sdrkwy/list',
    method: 'get',
    params: query
  })
}

// 查询山东仁科位移数据详细
export function getWy(id) {
  return request({
    url: '/aqy/sdrkwy/' + id,
    method: 'get'
  })
}

// 新增山东仁科位移数据
export function addWy(data) {
  return request({
    url: '/aqy/sdrkwy',
    method: 'post',
    data: data
  })
}

// 修改山东仁科位移数据
export function updateWy(data) {
  return request({
    url: '/aqy/sdrkwy',
    method: 'put',
    data: data
  })
}

// 删除山东仁科位移数据
export function delWy(id) {
  return request({
    url: '/aqy/sdrkwy/' + id,
    method: 'delete'
  })
}
