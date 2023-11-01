<template>
  <div>
    <inner-link v-model:src="url"></inner-link>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import {useRoute} from "vue-router";

const route = useRoute();
const url = ref('/');
onMounted(() => {
  url.value = route.meta.link
})


</script>
