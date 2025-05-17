<template>
  <lay-container :fluid="true">
    <lay-card>
      <lay-form ref="queryFormRef" :model="searchQuery"
                label-position="top" @keydown.enter.prevent="toSearch">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="图标名称" prop="name">
              <lay-input
                  v-model="searchQuery.name"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="图标所属分区" prop="iconPartition">
              <lay-select
                  size="sm"
                  style="width: 100%"
                  v-model="searchQuery.iconPartition"
                  :options="commonIconPartitionSelectList"
                  :items="commonIconPartitionSelectList"
                  :placeholder="'请选择'"
                  :allow-clear="true"
              ></lay-select>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <!-- table -->
    <div>
      <lay-table
          v-model:selected-keys="selectedKeys"
          :columns="columns"
          :data-source="dataSource"
          :default-toolbar="defaultToolbarFlag"
          :loading="loading"
          :page="pageQuery"
          :height="'550px'"
          :even="evenFlag"
          @change="change"
          @sortChange="sortChange">
        <template #icon="{ row }">
          <lay-avatar :src="row.url" @click="preview(row.url)"></lay-avatar>
        </template>
        <template #createTime="{ row }">
          {{ row.createTime }} （{{ getWeekdayCn(row.createTime) }}）
        </template>
        <template #modifyTime="{ row }">
          {{ row.modifyTime }} （{{ getWeekdayCn(row.modifyTime) }}）
        </template>
        <template #remark="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
            <div class="oneRow">{{ row.remark }}</div>
          </lay-tooltip>
        </template>
        <template #iconPartition="{ row }">
          <dict-scan :options="commonIconPartitionSelect" :value="row.iconPartition"/>
        </template>
        <template v-slot:toolbar>
          <!--          style="border-radius: 40%"-->
          <lay-button
              size="sm"
              type="normal"
              @click="toSearch"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_INDEX]"
          >
            查询
          </lay-button>
          <lay-button size="sm" @click="queryFormReset" v-permission="[$MENU_PERMISSION.COMMON_ICON_INDEX]"> 重置
          </lay-button>
          <lay-button
              size="sm"
              type="primary"
              @click="showSaveModal(Operate.ADD, null)"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_ADD]"
          >
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button
              size="sm"
              type="normal"
              @click="showBatchSaveModal(Operate.ADD)"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_UPLOAD_BATCH]"
          >
            <lay-icon class="layui-icon"></lay-icon>
            批量上传
          </lay-button>
          <lay-button size="sm"
                      @click="toRemove"
                      v-permission="[$MENU_PERMISSION.COMMON_ICON_DELETE]">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button
              border="green"
              border-style="dashed"
              size="xs"
              @click="showSaveModal(Operate.EDIT, row)"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_EDIT]">
            编辑
          </lay-button>
          <lay-button
              border="orange"
              border-style="dashed"
              size="xs"
              @click="showSaveModal(Operate.COPY, row)"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_ADD,$MENU_PERMISSION.COMMON_ICON_EDIT]">复制
          </lay-button>
          <lay-popconfirm
              content="确定要删除吗?"
              @cancel="cancel"
              @confirm="confirm(row)">
            <lay-button border="red"
                        border-style="dashed"
                        size="xs"
                        v-permission="[$MENU_PERMISSION.COMMON_ICON_DELETE]">
              删除
            </lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="showSaveModalFlag" :area="['400px', '700px']" :title="saveModalTitle">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" v-esc-close="toCancel">
        <lay-form ref="saveFormRef" :model="saveCommonIconVo" label-position="top">
          <lay-col :md="24">
            <lay-row>
              <lay-form-item label="图标名称" prop="name" required>
                <lay-input
                    v-model="saveCommonIconVo.name"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-row>
            <lay-row>
              <lay-form-item label="图标所属分区" prop="iconPartition" required>
                <lay-select
                    size="sm"
                    style="width: 100%"
                    v-model="saveCommonIconVo.iconPartition"
                    :options="commonIconPartitionSelectList"
                    :items="commonIconPartitionSelectList"
                    :allow-clear="true"
                ></lay-select>
              </lay-form-item>
            </lay-row>
            <lay-row>
              <lay-form-item label="备注" prop="remark">
                <lay-textarea
                    v-model="saveCommonIconVo.remark"
                    allow-clear
                ></lay-textarea>
              </lay-form-item>
            </lay-row>
            <lay-row>
              <lay-form-item label="图标" prop="avatar">
                <lay-upload
                    :url="ossPath"
                    v-model="saveFileList"
                    field="file[]"
                    :acceptMime="iconMime"
                    :auto="false"
                    @on-change="uploadOnChange">
                  <template #preview>
                    <div style="align-items: center;line-height: 30px">
                      文件名：{{ previewIconList[0]?.originalName }}
                    </div>
                    <lay-avatar :style="previewIconList[0]?.url ? '' : 'display: none'"
                                :src="previewIconList[0]?.url" @click="preview(previewIconList[0]?.url)"></lay-avatar>
                  </template>
                </lay-upload>
              </lay-form-item>
            </lay-row>
          </lay-col>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="toSubmit(true)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="saveToReset">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="showBatchSaveModalFlag" :area="['400px', '500px']" :title="batchSaveModalTitle">
      <div style="padding: 20px" @keydown.enter="toSubmit(false)" v-esc-close="toCancel">
        <lay-form ref="batchSaveFormRef" :model="batchSaveCommonIconVo" label-position="top">
          <lay-col :md="24">
            <lay-row>
              <lay-form-item label="图标所属分区" prop="iconPartition" required>
                <lay-select
                    size="sm"
                    style="width: 100%"
                    v-model="batchSaveCommonIconVo.iconPartition"
                    :options="commonIconPartitionSelectList"
                    :items="commonIconPartitionSelectList"
                    :allow-clear="true"
                ></lay-select>
              </lay-form-item>
            </lay-row>
            <lay-row>
              <lay-form-item label="图标" prop="file">
                <lay-upload
                    :url="ossPath"
                    v-model="saveBatchFileList"
                    field="file[]"
                    :acceptMime="iconMime"
                    :auto="false"
                    :multiple="true"
                    @on-change="uploadOnChange">
                  <template #preview>
                    <div class="easy-wrap">
                      <lay-avatar v-for="(item,index) in previewIconList"
                                  :key="index"
                                  :style="item.url ? '' : 'display: none'"
                                  :src="item.url"
                                  @click="preview(item.url)"
                      ></lay-avatar>
                    </div>
                  </template>
                </lay-upload>
              </lay-form-item>
            </lay-row>
          </lay-col>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="batchSaveToSubmit(true)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="saveBatchToReset">重置</lay-button>
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
  name: "CommonIcon",
};
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {Operate} from "@/types/Constants";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {CommonIconEntity, CommonIconVo, FindCommonIconEntity, FindPageCommonIconEntity} from "@/types/common/icon/Icon";
import {
  deleteCommonIcon,
  findCommonIcon,
  findPageCommonIcon,
  saveUpdate,
  saveUpdateBatch
} from "@/api/common/icon/Icon";
import {uploadTemp} from "@/api/system/Oss";
import {SysOssEntity} from "@/types/system/Oss";
import {getWeekdayCn} from "@/util/UDate";
import {preview} from "@/util/UImage";
import {useModalStore} from "@/layouts/composable/useModalStore";
import app from "@/main";

