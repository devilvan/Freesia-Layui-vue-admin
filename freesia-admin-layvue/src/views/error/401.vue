<template>
  <lay-card class="error-page">
    <lay-exception status="401" title="401" describe="抱歉，你无权访问该页面">
      <template #extra>
        <lay-button @click="refresh()">刷新</lay-button>
        <lay-button type="primary" @click="back()">返回</lay-button>
      </template>
    </lay-exception>
  </lay-card>
</template>

<style>
.error-page {
  padding-top: 200px;
  padding-bottom: 200px;
  margin: 10px;
}
</style>
<script setup lang="ts">
import router from "@/router";

function refresh() {
  window.location.reload()
}

function back() {
  router.back()
}
</script>