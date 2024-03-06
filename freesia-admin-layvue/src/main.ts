import {createApp} from 'vue'
import Router from './router'
import Store from './store'
import App from './App.vue'
import {permission, role} from "./directives/permission";
import DictTag from './views/component/DictTag.vue'
import DictScan from './views/component/DictScan.vue'
import InnerLink from './layouts/InnerLink.vue'

/**
 * 大坑，如果缺失src前的/，则会造成加载图片失败
 */
const app = createApp(App)
app.config.globalProperties.$SRC_ASSETS = "/src/assets/";
app.config.globalProperties.$ADMIN_PERMISSION = "*:*:*";
app.config.globalProperties.$ADMIN_ROLE = "admin";

app.use(Store);
app.use(Router);
app.component("DictTag", DictTag)
app.component("DictScan", DictScan)
app.component("InnerLink", InnerLink)

app.directive("permission", permission);
app.directive("role", role);
app.mount('#app');
export default app;
