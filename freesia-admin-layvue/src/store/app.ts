import { defineStore } from 'pinia'

export const useAppStore = defineStore({
  id: 'app',
  state: () => {
    return {
      title: import.meta.env.VITE_APP_TITLE,
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
      },
      currentTenant: '',
    }
  },
  actions: {
    async setTitle(title: string) {
      this.title = title;
    }
  },
  persist: {
    storage: localStorage,
    paths: ['title', 'tab', 'locale', 'theme', 'logo', 'level', 'inverted', 'breadcrumb', 'sideTheme', 'greyMode', 'accordion', 'themeVariable', 'subfield', 'tagsTheme', 'currentTenant'],
  }
})
