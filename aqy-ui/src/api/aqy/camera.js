import request from '@/utils/request'

// 查询监控摄像头列表
export function listCamera(query) {
  return request({
    url: '/aqy/camera/list',
    method: 'get',
    params: query
  })
}

// 查询监控摄像头详细
export function getCamera(id) {
  return request({
    url: '/aqy/camera/' + id,
    method: 'get'
  })
}

// 新增监控摄像头
export function addCamera(data) {
  return request({
    url: '/aqy/camera',
    method: 'post',
    data: data
  })
}

// 修改监控摄像头
export function updateCamera(data) {
  return request({
    url: '/aqy/camera',
    method: 'put',
    data: data
  })
}

// 删除监控摄像头
export function delCamera(id) {
  return request({
    url: '/aqy/camera/' + id,
    method: 'delete'
  })
}

// 获取接口调用令牌
export function getAccessToken() {
  return request({
    url: '/aqy/hikgnss/getAccessToken',
    method: 'post',
  })
}

export function getAccessToken2() {
  return request({
    url: '/aqy/tgy/getAccessToken',
    method: 'post',
  })
}


export function getStructures(id) {
  return request({
    url: '/aqy/tgy/getStructures/' + id,
    method: 'get'
  })
}

export function getMeasareas(id) {
  return request({
    url: '/aqy/tgy/getMeasareas/' + id,
    method: 'get'
  })
}

export function getAggregate(measItemId) {
  return request({
    url: '/aqy/tgy/getAggregate/' + measItemId,
    method: 'get'
  })
}



export function getMeaspointData(id, st, et) {
  return request({
    url: '/aqy/tgy/getMeaspointData/' + id + "/data/start/" + st + "/end/" + et,
    method: 'get'
  })
}



// 获取Web端视频监控画面的地址
export function getWebVideoUrl(data) {
  return request({
    url: '/aqy/hikgnss/getWebVideoUrl',
    method: 'post',
    data: data
  })
}

// 获取Web端视频监控画面的地址
export function byDeviceSerial(data) {
  return request({
    url: '/aqy/hikgnss/byDeviceSerial',
    method: 'post',
    data: data
  })
}
