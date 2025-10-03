<template>
  <lay-container fluid="true">
    <lay-card shadow="hover">
      <lay-form @keydown.enter.prevent="toSearch" :model="searchQuery" label-position="top">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="描述">
              <lay-input
                  v-model="searchQuery.costDesc"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="备注">
              <lay-input
                  v-model="searchQuery.remark"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="12">
            <lay-form-item label="时间">
              <lay-date-picker style="width: 100%" v-model="searchQuery.paymentTimeRange" allow-clear range
                               :format="sdf_YMDHMS" :inputFormat="sdf_YMDHMS" type="datetime"
                               :shortcuts="defaultShortcuts" simple :default-time="dateRangeDefaultTime"
                               @change="change"></lay-date-picker>
            </lay-form-item>
          </lay-col>
        </lay-row>
        <lay-transition>
          <lay-row :space="20" v-if="expandCollapseFlag">
            <lay-col :md="6">
              <lay-form-item label="标识">
                <lay-select
                    style="width: 100%"
                    size="sm"
                    v-model="searchQuery.paymentSign"
                    :options="paymentSignSelectList"
                    :items="paymentSignSelectList"
                    :allow-clear="true"
                    placeholder="请选择"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="类型" prop="type">
                <lay-select
                    style="width: 100%"
                    size="sm"
                    v-model="searchQuery.costTypeList"
                    :options="selectCostTypeList"
                    :items="selectCostTypeList"
                    :allow-clear="true"
                    placeholder="请选择"
                    :show-search="true"
                    :multiple="true"
                    @change="change()"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="关联用户" prop="accountCostUserIdList">
                <div style="display: inline-flex; text-align: left">
                  <lay-button size="sm" type="primary" @click="changeShowModalFlag">选择</lay-button>
                  <div style="padding-left: 10px;">
                    <lay-select
                        style="width: 100%"
                        size="sm"
                        :disabled="true"
                        v-model="searchQuery.accountCostUserNameList"
                        :allow-clear="true"
                        :multiple="true"
                    ></lay-select>
                  </div>
                </div>

                <lay-layer v-model="showModalFlag" :title="'关联用户'" :area="['1200px', '700px']">
                  <div v-esc-close="toCancelUserModal">
                    <lay-table
                        ref="userModalTableRef"
                        :page="userModalPageQuery"
                        :columns="userModalColumns"
                        :loading="userModalLoading"
                        :data-source="userEntityList"
                        v-model:selected-keys="searchQuery.accountCostUserIdList"
                        @change="changeShowModalFlag"
                    >
                      <template v-slot:toolbar>
                        <lay-button size="sm" type="normal" @click="changeShowModalFlag">
                          <lay-icon class="layui-icon-addition"></lay-icon>
                          查询
                        </lay-button>
                        <lay-button size="sm" type="danger" @click="searchUserModalConfirm">
                          <lay-icon class="layui-icon-addition"></lay-icon>
                          确认
                        </lay-button>
                      </template>
                    </lay-table>
                  </div>
                </lay-layer>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-transition>
      </lay-form>
      <lay-line contentPosition="right" offset="8%">
        <span v-if="expandCollapseFlag" @click="changeExpandCollapseFlag">
          <lay-button type="primary" size="sm">收起<lay-icon class="layui-icon-up"></lay-icon></lay-button>
        </span>
        <span v-else @click="changeExpandCollapseFlag">
          <lay-button type="primary" size="sm">展开<lay-icon class="layui-icon-down"></lay-icon></lay-button>
        </span>
      </lay-line>
    </lay-card>
    <!-- table -->
    <lay-table
        v-model:selected-keys="selectedKeys"
        :columns="columns"
        :data-source="dataSource"
        :default-toolbar="true"
        :loading="loading"
        :page="pageQuery"
        :even="false"
        :resize="true"
        :rowStyle="getRowStyle"
        @change="change"
        @sortChange="sortChange">
      <template #nickNameList="{ row }">
        <lay-tooltip :visible="false" trigger="hover" :content='row.accountCostUserName'>
          <div class="oneRow">{{ row.accountCostUserName }}</div>
        </lay-tooltip>
      </template>
      <template #paymentTime="{ row }">
        {{ row.paymentTime }} （{{ getWeekdayCn(row.paymentTime) }}）
      </template>
      <template #remark="{ row }">
        <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
          <div class="oneRow">{{ row.remark }}</div>
        </lay-tooltip>
      </template>
      <template #acNickName="{ row }">
        <lay-tooltip :visible="false" trigger="hover" :content="row.acNickName">
          <div class="oneRow">{{ row.acNickName }}</div>
        </lay-tooltip>
      </template>
      <template #paymentSign="{ row }">
        <dict-tag :options="paymentSignSelect" :value="row.paymentSign"/>
      </template>
      <template #iconType="{ row }">
        <lay-avatar :src="row.icon" @click="preview(row.icon)"></lay-avatar>
        &nbsp;&nbsp;{{ row.costType }}
      </template>
      <template v-slot:toolbar>
        <lay-button
            size="sm"
            type="normal"
            @click="toSearch"
            v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_INDEX]"
        >
          查询
        </lay-button>
        <lay-button size="sm" @click="queryFormReset()"
                    v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_INDEX]"
        > 重置
        </lay-button>
        <lay-button
            v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_ADD]"
            size="sm"
            type="primary"
            @click="showExpenseModal(Operate.ADD, null)"
        >
          <lay-icon class="layui-icon-addition"></lay-icon>
          新增
        </lay-button>
        <lay-button v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_EDIT]" size="sm" @click="toRemove">
          <lay-icon class="layui-icon-delete"></lay-icon>
          删除
        </lay-button>
        <lay-button type="warm" v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_IMPORT]" size="sm"
                    @click="showAccountsImportModal">
          <lay-icon class="layui-icon-down"></lay-icon>
          导入
        </lay-button>
        <lay-button type="normal" v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_EXPORT]" size="sm"
                    @click="showAccountsExportModal">
          <lay-icon class="layui-icon-up"></lay-icon>
          按时间导出
        </lay-button>
      </template>
      <template v-slot:operator="{ row }">
        <lay-button
            v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_EDIT]"
            border="green"
            border-style="dashed"
            size="xs"
            @click="showExpenseModal(Operate.EDIT, row)">编辑
        </lay-button>
        <lay-button
            v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_EDIT]"
            border="orange"
            border-style="dashed"
            size="xs"
            @click="showExpenseModal(Operate.COPY, row)">复制
        </lay-button>
        <lay-popconfirm
            content="确定要删除吗?"
            @confirm="confirm(row)">
          <lay-button v-permission="[$ACCOUNT_MENU_PERMISSION.ACCOUNT_COST_DELETE]" border="red"
                      border-style="dashed"
                      size="xs">删除
          </lay-button>
        </lay-popconfirm>
      </template>
    </lay-table>

    <lay-layer v-model="addExpenseModalShowFlag" :area="['1200px']" :title="title">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" v-esc-close="expenseModalClose">
        <div style="margin-bottom: 10px">
          <lay-step :active="addExpenseActive" center>
            <lay-step-item title="记账" content="First step"></lay-step-item>
            <lay-step-item v-if="accountCostVo.accountCostUserIdList" title="分摊"
                           content="Second step"></lay-step-item>
          </lay-step>
        </div>
        <!-- 步骤一：记账-->
        <div v-show="addExpenseActive === 0">
          <lay-form ref="addExpenseFormRef" :model="accountCostVo" :rules="expenseFromRules" label-position="top">
            <lay-row space="20">
              <lay-col :md="6">
                <lay-form-item label="金额" prop="outlay" required>
                  <lay-input v-model="accountCostVo.outlay" ref="addExpenseModalQuickSaveRef"
                             type="number"></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="描述" prop="costDesc" required>
                  <lay-autocomplete
                      style="width: 100%"
                      v-model="accountCostVo.costDesc"
                      :fetchSuggestions="doFindCacheCostType"
                      :allow-clear="true"
                      @keydown.enter.stop
                      @select="doInputIconUrl"
                  ></lay-autocomplete>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="图标" prop="icon" required>
                  <lay-row>
                    <lay-col md="4">
                      <lay-avatar v-if="!accountCostVo.icon" class="iconContainer"
                                  @click="changeSelectTypeModal"></lay-avatar>
                      <lay-avatar v-else class="iconContainer" :src="accountCostVo.icon"
                                  @click="changeSelectTypeModal"></lay-avatar>
                    </lay-col>
                    <lay-col md="20"
                             style="justify-content: center; align-items: center; font-size: 10pt; line-height: 40px">
                      图标：{{ accountCostVo.costType }}
                    </lay-col>
                  </lay-row>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="标识" prop="paymentSign" required>
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
                <lay-form-item label="时间" prop="paymentTime">
                  <lay-date-picker v-model="accountCostVo.paymentTime" allow-clear type="datetime"
                                   :shortcuts="singleShortcuts" :inputFormat="sdf_YMDHM"
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
              <lay-col :md="6">
                <lay-form-item label="关联用户" prop="accountCostUserIdList">
                  <lay-col :md="6">
                    <div style="display: inline-flex; text-align: left">
                      <lay-button size="sm" type="primary" @click="changeShowModalFlag">选择</lay-button>
                      <div style="padding-left: 10px;">
                        <lay-select
                            style="width: 100%"
                            size="sm"
                            :disabled="true"
                            v-model="accountCostVo.accountCostUserNameList"
                            :allow-clear="true"
                            :multiple="true"
                        ></lay-select>
                      </div>
                    </div>

                    <lay-layer v-model="showModalFlag" :title="'关联用户'" :area="['1200px', '700px']">
                      <div v-esc-close="toCancelUserModal">
                        <lay-table
                            ref="userModalTableRef"
                            :page="userModalPageQuery"
                            :columns="userModalColumns"
                            :loading="userModalLoading"
                            :data-source="userEntityList"
                            v-model:selected-keys="accountCostVo.accountCostUserIdList"
                            @change="changeShowModalFlag"
                        >
                          <template v-slot:toolbar>
                            <lay-button size="sm" type="normal" @click="changeShowModalFlag">
                              <lay-icon class="layui-icon-addition"></lay-icon>
                              查询
                            </lay-button>
                            <lay-button size="sm" type="danger" @click="insertUserModalConfirm">
                              <lay-icon class="layui-icon-addition"></lay-icon>
                              确认
                            </lay-button>
                          </template>
                        </lay-table>
                      </div>
                    </lay-layer>
                  </lay-col>
                </lay-form-item>
              </lay-col>
            </lay-row>
          </lay-form>
        </div>
        <!-- 步骤二：分摊-->
        <div v-if="addExpenseActive === 1">
          <lay-table
              ref="expenseAllocationTableRef"
              :columns="expenseAllocationColumns"
              :loading="expenseAllocationModalLoading"
              :data-source="accountCostVo.expenseAllocationUserList"
          >
            <template #amount="{ row }">
              <lay-input v-model="row.amount" type="number"/>
            </template>
          </lay-table>
        </div>
        <div style="width: 100%; text-align: right">
          <lay-button v-show="accountCostVo.accountCostUserIdList && addExpenseActive === 0" size="sm" type="primary"
                      @click="toNext()">下一步
          </lay-button>
          <lay-button v-show="accountCostVo.accountCostUserIdList && addExpenseActive === 1" size="sm" type="primary"
                      @click="toPrevious">上一步
          </lay-button>
          <lay-button v-show="!accountCostVo.accountCostUserIdList || addExpenseActive === 1" size="sm" type="primary"
                      @click="toSubmit(true)">保存
          </lay-button>
          <lay-button size="sm" type="primary" @click="toReset">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="showSelectTypeModalFlag" :area="['1200px', '500px']" :title="title">
      <div v-esc-close="selectTypeModalClose">
        <!--                <AccountTypeIconPicker @callBack="callBackFun" :size="'3.5em'"></AccountTypeIconPicker>-->
        <IconPicker :dataSource="findCommonIconPickerDataSource" :openKeys="openKeys" @callBack="callBackFun"/>
      </div>
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
          v-esc-close="hideAccountsImportModal"
      >
        <template #preview>
          <div v-if="fileList.length > 0" v-for="(file, index) in fileList">
            {{ index + ". " + file.name }}
          </div>
        </template>
      </lay-upload>
      <div style="width: 100%;margin-top: 20px; text-align: center; margin-bottom: 20px">
        <lay-button size="sm" type="primary" @click="toUpload">上传</lay-button>
        <lay-button size="sm" type="normal" @click="downloadTemplate">下载模板</lay-button>
        <lay-button size="sm" @click="hideAccountsImportModal">取消</lay-button>
      </div>
      <div style="width: 100%; text-align: center">
        只能上传小于10MB的文件
      </div>
    </lay-layer>

    <lay-layer v-model="showAccountsExportModalFlag" :area="['600px']" :title="title">
      <div style="padding: 20px" v-esc-close="toCancel">
        <lay-form ref="accountsExportFormRef" :model="accountsExportVo" :rules="accountsExportFromRules"
                  label-position="top">
          <lay-row space="20">
            <lay-col :md="24">
              <lay-form-item label="导出时间" prop="paymentTime" required>
                <lay-date-picker style="width: 100%" v-model="accountsExportVo.paymentTimeRange" allow-clear range
                                 type="datetime" :default-time="dateRangeDefaultTime"
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
import PopLayer from "@/views/component/poplayer/PopLayer.vue";
import PopFormItem from "@/views/component/poplayer/PopFormItem.vue";

