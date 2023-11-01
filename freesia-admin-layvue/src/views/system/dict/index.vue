<template>
  <lay-container fluid="true" class="dictionary-box">
    <div style="display: flex">
      <div :style="{ width: isFold ? `0px` : `300px` }" class="left-tree">
        <!-- tree -->
        <div v-show="!isFold">
          <lay-button type="normal" size="sm" @click="toAdd">
            <lay-icon type="layui-icon-addition"></lay-icon>
            新建
          </lay-button>
          <lay-button type="warm" size="sm" @click="toEdit">
            <lay-icon type="layui-icon-edit"></lay-icon>
            修改
          </lay-button>
          <lay-button type="danger" size="sm" @click="toDelete">
            <lay-icon type="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
        </div>
        <div style="padding-top: 10px">
          <lay-form :model="sysDictKeySearchQuery" ref="layFormRef11" required>
            <lay-input
                v-model="sysDictKeySearchQuery.keyName"
                placeholder="请输入字典键名查询"
                size="sm"
                :allow-clear="true"
                style="width: 200px; margin-right: 10px; background: #fff"
            ></lay-input>
            <lay-button type="normal" size="sm" @click="searchSysDictKey">
              查询
            </lay-button>
          </lay-form>
        </div>
        <lay-tree
            v-show="!isFold"
            style="margin-top: 10px"
            :data="sysDictKeyList"
            v-model:selectedKey="selectedKey"
            :showLine="showLine"
            :expandKeys="expandKeys"
            @node-click="handleClick"
        >
          <template #title="{ data }">
            <span :class="selectedKey === data.id ? 'isChecked' : ''">
               {{ data.dictKey }} {{ data.keyName }}
            </span>
          </template>
        </lay-tree>
        <div class="isFold" @click="isFold = !isFold">
          &nbsp;<lay-icon v-if="!isFold" class="layui-icon-left"></lay-icon>
          <lay-icon v-else class="layui-icon-right"></lay-icon>
        </div>
      </div>
      <div style="width: 1200px">
        <lay-table
            class="table-style"
            :columns="columns"
            :loading="loading"
            :default-toolbar="true"
            :data-source="sysDictValueList"
            v-model:selected-keys="selectedKeys"
            @sortChange="sortChange"
        >
          <template #status="{ row }">
            <div v-show="row.status === '0'">
              <lay-tag color="#31BDEC" variant="light">启用</lay-tag>
            </div>
            <div v-show="row.status === '1'">
              <lay-tag color="#FF5722" variant="light">禁用</lay-tag>
            </div>
          </template>
          <template #isDefault="{ row }">
            <div v-show="row.isDefault === '0'">
              <lay-tag color="#2dc570" variant="light">是</lay-tag>
            </div>
            <div v-show="row.isDefault === '1'">
              <lay-tag color="#F5319D" variant="light">否</lay-tag>
            </div>
          </template>
          <template v-slot:toolbar>
            <lay-input
                v-model="sysDictValueSearchQuery.name"
                placeholder="请输入关键字查询"
                size="sm"
                :allow-clear="true"
                style="width: 200px; margin-right: 10px; background: #fff"
            ></lay-input>
            <lay-button type="normal" size="sm" @click="toSearch">
              查询
            </lay-button>
            <lay-button
                size="sm"
                type="primary"
                @click="showSysDictValueSaveModal('新增', null)"
            >新增
            </lay-button
            >
            <lay-button size="sm" type="danger" @click="toRemove"
            >删除
            </lay-button
            >
            <lay-button size="sm" style="background-color: #FFB800;color:whitesmoke" @click="toEnable"
            >启用/禁用
            </lay-button
            >
            <lay-button type="normal" size="sm" @click="flushCache"
            >刷新缓存
            </lay-button
            >
          </template>
          <template v-slot:operator="{ row }">
            <lay-button
                size="xs"
                border="green"
                border-style="dashed"
                @click="showSysDictValueSaveModal('编辑', row)"
            >编辑
            </lay-button
            >
            <lay-popconfirm
                :content="'确定要禁用 【' + row.value + '】 字典值吗?'"
                @confirm="enableConfirm(row)"
                @cancel="cancel"
            >
              <lay-button border="blue" border-style="dashed" size="xs"
              >启/禁用
              </lay-button
              >
            </lay-popconfirm>
            <lay-popconfirm
                :content="'确定要删除 【' + row.value + '】 字典值吗?'"
                @confirm="confirm(row)"
                @cancel="cancel"
            >
              <lay-button border="red" border-style="dashed" size="xs"
              >删除
              </lay-button
              >
            </lay-popconfirm>
          </template>
        </lay-table>
      </div>
    </div>
    <lay-layer v-model="showSysDictValueSaveModalFlag" :title="title" :area="['500px']">
      <div style="padding: 20px">
        <lay-form :model="insertSysDictValueVo" ref="layFormRef11">
          <lay-row>
            <lay-row md="12">
              <lay-form-item label="字典键ID" prop="keyId" required>
                <lay-input v-model="insertSysDictValueVo.keyId" :disabled="true"></lay-input>
              </lay-form-item>
              <lay-form-item label="字典键名" prop="dictKey" required>
                <lay-input v-model="insertSysDictValueVo.dictKey" :disabled="true"></lay-input>
              </lay-form-item>
            </lay-row>
            <lay-row md="12">
              <lay-form-item label="字典值名" prop="valueName">
                <lay-input v-model="insertSysDictValueVo.valueName"></lay-input>
              </lay-form-item>
              <lay-form-item label="字典值" prop="value">
                <lay-input v-model="insertSysDictValueVo.value"></lay-input>
              </lay-form-item>
              <lay-form-item label="排序" prop="orderNum">
                <lay-input-number
                    :min="0"
                    v-model="insertSysDictValueVo.orderNum"
                ></lay-input-number>
              </lay-form-item>
            </lay-row>
            <lay-row md="12">
              <lay-form-item label="备注" prop="remark">
                <lay-textarea
                    allow-clear
                    placeholder="请输入备注"
                    v-model="insertSysDictValueVo.remark"
                ></lay-textarea>
              </lay-form-item>
            </lay-row>
            <lay-row md="12">
              <lay-form-item label="CSS样式" prop="cssStyle">
                <lay-textarea
                    placeholder="请输入CSS样式（JSON）"
                    v-model="insertSysDictValueVo.cssStyle"
                ></lay-textarea>
              </lay-form-item>
            </lay-row>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: center">
          <lay-button size="sm" type="primary" @click="sysDictValueInsert"
          >保存
          </lay-button
          >
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="sysDictKeyInsertModal" :title="modalTitle" :area="['500px', '400px']">
      <div style="padding: 20px">
        <lay-form :model="insertSysDictKey" ref="layFormRef11" required>
          <lay-form-item label="字典键" prop="dictKey">
            <lay-input v-model="insertSysDictKey.dictKey"></lay-input>
          </lay-form-item>
          <lay-form-item label="字典键名" prop="keyName">
            <lay-input v-model="insertSysDictKey.keyName"></lay-input>
          </lay-form-item>
          <lay-form-item label="备注" prop="remark">
            <lay-textarea
                :rows="3"
                allow-clear
                placeholder="请输入备注"
                v-model="insertSysDictKey.remark"
            ></lay-textarea>
          </lay-form-item>
        </lay-form>
        <div style="width: 100%; text-align: center; margin-top: 20px">
          <lay-button size="sm" type="primary" @click="sysDictKeyInsert"
          >保存
          </lay-button
          >
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>
    <lay-layer v-model="sysDictKeyModifyModal" :title="modalTitle" :area="['500px', '400px']">
      <div style="padding: 20px">
        <lay-form :model="modifySysDictKey" ref="layFormRef11" required>
          <lay-form-item label="字典键" prop="dictKey">
            <lay-input v-model="modifySysDictKey.dictKey"></lay-input>
          </lay-form-item>
          <lay-form-item label="字典键名" prop="keyName">
            <lay-input v-model="modifySysDictKey.keyName"></lay-input>
          </lay-form-item>
          <lay-form-item label="状态" prop="status">
            <lay-switch :model-value="modifySysDictKey.status === '0'"
                        @change="editModalChangeStatus($event, modifySysDictKey)"></lay-switch>
          </lay-form-item>
          <lay-form-item label="备注" prop="remark">
            <lay-textarea
                :rows="3"
                allow-clear
                placeholder="请输入备注"
                v-model="modifySysDictKey.remark"
            ></lay-textarea>
          </lay-form-item>
        </lay-form>
        <div style="width: 100%; text-align: center; margin-top: 20px">
          <lay-button size="sm" type="primary" @click="sysDictKeyModify"
          >保存
          </lay-button
          >
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>
  </lay-container>
