import { reactive } from 'vue'

interface UserInfo {
  id?: string
  username?: string
  nickname?: string
  avatar?: string
  roles?: string[]
  permissions?: string[]
}

const state = reactive({
  token: uni.getStorageSync('token') || '',
  userInfo: {} as UserInfo
})

export function useUserStore() {
  const setToken = (token: string) => {
    state.token = token
    uni.setStorageSync('token', token)
  }

  const clearToken = () => {
    state.token = ''
    uni.removeStorageSync('token')
  }

  const setUserInfo = (info: UserInfo) => {
    state.userInfo = info
    uni.setStorageSync('userInfo', JSON.stringify(info))
  }

  const clearUserInfo = () => {
    state.userInfo = {} as UserInfo
    uni.removeStorageSync('userInfo')
  }

  const getInfo = async () => {
    try {
      const { login: { getInfo } } = await import('@/api/Login')
      const res = await getInfo()
      if (res.code === 200) {
        setUserInfo(res.data)
        return res.data
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }

  const logout = async () => {
    try {
      const { login: { logout } } = await import('@/api/Login')
      await logout()
    } finally {
      clearToken()
      clearUserInfo()
    }
  }

  return {
    state,
    setToken,
    clearToken,
    setUserInfo,
    clearUserInfo,
    getInfo,
    logout
  }
}

export default state
