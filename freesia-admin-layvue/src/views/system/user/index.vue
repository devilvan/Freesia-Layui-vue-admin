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
                  v-permission="[$MENU_PERMISSION.SYSTEM_USER_INDEX]"
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
              :model-value="row.accountStatus === '1'"
              @change="changeStatus($event, row)"
          ></lay-switch>
        </template>
        <template #avatar="{ row }">
          <lay-avatar :src="$SRC_ASSETS + row.avatar" @click="preview($SRC_ASSETS + row.avatar)"></lay-avatar>
        </template>
        <template #gender="{ row }">
          <dict-tag :options="sysGenderList" :value="row.gender"/>
        </template>
        <template #remark="{ row }">
          <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
            <div class="oneRow">{{ row.remark }}</div>
          </lay-tooltip>
        </template>

        <template v-slot:toolbar>
          <lay-button size="sm" type="primary" @click="changeAddModalShowFlag(Operate.ADD)"
                      v-permission="[$MENU_PERMISSION.SYSTEM_USER_ADD]">
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove" v-permission="[$MENU_PERMISSION.SYSTEM_USER_DELETE]">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
          <lay-button size="sm" @click="toImport">
            <lay-icon class="layui-icon-upload-drag"
                      v-permission="[$MENU_PERMISSION.SYSTEM_USER_IMPORT_USER]"></lay-icon>
            导入
          </lay-button>
          <lay-button size="sm" type="normal" @click="assignRole"
                      v-permission="[$MENU_PERMISSION.SYSTEM_USER_ASSIGN_ROLE]">
            分配角色
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button size="xs" type="primary" @click="changeAddModalShowFlag(Operate.EDIT, row)"
                      v-permission="[$MENU_PERMISSION.SYSTEM_USER_EDIT]">编辑
          </lay-button>
          <lay-popconfirm content="确定要删除此用户吗?" @confirm="confirm" @cancel="cancel">
            <lay-button size="xs" border="red" border-style="dashed"
                        v-permission="[$MENU_PERMISSION.SYSTEM_USER_DELETE]">删除
            </lay-button>
          </lay-popconfirm>
          <lay-button size="xs" border="blue" border-style="dashed" @click="assignRoleById(row.id)"
                      v-permission="[$MENU_PERMISSION.SYSTEM_USER_ASSIGN_ROLE]">分配角色
          </lay-button>
        </template>
      </lay-table>
    </div>
    <lay-layer v-model="addModalShowFlag" :title="title" :area="['500px', '600px']">
      <div style="padding: 20px">
        <lay-form :model="sysUserVo" ref="sysUserVoFormRef" required>
          <lay-form-item label="昵称" prop="nickName">
            <lay-input v-model="sysUserVo.nickName"></lay-input>
          </lay-form-item>

          <lay-form-item label="性别" prop="gender">
            <lay-select
                v-model="sysUserVo.gender"
                style="width: 100%"
                placeholder="请选择性别"
                :options="sysGenderListSelect"
                :items="sysGenderListSelect"
            >
            </lay-select>
          </lay-form-item>
          <lay-form-item label="邮箱" prop="email">
            <lay-input v-model="sysUserVo.email"></lay-input>
          </lay-form-item>
          <lay-form-item label="手机号" prop="telNo">
            <lay-input v-model="sysUserVo.telNo"></lay-input>
          </lay-form-item>
          <lay-form-item label="描述" prop="remark">
            <lay-textarea
                placeholder="请输入描述"
                v-model="sysUserVo.remark"
                :show-count="true"
                :maxlength="127"
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
        :area="['380px', '500px']"
    >
      <lay-upload
          style="margin: 60px"
          :url="userImportRoute"
          v-model="fileList"
          field="file"
          acceptMime="application/vnd.ms-excel,
          application/vnd.ms-excel.sheet.binary.macroenabled.12,
          application/vnd.ms-excel.sheet.macroenabled.12,
          application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          :auto="false"
          :drag="true"
      >
        <template #preview>
          <div v-if="fileList.length > 0" v-for="(file, index) in fileList">
            {{ index + ". " + file.name }}
          </div>
        </template>
      </lay-upload>
      <div style="width: 100%; text-align: center">
        只能上传小于10MB的文件
      </div>
      <div style="width: 100%;margin-top: 20px; text-align: center">
        <lay-button size="sm" type="primary" @click="toUpload()">上传</lay-button>
      </div>
    </lay-layer>
  </lay-container>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "User",
};
</script>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "../../../types/Common";
import {FindPageSysUserListEntity, SysUserVo} from "../../../types/system/User";
import {findEditUserById, findPageSysUserList, saveUserInfo, userImport} from "../../../api/system/User";
import {Constants, loadSysDictValue, sysDictValueSelect} from "../../../util/UDict";
import {SysDictValueEntity} from "../../../types/system/Dict";
import router from "../../../router";
import app from "../../../main";
import {Operate} from "../../../types/Constants";
import {useCryptStore} from "../../../store/crypt";

