<template>
  <lay-container fluid="true" class="option-box">
    <lay-card>
      <lay-form style="margin-top: 10px">
        <lay-row>
          <lay-col :md="5">
            <lay-form-item label="用户账号" label-width="80">
              <lay-input
                  v-model="searchQuery.userAccount"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label="用户名" label-width="80">
              <lay-input
                  v-model="searchQuery.userName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="8">
            <lay-form-item label="登录时间" label-width="80">
              <lay-date-picker
                  size="sm"
                  v-model="searchQuery.rangeTime"
                  range
                  type="datetime"
                  :placeholder="['开始日期', '结束日期']"
              ></lay-date-picker
              >
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label-width="20">
              <lay-button
                  style="margin-left: 20px"
                  type="normal"
                  size="sm"
                  @click="toSearch"
              >
                查询
              </lay-button>
              <lay-button size="sm" @click="toReset"> 重置</lay-button>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <!-- table -->
    <div class="table-box">
      <lay-table
          class="table-style"
          :height="tableHeight"
          :resize="true"
          :autoColsWidth="true"
          :page="pageQuery"
          :total="pageQuery.total"
          :columns="columns"
          :loading="loading"
          :default-toolbar="true"
          :data-source="dataSource"
          v-model:selected-keys="selectedKeys"
          @change="change"
          @sortChange="sortChange"
      >
        <template #url="{ row }">
          <lay-tooltip
              :visible="false"
              trigger="hover"
              :content="row.url"
          >
            <div>{{ row.url }}</div>
          </lay-tooltip>
        </template>

        <template #result="{ row }">
          <div v-show="row.result === 'success'">
            <lay-tag color="#2dc570" variant="light">正常</lay-tag>
          </div>
          <div v-show="row.result === 'failed'">
            <lay-tag color="#F5319D" variant="light">失败</lay-tag>
          </div>
        </template>
        <template #operateTime="{ row }">
          <div>{{ row.operateTime }}</div>
        </template>
        <template v-slot:toolbar></template>
        <template v-slot:operator="{ row }">
          <lay-button
              size="xs"
              border="green"
              border-style="dashed"
              @click="showDetail(row)"
          >详情
          </lay-button
          >
        </template>
        <template #os="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.os">
            <div>{{ row.os }}</div>
          </lay-tooltip>
        </template>
        <template #remark="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
            <div class="oneRow">{{ row.remark }}</div>
          </lay-tooltip>
        </template>
      </lay-table>
    </div>

    <!-- layer -->

    <lay-layer v-model="visible11" title="详情" :area="['700px', '450px']">
      <div style="padding: 20px">
        <lay-row>
          <lay-col md="4" class="title">操作人</lay-col>
          <lay-col md="8" class="content">{{
              dataLayer.name + '(' + dataLayer.account + ')'
            }}
          </lay-col>
          <lay-col md="4" class="title">IP地址</lay-col>
          <lay-col md="8" class="content borderR">{{
              dataLayer.ipAddrees
            }}
          </lay-col>
        </lay-row>
        <lay-row>
          <lay-col md="4" class="title">操作模块</lay-col>
          <lay-col md="8" class="content">{{ dataLayer.optionModule }}</lay-col>
          <lay-col md="4" class="title">操作功能</lay-col>
          <lay-col md="8" class="content borderR">{{
              dataLayer.optionFunction
            }}
          </lay-col>
        </lay-row>
        <lay-row>
          <lay-col md="4" class="title">操作时间</lay-col>
          <lay-col md="8" class="content">{{ dataLayer.joinTime }}</lay-col>
          <lay-col md="4" class="title">请求耗时</lay-col>
          <lay-col md="8" class="content borderR"
          >{{ dataLayer.time }}s
          </lay-col
          >
        </lay-row>
        <lay-row>
          <lay-col md="4" class="title">请求方式</lay-col>
          <lay-col md="8" class="content">{{
              dataLayer.requestMethod
            }}
          </lay-col>
          <lay-col md="4" class="title">请求状态</lay-col>
          <lay-col md="8" class="content borderR">
            <div v-show="dataLayer.status == '正常'">
              <lay-tag color="#2dc570" variant="light">正常</lay-tag>
            </div>
            <div v-show="dataLayer.status == '失败'">
              <lay-tag color="#F5319D" variant="light">失败</lay-tag>
            </div>
          </lay-col>
        </lay-row>
        <lay-row>
          <lay-col md="4" class="title">请求地址</lay-col>
          <lay-col md="20" class="content borderR">{{
              dataLayer.requestPath
            }}
          </lay-col>
        </lay-row>
        <lay-row>
          <lay-col md="4" class="title">调用方法</lay-col>
          <lay-col md="20" class="content borderR"
          >com.eleadmin.common.system.controller.LoginRecordController.page
          </lay-col
          >
        </lay-row>
        <lay-row>
          <lay-col md="4" class="title">请求参数</lay-col>
          <lay-col md="20" class="content borderR"
          >{"nickname":"","limit":"10","page":"1","username":""}
          </lay-col
          >
        </lay-row>
        <lay-row>
          <lay-col md="4" class="title borderB">返回结果</lay-col>
          <lay-col md="20" class="content borderR borderB"
          >{"code":0,"message":"操作成功","data":{"list":[{"id":89548,"username":"admin","os":"Windows","device":"Windows
            10 or Windows Server
            2016","browser":"Chrome","ip":"113.128.81.221","loginType":3,"comments":null,"tenantId":4,"createTime":1689857372000,"updateTime":1689857372000,"userId":40,"nickname":"管理员"},{"id":89547,"username":"admin","os":"Win
          </lay-col
          >
        </lay-row>
      </div>
    </lay-layer>
  </lay-container>
</template>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {findPageOptionLog} from "../../../api/system/SensitiveLog";
import {SysSensitiveLogVo} from "../../../types/system/SensitiveLog";

onMounted(async () => {
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

const tableHeight = '530px'
const operateTimeRange = ref<Date[]>([]);
const loading = ref(false)
const selectedKeys = ref([])
const pageQuery = reactive<PageQuery>({
      current: 1,
      limit: 10,
      total: 0
    }
)
const columns = ref([
  {title: '用户名', key: 'operatorName', sort: 'desc', width: '100px'},
  {title: '部门名称', key: 'deptName', sort: 'desc', width: '120px'},
  {title: 'IP地址', key: 'ipAddress', hide: true},
  {title: '请求地址', key: 'url', customSlot: 'url', width: '360px'},
  {
    title: '操作结果',
    width: '80px',
    key: 'result',
    customSlot: 'result'
  },
  {title: '所属模块', key: 'module', width: '200px'},
  {title: '子模块', key: 'subModule', width: '200px'},
  {title: '操作类型', key: 'type', width: '200px'},
  {
    title: '操作系统',
    width: '300px',
    key: 'os',
    sort: 'desc',
    customSlot: 'os'
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
const dataSource = ref([])
const loadDataSource = async () => {
  let operateTime = operateTimeRange.value;
  if (operateTime && operateTime?.length > 0) {
    searchQuery.value.operateTimeFrom = operateTime[0]
    searchQuery.value.operateTimeTo = operateTime[1]
  }
  const {rows, total} = await findPageOptionLog(searchQuery.value, pageQuery)
  dataSource.value = rows;
  pageQuery.total = total;
}
const dataLayer = ref({
  id: '0',
  optionModule: '',
  requestMethod: '',
  requestPath: '',
  optionFunction: '',
  account: '',
  status: '',
  name: '',
  time: '',
  joinTime: '',
  ipAddrees: ''
})
const visible11 = ref(false)

function showDetail(row: any) {
  visible11.value = true
  dataLayer.value = row
}
</script>

<style scoped>
.option-box {
  width: calc(100vw - 220px);
  height: calc(100vh - 110px);
  margin-top: 10px;
  box-sizing: border-box;
  overflow: hidden;
}

.top-search {
  margin-top: 10px;
  padding: 10px;
  height: 40px;
  border-radius: 4px;
  background-color: #fff;
}

.table-box {
  margin-top: 10px;
  padding: 10px;
  height: 700px;
  width: 100%;
  border-radius: 4px;
  box-sizing: border-box;
  background-color: #fff;
}

.search-input {
  display: inline-block;
  width: 98%;
  margin-right: 10px;
}

.table-style {
  margin-top: 10px;
}

.isChecked {
  display: inline-block;
  background-color: #e8f1ff;
  color: red;
}

.oneRow {
  width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.title {
  height: 40px;
  line-height: 40px;
  padding: 0 10px;
  display: inline-block;
  background: #f7f7f7;
  border-top: 1px solid #e8e8e8;
  border-left: 1px solid #e8e8e8;
}

.content {
  height: 40px;
  line-height: 40px;
  padding: 0 3px 0 10px;
  border-top: 1px solid #e8e8e8;
  border-left: 1px solid #e8e8e8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.borderR {
  border-right: 1px solid #e8e8e8;
}

.borderB {
  border-bottom: 1px solid #e8e8e8;
}
</style>