/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "Accounts",
  components: {PopFormItem, PopLayer},
};
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {LaySelectEntity, PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {
  deleteAccountCost,
  findPageAccountCost,
  findAccountCost,
  saveUpdate,
  accountsImport, accountsExport, findSelectCostTypeList, findCacheCostType
} from "@/api/account/Account";
import router from "@/router";
import {Operate} from "@/types/Constants";
import {
  AccountCostEntity,
  AccountCostVo,
  FindCacheCostTypeEntity,
  FindCacheCostTypeVo,
  PaymentSign
} from "@/types/account/Account";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {buildRange, defaultShortcuts, singleShortcuts, getWeekdayCn} from "@/util/UDate";
import AccountTypeIconPicker from "@/views/component/svg/AccountTypeIconPicker.vue";
import {FindPageSysUserListEntity, SysUserEntity, SysUserVo} from "@/types/system/User";
import {findListSysUserById, findPageSysUserWithoutDataScope} from "@/api/system/User";
import app from "@/main";
import IconPicker from "@/views/component/svg/IconPicker.vue";
import {FindCommonIconEntity} from "@/types/common/icon/Icon";
import {CommonIconTemplateDetailVo, FindTreeIconTreeTypeEntity} from "@/types/common/icon/template/IconTemplateDetail";
import {findCustomIconTemplateDetail} from "@/api/common/icon/template/IconTemplateDetail";
import {preview} from "@/util/UImage";
import {useAppStore} from "@/store/app";
import Http from "@/api/Http";
import {findConfigByKey} from "@/api/system/Config";
import {SysConfigKey} from "@/types/system/Config";

/* INIT*/
onMounted(async () => {
  paymentSignSelect.value = await loadSysDictValue(Constants.PAYMENT_SIGN)
  paymentSignSelectList.value = await sysDictValueSelect(paymentSignSelect.value)
  searchQuery.value.paymentTimeRange = buildRange(7)
  let param: CommonIconTemplateDetailVo = {
    headerId: useStore.commonIconHeader
  }
  findCustomIconTemplateDetail(param).then((res: R<FindTreeIconTreeTypeEntity[]>) => {
    findCommonIconPickerDataSource.value = res.data;
    let temp: string[] = []
    findCommonIconPickerDataSource.value?.forEach((res: any) => {
      temp.push(res.name)
    })
    openKeys.value = temp;
  }).catch(e => {
    layer.msg(e.msg)
  });
  doFindPageUser();
  doFindSelectCostTypeList();
  loadDataSource()
})
/* INIT*/

/* VAR*/
const useStore = useAppStore()
const $ACCOUNT_MENU_PERMISSION = app.config.globalProperties.$ACCOUNT_MENU_PERMISSION
const $router = router;
const paymentSignSelect = ref<Array<SysDictValueEntity>>();
const paymentSignSelectList = ref<any[]>();
const searchQuery = ref<AccountCostVo>({})
const loading = ref(false)
const selectedKeys = ref<Array<string>>([])
const accountCostVo = ref<AccountCostVo>({
  status: true,
  accountCostUserIdList: [],
  accountCostUserNameList: [],
})
const addExpenseFormRef = ref(null)
const expenseAllocationTableRef = ref(null)
const addExpenseModalShowFlag = ref(false)
const dataSource = ref<Array<AccountCostEntity>>()
const title = ref('新增')
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10,
  limits: [10, 20, 50, 100],
  hideOnSinglePage: false,
  layout: ['count', 'prev', 'page', 'next', 'limits', 'refresh', 'skip'],
})
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '描述', width: '130px', key: 'costDesc', fixed: 'left', ellipsisTooltipTheme: 'dark'},
  {title: '金额', width: '130px', key: 'outlay', sort: 'desc'},
  {title: '类型', width: '130px', key: 'icon', customSlot: 'iconType'},
  {title: '标识', width: '130px', key: 'paymentSign', customSlot: 'paymentSign'},
  {title: '时间', width: '200px', key: 'paymentTime', customSlot: 'paymentTime', sort: 'desc'},
  {title: '修改时间', width: '150px', key: 'modifyTime'},
  {title: '记录人', width: '100px', key: 'acNickName', customSlot: 'acNickName'},
  {title: '关联用户', width: '200px', key: 'nickNameList', customSlot: 'nickNameList'},
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
const sdf_YMDHM = 'YYYY-MM-DD HH:mm'
const addExpenseModalQuickSaveRef = ref()
const userEntityList = ref<Array<SysUserEntity>>()
const userModalLoading = ref(false)
const expenseAllocationModalLoading = ref(false)
const userModalSelectedKeys = ref<Array<string>>([])
const userModalPageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const userModalColumns = [
  {title: '选项', type: 'checkbox', fixed: 'left'},
  {title: '用户名称', key: 'userName'},
  {title: '用户昵称', key: 'nickName'},
  {title: '用户类型', key: 'userType'},
]
const expenseAllocationColumns = [
  {title: '用户名称', key: 'userName'},
  {title: '用户昵称', key: 'nickName'},
  {title: '分摊金额', key: 'amount', customSlot: 'amount'},
]
const userModalSearchQuery = ref<SysUserVo>({})
const userModalTableRef = ref();
const dateRangeDefaultTime = ['00:00:00', '23:59:59'];
const expandCollapseFlag = ref<boolean>(false);
const findCommonIconPickerDataSource = ref<Array<FindCommonIconEntity>>()
const selectCostTypeList = ref<LaySelectEntity[]>([]);
const openKeys = ref<string[]>([]);
const showModalFlag = ref<Boolean>(false)
const addExpenseActive = ref(0)
/* VAR*/

