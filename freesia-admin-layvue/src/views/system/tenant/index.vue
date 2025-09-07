<template>
  <lay-container :fluid="true">
    <lay-card shadow="hover">
      <lay-form label-position="top" @keydown.enter.prevent="toSearch">
        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="租户编码" label-width="80">
              <lay-input
                  v-model="searchQuery.code"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 100%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="租户名称" label-width="80">
              <lay-input
                  v-model="searchQuery.name"
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
        <template #status="{ row }">
          <lay-switch
              :model-value="row.status"
          ></lay-switch>
        </template>
        <template #tenantType="{ row }">
          <dict-tag :options="sysTenantTypeList" :value="row.type" :showValue="true"/>
        </template>
        <template v-slot:toolbar>
          <lay-button type="normal" size="sm" @click="toSearch">查询</lay-button>
          <lay-button size="sm" @click="toReset"> 重置</lay-button>
          <lay-button
              size="sm"
              type="primary"
              @click="changeTenantModalFlag(Operate.ADD, null)"
              v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_ADD]"
          >
            <lay-icon class="layui-icon-addition"></lay-icon>
            新增
          </lay-button>
          <lay-button size="sm" @click="toRemove" v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_EDIT]">
            <lay-icon class="layui-icon-delete"></lay-icon>
            删除
          </lay-button>
          <lay-button size="sm" @click="assignTenantUser()" v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_ASSIGN_USER]">
            <lay-icon class="layui-icon-addition"></lay-icon>
            分配用户
          </lay-button>
        </template>
        <template v-slot:operator="{ row }">
          <lay-button
              size="xs"
              border="green"
              border-style="dashed"
              @click="changeTenantModalFlag(Operate.EDIT, row)"
              v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_EDIT]">编辑
          </lay-button>
          <lay-popconfirm
              content="确定要删除此租户吗?"
              @confirm="confirm(row)"
              @cancel="cancel">
            <lay-button size="xs" border="red" border-style="dashed"
                        v-permission="[$MENU_PERMISSION.SYSTEM_TENANT_DELETE]">删除
            </lay-button>
          </lay-popconfirm>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="sysTenantModalShowFlag" :title="title" :area="['1200px']">
      <div style="padding: 20px" @keydown.enter.prevent="toSubmit" @keydown.esc.prevent="toCancel">
        <lay-form :model="sysTenantVo" ref="sysTenantFormRef" label-position="top">
          <lay-row space="20">
            <lay-col :md="6">
              <lay-form-item label="租户编码" prop="code" required>
                <lay-input v-model="sysTenantVo.code" :allow-clear="true"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="联系人姓名" prop="contactName" required>
                <lay-input v-model="sysTenantVo.contactName"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="营业时间" prop="businessHoursFrom">
                <lay-date-picker type="yearmonth" v-model="sysTenantVo.businessHoursFrom"
                                 format="YYYY-MM"></lay-date-picker>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="租户名称" prop="name" required>
                <lay-input v-model="sysTenantVo.name" :allow-clear="true"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="联系人电话" prop="contactTel" required>
                <lay-input v-model="sysTenantVo.contactTel"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="租户类型" prop="type" required>
                <lay-select
                    style="width: 100%"
                    size="sm"
                    v-model="sysTenantVo.type"
                    placeholder="请选择"
                >
                  <template v-for="(sysTenantType, index) in sysTenantTypeSelectList" :key="index">
                    <lay-select-option :value="sysTenantType.value" :label="sysTenantType.label"></lay-select-option>
                  </template>
                </lay-select>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="联系人邮箱" prop="contactEmail" required>
                <lay-input v-model="sysTenantVo.contactEmail"></lay-input>
              </lay-form-item>
            </lay-col>

            <lay-col :md="6">
              <lay-form-item label="租户状态" prop="status" required>
                <lay-switch v-model="sysTenantVo.status"></lay-switch>
              </lay-form-item>
            </lay-col>
            <lay-col :md="6">
              <lay-form-item label="租户地址" prop="address" required>
                <lay-input v-model="sysTenantVo.address"></lay-input>
              </lay-form-item>
            </lay-col>
          </lay-row>
          <lay-row space="20">
            <lay-col :md="6">
              <lay-form-item label="租户备注" prop="remark">
                <lay-textarea
                    allow-clear
                    placeholder="请输入租户备注"
                    v-model="sysTenantVo.remark"
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="toSubmit">保存</lay-button>
          <lay-button size="sm" type="primary" @click="toReset()">重置</lay-button>
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
  name: "Tenant",
};
</script>
<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {PageQuery} from "@/types/Common";
import {TableResult} from "@/types/Result";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {SysDictValueEntity} from "@/types/system/Dict";
import {SysTenantEntity, SysTenantVo} from "@/types/system/Tenant";
import {deleteSysTenant, findPageSysTenant, findSysTenant, saveUpdate} from "@/api/system/Tenant";
import router from "@/router";
import {Operate} from "@/types/Constants";

