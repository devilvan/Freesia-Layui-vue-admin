<template>
  <div class="container">
    <object :data="props.name" type="image/svg+xml"></object>
  </div>
  <lay-tooltip :visible="false" trigger="hover" :content="props.desc">
    <div class="oneRow">{{ props.desc }}</div>
  </lay-tooltip>

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
  height: 75%;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  height: inherit;
}

.container:hover {
  background-color: #ff9a9e;
}

.container object {
  height: 100%;
}

.oneRow {
  width: 80px;
}


</style>