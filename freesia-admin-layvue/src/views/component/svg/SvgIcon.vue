<template>
  <svg :height="props.size" :width="props.size" aria-hidden="true" class="svg-icon">
    <use :fill="props.color" :xlink:href="symbolId"/>
  </svg>
</template>

<script setup>
import {computed} from 'vue'

const props = defineProps({
  prefix: {
    type: String,
    default: 'icon'
  },
  name: {
    type: String,
    required: true
  },
  color: {
    type: String,
    default: '#009688'
  },
  size: {
    type: String,
    default: '1em'
  }
})

// const symbolId = computed(() => `#${props.prefix}-${props.name}`)
const symbolId = computed(() => `#icon-${props.name}`)
</script>
<!--<style scoped>-->
<!--.svg-icon {-->
<!--  position: relative;-->
<!--  fill: currentColor;-->
<!--  vertical-align: -2px;-->
<!--}-->
<!--</style>-->