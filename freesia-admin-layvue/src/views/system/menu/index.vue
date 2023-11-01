<template>
  <lay-container fluid="true" class="menu-box">
    <lay-card>
      <lay-form style="margin-top: 10px">
        <lay-row>
          <lay-col :md="5">
            <lay-form-item label="菜单名称" label-width="80">
              <lay-input
                  v-model="searchQuery.name"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label="菜单地址" label-width="80">
              <lay-input
                  v-model="searchQuery.address"
                  placeholder="请输入"
                  size="sm"
                  :allow-clear="true"
                  style="width: 98%"
              ></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="5">
            <lay-form-item label="权限标识" label-width="80">
              <lay-input
                  v-model="searchQuery.identifying"
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
    <div class="table-box" style="width: 1457px">
      <lay-table
          ref="tableRef"
          :height="`570px`"
          :loading="loading"
          :columns="columns"
          children-column-name="children"
          :data-source="dataSource"
          :default-toolbar="true"
          :default-expand-all="expandMenuFlag"
          :expand-index="0"
          :resize="true"
      >
        <template #toolbar>
          <lay-button size="sm" type="primary" @click="changeSaveMenuVoModalFlag(MenuType.DIR,null)">新建目录</lay-button>
          <lay-button size="sm" type="normal" @click="changeSaveMenuVoModalFlag(MenuType.MENU, null)">新建菜单</lay-button>
          <lay-button size="sm" type="warm" @click="changeSaveMenuVoModalFlag(MenuType.BUTTON, null)">新建按钮</lay-button>
          <lay-button size="sm" type="danger" @click="changeSaveMenuVoModalFlag(MenuType.LINK, null)">新建链接</lay-button>
          <lay-button size="sm" @click="expandMenu(true)">展开全部</lay-button>
          <lay-button size="sm" @click="expandMenu(false)">折叠全部</lay-button>
        </template>
        <template #menuName="{ row }">
          <lay-icon :class="row.icon"></lay-icon> &nbsp;&nbsp;
          {{ row.menuName }}
        </template>
        <template #path="{ row }">
          <lay-tooltip
              :visible="false"
              trigger="hover"
              :content="row.path"
          >
            <div>{{ row.path }}</div>
          </lay-tooltip>
        </template>
        <template #component="{ row }">
          <lay-tooltip
              :visible="false"
              trigger="hover"
              :content="row.component"
          >
            <div>{{ row.component }}</div>
          </lay-tooltip>
        </template>
        <template #perms="{ row }">
          <lay-tooltip
              :visible="false"
              trigger="hover"
              :content="row.perms"
          >
            <div>{{ row.perms }}</div>
          </lay-tooltip>
        </template>
        <template #option="{ row }">
          <lay-button
              @click="changeVisible11(row.menuType, row)"
              size="xs"
              border="green"
              border-style="dashed"
          >
            修改
          </lay-button>
          <lay-button
              @click="toRemove(row)"
              size="xs"
              border="red"
              border-style="dashed"
          >
            删除
          </lay-button>
        </template>
        <template #menuType="{ row }">
          <div v-if="row.isFrame === '1'">
            <dict-tag :options="sysMenuTypeList" :value="row.menuType"/>
          </div>
          <div v-else>
            <dict-tag :options="sysMenuTypeList" :value="row.menuType" :isFrame="true"/>
          </div>
        </template>
        <template #status="{ row }">
          <div v-show="row.status === '0'">
            <lay-tag color="#2dc570" variant="light">是</lay-tag>
          </div>
          <div v-show="row.status === '1'">
            <lay-tag color="#F5319D" variant="light">否</lay-tag>
          </div>
        </template>
        <template #remark="{ row }">
          <lay-tooltip
              :visible="false"
              trigger="hover"
              :content="row.remark"
          >
            <div>{{ row.remark }}</div>
          </lay-tooltip>
        </template>
      </lay-table>
    </div>

    <lay-layer v-model="saveDirModalFlag" :title="title">
      <div style="padding: 20px">
        <lay-form :model="sysDirVo" ref="saveDirFormRef">
          <lay-row>
            <lay-col md="12">
              <lay-form-item label="目录名称" prop="menuName" required>
                <lay-input v-model="sysDirVo.menuName"></lay-input>
              </lay-form-item>
              <lay-form-item label="路由路径" prop="path" required>
                <lay-input v-model="sysDirVo.path" placeholder="例如：workspace"></lay-input>
              </lay-form-item>
              <lay-form-item label="图标" prop="icon">
                <lay-input v-model="sysDirVo.icon"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="12">
              <lay-form-item label="排序" prop="orderNum" required>
                <lay-input-number
                    style="width: 100%"
                    v-model="sysDirVo.orderNum"
                    position="right"
                    :min="0"
                    :step="10"
                ></lay-input-number>
              </lay-form-item>
              <lay-form-item label="是否显示" prop="status" required>
                <lay-select v-model="sysDirVo.status" :options="isShowOptions" :items="isShowOptions"
                            style="width: 100%">
                </lay-select>
              </lay-form-item>
            </lay-col>
            <lay-form-item label="备注" prop="remark" required>
              <lay-textarea v-model="sysDirVo.remark" :allow-clear="true" show-count :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-row>
        </lay-form>
        <div style="width: 97%; text-align: right">
          <lay-button size="sm" type="primary" @click="saveMenuSubmit(MenuType.DIR)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="resetModal(MenuType.DIR)">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="saveMenuModalFlag" :title="title">
      <div style="padding: 20px">
        <lay-form :model="sysMenuVo" ref="saveMenuFormRef">
          <lay-row>
            <lay-col md="12">
              <lay-form-item label="父目录" prop="parentId" required>
                <lay-tree-select v-model="sysMenuVo.parentId" :data="treeMenuSelectList"
                                 :allow-clear="true" @change="selectParentById(MenuType.MENU)"></lay-tree-select>
              </lay-form-item>
              <lay-form-item label="菜单名称" prop="menuName" required>
                <lay-input v-model="sysMenuVo.menuName"></lay-input>
              </lay-form-item>
              <lay-form-item label="路由路径" prop="path" required>
                <lay-input v-model="sysMenuVo.path" placeholder="例如：workspace" @input="pathInputEvent($event)">
                </lay-input>
              </lay-form-item>
              <lay-form-item label="图标" prop="icon">
                <lay-input v-model="sysMenuVo.icon"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="12">
              <lay-form-item label="排序" prop="orderNum" required>
                <lay-input-number
                    style="width: 100%"
                    v-model="sysMenuVo.orderNum"
                    position="right"
                    :min="0"
                    :step="10"
                ></lay-input-number>
              </lay-form-item>
              <lay-form-item label="是否显示" prop="status" required>
                <lay-select v-model="sysMenuVo.status" :options="isShowOptions" :items="isShowOptions"
                            style="width: 100%">
                </lay-select>
              </lay-form-item>
              <lay-form-item label="组件路径" prop="component" required>
                <lay-input v-model="sysMenuVo.component" placeholder="例如：system/menu/index">
                  <template #prepend="{disabled}">
                    {{ prepend }}
                  </template>
                </lay-input>
              </lay-form-item>
            </lay-col>
            <lay-form-item label="备注" prop="remark" required>
              <lay-textarea v-model="sysMenuVo.remark" :allow-clear="true" show-count :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-row>
        </lay-form>
        <div style="width: 97%; text-align: right">
          <lay-button size="sm" type="primary" @click="saveMenuSubmit(MenuType.MENU)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="resetModal(MenuType.MENU)">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="saveButtonModalFlag" :title="title">
      <div style="padding: 20px">
        <lay-form :model="sysButtonVo" ref="saveButtonFormRef">
          <lay-row>
            <lay-col md="12">
              <lay-form-item label="父目录" prop="parentId" required>
                <lay-tree-select v-model="sysButtonVo.parentId" :data="saveButtonTreeMenuSelectList"
                                 :allow-clear="true" @change="selectParentById(MenuType.BUTTON)"></lay-tree-select>
              </lay-form-item>
              <lay-form-item label="菜单名称" prop="menuName" required>
                <lay-input v-model="sysButtonVo.menuName" placeholder="例如：新增"></lay-input>
              </lay-form-item>
              <lay-form-item label="路由路径" prop="path" required>
                <lay-input v-model="sysButtonVo.path" placeholder="例如：workspace"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="12">
              <lay-form-item label="排序" prop="orderNum" required>
                <lay-input-number
                    style="width: 100%"
                    v-model="sysButtonVo.orderNum"
                    position="right"
                    :min="0"
                    :step="10"
                ></lay-input-number>
              </lay-form-item>
              <lay-form-item label="是否显示" prop="status" required>
                <lay-select v-model="sysButtonVo.status" :options="isShowOptions" :items="isShowOptions"
                            style="width: 100%">
                </lay-select>
              </lay-form-item>
              <lay-form-item label="图标" prop="icon">
                <lay-input v-model="sysButtonVo.icon"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-form-item label="备注" prop="remark" required>
              <lay-textarea v-model="sysButtonVo.remark" :allow-clear="true" show-count :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-row>
        </lay-form>
        <div style="width: 97%; text-align: right">
          <lay-button size="sm" type="primary" @click="saveMenuSubmit(MenuType.BUTTON)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="resetModal(MenuType.BUTTON)">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>

    <lay-layer v-model="saveLinkModalFlag" :title="title">
      <div style="padding: 20px">
        <lay-form :model="sysLinkVo" ref="saveLinkFormRef">
          <lay-row>
            <lay-col md="12">
              <lay-form-item label="父目录" prop="parentId" required>
                <lay-tree-select v-model="sysLinkVo.parentId" :data="saveLinkTreeMenuSelectList"
                                 :allow-clear="true" @change="selectParentById(MenuType.LINK)"></lay-tree-select>
              </lay-form-item>
              <lay-form-item label="菜单名称" prop="menuName" required>
                <lay-input v-model="sysLinkVo.menuName" placeholder="例如：新增"></lay-input>
              </lay-form-item>
              <lay-form-item label="链接地址" prop="path" required>
                <lay-input v-model="sysLinkVo.path" placeholder="例如：https://www.baidu.com"></lay-input>
              </lay-form-item>
              <lay-form-item label="权限标识" prop="perms" required>
                <lay-input v-model="sysLinkVo.perms" placeholder="例如：sys:menu:index"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col md="12">
              <lay-form-item label="排序" prop="orderNum" required>
                <lay-input-number
                    style="width: 100%"
                    v-model="sysLinkVo.orderNum"
                    position="right"
                    :min="0"
                    :step="10"
                ></lay-input-number>
              </lay-form-item>
              <lay-form-item label="是否显示" prop="status" required>
                <lay-select v-model="sysLinkVo.status" :options="isShowOptions" :items="isShowOptions"
                            style="width: 100%">
                </lay-select>
              </lay-form-item>
              <lay-form-item label="组件类型" prop="componentType" required>
                <lay-select v-model="sysLinkVo.componentType" :options="linkComponentOptions"
                            :items="linkComponentOptions"
                            style="width: 100%"></lay-select>
              </lay-form-item>
              <lay-form-item label="组件路径" prop="component" :hidden="!componentTypeEqInnerLink(sysLinkVo.componentType)"
                             :required="componentTypeEqInnerLink(sysLinkVo.componentType)">
                <lay-input v-model="sysLinkVo.component" placeholder="内部链接必填">
                </lay-input>
              </lay-form-item>
              <lay-form-item label="图标" prop="icon">
                <lay-icon-picker v-model="sysLinkVo.icon" allow-clear></lay-icon-picker>
                <!--                <lay-icon-picker v-model="sysLinkVo.icon" type="layui-icon-face-smile" page></lay-icon-picker>-->
              </lay-form-item>
            </lay-col>
            <lay-form-item label="备注" prop="remark" required>
              <lay-textarea v-model="sysLinkVo.remark" :allow-clear="true" show-count :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-row>
        </lay-form>
        <div style="width: 97%; text-align: right">
          <lay-button size="sm" type="primary" @click="saveMenuSubmit(MenuType.LINK)">保存</lay-button>
          <lay-button size="sm" type="primary" @click="resetModal(MenuType.LINK)">重置</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>
  </lay-container>
