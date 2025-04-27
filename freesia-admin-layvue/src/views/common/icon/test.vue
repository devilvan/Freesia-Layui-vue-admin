<template>
  <lay-card>
    <lay-button @click="getCheckKeys">按钮</lay-button>
    <lay-tree
        class="layTreeContainer"
        :tail-node-icon="true"
        :data="menuTree"
        :checked="true"
        :spread="true"
        :leaf="true"
        :showCheckbox="menuTreeShowCheckbox"
        v-model:checkedKeys="checkKeys"
    >
      <template #title="{ data }">
        <lay-icon :class="data.icon"></lay-icon>
        {{ data.menuName }}
      </template>
    </lay-tree>
  </lay-card>
</template>

<script lang="ts">
export default {
  name: 'GoodTest'
}
</script>

<script setup lang="ts">
import {ref} from "vue";

const menuTreeShowCheckbox = ref(true)
const checkKeys = ref<Array<string>>([])
const menuTree = {
  "children": [
    {
      "icon": "layui-icon-home",
      "id": "1694620689140015104",
      "menuName": "用户管理",
      "orderNum": 10,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "icon": "layui-icon-group",
      "id": "1694620689140015105",
      "menuName": "角色管理",
      "orderNum": 20,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "icon": "layui-icon-transfer",
      "id": "1694620689144209408",
      "menuName": "部门管理",
      "orderNum": 30,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "icon": "layui-icon-menu-fill",
      "id": "1699961705634385920",
      "menuName": "菜单管理",
      "orderNum": 40,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "icon": "layui-icon-read",
      "id": "1700053335351676928",
      "menuName": "字典管理",
      "orderNum": 50,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "icon": "layui-icon-set",
      "id": "1705493891418607616",
      "menuName": "参数配置",
      "orderNum": 60,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "children": [
        {
          "icon": "layui-icon-note",
          "id": "1701162586564382720",
          "menuName": "登录日志",
          "orderNum": 10,
          "parentId": "1740627922028314624",
          "visible": "1"
        },
        {
          "icon": "layui-icon-tips",
          "id": "1701163569730211840",
          "menuName": "操作记录",
          "orderNum": 20,
          "parentId": "1740627922028314624",
          "visible": "1"
        }
      ],
      "icon": "layui-icon-log",
      "id": "1740627922028314624",
      "menuName": "日志管理",
      "orderNum": 70,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "icon": "layui-icon-friends",
      "id": "1753329711417856000",
      "menuName": "租户管理",
      "orderNum": 80,
      "parentId": "1694619883355496448",
      "visible": "1"
    },
    {
      "icon": "layui-icon-file-b",
      "id": "1757028904398770176",
      "menuName": "文件管理",
      "orderNum": 90,
      "parentId": "1694619883355496448",
      "visible": "1"
    }
  ],
  "icon": "layui-icon-set",
  "id": "1694619883355496448",
  "menuName": "系统管理",
  "orderNum": 100,
  "parentId": "-1",
  "visible": "1"
}
const result = ref<string[]>([])

function getCheckKeys() {
  console.log("checkKeys: " + checkKeys.value + ", size: " + checkKeys.value.length)
  console.log("result: " + result.value + ", size: " + result.value.length)
}

function searchNodeMethod(node: any, value: any) {
  console.log("node: " + node + ", value: " + value)
}

function checkChange(ve: any) {
  let set = new Set<string>()
  recursion(ve, set)
  console.log(set)
  result.value = checkKeys.value
  set.forEach(item => {
    result.value.push(item)
  })
}

function recursion(ve: any, set: Set<string>) {
  if (!ve.isChecked) {
    if (ve.parentNode) {
      set.add(recursion(ve.parentNode, set))
    }
    return ve.id
  }
}
</script>

<style scoped>

</style>