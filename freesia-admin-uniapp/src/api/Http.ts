import { getToken, getTenantId, removeToken } from '@/utils/storage'

const baseURL = import.meta.env.VITE_APP_BASE_URL as string

class Http {
  request(options: {
    url: string
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
    data?: object
    header?: object
  }) {
    const { url, method = 'GET', data = {}, header = {} } = options

    return new Promise((resolve, reject) => {
      const token = getToken()
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
          const responseData = res.data
          if (responseData.code === 200) {
            resolve(responseData)
          } else if (responseData.code === 401) {
            removeToken()
            uni.showToast({ title: '会话已过期', icon: 'none' })
            setTimeout(() => {
              uni.redirectTo({ url: '/pages/login/index' })
            }, 1500)
            reject(responseData)
          } else {
            uni.showToast({ title: responseData.msg || '请求失败', icon: 'none' })
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

  get(url: string, params?: object) {
    return this.request({
      url: url,
      method: 'GET',
      data: params
    })
  }

  post(url: string, params?: object) {
    return this.request({
      url: url,
      method: 'POST',
      data: params
    })
  }

  put(url: string, params?: object) {
    return this.request({
      url: url,
      method: 'PUT',
      data: params
    })
  }

  delete(url: string, params?: object) {
    return this.request({
      url: url,
      method: 'DELETE',
      data: params
    })
  }
}

export default new Http()
