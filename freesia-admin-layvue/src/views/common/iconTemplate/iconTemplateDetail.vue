<template>
  <lay-panel style="margin: 10px" :shadow="'hover'">
    <lay-button type="normal" size="sm" @click="showSaveGroupingModal">
      <lay-icon type="layui-icon-addition"></lay-icon>
      新建图标分组
    </lay-button>
    <lay-button type="primary" size="sm" @click="showSaveIconModal">
      <lay-icon type="layui-icon-addition"></lay-icon>
      添加图标
    </lay-button>
  </lay-panel>
  <lay-collapse v-model="openKeys">
    <div v-for="[key, value] in dataSource" :key="key">
      <lay-collapse-item :title="key" :id="key">
        <ul class="site-doc-icon">
          <li v-for="(item, index) of value" :key="index">
            <SvgIcon class="svgIcon" :color="'green'" :name="item" :size="'3em'"></SvgIcon>
          </li>
        </ul>
      </lay-collapse-item>
    </div>
  </lay-collapse>

  <lay-layer v-model="saveGroupModalFlag" :area="['1200px']">
    <div style="padding: 20px" v-esc-close="hideSaveGroupingModal">
      <lay-form :model="saveGroupVo" ref="saveGroupFormRef" label-position="top" size="md">
        <lay-row space="20">
          <lay-col md="6">
            <lay-form-item label="分区名称" prop="name" required>
              <lay-input v-model="saveGroupVo.name"></lay-input>
            </lay-form-item>
          </lay-col>
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
        <lay-button size="sm" type="primary" @click="resetGroupModal">重置
        </lay-button>
        <lay-button size="sm" @click="hideSaveGroupingModal">取消</lay-button>
      </div>
    </div>
  </lay-layer>
  <lay-layer>
    <div v-esc-close="hideSaveIconModal">

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
  CommonIconTemplateDetailVo, FindTreeIconTreeTypeEntity, IconTreeType
} from "@/types/common/icon/template/IconTemplateDetail";
import {
  findCommonIconTemplateDetail,
  findTreeIconTreeType,
  saveUpdate
} from "@/api/common/icon/template/IconTemplateDetail";
import {R} from "@/types/Result";
import {layer} from "@layui/layui-vue";
import SvgIcon from "@/views/component/svg/SvgIcon.vue";
import {PROCEED_CODE} from "@/types/Constants";
import {MenuType, SysDictValueEntity} from "@/types/system/Dict";
import {Constants, loadSysDictValue, sysDictValueSelect} from "@/util/UDict";

/* INIT*/
onMounted(async () => {
  headerId.value = $route.params && $route.params.headerId as string;
  iconTreeTypeList.value = await loadSysDictValue(Constants.ICON_TREE_TYPE);
  iconTreeTypeListSelect.value = await sysDictValueSelect(iconTreeTypeList.value);
  doFindTreeIconTreeType()
  change()
})
/* INIT*/

/* VAR*/
const $route = useRoute();
const headerId = ref<string>('');
const dataSource = ref<Map<String, CommonIconTemplateDetailEntity>>()
const loading = ref(true)
const openKeys = ref<Array<string>>(<Array<string>>[]);
const saveGroupModalFlag = ref<boolean>(false)
const saveIconModalFlag = ref<boolean>(false)
const saveGroupVo = ref<CommonIconTemplateDetailVo>(<CommonIconTemplateDetailVo>{});
const iconTreeTypeList = ref<Array<SysDictValueEntity>>();
const iconTreeTypeListSelect = ref<any[]>();
const treeIconTreeTypeEntityList = ref<Array<FindTreeIconTreeTypeEntity>>([])
const saveGroupFormRef = ref();
const saveIconFormRef = ref();
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
  findCommonIconTemplateDetail(param).then((res: R<Map<String, CommonIconTemplateDetailEntity>>) => {
    dataSource.value = res.data
    openKeys.value = res.data?.keys();
    console.log(openKeys.value)
  }).catch(e => {
    layer.msg(e.msg)
  });
}

function showSaveGroupingModal() {
  saveGroupModalFlag.value = true
}

function showSaveIconModal() {
  saveIconModalFlag.value = true
}

function hideSaveGroupingModal() {
  saveGroupModalFlag.value = false
}

function hideSaveIconModal() {
  saveIconModalFlag.value = false
}

function saveGrouping() {
  saveUpdate(saveGroupVo.value).then((res: R<void>) => {
    hideSaveGroupingModal();
  })
}

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

function changeIconTreeTypeList(value: any) {

}

function saveGroup() {
  saveGroupVo.value.iconTreeType = IconTreeType.R;
  saveUpdate(saveGroupVo.value).then((res: R<void>) => {

  })
}

function resetGroupModal() {

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
  width: 10%;
  height: 105px;
  line-height: 25px;
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
</style>