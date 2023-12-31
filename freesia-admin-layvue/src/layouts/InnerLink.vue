<template>
  <div :style="'height:' + height">
    <iframe
        :id="props.iframeId"
        style="width: 100%; height: 100%"
        :src="props.src + '?' + new Date().getTime()"
        :frameborder="'yes'"
    ></iframe>
  </div>
</template>

<script lang="ts" setup>
import {defineComponent, ref} from "vue";

defineComponent({
  name: "InnerLink"
})

interface InnerLinkProps {
  src: string,
  iframeId?: string
}

const props = withDefaults(defineProps<InnerLinkProps>(), {
  src: "/"
});
//
// onMounted(() => {
//   // 获取div标签
//   var activity = document.querySelector('#activity');
//   // 创建日期
//   var ts = new Date().getTime()
//   // 使用es6模板语法 将变量拼接到地址栏
//   document.write(`<iframe  src="` + `${props.src}?+${ts} "` + `style="width: 100%; height: 100%" :frameborder="'yes'" ></iframe>`)
// })

// const height = ref(document.documentElement.clientHeight + "px");
const height = ref(820 + "px");
</script>

<style>
layui-body {
  display: flex;
  background: #5FB878;
  align-items: center;
  justify-content: center;
  color: white;
}
</style>