/* FUNCTION*/
const loadDataSource = () => {
  findPageAccountCost(searchQuery.value, pageQuery).then((res: TableResult<AccountCostEntity>) => {
    pageQuery.total = res.total;
    dataSource.value = res.rows
  })
}

function toReset() {
  if (addExpenseActive.value === 0) {
    accountCostVo.value = {
      status: false,
    }
  } else if (addExpenseActive.value === 1) {
    accountCostVo.value.expenseAllocationUserList = [];
  }
}

function toSearch() {
  pageQuery.current = 1
  dataSource.value = []
  change()
}

const change = () => {
  doFindSelectCostTypeList()
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 200)
}
const sortChange = (key: any, sort: string) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
  if (key === 'paymentTime') {
    dataSource.value?.sort((a: any, b: any) => {
      if (sort === 'asc') {
        return new Date(a.paymentTime).getTime() - new Date(b.paymentTime).getTime();
      } else {
        return new Date(b.paymentTime).getTime() - new Date(a.paymentTime).getTime();
      }
    })
  }
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
        let data = res.data;
        accountCostVo.value = data;
        accountCostVo.value.accountCostUserIdList = data?.accountCostUserId?.split(',')
        accountCostVo.value.accountCostUserNameList = data?.accountCostUserName?.split(',')
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
  } else if (Operate.COPY === text) {
    findAccountCost({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        let data = res.data;
        accountCostVo.value = data;
        accountCostVo.value.accountCostUserIdList = data?.accountCostUserId?.split(',')
        accountCostVo.value.accountCostUserNameList = data?.accountCostUserName?.split(',')
        accountCostVo.value.id = null;
        accountCostVo.value.recVer = null;
      }
    })
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
      let id = accountCostVo.value.id;
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
            // 如果是修改+回车，则关闭窗口
            if (id) {
              addExpenseModalShowFlag.value = false
            } else {
              addExpenseModalQuickSaveRef.value.focus();
            }
          }
        }
      })
    }
  })
}

