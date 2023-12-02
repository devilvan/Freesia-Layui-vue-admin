import { computed, Ref, ref, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../../store/app";

export function useTab() {

  const route = useRoute();
  const router = useRouter();
  const routes = router.getRoutes()
  const currentPath = computed(() => route.path);
  const appStore = useAppStore();

  const tabs: Ref<any> = ref([]);
  const tabsCache: string[] = []
  const defaultTabsPath = ['Workbench']

  if (routes) {
    routes.forEach(route => {
      if (defaultTabsPath.includes(route.name as string)) {
        tabs.value.push({
          meta: {
            ...route.meta,
            affix: true,
            closable: false,
          },
          id: route.path,
          name: route?.name
        })
        tabsCache.push(route.path)
      }
    })
  }

  if (route.path && !tabsCache.includes(route.path)) {
    const path = routes.find(item => item.path === route.path)
    path && tabs.value.push({
      meta: {...path.meta},
      id: route.path,
      name: route?.name,
    })
  }

  const to = (id: string) => {
    router.push(id);
  };

  const close = (id: string) => {
    tabs.value = tabs.value.filter((ele: any) => ele.id != id);
  };

  const closeAll = () => {
    tabs.value = tabs.value.filter((ele: any) => ele.meta.closable == false);
    to(tabs.value[0].id);
  };

  const closeCurrent = () => {
    tabs.value = tabs.value.filter((ele: any) => ele.id != currentPath.value);
    to(tabs.value[0].id);
  }

  const closeOther = () => {
    tabs.value = tabs.value.filter(
      (ele: any) => ele.meta.closable == false || ele.id == currentPath.value
    );
  };

  watch(route, () => {
    let bool = false;
    // $patch需要放前面，否则keepAliveList不能及时更新导致缓存标签失效
    appStore.$patch((state) => {
      state.keepAliveList = tabs.value.map((item: any) => item?.name).filter((item: any) => item)
    })
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
  });

  return {
    to,
    close,
    closeAll,
    closeOther,
    closeCurrent,
    tabs,
    currentPath,
  };
}
