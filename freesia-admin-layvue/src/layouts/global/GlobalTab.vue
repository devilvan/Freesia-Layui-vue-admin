<template>
  <div class="global-tab" v-if="appStore.tab">
    <lay-tab
        :modelValue="currentPath"
        :allowClose="true"
        :activeBarTransition="true"
        @change="to"
        @close="close"
        @contextmenu.prevent="rightClickShowMenu()"
    >
      <template :key="tab" v-for="tab in $tab.tabs">
        <lay-tab-item :id="tab.id" :title="tab?.meta?.title" :closable="tab?.meta?.closable" :icon="tab.meta?.icon">
          <template #title>
            <span v-if="!tab.meta || !tab.meta?.icon || tab.meta?.icon === ''" class="dot"></span>
            {{ tab.meta.title }}
          </template>
        </lay-tab-item>
      </template>
    </lay-tab>
    <lay-dropdown>
      <lay-icon type="layui-icon-down"
                :class=" appStore.tagsTheme == 'designer' ? 'designer-last-icon' : ''"></lay-icon>
      <template #content>
        <lay-dropdown-menu>
          <lay-dropdown-menu-item @click="closeCurrent">关闭当前</lay-dropdown-menu-item>
        </lay-dropdown-menu>
        <lay-dropdown-menu>
          <lay-dropdown-menu-item @click="closeRight">关闭右侧标签</lay-dropdown-menu-item>
        </lay-dropdown-menu>
        <lay-dropdown-menu>
          <lay-dropdown-menu-item @click="closeOther">关闭其他</lay-dropdown-menu-item>
        </lay-dropdown-menu>
        <lay-dropdown-menu>
          <lay-dropdown-menu-item @click="closeAll">关闭全部</lay-dropdown-menu-item>
        </lay-dropdown-menu>
      </template>
    </lay-dropdown>
  </div>
</template>

<script lang="ts" setup>
import {useAppStore} from '../../store/app'
import {useTabStore} from '../composable/useTabStore'
import {computed, onMounted, ref, watch} from "vue";
import router from "../../router";
import {useRoute} from "vue-router";

const appStore = useAppStore()

const $tab = useTabStore()
const route = useRoute();
const routes = router.getRoutes()
const defaultTabsName = ['Workbench']
const currentPath = computed(() => route.path);
const stat = ref('关闭')

onMounted(() => {
  if (routes) {
    $tab.tabs = []
    $tab.tabsCache = []
    routes.forEach(r => {
      let name = r.name as string;
      if (defaultTabsName.includes(name)) {
        $tab.tabs.push({
          meta: {
            ...r.meta,
            affix: true,
            closable: false,
          },
          id: r.path,
          name: name
        })
        $tab.tabsCache.push(name)
      } else if (!r.meta.link && r.meta.cache) {
        $tab.tabsCache.push(name)
      }
    })
  }

  // !$tab.tabsCache.includes(route.name as string)
  if (route.path && !defaultTabsName.includes(route.name as string)) {
    // const path = routes.find(item => item.path === route.path)
    $tab.tabs.push({
      meta: {...route.meta},
      id: route.path,
      name: route.name,
    })
  }
})

watch(route, () => {
  $tab.currentPath = route.path
  let bool = false;
  $tab.tabs.forEach((tab: any) => {
    if (tab.id === route.path) {
      bool = true;
      return;
    }
  });
  if (!bool) {
    $tab.tabs.push({
      id: route.path,
      title: route.meta.title,
      name: route?.name,
      meta: {...route.meta}
    });
  }
});

function close(id: string) {
  $tab.close(id);
}

function to(id: string) {
  $tab.to(id);
}

function closeOpen(id: string) {
  $tab.closeOpen(id);
}

function closeAll() {
  $tab.closeAll()
}

function closeCurrent() {
  $tab.closeCurrent()
}

function closeRight() {
  $tab.closeRight();
}

function closeOther() {
  $tab.closeOther()
}

function rightClickShowMenu() {
  if (stat.value === '开启') {
    stat.value = '关闭'
  } else if (stat.value === '关闭') {
    stat.value = '开启'
  }
  console.log("右键点击", stat.value)
}
</script>

<style lang="less">
.global-tab {
  display: flex;
  position: relative;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  border-top: 1px solid whitesmoke;
  z-index: 999;
}

.global-tab .layui-tab {
  flex-grow: 1;
  width: calc(100% - 40px);
}

.global-tab .layui-tab .layui-tab-bar {
  border: none;
  border-left: 1px solid whitesmoke;
}

.global-tab .layui-tab .layui-tab-bar.prev {
  border-left: none;
}

.global-tab > i {
  width: 40px;
  background: white;
  height: 100%;
  line-height: 40px;
  text-align: center;
  border-left: 1px solid whitesmoke;
}

.global-tab .layui-tab .dot {
  display: inline-block;
  background-color: whitesmoke;
  margin-right: 8px;
  border-radius: 50px;
  height: 8px;
  width: 8px;
}

.global-tab .layui-tab .layui-this .dot {
  background-color: var(--global-primary-color);
}

.global-tab .layui-tab .layui-tab-close:hover {
  background: transparent !important;
  color: #e2e2e2 !important;
}

.designer {
  display: flex;
  width: calc(100% - 15px);
  height: 37px;
  position: relative;
  font-size: 14px;
  color: dimgray;
  cursor: pointer;

  .layui-tab .layui-tab-bar {
    height: 32px;
    line-height: 32px;
    margin-top: 5px;
  }

  .layui-tab .layui-tab-bar.prev {
    border-left: none;
    height: 32px;
    line-height: 32px;
    margin-top: 5px;
  }

  box-shadow: unset;
  z-index: 999;

  .designer-tab {
    display: inline-block;
    flex-grow: 1;
    width: 100%;
    padding-left: 15px;
  }

  .designer-tab-item {
    display: inline-block;
    height: 32px !important;
    line-height: 32px !important;
    padding: 0px 10px;
    margin-top: 5px;
    background-color: #fff;
    border-radius: 4px;
    margin-right: 5px;
  }

  .dot {
    display: inline-block;
    background-color: whitesmoke;
    margin-right: 8px;
    border-radius: 50px;
    height: 8px;
    width: 8px;
  }

  .designer-close {
    position: relative;
    display: inline-block;
    width: 18px;
    height: 18px;
    line-height: 20px;
    margin-left: 8px;
    top: 1px;
    text-align: center;
    font-size: 14px;
    color: var(--global-neutral-color-8);
    transition: all 0.2s;
    -webkit-transition: all 0.2s;
  }
}

.dot-this {
  background-color: var(--global-primary-color) !important;
}

.designer-last-icon {
  width: 32px !important;
  height: 32px !important;
  background: white;
  margin-top: 5px;
  line-height: 32px !important;
  text-align: center;
  border-radius: 4px;
}
</style>
