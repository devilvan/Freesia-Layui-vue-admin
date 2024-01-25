<template>
  <lay-container fluid="true" class="role-box">
    <lay-card>
      <lay-form style="margin-top: 10px">
        <lay-row>
          <lay-col :md="5">
            <lay-form-item label="ID" label-width="80">
              <lay-input
                  v-model="searchQuery.id"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label-width="20">
              <lay-button
                  style="margin-left: 20px"
                  type="normal"
                  size="sm"
                  @click="toSearch"
              >
                查询
              </lay-button>
              <lay-button size="sm" @click="toReset"> 重置</lay-button>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <!-- table -->
    <div class="table-box">
      <lay-table
          class="table-style"
          :page="pageQuery"
          :columns="columns"
          :loading="loading"
          :default-toolbar="true"
          :data-source="dataSource"
          v-model:selected-keys="selectedKeys"
          @change="change"
          @sortChange="sortChange">
        <template #buildIn="{ row }">
          <!--          @change="changeBuildIn($event, row)"-->
          <lay-switch
              :model-value="row.buildIn"
          ></lay-switch>
        </template>
        <template v-slot:toolbar>
          <lay-button
              size="sm"
              type="primary"
              @click="changeConfigModalFlag('新增', null)">
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button
              size="xs"
              border="green"
              border-style="dashed"
              @click="changeConfigModalFlag('编辑', row)">编辑
          </lay-button>
          <lay-popconfirm
              content="确定要删除此配置吗?"
              @confirm="confirm(row)"
              @cancel="cancel">
            <lay-button size="xs" border="red" border-style="dashed">删除</lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="urlConfigModalShowFlag" :title="title" :area="['500px']">
      <div style="padding: 20px">
        <lay-form :model="urlConfigVo" ref="urlConfigFormRef">
          <lay-form-item label="ID" prop="id" :hidden="true" required>
            <lay-input v-model="urlConfigVo.id" disabled></lay-input>
          </lay-form-item>
          <lay-form-item label="配置标识" prop="code" required>
            <lay-input v-model="urlConfigVo.code"></lay-input>
          </lay-form-item>
          <lay-form-item label="网址" prop="url" required>
            <lay-input v-model="urlConfigVo.url" :allow-clear="true"></lay-input>
          </lay-form-item>
          <lay-form-item label="请求方式" prop="requestType">
            <lay-input v-model="urlConfigVo.requestType"></lay-input>
          </lay-form-item>
          <lay-form-item label="请求头信息" prop="header">
            <lay-input v-model="urlConfigVo.header"></lay-input>
          </lay-form-item>
          <lay-form-item label="参数信息" prop="param">
            <lay-input v-model="urlConfigVo.param"></lay-input>
          </lay-form-item>
          <lay-form-item label="内容形式" prop="contentType">
            <lay-input v-model="urlConfigVo.contentType"></lay-input>
          </lay-form-item>
          <lay-form-item label="是否系统内置" prop="buildIn">
            <lay-switch v-model="urlConfigVo.buildIn"></lay-switch>
          </lay-form-item>
          <lay-form-item label="备注" prop="remark">
            <lay-textarea
                allow-clear
                placeholder="请输入备注"
                v-model="urlConfigVo.remark"
            ></lay-textarea>
          </lay-form-item>

        </lay-form>
        <div style="width: 100%; text-align: center">
          <lay-button size="sm" type="primary" @click="toSubmit">保存</lay-button>
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
  name: "Url",
};
</script>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {deleteUrlConfig, findPageUrlConfig, saveUpdate} from "../../../api/common/Url";
import {UrlConfigEntity, UrlConfigVo} from "../../../types/common/Url";
import {TableResult} from "../../../types/Result";

/* INIT*/
onMounted(() => {
  loadDataSource()
})
const loadDataSource = () => {
  findPageUrlConfig(searchQuery.value, pageQuery).then((res: TableResult<UrlConfigEntity>) => {
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
/* INIT*/

/* VAR*/
const searchQuery = ref<UrlConfigVo>({})
const loading = ref(false)
const selectedKeys = ref()
const urlConfigVo = ref<UrlConfigVo>({
  buildIn: false
})
const urlConfigVoTemplate = ref<UrlConfigVo>({
  buildIn: false
})
const urlConfigFormRef = ref()
const urlConfigModalShowFlag = ref(false)

const title = ref('新增')
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '编号', width: '160px', key: 'id', fixed: 'left', sort: 'desc'},
  {title: '配置标识', width: '130px', key: 'id', fixed: 'left', sort: 'desc'},
  {title: '网址', width: '150px', key: 'url', sort: 'desc'},
  {title: '请求方式', width: '150px', key: 'requestType', sort: 'asc'},
  {title: '请求头信息', width: '100px', key: 'header', sort: 'desc'},
  {title: '请求参数', width: '100px', key: 'param', sort: 'desc'},
  {title: '系统内置', width: '40px', key: 'buildIn', customSlot: 'buildIn'},
  {title: '内容形式', width: '160px', key: 'contentType'},
  {title: '创建时间', width: '160px', key: 'createTime'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
/* VAR*/

/* FUNCTION*/
function toReset() {
  searchQuery.value = {}
}

function toSearch() {
  pageQuery.current = 1
  dataSource.value = []
  change()
}

const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 1000)
}
const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}
const dataSource = ref<Array<UrlConfigEntity>>()
const changeBuildIn = (isChecked: boolean, row: any) => {
  dataSource.value?.forEach((item: any) => {
    if (item.id === row.id) {
      layer.msg('Success', {icon: 1}, () => {
        item.configType = isChecked ? 'Y' : 'N'
      })
    }
  })
}
const remove = () => {
  layer.msg(selectedKeys.value, {area: '50%'})
}
const changeConfigModalFlag = (text: any, row: any) => {
  urlConfigVo.value = {}
  title.value = text
  if (row != null) {
    urlConfigVo.value = {...row}
  } else {
    urlConfigVo.value.buildIn = false
  }
  urlConfigModalShowFlag.value = !urlConfigModalShowFlag.value
}
const submit11 = function () {
  urlConfigFormRef.value.validate((isValidate: any, model: any, errors: any) => {
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
  urlConfigFormRef.value.clearValidate()
}
// 重置表单
const reset11 = function () {
  urlConfigFormRef.value.reset()
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
  saveUpdate(urlConfigVo.value).then((res: any) => {
    if (res.code === 200) {
      loadDataSource();
      layer.msg('保存成功！', {icon: 1, time: 1000})
      urlConfigModalShowFlag.value = false
    } else {
      layer.confirm(res.msg, {icon: 2})
    }
  })
}

function toCancel() {
  urlConfigModalShowFlag.value = false
}

function confirm(row: any) {
  if (row && row.buildIn) {
    layer.msg('系统内置参数无法删除！')
    return;
  } else {
    deleteUrlConfig(row.id, row.code).then((res: any) => {
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

/* FUNCTION*/
</script>

<style scoped>
.role-box {
  width: calc(100vw - 220px);
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
</style>
