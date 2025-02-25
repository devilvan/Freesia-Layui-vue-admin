<template>
  <lay-container class="role-box" fluid="true">
    <lay-card>
      <lay-form style="margin-top: 10px" @keyup.enter.prevent="toSearch" :model="searchQuery">
        <lay-row>
          <lay-col :md="6">
            <lay-form-item label="开销描述">
              <lay-input
                  v-model="searchQuery.costDesc"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="开销备注">
              <lay-input
                  v-model="searchQuery.remark"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="12">
            <lay-form-item label="开销时间">
              <lay-date-picker style="width: 100%" v-model="searchQuery.paymentTimeRange" allow-clear range
                               :format="sdf_YMDHMS" :inputFormat="sdf_YMDHMS" type="datetime"
                               :shortcuts="defaultShortcuts" simple></lay-date-picker>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="开销标识">
              <lay-select
                  class="search-input"
                  size="sm"
                  v-model="searchQuery.paymentSign"
                  :options="paymentSignSelectList"
                  :items="paymentSignSelectList"
                  :allow-clear="true"
                  placeholder="请选择"
              ></lay-select>
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
          @change="change"
          @sortChange="sortChange">
        <template #remark="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
            <div class="oneRow">{{ row.remark }}</div>
          </lay-tooltip>
        </template>
        <template #paymentSign="{ row }">
          <dict-scan :options="paymentSignSelect" :value="row.paymentSign"/>
        </template>
        <template #iconType="{ row }">
          <SvgIcon :name="row.icon" size="2em"></SvgIcon>
          {{ row.icon }}
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
          <lay-button size="sm" @click="queryFormReset()"> 重置</lay-button>
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
          <lay-button type="warm" v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_EDIT]" size="sm"
                      @click="showAccountsImportModal">
            <lay-icon class="layui-icon-down"></lay-icon>
            导入
          </lay-button>
          <lay-button type="normal" v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_EDIT]" size="sm"
                      @click="showAccountsExportModal">
            <lay-icon class="layui-icon-up"></lay-icon>
            按时间导出
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
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" @keydown.esc.prevent="toCancel">
        <lay-form ref="addExpenseFormRef" :model="accountCostVo" :rules="expenseFromRules" label-position="top">
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
              <lay-form-item label="开销标识" prop="paymentSign" required>
                <lay-select
                    size="sm"
                    style="width: 100%"
                    v-model="accountCostVo.paymentSign"
                    :options="paymentSignSelectList"
                    :items="paymentSignSelectList"
                    :allow-clear="true"
                    placeholder="请选择"
                ></lay-select>
              </lay-form-item>
            </lay-col>
          </lay-row>
          <lay-row space="20">
            <lay-col :md="6">
              <lay-form-item label="开销时间" prop="paymentTime">
                <lay-date-picker v-model="accountCostVo.paymentTime" allow-clear type="datetime"
                                 :shortcuts="singleShortcuts" :inputFormat="'YYYY-MM-DD HH:mm'"
                                 style="width: 100%" simple></lay-date-picker>
              </lay-form-item>
            </lay-col>
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
          <lay-button size="sm" type="primary" @click="toSubmit(true)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="toReset">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="showSelectTypeModalFlag" :area="['1200px']" :title="title">
      <AccountTypeIconPicker @callBack="callBackFun"></AccountTypeIconPicker>
    </lay-layer>

    <lay-layer
        v-model="visibleImport"
        title="导入"
        :area="['380px', '500px']"
    >
      <lay-upload
          style="margin: 60px"
          :url="accountsImportUrl"
          v-model="fileList"
          field="file"
          acceptMime="application/vnd.ms-excel,
          application/vnd.ms-excel.sheet.binary.macroenabled.12,
          application/vnd.ms-excel.sheet.macroenabled.12,
          application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          :auto="false"
          :drag="true"
      >
        <template #preview>
          <div v-if="fileList.length > 0" v-for="(file, index) in fileList">
            {{ index + ". " + file.name }}
          </div>
        </template>
      </lay-upload>
      <div style="width: 100%; text-align: center">
        只能上传小于10MB的文件
      </div>
      <div style="width: 100%;margin-top: 20px; text-align: center">
        <lay-button size="sm" type="primary" @click="toUpload">上传</lay-button>
      </div>
    </lay-layer>

    <lay-layer v-model="showAccountsExportModalFlag" :area="['600px']" :title="title">
      <div style="padding: 20px" @keydown.esc.prevent="toCancel">
        <lay-form ref="accountsExportFormRef" :model="accountsExportVo" :rules="accountsExportFromRules"
                  label-position="top">
          <lay-row space="20">
            <lay-col :md="24">
              <lay-form-item label="导出时间" prop="paymentTime" required>
                <lay-date-picker style="width: 100%" v-model="accountsExportVo.paymentTimeRange" allow-clear range
                                 type="datetime"
                                 :shortcuts="defaultShortcuts" simple></lay-date-picker>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="doExport">确定</lay-button>
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
  name: "MineAccounts",
};
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../../types/Common";
import {TableResult} from "../../../../types/Result";
import {
  deleteAccountCost,
  findPageAccountCost,
  findAccountCost,
  saveUpdate,
  accountsImport, accountsExport
} from "../../../../api/account/Account";
import router from "../../../../router";
import {Operate} from "../../../../types/Constants";
import {AccountCostEntity, AccountCostVo, PaymentSign} from "@/types/account/Account";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {buildRange, defaultShortcuts, singleShortcuts} from "@/util/UDate";
import AccountTypeIconPicker from "@/views/component/svg/AccountTypeIconPicker.vue";
import SvgIcon from "@/views/component/svg/SvgIcon.vue";

