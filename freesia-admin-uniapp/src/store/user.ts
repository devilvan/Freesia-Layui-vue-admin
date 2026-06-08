import { reactive } from 'vue'
import {
  getToken as loadToken,
  setToken as saveToken,
  removeToken as deleteToken,
  getTenantId,
  setTenantId,
  removeTenantId,
  getUserInfo,
  setUserInfo as saveUserInfo,
  removeUserInfo,
  getTenantList,
  setTenantList as saveTenantList,
  removeTenantList
} from '@/utils/storage'

interface UserInfo {
  id?: string
  username?: string
  nickname?: string
  avatar?: string
  roles?: string[]
  permissions?: string[]
  deptId?: string
  deptName?: string
}

interface TenantItem {
  id?: string
  name?: string
  code?: string
}

const state = reactive({
  token: loadToken() || '',
  userInfo: {} as UserInfo,
  noticeCount: 0,
  announcementCount: 0,
  sysTenantDtoList: (() => {
    try {
      const stored = getTenantList()
      return stored ? JSON.parse(stored) : []
    } catch (e) { return [] }
  })() as TenantItem[],
  currentTenantId: getTenantId() || ''
})

export function useUserStore() {
  const setToken = (token: string) => {
    state.token = token
    saveToken(token)
  }

  const clearToken = () => {
    state.token = ''
    deleteToken()
  }

  const setUserInfo = (info: UserInfo) => {
    state.userInfo = info
    saveUserInfo(JSON.stringify(info))
  }

  const clearUserInfo = () => {
    state.userInfo = {} as UserInfo
    removeUserInfo()
  }

  const setTenantList = (list: TenantItem[]) => {
    state.sysTenantDtoList = list || []
    saveTenantList(JSON.stringify(list || []))
  }

  const setCurrentTenant = (tenantId: string) => {
    state.currentTenantId = tenantId
    setTenantId(tenantId)
  }

  const getInfo = async () => {
    try {
      const loginModule = await import('@/api/Login')
      const res = await loginModule.getInfo()
      if (res.code === 200) {
        // PC端 getInfo 返回: { user, roles, permissions, sysTenantDtoList }
        const data = res.data
        setUserInfo(data.user || data)
        if (data.sysTenantDtoList) {
          setTenantList(data.sysTenantDtoList)
          // 首次加载时若未选租户，默认选第一个
          if (!state.currentTenantId && data.sysTenantDtoList.length > 0) {
            setCurrentTenant(data.sysTenantDtoList[0].id)
          }
        }
        return data
      }
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }

  const logout = async () => {
    try {
      const loginModule = await import('@/api/Login')
      await loginModule.logout()
    } catch(e) {
      // continue with local cleanup
    } finally {
      clearToken()
      clearUserInfo()
      state.noticeCount = 0
      state.announcementCount = 0
      state.sysTenantDtoList = []
      state.currentTenantId = ''
      removeTenantId()
      removeTenantList()
    }
  }

  return {
    state,
    setToken,
    clearToken,
    setUserInfo,
    clearUserInfo,
    setTenantList,
    setCurrentTenant,
    getInfo,
    logout
  }
}

export default state
