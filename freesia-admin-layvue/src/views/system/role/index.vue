<template>
  <lay-container :fluid="true">
    <lay-card>
      <lay-form style="margin-top: 10px">
        <lay-row :space="10">
          <lay-col :md="6">
            <lay-form-item label="角色名称" label-width="80">
              <lay-input
                  v-model="searchQuery.roleName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  class="width-resize"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="角色标识" label-width="80">
              <lay-input
                  v-model="searchQuery.identifying"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  class="width-resize"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="备注" label-width="80">
              <lay-input
                  v-model="searchQuery.mark"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  class="width-resize"
              ></lay-input>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <!-- table -->
    <div>
      <lay-table
          class="table-box"
          ref="dataSourceTableRef"
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
          <lay-switch :model-value="row.status === '1' || row.status"></lay-switch>
        </template>
        <template #dataScope="{ row }">
          <dict-scan :options="sysDataScope" :value="row.dataScope"/>
        </template>
        <template v-slot:toolbar>
          <lay-button size="sm" type="normal" @click="toSearch">查询</lay-button>
          <lay-button size="sm" @click="toReset">重置</lay-button>
          <lay-button size="sm" type="primary" @click="toAdd" v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_ADD]">
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove" v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_DELETE]">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
          <lay-button size="sm" type="primary" @click="assignUser"
                      v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_ASSIGN_USER_EDIT]">
            分配用户
          </lay-button>
          <lay-button size="sm" type="normal" @click="toPrivilegesSelectKeys"
                      v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_MENU_EDIT]">
            菜单权限
          </lay-button>
          <lay-button size="sm" type="warm" @click="toAssignButton()"
                      v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_ASSIGN_BUTTON_EDIT]">
            按钮权限
          </lay-button>
          <lay-button size="sm" border="orange" @click="assignDeptModalChange"
                      v-permission="[$MENU_PERMISSION.SYSTEM_DEPT_ASSIGN_ROLE]">
            分配部门
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <div class="scrollable-div">
            <lay-button
                size="xs"
                border="green"
                border-style="dashed"
                @click="toEdit(row)"
                v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_EDIT]"
            >编辑
            </lay-button
            >
            <lay-button
                size="xs"
                border="blue"
                border-style="dashed"
                @click="toPrivilegesRow(row)"
                v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_MENU_EDIT]"
            >菜单权限
            </lay-button
            >
            <lay-button
                size="xs"
                border="blue"
                border-style="dashed"
                @click="assignUserById(row.id)"
                v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_ASSIGN_USER_EDIT]"
            >分配用户
            </lay-button>
            <lay-button size="xs" border="red" border-style="dashed" @click="toRemove"
                        v-permission="[$MENU_PERMISSION.SYSTEM_ROLE_DELETE]">
              <lay-icon class="layui-icon-delete"></lay-icon>
              删除
            </lay-button>
          </div>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="addVisibleFlag" :title="title" :area="['1000px', '400px']">
      <div style="padding: 20px">
        <lay-form :model="addRoleVo" ref="addRoleFormRef" labelPosition="top">
          <lay-row space="20">
            <lay-col md="6">
              <lay-form-item label="角色名称" prop="roleName" required>
                <lay-input v-model="addRoleVo.roleName"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="角色标识" prop="roleKey" required>
                <lay-input v-model="addRoleVo.roleKey"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="数据范围" prop="dataScope" required>
                <lay-select
                    :disabled="!isAdmin()"
                    class="width-resize"
                    size="sm"
                    v-model="addRoleVo.dataScope"
                    :options="sysDataScopeSelect"
                    :items="sysDataScopeSelect"
                    :allow-clear="true"
                    placeholder="请选择"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="状态" prop="status" required>
                <lay-switch v-model="addRoleVo.status"></lay-switch>
              </lay-form-item>
            </lay-col>
          </lay-row>
          <lay-row space="20">
            <lay-col md="6">
              <lay-form-item label="排序号" prop="orderNum">
                <lay-input-number
                    class="width-resize"
                    v-model="addRoleVo.orderNum"
                    position="right"
                    :min="0"
                    :step="10"
                ></lay-input-number>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="描述" prop="remark">
                <lay-textarea
                    placeholder="请输入描述"
                    v-model="addRoleVo.remark"
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="addToSubmit"
          >保存
          </lay-button
          >
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="editVisibleFlag" :title="title" :area="['1000px', '400px']">
      <div style="padding: 20px">
        <lay-form :model="editRoleVo" ref="editRoleFormRef" labelPosition="top">
          <lay-row space="20">
            <lay-col md="6">
              <lay-form-item label="角色名称" prop="roleName" required>
                <lay-input v-model="editRoleVo.roleName"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="角色标识" prop="roleKey" required>
                <lay-input v-model="editRoleVo.roleKey"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="数据范围" prop="dataScope" required>
                <lay-select
                    :disabled="!isAdmin()"
                    size="sm"
                    class="width-resize"
                    v-model="editRoleVo.dataScope"
                    :options="sysDataScopeSelect"
                    :items="sysDataScopeSelect"
                    :allow-clear="true"
                    placeholder="请选择"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="状态" prop="status" required>
                <lay-switch :model-value="editRoleVo.status"></lay-switch>
              </lay-form-item>
            </lay-col>
          </lay-row>
          <lay-row space="20">
            <lay-col md="6">
              <lay-form-item label="排序号" prop="orderNum">
                <lay-input-number
                    class="width-resize"
                    v-model="editRoleVo.orderNum"
                    position="right"
                    :min="0"
                    :step="10"
                ></lay-input-number>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="描述" prop="remark">
                <lay-textarea
                    placeholder="请输入描述"
                    v-model="editRoleVo.remark"
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="editToSubmit"
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
                size="sm"
                class="width-resize"
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
                  :showCheckbox="menuTreeShowCheckbox"
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

  <lay-layer v-model="changeAssignDeptModalFlag" title="可分配的部门" :area="['600px', '500px']">
    <lay-button size="sm" type="primary" @click="assign" style="margin: 20px">
      <lay-icon class="layui-icon-addition"></lay-icon>
      分配
    </lay-button>
    <lay-tree
        class="layTreeContainer"
        :tail-node-icon="true"
        :data="deptTreeSelect"
        :default-expand-all="true"
        :showCheckbox="true"
        v-model:checkedKeys="assignDeptVo.deptIdList"
        :onlyIconControl="true"
    >
    </lay-tree>
  </lay-layer>
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
import {defineComponent, onMounted, reactive, ref, watch} from 'vue'
import {layer} from '@layui/layui-vue'
import {
  SaveRoleVo,
  AssignDeptVo,
  FindPageSysRoleListEntity,
  SaveRoleMenuPrivilegeVo,
  SysRoleVo
} from "../../../types/system/Role";
import {
  assignDept, deleteRole,
  findDeptRolesByRoleId,
  findPageSysRoleList,
  saveRole,
  saveRoleMenuPrivilege
} from "../../../api/system/Role";
import {PageQuery} from "../../../types/Common";
import {FindAllMenuTreeEntity} from "../../../types/system/Menu";
import {findAllMenuTree, findSelectedMenuListByRoleId} from "../../../api/system/Menu";
import {Constants, loadSysDictValue, sysDictValueSelect} from "../../../util/UDict";
import {SysDictValueEntity} from "../../../types/system/Dict";
import {useAppStore} from "../../../store/app";
import {useUserStore} from "../../../store/user";
import app from "../../../main";
import router from "../../../router";
import {SysDeptSelectEntity} from "../../../types/system/Dept";
import {findTreeAssignDeptSelect} from "../../../api/system/Dept";
/* INIT*/
const $ADMIN_ROLE = app.config.globalProperties.$ADMIN_ROLE
onMounted(async () => {
  sysDataScope.value = await loadSysDictValue(Constants.SYS_DATA_SCOPE)
  sysDataScopeSelect.value = await sysDictValueSelect(sysDataScope.value);
  await loadDataSource()
  await loadAllMenuTree()
  findTreeAssignDeptSelect().then((res: any) => {
    if (res.code === 200) {
      deptTreeSelect.value = res.data;
    }
  })
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
const selectedKeys = ref([])
const checkedKeys = ref([])
const pageQuery = reactive<PageQuery>({current: 1, limit: 10})
const sysDataScope = ref<Array<SysDictValueEntity>>([]);
const sysDataScopeSelect = ref<Array<SysDictValueEntity>>([]);

const addRoleVo = ref<SaveRoleVo>({});
const editRoleVo = ref<SaveRoleVo>({});
const saveRoleMenuPrivilegeModel = ref<SaveRoleMenuPrivilegeVo>({});
const addRoleFormRef = ref()
const editRoleFormRef = ref()
const saveRoleMenuPrivilegeFormRef = ref()
const dataSourceTableRef = ref()
const visible11 = ref(false)
const saveRoleMenuPrivilegeVisible = ref(false)
const menuTreeShowCheckbox = ref(true)
const menuTree = ref<Array<FindAllMenuTreeEntity>>()
const selectRowRoleId = ref<string>("");
const title = ref('新增')
const dataSource = ref<Array<FindPageSysRoleListEntity>>()
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
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
const assignDeptVo = ref<AssignDeptVo>({});
const changeAssignDeptModalFlag = ref(false)
const deptTreeSelect = ref<SysDeptSelectEntity>({})
const addVisibleFlag = ref(false)
const editVisibleFlag = ref(false)
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
    addRoleVo.value = info
  } else {
    addRoleVo.value = {
      name: '',
      flage: '',
      remark: ''
    }
  }
  visible11.value = !visible11.value
}
const submit11 = function () {
  addRoleFormRef.value.validate((isValidate: any, model: any, errors: any) => {
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
  addRoleFormRef.value.clearValidate()
}
// 重置表单
const reset11 = function () {
  addRoleFormRef.value.reset()
}

function toRemove() {
  if (!selectedKeys.value || selectedKeys.value.length === 0 || selectedKeys.value.length > 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  layer.confirm('确定删除吗？', {
    title: '提示',
    btn: [
      {
        text: '确定',
        callback: (id: any) => {
          deleteRole({id: selectedKeys.value[0]}).then((res: any) => {
            if (res.code === 200) {
              layer.msg(res.msg, {icon: 1})
              toCancel();
              loadDataSource()
            } else {
              layer.msg(res.msg, {icon: 3})
            }
          })
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

function addToSubmit() {
  addRoleFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveRole(addRoleVo.value).then((res: any) => {
        if (res.code === 200) {
          layer.msg(res.msg, {icon: 1})
          addRoleVo.value = {}
          toCancel();
          loadDataSource()
        } else {
          layer.msg(res.msg, {icon: 3})
        }
      }).catch(e => {
        layer.confirm(e.msg, {icon: 2})
      })
    }
  })
}

function editToSubmit() {
  editRoleFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveRole(editRoleVo.value).then((res: any) => {
        if (res.code === 200) {
          layer.msg(res.msg, {icon: 1})
          editRoleVo.value = {}
          toCancel();
          loadDataSource()
        } else {
          layer.msg(res.msg, {icon: 3})
        }
      }).catch(e => {
        layer.confirm(e.msg, {icon: 2})
      })
    }
  })
}

function toCancel() {
  addVisibleFlag.value = false
  editVisibleFlag.value = false
  saveRoleMenuPrivilegeVisible.value = false
}

function confirm(row: any) {
  console.log(row)
  layer.msg('您已成功删除')
}

function cancel() {
  layer.msg('您已取消操作')
}

/* 分配权限按钮 START */
// 行操作【分配权限】
async function toPrivilegesRow(row: any) {
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

// 按钮操作【分配权限】
async function toPrivilegesSelectKeys() {
  let selectKeys = selectedKeys.value
  if (selectKeys.length !== 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  let row = dataSourceTableRef.value.getCheckData()[0];
  selectRowRoleId.value = selectKeys[0];
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

function toAssignButton() {
  let selectKeys = selectedKeys.value
  if (selectKeys.length !== 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  let row = dataSourceTableRef.value.getCheckData()[0]
  selectRowRoleId.value = selectKeys[0];
  if (row.roleKey === $ADMIN_ROLE) {
    layer.msg("超级管理员无需分配按钮权限", {icon: 3})
    return;
  }
  $router.push('/system/role/assignButton/' + row.id);
}

function assignDeptModalChange() {
  if (!selectedKeys.value || selectedKeys.value.length === 0) {
    layer.msg("请选择数据", {icon: 3})
    return;
  }
  if (selectedKeys.value.length > 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  let row = dataSourceTableRef.value.getCheckData()[0]
  if (row.roleKey === $ADMIN_ROLE) {
    layer.msg("超级管理员无需分配部门权限", {icon: 3})
    return;
  }
  findDeptRolesByRoleId(row.id).then((res: any) => {
    if (res.code === 200) {
      assignDeptVo.value.deptIdList = res.data.selectedDept;
      checkedKeys.value = res.data.selectedDept;
    }
  })
  changeAssignDeptModalFlag.value = !changeAssignDeptModalFlag.value
}

function assign() {
  let row = dataSourceTableRef.value.getCheckData()[0];
  assignDeptVo.value.roleId = row.id;
  assignDept(assignDeptVo.value).then((res: any) => {
    if (res.code === 200) {
      change()
      assignDeptModalChange()
      assignDeptVo.value = {}
    }
  })
}

function toAdd() {
  addRoleVo.value.status = true;
  addRoleVo.value.orderNum = Math.floor(Math.max(...dataSource.value.map(obj => obj.orderNum))) + 10
  addVisibleFlag.value = true
}

function toEdit(row: any) {
  editRoleVo.value = row;
  editRoleVo.value.status = row.status === '1'
  editVisibleFlag.value = true
}

/* FUNCTION*/

</script>

<style scoped>
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

.scrollable-div {
  display: flex;
  overflow-x: auto;
}
</style>