</template>
<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {deleteMenu, findMenuListByUserId, findTreeMenuSelect, saveMenu} from "../../../api/system/Menu";
import {FindMenuListByUserIdEntity, SysMenuVo} from "../../../types/system/Menu";
import {Constants, loadSysDictValue, sysDictValueSelect} from "../../../util/UDict";
import {FindTreeMenuSelectEntity, MenuType, SysDictValueEntity} from "../../../types/system/Dict";
import {RouterComponent} from "../../../types/Menu";
/* INIT*/
onMounted(async () => {
  sysMenuTypeList.value = await loadSysDictValue(Constants.SYS_MENU_TYPE);
  sysMenuTypeListSelect.value = await sysDictValueSelect(sysMenuTypeList.value);
  await loadTreeMenuSelectList();
  loadDataSource();
})

function loadDataSource() {
  findMenuListByUserId().then(res => {
    dataSource.value = res.data;
  });
}

async function loadTreeMenuSelectList() {
  findTreeMenuSelect(MenuType.MENU).then(res => {
    res.data.forEach((f: any) => {
      treeMenuSelectList.value?.push(f);
    })
    saveLinkTreeMenuSelectList.value.push(initLinkTreeSelect);
    res.data.forEach((f: any) => {
      saveLinkTreeMenuSelectList.value.push(f);
    })
  });
  findTreeMenuSelect(MenuType.BUTTON).then(res => {
    res.data.forEach((f: any) => {
      saveButtonTreeMenuSelectList.value?.push(f);
    })
  });
}

