import {defineStore} from 'pinia'
// @ts-ignore
import {JSEncrypt} from "encryptlong";
import CryptoJS from "crypto-js";
import app from "../main";
import {getPublicKey, wrapEncryptPub2} from "../api/Crypt";

// 密钥改为纯内存存储，不再持久化到浏览器storage；
// 此处清理历史版本遗留的明文私钥（persist插件默认以store id作为storage key）
if (typeof window !== 'undefined') {
    localStorage.removeItem('crypt');
}

// 共享的密钥交换Promise，避免并发的加密调用重复触发交换
let readyPromise: Promise<void> | null = null;
// 定时重取定时器句柄
let renewTimer: ReturnType<typeof setTimeout> | null = null;

// 定时重取包裹密钥的间隔，需小于后端 freesia.crypt.aes-rotation-minutes（默认60分钟）
const AES_RENEW_INTERVAL_MS = 30 * 60 * 1000;

export const useCryptStore = defineStore({
    id: 'crypt',
    state: () => {
        return {
            pri2: '',
            pub2: '',
            aes: '',
        }
    },
    actions: {
        async setPri2(pri2: string) {
            this.pri2 = pri2;
        },
        async setAes(aes: string) {
            this.aes = aes;
        },
        async encryptRsa(publicKey: string, data: string) {
            let encrypt = new JSEncrypt();
            encrypt.setPublicKey(publicKey);
            return encrypt.encryptLong(data)
        },
        async decryptRsa(privateKey: string, data: string) {
            let decrypt = new JSEncrypt();
            decrypt.setPrivateKey(privateKey);
            return decrypt.decryptLong(data)
        },
        // 完整密钥交换：获取后端公钥 -> 生成客户端密钥对 -> 交换AES包裹密钥
        async initKeys() {
            const pubRes = await getPublicKey();
            if (pubRes.code !== 200) return false;
            let rsa = new JSEncrypt();
            let pub2 = rsa.getPublicKeyB64();
            let pri2 = rsa.getPrivateKeyB64();
            this.pub2 = pub2;
            this.pri2 = pri2;
            let encryptPub2 = await this.encryptRsa(pubRes.data, pub2);
            const wrapRes = await wrapEncryptPub2(encodeURI(encryptPub2));
            if (wrapRes.code !== 200) return false;
            this.aes = wrapRes.data;
            this.scheduleRenew();
            return true;
        },
        // 复用客户端密钥对重取AES包裹密钥（服务端轮换后保持密钥新鲜）
        async refreshAesKey() {
            // 客户端密钥对尚未生成（如启动失败），回退完整交换
            if (!this.pub2) {
                await this.initKeys();
                return;
            }
            const pubRes = await getPublicKey();
            if (pubRes.code !== 200) throw new Error('密钥刷新失败');
            const encryptPub2 = await this.encryptRsa(pubRes.data, this.pub2);
            const wrapRes = await wrapEncryptPub2(encodeURI(encryptPub2));
            if (wrapRes.code !== 200) throw new Error('密钥刷新失败');
            this.aes = wrapRes.data;
        },
        // 定时重取，保证密钥先于服务端轮换周期更新；失败则顺延下次
        async scheduleRenew() {
            if (renewTimer) clearTimeout(renewTimer);
            renewTimer = setTimeout(() => {
                this.refreshAesKey().finally(() => {
                    this.scheduleRenew();
                });
            }, AES_RENEW_INTERVAL_MS);
        },
        // 确保密钥就绪；首个加密调用会触发并等待密钥交换完成
        async ensureKeys() {
            if (this.pri2 && this.aes) return;
            if (!readyPromise) {
                readyPromise = this.initKeys().then((ok) => {
                    if (!ok) {
                        readyPromise = null;
                        throw new Error('密钥交换失败');
                    }
                }).catch((e) => {
                    readyPromise = null;
                    throw e;
                });
            }
            await readyPromise;
        },
        async encryptAes(data: any) {
            await this.ensureKeys();
            if (typeof (data) === 'object') {
                data = JSON.stringify(data);
            }
            return CryptoJS.AES.encrypt(data, CryptoJS.enc.Utf8.parse(
                app.config.globalProperties.$decryptedData(this.pri2, decodeURI(this.aes))
            ), {
                mode: CryptoJS.mode.ECB,
                padding: CryptoJS.pad.Pkcs7,
            }).toString();
        },
        doDecrypt(data: string) {
            let decrypt = CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse(
                app.config.globalProperties.$decryptedData(this.pri2, decodeURI(this.aes))
            ), {
                mode: CryptoJS.mode.ECB,
                padding: CryptoJS.pad.Pkcs7,
            });
            return CryptoJS.enc.Utf8.stringify(decrypt);
        },
        async decryptAes(data: string) {
            await this.ensureKeys();
            try {
                const plain = this.doDecrypt(data);
                // 错钥解密得到非法UTF-8（含替换符�）或抛错，视为服务端已轮换，重取后重试一次
                if (plain.includes('�')) {
                    throw new Error('AES密钥已过期');
                }
                return plain;
            } catch (e) {
                await this.refreshAesKey();
                return this.doDecrypt(data);
            }
        }
    },
})
