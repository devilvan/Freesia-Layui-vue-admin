<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: auto">
      <lay-container>
        <lay-timeline v-for="(dto, index) in giteeCommitsResponseDtoList" :key="index">
          <lay-timeline-item :title="dto.commit.author.date">
            <p>
              {{ dto.commit.author.name }}
              <br>{{ dto.commit.message }}
            </p>
          </lay-timeline-item>
        </lay-timeline>
      </lay-container>
    </div>
  </div>
</template>
<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useTab} from "../../../layouts/composable/useTab";
import {requestGiteeCommits} from "../../../api/dashboard/Gitee";
import {GiteeCommitsResponseDto} from "../../../types/dashboard/Gitee";
import {layer} from "@layui/layui-vue";
/* INIT*/
const {closeOpen} = useTab();

onMounted(() => {
  loadDataSource();
  // showCharts()
})
/* INIT*/
/* VAR*/
const giteeCommitsResponseDtoList = ref<Array<GiteeCommitsResponseDto>>([])
/* VAR*/
const loadDataSource = () => {
  requestGiteeCommits().then((res: any) => {
    if (res.code === 200) {
      giteeCommitsResponseDtoList.value = res.data;
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

.footer {
  width: 100%;
  display: flex;
  align-items: center;
  padding: 10px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-top: 1px solid whitesmoke;
  line-height: 60px;
  height: 60px;
}

.footer-button {
  right: 50px;
  position: absolute;
}

.row-padding {
  margin-top: 20px;
}
</style>
