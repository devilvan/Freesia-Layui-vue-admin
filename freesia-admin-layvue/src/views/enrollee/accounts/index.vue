<template>
  <lay-container class="role-box" fluid="true">
    <lay-card>
      <lay-form style="margin-top: 10px" @keyup.enter.prevent="toSearch">
        <lay-row>
          <lay-col :md="6">
            <lay-form-item label="租户编码" label-width="80">
              <lay-input
                  v-model="searchQuery.code"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="租户名称" label-width="80">
              <lay-input
                  v-model="searchQuery.name"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label-width="20">
              <lay-button
                  size="sm"
                  style="margin-left: 20px"
                  type="normal"
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
    <div>
      <lay-table
          v-model:selected-keys="selectedKeys"
          :columns="columns"
          :data-source="dataSource"
          :default-toolbar="true"
          :loading="loading"
          :page="pageQuery"
          class="table-box table-style"
          @change="change"
          @sortChange="sortChange">
        <template #iconType="{ row }">
          <SvgIcon :name="row.icon" size="2em"></SvgIcon>
          {{ row.icon }}
        </template>
        <template #status="{ row }">
          <lay-switch
              :model-value="row.status"
          ></lay-switch>
        </template>
        <template #tenantType="{ row }">
          <dict-tag :options="sysTenantTypeList" :showValue="true" :value="row.type"/>
        </template>
        <template v-slot:toolbar>
          <lay-button
              v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_ADD]"
              size="sm"
              type="primary"
              @click="showExpenseModal(Operate.ADD, null)"
          >
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_EDIT]" size="sm" @click="toRemove">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button
              v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_EDIT]"
              border="green"
              border-style="dashed"
              size="xs"
              @click="showExpenseModal(Operate.EDIT, row)">编辑
          </lay-button>
          <lay-popconfirm
              content="确定要删除吗?"
              @cancel="cancel"
              @confirm="confirm(row)">
            <lay-button v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_DELETE]" border="red" border-style="dashed"
                        size="xs">删除
            </lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="addExpenseModalShowFlag" :area="['1200px']" :title="title">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit" @keydown.esc.prevent="toCancel">
        <lay-form ref="addExpenseFormRef" :model="accountCostVo" label-position="top">
          <lay-row space="20">
            <lay-col :md="6">
              <lay-form-item label="开销描述" prop="costDesc" required>
                <lay-input v-model="accountCostVo.costDesc" :allow-clear="true"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="开销金额" prop="outlay" required>
                <lay-input v-model="accountCostVo.outlay" type="number"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="图标" prop="icon" required>
                <lay-row>
                  <lay-col md="4">
                    <lay-avatar v-if="!accountCostVo.icon" @click="changeSelectTypeModal"></lay-avatar>
                    <SvgIcon v-else :name="accountCostVo.icon" size="3em" @click="changeSelectTypeModal"></SvgIcon>
                  </lay-col>
                  <lay-col md="20"
                           style="justify-content: center; align-items: center; font-size: 10pt; line-height: 40px">
                    图标：{{ accountCostVo.icon }}
                  </lay-col>
                </lay-row>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="开销时间" prop="paymentTime">
                <lay-date-picker v-model="accountCostVo.paymentTime" allow-clear type="date"></lay-date-picker>
              </lay-form-item>
            </lay-col>
          </lay-row>
          <lay-row space="20">
            <lay-col :md="6">
              <lay-form-item label="备注" prop="remark">
                <lay-textarea
                    v-model="accountCostVo.remark"
                    allow-clear
                    placeholder="请输入备注"
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="toSubmit">保存</lay-button>
          <lay-button size="sm" type="primary" @click="toReset">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="showSelectTypeModalFlag" :area="['1200px']" :title="title">
      <AccountTypeIconPicker @callBack="callBackFun"></AccountTypeIconPicker>
    </lay-layer>
  </lay-container>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "Tenant",
};
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {TableResult} from "../../../types/Result";
import {deleteAccountCost, findPageAccountCost, findAccountCost, saveUpdate} from "../../../api/account/Account";
import router from "../../../router";
import {Operate} from "../../../types/Constants";
import {AccountCostEntity, AccountCostVo, AccountType} from "@/types/account/Account";
import AccountTypeIconPicker from "@/views/component/svg/AccountTypeIconPicker.vue";
import SvgIcon from "@/views/component/svg/SvgIcon.vue";

/* INIT*/
onMounted(async () => {
  loadDataSource()
})
const loadDataSource = () => {
  findPageAccountCost(searchQuery.value, pageQuery).then((res: TableResult<AccountCostEntity>) => {
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
/* INIT*/

/* VAR*/
const $router = router;
const teleportProps = ref({to: 'body', disabled: false})
const searchQuery = ref<AccountCostEntity>({})
const loading = ref(false)
const selectedKeys = ref<Array<string>>([])
const accountCostVo = ref<AccountCostVo>({
  status: true
})
const sysTenantVoTemplate = ref<AccountCostVo>({
  status: true
})
const addExpenseFormRef = ref()
const addExpenseModalShowFlag = ref(false)
const dataSource = ref<Array<AccountCostEntity>>()
const title = ref('新增')
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '开销描述', width: '130px', key: 'costDesc', fixed: 'left'},
  {title: '开销金额', width: '130px', key: 'outlay'},
  {title: '开支类型', width: '130px', key: 'icon', customSlot: 'iconType'},
  {title: '开销标识', width: '130px', key: 'paymentSign'},
  {title: '时间', width: '150px', key: 'paymentTime'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const showSelectTypeModalFlag = ref<Boolean>(false)
/* VAR*/

/* FUNCTION*/
function toReset() {
  accountCostVo.value = {
    status: false,
  }
}

function toSearch() {
  pageQuery.current = 1
  dataSource.value = []
  change()
}

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
const showExpenseModal = (text: any, row: any) => {
  title.value = Operate.ADD === text ? "新增" : Operate.EDIT === text ? "编辑" : "";
  if (row != null) {
    accountCostVo.value = {...row}
  }
  // 编辑下查询包含敏感数据字段
  if (Operate.EDIT === text) {
    findAccountCost({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        accountCostVo.value = res.data;
      }
    })
  }
  accountCostVo.value.paymentSign = AccountType.EXPENSES
  addExpenseModalShowFlag.value = !addExpenseModalShowFlag.value
}

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
          deleteAccountCost(selectedKeys.value).then((res: any) => {
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

function toSubmit() {
  addExpenseFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      accountCostVo.value.costType = accountCostVo.value.icon?.split("_")[0];
      saveUpdate(accountCostVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          accountCostVo.value = {};
          addExpenseModalShowFlag.value = false
        }
      })
    }
  })
}

function toCancel() {
  accountCostVo.value = {}
  addExpenseModalShowFlag.value = false
}

function confirm(row: any) {
  if (row && row.buildIn) {
    layer.msg('系统内置参数无法删除！')
    return;
  } else {
    deleteAccountCost([row.id]).then((res: any) => {
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


function changeSelectTypeModal() {
  showSelectTypeModalFlag.value = !showSelectTypeModalFlag.value
}


const callBackFun = (icon: any) => {
  accountCostVo.value.icon = icon;
  changeSelectTypeModal()
}

/* FUNCTION*/
</script>

<style scoped>
.role-box {
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
</style>
