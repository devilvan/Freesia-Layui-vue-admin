import {defineStore} from 'pinia'
// @ts-ignore
import {JSEncrypt} from "encryptlong";
import CryptoJS from "crypto-js";
import app from "../main";

export const useCryptStore = defineStore({
    id: 'crypt',
    state: () => {
        return {
            pri2: '',
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
        async encryptAes(data: any) {
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
        async decryptAes(data: string) {
            let decrypt = CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse(
                app.config.globalProperties.$decryptedData(this.pri2, decodeURI(this.aes))
            ), {
                mode: CryptoJS.mode.ECB,
                padding: CryptoJS.pad.Pkcs7,
            });
            return CryptoJS.enc.Utf8.stringify(decrypt);
        }
    },
    persist: {
        storage: localStorage,
    }
})
