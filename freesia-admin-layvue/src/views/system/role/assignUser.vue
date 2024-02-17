<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: auto">
      <lay-container fluid="true" class="user-box">
        <lay-form style="margin-top: 10px">
          <lay-card title="角色信息">
            <lay-row>
              <lay-col :md="6">
                <lay-form-item label="角色ID" label-width="80">
                  <lay-input
                      v-model="sysRoleEntity.id"
                      size="sm"
                      style="width: 98%"
                      :disabled="true"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="角色权限字符串" label-width="200">
                  <lay-input
                      v-model="sysRoleEntity.roleKey"
                      size="sm"
                      style="width: 98%"
                      :disabled="true"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="数据范围" label-width="200">
                  <lay-input
                      v-model="sysRoleEntity.dataScope"
                      size="sm"
                      style="width: 98%"
                      :disabled="true"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
            </lay-row>
            <lay-row>
              <lay-col :md="6">
                <lay-form-item label="角色名称" label-width="80">
                  <lay-input
                      v-model="sysRoleEntity.roleName"
                      size="sm"
                      style="width: 98%"
                      :disabled="true"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="状态" label-width="200">
                  <lay-input
                      v-model="roleStatus"
                      size="sm"
                      style="width: 98%"
                      :disabled="true"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="6">
                <lay-form-item label="备注" label-width="200">
                  <lay-input
                      v-model="sysRoleEntity.remark"
                      size="sm"
                      style="width: 98%"
                      :disabled="true"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
            </lay-row>
          </lay-card>
        </lay-form>
        <lay-card title="已分配角色用户信息">
          <!-- table -->
          <lay-table
              class="table-style"
              :page="pageQuery"
              :columns="columns"
              :loading="loading"
              :data-source="userEntityList"
              v-model:selected-keys="selectedKeys"
              @change="change"
          >
            <template #accountStatus="{ row }">
              <div v-show="row.accountStatus === '0'">
                <lay-tag color="#2dc570" variant="light">启用</lay-tag>
              </div>
              <div v-show="row.accountStatus === '1'">
                <lay-tag color="#F5319D" variant="light">禁用</lay-tag>
              </div>
            </template>
            <template #remark="{ row }">
              <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
                <div class="oneRow">{{ row.remark }}</div>
              </lay-tooltip>
            </template>
            <template v-slot:toolbar>
              <lay-button size="sm" type="normal" @click="assignUserModalChange">
                <lay-icon class="layui-icon-addition"></lay-icon>
                分配用户
              </lay-button>
              <lay-button size="sm" type="danger" @click="cancelAssign">
                <lay-icon class="layui-icon-subtraction"></lay-icon>
                取消分配
              </lay-button>
            </template>
          </lay-table>
        </lay-card>
      </lay-container>
    </div>
    <lay-layer v-model="openAssignUserModalFlag" title="已分配角色的用户" :area="['1000px', '500px']">
      <!-- table -->
      <lay-table
          class="table-style"
          :page="assignUserModalPageQuery"
          :columns="assignUserModalColumns"
          :loading="assignUserModalLoading"
          :data-source="assignUserModalEntityList"
          v-model:selected-keys="assignUserModalSelectedKeys"
          @change="assignUserModalChange"
      >
        <template #accountStatus="{ row }">
          <div v-show="row.accountStatus === '0'">
            <lay-tag color="#2dc570" variant="light">启用</lay-tag>
          </div>
          <div v-show="row.accountStatus === '1'">
            <lay-tag color="#F5319D" variant="light">禁用</lay-tag>
          </div>
        </template>
        <template #remark="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
            <div class="oneRow">{{ row.remark }}</div>
          </lay-tooltip>
        </template>
        <template v-slot:toolbar>
          <lay-button size="sm" type="primary" @click="assign">
            <lay-icon class="layui-icon-addition"></lay-icon>
            分配
          </lay-button>
        </template>
      </lay-table>
    </lay-layer>

    <div class="footer">
      <div class="footer-button">
        <lay-button type="primary" @click="$tab.closeOpen('/system/role/index')">返回</lay-button>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "AssignUser",
};
</script>
<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue'
import {PageQuery} from "../../../types/Common";
import {SysUserEntity, SysUserVo} from "../../../types/system/User";
import {SysDictValueEntity} from "../../../types/system/Dict";
import {useRoute} from "vue-router";
import {AssignUserVo, SysRoleEntity} from "../../../types/system/Role";
import {useTabStore} from "../../../layouts/composable/useTabStore";
import {
  assignUser,
  cancelAssignUser,
  findPageAllowAssignUserByRoleId,
  findPageUserByRoleId,
  findRoleById
} from "../../../api/system/Role";
import {layer} from "@layui/layui-vue";
import router from "../../../router";

