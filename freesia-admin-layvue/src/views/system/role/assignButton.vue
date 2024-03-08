<!--suppress ES6UnusedImports -->
<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: visible">
      <lay-container fluid="true" class="organization-box">
        <div style="display: flex">
          <div :style="{ width: isFold ? `0px` : `300px` }" class="left-tree">
            <!-- tree -->
            <!--            <div v-show="!isFold">-->
            <!--              <lay-button type="primary" size="sm" @click="expand">-->
            <!--                <lay-icon type="layui-icon-addition"></lay-icon>-->
            <!--                {{ defaultExpandAllFlag ? '收起全部':'全部展开'}}-->
            <!--              </lay-button>-->
            <!--            </div>-->
            <!--thumbColor="#000000"-->
            <lay-scroll style="margin-top: 10px; height: 60% ;background-color: whitesmoke">
              <lay-tree
                  v-show="!isFold"
                  :data="menuTree"
                  v-model:selectedKey="selectedKey"
                  :tail-node-icon="tailNodeIcon"
                  :showLine="showLine"
                  :onlyIconControl="onlyIconControlFlag"
                  :expandKeys="expandKeys"
                  :collapse-transition="collapseTransitionFlag"
                  :default-expand-all="defaultExpandAllFlag"
                  @node-click="handleClick"
              >
                <template #title="{ data }">
                  <span :class="selectedKey == data.id ? 'isChecked' : ''">
                    {{ data.menuName }}
                  </span>
                </template>
              </lay-tree>
            </lay-scroll>
            <div class="isFold" @click="isFold = !isFold">
              &nbsp;<lay-icon v-if="!isFold" class="layui-icon-left"></lay-icon>
              <lay-icon v-else class="layui-icon-right"></lay-icon>
            </div>
          </div>
          <div style="flex: 1; padding: 10px; overflow: visible">
            <!-- table -->
            <lay-card>
              <lay-form @keyup.enter.prevent="toSearch">
                <lay-row>
                  <lay-col :md="6">
                    <lay-form-item label="角色编码" label-width="80">
                      <lay-input
                          v-model="sysRoleEntity.roleKey"
                          size="sm"
                          :allow-clear="true"
                          style="width: 98%"
                          :disabled="true"
                      ></lay-input>
                    </lay-form-item>
                  </lay-col>
                  <lay-col :md="6">
                    <lay-form-item label="角色名称" label-width="80">
                      <lay-input
                          v-model="sysRoleEntity.roleName"
                          size="sm"
                          :allow-clear="true"
                          style="width: 98%"
                          :disabled="true"
                      ></lay-input>
                    </lay-form-item>
                  </lay-col>
                </lay-row>
                <lay-row v-show="searchQuery.id">
                  <lay-col :md="6">
                    <lay-form-item label="菜单名称" label-width="80">
                      <lay-input
                          v-model="searchQuery.menuName"
                          placeholder="请输入"
                          size="sm"
                          :allow-clear="true"
                          style="width: 98%"
                      ></lay-input>
                    </lay-form-item>
                  </lay-col>
                  <lay-col :md="6">
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
            <lay-table
                class="table-style"
                :columns="columns"
                :loading="loading"
                :default-toolbar="true"
                :data-source="dataSource"
                v-model:selected-keys="selectedKeys"
                @change="change"
                @sortChange="sortChange"
            >
              <template #menuName="{ row }">
                {{ row.menuName }}
              </template>
              <template #perms="{ row }">
                <lay-tooltip
                    :visible="false"
                    trigger="hover"
                    :content="row.perms"
                >
                  <div>{{ row.perms }}</div>
                </lay-tooltip>
              </template>
              <template #menuType="{ row }">
                <div>
                  <dict-tag :options="sysMenuTypeList" :value="row.menuType"/>
                </div>
              </template>
              <template #status="{ row }">
                <div v-show="row.status === '1'">
                  <lay-tag color="#2dc570" variant="light">是</lay-tag>
                </div>
                <div v-show="row.status === '0'">
                  <lay-tag color="#F5319D" variant="light">否</lay-tag>
                </div>
              </template>
              <template #remark="{ row }">
                <lay-tooltip
                    :visible="false"
                    trigger="hover"
                    :content="row.remark"
                >
                  <div>{{ row.remark }}</div>
                </lay-tooltip>
              </template>
              <template v-slot:toolbar>
                <lay-button
                    size="sm"
                    type="primary"
                    @click="assignButtonHandler"
                >分配
                </lay-button>
              </template>
            </lay-table>
          </div>
        </div>
      </lay-container>
      <div class="footer">
        <div class="footer-button">
          <lay-button type="primary" @click="$tab.closeOpen('/system/role/index')">返回</lay-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "AssignButton",
};
</script>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {FindPageSysDeptListEntity, FindPageSysUserByDeptEntity} from "../../../types/system/Dept";
import {PageQuery} from "../../../types/Common";
import {findPageSysUserByDept} from "../../../api/system/User";
import {SysUserVo} from "../../../types/system/User";
import {Constants, loadSysDictValue, matchDictValue} from "../../../util/UDict";
import {MatchDictValueModel, SysDictValueEntity} from "../../../types/system/Dict";
import DictTag from "../../component/DictTag.vue";
import {useRoute} from "vue-router";
import {assignButton, findAllMenuTree, findAllSysButton, findAssignedSysButtonByRoleId} from "../../../api/system/Menu";
import {AssignButtonVo, SysMenuEntity, SysMenuVo} from "../../../types/system/Menu";
import {role} from "../../../directives/permission";
import {findRoleById} from "../../../api/system/Role";
import {SysRoleEntity} from "../../../types/system/Role";
import {useTabStore} from "../../../layouts/composable/useTabStore";
/* INIT*/
onMounted(async () => {
  sysMenuTypeList.value = await loadSysDictValue(Constants.SYS_MENU_TYPE);
  roleId.value = $route.params && $route.params.roleId as string;
  await loadData()
})
/* INIT*/

