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
import {ref} from "vue";
import * as echarts from "echarts";

const gaugeData = [
  {
    value: 20,
    name: 'Perfect',
    title: {
      offsetCenter: ['0%', '-30%']
    },
    detail: {
      valueAnimation: true,
      offsetCenter: ['0%', '-20%']
    }
  },
  {
    value: 40,
    name: 'Good',
    title: {
      offsetCenter: ['0%', '0%']
    },
    detail: {
      valueAnimation: true,
      offsetCenter: ['0%', '10%']
    }
  },
  {
    value: 60,
    name: 'Commonly',
    title: {
      offsetCenter: ['0%', '30%']
    },
    detail: {
      valueAnimation: true,
      offsetCenter: ['0%', '40%']
    }
  }
];
let option = {
  series: [
    {
      type: 'gauge',
      startAngle: 90,
      endAngle: -270,
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
          width: 40
        }
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
      data: gaugeData,
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
      data: gaugeData,
      pointer: {
        show: false
      }
    }
  ]
};
setInterval(function () {
  gaugeData[0].value = +(Math.random() * 100).toFixed(2);
  gaugeData[1].value = +(Math.random() * 100).toFixed(2);
  gaugeData[2].value = +(Math.random() * 100).toFixed(2);
  budgetCapacityEchart = echarts.init(budgetCapacityEchartRef.value)
  budgetCapacityEchart.setOption(option)
}, 2000);
/*INIT*/


/*VAR*/
const budgetTitle = '我的预算';
const myChart = ref();
let budgetCapacityEchart: echarts.ECharts | null = null;
const budgetCapacityEchartRef = ref(null)
/*VAR*/

/*FUNCTION*/
/*FUNCTION*/
</script>

<style scoped>

</style>