</template>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {
  SysDictKeyEntity,
  SysDictKeyVo,
  SysDictValueEntity,
  SysDictValueVo,
  SysDictVo
} from "../../../types/system/Dict";
import {PageQuery} from "../../../types/Common";
import {
  deleteSysDictValueList,
  enableSysDictValueList,
  findPageSysDictValue,
  findSysDictKeyList,
  flushCacheSysDictValue,
  saveSysDictKey,
  saveSysDictValue
} from "../../../api/system/Dict";

onMounted(async () => {
  selectedNode.value = {}
  await loadSysDictKeyList();
})
const expandKeys = ref<string[]>()
const sysDictKeyList = ref<Array<SysDictKeyEntity>>([])
const sysDictValueList = ref<Array<SysDictValueEntity>>()
const showLine = ref(false)
const selectedKey = ref('')
const selectedNode = ref<SysDictKeyEntity>({})
const isFold = ref(false)
const sysDictKeySearchQuery = ref<SysDictVo>({})
const sysDictValueSearchQuery = ref<SysDictVo>({})

function toReset() {
  sysDictValueSearchQuery.value = {}
}

function handleClick(node: any) {
  if (node) {
    sysDictValueSearchQuery.value.keyId = node.id
    selectedNode.value = node;
  }
  change()
}

