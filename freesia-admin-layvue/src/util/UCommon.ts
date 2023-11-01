import {useAppStore} from "../store/app";

const appStore = useAppStore();

export function refresh() {
    appStore.routerAlive = false
    setTimeout(() => {
        appStore.routerAlive = true
    }, 200)
}
