<template>
  <lay-panel style="margin: 10px" :shadow="'hover'">
    <lay-button type="normal" size="sm" @click="showSaveGroupingModal(Operate.ADD, null)">
      <lay-icon type="layui-icon-addition"></lay-icon>
      新建图标分组
    </lay-button>
    <lay-button type="primary" size="sm" @click="showSingleSaveIconModal(Operate.ADD, null)">
      <lay-icon type="layui-icon-addition"></lay-icon>
      添加图标
    </lay-button>
    <lay-button type="warm" size="sm" @click="showMultipleSaveIconModal(Operate.ADD, null)">
      <lay-icon type="layui-icon-addition"></lay-icon>
      批量添加图标
    </lay-button>
  </lay-panel>
  <lay-collapse v-model="openKeys">
    <div v-for="[key, value] in dataSource" :key="key">
      <lay-collapse-item :title="key" :id="key">
        <ul class="site-doc-icon">
          <li v-for="(item, index) of value" :key="index">
            <lay-tooltip :visible="false" trigger="hover" :content="item.name">
              <SvgIcon :name="item.url" :desc="item.name"></SvgIcon>
            </lay-tooltip>
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

  <lay-layer v-model="saveSingleIconModalFlag" :area="['1200px']" :title="saveSingleIconTitle">
    <div style="padding: 20px" v-esc-close="hideSingleSaveIconModal">
      <lay-form :model="saveSingleIconVo" ref="saveSingleIconFormRef" label-position="top" size="md">
        <lay-row space="20">
          <lay-col md="6">
            <lay-form-item label="自定义分区" prop="parentId" required>
              <lay-select
                  style="width: 100%"
                  v-model="saveSingleIconVo.parentId"
                  :options="findGroupingList"
                  :placeholder="'请选择'"
                  :allow-clear="true"
                  @change="changeIconTreeTypeList"></lay-select>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="图标名称" prop="name" required>
              <lay-input v-model="saveSingleIconVo.name"></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="图标" prop="iconId" required>
              <lay-row>
                <lay-col md="4">
                  <lay-avatar v-if="!saveSingleIconVo.url" @click="showSingleIconPickerModal"></lay-avatar>
                  <object v-else style="height:40px" :data="saveSingleIconVo.url" type="image/svg+xml"
                          @click="showSingleIconPickerModal"></object>
                </lay-col>
                <lay-col md="20" class="iconName">
                  图标：{{ saveSingleIconVo.originName }}
                </lay-col>
              </lay-row>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="排序" prop="orderNum" required>
              <lay-input-number
                  style="width: 100%"
                  v-model="saveSingleIconVo.orderNum"
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
              <lay-textarea v-model="saveSingleIconVo.remark" :allow-clear="true" show-count
                            :maxlength="127"></lay-textarea>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
      <div style="width: 97%; text-align: right">
        <lay-button size="sm" type="primary" @click="saveIcon">保存</lay-button>
        <lay-button size="sm" type="primary" @click="resetSingleSaveIconModal">重置
        </lay-button>
        <lay-button size="sm" @click="hideSingleSaveIconModal">取消</lay-button>
      </div>
    </div>
  </lay-layer>

  <lay-layer v-model="saveMultipleIconModalFlag" :area="['400px', '500px']" :title="saveMultipleIconTitle">
    <div style="padding: 20px" v-esc-close="hideMultipleSaveIconModal">
      <lay-form :model="saveMultipleIconVo" ref="saveMultipleIconFormRef" label-position="top" size="md">
        <lay-col md="24">
          <lay-row>
            <lay-form-item label="自定义分区" prop="parentId" required>
              <lay-select
                  style="width: 100%"
                  v-model="saveMultipleIconVo.parentId"
                  :options="findGroupingList"
                  :placeholder="'请选择'"
                  :allow-clear="true"
                  @change="changeIconTreeTypeList"></lay-select>
            </lay-form-item>
          </lay-row>
          <lay-row>
            <lay-form-item label="图标" prop="multipleIconList" required>
              <div style="display: inline-block">
                <lay-avatar @click="showMultipleIconPickerModal"></lay-avatar>
                <lay-avatar v-for="(item,index) in saveMultipleIconVo.multipleIconList"
                            :key="index"
                            :style="item.url ? '' : 'display: none'"
                            :src="item.url"
                            @click="preview(item.url)"
                ></lay-avatar>
              </div>
            </lay-form-item>
          </lay-row>
        </lay-col>
      </lay-form>
      <div style="width: 97%; text-align: right">
        <lay-button size="sm" type="primary" @click="saveBatchIcon">保存</lay-button>
        <lay-button size="sm" type="primary" @click="resetMultipleSaveIconModal">重置
        </lay-button>
        <lay-button size="sm" @click="hideMultipleSaveIconModal">取消</lay-button>
      </div>
    </div>
  </lay-layer>

  <lay-layer v-model="showSingleIconPickerModalFlag" :area="['1200px', '600px']" :title="iconPickerModalTitle">
    <div v-esc-close="hideSingleIconPickerModal">
      <lay-collapse v-model="iconPickerModalOpenKeys">
        <div v-for="[key, value] in findCommonIconPickerDataSource" :key="key">
          <lay-collapse-item :title="key" :id="key">
            <lay-card :shadow="true">
              <lay-row>
                <lay-col :md="3" v-for="(item, index) of value">
                  <lay-checkcard-group :single="true" v-model="checkCardGroupKey" @change="selectCheckCard(item)">
                    <lay-tooltip :visible="false" trigger="hover" :content="item.name">
                      <lay-badge type="rim" :value="findSingleItemIndex(item.id)">
                        <lay-checkcard class="checkCard" :cover="item.url" v-model="item.id" :value="item.id"
                        ></lay-checkcard>
                      </lay-badge>
                    </lay-tooltip>
                  </lay-checkcard-group>
                </lay-col>
              </lay-row>
            </lay-card>
          </lay-collapse-item>
        </div>
      </lay-collapse>
    </div>
  </lay-layer>

  <lay-layer v-model="showMultiplePickerModalFlag" :area="['1200px', '600px']" :title="iconPickerModalTitle">
    <div v-esc-close="hideMultipleIconPickerModal">
      <lay-collapse v-model="iconPickerModalOpenKeys">
        <div v-for="[key, value] in findCommonIconPickerDataSource" :key="key">
          <lay-collapse-item :title="key" :id="key">
            <lay-card :shadow="true">
              <lay-row>
                <lay-col :md="3">
                  <div class="svgIcon" @click="selectMultipleIcons">
                    <SvgIcon
                        :name="'http://127.0.0.1:9002/freesia/icon/2025/04/24/61ca68d0a33b471f89339a5de53aa518.svg'"
                        :desc="'添加'"
                    ></SvgIcon>
                  </div>
                </lay-col>
                <lay-col :md="3" v-for="(item, index) of value">
                  <lay-checkcard-group v-model="checkCardGroupKeys">
                    <lay-tooltip :visible="false" trigger="hover" :content="item.name">
                      <lay-badge type="rim" :value="findMultipleItemIndex(item.id)">
                        <lay-checkcard class="checkCard" :cover="item.url" v-model="item.id" :value="item.id"
                        ></lay-checkcard>
                      </lay-badge>
                    </lay-tooltip>
                  </lay-checkcard-group>
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
import {onMounted, ref, watch} from 'vue'
import {useRoute} from "vue-router";
import {
  CommonIconTemplateDetailEntity,
  CommonIconTemplateDetailVo, FindMaxOrderNumVo, FindTreeIconTreeTypeEntity, IconTreeType
} from "@/types/common/icon/template/IconTemplateDetail";
import {
  findCustomIconTemplateDetail, findGrouping, findMaxOrderNum,
  findTreeIconTreeType,
  saveUpdate, saveUpdateBatch
} from "@/api/common/icon/template/IconTemplateDetail";
import {R} from "@/types/Result";
import {layer} from "@layui/layui-vue";
import SvgIcon from "@/views/component/svg/SvgIcon.vue";
import {SysDictValueEntity} from "@/types/system/Dict";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";
import {Operate} from "@/types/Constants";
import {findCommonIconPicker, findListCommonIcon} from "@/api/common/icon/Icon";
import {CommonIconVo, FindCommonIconEntity} from "@/types/common/icon/Icon";
import {Select} from "@/types/Common";
import {preview} from "@/util/UImage";
import LayMenuAdapter from "@/views/component/LayMenuAdapter.vue";

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
const openKeys = ref<string[]>([]);
const iconPickerModalOpenKeys = ref<string[]>([]);
const saveGroupModalFlag = ref<boolean>(false)
const saveSingleIconModalFlag = ref<boolean>(false)
const saveMultipleIconModalFlag = ref<boolean>(false)
const showMultiplePickerModalFlag = ref<boolean>(false)
const showSingleIconPickerModalFlag = ref<boolean>(false)
const saveGroupVo = ref<CommonIconTemplateDetailVo>(<CommonIconTemplateDetailVo>{});
const saveSingleIconVo = ref<CommonIconTemplateDetailVo>(<CommonIconTemplateDetailVo>{});
const saveMultipleIconVo = ref<CommonIconTemplateDetailVo>(<CommonIconTemplateDetailVo>{});
const iconTreeTypeList = ref<Array<SysDictValueEntity>>();
const iconTreeTypeListSelect = ref<any[]>();
const treeIconTreeTypeEntityList = ref<Array<FindTreeIconTreeTypeEntity>>([])
const saveGroupFormRef = ref();
const saveSingleIconFormRef = ref();
const saveMultipleIconFormRef = ref();
const findGroupingList = ref<Array<Select>>();
const saveGroupTitle = ref<string>('')
const saveSingleIconTitle = ref<string>('')
const saveMultipleIconTitle = ref<string>('')
const iconPickerModalTitle = ref<string>('图标选择器')
const checkCardGroupKeys = ref([])
const checkCardGroupKey = ref('')
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
    let temp: string[] = []
    dataSource.value?.forEach((value, key) => {
      if (value && value.length > 0) {
        temp.push(key)
      }
    })
    openKeys.value = temp;
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

