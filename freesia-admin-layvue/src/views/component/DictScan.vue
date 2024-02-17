<template>
  <div>
    <template v-for="(item, index) in options">
      <template v-if="values.includes(item.value)">
        <scan
            v-if="!isFrame"
            :key="item.value + ''"
            :index="index"
        >{{ item.valueName + " " }}
        </scan>
        <scan
            v-else
            :key="item.value + ''"
            :index="index"
        >{{ "链接" }}
        </scan>
      </template>
    </template>
    <template v-if="unmatch && showValue">
      {{ unmatchArray | handleArray }}
    </template>
  </div>
</template>
<script lang="ts" setup>
import {computed, ref} from "vue";
// 记录未匹配的项
const unmatchArray = ref<any>([]);

const props = defineProps({
  // 数据
  options: {
    type: Array,
    default: null,
  },
  // 当前的值
  value: [Number, String, Array],
  // 当未找到匹配的数据时，显示value
  showValue: {
    type: Boolean,
    default: true,
  },
  isFrame: {
    type: Boolean,
    default: false
  }
});

const values = computed(() => {
  if (props.value && typeof props.value !== 'undefined') {
    return Array.isArray(props.value) ? props.value : [String(props.value)];
  } else {
    return [];
  }
});

const unmatch = computed(() => {
  unmatchArray.value = [];
  if (props.value && typeof props.value !== "undefined") {
    // 传入值为非数组
    if (!Array.isArray(props.value)) {
      if (props.options.some((v: any) => v.value == props.value)) {
        return false;
      }
      unmatchArray.value.push(props.value);
      return true;
    }
    // 传入值为Array
    props.value.forEach((item: any) => {
      if (!props.options.some((v: any) => v.value == item))
        unmatchArray.value.push(item);
    });
    return true;
  }
  // 没有value不显示
  return false;
});

function handleArray(array: any) {
  if (array.length === 0) return "";
  return array.reduce((pre: any, cur: any) => {
    return pre + " " + cur;
  });
}

function resolveCssStyle(item: any) {
  let cssStyle = item.cssStyle;
  if (cssStyle) {
    return JSON.parse(cssStyle);
  }
  return {}
}
</script>
