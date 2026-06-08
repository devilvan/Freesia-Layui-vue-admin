import {reactive} from 'vue'

const state = reactive({
    allTenantFlag: false
})

export function useAccountCostStore() {
    const changeAllTenantFlag = () => {
        state.allTenantFlag = !state.allTenantFlag
    }

    return {
        state,
        allTenantFlag: state.allTenantFlag,
        changeAllTenantFlag
    }
}

export default state