/* INIT*/

/* VAR */
const title = ref('新增')
const loading = ref(false)
const tableRef = ref()
const prepend = ref()
const saveDirFormRef = ref()
const saveMenuFormRef = ref()
const saveButtonFormRef = ref()
const saveLinkFormRef = ref()
const saveDirModalFlag = ref(false)
const saveMenuModalFlag = ref(false)
const saveButtonModalFlag = ref(false)
const saveLinkModalFlag = ref(false)
const searchQuery = ref<SysMenuVo>({})
const sysMenuTypeList = ref<Array<SysDictValueEntity>>();
const sysMenuTypeListSelect = ref<any[]>();
const treeMenuSelectList = ref<Array<FindTreeMenuSelectEntity>>([]);
const saveButtonTreeMenuSelectList = ref<Array<FindTreeMenuSelectEntity>>([]);
const saveLinkTreeMenuSelectList = ref<Array<FindTreeMenuSelectEntity>>([]);
const selectParentNode = ref();
const dataSource = ref<Array<FindMenuListByUserIdEntity>>([])
const expandMenuFlag = ref(false)
const findTreeChildrenList = ref<any[]>([]);
const menuTreeCheckedKeys = ref<string[]>([])
const menuTreeShowCheckbox = ref(true)
const sysDirVo = ref<SysMenuVo>({
  status: '0'
})
const sysMenuVo = ref<SysMenuVo>({
  status: '0'
})
const sysButtonVo = ref<SysMenuVo>({
  status: '0'
})
const sysLinkVo = ref<SysMenuVo>({
  status: '0'
})
const expandMenu = function (flag: any) {
  expandMenuFlag.value = flag
}
const columns = [
  {
    fixed: 'left',
    type: 'checkbox',
    width: '120px',
    title: '复选',
  },
  {
    title: '菜单名称',
    key: 'menuName',
    width: '200px',
    customSlot: 'menuName'
  },
  {
    title: '路由地址',
    key: 'path',
    width: '100px',
    customSlot: 'path'
  },
  {
    title: '排序',
    width: '50px',
    key: 'orderNum'
  },
  {
    title: '菜单类型',
    key: 'menuType',
    width: '100px',
    customSlot: 'menuType'
  },
  {
    title: '启用',
    key: 'status',
    width: '40px',
    customSlot: 'status'
  },
  {
    title: '组件路径',
    key: 'component',
    width: '200px',
    customSlot: 'component'
  },
  {
    title: '权限标识',
    key: 'perms',
    width: '200px',
    customSlot: 'perms'
  },
  {
    title: '备注',
    key: 'remark',
    width: '200px',
    customSlot: 'remark'
  },
  {
    title: '操作',
    key: 'option',
    width: '150px',
    fixed: 'right',
    customSlot: 'option'
  }
]
const isShowOptions = ref([
  {
    label: '是',
    value: '0',
  },
  {
    label: '否',
    value: '1'
  }
])