/* INIT*/
const $route = useRoute();
const $router = router;
const $tab = useTabStore();
onMounted(async () => {
  // sysDataScopeList.value = await loadSysDictValue(Constants.SYS_DATA_SCOPE)
  roleId.value = $route.params && $route.params.roleId as string;
  loadFindRoleById(roleId.value);
  change()
})
let roleStatus = computed(() => {
  return sysRoleEntity.value.status === '0' ? '启用' : '禁用'
})
/* INIT*/
/* VAR*/
const roleId = ref<string>('');
const assignRoleVo = ref<SysUserVo>({})
const title = ref('新增')
const sysDataScopeList = ref<Array<SysDictValueEntity>>([])
const dataSource = ref<Array<SysRoleEntity>>([]);
const sysRoleEntity = ref<SysRoleEntity>({});
const userEntityList = ref<Array<SysUserEntity>>();
const assignUserModalEntityList = ref<Array<SysUserEntity>>();
const loading = ref(false)
const assignUserModalLoading = ref(false)
const selectedKeys = ref([])
const assignUserModalSelectedKeys = ref([])
const openAssignUserModalFlag = ref<boolean>(false)
const pageQuery: PageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const assignUserModalPageQuery: PageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', type: 'checkbox', fixed: 'left'},
  {title: 'ID', key: 'id'},
  {title: '用户名称', key: 'userName'},
  {title: '用户昵称', key: 'nickName'},
  {title: '用户类型', key: 'userType'},
  {title: '状态', key: 'accountStatus', customSlot: 'accountStatus'},
  {title: '备注', key: 'remark', customSlot: 'remark'},
  {
    title: '操作',
    width: '120px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const assignUserModalColumns = ref([
  {title: '选项', type: 'checkbox', fixed: 'left'},
  {title: 'ID', key: 'id'},
  {title: '用户名称', key: 'userName'},
  {title: '用户昵称', key: 'nickName'},
  {title: '用户类型', key: 'userType'},
  {title: '状态', key: 'accountStatus', customSlot: 'accountStatus'},
  {title: '备注', key: 'remark', customSlot: 'remark'},
])

/* VAR*/

function toReset() {
  assignRoleVo.value = {}
}

const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 1000)
}
const loadFindRoleById = (roleId: any) => {
  findRoleById(roleId).then((res: any) => {
    if (res.code == 200) {
      sysRoleEntity.value = res.data
    }
  })
}

const loadDataSource = () => {
  findPageUserByRoleId({id: roleId.value}, pageQuery).then((res: any) => {
    if (res.code == 200) {
      userEntityList.value = res.rows;
      pageQuery.total = res.total;
    }
  })
}

function assign() {
  if (assignUserModalSelectedKeys.value.length < 1) {
    layer.msg("请选择数据", {icon: 3})
    return;
  }
  assignUser({
    roleId: roleId.value,
    userIdList: assignUserModalSelectedKeys.value
  }).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg);
      openAssignUserModalFlag.value = false
      pageQuery.current = 1;
      change();
    } else {
      layer.confirm(res.msg, {icon: 2})
    }
  })
}

function cancelAssign() {
  if (selectedKeys.value.length < 1) {
    layer.msg("请选择数据", {icon: 3})
    return;
  }
  layer.confirm('确定取消分配该用户的角色吗？', {
    btn: [
      {
        text: '确定',
        callback: () => {
          cancelAssignUser({
            roleId: roleId.value,
            userIdList: selectedKeys.value
          }).then((res: any) => {
            if (res.code == 200) {
              layer.msg(res.msg);
              pageQuery.current = 1;
              change();
            } else {
              layer.confirm(res.msg, {icon: 2})
            }
          })
          layer.closeAll()
        }
      },
      {
        text: '取消',
        callback: () => {
          layer.closeAll()
        }
      }
    ]
  })

}

function openAssignUserModal() {
  openAssignUserModalFlag.value = !openAssignUserModalFlag.value
}

function loadAssignUserModalDataSource() {
  findPageAllowAssignUserByRoleId({id: roleId.value}, assignUserModalPageQuery).then((res: any) => {
    if (res.code == 200) {
      assignUserModalEntityList.value = res.rows;
      assignUserModalPageQuery.total = res.total;
    }
  })
}

function assignUserModalChange() {
  assignUserModalLoading.value = true
  openAssignUserModal()
  setTimeout(() => {
    loadAssignUserModalDataSource();
    assignUserModalLoading.value = false
  }, 1000)
}
</script>

<style scoped>
.user-box {
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

.oneRow {
  width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.footer {
  width: 100%;
  display: flex;
  align-items: center;
  padding: 10px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-top: 1px solid whitesmoke;
  line-height: 60px;
  height: 60px;
}

.footer-button {
  right: 50px;
  position: absolute;
}
</style>
