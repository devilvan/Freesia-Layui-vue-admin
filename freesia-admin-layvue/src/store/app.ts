import {defineStore} from 'pinia'

export const useAppStore = defineStore({
    id: 'app',
    state: () => {
        return {
            tab: true,
            logo: true,
            level: true,
            inverted: false,
            routerAlive: true,
            collapse: false,
            subfield: false,
            locale: "zh_CN",
            subfieldPosition: "side",
            theme: 'light',
            breadcrumb: true,
            sideWidth: "220px",
            sideTheme: 'dark',
            greyMode: false,
            accordion: true,
            tagsTheme: 'concise',
            themeVariable: {
                "--global-checked-color": "#5fb878",
                "--global-primary-color": "#009688",
                "--global-normal-color": "#1e9fff",
                "--global-danger-color": "#ff5722",
                "--global-warm-color": "#ffb800",
                "--global-border-radius": "0px",
                // --global-neutral-color-1: "#FAFAFA";
                // --global-neutral-color-2: "#F6F6F6";
                // --global-neutral-color-3: "#eeeeee";
                // --global-neutral-color-4: "#e2e2e2";
                // --global-neutral-color-5: "#dddddd";
                // --global-neutral-color-6: "#d2d2d2";
                // --global-neutral-color-7: "#cccccc";
                // --global-neutral-color-8: "#c2c2c2";

            },
            currentTenant: '',
            commonIconHeader: '',
        }
    },
    persist: {
        storage: localStorage,
        paths: ['tab', 'locale', 'theme', 'logo', 'level', 'inverted', 'breadcrumb', 'sideTheme', 'greyMode', 'accordion', 'themeVariable', 'subfield', 'tagsTheme', 'currentTenant'],
    }
})
