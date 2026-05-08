<template>
  <lay-card shadow="hover">
    <template #title>{{ props.title }}</template>
    <lay-row space="20">
      <lay-col v-for="(item, index) in accountBudgetEntityList" :key="index" :md="8">
        <lay-card shadow="hover">
          <div style="padding-bottom: 20px">
            <div style="font-size: 12pt;text-align: center;height: 30px">
              {{ item.name }}
            </div>
            <div v-if="item.durationFrom" style="font-size: 10pt;text-align: center;height: 30px">
              {{ item.durationFrom }} - {{ item.durationTo }}
            </div>
            <div style="font-size: 20pt">
              <div style="display: flex;justify-content: center" :style="
                    item.value >= 0 && item.value <= 50 ?
                    'color: #36b368' :
                    item.value > 50 && item.value <= 80 ?
                    'color: #FFB800' :
                    item.value > 80 && item.value <= 100 ?
                    'color: #FF9B2D' :
                    item.value > 100 ?
                    'color: #FF5722' : 'color: #393D49'">
                <div>
                  <lay-count-up :end-val="item.outlay" prefix="¥" decimalPlaces="2"></lay-count-up>
                  /
                  <lay-count-up :end-val="item.budget"></lay-count-up>
                </div>
                <div style="margin-left: 30px">
                  {{ item.value }}%
                </div>
              </div>
            </div>
          </div>
          <template #footer>
            <div class="button-list">
              <div @click="changeBudgetUpdateModal(item)">
                <lay-icon type="layui-icon-edit"></lay-icon>
                设置预算
              </div>
              <div @click="doFindAccountReport(item)">
                <lay-icon type="layui-icon-chart-screen"></lay-icon>
                历史数据
              </div>
            </div>
          </template>
        </lay-card>
      </lay-col>
    </lay-row>
  </lay-card>

  <lay-layer v-model="showModalFlag" :area="['1200px']" :title="'设置预算'">
    <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" v-esc-close="toCancel">
      <lay-form ref="saveFormRef" :model="saveAccountBudgetVo" :rules="saveFromRules" label-position="top">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="预算描述" prop="budgetDesc" required>
              <lay-input
                  v-model="saveAccountBudgetVo.budgetDesc"
                  :allow-clear="true"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="预算金额" prop="outlay" required>
              <lay-input
                  v-model="saveAccountBudgetVo.outlay"
                  :allow-clear="true"
                  type="number"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="预算日期类型" prop="budgetType" required>
              <lay-select
                  style="width: 100%"
                  size="sm"
                  v-model="saveAccountBudgetVo.budgetType"
                  :options="accountBudgetDurationTypeSelectList"
                  :items="accountBudgetDurationTypeSelectList"
                  :allow-clear="true"
                  @change="saveBudgetTypeChange"
              ></lay-select>
            </lay-form-item>
          </lay-col>
        </lay-row>
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="备注" prop="remark">
              <lay-textarea
                  v-model="saveAccountBudgetVo.remark"
                  allow-clear
              ></lay-textarea>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="时间范围从" style="width: 100%"
                           :style="saveAccountBudgetVo.budgetType !== 'CUSTOM' ? 'display: none' : ''"
                           prop="durationFrom" :required="saveAccountBudgetVo.budgetType === 'CUSTOM'"
                           :hidden="saveAccountBudgetVo.budgetType !== 'CUSTOM'">
              <lay-date-picker style="width: 100%" simple type="datetime" v-model="saveAccountBudgetVo.durationFrom"
                               allow-clear :inputFormat="'YYYY-MM-DD HH:mm:ss'"></lay-date-picker>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="时间范围到" style="width: 100%"
                           :style="saveAccountBudgetVo.budgetType !== 'CUSTOM' ? 'display: none' : ''"
                           prop="durationTo" :required="saveAccountBudgetVo.budgetType === 'CUSTOM'"
                           :hidden="saveAccountBudgetVo.budgetType !== 'CUSTOM'">
              <lay-date-picker style="width: 100%" simple type="datetime" v-model="saveAccountBudgetVo.durationTo"
                               allow-clear :inputFormat="'YYYY-MM-DD HH:mm:ss'"></lay-date-picker>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
      <div style="width: 100%; text-align: right">
        <lay-button size="sm" type="primary" @click="toSubmit(true)">保存</lay-button>
        <lay-button size="sm" type="primary" @click="toReset">重置</lay-button>
        <lay-button size="sm" @click="toCancel">取消</lay-button>
      </div>
    </div>
  </lay-layer>

  <lay-layer v-model="showReportModalFlag" :area="['1200px', '600px']" :title="'历史数据'">
    <lay-card>
      <lay-form ref="queryFormRef" :model="searchQuery"
                label-position="top" @keydown.enter.prevent="change()">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="报表时间" prop="billingTime">
              <lay-date-picker style="width: 100%"
                               v-model="searchQuery.billingTime"
                               size="sm"
                               simple allow-clear
                               @change="change()"></lay-date-picker>
            </lay-form-item>
          </lay-col>
          <lay-col :md="12">
            <lay-form-item label="报表时间范围" prop="billingTimeRange">
              <lay-date-picker style="width: 100%"
                               v-model="searchQuery.billingTimeRange"
                               allow-clear range simple
                               type="datetime"
                               :default-time="dateRangeDefaultTime"
                               :shortcuts="defaultShortcuts" @change="change()"></lay-date-picker>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <div style="padding: 20px" v-esc-close="toCancel">
      <lay-table
          v-model:selected-keys="selectedKeys"
          :columns="columns"
          :data-source="dataSource"
          :default-toolbar="defaultToolbar"
          :loading="loading"
          :page="pageQuery"
          :even="evenFlag"
          @change="change">
        <template #budgetType="{ row }">
          <dict-scan :options="accountBudgetDurationTypeSelect" :value="row.budgetType"/>
        </template>
        <template v-slot:toolbar>
          <lay-button size="sm" type="normal" @click="change()">查询</lay-button>
          <lay-button size="sm" @click="toResetReport()">重置</lay-button>
        </template>
      </lay-table>
    </div>
  </lay-layer>
