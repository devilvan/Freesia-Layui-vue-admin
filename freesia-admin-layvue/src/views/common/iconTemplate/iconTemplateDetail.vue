<template>
  <lay-panel style="margin: 10px" :shadow="'hover'">
    <lay-button type="normal" size="sm" @click="showSaveGroupingModal(Operate.ADD, null)">
      <lay-icon type="layui-icon-addition"></lay-icon>
      新建图标分组
    </lay-button>
    <lay-button type="primary" size="sm" @click="showSaveIconModal(Operate.ADD, null)">
      <lay-icon type="layui-icon-addition"></lay-icon>
      添加图标
    </lay-button>
  </lay-panel>
  <lay-collapse v-model="openKeys">
    <div v-for="[key, value] in dataSource" :key="key">
      <lay-collapse-item :title="key" :id="key">
        <ul class="site-doc-icon">
          <li>
            <SvgIcon class="svgIcon"
                     :name="'http://127.0.0.1:9002/freesia/icon/2025/04/24/61ca68d0a33b471f89339a5de53aa518.svg'"
                     :desc="'添加图标'"
            ></SvgIcon>
          </li>
          <li v-show="item.iconTreeType === IconTreeType.L && item.children?.length > 0" v-for="(item, index) of value"
              :key="index">
            <SvgIcon :name="item.url" :desc="item.name"></SvgIcon>
          </li>
        </ul>
      </lay-collapse-item>
    </div>
  </lay-collapse>

  <lay-layer v-model="saveGroupModalFlag" :area="['1200px']" :title="saveGroupTitle">
    <div style="padding: 20px" v-esc-close="hideSaveGroupingModal">
      <lay-form :model="saveGroupVo" ref="saveGroupFormRef" label-position="top" size="md">
        <lay-row space="20">
          <lay-col md="6">
            <lay-form-item label="分区名称" prop="name" required>
              <lay-input v-model="saveGroupVo.name"></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="排序" prop="orderNum" required>
              <lay-input-number
                  style="width: 100%"
                  v-model="saveGroupVo.orderNum"
                  position="right"
                  :min="0"
                  :step="10"
              ></lay-input-number>
            </lay-form-item>
          </lay-col>
        </lay-row>
        <lay-row :space="20">
          <lay-col md="6">
            <lay-form-item label="备注" prop="remark">
              <lay-textarea v-model="saveGroupVo.remark" :allow-clear="true" show-count
                            :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
      <div style="width: 97%; text-align: right">
        <lay-button size="sm" type="primary" @click="saveGroup">保存</lay-button>
        <lay-button size="sm" type="primary" @click="resetSaveGroupModal">重置
        </lay-button>
        <lay-button size="sm" @click="hideSaveGroupingModal">取消</lay-button>
      </div>
    </div>
  </lay-layer>

  <lay-layer v-model="saveIconModalFlag" :area="['1200px']" :title="saveIconTitle">
    <div style="padding: 20px" v-esc-close="hideSaveIconModal">
      <lay-form :model="saveIconVo" ref="saveIconFormRef" label-position="top" size="md">
        <lay-row space="20">
          <lay-col md="6">
            <lay-form-item label="自定义分区" prop="headerId" required>
              <lay-select
                  style="width: 100%"
                  v-model="saveIconVo.headerId"
                  :options="findGroupingList"
                  :placeholder="'请选择'"
                  :allow-clear="true" @change="changeIconTreeTypeList"></lay-select>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="图标名称" prop="name" required>
              <lay-input v-model="saveIconVo.name"></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="图标" prop="icon" required>
              <lay-row>
                <lay-col md="4">
                  <lay-avatar v-if="!saveIconVo.iconId" @click="showIconPickerModal"></lay-avatar>
                  <SvgIcon v-else :name="saveIconVo.iconId" size="3em" @click="changeIconTreeTypeList"></SvgIcon>
                </lay-col>
                <lay-col md="20"
                         style="justify-content: center; align-items: center; font-size: 10pt; line-height: 40px">
                  图标：{{ saveIconVo.name }}
                </lay-col>
              </lay-row>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="排序" prop="orderNum" required>
              <lay-input-number
                  style="width: 100%"
                  v-model="saveIconVo.orderNum"
                  position="right"
                  :min="0"
                  :step="10"
              ></lay-input-number>
            </lay-form-item>
          </lay-col>
        </lay-row>
        <lay-row :space="20">
          <lay-col md="6">
            <lay-form-item label="备注" prop="remark">
              <lay-textarea v-model="saveIconVo.remark" :allow-clear="true" show-count
                            :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
      <div style="width: 97%; text-align: right">
        <lay-button size="sm" type="primary" @click="saveIcon">保存</lay-button>
        <lay-button size="sm" type="primary" @click="resetSaveIconModal">重置
        </lay-button>
        <lay-button size="sm" @click="hideSaveIconModal">取消</lay-button>
      </div>
    </div>
  </lay-layer>

  <lay-layer v-model="showIconPickerModalFlag" :area="['1000px', '600px']" :title="iconPickerModalTitle">
    <div v-esc-close="hideIconPickerModal">
      <lay-collapse v-model="openKeys">
        <div v-for="[key, value] in findCommonIconPickerDataSource" :key="key">
          <lay-collapse-item :title="key" :id="key">
            <lay-card :shadow="true">
              <lay-row :space="20">
                <lay-col :md="3">
                  <div class="svgIcon">
                    <SvgIcon
                        :name="'http://127.0.0.1:9002/freesia/icon/2025/04/24/61ca68d0a33b471f89339a5de53aa518.svg'"
                        :desc="'添加图标'"
                    ></SvgIcon>
                  </div>
                </lay-col>
                <lay-col :md="3" v-for="(item, index) of value">
                  <div class="svgIcon">
                    <SvgIcon :name="item.url" :desc="item.name"></SvgIcon>
                  </div>
                </lay-col>
              </lay-row>
            </lay-card>
          </lay-collapse-item>
        </div>
      </lay-collapse>
    </div>
  </lay-layer>


