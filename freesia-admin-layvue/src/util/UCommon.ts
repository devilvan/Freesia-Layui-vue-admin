import {useAppStore} from "../store/app";
import {layer} from "@layui/layui-vue";

const appStore = useAppStore();

export function refresh() {
    appStore.routerAlive = false
    setTimeout(() => {
        appStore.routerAlive = true
    }, 200)
}

export function close(id: any) {
    layer.close(id);
}