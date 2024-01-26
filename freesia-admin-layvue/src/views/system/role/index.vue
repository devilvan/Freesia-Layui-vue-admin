<template>
  <lay-container fluid="true" class="role-box">
    <lay-card>
      <lay-form style="margin-top: 10px">
        <lay-row>
          <lay-col :md="5">
            <lay-form-item label="角色名称" label-width="80">
              <lay-input
                  v-model="searchQuery.roleName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label="角色标识" label-width="80">
              <lay-input
                  v-model="searchQuery.identifying"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label="备注" label-width="80">
              <lay-input
                  v-model="searchQuery.mark"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
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
          :page="pageQuery"
          :columns="columns"
          :loading="loading"
          :default-toolbar="true"
          :data-source="dataSource"
          v-model:selected-keys="selectedKeys"
          @change="change"
          @sortChange="sortChange"
      >
        <template #status="{ row }">
          <lay-switch
              :model-value="row.status === '0'"
              @change="changeStatus($event, row)"
          ></lay-switch>
        </template>
        <template #dataScope="{ row }">
          <dict-scan :options="sysDataScope" :value="row.dataScope"/>
        </template>
        <template v-slot:toolbar>
          <lay-button size="sm" type="primary" @click="changeVisible11('新增', null)">
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
          <lay-button size="sm" type="normal" @click="assignUser" v-permission="[MenuPermission.SYS_ROLE_USER_EDIT]">
            分配用户
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button
              size="xs"
              border="green"
              border-style="dashed"
              @click="changeVisible11('编辑', row)"
          >编辑
          </lay-button
          >
          <lay-button
              size="xs"
              border="blue"
              border-style="dashed"
              @click="toPrivileges(row)"
              v-permission="[MenuPermission.SYS_ROLE_MENU_EDIT]"
          >菜单权限
          </lay-button
          >
          <lay-button
              size="xs"
              border="blue"
              border-style="dashed"
              @click="assignUserById(row.id)"
              v-permission="[MenuPermission.SYS_ROLE_USER_EDIT]"
          >分配用户
          </lay-button
          >
          <lay-popconfirm
              content="确定要删除此角色吗?"
              @confirm="confirm"
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

    <lay-layer v-model="visible11" :title="title" :area="['500px', '370px']">
      <div style="padding: 20px">
        <lay-form :model="model11" ref="queryParamsFormRef" required>
          <lay-form-item label="角色名称" prop="name">
            <lay-input v-model="model11.name"></lay-input>
          </lay-form-item>
          <lay-form-item label="角色标识" prop="flage">
            <lay-input v-model="model11.flage"></lay-input>
          </lay-form-item>
          <lay-form-item label="描述" prop="remark">
            <lay-textarea
                placeholder="请输入描述"
                v-model="model11.remark"
            ></lay-textarea>
          </lay-form-item>
        </lay-form>
        <div style="width: 100%; text-align: center">
          <lay-button size="sm" type="primary" @click="toSubmit"
          >保存
          </lay-button
          >
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="saveRoleMenuPrivilegeVisible" title="分配权限" :area="['500px', '800px']">
      <div style="height: 300px; padding: 20px">
        <lay-form :model="saveRoleMenuPrivilegeModel" ref="saveRoleMenuPrivilegeFormRef" :label-position="'top'">
          <lay-form-item label="角色ID" prop="roleId">
            <lay-input v-model="saveRoleMenuPrivilegeModel.roleId" :disabled="true"></lay-input>
          </lay-form-item>
          <lay-form-item label="角色名称" prop="roleName">
            <lay-input v-model="saveRoleMenuPrivilegeModel.roleName" :disabled="true"></lay-input>
          </lay-form-item>
          <lay-form-item label="数据范围" prop="dataScope">
            <lay-select
                :disabled="!isAdmin()"
                class="search-input"
                size="sm"
                style="width: 100%"
                v-model="saveRoleMenuPrivilegeModel.dataScope"
                :options="sysDataScopeSelect"
                :items="sysDataScopeSelect"
                :allow-clear="true"
                placeholder="请选择"
            ></lay-select>
            <!--            <lay-input v-model="saveRoleMenuPrivilegeModel.dataScope" :disabled="true">-->
            <!--            </lay-input>-->
          </lay-form-item>
          <lay-form-item label="菜单树" prop="treeSelectedIdList">
            <lay-container>
              <lay-tree
                  class="layTreeContainer"
                  :tail-node-icon="true"
                  :data="menuTree"
                  :showCheckbox="menuTreeShowCheckbox2"
                  v-model:checkedKeys="saveRoleMenuPrivilegeModel.treeSelectedIdList"
              >
                <template #title="{ data }">
                  <lay-icon :class="data.icon"></lay-icon>
                  {{ data.menuName }}
                </template>
              </lay-tree>
            </lay-container>
          </lay-form-item>
        </lay-form>
      </div>
      <div class="saveRoleMenuPrivilegeLayerFooter">
        <lay-line></lay-line>
        <div style="width: 95%; text-align: right">
          <lay-button size="sm" type="primary" @click="toPrivilegesSubmit">保存</lay-button>
          <lay-button size="sm" @click="toPrivilegesCancel">取消</lay-button>
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
  name: "Role",
};
</script>
<script setup lang="ts">
import {defineComponent, onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {FindPageSysRoleListEntity, SaveRoleMenuPrivilegeVo, SysRoleVo} from "../../../types/system/Role";
import {findPageSysRoleList, saveRoleMenuPrivilege} from "../../../api/system/Role";
import {PageQuery} from "../../../types/Common";
import {FindAllMenuTreeEntity} from "../../../types/system/Menu";
import {findAllMenuTree, findSelectedMenuListByRoleId} from "../../../api/system/Menu";
import {Constants, loadSysDictValue, sysDictValueSelect} from "../../../util/UDict";
import {SysDictValueEntity} from "../../../types/system/Dict";
import {MenuPermission} from '../../../types/Permission';
import {useAppStore} from "../../../store/app";
import {useUserStore} from "../../../store/user";
import app from "../../../main";
import router from "../../../router";
/* INIT*/
defineComponent({
  components: {MenuPermission}
})
onMounted(async () => {
  sysDataScope.value = await loadSysDictValue(Constants.SYS_DATA_SCOPE)
  sysDataScopeSelect.value = await sysDictValueSelect(sysDataScope.value);
  await loadDataSource()
  await loadAllMenuTree()
})
const loadDataSource = async () => {
  const {code, total, rows} = await findPageSysRoleList(searchQuery.value, pageQuery)
  if (code == 200) {
    pageQuery.total = total;
    dataSource.value = rows
  }
}

// 初始化菜单下拉树
async function loadAllMenuTree() {
  const {data, code} = await findAllMenuTree()
  if (code === 200) {
    menuTree.value = data;
  }
}

/* INIT*/

/* VAR*/
const $router = router;
const appStore = useAppStore();
const userStore = useUserStore();
const searchQuery = ref<SysRoleVo>({})
const loading = ref(false)
const selectedKeys = ref()
const pageQuery = reactive<PageQuery>({current: 1, limit: 10})
const sysDataScope = ref<Array<SysDictValueEntity>>([]);
const sysDataScopeSelect = ref<Array<SysDictValueEntity>>([]);
const model11 = ref({
  name: '',
  flage: '',
  remark: ''
})
const saveRoleMenuPrivilegeModel = ref<SaveRoleMenuPrivilegeVo>({});
const queryParamsFormRef = ref()
const saveRoleMenuPrivilegeFormRef = ref()
const visible11 = ref(false)
const saveRoleMenuPrivilegeVisible = ref(false)
const menuTreeShowCheckbox2 = ref(true)
const menuTree = ref<Array<FindAllMenuTreeEntity>>()
const selectRowRoleId = ref<string>("");
const title = ref('新增')
const dataSource = ref<Array<FindPageSysRoleListEntity>>()
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '编号', width: '160px', key: 'id', fixed: 'left', sort: 'desc'},
  {title: '角色名称', width: '150px', key: 'roleName', sort: 'desc'},
  {title: '角色权限编码', width: '120px', key: 'roleKey', sort: 'asc'},
  {title: '数据范围', width: '200px', key: 'dataScope', sort: 'desc', customSlot: 'dataScope'},
  {title: '状态', width: '80px', key: 'status', customSlot: 'status'},
  {title: '创建时间', width: '160px', key: 'createTime', sort: 'desc'},
  {title: '备注', width: '260px', key: 'remark', sort: 'desc'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
/* VAR*/


/* FUNCTION*/
function toReset() {
  searchQuery.value = {}
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
const changeStatus = (isChecked: boolean, row: any) => {
  dataSource.value?.forEach((item: any) => {
    if (item.id === row.id) {
      layer.msg('Success', {icon: 1}, () => {
        item.status = isChecked
      })
    }
  })
}
const remove = () => {
  layer.msg(selectedKeys.value, {area: '50%'})
}

const changeVisible11 = (text: any, row: any) => {
  title.value = text
  if (row != null) {
    let info = JSON.parse(JSON.stringify(row))
    model11.value = info
  } else {
    model11.value = {
      name: '',
      flage: '',
      remark: ''
    }
  }
  visible11.value = !visible11.value
}
const submit11 = function () {
  queryParamsFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    layer.open({
      type: 1,
      title: '表单提交结果',
      content: `<div style="padding: 10px"><p>是否通过 : ${isValidate}</p> <p>表单数据 : ${JSON.stringify(
          model
      )} </p> <p>错误信息 : ${JSON.stringify(errors)}</p></div>`,
      shade: false,
      isHtmlFragment: true,
      btn: [
        {
          text: '确认',
          callback(index: number) {
            layer.close(index)
          }
        }
      ],
      area: '500px'
    })
  })
}
// 清除校验
const clearValidate11 = function () {
  queryParamsFormRef.value.clearValidate()
}
// 重置表单
const reset11 = function () {
  queryParamsFormRef.value.reset()
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
          layer.msg('您已成功删除')
          layer.close(id)
        }
      },
      {
        text: '取消',
        callback: (id: any) => {
          layer.msg('您已取消操作')
          layer.close(id)
        }
      }
    ]
  })
}

