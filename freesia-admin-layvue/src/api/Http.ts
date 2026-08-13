import axios, {AxiosRequestHeaders, AxiosResponse, InternalAxiosRequestConfig} from 'axios';
import {useUserStore} from "@/store/user";
import {layer} from '@layui/layui-vue';
import router from '@/router'
import {useAppStore} from "@/store/app";

export let loginPath: string = '/login'
export let downloadPath = import.meta.env.VITE_APP_DOWNLOAD_PATH;
type TAxiosOption = {
    timeout: number;
    baseURL: string;
}

const config: TAxiosOption = {
    timeout: 30000,
    baseURL: import.meta.env.VITE_APP_BASE_URL as string
}

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
            const res = await axios({
                method: 'post',
                url: config.baseURL + RENEW_URL,
                headers: { Authorization: 'Bearer ' + currentToken },
                timeout: 10000
            })
            if (res.data?.code === 200 && res.data?.data?.token) {
                const newToken = res.data.data.token
                useUserStore().token = newToken
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
/**
 * 401 统一登出处理：
 * 1. 防并发 401 风暴——token 过期瞬间多个在飞请求同时 401，只处理一次
 * 2. 已在登录页时不重复跳转
 */
function handle401(data: any = null): void {
    if (handling401) return
    handling401 = true
    const userInfoStore = useUserStore()
    // 清空用户状态（token、用户信息、权限等），避免旧数据残留
    userInfoStore.$reset()
    layer.msg(data?.msg || '登录已过期，请重新登录', { icon: 2 })
    if (router.currentRoute.value.path !== loginPath) {
        router.replace(loginPath).finally(() => (handling401 = false))
    } else {
        handling401 = false
    }
}
// ==================== 401 统一处理 END ====================

class Http {
    service;

    constructor(config: TAxiosOption) {
        this.service = axios.create(config)

        /* 请求拦截 */
        this.service.interceptors.request.use(async (config: InternalAxiosRequestConfig) => {
            const userInfoStore = useUserStore();
            let currentToken = userInfoStore.token;

            // 非续期请求本身，检查token是否需要续期
            if (currentToken && !config.url?.includes(RENEW_URL)) {
                if (isTokenExpiringSoon(currentToken)) {
                    const renewResult = await tryRenewToken(currentToken)
                    if (typeof renewResult === 'string') {
                        currentToken = renewResult
                    } else if (renewResult === null) {
                        // 服务端拒绝续期：token 已不可用，直接登出，不再用旧 token 发起请求
                        handle401()
                        return Promise.reject({ code: 401, msg: '登录已过期，请重新登录' })
                    }
                    // renewResult === 'error'：网络/服务异常，放行旧 token，交由后续请求判断
                }
            }

            if (currentToken) {
                (config.headers as AxiosRequestHeaders).Authorization = "Bearer " + currentToken
            } else {
                const path = router.currentRoute.value.path
                if (path !== loginPath && !path.startsWith('/oauth/callback/')) {
                    router.push(loginPath).then(r => r)
                }
            }
            config.headers['X-Tenant-Id'] = useAppStore().currentTenant
            if (config.method === 'get' && config.params) {
                config.params = new URLSearchParams(config.params);
            }
            return config
        }, error => {
            return Promise.reject(error);
        })

        /* 响应拦截 */
        this.service.interceptors.response.use((response: AxiosResponse<any>) => {
            let responseData = response.data;
            switch (responseData.code) {
                case 200:
                    return responseData;
                case 401:
                    handle401(responseData)
                    return Promise.reject(responseData);
                case 403:
                    router.replace('/error/403').then(r => r)
                    return responseData;
                case 404:
                    router.replace('/error/404').then(r => r)
                    return responseData;
                case 500:
                    layer.confirm(responseData.msg, {icon: 2})
                    return responseData;
                default:
                    break;
            }
            if ('blob' === response.config.responseType) {
                let contentDisposition = response.headers['content-disposition'];
                let fileName = 'file';
                if (contentDisposition && contentDisposition.includes('filename=')) {
                    const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    const matches = filenameRegex.exec(contentDisposition);
                    if (matches != null && matches[1]) {
                        fileName = matches[1].replace(/['"]/g, '');
                        if (fileName.includes('%')) {
                            try {
                                fileName = decodeURIComponent(fileName);
                            } catch (e) { /* ignore */ }
                        }
                    }
                }
                let contentType = response.headers["content-type"] as string;
                const blob = new Blob([responseData], {type: contentType})
                const fileLink = document.createElement('a')
                fileLink.download = fileName
                fileLink.href = window.URL.createObjectURL(blob)
                document.body.appendChild(fileLink)
                fileLink.style.display = "none"
                fileLink.click()
                URL.revokeObjectURL(fileLink.href)
                document.body.removeChild(fileLink)
            }
        }, error => {
            // HTTP 层 401（如 Sa-Token Filter / 网关直接返回）也走统一登出
            if (error?.response?.status === 401) {
                handle401(error.response.data)
            }
            return Promise.reject(error)
        })
    }

    get<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.get(url, {params, ..._object})
    }
    post<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.post(url, params, _object)
    }
    put<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.put(url, params, _object)
    }
    delete<T>(url: string, params?: any, _object = {}): Promise<any> {
        return this.service.delete(url, {params, ..._object})
    }
    getDownload(id: any): Promise<any> {
        return this.service.request({
            url: downloadPath + id,
            method: 'get',
            responseType: 'blob'
        })
    }
    downloadUrl(url: any): Promise<any> {
        return this.service.request({
            url: url,
            method: 'get',
            responseType: 'blob',
        })
    }
}

export default new Http(config)
