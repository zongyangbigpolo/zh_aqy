import request from '@/utils/request'

// 查询位移监测设备上传数据记录列表
export function listRealTime(query) {
  return request({
    url: '/aqy/rawReport/listRealTime',
    method: 'get',
    params: query
  })
}
