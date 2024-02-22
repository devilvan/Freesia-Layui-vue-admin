<template>
  <router-view></router-view>
</template>
<script setup lang="ts">
import {onMounted} from "vue";
import {useAppStore} from "./store/app";

const appStore = useAppStore()
onMounted(() => {
  document.title = appStore.title;
})
</script>
<style>
@import "./styles/index.css";

#app {
  height: 100%;
}

body {
  height: 100%;
}

html {
  height: 100%;
}
</style>