function showSingleSaveIconModal(title: string, row: any) {
  saveSingleIconTitle.value = Operate.ADD === title ? "新增" : Operate.EDIT === title ? "编辑" : "";
  if (row != null) {
    saveSingleIconTitle.value = {...row}
  }
  if (Operate.EDIT === title) {
  } else if (Operate.ADD === title) {
    saveSingleIconVo.value = {}
    checkCardGroupKey.value = null
    saveSingleIconModalFlag.value = true
    let param: CommonIconTemplateDetailVo = {
      headerId: headerId.value
    }
    findGrouping(param).then((res: R<Map<string, string>>) => {
      findGroupingList.value = res.data;
    })
  } else if (Operate.COPY === title) {
  }
}

function showMultipleSaveIconModal(title: string, row: any) {
  saveMultipleIconTitle.value = Operate.ADD === title ? "新增" : Operate.EDIT === title ? "编辑" : "";
  if (row != null) {
    saveMultipleIconTitle.value = {...row}
  }
  if (Operate.EDIT === title) {
  } else if (Operate.ADD === title) {
    saveMultipleIconVo.value = {}
    checkCardGroupKeys.value = []
    saveMultipleIconModalFlag.value = true;
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

function hideSingleSaveIconModal() {
  saveSingleIconModalFlag.value = false
}

function hideMultipleSaveIconModal() {
  saveMultipleIconModalFlag.value = false
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
  saveSingleIconVo.value.parentId = value;
  let param: FindMaxOrderNumVo = {
    parentId: value,
    headerId: headerId.value,
    iconTreeType: IconTreeType.L,
  }
  findMaxOrderNum(param).then((res: R<number>) => {
    saveSingleIconVo.value.orderNum = res.data
  });
}

function doFindCommonIconPicker() {
  findCommonIconPicker({}).then((res: R<Record<string, Array<FindCommonIconEntity>>>) => {
    findCommonIconPickerDataSource.value = new Map(Object.entries(res.data))
    iconPickerModalOpenKeys.value = Array.from(findCommonIconPickerDataSource.value.keys())
  })
}

function saveGroup() {
  saveGroupVo.value.headerId = headerId.value;
  saveGroupVo.value.parentId = '-1';
  saveGroupVo.value.grouping = saveGroupVo.value?.name
  saveGroupVo.value.iconTreeType = IconTreeType.R;
  saveUpdate(saveGroupVo.value).then((res: R<void>) => {
    change()
    hideSaveGroupingModal()
  })
}

function saveIcon() {
  saveSingleIconFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveSingleIconVo.value.headerId = headerId.value;
      saveSingleIconVo.value.iconTreeType = IconTreeType.L;
      saveUpdate(saveSingleIconVo.value).then((res: R<void>) => {
        change()
        hideSingleSaveIconModal()
      })
    }
  })
}