</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "IconTemplateDetail",
};
</script>
<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {useRoute} from "vue-router";
import {
  CommonIconTemplateDetailEntity,
  CommonIconTemplateDetailVo, FindMaxOrderNumVo, FindTreeIconTreeTypeEntity, IconTreeType
} from "@/types/common/icon/template/IconTemplateDetail";
import {
  findCommonIconTemplateDetail, findCustomIconTemplateDetail, findGrouping, findMaxOrderNum,
  findTreeIconTreeType,
  saveUpdate
} from "@/api/common/icon/template/IconTemplateDetail";
import {R} from "@/types/Result";
import {layer} from "@layui/layui-vue";
import SvgIcon from "@/views/component/svg/SvgIcon.vue";
import {SysDictValueEntity} from "@/types/system/Dict";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {Operate} from "@/types/Constants";
import {findAccountCost} from "@/api/account/Account";
import AccountTypeIconPicker from "@/views/component/svg/AccountTypeIconPicker.vue";
import IconPicker from "@/views/component/svg/IconPicker.vue";
import {findCommonIconPicker} from "@/api/common/icon/Icon";
import {FindCommonIconEntity} from "@/types/common/icon/Icon";

/* INIT*/
onMounted(async () => {
  headerId.value = $route.params && $route.params.headerId as string;
  iconTreeTypeList.value = await loadSysDictValue(Constants.ICON_TREE_TYPE);
  iconTreeTypeListSelect.value = await sysDictValueSelect(iconTreeTypeList.value);
  doFindTreeIconTreeType()
  doFindCommonIconPicker();
  change()
})
/* INIT*/

/* VAR*/
const $route = useRoute();
const headerId = ref<string>('');
const dataSource = ref<Map<string, FindTreeIconTreeTypeEntity[]>>()
const findCommonIconPickerDataSource = ref<Map<string, Array<FindCommonIconEntity>>>()
const loading = ref(true)
const openKeys = ref<Array<string>>(<Array<string>>[]);
const saveGroupModalFlag = ref<boolean>(false)
const saveIconModalFlag = ref<boolean>(false)
const showIconPickerModalFlag = ref<boolean>(false)
const saveGroupVo = ref<CommonIconTemplateDetailVo>(<CommonIconTemplateDetailVo>{});
const saveIconVo = ref<CommonIconTemplateDetailVo>(<CommonIconTemplateDetailVo>{});
const iconTreeTypeList = ref<Array<SysDictValueEntity>>();
const iconTreeTypeListSelect = ref<any[]>();
const treeIconTreeTypeEntityList = ref<Array<FindTreeIconTreeTypeEntity>>([])
const saveGroupFormRef = ref();
const saveIconFormRef = ref();
const findGroupingList = ref<Array<Map<string, string>>>();
const saveGroupTitle = ref<string>('')
const saveIconTitle = ref<string>('')
const iconPickerModalTitle = ref<string>('图标选择器')
/* VAR*/

/* FUNCTION*/
/**
 * 刷新
 */
const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 200)
}

/**
 * 初始化
 */
const loadDataSource = () => {
  let param: CommonIconTemplateDetailVo = {
    headerId: headerId.value
  }
  findCustomIconTemplateDetail(param).then((res: R<Record<string, FindTreeIconTreeTypeEntity[]>>) => {
    dataSource.value = new Map(Object.entries(res.data));
    dataSource.value?.forEach((value, key) => {
      openKeys.value.push(key)
    })
  }).catch(e => {
    layer.msg(e.msg)
  });
}

function showSaveGroupingModal(title: string, row: any) {
  saveGroupTitle.value = Operate.ADD === title ? "新增" : Operate.EDIT === title ? "编辑" : "";
  if (row != null) {
    saveGroupTitle.value = {...row}
  }
  if (Operate.EDIT === title) {
  } else if (Operate.ADD === title) {
    saveGroupVo.value = {}
    saveGroupModalFlag.value = true
    let param: FindMaxOrderNumVo = {
      headerId: headerId.value,
      iconTreeType: IconTreeType.R
    }
    findMaxOrderNum(param).then((res: R<number>) => {
      saveGroupVo.value.orderNum = res.data
    });
  } else if (Operate.COPY === title) {
  }
}

