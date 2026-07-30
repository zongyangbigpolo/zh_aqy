import request from '@/utils/request'

// 查询工程项目列表
export function listProject(query) {
  return request({
    url: '/aqy/project/list',
    method: 'get',
    params: query
  })
}


// 查询工程项目详细
export function getProject(id) {
  return request({
    url: '/aqy/project/' + id,
    method: 'get'
  })
}

// 新增工程项目
export function addProject(data) {
  return request({
    url: '/aqy/project',
    method: 'post',
    data: data
  })
}

// 修改工程项目
export function updateProject(data) {
  return request({
    url: '/aqy/project',
    method: 'put',
    data: data
  })
}

// 删除工程项目
export function delProject(id) {
  return request({
    url: '/aqy/project/' + id,
    method: 'delete'
  })
}


