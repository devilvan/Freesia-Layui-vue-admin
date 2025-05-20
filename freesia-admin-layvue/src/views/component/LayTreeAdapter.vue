<template>
  <lay-card>
    <lay-button @click="getCheckKeys">按钮</lay-button>
    <lay-tree
        :data="props.data"
        :tailNodeIcon="props.tailNodeIcon"
        :default-expand-all="props.defaultExpandAll"
        :showCheckbox="props.showCheckbox"
        @check-change="checkChange"
        v-model:checkedKeys="internalCheckedKeys"
        :value="modelValue"
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
  name: 'LayTreeAdapter'
}
</script>

<script setup lang="ts">
/*
* 处理2.18.0版本后,lay-tree不再支持子节点未完全选择时，父节点仍然保留的问题
* */
import {ref, watch} from "vue";

/*INIT*/
const props = defineProps({
  modelValue: {
    type: Array<String>
  },
  checkedKeys: {
    type: Array<String>
  },
  keys: {
    type: Array<String>
  },
  data: {
    type: Object,
    required: true
  },
  tailNodeIcon: {
    type: Boolean,
    required: false,
    default: false
  },
  defaultExpandAll: {
    type: Boolean,
    required: false,
    default: false
  },
  showCheckbox: {
    type: Boolean,
    required: false,
    default: false
  },

})

const emit = defineEmits<{
  (e: 'update:modelValue', value: Array<String>): void
  (e: 'update:keys', value: Array<String>): void
}>()
/*INIT*/

/*VAR*/
const result = ref<string[]>(props.modelValue || [])
// 内部状态
const internalCheckedKeys = ref<string[]>(props.checkedKeys || [])
/*VAR*/

/*FUNCTION*/
// watch(internalCheckedKeys, (newVal) => {
//   emit('update:keys', newVal)
// }, { deep: true })

function getCheckKeys() {
  console.log("result: " + result.value)
}

function checkChange(ve: any) {
  const newCheckedKeys = [...result.value];

  if (ve.isChecked) {
    // 选中节点时，添加当前节点和所有父节点
    if (!newCheckedKeys.includes(ve.id)) {
      newCheckedKeys.push(ve.id);
    }
    addParentKeys(ve, newCheckedKeys);

    // 如果是父节点，递归选中所有子节点
    if (ve.children && ve.children.length > 0) {
      addChildrenKeys(ve.children, newCheckedKeys);
    }
  } else {
    // 取消节点时，先移除当前节点
    const index = newCheckedKeys.indexOf(ve.id);
    if (index > -1) {
      newCheckedKeys.splice(index, 1);
    }

    // 如果是父节点，同时移除所有子节点
    if (ve.children && ve.children.length > 0) {
      removeAllChildrenKeys(ve.children, newCheckedKeys);
    }

    // 检查父节点是否需要移除
    if (ve.parentNode) {
      checkParentNodeRemoval(ve.parentNode, newCheckedKeys);
    }
  }
  result.value = newCheckedKeys;
  emit('update:modelValue', result.value)
  // emit('update:keys', result.value)
}

// 移除所有子节点的key
function removeAllChildrenKeys(children: any[], keys: string[]) {
  children.forEach(child => {
    const index = keys.indexOf(child.id);
    if (index > -1) {
      keys.splice(index, 1);
    }
    if (child.children && child.children.length > 0) {
      removeAllChildrenKeys(child.children, keys);
    }
  });
}

// 检查父节点是否需要移除
function checkParentNodeRemoval(parentNode: any, keys: string[]) {
  const hasSelectedChildren = parentNode.children.some(
      (child: any) => keys.includes(child.id)
  );

  if (!hasSelectedChildren) {
    const parentIndex = keys.indexOf(parentNode.id);
    if (parentIndex > -1) {
      keys.splice(parentIndex, 1);
    }

    if (parentNode.parentNode) {
      checkParentNodeRemoval(parentNode.parentNode, keys);
    }
  }
}

// 添加所有父节点的key
function addParentKeys(node: any, keys: string[]) {
  if (node.parentNode) {
    if (!keys.includes(node.parentNode.id)) {
      keys.push(node.parentNode.id);
    }
    addParentKeys(node.parentNode, keys);
  }
}

// 添加所有子节点的key
function addChildrenKeys(children: any[], keys: string[]) {
  children.forEach(child => {
    if (!keys.includes(child.id)) {
      keys.push(child.id);
    }
    if (child.children && child.children.length > 0) {
      addChildrenKeys(child.children, keys);
    }
  });
}

/*FUNCTION*/

</script>

<style scoped>

</style>