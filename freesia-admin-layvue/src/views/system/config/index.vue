<template>
  <lay-container :fluid="true">
    <lay-card>
      <lay-form label-position="top">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="角色名称" label-width="80">
              <lay-input
                  v-model="searchQuery.roleName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 100%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="角色标识" label-width="80">
              <lay-input
                  v-model="searchQuery.identifying"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 100%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="备注" label-width="80">
              <lay-input
                  v-model="searchQuery.mark"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 100%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <!-- table -->
    <div>
      <lay-table
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
          <lay-button type="normal" size="sm" @click="toSearch">查询</lay-button>
          <lay-button size="sm" @click="toReset">重置</lay-button>
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

    <lay-layer v-model="configModalShowFlag" :title="title" :area="['500px']">
      <div style="padding: 20px">
        <lay-form :model="configVo" ref="configFormRef">
          <lay-form-item label="参数名称" prop="configName" required>
            <lay-input v-model="configVo.configName"></lay-input>
          </lay-form-item>
          <lay-form-item label="参数键" prop="configKey" required>
            <lay-input v-model="configVo.configKey" :disabled="configVo.id"></lay-input>
          </lay-form-item>
          <lay-form-item label="参数值" prop="configValue" required>
            <lay-input v-model="configVo.configValue"></lay-input>
          </lay-form-item>
          <lay-form-item label="是否系统内置" prop="buildIn">
            <lay-switch v-model="configVo.buildIn"></lay-switch>
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
  name: "Config",
};
</script>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "@/types/Common";
import {deleteConfig, findPageSysConfig, saveConfig} from "@/api/system/Config";
import {SysConfigEntity, SysConfigVo} from "@/types/system/Config";

/* INIT*/
onMounted(() => {
  loadDataSource()
})
const loadDataSource = () => {
  findPageSysConfig(searchQuery.value, pageQuery).then((res: any) => {
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
const searchQuery = ref<SysConfigVo>({})
const loading = ref(false)
const selectedKeys = ref()
const configVo = ref<SysConfigVo>({
  buildIn: false
})
const configVoTemplate = ref<SysConfigVo>({
  buildIn: false
})
const configFormRef = ref()
const configModalShowFlag = ref(false)

const title = ref('新增')
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '参数名称', width: '150px', key: 'configName'},
  {title: '参数键', width: '150px', key: 'configKey'},
  {title: '参数值', width: '100px', key: 'configValue'},
  {title: '系统内置', width: '40px', key: 'buildIn', customSlot: 'buildIn'},
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
const dataSource = ref<Array<SysConfigEntity>>()
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
  configVo.value = {}
  title.value = text
  if (row != null) {
    configVo.value = {...row}
  } else {
    configVo.value.buildIn = false
  }
  configModalShowFlag.value = !configModalShowFlag.value
}
const submit11 = function () {
  configFormRef.value.validate((isValidate: any, model: any, errors: any) => {
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
  configFormRef.value.clearValidate()
}
// 重置表单
const reset11 = function () {
  configFormRef.value.reset()
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
  saveConfig(configVo.value).then((res: any) => {
    if (res.code === 200) {
      loadDataSource();
      layer.msg('保存成功！', {icon: 1, time: 1000})
      configModalShowFlag.value = false
    } else {
      layer.confirm(res.msg, {icon: 2})
    }
  })
}

function toCancel() {
  configModalShowFlag.value = false
}

function confirm(row: any) {
  if (row && row.buildIn) {
    layer.msg('系统内置参数无法删除！')
    return;
  } else {
    deleteConfig(row.configKey).then((res: any) => {
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