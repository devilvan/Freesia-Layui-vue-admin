<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: auto">
      <lay-container style="margin-top: 20px">
        <lay-panel>
          <div style="margin-top: 10px; margin-bottom: 30px">
            <lay-line contentPosition="left" style="font-size: 16pt; font-weight: lighter" borderWidth="2px" theme="cyan">Gitee 提交更新记录
            </lay-line>
          </div>
          <lay-timeline v-for="(list, key) in giteeCommitsResponseDtoMapList" :key="key">
            <lay-timeline-item :title="key">
              <div v-for="(dto, listIndex) in list" :key="listIndex"
                   :style="{paddingBottom: listIndex !== list.length - 1 ? '20px' : '0px'} ">
                <p>
                  <lay-avatar :src="dto.avatarUrl" :radius="true" @click="preview(dto.avatarUrl)"></lay-avatar>&nbsp;&nbsp;
                  <scan style="font-size: 16pt">{{ dto.name }}</scan>&nbsp;&nbsp;
                  <scan>{{ dto.date }}</scan>
                </p>
                <p style="font-size: 12pt">{{ dto.message }}</p>
                <lay-line theme="blue" borderWidth="1px"></lay-line>
                <scan></scan>
              </div>
            </lay-timeline-item>
          </lay-timeline>
        </lay-panel>
      </lay-container>
    </div>
  </div>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "Gitee",
};
</script>
<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useTabStore} from "../../../layouts/composable/useTabStore";
import {findGiteeCommits} from "../../../api/dashboard/Gitee";
import {layer} from "@layui/layui-vue";
import {FindGiteeCommitsEntity} from "../../../types/dashboard/Gitee";
/* INIT*/
const {closeOpen} = useTabStore();

onMounted(() => {
  loadDataSource();
  // showCharts()
})
/* INIT*/
/* VAR*/
const giteeCommitsResponseDtoMapList = ref<Map<String, Array<FindGiteeCommitsEntity>>>(new Map<String, Array<FindGiteeCommitsEntity>>())
/* VAR*/
const loadDataSource = () => {
  findGiteeCommits().then((res: any) => {
    if (res.code === 200) {
      giteeCommitsResponseDtoMapList.value = res.data;

    } else {
      layer.msg(res.msg, {icon: 2})
    }
  })
}

function formatNumber(num: number, decimalPlaces: number) {
  const pattern = `^-?\\d+(?:\.\\d{0,${decimalPlaces}})?`;
  const regex = new RegExp(pattern);
  const formatted = num.toString().match(regex);
  return formatted ? formatted[0] : "";
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
