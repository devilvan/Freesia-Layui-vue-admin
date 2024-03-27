import {defineStore} from 'pinia'

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
    },
    persist: {
        storage: localStorage,
    }
})
