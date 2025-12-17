<template>
  <lay-container :fluid="true">
    <lay-card :shadow="'hover'">
      <lay-form ref="queryFormRef" :model="searchQuery"
                label-position="top" @keydown.enter.prevent="toSearch">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="模板名称" prop="name">
              <lay-input
                  v-model="searchQuery.name"
                  :allow-clear="true"
                  placeholder="请输入"
                  size="sm"
              ></lay-input>
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
          :even="evenFlag"
          @change="change"
          @sortChange="sortChange">
        <template #defaultFlag="{ row }">
          <lay-switch
              :model-value="row.defaultFlag"
          ></lay-switch>
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
        <template v-slot:toolbar>
          <lay-button size="sm" type="normal" @click="toSearch"
                      v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_INDEX]">查询
          </lay-button>
          <lay-button size="sm" @click="queryFormReset"
                      v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_INDEX]">重置
          </lay-button>
          <lay-button size="sm" type="primary" @click="showSaveModal(Operate.ADD, null)"
                      v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_ADD]">
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove" v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_DELETE]">
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
              v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_EDIT]">编辑
          </lay-button>
          <lay-button
              border="blue"
              border-style="dashed"
              size="xs"
              @click="setupDetail(row.id)"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_SETUP_DETAIL]">
            设置模板
          </lay-button>
          <lay-button
              border="orange"
              border-style="dashed"
              size="xs"
              @click="showSaveModal(Operate.COPY, row)"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_ADD, $MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_EDIT]">
            复制
          </lay-button>
          <lay-popconfirm
              content="确定要删除吗?"
              @cancel="cancel"
              @confirm="confirm(row)"
              v-permission="[$MENU_PERMISSION.COMMON_ICON_TEMPLATE_HEADER_DELETE]">
            <lay-button border="red"
                        border-style="dashed"
                        size="xs">删除
            </lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="showSaveModalFlag" :area="['300px']" :title="saveModalTitle">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit(false)" v-esc-close="toCancel">
        <lay-form ref="saveFormRef" :model="saveVo" label-position="top">
          <lay-form-item label="模板名称" prop="name" required>
            <lay-input
                v-model="saveVo.name"
                :allow-clear="true"
                placeholder="请输入"
                size="sm"
            ></lay-input>
          </lay-form-item>
          <lay-form-item label="排序号" prop="orderNum">
            <lay-input-number
                style="width: 100%"
                v-model="saveVo.orderNum"
                position="right"
            ></lay-input-number>
          </lay-form-item>
          <lay-form-item label="是否默认" prop="defaultFlag">
            <lay-switch v-model="saveVo.defaultFlag"></lay-switch>
          </lay-form-item>
          <lay-form-item label="备注" prop="remark">
            <lay-textarea
                v-model="saveVo.remark"
                allow-clear
            ></lay-textarea>
          </lay-form-item>
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
  name: "IconTemplate",
};
</script>
<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {Operate} from "@/types/Constants";
import {
  CommonIconTemplateHeaderEntity,
  CommonIconTemplateHeaderVo
} from "@/types/common/icon/template/IconTemplateHeader";
import {
  deleteCommonIconTemplateHeader,
  findCommonIconTemplateHeader, findMaxOrderNum,
  findPageCommonIconTemplateHeader, saveUpdate
} from "@/api/common/icon/template/IconTemplateHeader";
import {getWeekdayCn} from "@/util/UDate";
import router from "@/router";
import app from "@/main";

/* INIT*/
onMounted(async () => {
  change()
})
/* INIT*/

/* VAR*/
const $MENU_PERMISSION = app.config.globalProperties.$MENU_PERMISSION
const searchQuery = ref<CommonIconTemplateHeaderVo>({})
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const dataSource = ref<Array<CommonIconTemplateHeaderEntity>>()
const selectedKeys = ref<Array<string>>([])
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '模板名称', width: '130px', key: 'name', fixed: 'left'},
  {title: '排序', width: '80px', key: 'orderNum', sort: 'asc'},
  {title: '是否默认', width: '40px', key: 'defaultFlag', customSlot: 'defaultFlag'},
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
const saveModalTitle = ref('');
const saveFormRef = ref(null)
const saveVo = ref<CommonIconTemplateHeaderVo>(<CommonIconTemplateHeaderVo>{})
const queryFormRef = ref(null)
/* VAR*/

/* FUNCTION*/
/**
 * 初始化表格
 */
const loadDataSource = () => {
  findPageCommonIconTemplateHeader(searchQuery.value, pageQuery).then((res: TableResult<CommonIconTemplateHeaderEntity>) => {
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
  saveModalTitle.value = Operate.ADD === text ? "新增" : Operate.EDIT === text ? "编辑" : Operate.COPY === text ? "复制" : "";
  if (row != null) {
    saveVo.value = {...row}
  }
  if (Operate.EDIT === text) {
    findCommonIconTemplateHeader({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        saveVo.value = res.data;
      }
    })
  } else if (Operate.ADD === text) {
    findMaxOrderNum().then((res: R<number>) => {
      saveVo.value.orderNum = res.data
    })
  } else if (Operate.COPY === text) {
    findCommonIconTemplateHeader({
      id: row.id
    }).then((res: any) => {
      saveVo.value = res.data;
      findMaxOrderNum().then((res: R<number>) => {
        saveVo.value.orderNum = res.data
      })
    })
  }
  showSaveModalFlag.value = !showSaveModalFlag.value
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
          deleteCommonIconTemplateHeader(selectedKeys.value).then((res: any) => {
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
      saveUpdate(saveVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          saveVo.value = {};
          if (clickFlag) {
            showSaveModalFlag.value = false
          } else {
            // 如果是修改+回车，则关闭窗口
            if (saveVo.id && saveVo.id != 0) {
              showSaveModalFlag.value = false
            }
          }
          showSaveModalFlag.value = false
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
  showSaveModalFlag.value = false
}

function confirm(row: any) {
  if (row) {
    deleteCommonIconTemplateHeader([row.id]).then((res: any) => {
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

function setupDetail(id: any) {
  router.push("/common/iconTemplate/iconTemplateDetail/" + id)
}

/* FUNCTION*/
</script>