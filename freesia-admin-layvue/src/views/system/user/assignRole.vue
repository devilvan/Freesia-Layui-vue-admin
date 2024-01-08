<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: auto">
      <lay-container fluid="true" class="user-box">
        <lay-card>
          <lay-form style="margin-top: 10px">
            <lay-card title="用户信息">
              <lay-row>
                <lay-col :md="8">
                  <lay-form-item label="用户ID" label-width="80">
                    <lay-input
                        v-model="findUserRolesByUserIdEntity.userId"
                        size="sm"
                        style="width: 98%"
                        :disabled="true"
                    ></lay-input>
                  </lay-form-item>
                </lay-col>
                <lay-col :md="8">
                  <lay-form-item label="用户名" label-width="80">
                    <lay-input
                        v-model="findUserRolesByUserIdEntity.userName"
                        size="sm"
                        style="width: 98%"
                        :disabled="true"
                    ></lay-input>
                  </lay-form-item>
                </lay-col>
              </lay-row>
            </lay-card>
          </lay-form>
        </lay-card>
        <lay-card title="角色信息">
          <!-- table -->
          <lay-table
              class="table-style"
              :page="pageQuery"
              :columns="columns"
              :loading="loading"
              :data-source="dataSource"
              v-model:selected-keys="selectedKeys"
          >
            <template #dataScope="{ row }">
              <dict-scan :options="sysDataScopeList" :value="row.dataScope"/>
            </template>
            <template #status="{ row }">
              <div v-show="row.status === '0'">
                <lay-tag color="#2dc570" variant="light">启用</lay-tag>
              </div>
              <div v-show="row.status === '1'">
                <lay-tag color="#F5319D" variant="light">禁用</lay-tag>
              </div>
            </template>
            <template #remark="{ row }">
              <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
                <div class="oneRow">{{ row.remark }}</div>
              </lay-tooltip>
            </template>
            <template v-slot:toolbar>
              <lay-button size="sm" type="primary" @click="assign()">
                <lay-icon class="layui-icon-addition"></lay-icon>
                分配
              </lay-button>
            </template>
          </lay-table>
        </lay-card>
      </lay-container>
    </div>
    <div class="footer">
      <div class="footer-button">
        <lay-button type="primary" @click="turnBack">返回</lay-button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {FindUserRolesByUserIdEntity, SysUserVo} from "../../../types/system/User";
import {assignRole, findAllRoles, findUserRolesByUserId} from "../../../api/system/User";
import {SysDictValueEntity} from "../../../types/system/Dict";
import {useRoute, useRouter} from "vue-router";
import {Constants, loadSysDictValue} from "../../../util/UDict";
import {SysRoleEntity} from "../../../types/system/Role";
import {useTab} from "../../../layouts/composable/useTab";

/* INIT*/
const $route = useRoute();
const $router = useRouter();
const {closeOpen} = useTab();
onMounted(async () => {
  sysDataScopeList.value = await loadSysDictValue(Constants.SYS_DATA_SCOPE)
  userId.value = $route.params && $route.params.userId as string;
  loadDataSource(userId.value)
})
/* INIT*/
/* VAR*/
const userId = ref<string>('');
const assignRoleVo = ref<SysUserVo>({})
const visibleImport = ref(false)
const file1 = ref<any>([])
const model11 = ref<any>({})
const layFormRef11 = ref()
const visible11 = ref(false)
const title = ref('新增')
const sysDataScopeList = ref<Array<SysDictValueEntity>>([])
const dataSource = ref<Array<SysRoleEntity>>([]);
const findUserRolesByUserIdEntity = ref<FindUserRolesByUserIdEntity>({});
const loading = ref(false)
const selectedKeys = ref([''])
const pageQuery: PageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', type: 'checkbox', fixed: 'left'},
  // {title: '编号', key: 'id', fixed: 'left'},
  {title: '角色名称', key: 'roleName'},
  {title: '角色键名', key: 'roleKey'},
  {title: '状态', key: 'status', customSlot: 'status'},
  {title: '数据范围', key: 'dataScope', customSlot: 'dataScope'},
  {title: '备注', key: 'remark', customSlot: 'remark'},
  // {
  //   title: '操作',
  //   width: '120px',
  //   customSlot: 'operator',
  //   key: 'operator',
  //   fixed: 'right'
  // }
])

/* VAR*/

function toImport() {
  visibleImport.value = true
}

function toReset() {
  assignRoleVo.value = {}
}

const loadDataSource = (roleId: any) => {
  findUserRolesByUserId(roleId).then((res: any) => {
    if (res.code == 200) {
      selectedKeys.value = res.data.selectedRoles
      findUserRolesByUserIdEntity.value = res.data;
    }
  })
  findAllRoles().then((res: any) => {
    if (res.code === 200) {
      dataSource.value = res.data
    }
  })
}

function assign() {
  assignRole({
    userId: userId.value,
    afterRoleIdSet: selectedKeys.value
  }).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg);
      loadDataSource(userId.value)
    } else {
      layer.confirm(res.msg, {icon: 2})
    }
  })
}

function turnBack() {
  closeOpen('/system/user/index');
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