function toCancel() {
  accountCostVo.value = {}
  addExpenseModalShowFlag.value = false
  showAccountsExportModalFlag.value = false;
}

function expenseModalClose() {
  accountCostVo.value = {}
  addExpenseModalShowFlag.value = false
}

function selectTypeModalClose() {
  showSelectTypeModalFlag.value = false
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


function changeSelectTypeModal() {
  showSelectTypeModalFlag.value = !showSelectTypeModalFlag.value
}


const callBackFun = (icon: FindCommonIconEntity) => {
  accountCostVo.value.icon = icon.url;
  accountCostVo.value.costType = icon.name
  changeSelectTypeModal()
}

function showAccountsImportModal() {
  visibleImport.value = true
}

function hideAccountsImportModal() {
  visibleImport.value = false
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
    layer.msg('导出成功', {icon: 1})
    showAccountsExportModalFlag.value = !showAccountsExportModalFlag.value
  })
}

function queryFormReset() {
  searchQuery.value = {}
  searchQuery.value.paymentTimeRange = buildRange(7)
}

function doFindPageUser() {
  findPageSysUserWithoutDataScope(userModalSearchQuery.value, userModalPageQuery).then((res: any) => {
    if (res.code == 200) {
      userEntityList.value = res.rows;
      userModalPageQuery.total = res.total;
    }
  })
}