/* INIT*/
onMounted(async () => {
  sysTenantTypeList.value = await loadSysDictValue(Constants.SYS_TENANT_TYPE)
  sysTenantTypeSelectList.value = await sysDictValueSelect(sysTenantTypeList.value);
  loadDataSource()
})
const loadDataSource = () => {
  findPageSysTenant(searchQuery.value, pageQuery).then((res: TableResult<SysTenantEntity>) => {
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
const $router = router;
const sysTenantTypeList = ref<Array<SysDictValueEntity>>([])
const sysTenantTypeSelectList = ref<Array>([])
const searchQuery = ref<SysTenantEntity>({})
const loading = ref(false)
const selectedKeys = ref<Array<string>>([])
const sysTenantVo = ref<SysTenantVo>({
  status: true
})
const sysTenantVoTemplate = ref<SysTenantVo>({
  status: true
})
const sysTenantFormRef = ref()
const sysTenantModalShowFlag = ref(false)
const dataSource = ref<Array<SysTenantEntity>>()
const title = ref('新增')
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '租户编码', width: '130px', key: 'code', fixed: 'left'},
  {title: '租户名称', width: '130px', key: 'name'},
  {title: '租户类型', width: '150px', key: 'type', sort: 'desc', customSlot: 'tenantType'},
  {title: '租户状态', width: '100px', key: 'status', customSlot: 'status'},
  {title: '联系人姓名', width: '150px', key: 'contactName', sort: 'desc'},
  {title: '联系人电话', width: '140px', key: 'contactTel'},
  {title: '联系人邮箱', width: '160px', key: 'contactEmail'},
  {title: '租户地址', width: '160px', key: 'address'},
  {title: '租户备注', width: '150px', key: 'remark', sort: 'desc'},
  {title: '营业时间', width: '160px', key: 'businessHoursFrom'},
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
  sysTenantVo.value = {
    status: false
  }
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
const remove = () => {
  layer.msg(selectedKeys.value, {area: '50%'})
}
const changeTenantModalFlag = (text: any, row: any) => {
  title.value = Operate.ADD === text ? "新增" : Operate.EDIT === text ? "编辑" : "";
  if (row != null) {
    sysTenantVo.value = {...row}
  }
  // 编辑下查询包含敏感数据字段
  if (Operate.EDIT === text) {
    findSysTenant({
      id: row.id
    }).then((res: any) => {
      if (res.code === 200) {
        sysTenantVo.value = res.data;
      }
    })
  }
  sysTenantModalShowFlag.value = !sysTenantModalShowFlag.value
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
          deleteSysTenant(selectedKeys.value).then((res: any) => {
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

function toSubmit() {
  sysTenantFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveUpdate(sysTenantVo.value).then((res: any) => {
        if (res.code === 200) {
          loadDataSource();
          layer.msg('保存成功！', {icon: 1, time: 1000})
          sysTenantVo.value = {};
          sysTenantModalShowFlag.value = false
        }
      })
    }
  })
}

function toCancel() {
  sysTenantVo.value = {}
  sysTenantModalShowFlag.value = false
}

function confirm(row: any) {
  if (row && row.buildIn) {
    layer.msg('系统内置参数无法删除！')
    return;
  } else {
    deleteSysTenant([row.id]).then((res: any) => {
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

function assignTenantUser() {
  if (!selectedKeys.value || selectedKeys.value.length === 0 || selectedKeys.value.length > 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  $router.push("/system/tenant/assignUser/" + selectedKeys.value[0])
}

function assignTenantRole() {
  if (!selectedKeys.value || selectedKeys.value.length === 0 || selectedKeys.value.length > 1) {
    layer.msg("请选择1条数据", {icon: 3})
    return;
  }
  $router.push("/system/tenant/assignRole/" + selectedKeys.value[0])
}

/* FUNCTION*/
</script>
