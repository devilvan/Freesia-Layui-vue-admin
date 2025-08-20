import {defineStore} from 'pinia'

export const useAccountCostStore = defineStore({
    id: 'accountCost',
    state: () => {
        return {
            allTenantFlag: false,
        }
    },
    actions: {
        changeAllTenantFlag() {
            this.allTenantFlag = !this.allTenantFlag;
        },
    },
    persist: {
        storage: localStorage,
        paths: ['allTenantFlag'],
    },
})