function userModalChange() {
  userModalLoading.value = true
  setTimeout(() => {
    doFindPageUser()
    userModalLoading.value = false
  }, 200)
}

/**
 * 处理确定事件
 */
const handleConfirm = (selectKeys: string[], rows: [], tableRef: object) => {
  accountCostVo.value.accountCostUserNameList = rows?.map(v => v.nickName);
  accountCostVo.value.accountCostUserIdList = selectKeys
  userModalSelectedKeys.value = []
};

const searchQueryHandleConfirm = (selectKeys: string[], rows: [], tableRef: object) => {
  accountCostVo.value.accountCostUserNameList = rows?.map(v => v.nickName);
  accountCostVo.value.accountCostUserIdList = selectKeys
  userModalSelectedKeys.value = []
};

function changeExpandCollapseFlag() {
  expandCollapseFlag.value = !expandCollapseFlag.value
}

function getDayColor(date: Date) {
  const day = new Date(date).getDay();
  if (day === 0) {
    return '#ff9a9e';
  }
  if (day === 1) return '#FF5722';
  if (day === 2) return '#FFB800';
  if (day === 3) return '#36b368';
  if (day === 4) return '#2d8cf0';
  if (day === 5) return '#3963bc';
  if (day === 6) return '#998adb';
  return '#000000'
}

