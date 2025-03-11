<template>
  <div style="display: inline-flex; text-align: left">
    <lay-button size="sm" type="primary" @click="changeShowModalFlag">选择</lay-button>
    <div style="padding-left: 10px">
      <lay-input v-model="selectLabels" :allow-clear="true"
                 :disabled="true"></lay-input>
    </div>
  </div>

  <lay-layer v-model="showModalFlag" :title="props.title" :area="props.area">
    <lay-table
        ref="modalTableRef"
        class="table-box table-style"
        :page="pageQuery"
        :columns="props.columns"
        :loading="loading"
        :data-source="dataSource"
        :height="'550px'"
        v-model:selected-keys="selectKeys"
        @change="changeShowModalFlag"
    >
      <template v-slot:toolbar>
        <lay-button size="sm" type="normal" @click="changeShowModalFlag">
          <lay-icon class="layui-icon-addition"></lay-icon>
          查询
        </lay-button>
        <lay-button size="sm" type="danger" @click="confirm">
          <lay-icon class="layui-icon-addition"></lay-icon>
          确认
        </lay-button>
      </template>
    </lay-table>
  </lay-layer>
</template>

<script lang="ts">
export default {
  name: "PopFormItem",
}
</script>
<script lang="ts" setup>
/*INIT*/
import {findPageSysUserWithoutDataScope} from "../../../api/system/User";
import {onMounted, reactive, ref, watch} from "vue";
import {PageQuery} from "../../../types/Common";

const props = defineProps({
  modelValue: {
    type: Array,
    required: true
  },
  // 标题
  title: {
    type: String,
    default: "放大镜弹层",
  },
  // 窗口大小
  area: {
    type: [],
    default: ['1200px', '700px']
  },
  // 列表列
  columns: {
    type: Array,
    required: true
  },
  // 已选行号
  selectedKeys: {
    type: Array,
    default: []
  },
  selectedLabels: {
    type: Array,
    default: []
  },
});
watch(
    () => props.selectedLabels,
    (val) => {
      selectLabels.value = val;
    },
);
watch(
    () => props.selectedKeys,
    (val) => {
      selectKeys.value = val;
    },
);

// 监听 props.modelValue 的变化
const emit = defineEmits<{
  (e: 'callback', selectKeys: string[], rows: [], tableRef: object): void; // 更新 v-model
  (e: 'update:modelValue', rows: Array): void; // 更新 v-model
}>();
/*INIT*/

/*VAR*/
const showModalFlag = ref(false);
const loading = ref(false)
const selectKeys = ref<Array<string>>()
const selectRows = ref<Array<object>>([])
const selectLabels = ref<Array<string>>([])
const dataSource = ref([])
const modalSearchQuery = ref({});
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const modalTableRef = ref();
/*VAR*/

/*FUNCTION*/
function doFindPageUser() {
  findPageSysUserWithoutDataScope(modalSearchQuery.value, pageQuery).then((res: any) => {
    if (res.code == 200) {
      dataSource.value = res.rows;
      pageQuery.total = res.total;
    }
  })
}

function changeShowModalFlag() {
  loading.value = true
  setTimeout(() => {
    doFindPageUser()
    loading.value = false
  }, 200)
  showModalFlag.value = !showModalFlag.value
}


function confirm() {
  selectKeys.value = selectKeys;
  let checkData = modalTableRef.value.getCheckData();
  selectLabels.value = checkData.map(v => v.nickName)
  showModalFlag.value = !showModalFlag.value
  emit('confirm', selectKeys.value, checkData, modalTableRef);
  emit('update:modelValue', checkData.map(v => v.id));
}

/*FUNCTION*/
</script>

<style scoped>

</style>