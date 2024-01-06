<template>
  <div>
    <InnerLink :src="link" :iframeId="title"/>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRoute} from "vue-router";

const route = useRoute();
const title = route.meta.title;
const link = ref('/');
onMounted(() => {
  link.value = route.meta.link as string
})


</script>