</template>

<script lang="ts">
export default {
  name: "BudgetStatistic"
};
</script>
<script lang="ts" setup>

/*INIT*/
import {onMounted, reactive, ref, watch} from "vue";
import {AccountBudgetVo, EchartCapacityOptionEntity} from "@/types/account/AccountBudget";
import {SysDictValueEntity} from "@/types/system/Dict";
import {findAccountBudget, saveUpdate} from "@/api/account/AccountBudget";
import {layer} from "@layui/layui-vue";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {findAccountReport, findPageAccountReport} from "@/api/account/AccountReport";
import {R, TableResult} from "@/types/Result";
import {AccountReportEntity, AccountReportVo} from "@/types/account/AccountReport";
import {PageQuery} from "@/types/Common";
import {defaultShortcuts} from "@/util/UDate";
import {findSysColumnHeader} from "@/api/system/ColumnHeader";
import {buildItem, buildTableDefaultToolbar, convertToDefaultColumn} from "@/util/UColumn";
import {SysColumnHeaderEntity} from "@/types/system/ColumnHeader";
import {TableColumn, TableDefaultToolbar} from "@layui/layui-vue/types/component/table/typing";
import {SysColumnDetailEntity} from "@/types/system/ColumnDetail";

const props = defineProps({
  title: {
    required: false,
    default: '开支预算统计'
  },
  dataSource: {
    required: true,
    type: Array<EchartCapacityOptionEntity>
  }
})

watch(
    () => props.dataSource,
    (val) => {
      accountBudgetEntityList.value = val;
    },
);

onMounted(async () => {
  doBuildColumn()
  accountBudgetDurationTypeSelect.value = await loadSysDictValue(Constants.ACCOUNT_BUDGET_DURATION_TYPE)
  accountBudgetDurationTypeSelectList.value = await sysDictValueSelect(accountBudgetDurationTypeSelect.value)
})
/*INIT*/

/*VAR*/
const accountBudgetEntityList = ref<Array<EchartCapacityOptionEntity>>([]);
const showModalFlag = ref(false)
const showReportModalFlag = ref(false)
const saveAccountBudgetVo = ref<AccountBudgetVo>(<AccountBudgetVo>{})
const saveFormRef = ref(null)
const queryFormRef = ref(null)
const accountBudgetDurationTypeSelect = ref<Array<SysDictValueEntity>>();
const accountBudgetDurationTypeSelectList = ref();
const saveFromRules = ref({
  outlay: {
    validator(rule: { field: any; }, value: any, callback: (arg0: Error) => void) {
      if (value <= 0) {
        callback(new Error("金额不能为0"));
      } else {
        return true;
      }
    }
  },
})
const searchQuery = ref<AccountReportVo>({})
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10,
  limits: [10, 20, 50, 100, 200],
  hideOnSinglePage: false,
  layout: ['count', 'prev', 'page', 'next', 'limits', 'refresh', 'skip'],
})
const columns = ref<TableColumn[]>([])
const defaultColumns: TableColumn[] = [
  {title: '预算类型', width: '130px', key: 'budgetType', customSlot: 'budgetType'},
  {title: '开始时间', width: '130px', key: 'billingTimeFrom'},
  {title: '结束时间', width: '130px', key: 'billingTimeTo'},
  {title: '预算支出金额', width: '130px', key: 'budgetAmount', totalRow: true},
  {title: '支出金额', width: '130px', key: 'outlay', totalRow: true},
  {title: '攒钱', width: '130px', key: 'saveAmount', totalRow: true},
  {title: '收入金额', width: '130px', key: 'incomeAmount', totalRow: true},
]
const dataSource = ref<Array<AccountReportEntity>>()
const selectedKeys = ref<Array<string>>([])
const loading = ref(false)
const evenFlag = ref(true)
const dateRangeDefaultTime = ['00:00:00', '23:59:59'];
const defaultToolbar = ref<TableDefaultToolbar[]>([])
/*VAR*/

