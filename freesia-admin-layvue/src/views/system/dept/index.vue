<!--suppress ES6UnusedImports -->
<template>
  <lay-container fluid="true" class="organization-box">
    <div style="display: flex">
      <div :style="{ width: isFold ? `0px` : `300px` }" class="left-tree">
        <!-- tree -->
        <div v-show="!isFold">
          <lay-row>
            <lay-col md="6">
              <lay-button type="normal" size="sm" @click="toAdd"
                          v-permission="[$MENU_PERMISSION.SYSTEM_DEPT_ADD]">
                <lay-icon type="layui-icon-addition"></lay-icon>
                新建
              </lay-button>
            </lay-col>
            <lay-col md="6">
              <lay-button type="warm" size="sm" @click="toEdit"
                          v-permission="[$MENU_PERMISSION.SYSTEM_DEPT_EDIT]">
                <lay-icon type="layui-icon-edit"></lay-icon>
                修改
              </lay-button>
            </lay-col>
            <lay-col md="6">
              <lay-button type="danger" size="sm" @click="toDelete"
                          v-permission="[$MENU_PERMISSION.SYSTEM_DEPT_DELETE]">
                <lay-icon type="layui-icon-delete"></lay-icon>
                删除
              </lay-button>
            </lay-col>
            <lay-col md="6">
              <lay-button type="normal" size="sm" @click="assignRole"
                          v-permission="[$MENU_PERMISSION.SYSTEM_DEPT_ASSIGN_ROLE]">
                <lay-icon type="layui-icon-delete"></lay-icon>
                分配角色
              </lay-button>
            </lay-col>
          </lay-row>
        </div>
        <lay-tree
            v-show="!isFold"
            style="margin-top: 10px"
            :data="deptTree"
            v-model:selectedKey="selectedKey"
            :tail-node-icon="tailNodeIcon"
            :showLine="showLine"
            :onlyIconControl="true"
            :expandKeys="expandKeys"
            :defaultExpandAll="true"
            @node-click="handleClick"
        >
          <template #title="{ data }">
            <span :class="selectedKey == data.id ? 'isChecked' : ''">
              {{ data.deptName }}
            </span>
          </template>
        </lay-tree>
        <div class="isFold" @click="isFold = !isFold">
          &nbsp;<lay-icon v-if="!isFold" class="layui-icon-left"></lay-icon>
          <lay-icon v-else class="layui-icon-right"></lay-icon>
        </div>
      </div>
      <div style="flex: 1; padding: 10px; overflow: visible">
        <!-- table -->
        <lay-card shadow="hover">
          <lay-form label-position="top">
            <lay-row :space="20">
              <lay-col :md="6">
                <lay-form-item label="用户账号" label-width="80">
                  <lay-input
                      v-model="searchQuery.userAccount"
                      placeholder="请输入"
                      size="sm"
                      :allow-clear="true"
                      style="width: 100%"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="用户名" label-width="80">
                  <lay-input
                      v-model="searchQuery.userName"
                      placeholder="请输入"
                      size="sm"
                      :allow-clear="true"
                      style="width: 100%"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="性别" label-width="80">
                  <lay-select
                      style="width: 100%"
                      size="sm"
                      v-model="searchQuery.sex"
                      :allow-clear="true"
                      placeholder="请选择"
                  >
                    <lay-select-option
                        value="man"
                        label="男"
                    ></lay-select-option>
                    <lay-select-option
                        value="woman"
                        label="女"
                    ></lay-select-option>
                  </lay-select>
                </lay-form-item>
              </lay-col>
            </lay-row>
          </lay-form>
        </lay-card>
        <lay-table
            class="table-box"
            :page="pageQuery"
            :columns="columns"
            :loading="loading"
            :default-toolbar="true"
            :data-source="dataSource"
            v-model:selected-keys="selectedKeys"
            @change="change"
            @sortChange="sortChange"
        >
          <template #accountStatus="{ row }">
            <lay-switch
                :model-value="row.accountStatus === '1'"
                @change="changeStatus($event, row)"
            ></lay-switch>
          </template>
          <template #deptName="{ row }">
            <lay-tag color="#165DFF" variant="light">{{ row.deptName }}</lay-tag>
          </template>
          <template #gender="{ row }">
            <dict-tag :options="sysGenderList" :value="row.gender" :showValue="true"/>
          </template>
          <template v-slot:toolbar>
            <lay-button type="normal" size="sm" @click="toSearch">查询</lay-button>
            <lay-button size="sm" @click="toReset">重置</lay-button>
            <lay-button
                size="sm"
                type="primary"
                @click="changeVisible11('新增', null)"
            >新增
            </lay-button
            >
            <lay-button size="sm" @click="toRemove">删除</lay-button>
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
            <lay-popconfirm
                content="确定要删除此用户吗?"
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
    </div>
    <lay-layer v-model="visible11" :title="title" :area="['500px', '450px']">
      <div style="padding: 20px">
        <lay-form :model="model11" ref="addModalFormRef" required>
          <lay-form-item label="用户账号" prop="account">
            <lay-input v-model="model11.account"></lay-input>
          </lay-form-item>
          <lay-form-item label="用户名" prop="name">
            <lay-input v-model="model11.name"></lay-input>
          </lay-form-item>
          <lay-form-item label="性别" prop="sex">
            <lay-select v-model="model11.sex" style="width: 100%">
              <lay-select-option value="男" label="男"></lay-select-option>
              <lay-select-option value="女" label="女"></lay-select-option>
            </lay-select>
          </lay-form-item>
          <lay-form-item label="角色" prop="role">
            <lay-input v-model="model11.role"></lay-input>
          </lay-form-item>
          <lay-form-item label="状态" prop="status">
            <lay-switch :model-value="model11.status"></lay-switch>
          </lay-form-item>
        </lay-form>
        <div style="width: 100%; text-align: center">
          <lay-button size="sm" type="primary" @click="saveInsertDept"
          >保存
          </lay-button
          >
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="addVisibleFlag" :title="addModalTitle" :area="['1200px', '500px']">
      <div style="padding: 20px">
        <lay-form :model="addSysDeptVo" ref="addModalFormRef" label-position="top" size="md">
          <lay-row space="20">
            <lay-col md="6">
              <lay-form-item label="上级部门" prop="parentId" required>
                <!-- :onlyLastLevel="true" 不能与:changeOnSelect="true" 一起使用-->
                <lay-cascader style="width: 100%"
                              :options="deptTreeSelect"
                              v-model="addSysDeptVo.ancestors"
                              :replaceFields="replaceFields"
                              decollator=","
                              allow-clear
                              :changeOnSelect="true"
                              @change="changeAddModalParentIdSelect">
                </lay-cascader>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="部门名称" prop="deptName" required>
                <lay-input v-model="addSysDeptVo.deptName" style="width: 100%"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="负责人" prop="leader" required>
                <lay-input v-model="addSysDeptVo.leader"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="联系电话" prop="telNo">
                <lay-input v-model="addSysDeptVo.telNo"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="邮箱" prop="email">
                <lay-input v-model="addSysDeptVo.email"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="排序号" prop="orderNum">
                <lay-input-number
                    style="width: 100%"
                    v-model="addSysDeptVo.orderNum"
                    position="right"
                ></lay-input-number>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="备注" prop="remark" required>
                <lay-textarea
                    placeholder="请输入备注"
                    v-model="addSysDeptVo.remark"
                    :rows="4"
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="saveInsertDept"
          >保存
          </lay-button
          >
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="editVisibleFlag" :title="editModalTitle" :area="['1200px', '500px']">
      <div style="padding: 20px">
        <lay-form :model="editSysDeptVo" ref="editModalFormRef" label-position="top" size="md">
          <lay-row space="20">
            <lay-col md="6">
              <lay-form-item label="上级部门" prop="parentId" required>
                <!-- :onlyLastLevel="true" 不能与:changeOnSelect="true" 一起使用-->
                <lay-cascader style="width: 100%"
                              :options="deptTreeSelect"
                              v-model="editSysDeptVo.ancestors"
                              :replaceFields="replaceFields"
                              decollator=","
                              allow-clear
                              :changeOnSelect="true"
                              @change="changeEditModalParentIdSelect">
                </lay-cascader>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="部门名称" prop="deptName" required>
                <lay-input v-model="editSysDeptVo.deptName" style="width: 100%"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="负责人" prop="leader" required>
                <lay-input v-model="editSysDeptVo.leader"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="联系电话" prop="telNo">
                <lay-input v-model="editSysDeptVo.telNo"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="邮箱" prop="email">
                <lay-input v-model="editSysDeptVo.email"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="排序号" prop="orderNum">
                <lay-input-number
                    style="width: 100%"
                    v-model="editSysDeptVo.orderNum"
                    position="right"
                ></lay-input-number>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="备注" prop="remark" required>
                <lay-textarea
                    placeholder="请输入备注"
                    v-model="editSysDeptVo.remark"
                    :rows="4"
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="saveEditDept"
          >保存
          </lay-button
          >
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
  name: "Dept",
};
</script>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {
  FindPageSysDeptListEntity,
  FindPageSysUserByDeptEntity,
  SysDeptSelectEntity,
  SysDeptVo
} from "../../../types/system/Dept";
import {PageQuery} from "../../../types/Common";
import {
  deleteDept,
  findDeptTreeList,
  findIncrementOrderNum,
  findTreeDeptSelect,
  saveDept
} from "../../../api/system/Dept";
import {findPageSysUserByDept} from "../../../api/system/User";
import {SysUserVo} from "../../../types/system/User";
import {Constants, loadSysDictValue, matchDictValue} from "../../../util/UDict";
import {MatchDictValueModel, SysDictValueEntity} from "../../../types/system/Dict";
import DictTag from "../../component/DictTag.vue";
import {useCryptStore} from "../../../store/crypt";
import router from "../../../router";