function showSaveIconModal(title: string, row: any) {
  saveIconTitle.value = Operate.ADD === title ? "新增" : Operate.EDIT === title ? "编辑" : "";
  if (row != null) {
    saveIconTitle.value = {...row}
  }
  if (Operate.EDIT === title) {
  } else if (Operate.ADD === title) {
    saveIconVo.value = {}
    saveIconModalFlag.value = true
    let param: CommonIconTemplateDetailVo = {
      headerId: headerId.value
    }
    findGrouping(param).then((res: R<Map<string, string>>) => {
      findGroupingList.value = res.data;
    })
  } else if (Operate.COPY === title) {
  }
}

function hideSaveGroupingModal() {
  saveGroupModalFlag.value = false
}

function hideSaveIconModal() {
  saveIconModalFlag.value = false
}

/**
 * 查询自定义分类树
 */
function doFindTreeIconTreeType() {
  let param: CommonIconTemplateDetailVo = {
    headerId: headerId.value
  }
  findTreeIconTreeType(param).then((res: R<Array<FindTreeIconTreeTypeEntity>>) => {
    res.data?.forEach(item => {
      treeIconTreeTypeEntityList.value.push(item)
    })
  })
}

/**
 * 事件：改变自定义分类树
 * @param value ID
 */
function changeIconTreeTypeList(value: any) {
  console.log(value)
  saveIconVo.value.parentId = value;
  let param: FindMaxOrderNumVo = {
    parentId: value,
    headerId: headerId.value,
    iconTreeType: IconTreeType.L,
  }
  findMaxOrderNum(param).then((res: R<number>) => {
    saveIconVo.value.orderNum = res.data
  });
}

function doFindCommonIconPicker() {
  findCommonIconPicker({}).then((res: R<Record<string, Array<FindCommonIconEntity>>>) => {
    findCommonIconPickerDataSource.value = new Map(Object.entries(res.data))
  })
}

function saveGroup() {
  saveGroupVo.value.headerId = headerId.value;
  saveGroupVo.value.parentId = '-1';
  saveGroupVo.value.grouping = saveGroupVo.value?.name
  saveGroupVo.value.iconTreeType = IconTreeType.R;
  saveUpdate(saveGroupVo.value).then((res: R<void>) => {
    hideSaveGroupingModal()
  })
}

function saveIcon() {
  saveIconFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveIconVo.value.headerId = headerId.value;
      saveIconVo.value.iconTreeType = IconTreeType.L;
      saveUpdate(saveGroupVo.value).then((res: R<void>) => {
        hideSaveIconModal()
      })
    }
  })

}

function resetSaveGroupModal() {
  saveGroupFormRef.value.reset();
}

function resetSaveIconModal() {
  saveIconFormRef.value.reset();
}

function showIconPickerModal() {
  showIconPickerModalFlag.value = true
}

function hideIconPickerModal() {
  showIconPickerModalFlag.value = false
}

function callBackFun(icon: any) {
  saveIconVo.value.iconId = icon;
  hideIconPickerModal()
}

/* FUNCTION*/
</script>

<style>
.site-doc-icon {
  margin-bottom: 10px;
  font-size: 0;
}

.site-doc-icon li {
  display: flow;
  vertical-align: middle;
  width: 100px;
  height: 100px;
  line-height: 20px;
  padding: 20px 0;
  //margin-right: -1px;
  //margin-bottom: -1px;
  border: 1px solid #e2e2e2;
  font-size: 14px;
  text-align: center;
  color: #000;
  transition: all 0.3s;
  -webkit-transition: all 0.3s;
}

.site-doc-icon li div .svgIcon {
  margin-top: -10px;
}

.site-doc-icon li .doc-icon-name,
.site-doc-icon li .doc-icon-code {
  color: #000;
  font-weight: bold;
  font-size: 16pt;
}

.site-doc-icon li:hover {
  background-color: #F6F6F6;
}

.site-doc-icon li:hover {
  background-color: #ff9a9e;
}

.site-doc-icon li .layui-icon {
  display: inline-block;
  font-size: 32px;
}

.anim .site-doc-icon {
  margin-bottom: 50px;
  font-size: 0;
}

.anim .site-doc-icon li {
  width: 50%;
}

.anim .site-doc-icon li {
  display: inline-block;
  vertical-align: middle;
  width: 16.5%;
  height: 105px;
  line-height: 25px;
  padding: 20px 0;
  margin-right: -1px;
  margin-bottom: -1px;
  border: 1px solid #e2e2e2;
  font-size: 14px;
  text-align: center;
  color: #666;
  transition: all 0.3s;
  -webkit-transition: all 0.3s;
}

.anim .site-doc-icon li .layui-anim {
  width: 125px;
  height: 125px;
  line-height: 125px;
  margin: 0 auto 10px;
  text-align: center;
  background-color: var(--global-primary-color);
  cursor: pointer;
  color: #fff;
  border-radius: 50%;
}

.anim .site-doc-icon li .code {
  white-space: nowrap;
}

.svgIcon {
  width: 100px;
  height: 100px;
}
</style>