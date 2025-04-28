<template>
  <lay-collapse v-model="openKeys">
    <div v-for="[key, value] in props.dataSource" :key="key">
<!--      <lay-collapse-item :title="key" :id="key">-->
<!--        <ul class="site-doc-icon">-->
<!--          <li v-for="(item, index) of value" :key="index">-->
<!--            <div @click="selectIcon(item)">-->
<!--              <SvgIcon :name="item.url" :desc="item.name"></SvgIcon>-->
<!--            </div>-->
<!--          </li>-->
<!--        </ul>-->
<!--      </lay-collapse-item>-->
      <lay-collapse-item :title="key" :id="key">
        <ul class="site-doc-icon">
          <li>
            <SvgIcon class="svgIcon"
                     :name="'http://127.0.0.1:9002/freesia/icon/2025/04/24/61ca68d0a33b471f89339a5de53aa518.svg'"
                     :desc="'添加图标'"
            ></SvgIcon>
          </li>
          <li v-show="item.iconTreeType === IconTreeType.L && item.children?.length > 0"
              v-for="(item, index) of value"
              :key="index">
            <SvgIcon class="svgIcon" :name="item.url" :desc="item.name"></SvgIcon>
          </li>
        </ul>
      </lay-collapse-item>
    </div>
  </lay-collapse>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import SvgIcon from "@/views/component/svg/SvgIcon.vue";
import {IconTreeType} from "@/types/common/icon/template/IconTemplateDetail";

/*INIT*/
const props = defineProps({
  dataSource: {
    type: Array,
    required: true
  },
  color: {
    type: String,
    default: '#009688'
  },
  size: {
    type: String,
    default: '3em'
  },
})
onMounted(() => {
})
/*INIT*/


/*VAR*/
const emit = defineEmits(['callBack']);
const openKeys = ref<Array<string>>(<Array<string>>['0', '1']);
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
  width: 20%;
  height: 120px;
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
</style>