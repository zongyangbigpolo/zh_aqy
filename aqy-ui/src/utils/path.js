export function resolveRoutePath(basePath = '/', routePath = '') {
  if (isAbsoluteUrl(routePath)) {
    return routePath
  }
  if (isAbsoluteUrl(basePath)) {
    return basePath
  }
  if (routePath.startsWith('/')) {
    return normalizePath(routePath)
  }
  const base = basePath || '/'
  const route = routePath || ''
  return normalizePath(`${base.replace(/\/+$/, '')}/${route.replace(/^\/+/, '')}`)
}

function normalizePath(value) {
  const parts = []
  String(value || '/').split('/').forEach(part => {
    if (!part || part === '.') {
      return
    }
    if (part === '..') {
      parts.pop()
      return
    }
    parts.push(part)
  })
  return `/${parts.join('/')}`
}

function isAbsoluteUrl(value) {
  return /^https?:\/\//.test(value)
}
