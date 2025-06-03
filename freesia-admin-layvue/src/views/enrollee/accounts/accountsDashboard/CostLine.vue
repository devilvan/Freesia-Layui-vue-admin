<template>
  <lay-card shadow="hover">
    <template #title>{{ costLineChartTitle }}</template>
    <lay-form :model="findCostLineChartQueryVo" ref="findCostLineChartQueryRef" label-position="left">
      <lay-row :space="20">
        <lay-col :md="8">
          <lay-form-item label="时间范围：" prop="dateScope">
            <lay-radio v-model="findCostLineChartQueryVo.dateScope" name="action" :value="DateScope.WEEK"
                       label="近一周" @change="changeDateScope(DateScope.WEEK)"></lay-radio>
            <lay-radio v-model="findCostLineChartQueryVo.dateScope" name="action" :value="DateScope.MONTH"
                       label="月" @change="changeDateScope(DateScope.MONTH)"></lay-radio>
            <lay-radio v-model="findCostLineChartQueryVo.dateScope" name="action" :value="DateScope.YEAR"
                       label="年" @change="changeDateScope(DateScope.YEAR)"></lay-radio>
          </lay-form-item>
        </lay-col>
        <lay-col :md="8">
          <lay-form-item label="选择时间：" prop="month">
            <lay-date-picker v-if="findCostLineChartQueryVo.dateScope === DateScope.MONTH" style="width: 100%"
                             @change="doFindCostLineChart"
                             v-model="findCostLineChartQueryVo.dateValue" type="yearmonth" allow-clear
                             :format="sdf_YM" :inputFormat="sdf_YM" simple></lay-date-picker>
            <lay-date-picker v-if="findCostLineChartQueryVo.dateScope === DateScope.YEAR" style="width: 100%"
                             @change="doFindCostLineChart"
                             v-model="findCostLineChartQueryVo.dateValue" type="year" allow-clear
                             :format="sdf_Y" :inputFormat="sdf_Y" simple></lay-date-picker>
          </lay-form-item>
        </lay-col>
        <lay-col :md="8">
          <lay-form-item label="开销类型：" prop="type">
            <lay-select
                style="width: 100%"
                size="sm"
                v-model="findCostLineChartQueryVo.costType"
                :options="findSelectCostTypeList"
                :items="findSelectCostTypeList"
                :allow-clear="true"
                placeholder="请选择"
                @change="changeCostType"
            ></lay-select>
          </lay-form-item>
        </lay-col>
      </lay-row>
    </lay-form>
    <div v-if="findCostLineChartQueryVo.dateScope === DateScope.WEEK" ref="weekCostLineChartRef"
         style="height: 500px"></div>
    <div v-if="findCostLineChartQueryVo.dateScope === DateScope.MONTH" ref="monthCostLineChartRef"
         style="height: 500px"></div>
    <div v-if="findCostLineChartQueryVo.dateScope === DateScope.YEAR" ref="yearCostLineChartRef"
         style="height: 500px"></div>
  </lay-card>
</template>

<script lang="ts">
export default {
  name: "CostLine"
}
</script>
<script lang="ts" setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import {DateScope, FindCostLineChartVo} from "@/types/account/Account";
import * as echarts from "echarts";
import {findCostLineChart} from "@/api/account/Account";
import {findListSelectCostType} from "@/api/common/icon/template/IconTemplateHeader";
import {R} from "@/types/Result";
import {LaySelectEntity} from "@/types/Common";
import {layer} from "@layui/layui-vue";

/*INIT*/
const props = defineProps({
  // 数据
  title: {
    type: String,
    default: "开销折线图",
  },
});
onMounted(() => {
  findCostLineChartQueryVo.value.dateScope = DateScope.WEEK
  doFindListSelectCostType();
  doFindCostLineChart()
})
/*INIT*/

/*DESTROY*/
onBeforeUnmount(() => {
  if (weekCostLineChart) {
    weekCostLineChart.dispose();
  }
  if (monthCostLineChart) {
    monthCostLineChart.dispose();
  }
  if (yearCostLineChart) {
    yearCostLineChart.dispose();
  }
});
/*DESTROY*/

