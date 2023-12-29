<template>
  <div>
    <InnerLink v-model:src="url"></InnerLink>
  </div>
</template>

<script setup lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import {useRoute} from "vue-router";
import InnerLink from "../../../layouts/InnerLink.vue";

defineComponent({
  components: {InnerLink}
})

const route = useRoute();
const url = ref('/');
onMounted(() => {
  url.value = route.meta.link as string
})


</script>
