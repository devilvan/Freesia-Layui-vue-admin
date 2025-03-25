<template>
  <lay-container :fluid="true">
    <lay-card>
      <lay-form ref="queryFormRef" :model="searchQuery"
                label-position="top">
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
          >
            查询
          </lay-button>
          <lay-button size="sm" @click="queryFormReset"
          > 重置
          </lay-button>
          <lay-button
              size="sm"
              type="primary"
              @click="showSaveModal(Operate.ADD, null)"
          >
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm"
                      @click="toRemove">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button
              border="green"
              border-style="dashed"
              size="xs"
              @click="showSaveModal(Operate.EDIT, row)">编辑
          </lay-button>
          <lay-button
              border="orange"
              border-style="dashed"
              size="xs"
              @click="showSaveModal(Operate.COPY, row)">复制
          </lay-button>
          <lay-popconfirm
              content="确定要删除吗?"
              @cancel="cancel"
              @confirm="confirm(row)">
            <lay-button border="red"
                        border-style="dashed"
                        size="xs">删除
            </lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="showModalFlag" :area="['1200px']" :title="saveModalTitle">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" @keydown.esc.prevent="toCancel">
        <lay-form ref="saveFormRef" :model="saveCommonIconVo" :rules="saveFromRules" label-position="top">
          <lay-row :space="20">
            <lay-col :md="6">
              <lay-form-item label="图标名称" prop="name" required>
                <lay-input
                    v-model="saveCommonIconVo.name"
                    :allow-clear="true"
                    size="sm"
                ></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="图标所属分区" prop="iconPartition" required>
                <lay-select
                    size="sm"
                    style="width: 100%"
                    v-model="saveCommonIconVo.iconPartition"
                    :options="commonIconPartitionSelectList"
                    :items="commonIconPartitionSelectList"
                    :allow-clear="true"
                    @change="saveIconPartitionChange"
                ></lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="图标" prop="avatar">
                <lay-upload
                    :url="ossPath"
                    v-model="fileList"
                    field="file"
                    :acceptMime="iconMime"
                    :auto="false"
                    @on-change="uploadOnChange">
                </lay-upload>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="图标" prop="avatar">
                <div style="display: inline-flex">
                  <div style="width: 60px;justify-content: center;">
                    <object v-if="previewIcon.url" :data="previewIcon.url" type="image/svg+xml" width="30"
                            height="30"></object>
                  </div>
                  <div style="justify-content: center; align-items: center;line-height: 30px">
                    文件名：{{ previewIcon.originalName }}
                  </div>
                </div>
                cd
              </lay-form-item>
            </lay-col>
          </lay-row>
          <lay-row :space="20">
            <lay-col :md="6">
              <lay-form-item label="备注" prop="remark">
                <lay-textarea
                    v-model="saveCommonIconVo.remark"
                    allow-clear
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="toSubmit(true)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="toReset">重置</lay-button>
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
import {TableResult} from "@/types/Result";
import {Operate} from "@/types/Constants";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {CommonIconEntity, CommonIconVo} from "@/types/common/Icon";
import {deleteCommonIcon, findCommonIcon, findPageCommonIcon, saveUpdate} from "@/api/common/Icon";
import {parseImgPath} from "@/util/UImage";
import {uploadTemp} from "@/api/system/Oss";
import {SysOssEntity} from "@/types/system/Oss";

/* INIT*/
onMounted(async () => {
  commonIconPartitionSelect.value = await loadSysDictValue(Constants.COMMON_ICON_PARTITION)
  commonIconPartitionSelectList.value = await sysDictValueSelect(commonIconPartitionSelect.value)
  loadDataSource()
})
/* INIT*/

/* VAR*/
const searchQuery = ref<CommonIconVo>(<CommonIconVo>{})
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const dataSource = ref<Array<CommonIconEntity>>()
const selectedKeys = ref<Array<string>>([])
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '预算描述', width: '130px', key: 'budgetDesc', fixed: 'left'},
  {title: '预算金额', width: '130px', key: 'outlay', sort: 'desc'},
  {title: '预算类型', width: '130px', key: 'iconPartition', customSlot: 'iconPartition'},
  {title: '时间范围从', width: '130px', key: 'durationFrom'},
  {title: '时间范围到', width: '130px', key: 'durationTo'},
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
const showModalFlag = ref(false)
const saveModalTitle = ref('');
const saveFormRef = ref(null)
const saveCommonIconVo = ref<CommonIconVo>(<CommonIconVo>{})
const queryFormRef = ref(null)
const commonIconPartitionSelect = ref<Array<SysDictValueEntity>>(<Array<SysDictValueEntity>>[]);
const commonIconPartitionSelectList = ref();
const saveFromRules = ref({
  outlay: {
    validator(rule: { field: any; }, value: any, callback: (arg0: Error) => void) {
      if (value <= 0) {
        callback(new Error("金额不能为0"));
      } else {
        return true;
      }
    }
  },
})
const iconMime = "image/apng,image/bmp,image/gif,image/jpeg,image/pjpeg,image/png,image/svg+xml,image/tiff,image/webp,image/x-icon"
const ossPath = import.meta.env.VITE_APP_UPLOAD_PATH
const fileList = ref([])
const previewIcon = ref<SysOssEntity>(<SysOssEntity>{})
/* VAR*/

/* FUNCTION*/
/**
 * 初始化表格
 */
const loadDataSource = () => {
  findPageCommonIcon(searchQuery.value, pageQuery).then((res: TableResult<CommonIconEntity>) => {
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
  }, 1000)
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
    }).then((res: any) => {
      if (res.code === 200) {
        saveCommonIconVo.value = res.data;
      }
    })
  } else if (Operate.ADD === text) {
  } else if (Operate.COPY === text) {
  }
  showModalFlag.value = !showModalFlag.value
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
      saveUpdate(saveCommonIconVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          saveCommonIconVo.value = {};
          if (clickFlag) {
            showModalFlag.value = false
          } else {
            // 如果是修改+回车，则关闭窗口
            if (saveCommonIconVo.id && saveCommonIconVo.id != 0) {
              showModalFlag.value = false
            }
          }
          showModalFlag.value = false
        }
      })
    }
  })
}

/**
 * 保存弹出框-重置
 */
function toReset() {
  saveFormRef.value.reset();
}

/**
 * 保存弹出框-取消
 */
function toCancel() {
  showModalFlag.value = false
}

function saveIconPartitionChange(value: any) {
  if (!value || value !== 'CUSTOM') {
    // 如果不是自定义则时间范围置空
    saveCommonIconVo.value.durationFrom = null;
    saveCommonIconVo.value.durationTo = null;
  }
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
        previewIcon.value = res.data
      }
    }
  })
}

/* FUNCTION*/
</script>