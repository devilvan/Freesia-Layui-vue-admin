import axios, {AxiosRequestHeaders, AxiosResponse, InternalAxiosRequestConfig} from 'axios';
import {useUserStore} from "../store/user";
import {layer} from '@layui/layui-vue';
import router from '../router'
import {useAppStore} from "../store/app";

// 是否显示重新登录
export let isReLogin = {show: false};
export let loginPath: string = '/login'
type TAxiosOption = {
    timeout: number;
    baseURL: string;
}

const config: TAxiosOption = {
    timeout: 5000,
    baseURL: "http://localhost:8570"
}

class Http {
    service;

    constructor(config: TAxiosOption) {
        this.service = axios.create(config)
        /* 请求拦截 */
        this.service.interceptors.request.use((config: InternalAxiosRequestConfig) => {
            const userInfoStore = useUserStore();
            if (userInfoStore.token) {
                (config.headers as AxiosRequestHeaders).Authorization = "Bearer " + userInfoStore.token as string
            } else {
                if (router.currentRoute.value.path !== loginPath) {
                    router.push(loginPath)
                }
            }
            config.headers['X-Tenant-Id'] = useAppStore().currentTenant
            // GET请求
            if (config.method === 'get' && config.params) {
                // 对查询参数进行 URL 编码
                // 将编码后的查询参数赋值给原先的params
                config.params = new URLSearchParams(config.params);
            }
            return config
        }, error => {
            return Promise.reject(error);
        })

        /* 响应拦截 */
        this.service.interceptors.response.use((response: AxiosResponse<any>) => {
            const userInfoStore = useUserStore();
            switch (response.data.code) {
                case 200:
                    return response.data;
                case 401:
                    router.push('/error/401')
                    layer.confirm(
                        '会话认证失败, 请重新登录',
                        {
                            icon: 2, yes: function () {
                                userInfoStore.token = ''
                                router.push(loginPath);
                                layer.closeAll()
                            }
                        });
                    return response.data;
                case 403:
                    router.push('/error/403')
                    layer.confirm(
                        '没有权限访问网站',
                        {
                            icon: 2, yes: function () {
                                router.push('/');
                                layer.closeAll()
                            }
                        });
                    return response.data;
                case 404:
                    router.push('/error/404')
                    layer.confirm(
                        '找不到该页面',
                        {
                            icon: 2, yes: function () {
                                userInfoStore.token = ''
                                router.push('/');
                                layer.closeAll()
                            }
                        });
                    return response.data;
                case 500:
                    layer.confirm(response.data.msg, {icon: 2})
                    return response.data;
                default:
                    break;
            }
        }, error => {
            return Promise.reject(error)
        })
    }

    /* GET 方法 */
    get<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.get(url, {params, ..._object})
    }

    /* POST 方法 */
    post<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.post(url, params, _object)
    }

    /* PUT 方法 */
    put<T>(url: string, params?: object, _object = {}): Promise<any> {
        return this.service.put(url, params, _object)
    }

    /* DELETE 方法 */
    delete<T>(url: string, params?: any, _object = {}): Promise<any> {
        return this.service.delete(url, {params, ..._object})
    }
}

/**
 * 参数处理
 * @param {*} params  参数
 */
export function tansParams(params: any) {
    let result = ''
    for (const propName of Object.keys(params)) {
        const value = params[propName];
        const part = encodeURIComponent(propName) + "=";
        if (value !== null && value !== "" && typeof (value) !== "undefined") {
            if (typeof value === 'object') {
                for (const key of Object.keys(value)) {
                    if (value[key] !== null && value[key] !== "" && typeof (value[key]) !== 'undefined') {
                        let params = propName + '[' + key + ']';
                        var subPart = encodeURIComponent(params) + "=";
                        result += subPart + encodeURIComponent(value[key]) + "&";
                    }
                }
            } else {
                result += part + encodeURIComponent(value) + "&";
            }
        }
    }
    return result
}

export default new Http(config)
