<template>
  <div :style="'height:' + height">
    <iframe
        :id="iframeId"
        style="width: 100%; height: 100%"
        :src="src"
        frameborder="yes"
    ></iframe>
  </div>
</template>

<script lang="ts" setup>
import {ref} from "vue";

const props = defineProps({
  src: {
    type: String,
    default: "/"
  },
  iframeId: {
    type: String
  }
});

// const height = ref(document.documentElement.clientHeight + "px");
const height = ref(820 + "px");
</script>

<style>
layui-body {
  display: flex;
  background: #5FB878;
  align-items: center;
  justify-content: center;
  color: white;
}
</style>
