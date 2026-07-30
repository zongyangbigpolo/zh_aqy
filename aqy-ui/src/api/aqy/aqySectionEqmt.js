import request from '@/utils/request'

// 查询断面配置的监测设备类型列表
export function listAqySectionEqmt(query) {
  return request({
    url: '/aqy/aqySectionEqmt/list',
    method: 'get',
    params: query
  })
}

// 查询断面配置的监测设备类型详细
export function getAqySectionEqmt(id) {
  return request({
    url: '/aqy/aqySectionEqmt/' + id,
    method: 'get'
  })
}

// 新增断面配置的监测设备类型
export function addAqySectionEqmt(data) {
  return request({
    url: '/aqy/aqySectionEqmt',
    method: 'post',
    data: data
  })
}

// 修改断面配置的监测设备类型
export function updateAqySectionEqmt(data) {
  return request({
    url: '/aqy/aqySectionEqmt',
    method: 'put',
    data: data
  })
}

// 删除断面配置的监测设备类型
export function delAqySectionEqmt(id) {
  return request({
    url: '/aqy/aqySectionEqmt/' + id,
    method: 'delete'
  })
}

// 查询断面配置的监测设备类型列表
export function listEqmtTypeByProjectId(projectId) {
  return request({
    url: '/aqy/aqySectionEqmt/listEqmtTypeByProjectId/' + projectId,
    method: 'get'
  })
}
