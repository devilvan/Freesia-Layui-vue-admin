<template>
  <lay-row>
    <lay-col :md="12">
      <lay-tooltip
          trigger="hover"
          :middlewares="tooltipMiddlewares"
          :popper-style="tooltipPopperStyle">
        <scan class="allocContent">
          <lay-icon type="layui-icon-add-circle"/>
          应收金额：￥{{ dataSource.totalCollected }}
        </scan>
        <template #content>
          <div class="hoverTable">
            <lay-table
                ref="collectedTableRef"
                :columns="collectedColumns"
                :data-source="dataSource.collected"
                :default-toolbar="false"
                :even="true">
            </lay-table>
          </div>
        </template>
      </lay-tooltip>
    </lay-col>
    <lay-col :md="12">
      <lay-tooltip
          trigger="hover"
          :middlewares="tooltipMiddlewares"
          :popper-style="tooltipPopperStyle">
        <scan class="allocContent">
          <lay-icon type="layui-icon-reduce-circle"/>
          应付金额：￥{{ dataSource.totalAllocated }}
        </scan>
        <template #content>
          <div class="hoverTable">
            <lay-table
                ref="allocatedTableRef"
                :columns="allocatedColumns"
                :data-source="dataSource.allocated"
                :default-toolbar="false"
                :even="true">
            </lay-table>
          </div>
        </template>
      </lay-tooltip>
    </lay-col>
  </lay-row>
  <lay-row>
    <scan class="allocContent">
      <lay-icon type="layui-icon-rmb"/>
      总金额：￥{{ (dataSource.totalCollected - dataSource.totalAllocated).toFixed(2) }}
    </scan>
    <template #content>
      <div class="hoverTable">
        <lay-table
            ref="collectedTableRef"
            :columns="collectedColumns"
            :data-source="dataSource.collected"
            :default-toolbar="false"
            :even="true">
        </lay-table>
      </div>
    </template>
  </lay-row>
</template>
<script lang="ts">
export default {
  name: "TextDashboard",
};
</script>
<script setup lang="ts">
/*INIT*/
import {onMounted, reactive, ref} from "vue";
import {findAllocAmount} from "@/api/account/AccountCostUserAlloc";
import {RpFindAllocAmountDto} from "@/types/account/AccountCostUserAlloc";
import {R} from "@/types/Result";
import {TableColumn} from "@layui/layui-vue/types/component/table/typing";
import {PageQuery} from "@/types/Common";
import {flip, offset, shift, size} from "@floating-ui/dom";

const props = defineProps({
  title: {
    required: false,
    default: '面板信息'
  },
})

onMounted(() => {
  loadDataSource();
})
/*INIT*/


/*VAR*/
const dataSource = ref<RpFindAllocAmountDto>({});
const collectedTableRef = ref()
const allocatedTableRef = ref()
const tooltipMiddlewares = [
  offset(8),
  flip({padding: 8}),
  shift({padding: 8}),
  size({
    padding: 8,
    apply({availableWidth, availableHeight, elements}) {
      Object.assign(elements.floating.style, {
        maxWidth: `${Math.max(240, availableWidth)}px`,
        maxHeight: `${Math.max(180, availableHeight)}px`,
        overflow: 'hidden',
      });
    },
  }),
]
const tooltipPopperStyle = {
  maxWidth: 'calc(100vw - 24px)',
  maxHeight: 'calc(100vh - 300px)',
  overflow: 'hidden',
}
const collectedColumns = ref<TableColumn[]>([
  {title: '昵称', width: '130px', key: 'nickName'},
  {title: '描述', width: '130px', key: 'costDesc'},
  {title: '金额', width: '130px', key: 'amount'},
  {title: '时间', width: '200px', key: 'paymentTime'},
  {title: '备注', width: '200px', key: 'remark'},
])
const allocatedColumns = ref<TableColumn[]>([
  {title: '昵称', width: '130px', key: 'payeeNickName'},
  {title: '描述', width: '130px', key: 'costDesc'},
  {title: '金额', width: '130px', key: 'amount'},
  {title: '时间', width: '200px', key: 'paymentTime'},
  {title: '备注', width: '200px', key: 'remark'},
])
/*VAR*/

/*FUNCTION*/
function loadDataSource() {
  findAllocAmount().then((res: R<RpFindAllocAmountDto>) => {
    if (res.code === 200) {
      dataSource.value = res.data;
    }
  })
}

/*FUNCTION*/
</script>

<style scoped>
.allocContent {
  width: 100%;
  text-align: center;
  font-size: 20px;
  font-family: 'Helvetica Neue';
}

.hoverTable {
  width: min(800px, calc(100vw - 32px));
  max-height: calc(100vh - 360px);
  overflow: auto;
}
</style>
