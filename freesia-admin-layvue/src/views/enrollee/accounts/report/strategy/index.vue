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
                  :options="accountReportStrategyDurationTypeSelectList"
                  :items="accountReportStrategyDurationTypeSelectList"
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
    <div>
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
        <template #weekBegin="{ row }">
          <dict-scan :options="weekDaySelect" :value="row.weekBegin"/>
        </template>
        <template v-slot:toolbar>
          <lay-button size="sm" type="normal" @click="toSearch">查询</lay-button>
          <lay-button size="sm" @click="queryFormReset"> 重置
          </lay-button>
          <lay-button size="sm" type="primary" @click="showSaveModal(Operate.ADD, null)">
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button
              border="green"
              border-style="dashed"
              size="xs"
              @click="showSaveModal(Operate.EDIT, row)">编辑
          </lay-button>
          <lay-button
              border="orange"
              border-style="dashed"
              size="xs"
              @click="showSaveModal(Operate.COPY, row)">复制
          </lay-button>
          <lay-popconfirm
              content="确定要删除吗?"
              @cancel="cancel"
              @confirm="confirm(row)">
            <lay-button border="red"
                        border-style="dashed"
                        size="xs">删除
            </lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="showModalFlag" :area="['1200px']" :title="saveModalTitle">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" @keydown.esc.prevent="toCancel">
        <lay-form ref="saveFormRef" :model="saveAccountReportStrategyVo" :rules="saveFromRules" label-position="top">
          <lay-row :space="20">
            <lay-col :md="6">
              <lay-form-item label="预算类型" prop="budgetType" required>
                <lay-select
                    size="sm"
                    style="width: 100%"
                    v-model="saveAccountReportStrategyVo.budgetType"
                    :options="accountReportStrategyDurationTypeSelectList"
                    :items="accountReportStrategyDurationTypeSelectList"
                    :allow-clear="true"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="开始周" prop="weekBegin" required>
                <lay-select
                    size="sm"
                    style="width: 100%"
                    v-model="saveAccountReportStrategyVo.weekBegin"
                    :options="weekDaySelectList"
                    :items="weekDaySelectList"
                    :allow-clear="true"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="是否启用" prop="enabled" required>
                <lay-switch v-model="saveAccountReportStrategyVo.enabled"></lay-switch>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="是否重新计算" prop="recalculateFlag">
                <lay-switch v-model="saveAccountReportStrategyVo.recalculateFlag"></lay-switch>
              </lay-form-item>
            </lay-col>
          </lay-row>
          <lay-row :space="20">
            <lay-col :md="6">
              <lay-form-item label="备注" prop="remark">
                <lay-textarea v-model="saveAccountReportStrategyVo.remark" :allow-clear="true" show-count
                              :maxlength="1024"></lay-textarea>
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
  name: "AccountReportStrategy",
};
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "@/types/Common";
import {TableResult} from "@/types/Result";
import {deleteAccountReportStrategy, findPageAccountReportStrategy} from "@/api/account/AccountReportStrategy";
import {AccountReportStrategyEntity, AccountReportStrategyVo} from "@/types/account/AccountReportStrategy";
import {Operate} from "@/types/Constants";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {findAccountReportStrategy, saveUpdate} from "@/api/account/AccountReportStrategy";

/* INIT*/
onMounted(async () => {
  accountReportStrategyDurationTypeSelect.value = await loadSysDictValue(Constants.ACCOUNT_BUDGET_DURATION_TYPE)
  accountReportStrategyDurationTypeSelectList.value = await sysDictValueSelect(accountReportStrategyDurationTypeSelect.value)
  weekDaySelect.value = await loadSysDictValue(Constants.WEEK_DAY)
  weekDaySelectList.value = await sysDictValueSelect(weekDaySelect.value)
  loadDataSource()
})
/* INIT*/

/* VAR*/
const searchQuery = ref<AccountReportStrategyVo>({})
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const dataSource = ref<Array<AccountReportStrategyEntity>>()
const selectedKeys = ref<Array<string>>([])
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '预算类型', width: '130px', key: 'budgetType'},
  {title: '是否启用', width: '130px', key: 'enabled'},
  {title: '开始周', width: '130px', key: 'weekBegin', customSlot: 'weekBegin'},
  {title: '最新生成时间', width: '130px', key: 'generateTime'},
  {title: '下次生成时间', width: '130px', key: 'nextGenerateTime'},
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
const saveAccountReportStrategyVo = ref<AccountReportStrategyVo>(<AccountReportStrategyVo>{})
const queryFormRef = ref(null)
const accountReportStrategyDurationTypeSelect = ref<Array<SysDictValueEntity>>(<Array<SysDictValueEntity>>[]);
const accountReportStrategyDurationTypeSelectList = ref();
const weekDaySelect = ref<Array<SysDictValueEntity>>(<Array<SysDictValueEntity>>[]);
const weekDaySelectList = ref();
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
  findPageAccountReportStrategy(searchQuery.value, pageQuery).then((res: TableResult<AccountReportStrategyEntity>) => {
    if (res.code == 200) {
      pageQuery.total = res.total;
      dataSource.value = res.rows
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
  layer.msg(`字段${key} - 排序${sort}}, 你可以利用 sort-change 实现服务端排序`)
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
    saveAccountReportStrategyVo.value = {...row}
  }
  if (Operate.EDIT === text) {
    findAccountReportStrategy({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        saveAccountReportStrategyVo.value = res.data;
      }
    })
  } else if (Operate.ADD === text) {
    // 开始周默认周一
    saveAccountReportStrategyVo.value.weekBegin = 1;
    saveAccountReportStrategyVo.value.enabled = true;
    saveAccountReportStrategyVo.value.recalculateFlag = true;
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
          deleteAccountReportStrategy(selectedKeys.value).then((res: any) => {
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
      saveUpdate(saveAccountReportStrategyVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          saveAccountReportStrategyVo.value = {};
          if (clickFlag) {
            showModalFlag.value = false
          } else {
            // 如果是修改+回车，则关闭窗口
            if (saveAccountReportStrategyVo.id && saveAccountReportStrategyVo.id != 0) {
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
    deleteAccountReportStrategy([row.id]).then((res: any) => {
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