import { createSSRApp } from "vue";
import App from "./App.vue";
import { JSEncrypt } from "encryptlong";
import { getPublicKey, wrapEncryptPub2 } from "./api/Crypt";
import { useCryptStore } from "./store/crypt";
import MessageBell from "./components/MessageBell.vue";

export function createApp() {
  const app = createSSRApp(App);
  app.component('MessageBell', MessageBell);
  
  /**
   * 加密数据
   * @param publicKey 公钥
   * @param data 待加密的数据
   */
  uni.$encryptedData = function (publicKey, data) {
    let encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);
    return encrypt.encryptLong(data);
  };

  /**
   * 解密数据
   * @param privateKey 私钥
   * @param data 待解密的数据
   */
  uni.$decryptedData = function (privateKey, data) {
    let decrypt = new JSEncrypt();
    decrypt.setPrivateKey(privateKey);
    return decrypt.decryptLong(data);
  };

  /**
   * 获取RSA公钥
   */
  uni.$getPublicKey = async function () {
    const crypt = useCryptStore();
    try {
      const res = await getPublicKey();
      if (res.code === 200) {
        const pub1 = res.data;
        const rsa = new JSEncrypt();
        const pub2 = rsa.getPublicKeyB64();
        const pri2 = rsa.getPrivateKeyB64();
        await crypt.setPri2(pri2);
        
        let encryptPub2 = uni.$encryptedData(pub1, pub2);
        encryptPub2 = encodeURI(encryptPub2);
        
        const wrapEncryptPub2Res = await wrapEncryptPub2(encryptPub2);
        if (wrapEncryptPub2Res.code === 200) {
          await crypt.setAes(wrapEncryptPub2Res.data);
        }
      }
    } catch (e) {
      console.error('获取公钥失败', e);
    }
  };

  return {
    app,
  };
}
