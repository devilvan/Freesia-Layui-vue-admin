import {createApp} from 'vue'
import Router from './router'
import Store from './store'
import App from './App.vue'
import {permission, role} from "./directives/permission";
import DictTag from './views/component/DictTag.vue'
import DictScan from './views/component/DictScan.vue'
import InnerLink from './layouts/InnerLink.vue'
import {MenuPermission} from "./types/Permission";
import LayJsonSchemaForm from "@layui/json-schema-form";
import "@layui/json-schema-form/lib/index.css";
// @ts-ignore
import {JSEncrypt} from "encryptlong";
import {useCryptStore} from "./store/crypt";
import 'virtual:svg-icons-register'
import SvgIcon from "./views/component/svg/SvgIcon.vue";
import {AccountMenuPermission} from "./types/account/AccountPermission";
import escClose from "@/directives/escClose";
import layer from "@layui/layui-vue";
// @fses/ai-chat 依赖的 ant-design-vue 基础样式
import 'ant-design-vue/dist/reset.css'
import { FsesAiChatPlugin } from '@fses/ai-chat'
// 导入 svgIcon

/**
 * 大坑，如果缺失src前的/，则会造成加载图片失败
 */
const app = createApp(App)
app.config.globalProperties.$SRC_ASSETS = "/src/assets/";
app.config.globalProperties.$ADMIN_PERMISSION = "*:*:*";
app.config.globalProperties.$ADMIN_ROLE = "admin";
app.config.globalProperties.$MENU_PERMISSION = MenuPermission
app.config.globalProperties.$ACCOUNT_MENU_PERMISSION = AccountMenuPermission

app.use(LayJsonSchemaForm)
app.use(Store);
app.use(Router);
app.use(layer);
app.use(FsesAiChatPlugin, {
  apiBaseUrl: (import.meta.env.VITE_APP_BASE_URL as string) + '/api',
})
app.component("DictTag", DictTag)
app.component("DictScan", DictScan)
app.component("InnerLink", InnerLink)
app.component('SvgIcon', SvgIcon);


app.directive("permission", permission);
app.directive("role", role);
app.directive("esc-close", escClose)
// Optional: Provide cleanup function
app.config.globalProperties.$cleanupEscListener = () => {
    if (document.__escLayerListener) {
        document.removeEventListener('keydown', document.__escLayerListener);
        delete document.__escLayerListener;
    }
};

app.config.errorHandler = (err, instance, info) => {
  console.error('[Vue Error]', err, info)
}

app.mount('#app');

app.config.globalProperties.$decryptedData = function (privateKey: string, data: string) {
    let decrypt = new JSEncrypt();
    decrypt.setPrivateKey(privateKey);
    return decrypt.decryptLong(data)
}

/**
 * 启动时预热密钥交换（纯内存，不落盘）。
 * 若失败不阻塞启动，首个加密调用会通过 ensureKeys 重新触发交换。
 */
useCryptStore().ensureKeys().catch(() => {});
export default app;
