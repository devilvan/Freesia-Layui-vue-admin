<template>
  <div class="global-content" :class="{ 'has-tab': appStore.tab }">
    <router-view v-slot="{ Component, route }" v-if="appStore.routerAlive">
      <div style="position: relative" class="layui-scroll-test">
        <lay-watermark :content="getContent()" :fontSize="`12pt`" font="20px Microsoft Yahei"
                       element-box=".layui-scroll-test" :rotate="-36" :maxTotal="10"></lay-watermark>
        <lay-scroll height="100%" style="background-color: #ffffff; position: relative" thumbColor="#000000">
          <keep-alive :include="$tab.tabsCache.value">
            <component :is="Component" :key="route.name"/>
          </keep-alive>
        </lay-scroll>
      </div>
    </router-view>
  </div>
</template>

<script lang="ts" setup>
import {useAppStore} from '../../store/app'
import {useTab} from "../composable/useTab";
import {useUserStore} from "../../store/user";
import {formatDateTime} from '../../util/UDate'

const appStore = useAppStore()
const $tab = useTab()
const $userInfo = useUserStore().userInfo;

function getContent() {
  return $userInfo.userName + ' ' + formatDateTime(new Date(), 'yyyy-MM-dd HH:mm:ss')
}
</script>

<style scoped>
.global-content {
  height: 100%;
  overflow: auto;
}

.global-content.has-tab {
  height: calc(100% - 46px);
  overflow: auto;
}

.global-content::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.global-content::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: #e2e2e2;
}
</style>
