/**
 * Token storage with cookie fallback for H5 mode.
 * On mobile browsers localStorage can be unreliable (private mode, WebView, iframe).
 * Cookies provide a more robust fallback for session persistence.
 */

const TOKEN_KEY = 'token'
const TENANT_ID_KEY = 'tenantId'
const USER_INFO_KEY = 'userInfo'
const TENANT_LIST_KEY = 'sysTenantDtoList'

// 4-hour cookie max-age in seconds
const COOKIE_MAX_AGE = 4 * 60 * 60

const isH5 = typeof window !== 'undefined' && typeof document !== 'undefined'

function setCookie(key: string, value: string, maxAge: number) {
  if (!isH5) return
  try {
    const secure = location.protocol === 'https:' ? '; Secure' : ''
    document.cookie = `${key}=${encodeURIComponent(value)}; path=/; max-age=${maxAge}; SameSite=Lax${secure}`
  } catch (_) { /* cookie write failed — non-critical */ }
}

function getCookie(key: string): string | null {
  if (!isH5) return null
  try {
    const match = document.cookie.match(new RegExp('(?:^|; )' + key + '=([^;]*)'))
    return match ? decodeURIComponent(match[1]) : null
  } catch (_) { return null }
}

function removeCookie(key: string) {
  if (!isH5) return
  try {
    document.cookie = `${key}=; path=/; max-age=0; SameSite=Lax`
  } catch (_) { /* cookie removal failed — non-critical */ }
}

function safeGetStorage(key: string): string | null {
  try {
    const val = uni.getStorageSync(key)
    return val || null
  } catch (_) { return null }
}

function safeSetStorage(key: string, value: string) {
  try {
    uni.setStorageSync(key, value)
  } catch (_) { /* storage set failed, cookie fallback handles it */ }
}

function safeRemoveStorage(key: string) {
  try {
    uni.removeStorageSync(key)
  } catch (_) { /* non-critical */ }
}

// ---- public API ----

export function getToken(): string | null {
  const stored = safeGetStorage(TOKEN_KEY)
  if (stored) return stored
  // Fallback to cookie on H5
  if (isH5) {
    const cookieVal = getCookie(TOKEN_KEY)
    if (cookieVal) {
      // Restore from cookie back to storage
      safeSetStorage(TOKEN_KEY, cookieVal)
      return cookieVal
    }
  }
  return null
}

export function setToken(token: string) {
  safeSetStorage(TOKEN_KEY, token)
  if (isH5) {
    setCookie(TOKEN_KEY, token, COOKIE_MAX_AGE)
  }
}

export function removeToken() {
  safeRemoveStorage(TOKEN_KEY)
  if (isH5) {
    removeCookie(TOKEN_KEY)
  }
}

export function getTenantId(): string | null {
  const stored = safeGetStorage(TENANT_ID_KEY)
  if (stored) return stored
  if (isH5) {
    const cookieVal = getCookie(TENANT_ID_KEY)
    if (cookieVal) {
      safeSetStorage(TENANT_ID_KEY, cookieVal)
      return cookieVal
    }
  }
  return null
}

export function setTenantId(tenantId: string) {
  safeSetStorage(TENANT_ID_KEY, tenantId)
  if (isH5) {
    setCookie(TENANT_ID_KEY, tenantId, COOKIE_MAX_AGE)
  }
}

export function removeTenantId() {
  safeRemoveStorage(TENANT_ID_KEY)
  if (isH5) {
    removeCookie(TENANT_ID_KEY)
  }
}

export function getUserInfo(): string | null {
  return safeGetStorage(USER_INFO_KEY)
}

export function setUserInfo(info: string) {
  safeSetStorage(USER_INFO_KEY, info)
}

export function removeUserInfo() {
  safeRemoveStorage(USER_INFO_KEY)
}

export function getTenantList(): string | null {
  return safeGetStorage(TENANT_LIST_KEY)
}

export function setTenantList(list: string) {
  safeSetStorage(TENANT_LIST_KEY, list)
}

export function removeTenantList() {
  safeRemoveStorage(TENANT_LIST_KEY)
}