/* INIT*/
onMounted(async () => {
  commonIconPartitionSelect.value = await loadSysDictValue(Constants.COMMON_ICON_PARTITION)
  commonIconPartitionSelectList.value = await sysDictValueSelect(commonIconPartitionSelect.value)
  change()
})
/* INIT*/

/* VAR*/
const $MENU_PERMISSION = app.config.globalProperties.$MENU_PERMISSION
const modalStore = useModalStore()
const searchQuery = ref<CommonIconVo>(<CommonIconVo>{})
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const dataSource = ref<Array<FindPageCommonIconEntity>>()
const selectedKeys = ref<Array<string>>([])
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '图标名称', width: '130px', key: 'name', fixed: 'left'},
  {title: '图标', width: '50px', key: 'icon', fixed: 'left', customSlot: 'icon'},
  {title: '所属分区', width: '130px', key: 'iconPartition', customSlot: 'iconPartition'},
  {title: '创建时间', width: '180px', key: 'createTime', customSlot: 'createTime', sort: 'desc'},
  {title: '修改时间', width: '180px', key: 'modifyTime', customSlot: 'modifyTime', sort: 'desc'},
  {title: '备注', width: '150px', key: 'remark', customSlot: 'remark'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const loading = ref(true)
const defaultToolbarFlag = ref(true)
const evenFlag = ref(true)
const showSaveModalFlag = ref(false)
const showBatchSaveModalFlag = ref(false)
const saveModalTitle = ref('');
const batchSaveModalTitle = ref('新增');
const saveFormRef = ref()
const saveCommonIconVo = ref<CommonIconVo>(<CommonIconVo>{})
const batchSaveFormRef = ref()
const batchSaveCommonIconVo = ref<CommonIconVo>(<CommonIconVo>{})
const queryFormRef = ref(null)
const commonIconPartitionSelect = ref<Array<SysDictValueEntity>>(<Array<SysDictValueEntity>>[]);
const commonIconPartitionSelectList = ref();
const iconMime = "image/svg+xml"
const ossPath = import.meta.env.VITE_APP_UPLOAD_PATH
const saveFileList = ref<File[]>(<File[]>[])
const saveBatchFileList = ref<File[]>(<File[]>[])
const previewIconList = ref<Array<SysOssEntity>>(<SysOssEntity[]>[])
/* VAR*/

/* FUNCTION*/
/**
 * 初始化表格
 */
const loadDataSource = () => {
  findPageCommonIcon(searchQuery.value, pageQuery).then((res: TableResult<FindPageCommonIconEntity>) => {
    if (res.code == 200) {
      pageQuery.total = res.total;
      dataSource.value = res.rows
    } else {
      layer.msg(res.msg)
      return;
    }
  }).catch(e => {
    layer.msg(e.msg)
  });
}

/**
 * 刷新表格
 */
const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 200)
}

