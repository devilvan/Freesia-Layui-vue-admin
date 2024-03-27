import Http from "./Http";

export const getPublicKey = function () {
    return Http.post('/common/cryptController/getPublicKey')
}

export const wrapEncryptPub2 = function (encryptPub2: string) {
    let params = {
        encryptPub2: encryptPub2
    }
    return Http.post('/common/cryptController/wrapEncryptPub2', params)
}