function toSubmit() {
  layer.msg('保存成功！', {icon: 1, time: 1000})
  visible11.value = false
}

function toCancel() {
  visible11.value = false
  saveRoleMenuPrivilegeVisible.value = false
}

function confirm() {
  layer.msg('您已成功删除')
}

function cancel() {
  layer.msg('您已取消操作')
}

/* 分配权限按钮 START */
// 点击【分配权限】
async function toPrivileges(row: any) {
  selectRowRoleId.value = row.id;
  const {data, code, msg} = await findSelectedMenuListByRoleId(row.id)
  if (code === 200) {
    saveRoleMenuPrivilegeModel.value.treeSelectedIdList = data;
    saveRoleMenuPrivilegeModel.value.roleId = row.id
    saveRoleMenuPrivilegeModel.value.roleName = row.roleName
    saveRoleMenuPrivilegeModel.value.dataScope = row.dataScope
  } else {
    layer.msg(msg);
  }
  saveRoleMenuPrivilegeVisible.value = true

}

// 【分配权限】 -> 【保存】
async function toPrivilegesSubmit() {
  const {
    code,
    msg
  } = await saveRoleMenuPrivilege(saveRoleMenuPrivilegeModel.value)
  if (code === 200) {
    await userStore.getInfo();
    refresh()
  }
  layer.msg(msg, {icon: 1, time: 1000})
  console.log(saveRoleMenuPrivilegeModel.value?.treeSelectedIdList)
  saveRoleMenuPrivilegeVisible.value = false
}

// 【分配权限】 -> 【取消】
function toPrivilegesCancel() {
  saveRoleMenuPrivilegeVisible.value = false
}

/* 分配权限按钮 END */

function refresh() {
  appStore.routerAlive = false
  setTimeout(() => {
    appStore.routerAlive = true
  }, 200)
}

function isAdmin() {
  let flag = userStore.permissions.find(f => {
    return f === app.config.globalProperties.$ADMIN_PERMISSION;
  });
  return flag === app.config.globalProperties.$ADMIN_PERMISSION;
}

function assignUser() {
  if (!selectedKeys.value || selectedKeys.value.length === 0 || selectedKeys.value.length > 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  $router.push("/system/role/assignUser/" + selectedKeys.value[0])
}

function assignUserById(id: any) {
  $router.push("/system/role/assignUser/" + id)
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

.layTreeContainer {
  width: 100%;
  height: 280px;
  overflow: auto;
}

.saveRoleMenuPrivilegeLayerFooter {
  position: absolute;
  bottom: 0;
  width: 100%;
  display: inline-block;
  padding-top: 10px;
  padding-bottom: 10px;
}
</style>
