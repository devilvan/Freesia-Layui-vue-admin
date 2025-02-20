<template>
  <lay-breadcrumb>
    <lay-breadcrumb-item v-if="breadcrumbs && breadcrumbs.length > 0" v-for="(breadcrumb, index) in breadcrumbs"
                         :key="index">
      {{ breadcrumb.title }}
    </lay-breadcrumb-item>
    <lay-breadcrumb-item v-else>
      {{ route.meta?.title }}
    </lay-breadcrumb-item>
  </lay-breadcrumb>
</template>

<script lang="ts">
export default {
  name: "GlobalBreadcrumb"
}
</script>


<script lang="ts" setup>
import {computed} from "vue";
import {useRoute} from "vue-router";
import {getParents} from "../../library/treeUtil";
import {useUserStore} from "../../store/user";

const userStore = useUserStore();
const route = useRoute();
const breadcrumbs = computed(() => {
  return getParents(userStore.menus, route.path)?.reverse();
})
</script>
