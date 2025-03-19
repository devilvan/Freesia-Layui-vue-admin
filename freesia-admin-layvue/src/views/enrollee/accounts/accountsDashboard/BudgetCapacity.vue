<template>
  <lay-card shadow="hover">
    <template #title>{{ props.title }}</template>
    <lay-row :space="10">
      <lay-col>
        <div ref="budgetCapacityEchartRef" style="height: 500px"></div>
      </lay-col>
    </lay-row>
  </lay-card>
</template>
<script lang="ts">
export default {
  name: "BudgetCapacity",
};
</script>
<script setup lang="ts">
/*INIT*/
import {ref, watch} from "vue";
import * as echarts from "echarts";
import {AccountBudgetVo} from "@/types/account/AccountBudget";
import {EchartCapacityOptionEntity} from "@/types/account/AccountBudget";

const props = defineProps({
  title: {
    required: false,
    default: '我的预算'
  },
  dataSource: {
    required: true,
    type: Array<EchartCapacityOptionEntity>
  }
})

watch(
    () => props.dataSource,
    (val) => {
      accountBudgetEntityList.value = val;
      doBuildBudgetCapacity();
    },
);
/*INIT*/


/*VAR*/
let budgetCapacityEchart: echarts.ECharts | null = null;
const budgetCapacityEchartRef = ref(null)
const accountBudgetVo = ref<AccountBudgetVo>({});
const accountBudgetEntityList = ref<Array<EchartCapacityOptionEntity>>([]);
let gaugeData = ref<Array<EchartCapacityOptionEntity>>([])
let option = ref({
  tooltip: {
    show: true,
    formatter: `{b}: {c}% <br/>`
  },
  series: [
    {
      type: 'gauge',
      startAngle: 0,
      endAngle: -360,
      pointer: {
        show: false
      },
      progress: {
        show: true,
        overlap: false,
        roundCap: true,
        clip: false,
        itemStyle: {
          borderWidth: 1,
          borderColor: '#464646'
        }
      },
      axisLine: {
        lineStyle: {
          width: 80
        },
      },
      splitLine: {
        show: false,
        distance: 0,
        length: 10
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        show: false,
        distance: 50
      },
      data: gaugeData.value,
      title: {
        fontSize: 14
      },
      detail: {
        width: 50,
        height: 14,
        fontSize: 14,
        color: 'inherit',
        borderColor: 'inherit',
        borderRadius: 20,
        borderWidth: 1,
        formatter: '{value}%'
      }
    },
    {
      data: gaugeData.value,
      pointer: {
        show: false
      }
    }
  ]
});
/*VAR*/

/*FUNCTION*/
/**
 * 查询预算容量图
 */
function doBuildBudgetCapacity() {
  let data = props.dataSource;
  // let data = accountBudgetEntityList.value || props.dataSource;
  if (data) {
    let offset = -125
    for (let i = 0; i < data.length; i++) {
      let tmp = data[i]
      gaugeData.value.push({
        value: tmp.value,
        // name: `${tmp.name}\n（${tmp.durationFrom}-${tmp.durationTo}）`,
        name: `${tmp.name}`,
        title: {
          offsetCenter: ['-130%', `${offset}%`]
        },
        detail: {
          valueAnimation: true,
          offsetCenter: ['-130%', `${offset + 15}%`]
        },
      })
      offset += 40
    }
    budgetCapacityEchart = echarts.init(budgetCapacityEchartRef.value)
    budgetCapacityEchart.setOption(option.value)
  }
}

/*FUNCTION*/
</script>

<style scoped>

</style>