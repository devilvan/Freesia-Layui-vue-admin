<template>
  <lay-container :fluid="true">
    <lay-card>
      <lay-form ref="queryFormRef" :model="searchQuery"
                label-position="top" @keydown.enter.prevent="toSearch">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="预算描述" prop="budgetDesc">
              <lay-input
                  v-model="searchQuery.budgetDesc"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="预算日期类型" prop="budgetType">
              <lay-select
                  size="sm"
                  style="width: 100%"
                  v-model="searchQuery.budgetType"
                  :options="accountReportDurationTypeSelectList"
                  :items="accountReportDurationTypeSelectList"
                  :allow-clear="true"
              ></lay-select>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="时间范围从" prop="durationFrom">
              <lay-date-picker style="width: 100%" simple type="date" v-model="searchQuery.durationFrom"
                               allow-clear></lay-date-picker>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="时间范围到" prop="durationTo">
              <lay-date-picker style="width: 100%" simple type="date" v-model="searchQuery.durationTo"
                               allow-clear></lay-date-picker>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <!-- table -->
    <lay-table
        v-model:selected-keys="selectedKeys"
        :columns="columns"
        :data-source="dataSource"
        :default-toolbar="defaultToolbarFlag"
        :loading="loading"
        :page="pageQuery"
        :even="evenFlag"
        @change="change"
        @sortChange="sortChange">
      <template #budgetType="{ row }">
        <dict-scan :options="accountReportDurationTypeSelect" :value="row.budgetType"/>
      </template>
      <template v-slot:toolbar>
        <lay-button size="sm" type="normal" @click="toSearch">查询</lay-button>
        <lay-button size="sm" @click="queryFormReset"> 重置
        </lay-button>
<!--          <lay-button size="sm" type="primary" @click="showSaveModal(Operate.ADD, null)">-->
<!--            <lay-icon class="layui-icon-addition"></lay-icon>-->
<!--            新增-->
<!--          </lay-button>-->
<!--          <lay-button size="sm" @click="toRemove">-->
<!--            <lay-icon class="layui-icon-delete"></lay-icon>-->
<!--            删除-->
<!--          </lay-button>-->
      </template>
<!--        <template v-slot:operator="{ row }">-->
<!--          <lay-button-->
<!--              border="green"-->
<!--              border-style="dashed"-->
<!--              size="xs"-->
<!--              @click="showSaveModal(Operate.EDIT, row)">编辑-->
<!--          </lay-button>-->
<!--          <lay-button-->
<!--              border="orange"-->
<!--              border-style="dashed"-->
<!--              size="xs"-->
<!--              @click="showSaveModal(Operate.COPY, row)">复制-->
<!--          </lay-button>-->
<!--          <lay-popconfirm-->
<!--              content="确定要删除吗?"-->
<!--              @cancel="cancel"-->
<!--              @confirm="confirm(row)">-->
<!--            <lay-button border="red"-->
<!--                        border-style="dashed"-->
<!--                        size="xs">删除-->
<!--            </lay-button>-->
<!--          </lay-popconfirm>-->
<!--        </template>-->
    </lay-table>

    <lay-layer v-model="showModalFlag" :area="['1200px']" :title="saveModalTitle">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" @keydown.esc.prevent="toCancel">
        <lay-form ref="saveFormRef" :model="saveAccountReportVo" :rules="saveFromRules" label-position="top">
          <lay-row :space="20">
            <lay-col :md="6">
              <lay-form-item label="标题" prop="title">
                <lay-input
                    v-model="saveAccountReportVo.title"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="预算类型" prop="budgetType">
                <lay-input
                    v-model="saveAccountReportVo.budgetType"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="支出金额" prop="outlay">
                <lay-input
                    v-model="saveAccountReportVo.outlay"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="收入金额" prop="incomeAmount">
                <lay-input
                    v-model="saveAccountReportVo.incomeAmount"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="报表时间" prop="billingTime">
                <lay-input
                    v-model="saveAccountReportVo.billingTime"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="报表时间从" prop="billingTimeFrom">
                <lay-input
                    v-model="saveAccountReportVo.billingTimeFrom"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="报表时间到" prop="billingTimeTo">
                <lay-input
                    v-model="saveAccountReportVo.billingTimeTo"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="是否重新计算" prop="recalculateFlag">
                <lay-input
                    v-model="saveAccountReportVo.recalculateFlag"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
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
  </lay-container>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "AccountReport",
};
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "@/types/Common";
import {TableResult} from "@/types/Result";
import {deleteAccountReport, findPageAccountReport} from "@/api/account/AccountReport";
import {AccountReportEntity, AccountReportVo} from "@/types/account/AccountReport";
import {Operate} from "@/types/Constants";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {findAccountReport, saveUpdate} from "@/api/account/AccountReport";