function saveBatchIcon() {
  saveMultipleIconFormRef.value.validate((isValidate: any, model: any, errors: any) => {
    if (isValidate) {
      saveMultipleIconVo.value.headerId = headerId.value;
      saveMultipleIconVo.value.iconTreeType = IconTreeType.L;
      saveUpdateBatch(saveMultipleIconVo.value).then((res: R<void>) => {
        change()
        hideMultipleSaveIconModal()
      })
    }
  })
}

function resetSaveGroupModal() {
  saveGroupFormRef.value.reset();
}

function resetSingleSaveIconModal() {
  saveSingleIconFormRef.value.reset();
}

function resetMultipleSaveIconModal() {
  saveMultipleIconFormRef.value.reset();
}

function showMultipleIconPickerModal() {
  showMultiplePickerModalFlag.value = true
}

function hideMultipleIconPickerModal() {
  showMultiplePickerModalFlag.value = false
}

function showSingleIconPickerModal() {
  showSingleIconPickerModalFlag.value = true
}

function hideSingleIconPickerModal() {
  showSingleIconPickerModalFlag.value = false
}

function selectCheckCard(item: FindCommonIconEntity) {
  saveSingleIconVo.value.url = item.url
  saveSingleIconVo.value.iconId = item.id
  saveSingleIconVo.value.originName = item.name
  hideSingleIconPickerModal()
}

