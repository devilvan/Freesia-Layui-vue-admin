import {computed} from "vue";
import {useRoute} from "vue-router";
import {defineStore} from "pinia";
import router from "../../router";

const route = useRoute();

export const useTabStore = defineStore({
    id: 'tabStore',
    state: () => {
        return {
            tabs: [],
            tabsCache: [''],
            currentPath: ''
        }
    },
    actions: {
        async close(id: string) {
            this.tabs = this.tabs.filter((ele: any) => ele.id != id);
            const latestView = this.tabs.slice(-1)[0]
            if (latestView) {
                this.to(latestView.id);
            }
        },
        async to(id: string) {
            await router.push(id);
        },
        async closeOpen(id: string) {
            let index = -1;
            for (let i = 0, len = this.tabs.length; i < len; i++) {
                if (this.tabs[i].id == this.currentPath) {
                    index = i
                    break
                }
            }
            this.tabs.splice(index, 1);
            this.tabs = this.tabs.filter((ele: any) => ele != id);
            this.to(id);
        },
        async closeAll() {
            this.tabs = this.tabs.filter((ele: any) => ele.meta.closable == false);
            this.to(this.tabs[0].id);
        },
        async closeCurrent() {
            let index = this.tabs.indexOf((ele: any) => ele.id == this.currentPath);
            this.tabs.splice(index, 1);
            this.tabs = this.tabs.filter((ele: any) => ele.id != this.currentPath);
            const latestView = this.tabs.slice(-1)[0]
            if (latestView) {
                this.to(latestView.id);
            }
        },
        async closeOther() {
            this.tabs = this.tabs.filter(
                (ele: any) => ele.meta.closable == false || ele.id == this.currentPath
            );
        },
    }
})
