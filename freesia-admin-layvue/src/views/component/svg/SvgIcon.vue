<template>
  <div class="container">
    <object style="height: 80px" :data="props.name" type="image/svg+xml"></object>
    <div class="container-desc">{{ props.desc }}</div>
  </div>

</template>

<script setup lang="ts">

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  desc: {
    type: String,
    required: false,
    default: ''
  },
  color: {
    type: String,
    default: '#009688'
  },
})
</script>

<style scoped>
.container {
  height: inherit;
  width: inherit;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  text-align: center;
}

.container-desc {
  padding: 0 20%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>