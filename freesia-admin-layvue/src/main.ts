import {createApp} from 'vue'
import Router from './router'
import Store from './store'
import App from './App.vue'
import {permission, role} from "./directives/permission";
import DictTag from './views/component/DictTag.vue'
import DictScan from './views/component/DictScan.vue'
import InnerLink from './layouts/InnerLink.vue'
import {MenuPermission} from "./types/Permission";
// @ts-ignore
import {JSEncrypt} from "encryptlong";
import {getPublicKey, wrapEncryptPub2} from "./api/Crypt";
import {useCryptStore} from "./store/crypt";

/**
 * 大坑，如果缺失src前的/，则会造成加载图片失败
 */
const app = createApp(App)
app.config.globalProperties.$SRC_ASSETS = "/src/assets/";
app.config.globalProperties.$ADMIN_PERMISSION = "*:*:*";
app.config.globalProperties.$ADMIN_ROLE = "admin";
app.config.globalProperties.$MENU_PERMISSION = MenuPermission

app.use(Store);
app.use(Router);
app.component("DictTag", DictTag)
app.component("DictScan", DictScan)
app.component("InnerLink", InnerLink)

app.directive("permission", permission);
app.directive("role", role);
app.mount('#app');

/**
 * 待加密数据
 * @param publicKey 公钥
 * @param data 待加密的数据
 */
app.config.globalProperties.$encryptedData = function (publicKey: string, data: string) {
    let encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);
    return encrypt.encryptLong(data)
}

app.config.globalProperties.$decryptedData = function (privateKey: string, data: string) {
    let decrypt = new JSEncrypt();
    decrypt.setPrivateKey(privateKey);
    return decrypt.decryptLong(data)
}

/**
 * 获取RSA公钥，挂载到Vue上，可通过this.$getPublicKey调用
 */
app.config.globalProperties.$getPublicKey = function () {
    let crypt = useCryptStore();
    getPublicKey().then((res: any) => {
        if (res.code === 200) {
            //将获取到得公钥，又保存在Vue原形上的自定义变量$PUB1上
            let pub1 = res.data;
            let rsa = new JSEncrypt();
            // let pub2 = removeStartEndWith(rsa.getPublicKey());
            let pub2 = rsa.getPublicKeyB64();
            let pri2 = rsa.getPrivateKeyB64();
            crypt.setPri2(pri2).then(r => r);
            // 根据PUB1加密后的PUB2
            let encryptPub2 = this.$encryptedData(pub1, pub2);
            encryptPub2 = encodeURI(encryptPub2)
            wrapEncryptPub2(encryptPub2).then((wrapEncryptPub2Res: any) => {
                if (wrapEncryptPub2Res.code === 200) {
                    let decryptAes = this.$decryptedData(pri2, decodeURI(wrapEncryptPub2Res.data));
                    crypt.setAes(decryptAes).then(r => r);
                }
            })
        }
    })
}

export default app;
