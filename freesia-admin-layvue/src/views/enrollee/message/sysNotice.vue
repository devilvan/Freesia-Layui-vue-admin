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
    <!--    <template #status="{ row }">-->
    <!--      <lay-switch-->
    <!--          :model-value="row.status"-->
    <!--          @change="changeStatus($event, row)"-->
    <!--      ></lay-switch>-->
    <!--    </template>-->
    <template v-slot:toolbar>
      <lay-button size="sm" type="primary">标记已读</lay-button>
      <lay-button size="sm" type="normal" @click="showSaveModal(Operate.ADD)">新增</lay-button>
      <lay-button size="sm" @click="remove">删除</lay-button>
    </template>
    <template v-slot:operator="{ row }">
      <lay-button size="sm" type="primary" @click="toEdit(row)">编辑</lay-button>
      <lay-button size="sm">查看</lay-button>
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
      <div style="width: 97%; text-align: right">
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
import {findPageSysNotice, saveUpdate} from "@/api/system/Notice";
import {SysNoticeEntity, SysNoticeVo} from "@/types/system/Notice";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {defaultShortcuts} from "@/util/UDate";
import {R} from "@/types/Result";

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
const page = reactive({current: 1, limit: 10, total: 100})
const columns = ref([
  {title: '选项', width: '50px', type: 'checkbox', fixed: 'left'},
  {title: '编号', width: '80px', key: 'id', fixed: 'left', sort: 'desc'},
  {title: '姓名', width: '80px', key: 'name', sort: 'desc'},
  {title: '内容', width: '260px', key: 'remark'},
  {title: '时间', width: '120px', key: 'joinTime'},
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
  }, 1000)
}

const sortChange = (key: any, sort: any) => {
  layer.msg(
      `字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`
  )
}

const dataSource = ref<SysNoticeEntity[]>();

const remove = () => {
  layer.msg(selectedKeys.value[0], {area: '50%'})
}

function loadDataSource() {
  findPageSysNotice().then(res => {
    dataSource.value = res.data;
  });
}

function toEdit(row: any) {
}

function showSaveModal(o: Operate) {
  operate.value = o
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