function selectMultipleIcons() {
  let param: CommonIconVo = {
    idList: checkCardGroupKeys.value
  }
  findListCommonIcon(param).then((res: R<FindCommonIconEntity[]>) => {
    saveMultipleIconVo.value.multipleIconList = res.data
  })
  hideMultipleIconPickerModal()
}

/**
 * 查询多选图标的下标
 * @param id
 */
function findSingleItemIndex(id: string | undefined): number {
  return checkCardGroupKey.value === id ? 1 : 0;
}

/**
 * 查询多选图标的下标
 * @param id
 */
function findMultipleItemIndex(id: string | undefined): number {
  return checkCardGroupKeys.value.findIndex(item => item === id) + 1;
}

/* FUNCTION*/
</script>

<style>
.site-doc-icon {
  margin-bottom: 10px;
  font-size: 0;
}

.site-doc-icon li {
  display: inline-block;
  vertical-align: middle;
  width: 100px;
  height: 100px;
  line-height: 20px;
  margin: 20px 0;
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
  margin-right: -1px;
  margin-bottom: -1px;
  border: 1px solid #e2e2e2;
  font-size: 14px;
  text-align: center;
  color: #666;
  transition: all 0.3s;
  -webkit-transition: all 0.3s;
}

.svgIcon {
  width: 120px;
  height: 120px;
  cursor: pointer
}

.checkCard {
  height: 120px;
  width: 120px
}

.iconName {
  justify-content: center;
  align-items: center;
  font-size: 10pt;
  line-height: 40px
}
</style>