/*FUNCTION*/
/**
 * 刷新表格
 */
const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 200)
}

/**
 * 初始化表格
 */
const loadDataSource = () => {
  findPageAccountReport(searchQuery.value, pageQuery).then((res: TableResult<AccountReportEntity>) => {
    if (res.code == 200) {
      pageQuery.total = res.total;
      dataSource.value = res.rows || []
    } else {
      layer.msg(res.msg)
      return;
    }
  }).catch(e => {
    layer.msg(e.msg)
  });
}

function doBuildColumn() {
  findSysColumnHeader({
    name: 'BudgetHistory',
    defaultColumnVoList: convertToDefaultColumn(defaultColumns),
  }).then((res: R<SysColumnHeaderEntity>) => {
    let sysColumnHeaderEntity = res.data;
    if (res.code === 200 && sysColumnHeaderEntity) {
      let sysColumnDetailDtoList = sysColumnHeaderEntity.sysColumnDetailDtoList;
      let sysColumnHeader: SysColumnHeaderEntity = sysColumnHeaderEntity || {};
      if (sysColumnDetailDtoList && sysColumnDetailDtoList.length > 0) {
        let tempColumns: TableColumn[] = []
        sysColumnDetailDtoList.forEach((item: SysColumnDetailEntity) => {
          tempColumns.push(buildItem(item, sysColumnHeader))
        })
        columns.value = [
          ...tempColumns,
        ];
        if (sysColumnHeaderEntity.id != null) {
          defaultToolbar.value = buildTableDefaultToolbar(sysColumnHeaderEntity.id, columns);
        }
      }
    } else {
      columns.value = defaultColumns;
    }
  })
}

function changeBudgetUpdateModal(item: EchartCapacityOptionEntity) {
  findAccountBudget({id: item.id}).then((res: any) => {
    if (res.code === 200) {
      saveAccountBudgetVo.value = res.data
    }
  })
  showModalFlag.value = !showModalFlag.value
}

/**
 * 保存弹出框-保存
 */
function toSubmit(clickFlag: boolean) {
  saveFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveUpdate(saveAccountBudgetVo.value).then((res: any) => {
        if (res.code === 200) {
          layer.msg('保存成功！', {icon: 1, time: 1000})
          saveAccountBudgetVo.value = {};
          if (clickFlag) {
            showModalFlag.value = false
          } else {
            // 如果是修改+回车，则关闭窗口
            if (saveAccountBudgetVo.id && saveAccountBudgetVo.id != 0) {
              showModalFlag.value = false
            }
          }
          showModalFlag.value = false
          window.location.reload();
        }
      })
    }
  })
}

function doFindAccountReport(item: EchartCapacityOptionEntity) {
  searchQuery.value = {}
  dataSource.value = []
  pageQuery.current = 1
  searchQuery.value.budgetId = item.id
  change()
  showReportModalFlag.value = true;
}

/**
 * 保存弹出框-重置
 */
function toReset() {
  saveFormRef.value.reset();
}

function toResetReport() {
  queryFormRef.value.reset();
  searchQuery.value.billingTime = ''
  searchQuery.value.billingTimeRange = []
}

/**
 * 保存弹出框-取消
 */
function toCancel() {
  showModalFlag.value = false
  showReportModalFlag.value = false
}

function saveBudgetTypeChange(value: any) {
  if (!value || value !== 'CUSTOM') {
    // 如果不是自定义则时间范围置空
    saveAccountBudgetVo.value.durationFrom = null;
    saveAccountBudgetVo.value.durationTo = null;
  }
}

/*FUNCTION*/
</script>

<style scoped>
.button-list {
  display: flex;
  width: 100%;
}

.button-list > div {
  flex: 1;
  text-align: center;
  color: #909399;
}

.button-list div:hover {
  background: whitesmoke !important;
  cursor: pointer
}

.button-list div:hover {
  color: var(--global-primary-color) !important;
}
</style>