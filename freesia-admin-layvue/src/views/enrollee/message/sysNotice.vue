<template>
  <lay-table
      :page="page"
      :columns="columns"
      :loading="loading"
      :default-toolbar="true"
      :data-source="dataSource"
      v-model:selected-keys="selectedKeys"
      @change="change"
      @sortChange="sortChange"
  >
    <template #effectiveTime="{ row }">
      {{ row.effectiveTimeFrom }} - {{ row.effectiveTimeTo }}
    </template>
    <template v-slot:toolbar>
      <lay-button size="sm" type="primary">标记已读</lay-button>
      <lay-button size="sm" type="warm">标记已读</lay-button>
      <lay-button size="sm" type="normal" @click="showSaveModal(Operate.ADD, null)">新增</lay-button>
      <lay-button size="sm" type="danger" @click="toRemove">删除</lay-button>
    </template>
    <template v-slot:operator="{ row }">
      <lay-button size="sm" type="primary" @click="showSaveModal(Operate.EDIT, row)">编辑</lay-button>
      <lay-button size="sm">查看</lay-button>
      <lay-popconfirm
          content="确定要删除吗?"
          @cancel="toCancel"
          @confirm="confirmDelete(row)">
        <lay-button size="sm" type="danger">删除</lay-button>
      </lay-popconfirm>
    </template>
  </lay-table>

  <lay-layer
      v-model="saveModalFlag"
      :title="operate === Operate.ADD ? '新增' : operate === Operate.EDIT ? '编辑' : operate === Operate.COPY ? '复制' : '新增'"
      :area="['600px']"
  >
    <div style="padding: 20px" v-esc-close="closeSaveModal">
      <lay-form :model="saveVo" ref="saveGroupFormRef" label-position="top" size="md">
        <lay-col :md="24">
          <lay-row>
            <lay-form-item label="标题" prop="title" required>
              <lay-input v-model="saveVo.title"></lay-input>
            </lay-form-item>
          </lay-row>
          <lay-row>
            <lay-form-item label="通知类型" required>
              <lay-select
                  style="width: 100%"
                  size="sm"
                  v-model="saveVo.type"
                  :options="sysNoticeTypeSelectList"
                  :items="sysNoticeTypeSelectList"
                  :allow-clear="true"
                  placeholder="请选择"
              ></lay-select>
            </lay-form-item>
          </lay-row>
          <lay-row>
            <lay-form-item label="生效时间">
              <lay-date-picker style="width: 100%" v-model="saveVo.effectiveTime" allow-clear range
                               :format="sdf_YMDHMS" :inputFormat="sdf_YMDHMS" type="datetime"
                               :shortcuts="defaultShortcuts" simple
                               :default-time="dateRangeDefaultTime"></lay-date-picker>
            </lay-form-item>
          </lay-row>
          <lay-row>
            <lay-form-item label="内容" prop="content">
              <lay-textarea v-model="saveVo.content" :allow-clear="true" show-count
                            :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-row>
        </lay-col>

      </lay-form>
      <div style="width: 100%; text-align: right">
        <lay-button size="sm" type="primary" @click="toSave">保存</lay-button>
        <lay-button size="sm" @click="closeSaveModal">取消</lay-button>
      </div>
    </div>
  </lay-layer>
</template>
<script lang="ts">

export default {
  name: 'SysNotice'
}
</script>
<script lang="ts" setup>
import {ref, watch, reactive, onMounted} from 'vue'
import {layer} from '@layui/layui-vue'
import {Operate} from "@/types/Constants";
import {findMenuListByUserId} from "@/api/system/Menu";
import {deleteSysNotice, findPageSysNotice, findSysNotice, saveUpdate} from "@/api/system/Notice";
import {SysNoticeEntity, SysNoticeVo} from "@/types/system/Notice";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {defaultShortcuts} from "@/util/UDate";
import {R, TableResult} from "@/types/Result";
import {PageQuery} from "@/types/Common";
import {deleteAccountCost, findAccountCost} from "@/api/account/Account";

/*INIT*/
onMounted(async () => {
  sysNoticeTypeSelect.value = await loadSysDictValue(Constants.SYS_NOTICE_TYPE)
  sysNoticeTypeSelectList.value = await sysDictValueSelect(sysNoticeTypeSelect.value)
  setTimeout(() => {
    loading.value = false
    loadDataSource()
  }, 200)
})
/*INIT*/

/*VAR*/
const currentTab = ref('system')
const messageInfo = ref({
  system: 3,
  user: 0,
  todo: 11
})
const selectedKeys = ref<string[]>([])
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', width: '50px', type: 'checkbox', fixed: 'left'},
  {title: '标题', width: '80px', key: 'title'},
  {title: '内容', width: '260px', key: 'content'},
  {title: '生效时间', width: '120px', key: 'effectiveTime', customSlot: 'effectiveTime'},
  {title: '发布时间', width: '60px', key: 'createTime'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const loading = ref(true)
const operate = ref<string>(Operate.ADD)
const saveModalFlag = ref<boolean>(false)
const searchQuery = ref<SysNoticeVo>({});
const saveVo = ref<SysNoticeVo>({});
const sysNoticeTypeSelect = ref<Array<SysDictValueEntity>>();
const sysNoticeTypeSelectList = ref<any[]>();
const sdf_YMDHMS = 'YYYY-MM-DD HH:mm:ss'
const dateRangeDefaultTime = ['00:00:00', '23:59:59'];
/*VAR*/

/*FUNCTION*/
function change() {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 100)
}

const sortChange = (key: any, sort: any) => {
  layer.msg(
      `字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`
  )
}

const dataSource = ref<SysNoticeEntity[]>();

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
          deleteSysNotice(selectedKeys.value).then((res: any) => {
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

function loadDataSource() {
  findPageSysNotice(searchQuery.value, pageQuery).then((res: TableResult<SysNoticeEntity>) => {
    dataSource.value = res.rows;
  });
}

function showSaveModal(o: Operate, row: any) {
  operate.value = o
  if (row != null) {
    saveVo.value = {...row}
  }
  if (Operate.EDIT === o) {
    saveVo.value = {}
    let param: SysNoticeVo = {
      id: row.id
    }
    findSysNotice(param).then((res: R<SysNoticeEntity>) => {
      let data = res.data;
      saveVo.value = {...data}
      saveVo.value.effectiveTime = [data?.effectiveTimeFrom, data?.effectiveTimeTo]
    })
  } else if (Operate.ADD === o) {
  }
  saveModalFlag.value = true
}

function toSave() {
  saveUpdate(saveVo.value).then((res: R<void>) => {
    closeSaveModal();
    change();
  })
}

function closeSaveModal() {
  saveModalFlag.value = false
}

function toCancel() {
  layer.msg('您已取消操作')
}

function confirmDelete(row: any) {
  if (row && row.buildIn) {
    layer.msg('系统内置参数无法删除！')
    return;
  } else {
    deleteSysNotice([row.id]).then((res: any) => {
      if (res.code === 200) {
        layer.msg('删除成功')
      }
      loadDataSource();
    }).catch(e => {
      layer.confirm(e.msg, {icon: 2})
    })
  }
}

/*FUNCTION*/
</script>

<style scoped>
.table-content {
  width: 100%;
  height: 100%;
  padding: 10px;
  box-sizing: border-box;
  background-color: #fff;
}

.option-icon {
  cursor: pointer;
}
</style>