/* INIT*/
onMounted(async () => {
  paymentSignSelect.value = await loadSysDictValue(Constants.PAYMENT_SIGN)
  paymentSignSelectList.value = await sysDictValueSelect(paymentSignSelect.value)
  searchQuery.value.paymentTimeRange = buildRange(7)
  loadDataSource()
})
/* INIT*/

/* VAR*/
const $router = router;
const paymentSignSelect = ref();
const paymentSignSelectList = ref();
const searchQuery = ref<AccountCostVo>({})
const loading = ref(false)
const selectedKeys = ref<Array<string>>([])
const accountCostVo = ref<AccountCostVo>({
  status: true
})
const addExpenseFormRef = ref(null)
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
  {title: '开销标识', width: '130px', key: 'paymentSign', customSlot: 'paymentSign'},
  {title: '开支时间', width: '150px', key: 'paymentTime'},
  {title: '备注', width: '150px', key: 'remark', customSlot: 'remark'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const showSelectTypeModalFlag = ref<Boolean>(false)
const now = new Date()
const expenseFromRules = ref({
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
const fileList = ref([])
const accountsImportUrl = import.meta.env.VITE_APP_BASE_URL as string + '/api/accountCostController/accountsImport'
const visibleImport = ref(false)
const showAccountsExportModalFlag = ref(false)
const accountsExportFormRef = ref()
const accountsExportVo = ref<AccountCostVo>({})
const accountsExportFromRules = ref({
  paymentTime: {
    validator(rule: { field: any; }, value: any, callback: (arg0: Error) => void) {
      if (!value) {
        callback(new Error("日期不能为空"));
        let split = value.split(",");
        if (split.length !== 2) {
          callback(new Error("日期格式不正确，应包含日期从、日期到"));
        }
        let dateFrom = new Date(split[0]);
        if (!isNaN(dateFrom.getTime())) {
          callback(new Error("日期从格式有误" + dateFrom));
        }
        let dateTo = new Date(split[0]);
        if (!isNaN(dateTo.getTime())) {
          callback(new Error("日期到格式有误" + dateTo));
        }
      }
    }
  },
})
const sdf_YMDHMS = 'YYYY-MM-DD HH:mm:ss'
/* VAR*/

/* FUNCTION*/
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
  if (Operate.EDIT === text) {
    findAccountCost({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        accountCostVo.value = res.data;
      }
    })
  } else if (Operate.ADD === text) {
    accountCostVo.value = {}
    accountCostVo.value.paymentTime = now
    let isDefaultPaymentSignSelect = paymentSignSelectList.value.find((paymentSignSelect: SysDictValueEntity) => {
      return paymentSignSelect.isDefault
    });
    if (isDefaultPaymentSignSelect) {
      accountCostVo.value.paymentSign = isDefaultPaymentSignSelect.value;
    }
  }
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

function toSubmit(clickFlag: boolean) {
  addExpenseFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      accountCostVo.value.costType = accountCostVo.value.icon?.split("_")[0];
      saveUpdate(accountCostVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          let paymentTime = accountCostVo.value.paymentTime;
          accountCostVo.value = {};
          if (clickFlag) {
            addExpenseModalShowFlag.value = false
          } else {
            accountCostVo.value.paymentTime = paymentTime
            let isDefaultPaymentSignSelect = paymentSignSelectList.value.find((paymentSignSelect: SysDictValueEntity) => {
              return paymentSignSelect.isDefault
            });
            if (isDefaultPaymentSignSelect) {
              accountCostVo.value.paymentSign = isDefaultPaymentSignSelect.value;
            }
          }
        }
      })
    }
    addExpenseFormRef.value.focus();
  })
}

function toCancel() {
  accountCostVo.value = {}
  addExpenseModalShowFlag.value = false
  showAccountsExportModalFlag.value = false;
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

function showAccountsImportModal() {
  visibleImport.value = true
}

function showAccountsExportModal() {
  accountsExportVo.value.paymentTimeRange = buildRange(7)
  showAccountsExportModalFlag.value = true
}

function toUpload() {
  if (!fileList.value || fileList.value.length < 1) {
    layer.confirm('清选择文件', {icon: 3})
    return;
  }
  accountsImport(fileList.value).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg, {icon: 1})
      fileList.value = []
      loadDataSource()
      visibleImport.value = !visibleImport.value
    }
  })
}

function doExport() {
  accountsExport(accountsExportVo.value).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg, {icon: 1})
      showAccountsExportModalFlag.value = !showAccountsExportModalFlag.value
    }
  })
}

function queryFormReset() {
  searchQuery.value = {}
  searchQuery.value.paymentTimeRange = buildRange(7)
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
