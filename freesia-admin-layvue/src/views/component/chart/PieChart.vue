<template>
  <div :id="id" :style="{height:height,width:this.width}"/>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts'
import {onMounted} from "vue";

interface PieChartProps {
  id?: string;
  data: any,
  width?: string,
  height?: string
}

interface PicChartDataFormat {
  name: string,
  value: string,
}

const props = withDefaults(defineProps<PieChartProps>(), {
  id: 'chartRef',
  width: '100%',
  height: '100%'
});

const commandStatOptions = {
  legend: {
    top: 'bottom'
  },
  toolbox: {
    show: true,
    feature: {
      mark: {show: true},
      dataView: {show: true, readOnly: false},
      restore: {show: true},
      saveAsImage: {show: true}
    }
  },
  series: [
    {
      name: 'Nightingale Chart',
      type: 'pie',
      radius: [50, 250],
      center: ['50%', '50%'],
      roseType: 'area',
      itemStyle: {
        borderRadius: 8
      },
      data: props.data
    }
  ]
};


onMounted(() => {
  initChart();
});

function initChart() {
  let id = document.getElementById(props.id);

  let chart = echarts.init(id)
  chart.setOption(commandStatOptions)
}
</script>