const linkComponentOptions = ref([
  {
    label: '内部链接',
    value: 'innerLink'
  },
  {
    label: '窗口打开',
    value: 'modal'
  },
  {
    label: '新建标签页',
    value: 'blank'
  }
])
const initLinkTreeSelect = {
  field: "-1",
  menuType: "M",
  parentId: "-1",
  parentName: "顶级目录",
  title: "顶级目录",
  id: '-1'
}
/* VAR */

/* TOGGLE*/
function toSearch() {
  change()
}

const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 1000)
}

function toReset() {
  searchQuery.value = {}
}

function toCancel() {
  saveDirModalFlag.value = false
  saveMenuModalFlag.value = false
  saveButtonModalFlag.value = false
  saveLinkModalFlag.value = false
}

function toRemove(row: any) {
  if (row.isFrame === '0') {
    layer.confirm('您确定删除【' + row.menuName + '】' + '链接吗？', {
      title: '提示',
      btn: [
        {
          text: '确定',
          callback: async (id: any) => {
            const {code} = await deleteMenu(row.id);
            if (code === 200) {
              layer.msg('您已成功删除链接【' + row.menuName + '】')
            }
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
  } else if (row.menuType === MenuType.DIR) {
    layer.confirm('您确定删除【' + row.menuName + '】' + '目录下的菜单、链接和按钮数据吗？', {
      title: '提示',
      btn: [
        {
          text: '确定',
          callback: async (id: any) => {
            const {code} = await deleteMenu(row.id);
            if (code === 200) {
              layer.msg('您已成功删除目录【' + row.menuName + '】')
              loadDataSource()
            }
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
  } else if (row.menuType === MenuType.MENU) {
    layer.confirm('您确定删除【' + row.menuName + '】' + '菜单下的链接和按钮数据吗？', {
      title: '提示',
      btn: [
        {
          text: '确定',
          callback: async (id: any) => {
            const {code} = await deleteMenu(row.id);
            if (code === 200) {
              layer.msg('您已成功删除菜单【' + row.menuName + '】')
              loadDataSource()
            }
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
  } else if (row.menuType === MenuType.BUTTON) {
    layer.confirm('您确定删除【' + row.menuName + '】' + '按钮吗？', {
      title: '提示',
      btn: [
        {
          text: '确定',
          callback: async (id: any) => {
            const {code} = await deleteMenu(row.id);
            if (code === 200) {
              layer.msg('您已成功删除菜单【' + row.menuName + '】')
              loadDataSource()
            }
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

}

function resetModal(menuType: any) {
  if (MenuType.DIR === menuType) {
    saveDirFormRef.value.reset()
  } else if (MenuType.MENU === menuType) {
    saveMenuFormRef.value.reset()
  } else if (MenuType.BUTTON === menuType) {
    saveButtonFormRef.value.reset()
  } else if (MenuType.LINK === menuType) {
    saveLinkFormRef.value.reset()
  } else {
    layer.confirm('菜单类型' + menuType + '不合法！', {icon: 2})
    return;
  }
}

/* TOGGLE*/

/* FUNCTION*/
const changeSaveMenuVoModalFlag = (menuType: any, row: any) => {
  if (row && row.value) {
    sysMenuVo.value = {...row.value}
  }
  if (MenuType.DIR === menuType) {
    title.value = '新建目录'
    saveDirModalFlag.value = !saveDirModalFlag.value
  } else if (MenuType.MENU === menuType) {
    title.value = '新建菜单'
    saveMenuModalFlag.value = !saveMenuModalFlag.value
  } else if (MenuType.BUTTON === menuType) {
    title.value = '新建按钮'
    saveButtonModalFlag.value = !saveButtonModalFlag.value
  } else if (MenuType.LINK === menuType) {
    title.value = '新建链接'
    saveLinkModalFlag.value = !saveLinkModalFlag.value
  } else {
    return;
  }
}

function saveMenuSubmit(menuType: any) {
  let saveFormRef = ref();
  if (MenuType.DIR === menuType) {
    saveFormRef = saveDirFormRef
  } else if (MenuType.MENU === menuType) {
    saveFormRef = saveMenuFormRef
  } else if (MenuType.BUTTON === menuType) {
    saveFormRef = saveButtonFormRef
  } else if (MenuType.LINK === menuType) {
    saveFormRef = saveLinkFormRef
  } else {
    layer.confirm('菜单类型' + menuType + '不合法！', {icon: 2})
  }
  saveFormRef.value.validate(async (isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      model.menuType = menuType;
      let {code, msg} = await saveMenu(model);
      if (code === 200) {
        layer.msg(msg, {icon: 1})
        resetModal(menuType)
        loadDataSource()
        toCancel()
      }
    }
  })
}

function selectParentById(menuType: any) {
  let tmpVo = ref();
  if (MenuType.MENU === menuType) {
    tmpVo = sysMenuVo;
  } else if (MenuType.BUTTON === menuType) {
    tmpVo = sysButtonVo;
  } else if (MenuType.LINK === menuType) {
    tmpVo = sysLinkVo;
  }
  dataSource.value.find(f => {
    if (tmpVo.value.parentId === f.id) {
      prepend.value = f.path;
      return;
    }
  })
}

function pathInputEvent(val: any) {
  sysMenuVo.value.component = prepend.value + "/" + val;
  sysMenuVo.value.perms = prepend.value + ":" + val;
}

function componentTypeEqInnerLink(componentType: any) {
  return componentType === RouterComponent.INNER_LINK;
}

function findTreeChildren(treeList: any[], target: any) {
  if (MenuType.DIR === target.menuType) {
    treeList.find(item => {
      if (item.id === target.id) {
        if (MenuType.DIR === item.menuType) {
          findTreeChildren(item.children, item);
        } else {
          recursionChildren(item);
          findTreeChildrenList.value.push(item);
        }
      }
    })
  } else if (MenuType.MENU === target.menuType) {
    treeList.find(item => {
      if (item.id === target.parentId) {
        if (item.children && item.children.length > 0) {
          item.children.forEach((child: any) => {
            if (child.id === target.id) {
              recursionChildren(child);
              findTreeChildrenList.value.push(child);
            }
          })
        }
      }
    })
  }

}

function recursionChildren(item: any) {
  let childrenList: any[] = findChildList(item);
  childrenList.forEach(child => {
    findTreeChildrenList.value.push(child);
    if (hasChild(child)) {
      recursionChildren(child);
    }
  })
}

function hasChild(item: any): boolean {
  let list = findChildList(item);
  return list && list.length > 0;
}

function findChildList(item: any): any[] {
  let result: any[] = [];
  item.children?.forEach((f: any) => {
    result.push(f);
  })
  return result;
}

/* FUNCTION*/

</script>

<style scoped>
.menu-box {
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