/*VAR*/
const costLineChartTitle = props.title
const weekCostLineChartRef = ref();
const monthCostLineChartRef = ref();
const yearCostLineChartRef = ref();
const findCostLineChartQueryVo = ref<FindCostLineChartVo>({});
const findCostLineChartQueryRef = ref(null)
let weekCostLineChart: echarts.ECharts | null = null;
let monthCostLineChart: echarts.ECharts | null = null;
let yearCostLineChart: echarts.ECharts | null = null;
const sdf_YM = 'YYYY-MM'
const sdf_Y = 'YYYY'
const findSelectCostTypeList = ref<LaySelectEntity[]>([]);
/*VAR*/

/*FUNCTION*/
function doFindCostLineChart() {
  let dateScope = findCostLineChartQueryVo.value.dateScope;
  // if (dateScope === DateScope.MONTH || dateScope === DateScope.YEAR) {
  //   let dateValue = findCostLineChartQueryVo.value.dateValue;
  //   if (!dateValue || dateValue === '') {
  //     return;
  //   }
  // }
  findCostLineChart(findCostLineChartQueryVo.value).then((res: any) => {
    if (res.code === 200) {
      let data = res.data;
      if (dateScope === DateScope.WEEK) {
        showWeekCostLineChart(data);
      } else if (dateScope === DateScope.MONTH) {
        showMonthCostLineChart(data);
      } else if (dateScope === DateScope.YEAR) {
        showYearCostLineChart(data);
      }
    }
  })
}

function changeDateScope(dateScope: string) {
  findCostLineChartQueryVo.value.dateScope = dateScope;
  findCostLineChartQueryVo.value.dateValue = ''
  doFindCostLineChart();
}

function showWeekCostLineChart(data: any) {
  let option = {
    title: {
      text: costLineChartTitle,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
    },
    toolbox: {
      show: true,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        dataView: {readOnly: false},
        magicType: {type: ['line', 'bar']},
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      data: data.xAxis || []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: data.series[0].data,
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        markLine: {
          data: [{type: 'average', name: 'Avg'}]
        },
        markPoint: {
          data: [
            {type: 'max', name: 'Max'},
            {type: 'min', name: 'Min'}
          ]
        },
      }
    ]
  }
  weekCostLineChart = echarts.init(weekCostLineChartRef.value)
  weekCostLineChart.setOption(option)
}

function showMonthCostLineChart(data: any) {
  let option = {
    title: {
      text: costLineChartTitle,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    toolbox: {
      show: true,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        dataView: {readOnly: false},
        magicType: {type: ['line', 'bar']},
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      data: data.xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: data.series[0].data,
        type: 'line',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        markLine: {
          data: [{type: 'average', name: 'Avg'}]
        },
        markPoint: {
          data: [
            {type: 'max', name: 'Max'},
            {type: 'min', name: 'Min'}
          ]
        },
      }
    ]
  }
  monthCostLineChart = echarts.init(monthCostLineChartRef.value)
  monthCostLineChart.setOption(option)
}

function showYearCostLineChart(data: any) {
  let option = {
    title: {
      text: costLineChartTitle,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    toolbox: {
      show: true,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        dataView: {readOnly: false},
        magicType: {type: ['line', 'bar']},
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      data: data.xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: data.series[0].data,
        type: 'line',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        markLine: {
          data: [{type: 'average', name: 'Avg'}]
        },
        markPoint: {
          data: [
            {type: 'max', name: 'Max'},
            {type: 'min', name: 'Min'}
          ]
        },
      }
    ]
  }
  yearCostLineChart = echarts.init(yearCostLineChartRef.value)
  yearCostLineChart.setOption(option)
}

function doFindListSelectCostType() {
  findListSelectCostType().then((res: R<LaySelectEntity[]>) => {
    findSelectCostTypeList.value = res.data
  }).catch(e => {
    layer.confirm(e.message)
  })
}

function changeCostType(value: any) {
  findCostLineChartQueryVo.value.costType = value;
  doFindCostLineChart();
}



/*FUNCTION*/

</script>

<style scoped>

</style>