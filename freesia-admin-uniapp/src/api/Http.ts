import { getToken, getTenantId, removeToken } from '@/utils/storage'

const baseURL = import.meta.env.VITE_APP_BASE_URL as string

// ==================== Token 自动续期 ====================
const RENEW_URL = '/api/sysLoginController/renewToken'
const RENEW_BEFORE_SECONDS = 300 // 提前5分钟续期
let renewPromise: Promise<string | null | 'error'> | null = null

/** 解析 JWT payload（不验证签名） */
function parseJwt(token: string): { exp: number } | null {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64).split('').map(c =>
        '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
      ).join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

/** token 是否将在 RENEW_BEFORE_SECONDS 内过期 */
function isTokenExpiringSoon(token: string): boolean {
  const jwt = parseJwt(token)
  if (!jwt || !jwt.exp) return false
  return (jwt.exp - Math.floor(Date.now() / 1000)) < RENEW_BEFORE_SECONDS
}

/**
 * 尝试续期，返回新token、null（服务端明确拒绝，需登出）或 'error'（网络/服务异常，不登出）。
 * 用 renewPromise 锁防止并发续期请求。
 */
async function tryRenewToken(currentToken: string): Promise<string | null | 'error'> {
  if (renewPromise) return renewPromise

  renewPromise = (async () => {
    try {
      const res = await new Promise<any>((resolve, reject) => {
        uni.request({
          url: baseURL + RENEW_URL,
          method: 'POST',
          header: { Authorization: 'Bearer ' + currentToken },
          timeout: 10000,
          success: (res) => resolve(res.data),
          fail: reject
        })
      })
      if (res?.code === 200 && res?.data?.token) {
        const newToken = res.data.token
        // 更新存储中的 token
        const { setToken } = await import('@/utils/storage')
        setToken(newToken)
        return newToken
      }
      return null // 服务端明确拒绝续期（如 token 已失效）
    } catch {
      return 'error' // 网络/服务异常，不贸然登出
    } finally {
      renewPromise = null
    }
  })()
  return renewPromise
}
// ==================== Token 自动续期 END ====================

// ==================== 401 统一处理 ====================
let handling401 = false
/** 401 统一登出处理：防并发 401 风暴（只处理一次），已在登录页时不重复跳转 */
function handle401(): void {
  if (handling401) return
  handling401 = true
  removeToken()
  uni.showToast({ title: '会话已过期，请重新登录', icon: 'none' })
  const pages = getCurrentPages()
  const currentRoute = pages[pages.length - 1]?.route
  setTimeout(() => {
    handling401 = false
    if (currentRoute !== 'pages/login/index') {
      uni.redirectTo({ url: '/pages/login/index' })
    }
  }, 1500)
}
// ==================== 401 统一处理 END ====================

class Http {
  async request(options: {
    url: string
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
    data?: object
    header?: object
  }) {
    const { url, method = 'GET', data = {}, header = {} } = options

    let token = getToken()

    // 非续期请求本身，检查token是否需要续期
    if (token && !url.includes(RENEW_URL)) {
      if (isTokenExpiringSoon(token)) {
        const renewResult = await tryRenewToken(token)
        if (typeof renewResult === 'string') {
          token = renewResult
        } else if (renewResult === null) {
          // 服务端拒绝续期：token 已不可用，直接登出
          handle401()
          return Promise.reject({ code: 401, msg: '会话已过期，请重新登录' })
        }
        // renewResult === 'error'：网络/服务异常，放行旧 token，交由后续请求判断
      }
    }

    return new Promise((resolve, reject) => {
      const defaultHeader: any = {
        'Content-Type': 'application/json'
      }

      if (token) {
        defaultHeader['Authorization'] = 'Bearer ' + token
      }
      const tenantId = getTenantId()
      if (tenantId) {
        defaultHeader['X-Tenant-Id'] = tenantId
      }

      uni.request({
        url: baseURL + url,
        method: method as any,
        data: data,
        header: { ...defaultHeader, ...header },
        timeout: 30000,
        success: (res) => {
          const responseData = res.data as any
          // HTTP 401（如网关 / Filter 直接返回）统一走登出
          if (res.statusCode === 401) {
            handle401()
            reject(responseData || { code: 401, msg: '会话已过期，请重新登录' })
            return
          }
          // 非标准响应（无 code 字段），直接返回原始数据
          if (responseData && responseData.code === undefined) {
            resolve(responseData)
            return
          }
          if (responseData?.code === 200) {
            resolve(responseData)
          } else if (responseData?.code === 401) {
            handle401()
            reject(responseData)
          } else {
            uni.showToast({ title: responseData?.msg || '请求失败', icon: 'none' })
            reject(responseData)
          }
        },
        fail: (err) => {
          uni.showToast({ title: '网络请求失败', icon: 'none' })
          reject(err)
        }
      })
    })
  }

  get(url: string, params?: object): Promise<any> {
    return this.request({
      url: url,
      method: 'GET',
      data: params
    })
  }

  post(url: string, params?: object): Promise<any> {
    return this.request({
      url: url,
      method: 'POST',
      data: params
    })
  }

  put(url: string, params?: object): Promise<any> {
    return this.request({
      url: url,
      method: 'PUT',
      data: params
    })
  }

  delete(url: string, params?: object): Promise<any> {
    return this.request({
      url: url,
      method: 'DELETE',
      data: params
    })
  }
}

export default new Http()
