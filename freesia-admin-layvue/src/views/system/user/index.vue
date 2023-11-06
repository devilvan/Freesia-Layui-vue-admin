<template>
  <lay-container fluid="true" class="user-box">
    <lay-card>
      <lay-form style="margin-top: 10px">
        <lay-row>
          <lay-col :md="5">
            <lay-form-item label="用户名" label-width="80">
              <lay-input
                  v-model="searchQuery.userName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label="用户昵称" label-width="80">
              <lay-input
                  v-model="searchQuery.nickName"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label="性别" label-width="80">
              <lay-select
                  class="search-input"
                  size="sm"
                  v-model="searchQuery.gender"
                  :allow-clear="true"
                  placeholder="请选择"
              >
                <template v-for="(sysMenuType, index) in sysGenderList" :key="index">
                  <lay-select-option :value="sysMenuType.value" :label="sysMenuType.label"></lay-select-option>
                </template>
              </lay-select>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label-width="20">
              <lay-button
                  style="margin-left: 20px"
                  type="primary"
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
          @sortChange="sortChange"
      >
        <template #status="{ row }">
          <lay-switch
              :model-value="row.accountStatus === '0'"
              @change="changeStatus($event, row)"
          ></lay-switch>
        </template>
        <template #avatar="{ row }">
          <lay-avatar :src="$SRC_ASSETS + row.avatar" @click="preview($SRC_ASSETS + row.avatar)"></lay-avatar>
        </template>
        <template #gender="{ row }">
          <dict-tag :options="sysGenderList" :value="row.gender"/>
        </template>
        <template v-slot:toolbar>
          <lay-button size="sm" type="primary" @click="changeVisible11('新增')">
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
          <lay-button size="sm" @click="toImport">
            <lay-icon class="layui-icon-upload-drag"></lay-icon>
            导入
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button size="xs" type="primary" @click="changeVisible11('编辑', row)">编辑</lay-button>
          <lay-popconfirm content="确定要删除此用户吗?" @confirm="confirm" @cancel="cancel">
            <lay-button size="xs" border="red" border-style="dashed">删除</lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="visible11" :title="title" :area="['500px', '550px']">
      <div style="padding: 20px">
        <lay-form :model="model11" ref="layFormRef11" required>
          <lay-form-item label="姓名" prop="name">
            <lay-input v-model="model11.name"></lay-input>
          </lay-form-item>
          <lay-form-item label="年龄" prop="age">
            <lay-input v-model="model11.age"></lay-input>
          </lay-form-item>
          <lay-form-item label="性别" prop="sex">
            <lay-select v-model="model11.sex" style="width: 100%">
              <lay-select-option value="男" label="男"></lay-select-option>
              <lay-select-option value="女" label="女"></lay-select-option>
            </lay-select>
          </lay-form-item>
          <lay-form-item label="城市" prop="city">
            <lay-input v-model="model11.city"></lay-input>
          </lay-form-item>
          <lay-form-item label="email" prop="email">
            <lay-input v-model="model11.email"></lay-input>
          </lay-form-item>
          <lay-form-item label="描述" prop="remark">
            <lay-textarea
                placeholder="请输入描述"
                v-model="model11.remark"
            ></lay-textarea>
          </lay-form-item>
        </lay-form>
        <div style="width: 100%; text-align: center">
          <lay-button size="sm" type="primary" @click="toSubmit">保存</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>
    <lay-layer
        v-model="visibleImport"
        title="导入用户"
        :area="['380px', '380px']"
    >
      <lay-upload
          :beforeUpload="beforeUpload10"
          style="margin: 60px"
          url="https://www.mocky.io/v2/5cc8019d300000980a055e76"
          v-model="file1"
          field="file"
          :auto="false"
          :drag="true"
      >
        <template #preview>
          {{ file1[0]?.name }}
        </template>
      </lay-upload>
      <div style="width: 100%; text-align: center">
        只能上传小于1000KB的文件
      </div>
    </lay-layer>
  </lay-container>
</template>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {FindPageSysUserListEntity, SysUserVo} from "../../../types/system/User";
import {findPageSysUserList} from "../../../api/system/User";
import {Constants, loadSysDictValue} from "../../../util/UDict";
import {SysDictValueEntity} from "../../../types/system/Dict";

onMounted(async () => {
  sysGenderList.value = await loadSysDictValue(Constants.SYS_GENDER)
  change()
})

const searchQuery = ref<SysUserVo>({})

const visibleImport = ref(false)
const file1 = ref<any>([])
const sysGenderList = ref<Array<SysDictValueEntity>>()

function toImport() {
  visibleImport.value = true
}

function toReset() {
  searchQuery.value = {}
}

function toSearch() {
  pageQuery.current = 1
  change()
}

const loading = ref(false)
const selectedKeys = ref<string[]>([])
const pageQuery: PageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', width: '60px', type: 'checkbox', fixed: 'left'},
  {title: '编号', width: '130px', key: 'id', fixed: 'left', sort: 'desc'},
  {title: '头像', width: '50px', key: 'avatar', customSlot: 'avatar'},
  {title: '用户名', width: '80px', key: 'userName', sort: 'desc'},
  {title: '昵称', width: '80px', key: 'nickName', sort: 'desc'},
  {title: '状态', width: '80px', key: 'accountStatus', customSlot: 'status'},
  {title: '部门', width: '120px', key: 'deptName'},
  {title: '邮箱', width: '150px', key: 'email'},
  {title: '性别', width: '80px', key: 'gender', customSlot: 'gender'},
  {title: '时间', width: '130px', key: 'createTime'},
  {title: '签名', width: '120px', key: 'remark'},
  {
    title: '操作',
    width: '120px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
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
const dataSource = ref<Array<FindPageSysUserListEntity>>();

const changeStatus = (isChecked: boolean, row: any) => {
  dataSource.value?.forEach((item) => {
    if (item.id === row.id) {
      layer.msg('Success', {icon: 1}, () => {
        item.accountStatus = isChecked ? '0' : '1'
      })
    }
  })
}

const remove = () => {
  layer.msg(JSON.stringify(selectedKeys.value), {area: '50%'})
}

const loadDataSource = () => {
  findPageSysUserList(searchQuery.value, pageQuery).then((res: any) => {
    if (res.code == 200) {
      pageQuery.total = res.total;
      dataSource.value = res.rows
    }
  })
}
const model11 = ref<any>({})
const layFormRef11 = ref()
const visible11 = ref(false)
const title = ref('新增')
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

const beforeUpload10 = (file: File) => {
  var isOver = false
  if (file.size > 1000) {
    isOver = true
    layer.msg(`file size over 1000 KB`, {icon: 2})
  }
  return new Promise((resolver) => resolver(true))
}

/**
 * 预览图片
 */
function preview(path: any) {
  let option = {
    imgList: [{src: path, alt: 'Do you like what you see?'}]
  };
  layer.photos(option)
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
</style>
