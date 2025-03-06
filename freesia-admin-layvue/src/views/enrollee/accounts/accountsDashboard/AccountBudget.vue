<template>
  <lay-card>
    <template #title>{{ budgetTitle }}</template>
    <lay-row :space="10">
      <lay-col>
        <div ref="budgetCapacityEchartRef" style="height: 500px"></div>
      </lay-col>
    </lay-row>
  </lay-card>
</template>
<script lang="ts">
export default {
  name: "AccountBudget",
};
</script>
<script setup lang="ts">
/*INIT*/
import {onMounted, ref} from "vue";
import * as echarts from "echarts";
import {AccountBudgetVo} from "@/types/account/AccountBudget";
import {findBudgetCapacity} from "@/api/account/Account";
import {EchartCapacityOptionEntity} from "@/types/account/AccountBudget";

onMounted(() => {
  findBudgetCapacity(accountBudgetVo.value).then((res: any) => {
    if (res.code === 200) {
      let data = res.data;
      if (data) {
        let offset = -125
        for (let i = 0; i < data.length; i++) {
          let tmp = data[i]
          gaugeData.value.push({
            value: tmp.value,
            // name: `${tmp.name}\n（${tmp.durationFrom}-${tmp.durationTo}）`,
            name: `${tmp.name}`,
            title: {
              offsetCenter: ['-160%', `${offset}%`]
            },
            detail: {
              valueAnimation: true,
              offsetCenter: ['-160%', `${offset + 15}%`]
            },
          })
          offset += 40
        }
        budgetCapacityEchart = echarts.init(budgetCapacityEchartRef.value)
        budgetCapacityEchart.setOption(option.value)
      }
    }
  })

})
/*INIT*/


/*VAR*/
const budgetTitle = '我的预算';
const myChart = ref();
let budgetCapacityEchart: echarts.ECharts | null = null;
const budgetCapacityEchartRef = ref(null)
const accountBudgetVo = ref<AccountBudgetVo>({});
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
/*FUNCTION*/
</script>

<style scoped>

</style>