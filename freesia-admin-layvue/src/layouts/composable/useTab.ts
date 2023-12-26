import { computed, Ref, ref, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../../store/app";
import router from "../../router";

export function useTab() {

  const route = useRoute();
  const $router = router;
  const routes = $router.getRoutes()
  const currentPath = computed(() => route.path);
  const appStore = useAppStore();

  const tabs: Ref<any> = ref([]);
  const tabsCache: Ref<string[]> = ref([])
  const defaultTabsName = ['Workbench']

  if (routes) {
    routes.forEach(route => {
      let name = route.name as string;
      if (defaultTabsName.includes(name)) {
        tabs.value.push({
          meta: {
            ...route.meta,
            affix: true,
            closable: false,
          },
          id: route.path,
          name: route.name
        })
        tabsCache.value.push(name)
      }
      else if (route.meta.cache) {
        tabsCache.value.push(name)
      }
    })
  }

  if (route.path && !tabsCache.value.includes(route.name as string)) {
    // const path = routes.find(item => item.path === route.path)
    tabs.value.push({
      meta: {...route.meta},
      id: route.path,
      name: route.name,
    })
  }

  const to = (id: string) => {
    $router.push(id);
  };

  const close = (id: string) => {
    tabs.value = tabs.value.filter((ele: any) => ele.id != id);
    const latestView = tabs.value.slice(-1)[0]
    if (latestView) {
      to(latestView.id);
    }
  };

  const closeOpen = (id: string) => {
    let index = -1;
    for (let i = 0, len = tabs.value.length; i < len; i++) {
      if (tabs.value[i].id == currentPath.value) {
        index = i
        break
      }
    }
    tabs.value.splice(index, 1);
    tabs.value = tabs.value.filter((ele: any) => ele != id);
    to(id);
  }

  const closeAll = () => {
    tabs.value = tabs.value.filter((ele: any) => ele.meta.closable == false);
    to(tabs.value[0].id);
  };

  const closeCurrent = () => {
    let index = tabs.value.indexOf((ele: any) => ele.id == currentPath.value);
    tabs.value.splice(index, 1);
    tabs.value = tabs.value.filter((ele: any) => ele.id != currentPath.value);
    const latestView = tabs.value.slice(-1)[0]
    if (latestView) {
      to(latestView.id);
    }
  }

  const closeOther = () => {
    tabs.value = tabs.value.filter(
      (ele: any) => ele.meta.closable == false || ele.id == currentPath.value
    );
  };

  watch(route, () => {
    let bool = false;
    tabs.value.forEach((tab: any) => {
      if (tab.id === route.path) {
        bool = true;
        return ;
      }
    });
    if (!bool) {
      tabs.value.push({
        id: route.path,
        title: route.meta.title,
        name: route?.name,
        meta: {...route.meta}
      });
    }
    appStore.$patch((state) => {
      state.keepAliveList = tabs.value.map((item: any) => {
        if (item.meta.cache) {
          return item.name
        }
      }).filter((item: any) => item)
    })
  });

  return {
    to,
    close,
    closeAll,
    closeOpen,
    closeOther,
    closeCurrent,
    tabs,
    tabsCache,
    currentPath,
  };
}