/* VAR*/
const $route = useRoute();
const $tab = useTabStore();
const roleId = ref<string>('');
const sysMenuTypeList = ref<Array<SysDictValueEntity>>([]);
const menuTree = ref<Array<FindPageSysDeptListEntity>>()
const defaultExpandAllFlag = ref(true);
const onlyIconControlFlag = ref(true);
const collapseTransitionFlag = ref(true);
const showLine = ref(true)
const tailNodeIcon = ref(false)
const selectedKey = ref('')
const selectedNode = ref({})
const isFold = ref(false)
const searchQuery = ref<SysMenuVo>({})
const loading = ref(false)
const selectedKeys = ref()
const beforeAssignButtonIdList = ref()
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const expandKeys = ref<string[]>([])
const dataSource = ref<Array<SysMenuEntity>>()
const sysRoleEntity = ref<SysRoleEntity>({});
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '菜单名称', key: 'menuName', width: '200px', customSlot: 'menuName'},
  {title: '排序', key: 'orderNum', width: '50px'},
  {title: '菜单类型', key: 'menuType', width: '100px', customSlot: 'menuType'},
  {title: '启用', key: 'status', width: '40px', customSlot: 'status'},
  {title: '权限标识', key: 'perms', width: '200px', customSlot: 'perms'},
  {title: '备注', key: 'remark', width: '200px', customSlot: 'remark'},
])
/* VAR*/

/* FUNCTION*/
function loadData() {
  findAllMenuTree().then((res: any) => {
    if (res.code === 200) {
      menuTree.value = res.data;
      recursionTree(res.data);
    }
  })
  findRoleById(roleId.value).then((res: any) => {
    if (res.code == 200) {
      sysRoleEntity.value = res.data
    }
  })
}

function recursionTree(data: any) {
  if (data) {
    data.forEach((f: { id: string, children: Array<any> }) => {
      // 注释该行代码，否则子页都将展开
      // expandKeys.value.push(f.id)
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
  // 点击左侧菜单树时重置右侧表格中的查询条件
  searchQuery.value = {}
  searchQuery.value.id = selectedNode.value.id
  change()
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
const loadDataSource = () => {
  findAllSysButton(searchQuery.value).then((res: any) => {
    if (res.code == 200) {
      dataSource.value = res.data;
    }
  })
  findAssignedSysButtonByRoleId(searchQuery.value, roleId.value).then((res: any) => {
    if (res.code === 200) {
      beforeAssignButtonIdList.value = res.data;
      selectedKeys.value = res.data
    }
  })
}

function expand() {
  defaultExpandAllFlag.value = !defaultExpandAllFlag.value;
}

function assignButtonHandler() {
  let assignButtonVo: AssignButtonVo = {
    roleId: roleId.value,
    beforeAssignButtonIdList: beforeAssignButtonIdList.value,
    assignButtonIdList: selectedKeys.value
  }
  assignButton(assignButtonVo).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg, {icon: 1})
      change()
    }
  })
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
