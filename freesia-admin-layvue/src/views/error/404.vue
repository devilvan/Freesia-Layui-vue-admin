<template>
  <lay-card class="error-page">
    <lay-exception status="404" title="404" describe="抱歉，你访问的页面不存在">
      <template #extra>
        <lay-button>刷新</lay-button>
        <lay-button type="primary" @click="turnBack()">返回</lay-button>
      </template>
    </lay-exception>
  </lay-card>
</template>

<script lang="ts" setup>

import router from "../../router";

function turnBack() {
  router.back()
}
</script>

<style>
.error-page {
  padding-top: 200px;
  padding-bottom: 200px;
  margin: 10px;
}
</style>
