import request from '@/utils/request'

export function downConfig(data) {
  return request({
    url: '/mqttCmd/cmd/downConfig',
    method: 'post',
    data: data
  })
}

export function batchDownConfig(data) {
  return request({
    url: '/mqttCmd/cmd/batchDownConfig',
    method: 'post',
    data: data
  })
}

export function setSampleParam(data) {
  return request({
    url: '/mqttCmd/cmd/setSampleParam',
    method: 'post',
    data: data
  })
}

export function batchSetSampleParam(data) {
  return request({
    url: '/mqttCmd/cmd/batchSetSampleParam',
    method: 'post',
    data: data
  })
}

export function setThresholdValue(data) {
  return request({
    url: '/mqttCmd/cmd/setThresholdValue',
    method: 'post',
    data: data
  })
}

export function batchSetThresholdValue(data) {
  return request({
    url: '/mqttCmd/cmd/batchSetThresholdValue',
    method: 'post',
    data: data
  })
}

export function getStatus(data) {
  return request({
    url: '/mqttCmd/cmd/getStatus',
    method: 'post',
    data: data
  })
}
export function soundLightAlarm() {
  return request({
    url: '/mqttCmd/cmd/soundLightAlarm',
    method: 'post',
  })
}
