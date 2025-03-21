<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: auto">
      <lay-container :fluid="true">
        <lay-card>
          <lay-form style="margin-top: 10px">
            <lay-card title="部门信息">
              <lay-row>
                <lay-col :md="8">
                  <lay-form-item label="部门ID" label-width="80">
                    <lay-input
                        v-model="findDeptRolesByDeptIdEntity.deptId"
                        size="sm"
                        style="width: 100%"
                        :disabled="true"
                    ></lay-input>
                  </lay-form-item>
                </lay-col>
                <lay-col :md="8">
                  <lay-form-item label="部门名" label-width="80">
                    <lay-input
                        v-model="findDeptRolesByDeptIdEntity.deptName"
                        size="sm"
                        style="width: 100%"
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
              class="table-box"
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
              <div v-show="row.status === '1'">
                <lay-tag color="#2dc570" variant="light">启用</lay-tag>
              </div>
              <div v-show="row.status === '0'">
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
  </div>
  <lay-affix class="affix-footer" :target="target" :offset="30" position="bottom" v-if="target">
    <lay-button type="primary" @click="$tab.closeOpen('/system/dept/index')">返回</lay-button>
  </lay-affix>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "DeptAssignRole",
};
</script>
<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {FindDeptRolesByDeptIdEntity, SysDeptVo} from "../../../types/system/Dept";
import {assignRole, findDeptRolesByDeptId} from "../../../api/system/Dept";
import {SysDictValueEntity} from "../../../types/system/Dict";
import {useRoute} from "vue-router";
import {Constants, loadSysDictValue} from "../../../util/UDict";
import {SysRoleEntity} from "../../../types/system/Role";
import {useTabStore} from "../../../layouts/composable/useTabStore";
import {findAllRoles} from "../../../api/system/Role";
import router from "../../../router";

/* INIT*/
const $route = useRoute();
const $router = router;
const $tab = useTabStore();
onMounted(async () => {
  sysDataScopeList.value = await loadSysDictValue(Constants.SYS_DATA_SCOPE)
  deptId.value = $route.params && $route.params.deptId as string;
  loadDataSource(deptId.value)
})
/* INIT*/
/* VAR*/
const deptId = ref<string>('');
const assignRoleVo = ref<SysDeptVo>({})
const visibleImport = ref(false)
const file1 = ref<any>([])
const model11 = ref<any>({})
const layFormRef11 = ref()
const visible11 = ref(false)
const title = ref('新增')
const sysDataScopeList = ref<Array<SysDictValueEntity>>([])
const dataSource = ref<Array<SysRoleEntity>>([]);
const findDeptRolesByDeptIdEntity = ref<FindDeptRolesByDeptIdEntity>({});
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
const target = ref()
nextTick(() => {
  target.value = document.querySelector(".layui-body");
})
/* VAR*/

function toImport() {
  visibleImport.value = true
}

function toReset() {
  assignRoleVo.value = {}
}

const loadDataSource = (roleId: any) => {
  findDeptRolesByDeptId(roleId).then((res: any) => {
    if (res.code == 200) {
      selectedKeys.value = res.data.selectedRoles
      findDeptRolesByDeptIdEntity.value = res.data;
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
    deptId: deptId.value,
    afterRoleIdSet: selectedKeys.value
  }).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg);
      loadDataSource(deptId.value)
    } else {
      layer.confirm(res.msg, {icon: 2})
    }
  })
}
</script>
