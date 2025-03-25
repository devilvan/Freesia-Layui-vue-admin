<template>
  <lay-layer v-model="showModalFlag" :title="props.title" :area="props.area">
    <lay-table
        ref="modalTableRef"
        :page="pageQuery"
        :columns="props.columns"
        :loading="loading"
        :data-source="props.dataSource"
        v-model:selected-keys="selectKeys"
        @change="doModalChange"
    >
      <template v-slot:toolbar>
        <lay-button size="sm" type="normal" @click="doModalChange">
          <lay-icon class="layui-icon-addition"></lay-icon>
          查询
        </lay-button>
        <lay-button size="sm" type="danger" @click="modalConfirm">
          <lay-icon class="layui-icon-addition"></lay-icon>
          确认
        </lay-button>
      </template>
    </lay-table>
  </lay-layer>

</template>

<script lang="ts">
export default {
  name: "PopLayer"
}
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref, watch} from "vue";
import {PageQuery} from "@/types/Common";

/*INIT*/
const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  // 标题
  title: {
    type: String,
    default: "放大镜弹层",
  },
  // 窗口大小
  area: {
    type: Array,
    default: ['1200px', '700px']
  },
  // 列表列
  columns: {
    type: Array,
    required: true
  },
  // 链表值
  dataSource: {
    type: Array,
    required: true
  },
  // 链表值
  dataSource: {
    type: Array,
    required: true
  },
  // 查询方法
  modalChange: {
    type: Function,
  },
  selectedKeys: {
    type: Array,
    default: []
  },
});
onMounted(() => {
  if (props.modalChange) {
    props.modalChange()
  }
  selectKeys.value = props.selectedKeys
})
// 监听 props.modelValue 的变化
watch(
    () => props.modelValue,
    (val) => {
      showModalFlag.value = val;
    },
);
const emit = defineEmits<{
  (e: 'confirm', selectKeys: string[], rows: [], tableRef: object): void; // 更新 v-model
  (e: 'update:modelValue', value: boolean): void; // 更新 v-model
}>();
/*INIT*/

/*VAR*/
const showModalFlag = ref(false);
const loading = ref(false)
const selectKeys = ref<Array<string>>([])
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const modalSearchQuery = ref({})
const modalTableRef = ref();
/*VAR*/

/*FUNCTION*/
function doModalChange() {
  loading.value = true
  setTimeout(() => {
    if (props.modalChange) {
      props.modalChange()
    }
    loading.value = false
  }, 200)
}

function modalConfirm() {
  let checkDataList = modalTableRef.value.getCheckData();
  modalClose();
  emit('confirm', selectKeys.value, checkDataList, modalTableRef.value);
}

function modalClose() {
  showModalFlag.value = false
  emit('update:modelValue', false);
}

/*FUNCTION*/

</script>

<style scoped>

</style>