/* INIT*/
const $router = router;
const $crypt = useCryptStore();
const userImportRoute = import.meta.env.VITE_APP_BASE_URL + "/api/sysUserController/userImport"
const avatarPathGlob = import.meta.glob('@/assets/avatar/*')
onMounted(async () => {
  sysGenderList.value = await loadSysDictValue(Constants.SYS_GENDER)
  sysGenderListSelect.value = await sysDictValueSelect(sysGenderList.value)
  for (const path in avatarPathGlob) {
    avatarPath.push(path);
  }
  change()
})
/* INIT*/
/* VAR*/
const avatarPath: any[] = [];
const uploadAvatar = ref();
const searchQuery = ref<SysUserVo>({})
const visibleImport = ref(false)
const fileList = ref([])
const sysGenderList = ref<Array<SysDictValueEntity>>()
const sysGenderListSelect = ref<Array<SysDictValueEntity>>()
const loading = ref(false)
const selectedKeys = ref()
const sysUserVo = ref<any>({})
const sysUserVoFormRef = ref()
const addModalShowFlag = ref(false)
const title = ref('新增')
const pageQuery: PageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', width: '60px', type: 'checkbox', fixed: 'left'},
  {title: '用户名', width: '80px', key: 'userName'},
  {title: '昵称', width: '80px', key: 'nickName'},
  {title: '头像', width: '50px', key: 'avatar', customSlot: 'avatar'},
  {title: '状态', width: '80px', key: 'accountStatus', customSlot: 'status'},
  {title: '部门', width: '120px', key: 'deptName'},
  {title: '邮箱', width: '150px', key: 'email'},
  {title: '性别', width: '80px', key: 'gender', customSlot: 'gender'},
  {title: '备注', width: '120px', key: 'remark', customSlot: 'remark'},
  {
    title: '操作',
    width: '120px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
/* VAR*/
/*FUNCTION*/
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

const change = () => {
  loading.value = true
  // setTimeout(() => {
  loadDataSource()
  loading.value = false
  // }, 1000)
}
const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}
const dataSource = ref<Array<FindPageSysUserListEntity>>();

const changeStatus = (isChecked: boolean, row: any) => {
  dataSource.value?.forEach((item) => {
    if (item.id === row.id) {
      layer.msg('Success', {icon: 1}, () => {
        item.accountStatus = isChecked ? '1' : '0'
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
const changeAddModalShowFlag = (operate: any, row?: any) => {
  title.value = Operate.ADD === operate ? '新增' : Operate.EDIT === operate ? '编辑' : '标题'
  if (Operate.EDIT === operate) {
    findEditUserById(row.id).then((res: any) => {
      if (res.code === 200) {
        $crypt.decryptAes(res.data).then((decryptAes: any) => {
          let decrypt = JSON.parse(decryptAes);
          sysUserVo.value = {...decrypt};
        })
      }
    })
  }
  addModalShowFlag.value = !addModalShowFlag.value
}
const submit11 = function () {
  sysUserVoFormRef.value.validate((isValidate: any, model: any, errors: any) => {
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
  sysUserVoFormRef.value.clearValidate()
}
// 重置表单
const reset11 = function () {
  sysUserVoFormRef.value.reset()
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
  saveUserInfo($crypt.encryptAes(sysUserVo.value)).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg, {icon: 1})
      change()
      addModalShowFlag.value = !addModalShowFlag.value
    }
  })
}

function toCancel() {
  addModalShowFlag.value = false
}

function confirm() {
  layer.msg('您已成功删除')
}

function cancel() {
  layer.msg('您已取消操作')
}

function toUpload() {
  uploadAvatar.value = randomUserAvatar();
  userImport(fileList.value, uploadAvatar.value).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg, {icon: 1})
      visibleImport.value = !visibleImport.value
    }
  })
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

function assignRole() {
  if (!selectedKeys.value || selectedKeys.value.length === 0 || selectedKeys.value.length > 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  $router.push("/system/user/assignRole/" + selectedKeys.value[0])
}

function assignRoleById(id: any) {
  $router.push("/system/user/assignRole/" + id)
}

function randomUserAvatar() {
  let index = Math.floor(Math.random() * avatarPath.length);
  return avatarPath[index].replace(app.config.globalProperties.$SRC_ASSETS, '');
}

/*FUNCTION*/


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
