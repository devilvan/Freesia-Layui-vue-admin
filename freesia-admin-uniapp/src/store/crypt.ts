import { reactive } from 'vue'
import CryptoJS from 'crypto-js'

const state = reactive({
  pri2: '',
  aes: ''
})

export function useCryptStore() {
  const setPri2 = async (pri2: string) => {
    state.pri2 = pri2
  }

  const setAes = async (aes: string) => {
    state.aes = aes
  }

  const encryptAes = async (data: any) => {
    if (typeof data === 'object') {
      data = JSON.stringify(data)
    }
    const decryptAesKey = uni.$decryptedData(state.pri2, decodeURI(state.aes))
    return CryptoJS.AES.encrypt(data, CryptoJS.enc.Utf8.parse(decryptAesKey), {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7
    }).toString()
  }

  const decryptAes = async (data: string) => {
    const decryptAesKey = uni.$decryptedData(state.pri2, decodeURI(state.aes))
    const decrypt = CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse(decryptAesKey), {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7
    })
    return CryptoJS.enc.Utf8.stringify(decrypt)
  }

  return {
    state,
    setPri2,
    setAes,
    encryptAes,
    decryptAes
  }
}

export default state
