import CryptoJS from 'crypto-js';
// @ts-ignore
import {JSEncrypt} from "encryptlong";
import {useCryptStore} from "../store/crypt";

const $crypt = useCryptStore();

export function encryptRsa(publicKey: string, data: string) {
    let encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);
    return encrypt.encryptLong(data)
}

export function decryptRsa(privateKey: string, data: string) {
    let decrypt = new JSEncrypt();
    decrypt.setPrivateKey(privateKey);
    return decrypt.decryptLong(data)
}

export function encryptAes(data: string) {
    return CryptoJS.AES.encrypt(data, CryptoJS.enc.Utf8.parse($crypt.aes), {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.ZeroPadding,
    }).toString();
}

export function decryptAes(data: string) {
    let decrypt = CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse($crypt.aes), {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.ZeroPadding,
    });
    let s = CryptoJS.enc.Utf8.stringify(
        decrypt
    );
    return s
}