function getRowStyle(row: any, rowIndex: number) {
  const day = new Date(row.paymentTime).getDay();
  if (day === 0) return 'background-color:' + 'rgba(255, 154, 158, 0.4)';
  if (day === 1) return 'background-color:' + 'rgba(255, 87, 34, 0.4)';
  if (day === 2) return 'background-color:' + 'rgba(255, 184, 0, 0.4)';
  if (day === 3) return 'background-color:' + 'rgba(54, 179, 104, 0.4)';
  if (day === 4) return 'background-color:' + 'rgba(45, 140, 240, 0.4)';
  if (day === 5) return 'background-color:' + 'rgba(57, 99, 188, 0.4)';
  if (day === 6) return 'background-color:' + 'rgba(153, 138, 219, 0.4)';
  return ''
}

function changeShowModalFlag() {
  userModalLoading.value = true
  setTimeout(() => {
    doFindPageUser()
    userModalLoading.value = false
  }, 200)
  showModalFlag.value = !showModalFlag.value
}

function searchUserModalConfirm() {
  let checkData = userModalTableRef.value.getCheckData();
  searchQuery.value.accountCostUserNameList = checkData.map((v: any) => v.nickName)
  searchQuery.value.accountCostUserIdList = checkData.map((v: any) => v.id)
  showModalFlag.value = !showModalFlag.value
}