/* INIT*/
onMounted(async () => {
  accountReportDurationTypeSelect.value = await loadSysDictValue(Constants.ACCOUNT_BUDGET_DURATION_TYPE)
  accountReportDurationTypeSelectList.value = await sysDictValueSelect(accountReportDurationTypeSelect.value)
  loadDataSource()
})
/* INIT*/

/* VAR*/
const searchQuery = ref<AccountReportVo>({})
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const dataSource = ref<Array<AccountReportEntity>>()
const selectedKeys = ref<Array<string>>([])
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  // {title: '标题', width: '130px', key: 'title'},
  {title: '预算类型', width: '130px', key: 'budgetType', customSlot: 'budgetType'},
  {title: '支出金额', width: '130px', key: 'outlay'},
  {title: '收入金额', width: '130px', key: 'incomeAmount'},
  {title: '报表时间', width: '130px', key: 'billingTime'},
  {title: '报表时间从', width: '130px', key: 'billingTimeFrom'},
  {title: '报表时间到', width: '130px', key: 'billingTimeTo'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const loading = ref(false)
const defaultToolbarFlag = ref(true)
const evenFlag = ref(true)
const showModalFlag = ref(false)
const saveModalTitle = ref('');
const saveFormRef = ref(null)
const saveAccountReportVo = ref<AccountReportVo>(<AccountReportVo>{})
const queryFormRef = ref(null)
const accountReportDurationTypeSelect = ref<Array<SysDictValueEntity>>(<Array<SysDictValueEntity>>[]);
const accountReportDurationTypeSelectList = ref();
const saveFromRules = ref({
  // outlay: {
  //  validator(rule: { field: any; }, value: any, callback: (arg0: Error) => void) {
  //   if (value <= 0) {
  //    callback(new Error("金额不能为0"));
  //   } else {
  //    return true;
  //   }
  //  }
  // },
})
/* VAR*/

/* FUNCTION*/
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
 * 触发列表字段排序
 */
const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}

/**
 * 查询按钮
 */
function toSearch() {
  pageQuery.current = 1
  dataSource.value = []
  change()
}

/**
 * 查询条件重置
 */
function queryFormReset() {
  searchQuery.value = {}
}

/**
 * 显示新增/修改/复制弹窗
 */
const showSaveModal = (text: any, row: any) => {
  saveModalTitle.value = Operate.ADD === text ? "新增" : Operate.EDIT === text ? "编辑" : Operate.COPY === text ? "复制" : "";
  if (row != null) {
    saveAccountReportVo.value = {...row}
  }
  if (Operate.EDIT === text) {
    findAccountReport({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        saveAccountReportVo.value = res.data;
      }
    })
  } else if (Operate.ADD === text) {
  } else if (Operate.COPY === text) {
  }
  showModalFlag.value = !showModalFlag.value
}

/**
 * 删除按钮
 */
function toRemove() {
  if (selectedKeys.value.length == 0) {
    layer.msg('您未选择数据，请先选择要删除的数据', {icon: 3, time: 2000})
    return
  }
  layer.confirm('您将删除所有选中的数据？', {
    title: '提示',
    btn: [
      {
        text: '确定',
        callback: (id: any) => {
          deleteAccountReport(selectedKeys.value).then((res: any) => {
            if (res.code === 200) {
              layer.msg('删除成功')
            }
            loadDataSource();
          }).catch(e => {
            layer.confirm(e.msg, {icon: 2})
          })
          layer.close(id)
        }
      },
      {
        text: '取消',
        callback: (id: any) => {
          layer.close(id)
        }
      }
    ]
  })
}

/**
 * 保存弹出框-保存
 */
function toSubmit(clickFlag: boolean) {
  saveFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveUpdate(saveAccountReportVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          saveAccountReportVo.value = {};
          if (clickFlag) {
            showModalFlag.value = false
          } else {
            // 如果是修改+回车，则关闭窗口
            if (saveAccountReportVo.id && saveAccountReportVo.id != 0) {
              showModalFlag.value = false
            }
          }
          showModalFlag.value = false
        }
      })
    }
  })
}

/**
 * 保存弹出框-重置
 */
function toReset() {
  saveFormRef.value.reset();
}

/**
 * 保存弹出框-取消
 */
function toCancel() {
  showModalFlag.value = false
}

function confirm(row: any) {
  if (row) {
    deleteAccountReport([row.id]).then((res: any) => {
      if (res.code === 200) {
        layer.msg('删除成功')
      }
      loadDataSource();
    }).catch(e => {
      layer.confirm(e.msg, {icon: 2})
    })
  }
}

function cancel() {
  layer.msg('您已取消操作')
}

/* FUNCTION*/
</script>