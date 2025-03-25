<template>
  <lay-container :fluid="true">
    <lay-card>
      <lay-form label-position="top">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="用户名">
              <lay-input
                  style="width: 100%"
                  v-model="searchQuery.operatorName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="部门名称">
              <lay-input
                  style="width: 100%"
                  v-model="searchQuery.deptName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="12">
            <lay-form-item label="登录时间">
              <lay-date-picker
                  style="width: 100%"
                  size="sm"
                  v-model="operateTimeRange"
                  allow-clear
                  type="datetime"
                  range
                  simple
                  :shortcuts="defaultShortcuts" :inputFormat="'YYYY-MM-DD HH:mm:ss'"
                  :placeholder="['开始日期', '结束日期']"
              ></lay-date-picker
              >
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="所属模块">
              <lay-input
                  style="width: 100%;"
                  v-model="searchQuery.module"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="子模块">
              <lay-input
                  style="width: 100%"
                  v-model="searchQuery.subModule"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="操作类型">
              <lay-input
                  style="width: 100%"
                  v-model="searchQuery.type"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="操作结果">
              <lay-input
                  style="width: 100%"
                  v-model="searchQuery.result"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
              ></lay-input>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <!-- table -->
    <div>
      <lay-table
          :resize="true"
          :autoColsWidth="true"
          :page="pageQuery"
          :columns="columns"
          :loading="loading"
          :default-toolbar="true"
          :data-source="dataSource"
          v-model:selected-keys="selectedKeys"
          @change="change"
          @sortChange="sortChange"
      >
        <template #url="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.url">
            <div>{{ row.url }}</div>
          </lay-tooltip>
        </template>
        <template #os="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.os">
            <div>{{ row.os }}</div>
          </lay-tooltip>
        </template>

        <template #result="{ row }">
          <div v-show="row.result === 'success'">
            <lay-tag color="#2dc570" variant="light">成功</lay-tag>
          </div>
          <div v-show="row.result === 'failed'">
            <lay-tag color="#F5319D" variant="light">失败</lay-tag>
          </div>

        </template>
        <template #remark="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
            <div class="oneRow">{{ row.remark }}</div>
          </lay-tooltip>
        </template>
        <template v-slot:toolbar>
          <lay-button type="normal" size="sm" @click="toSearch">查询</lay-button>
          <lay-button size="sm" @click="toReset">重置</lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-popconfirm
              content="确定要删除此条登录记录吗?"
              @confirm="confirm(row)"
              @cancel="cancel"
          >
            <lay-button size="xs" border="red" border-style="dashed"
            >删除
            </lay-button
            >
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>
  </lay-container>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "Login",
};
</script>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {SysSensitiveEntity, SysSensitiveLogVo} from "@/types/system/SensitiveLog";
import {Constants, loadSysDictValue} from "@/util/UDict";
import {findPageLoginLog} from "@/api/system/SensitiveLog";
import {PageQuery} from "@/types/Common";
import {SysDictValueEntity} from "@/types/system/Dict";
import {defaultShortcuts} from "@/util/UDate";

onMounted(async () => {
  sysOperateResultList.value = await loadSysDictValue(Constants.SYS_OPERATE_RESULT);
  await loadDataSource();
})

const searchQuery = ref<SysSensitiveLogVo>({})

function toReset() {
  searchQuery.value = {}
}

function toSearch() {
  pageQuery.current = 1
  change()
}

const tableHeight = '550px'
const loading = ref(false)
const selectedKeys = ref()
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10,
  total: 0
})
const operateTimeRange = ref<Date[]>();
const sysOperateResultList = ref<Array<SysDictValueEntity>>([]);
const dataSource = ref<Array<SysSensitiveEntity>>()
const columns = ref([
  {title: '用户名', key: 'operatorName', sort: 'desc', width: '100px'},
  {title: '部门名称', key: 'deptName', sort: 'desc', width: '120px'},
  {title: 'IP地址', key: 'ipAddress', hide: true},
  {title: '请求地址', key: 'url', customSlot: 'url'},
  {title: '所属模块', key: 'module', width: '100px'},
  {title: '子模块', key: 'subModule', width: '100px'},
  {title: '操作类型', key: 'type', width: '100px'},
  {
    title: '操作系统',
    width: '300px',
    key: 'os',
    sort: 'desc',
    customSlot: 'os'
  },
  // {title: '操作系统', key: 'os'},
  {
    title: '操作结果',
    width: '80px',
    key: 'result',
    customSlot: 'result'
  },
  {title: '浏览器', key: 'browser', width: '100px'},
  {title: '操作时间', key: 'operateTime', width: '160px'},
  {title: '地点', key: 'location', width: '100px'},
  {title: '备注', key: 'remark', customSlot: 'remark', width: '200px'},
])
const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 1000)
}
const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}
const changeStatus = (isChecked: boolean, row: any) => {
  dataSource.value?.forEach((item: any) => {
    if (item.id === row.id) {
      layer.msg('Success', {icon: 1}, () => {
        item.ipAddrees = isChecked
      })
    }
  })
}
const remove = () => {
  layer.msg(selectedKeys.value, {area: '50%'})
}
const loadDataSource = async () => {
  let operateTime = operateTimeRange.value;
  if (operateTime && operateTime?.length > 0) {
    searchQuery.value.operateTimeFrom = operateTime[0]
    searchQuery.value.operateTimeTo = operateTime[1]
  }
  const {rows, total} = await findPageLoginLog(searchQuery.value, pageQuery)
  dataSource.value = rows;
  pageQuery.total = total;
}

function confirm(row: any) {
  layer.msg('您已成功删除')
}

function cancel() {
  layer.msg('您已取消操作')
}
</script>