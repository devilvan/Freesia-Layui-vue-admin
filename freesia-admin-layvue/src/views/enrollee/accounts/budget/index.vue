<template>
  <lay-container class="role-box" fluid="true">
    <lay-card>
      <lay-form ref="queryFormRef" :model="searchQuery" label-position="left">
        <lay-row :space="10">
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
                  :options="accountBudgetDurationTypeSelectList"
                  :items="accountBudgetDurationTypeSelectList"
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
          :height="'600px'"
          :even="evenFlag"
          @change="change"
          @sortChange="sortChange">
        <template #remark="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
            <div class="oneRow">{{ row.remark }}</div>
          </lay-tooltip>
        </template>
        <template v-slot:toolbar>
          <lay-button
              size="sm"
              style="margin-left: 20px"
              type="normal"
              @click="toSearch"
          >
            查询
          </lay-button>
          <lay-button size="sm" @click="queryFormReset"
          > 重置
          </lay-button>
          <lay-button
              size="sm"
              type="primary"
              @click="showSaveModal(Operate.ADD, null)"
          >
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

    <lay-layer v-model="showModalFlag" :area="['1200px']" :title="title">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" @keydown.esc.prevent="toCancel">
        <lay-form ref="saveFormRef" :model="saveAccountBudgetVo" label-position="top">
          <lay-row :space="10">
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
              <lay-form-item label="预算金额" prop="budgetDesc" required>
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
                    size="sm"
                    style="width: 100%"
                    v-model="saveAccountBudgetVo.budgetType"
                    :options="accountBudgetDurationTypeSelectList"
                    :items="accountBudgetDurationTypeSelectList"
                    :allow-clear="true"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="时间范围从" prop="durationFrom" required>
                <lay-date-picker style="width: 100%" simple type="date" v-model="saveAccountBudgetVo.durationFrom"
                                 allow-clear></lay-date-picker>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="时间范围到" prop="durationTo" required>
                <lay-date-picker style="width: 100%" simple type="date" v-model="saveAccountBudgetVo.durationTo"
                                 allow-clear></lay-date-picker>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="备注" prop="remark">
                <lay-textarea
                    v-model="saveAccountBudgetVo.remark"
                    allow-clear
                ></lay-textarea>
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
  name: "Budget",
};
</script>
<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "@/types/Common";
import {TableResult} from "@/types/Result";
import {deleteAccountBudget, findPageAccountBudget} from "@/api/account/AccountBudget";
import {AccountBudgetEntity, AccountBudgetVo} from "@/types/account/AccountBudget";
import {Operate} from "@/types/Constants";
import {deleteAccountCost, saveUpdate} from "@/api/account/Account";
import {defaultShortcuts} from "@/util/UDate";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";

/* INIT*/
onMounted(async () => {
  accountBudgetDurationTypeSelect.value = await loadSysDictValue(Constants.ACCOUNT_BUDGET_DURATION_TYPE)
  accountBudgetDurationTypeSelectList.value = await sysDictValueSelect(accountBudgetDurationTypeSelect.value)
  loadDataSource()
})
/* INIT*/

/* VAR*/
const searchQuery = ref<AccountBudgetVo>({})
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const dataSource = ref<Array<AccountBudgetEntity>>()
const selectedKeys = ref<Array<string>>([])
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '预算描述', width: '130px', key: 'budgetDesc', fixed: 'left'},
  {title: '预算金额', width: '130px', key: 'outlay', sort: 'desc'},
  {title: '时间范围从', width: '130px', key: 'durationFrom'},
  {title: '时间范围到', width: '130px', key: 'durationTo'},
  {title: '预算类型', width: '130px', key: 'budgetType', customSlot: 'budgetType'},
  {title: '备注', width: '150px', key: 'remark', customSlot: 'remark'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const loading = ref(true)
const defaultToolbarFlag = ref(true)
const evenFlag = ref(true)
const saveVo = ref<AccountBudgetVo>({})
const showModalFlag = ref(false)
const saveModalTitle = ref('');
const saveFormRef = ref(null)
const saveAccountBudgetVo = ref<AccountBudgetVo>({})
const saveFormSchema = ref({
  budgetDesc: {
    label: '预算描述',
    type: 'input',
    props: {
      type: 'text',
    },
    colProps: {
      md: 6
    }
  },
  outlay: {
    label: '预算金额',
    type: 'input',
    props: {
      type: 'number',
    },
    colProps: {
      md: 6
    }
  },
  durationFrom: {
    label: '时间范围从',
    type: 'date',
    props: {
      type: 'datetime',
    },
    colProps: {
      md: 6
    }
  },
  durationTo: {
    label: '时间范围到',
    type: 'date',
    props: {
      type: 'datetime',
    },
    colProps: {
      md: 6
    }
  },
  budgetType: {
    label: '预算类型',
    type: 'select',
    props: {
      options: [
        {label: '唱', value: '1'},
        {label: '跳', value: '2'},
        {label: 'rap', value: '3'},
        {label: '篮球', value: '4'}
      ],
    },
    colProps: {
      md: 6
    }
  },
  remark: {
    label: '备注',
    type: 'textarea',
    props: {},
    colProps: {
      md: 12
    },
  },
})
const accountBudgetSaveVo = ref<AccountBudgetVo>({})
const queryFormRef = ref(null)
const queryAccountBudgetVo = ref<AccountBudgetVo>({})
const accountBudgetDurationTypeSelect = ref<Array<SysDictValueEntity>>();
const accountBudgetDurationTypeSelectList = ref();
/* VAR*/

/* FUNCTION*/
/**
 * 初始化表格
 */
const loadDataSource = () => {
  findPageAccountBudget(searchQuery.value, pageQuery).then((res: TableResult<AccountBudgetEntity>) => {
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
  }, 1000)
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
  saveModalTitle.value = Operate.ADD === text ? "新增" : Operate.EDIT === text ? "编辑" : "";
  if (Operate.EDIT === text) {
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
          deleteAccountBudget(selectedKeys.value).then((res: any) => {
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

/* FUNCTION*/
</script>