function toDelete() {
  if (selectedKey.value == '') {
    layer.msg('您未选择字典，请先选择要删除的字典', {
      icon: 3,
      time: 2000
    })
    return
  }
  layer.confirm(
      '您将删除所选中的字典 [ ' + selectedNode.value.dictKey + ' ] ？',
      {
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
      }
  )
}

function flushCache() {
  let dictKey = selectedNode.value.dictKey;
  if (!dictKey || dictKey === "") {
    layer.msg('您未选择字典键，请先选择要删除的字典', {
      icon: 3,
      time: 1000
    })
    return
  }
  layer.confirm(
      '您将刷新字典值 [ ' + dictKey + ' ] ？',
      {
        title: '提示',
        btn: [
          {
            text: '确定',
            callback: (id: any) => {
              flushCacheSysDictValue(dictKey)
              layer.msg('刷新 [ ' + dictKey + ' ] 成功')
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
      }
  )
}

function toSearch() {
  pageQuery.current = 1
}

async function searchSysDictKey() {
  pageQuery.current = 1
  const {data} = await findSysDictKeyList(sysDictKeySearchQuery.value);
  sysDictKeyList.value = data;
  sysDictValueList.value = []
}

const dataSourceSex = ref([
  {
    id: '1',
    name: '男',
    nameValue: 1,
    sort: 1,
    joinTime: '2022-02-09',
    status: true
  },
  {
    id: '2',
    name: '女',
    nameValue: 2,
    sort: 2,
    joinTime: '2022-02-09',
    status: true
  }
])
const dataSourceOri = ref([
  {
    id: '1',
    name: '公司',
    nameValue: 1,
    sort: 1,
    joinTime: '2022-02-09'
  },
  {
    id: '2',
    name: '子公司',
    nameValue: 2,
    sort: 2,
    joinTime: '2022-02-09',
    status: true
  },
  {
    id: '3',
    name: '部门',
    nameValue: 3,
    sort: 3,
    joinTime: '2022-02-09',
    status: true
  },
  {
    id: '4',
    name: '小组',
    nameValue: 4,
    sort: 4,
    joinTime: '2022-02-09',
    status: true
  }
])
const loading = ref(false)
const selectedKeys = ref()
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10,
})
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '字典键', key: 'dictKey', width: '150px'},
  {title: '字典值名', key: 'valueName', width: '200px'},
  {title: '状态', key: 'status', width: '80px', customSlot: 'status'},
  {title: '排序', width: '80px', key: 'orderNum', sort: 'desc'},
  {title: '是否默认', width: '120px', key: 'isDefault', sort: 'desc', customSlot: 'isDefault'},
  {title: '创建时间', width: '160px', key: 'createTime', sort: 'desc'},
  {title: '备注', width: '160px', key: 'remark'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const change = () => {
  loading.value = true
  setTimeout(() => {
    loadSysDictValueList();
    loading.value = false
  }, 1000)
}

async function loadSysDictKeyList() {
  const {data} = await findSysDictKeyList(sysDictValueSearchQuery.value);
  sysDictKeyList.value = data;
}

async function loadSysDictValueList() {
  const {rows, total} = await findPageSysDictValue(sysDictValueSearchQuery.value, pageQuery);
  sysDictValueList.value = rows;
  pageQuery.total = total;
}

const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}

