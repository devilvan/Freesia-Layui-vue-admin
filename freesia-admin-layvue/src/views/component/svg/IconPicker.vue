<template>
  <lay-collapse v-model="props.openKeys">
    <div v-for="(item, index) in dataSource" :key="item.name">
      <!--      <lay-checkcard-group :single="true" v-model="checkCardGroupKey" @change="selectIcon(item)">-->
      <!--      </lay-checkcard-group>-->
      <!--        <ul class="site-doc-icon">-->
      <!--          <li v-if="item.children" v-for="(cItem, cIndex) of item.children" :key="cIndex">-->
      <!--            <lay-tooltip :visible="false" trigger="hover" :content="cItem.name">-->
      <!--              <SvgIcon :name="cItem.url" :desc="cItem.name"></SvgIcon>-->
      <!--            </lay-tooltip>-->
      <!--          </li>-->
      <!--        </ul>-->
      <lay-row>
        <lay-collapse-item :title="item.name" :id="item.name">
          <lay-col :md="3" v-for="(cItem, index) of item.children">
            <lay-checkcard-group :single="true" v-model="checkCardGroupKey" @change="selectIcon(cItem)">
              <lay-tooltip :visible="false" trigger="hover" :content="cItem.name">
                <lay-checkcard style="width: 120px; height: 120px" class="checkCard" :cover="cItem.url"
                               v-model="cItem.id"
                               :value="cItem.id"
                ></lay-checkcard>
                <div class="container-desc">{{ cItem.name }}</div>
              </lay-tooltip>
            </lay-checkcard-group>
          </lay-col>
        </lay-collapse-item>
      </lay-row>
    </div>
  </lay-collapse>
</template>

<script setup lang="ts">
import SvgIcon from "@/views/component/svg/SvgIcon.vue";
import {ref} from "vue";
import {FindCommonIconEntity} from "@/types/common/icon/Icon";
import {Operate} from "@/types/Constants";

/*INIT*/
const props = defineProps({
  dataSource: {
    type: [],
    required: true
  },
  openKeys: {
    type: Array,
    required: true
  }
})
/*INIT*/


/*VAR*/
const emit = defineEmits(['callBack']);
// const openKeys = ref<Array<string>>(<Array<string>>['0', '1']);
// const dataSource = ref<Map<string, FindTreeIconTreeTypeEntity[]>>()
const checkCardGroupKey = ref('')
/*VAR*/

/*FUNCTION*/
function selectIcon(layIcon: any) {
  emit('callBack', layIcon);
}

/*FUNCTION*/

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

.container {
  height: inherit;
  width: inherit;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  text-align: center;
}

.container-desc {
  padding: 0 30%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>