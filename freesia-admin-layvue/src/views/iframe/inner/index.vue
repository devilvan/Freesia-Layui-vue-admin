<template>
  <div>
    <InnerLink :key="url + '-' + title" :src="url" :iframeId="title"/>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRoute} from "vue-router";

const route = useRoute();
const title = route.meta.title;
const url = ref('/');
onMounted(() => {
  url.value = route.meta.link as string
})


</script>