const editModalChangeStatus = (isChecked: boolean, row: any) => {
  sysDictKeyList.value.forEach((item) => {
    if (item.id === row.id) {
      layer.msg('Success', {icon: 1}, () => {
        item.status = isChecked ? '0' : '1';
      })
    }
  })
}
const remove = () => {
  layer.msg(selectedKeys.value, {area: '50%'})
}

const insertSysDictValueVo = ref<SysDictValueVo>({})
const modifySysDictValueVo = ref<SysDictValueVo>({})
const layFormRef11 = ref()
const showSysDictValueSaveModalFlag = ref(false)
const title = ref('新增')
const showSysDictValueSaveModal = (text: any, row: SysDictValueEntity) => {
  if (selectedNode.value.id) {
    title.value = text
    if (row) {
      // 编辑操作
      insertSysDictValueVo.value = {
        id: row.id,
        keyId: selectedNode.value.id,
        dictKey: selectedNode.value.dictKey,
        valueName: row.valueName,
        value: row.value,
        orderNum: row.orderNum,
        isDefault: row.isDefault,
        status: row.status,
        remark: row.remark,
        cssStyle: row.cssStyle
      }
    } else {
      // 新增操作
      insertSysDictValueVo.value = {
        keyId: selectedNode.value.id,
        dictKey: selectedNode.value.dictKey,
      }
    }
    showSysDictValueSaveModalFlag.value = !showSysDictValueSaveModalFlag.value
  }
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
    layer.msg('您未选择数据，请先选择要删除的字典项', {icon: 3, time: 2000})
    return
  }
  layer.confirm('您将删除所有选中的字典项？', {
    title: '提示',
    btn: [
      {
        text: '确定',
        callback: async (id: any) => {
          await deleteSysDictValueList(selectedKeys.value)
          await loadSysDictValueList()
          layer.close(id)
          layer.msg('您已成功删除')
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

function toEnable() {
  if (selectedKeys.value.length == 0) {
    layer.msg('您未选择数据，请先选择要启用/禁用的字典项', {icon: 3, time: 2000})
    return
  }
  layer.confirm('您将启用/禁用所有选中的字典项？', {
    title: '提示',
    btn: [
      {
        text: '确定',
        callback: async (id: any) => {
          await enableSysDictValueList(selectedKeys.value)
          await loadSysDictValueList()
          layer.close(id)
          layer.msg('启用/禁用成功')
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

function toAdd() {
  modalTitle.value = '新建字典'
  sysDictKeyInsertModal.value = true
}

function toEdit() {
  console.log(selectedKey.value)
  if (selectedKey.value) {
    modalTitle.value = '修改字典'
    sysDictKeyList.value.find(sysDictKey => {
      if (sysDictKey.id === selectedKey.value) {
        modifySysDictKey.value = sysDictKey;
      }
    });
    sysDictKeyModifyModal.value = true
  }
}

async function sysDictKeyInsert() {
  await saveSysDictKey(insertSysDictKey.value);
  layer.msg('保存成功！', {icon: 1, time: 1000})
  await loadSysDictKeyList();
  toCancel()
}

async function sysDictKeyModify() {
  await saveSysDictKey(modifySysDictKey.value);
  layer.msg('保存成功！', {icon: 1, time: 1000})
  await loadSysDictKeyList();
  toCancel()

}

async function sysDictValueInsert() {
  await saveSysDictValue(insertSysDictValueVo.value);
  layer.msg('保存成功！', {icon: 1, time: 1000})
  await loadSysDictValueList();
  toCancel()

}

function toCancel() {
  showSysDictValueSaveModalFlag.value = false
  sysDictKeyInsertModal.value = false
  sysDictKeyModifyModal.value = false
}

async function confirm(row: any) {
  let idList: string[] = [];
  idList.push(row.id);
  await deleteSysDictValueList(idList);
  await loadSysDictValueList()
  setTimeout(() => {
    layer.msg('您已成功删除')
  }, 1000)
}

async function enableConfirm(row: any) {
  let idList: string[] = [];
  idList.push(row.id);
  await enableSysDictValueList(idList);
  await loadSysDictValueList()
  setTimeout(() => {
    layer.msg('操作成功')
  }, 1000)
}

function cancel() {
  layer.msg('您已取消操作')
}

const insertSysDictKey = ref<SysDictKeyVo>({})
const modifySysDictKey = ref<SysDictKeyVo>({})
const editSysDictKey = ref<SysDictKeyVo>({})
const layFormRef22 = ref()
const sysDictKeyInsertModal = ref(false)
const sysDictKeyModifyModal = ref(false)
const modalTitle = ref('新建字典')
</script>

<style scoped>
.dictionary-box {
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
