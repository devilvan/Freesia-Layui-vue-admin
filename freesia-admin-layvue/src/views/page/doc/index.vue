<template>
  <div>
    <InnerLink :src="url" :iframeId="url"/>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRoute} from "vue-router";

const route = useRoute();
const url = ref('/');
onMounted(() => {
  url.value = route.meta.link as string
})


</script>
