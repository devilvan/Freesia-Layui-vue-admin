<template>
  <div class="global-sys-column-modal">
    <div class="column-drag-list">
      <VueDraggable
          ref="el"
          v-model="localColumns"
          :animation="150"
          handle=".drag-handle"
          @update="onDragUpdate"
      >
        <div
            v-for="(column, columnIndex) in localColumns"
            :key="column.key || column.type || columnIndex"
            class="column-item"
        >
          <lay-icon class="drag-handle" type="layui-icon-more-vertical"></lay-icon>
          <DraggableLayCheckbox
              skin="primary"
              :value="columnIndex"
              :modelValue="!column.hide"
              @update:modelValue="(val: boolean) => onCheckboxChange(column, val)"
          >
            {{ column.title }}
          </DraggableLayCheckbox>
        </div>
      </VueDraggable>
      <div style="margin-top: 10px; display: flex; justify-content: flex-end;">
        <lay-button size="sm" type="primary" @click="doResorted">确认</lay-button>
        <lay-button size="sm" type="normal" @click="doCloseModal">取消</lay-button>
      </div>
    </div>
  </div>
</template>

<script lang='ts'>
export default {
  name: 'GlobalSysColumnModal'
}
</script>

<script setup lang="ts">
import {computed, h, onMounted, ref, watch} from 'vue'
import {VueDraggable} from 'vue-draggable-plus'
import DraggableLayCheckbox from "@/layouts/global/draggableLayCheckbox/DraggableLayCheckbox.vue";
import {TableColumn} from "@layui/layui-vue/types/component/table/typing";
import {resorted, toggleEnabled} from "@/api/system/ColumnDetail";
import {R} from "@/types/Result";
import {refresh} from "@/util/UCommon";
import {layer} from "@layui/layui-vue";

const props = defineProps({
  headerId: {
    type: String,
    default: ''
  },
  columns: {
    type: Array as () => TableColumn[],
    default: () => []
  },
  size: {
    type: String,
    default: '3em'
  },
  icon: {}
})

const emit = defineEmits(['update:columns', 'change', 'checkbox-change', 'drag-update'])

const localColumns = ref<TableColumn[]>([])

watch(() => props.columns, (newVal) => {
  localColumns.value = [...newVal] as TableColumn[]
}, {immediate: true, deep: true})

watch(localColumns, (newVal) => {
  emit('update:columns', newVal)
  emit('change', newVal)
}, {deep: true})

function onCheckboxChange(column: TableColumn, checked: boolean) {
  column.hide = !checked
  if (column.id) {
    toggleEnabled(column.id).then((res: R<void>) => res)
  }
  emit('checkbox-change', column)
}

function onDragUpdate() {
  emit('drag-update', localColumns.value)
}

onMounted(async () => {
})

function doResorted() {
  resorted(props.headerId, localColumns.value).then((res: R<void>) => {
    if (res.code === 200) {
      doCloseModal()
      refresh()
    }
  })
}

function doCloseModal() {
  layer.closeAll()
}
</script>

<style lang="less" scoped>
.global-sys-column-modal {
  padding: 10px;

  .column-drag-list {
    .column-item {
      display: flex;
      align-items: center;
      padding: 8px;
      margin-bottom: 4px;
      background-color: #fff;
      border: 1px solid #e6e6e6;
      border-radius: 4px;

      &:hover {
        background-color: #f5f5f5;
      }

      .drag-handle {
        margin-right: 8px;
        color: #999;
        cursor: move;

        &:active {
          cursor: grabbing;
        }
      }
    }
  }
}
</style>
