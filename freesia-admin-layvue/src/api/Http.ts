import axios, {AxiosRequestHeaders, AxiosResponse, InternalAxiosRequestConfig} from 'axios';
import {useUserStore} from "@/store/user";
import {layer} from '@layui/layui-vue';
import router from '@/router'
import {useAppStore} from "@/store/app";

export let loginPath: string = '/login'
export let mobileLoginPath: string = '/mobile/login'
export let downloadPath = import.meta.env.VITE_APP_DOWNLOAD_PATH;
type TAxiosOption = {
    timeout: number;
    baseURL: string;
}

export const isMobile = () => {
    // 更全面的移动设备检测
    const mobileRegex = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i;
    const isMobileDevice = mobileRegex.test(navigator.userAgent);

    // 结合屏幕宽度检测
    const isSmallScreen = window.innerWidth <= 768;

    let flag = isMobileDevice || isSmallScreen;
    // console.log(flag ? '移动端' : 'PC端')
    return flag;
}

const config: TAxiosOption = {
    timeout: 30000,
    // import.meta.env 是在运行时获取环境变量的值，适用于应用程序代码中需要动态获取环境变量的场合。（配置文件中获取不到，因为配置文件是在构建时被读取！！！）
    baseURL: import.meta.env.VITE_APP_BASE_URL as string
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
                if (router.currentRoute.value.path !== loginPath || router.currentRoute.value.path !== mobileLoginPath) {
                    if (isMobile()) {
                        router.push(mobileLoginPath).then(r => r)
                    } else {
                        router.push(loginPath).then(r => r)
                    }
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
            let responseData = response.data;
            switch (responseData.code) {
                case 200:
                    return responseData;
                case 401:
                    router.replace('/error/401').then(r => r)
                    layer.confirm(
                        '会话认证失败, 请重新登录1',
                        {
                            icon: 2, yes: function () {
                                userInfoStore.token = ''
                                if (isMobile()) {
                                    router.push(mobileLoginPath);
                                } else {
                                    router.push(loginPath);
                                }
                                layer.closeAll()
                            }
                        });
                    layer.notify({
                        title: "提示",
                        content: "会话认证失败, 请重新登录"
                    })
                    userInfoStore.token = ''
                    if (isMobile()) {
                        router.push(mobileLoginPath);
                    } else {
                        router.push(loginPath);
                    }
                    layer.closeAll()
                    return responseData;
                case 403:
                    router.replace('/error/403').then(r => r)
                    layer.confirm(
                        '没有权限访问网站',
                        {
                            icon: 2, yes: function () {
                                router.push('/').then(r => r);
                                layer.closeAll()
                            }
                        });
                    return responseData;
                case 404:
                    router.replace('/error/404').then(r => r)
                    layer.confirm(
                        '找不到该页面',
                        {
                            icon: 2, yes: function () {
                                userInfoStore.token = ''
                                layer.closeAll()
                            }
                        });
                    return responseData;
                case 500:
                    layer.confirm(responseData.msg, {icon: 2})
                    return responseData;
                default:
                    break;
            }
            if ('blob' === response.config.responseType) {
                let contentDisposition = response.headers['content-disposition'];
                let fileName = 'file'; // 默认文件名
                if (contentDisposition && contentDisposition.includes('filename=')) {
                    const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    const matches = filenameRegex.exec(contentDisposition);
                    if (matches != null && matches[1]) {
                        let filename = matches[1].replace(/['"]/g, '');
                        // 处理UTF-8编码的文件名
                        if (filename.includes('%')) {
                            try {
                                filename = decodeURIComponent(filename);
                            } catch (e) {
                                layer.confirm('Error decoding filename: ' + e, {icon: 3});
                            }
                        }
                    }
                }
                let contentType = response.headers["content-type"] as string;
                const blob = new Blob([responseData], {type: contentType})
                const fileLink = document.createElement('a') //创建一个a标签通过a标签的点击事件区下载文件
                fileLink.download = fileName
                fileLink.href = window.URL.createObjectURL(blob) //使用blob创建一个指向类型数组的URL
                document.body.appendChild(fileLink)
                fileLink.style.display = "none"
                fileLink.click()
                URL.revokeObjectURL(fileLink.href) // 释放URL 对象
                document.body.removeChild(fileLink)
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
