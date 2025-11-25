<template>
  <div class="global-content" :class="{ 'has-tab': appStore.tab }">
    <router-view v-slot="{ Component, route }" v-if="appStore.routerAlive">
      <div style="position: relative" class="layui-scroll-test">
        <lay-watermark :content="getContent()" :fontSize="`12pt`" font="20px Microsoft Yahei"
                       element-box=".layui-scroll-test" :rotate="-36" :maxTotal="10"></lay-watermark>
        <lay-scroll height="100%" style="background-color: #ffffff; position: relative" thumbColor="#000000">
          <!-- 注意：include里必须是ref形式的值（$tab.tabsCache），不能加.value，否则失效-->
          <keep-alive :include="$tab.tabsCache">
            <component :is="Component" :key="route.name"/>
          </keep-alive>
        </lay-scroll>
      </div>
      <lay-tooltip trigger="hover" content="待办事项" position="left">
        <lay-backtop @click="changeDrawer" :showHeight="0" :bottom="100" :bgcolor="backTopColor" circle disabled>
          <lay-badge type="rim" value="10" position="top-right">
            <lay-icon type="layui-icon-success" size="50px"></lay-icon>
          </lay-badge>
        </lay-backtop>
      </lay-tooltip>
      <lay-tooltip trigger="hover" content="返回顶部" position="left">
        <lay-backtop :showHeight="700" :bgcolor="backTopColor" :circle="true">
          <lay-icon type="layui-icon-top" size="50px"></lay-icon>
        </lay-backtop>
      </lay-tooltip>
    </router-view>
  </div>
</template>

<script lang="ts" setup>
import {useAppStore} from '@/store/app'
import {useTabStore} from "../composable/useTabStore";
import {useUserStore} from "@/store/user";
import {formatDateTime} from '@/util/UDate'
import {h, ref} from "vue";
import TodoModal from "@/layouts/global/TodoModal.vue";
import {layer} from "@layui/layui-vue";

const appStore = useAppStore()
const $tab = useTabStore()
const $userInfo = useUserStore().userInfo;
const backTopColor = ref(appStore.themeVariable['--global-primary-color'])
const todoModalFlag = ref<boolean>(false)
const drawerId = ref();

function getContent() {
  return $userInfo.userName + ' ' + formatDateTime(new Date(), 'yyyy-MM-dd HH:mm:ss')
}

function changeDrawer() {
  if (!drawerId.value) {
    drawerId.value = layer.drawer({
      title: "代办事项",
      area: ['600px', '100%'],
      content: h(TodoModal),
      close: () => closeDrawer()
    })
  } else {
    closeDrawer()
  }
}

function closeDrawer() {
  layer.close(drawerId.value);
  drawerId.value = null
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
