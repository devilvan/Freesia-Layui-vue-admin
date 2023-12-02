<template>
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
    <!-- table -->
    <div class="table-box">
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
    </div>
  </lay-container>
</template>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {FindUserRolesByUserIdEntity, SysUserVo} from "../../../types/system/User";
import {assignRole, findAllRoles, findUserRolesByUserId} from "../../../api/system/User";
import {SysDictValueEntity} from "../../../types/system/Dict";
import {useRoute} from "vue-router";
import {Constants, loadSysDictValue} from "../../../util/UDict";
import {SysRoleEntity} from "../../../types/system/Role";

/* INIT*/
const $route = useRoute();
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
  {title: '编号', key: 'id', fixed: 'left'},
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

function toSearch() {
  pageQuery.current = 1
  change()
}


const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource(userId)
    loading.value = false
  }, 1000)
}
const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}

const loadDataSource = (userId: any) => {
  findUserRolesByUserId(userId).then((res: any) => {
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

const changeVisible11 = (text: any, row?: any) => {
  title.value = text
  if (row) {
    let info = JSON.parse(JSON.stringify(row))
    model11.value = info
  } else {
    model11.value = {}
  }
  visible11.value = !visible11.value
}
const submit11 = function () {
  layFormRef11.value.validate((isValidate: any, model: any, errors: any) => {
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
  layFormRef11.value.clearValidate()
}
// 重置表单
const reset11 = function () {
  layFormRef11.value.reset()
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
}

function confirm() {
  layer.msg('您已成功删除')
}

function cancel() {
  layer.msg('您已取消操作')
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
  }).catch((e: any) => {
    layer.confirm(e.msg, {icon: 3})
  })
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
</style>