/**
 * 触发列表字段排序
 */
const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}

/**
 * 查询按钮
 */
function toSearch() {
  pageQuery.current = 1
  dataSource.value = []
  change()
}

/**
 * 查询条件重置
 */
function queryFormReset() {
  searchQuery.value = {}
}

/**
 * 显示新增/修改/复制弹窗
 */
const showSaveModal = (text: any, row: any) => {
  saveModalTitle.value = Operate.ADD === text ? "新增" : Operate.EDIT === text ? "编辑" : "";
  if (row != null) {
    saveCommonIconVo.value = {...row}
  }
  if (Operate.EDIT === text) {
    findCommonIcon({
      id: row.id
    }).then((res: R<FindCommonIconEntity>) => {
      if (res.code === 200) {
        let data = res.data;
        saveCommonIconVo.value = {
          id: data?.id,
          recVer: data?.recVer,
          logicDel: data?.logicDel,
          buildIn: data?.buildIn,
          name: data?.name,
          fileId: data?.fileId,
          iconPartition: data?.iconPartition,
          remark: data?.remark,
        };
        previewIconList.value = []
        previewIconList.value.push({
          originalName: data?.fileName,
          url: data?.url
        })
      }
    })
  } else if (Operate.ADD === text) {
    saveCommonIconVo.value = {}
    previewIconList.value = {}
  } else if (Operate.COPY === text) {
    findCommonIcon({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        saveCommonIconVo.value = res.data;
      }
    })
  }
  showSaveModalFlag.value = !showSaveModalFlag.value
}

function showBatchSaveModal(text: any) {
  batchSaveModalTitle.value = Operate.ADD === text ? "新增" : Operate.EDIT === text ? "编辑" : "";
  previewIconList.value = {}
  showBatchSaveModalFlag.value = !showBatchSaveModalFlag.value
}

/**
 * 删除按钮
 */
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
          deleteCommonIcon(selectedKeys.value).then((res: any) => {
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

/**
 * 保存弹出框-保存
 */
function toSubmit(clickFlag: boolean) {
  saveFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveUpdate(saveFileList.value, saveCommonIconVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          saveCommonIconVo.value = {};
          if (clickFlag) {
            showSaveModalFlag.value = false
          } else {
            // 如果是修改+回车，则关闭窗口
            if (saveCommonIconVo.id && saveCommonIconVo.id != 0) {
              showSaveModalFlag.value = false
            }
          }
          previewIconList.value = [];
          saveFileList.value = []
          showSaveModalFlag.value = false
        }
      })
    }
  })
}

/**
 * 批量上传-保存
 */
function batchSaveToSubmit(clickFlag: boolean) {
  batchSaveFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveUpdateBatch(saveBatchFileList.value, batchSaveCommonIconVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          batchSaveCommonIconVo.value = {};
          if (clickFlag) {
            showBatchSaveModalFlag.value = false
          } else {
            // 如果是修改+回车，则关闭窗口
            if (batchSaveCommonIconVo.id && batchSaveCommonIconVo.id != 0) {
              showBatchSaveModalFlag.value = false
            }
          }
          previewIconList.value = [];
          saveBatchFileList.value = []
          showBatchSaveModalFlag.value = false
        }
      })
    }
  })
}

/**
 * 保存弹出框-重置
 */
function saveToReset() {
  saveFormRef?.value.reset();
  previewIconList.value = []
}

function saveBatchToReset() {
  batchSaveFormRef?.value.reset();
  previewIconList.value = []
}

/**
 * 保存弹出框-取消
 */
function toCancel() {
  showSaveModalFlag.value = false
  showBatchSaveModalFlag.value = false
}

function confirm(row: any) {
  if (row) {
    deleteCommonIcon([row.id]).then((res: any) => {
      if (res.code === 200) {
        layer.msg('删除成功')
      }
      loadDataSource();
    }).catch(e => {
      layer.confirm(e.msg, {icon: 2})
    })
  }
}

function cancel() {
  layer.msg('您已取消操作')
}

function uploadOnChange(file: any) {
  uploadTemp(file).then((res: any) => {
    if (res.code === 200) {
      if (res.data) {
        previewIconList.value = res.data
      }
    }
  })
}

/* FUNCTION*/
</script>