function insertUserModalConfirm() {
  let checkData = userModalTableRef.value.getCheckData();
  accountCostVo.value.accountCostUserNameList = checkData.map((v: any) => v.nickName)
  accountCostVo.value.accountCostUserIdList = checkData.map((v: any) => v.id)
  showModalFlag.value = !showModalFlag.value
}

function toCancelUserModal() {
  showModalFlag.value = !showModalFlag.value
}

async function downloadTemplate() {
  findConfigByKey(SysConfigKey.ACCOUNT_IMPORT_TEMPLATE_URL).then((res: any) => {
    if (res.code === 200) {
      Http.downloadUrl(res.data)
      layer.notify({
        title: "消息",
        content: '下载模板成功！',
        icon: 1,
      })
    }
  })
}

function doFindSelectCostTypeList() {
  findSelectCostTypeList(searchQuery.value).then((res: R<LaySelectEntity[]>) => {
    selectCostTypeList.value = res.data
  }).catch(e => {
    layer.confirm(e.message)
  })
}

function doFindCacheCostType(value: any) {
  if (!value || value === '') {
    return;
  }
  let vo: FindCacheCostTypeVo = {
    costDesc: value
  }
  return findCacheCostType(vo).then((res: any) => {
    if (res.code === 200) {
      return res.data?.map((item: FindCacheCostTypeEntity) => {
        return {
          value: item.value,
          iconUrl: item.iconUrl,
          disabled: item.disabled
        }
      })
    }
  });
}

function doInputIconUrl(entity: FindCacheCostTypeEntity) {
  accountCostVo.value.icon = entity.iconUrl;
  accountCostVo.value.costType = entity.value;
}

function toNext() {
  addExpenseFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      addExpenseActive.value = addExpenseActive.value + 1
      if (accountCostVo.value.accountCostUserIdList) {
        findListSysUserById(accountCostVo.value.accountCostUserIdList).then((res: any) => {
          if (res.code === 200) {
            accountCostVo.value.expenseAllocationUserList = res.data
          }
        })
      }
    }
  })
}

function toPrevious() {
  addExpenseActive.value = addExpenseActive.value - 1
}

/* FUNCTION*/
</script>

<style scoped>
.iconContainer {
  width: 40px;
  height: 40px;
}
</style>