/* INIT*/
const $router = router
const $crypt = useCryptStore();
onMounted(async () => {
  sysGenderList.value = await loadSysDictValue(Constants.SYS_GENDER);
  await loadData()
})
/* INIT*/

/* VAR*/
const sysGenderList = ref<Array<SysDictValueEntity>>([]);
const deptTree = ref<Array<FindPageSysDeptListEntity>>()
const deptTreeSelect = ref<SysDeptSelectEntity>({})
const showLine = ref(true)
const tailNodeIcon = ref(false)
const selectedKey = ref('')
const selectedNode = ref()
const isFold = ref(false)
const searchQuery = ref<SysUserVo>({})
const loading = ref(false)
const selectedKeys = ref()
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const dataSource = ref<Array<FindPageSysUserByDeptEntity>>()
const expandKeys = ref<any[]>()
const addSysDeptVo = ref<SysDeptVo>({})
const editSysDeptVo = ref<SysDeptVo>({})
const layFormRef22 = ref()
const addVisibleFlag = ref(false)
const editVisibleFlag = ref(false)
const addModalTitle = ref('新建')
const editModalTitle = ref('编辑')
const model11 = ref({
  name: '',
  role: '',
  sex: '',
  status: '',
  account: ''
})
const addModalFormRef = ref()
const editModalFormRef = ref()
const visible11 = ref(false)
const title = ref('新增')
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '用户名', width: '80px', key: 'userName', sort: 'userName'},
  {title: '用户昵称', width: '80px', key: 'nickName', sort: 'nickName'},
  {title: '性别', width: '80px', key: 'gender', sort: 'gender', customSlot: 'gender'},
  {title: '部门名称', width: '120px', key: 'deptName', customSlot: 'deptName'},
  {title: '创建时间', width: '120px', key: 'createTime'},
  {title: '状态', width: '80px', key: 'accountStatus', sort: 'accountStatus', customSlot: 'accountStatus'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const replaceFields = {
  label: 'title',
  value: 'id',
  children: 'children'
}
/* VAR*/

/* FUNCTION*/
const loadData = async () => {
  findDeptTreeList(searchQuery.value, pageQuery).then((res: any) => {
    if (res.code === 200) {
      deptTree.value = res.data;
      // 递归树结构默认展开所有
      recursionTree(res.data);
    }
  })
  findTreeDeptSelect().then((res: any) => {
    if (res.code === 200) {
      deptTreeSelect.value = res.data;
    }
  })
}

function recursionTree(data: any) {
  if (data) {
    data.forEach((f: { id: string, children: Array<any> }) => {
      if (f.children && f.children.length > 0) {
        recursionTree(f.children)
      }
    })
  }
}

function toReset() {
  searchQuery.value = {}
}

function handleClick(node: any) {
  selectedNode.value = JSON.parse(JSON.stringify(node))
  searchQuery.value.deptId = selectedNode.value.id
  editSysDeptVo.value = selectedNode.value
  dataSource.value = []
  change()
}

function toAdd() {
  addVisibleFlag.value = true
}

function toEdit() {
  if (Object.keys(editSysDeptVo.value).length === 0) {
    layer.msg("请选择部门！", {icon: 3});
    return;
  }
  editVisibleFlag.value = true
}

function toDelete() {
  if (!selectedKey.value || selectedKey.value === '') {
    layer.msg('请选择要删除的部门', {
      icon: 3,
      time: 2000
    })
    return;
  }
  layer.confirm(
      '您将删除所选中的部门【' + selectedNode.value.deptName + '】？',
      {
        title: '提示',
        btn: [
          {
            text: '确定',
            callback: (id: any) => {
              deleteDept(selectedNode.value.id).then((res: any) => {
                if (res.code == 200) {
                  loadData();
                  loadDataSource()
                }
              });
              layer.msg('您已成功删除')
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
      }
  )
}

function toSearch() {
  pageQuery.current = 1
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
  dataSource.value?.forEach((item) => {
    if (item.id === row.id) {
      layer.msg('Success', {icon: 1}, () => {
        item.accountStatus = isChecked ? '1' : '0'
      })
    }
  })
}
const remove = () => {
  layer.msg(selectedKeys.value, {area: '50%'})
}
const loadDataSource = async () => {
  const {rows, msg, code, total} = await findPageSysUserByDept(searchQuery.value, pageQuery);
  if (code == 200) {
    pageQuery.total = total;
    dataSource.value = rows;
  } else {
    layer.msg(msg)
    return;
  }
  return rows
}

const changeVisible11 = (text: any, row: any) => {
  title.value = text
  if (row != null) {
    let info = JSON.parse(JSON.stringify(row))
    model11.value = info
  } else {
    model11.value = {
      name: '',
      role: '',
      sex: '',
      status: '',
      account: ''
    }
  }
  visible11.value = !visible11.value
}
const submit11 = function () {
  addModalFormRef.value.validate((isValidate: any, model: any, errors: any) => {
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
          callback(index: any) {
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
  addModalFormRef.value.clearValidate()
}
// 重置表单
const reset11 = function () {
  addModalFormRef.value.reset()
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

function saveInsertDept() {
  $crypt.encryptAes(addSysDeptVo.value).then(encrypt => {
    saveDept(encrypt).then((decrypt: any) => {
      if (decrypt.code === 200) {
        layer.msg(decrypt.msg, {icon: 1})
        addVisibleFlag.value = false
        addSysDeptVo.value = {}
        loadData()
      }
    })
  })
}

function saveEditDept() {
  $crypt.encryptAes(editSysDeptVo.value).then(encrypt => {
    saveDept(encrypt).then((decrypt: any) => {
      if (decrypt.code === 200) {
        layer.msg(decrypt.msg, {icon: 1})
        editVisibleFlag.value = false
        editSysDeptVo.value = {}
        loadData()
      }
    })
  })

}

function toCancel() {
  visible11.value = false
  addVisibleFlag.value = false
  editVisibleFlag.value = false
}

function confirm() {
  layer.msg('您已成功删除')
}

function cancel() {
  layer.msg('您已取消操作')
}

function changeAddModalParentIdSelect(value: any) {
  addSysDeptVo.value.ancestors = value.value;
  addSysDeptVo.value.parentId = value.currentClick.id;
  console.log(value);
  findIncrementOrderNum(value.currentClick.id).then((res: any) => {
    if (res.code === 200) {
      addSysDeptVo.value.orderNum = res.data
    }
  })
}

function changeEditModalParentIdSelect(value: any) {
  console.log(value);
  editSysDeptVo.value.ancestors = value.value;
  editSysDeptVo.value.parentId = value.currentClick.id;
}

function assignRole() {
  if (!selectedNode.value || Object.keys(selectedNode.value).length === 0) {
    layer.msg("请选择1个部门", {icon: 3})
    return;
  }
  $router.push("/system/dept/assignRole/" + selectedNode.value.id)
}

function assignRoleById(id: any) {
  $router.push("/system/dept/assignRole/" + id)
}

/* FUNCTION*/

</script>

<style scoped>
.organization-box {
  width: calc(100vw - 240px);
  height: calc(100vh - 110px);
  margin-top: 10px;
  box-sizing: border-box;
  background-color: #fff;
  overflow: hidden;
}

.left-tree {
  display: inline-block;
  padding: 20px 15px 0 5px;
  height: 1200px;
  border-right: 1px solid #e6e6e6;
  box-sizing: border-box;
  position: relative;
}

/* todo layui-tree-entry 设置无效 */
.layui-tree-entry {
  position: relative;
  padding: 10px 0;
  height: 20px;
  line-height: 20px;
  white-space: nowrap;
}

.isFold {
  position: absolute;
  top: 36%;
  right: -10px;
  width: 26px;
  height: 26px;
  line-height: 26px;
  border-radius: 13px;
  background-color: #fff;
  border: 1px solid #e6e6e6;
  cursor: pointer;
}
</style>
