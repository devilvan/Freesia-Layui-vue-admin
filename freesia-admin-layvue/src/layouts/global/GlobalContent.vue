<template>
  <div class="global-content" :class="{ 'has-tab': appStore.tab }">
    <router-view v-slot="{ Component, route }" v-if="appStore.routerAlive">
      <keep-alive :include="useTab.tabsCache">
        <component v-if="!route.meta.link" :is="Component" :key="route.name"/>
      </keep-alive>
    </router-view>
  </div>
</template>

<script lang="ts">
export default {
  name: 'GlobalContent'
}
</script>

<script lang="ts" setup>
import {useAppStore} from '../../store/app'
import {useTab} from "../composable/useTab";

const appStore = useAppStore()
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
