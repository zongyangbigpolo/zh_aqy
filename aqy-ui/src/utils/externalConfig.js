export function getAmapJsKey() {
  return process.env.VUE_APP_AMAP_JS_KEY || ''
}

export function getAmapWeatherKey() {
  return process.env.VUE_APP_AMAP_WEATHER_KEY || ''
}

export function getAppDownloadUrl() {
  return process.env.VUE_APP_APP_DOWNLOAD_URL || ''
}

export function getWebSocketUrl(path = '/websocket/message') {
  if (process.env.VUE_APP_WEBSOCKET_URL) {
    return process.env.VUE_APP_WEBSOCKET_URL
  }
  const baseApi = process.env.VUE_APP_BASE_API || '/prod-api'
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  return `${protocol}//${window.location.host}${baseApi}${path}`
}
