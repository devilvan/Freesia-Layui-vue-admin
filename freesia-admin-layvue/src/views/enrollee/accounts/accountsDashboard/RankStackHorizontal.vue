<template>
  <lay-card>
    <lay-tab type="brief" v-model="currentIndex" @change="doFindCostRank">
      <lay-tab-item title="周消费排名" id="0">
        <div ref="weekCostRankRef" style="height: 600px"></div>
      </lay-tab-item>
      <lay-tab-item title="月消费排名" id="1">
        <div ref="monthCostRankRef" style="height: 600px"></div>
      </lay-tab-item>
    </lay-tab>
  </lay-card>
</template>

<script lang="ts">
export default {
  name: "RankStackHorizontal"
}
</script>
<script lang="ts" setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import {
  DateScope,
  EchartStackedHorizontalBarOptionEntity,
  FindRankByCostTypeVo
} from "@/types/account/Account";
import * as echarts from "echarts";
import {findRankByCostType} from "@/api/account/Account";
import {R} from "@/types/Result";
import {layer} from "@layui/layui-vue";

/*INIT*/
const props = defineProps({
  // 数据
  title: {
    type: String,
    default: "近一年支付",
  },
});
onMounted(() => {
  doFindCostRank();
})
/*INIT*/

/*DESTROY*/
onBeforeUnmount(() => {
  if (weekCostRankChart) {
    weekCostRankChart.dispose();
  }
  if (monthCostRankChart) {
    monthCostRankChart.dispose();
  }
});
/*DESTROY*/

/*VAR*/
const currentIndex = ref('0')
const weekCostRankRef = ref();
const monthCostRankRef = ref();
let weekCostRankChart: echarts.ECharts | null = null;
let monthCostRankChart: echarts.ECharts | null = null;
const findRankByCostTypeVo = ref<FindRankByCostTypeVo>({});
/*VAR*/


/*FUNCTION*/
function doFindCostRank() {
  if (currentIndex.value === '0') {
    if (weekCostRankChart !== null) return ;
    findRankByCostTypeVo.value.dateScope = DateScope.WEEK
  } else if (currentIndex.value === '1') {
    if (monthCostRankChart !== null) return ;
    findRankByCostTypeVo.value.dateScope = DateScope.MONTH
  } else {
    return;
  }
  findRankByCostType(findRankByCostTypeVo.value).then((res: R<EchartStackedHorizontalBarOptionEntity>) => {
    if (res.code === 200) {
      let echartStackedHorizontalBarOptionEntity: EchartStackedHorizontalBarOptionEntity | undefined = res.data;
      let series = echartStackedHorizontalBarOptionEntity?.series?.map(item => {
        return {
          name: item.name,
          type: 'bar',
          stack: 'total',
          label: {
            show: true
          },
          emphasis: {
            focus: 'series'
          },
          data: item.value
        }
      })
      let option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            animation: true,
          },
          formatter: function (params: any, ticket: string, callback: (ticket: string, html: string) => {}) {
            params = params.filter((item: any) => item.value).sort((i1: any, i2: any) => i2.value - i1.value)
            let out = `<div style="width: 200px;font-size: 12pt">${params[0]?.axisValue}</div></br>`;
            let totalAmount = params.reduce((accumulator: any, currentValue: any) => accumulator + currentValue.value, 0);
            params.forEach((item: any) => {
              const percent = ((item.data / totalAmount) * 100).toFixed(2);
              out += `<div>${item.marker} ${item.seriesName}：
                        <span style="display:inline-block;margin-left:4px;margin-right:2px;border-radius:10px;font-weight:bold;color:${item.color}">${item.data}</span>
                        <span style="border-radius:10px;font-weight:bold;color:${item.color}">(${percent}%)</span>
                      </div>`
            })
            out += `</br><div style="font-weight:bold">总金额：${totalAmount.toFixed(2)}元</div>`
            return out;
          }
        },
        legend: {},
        grid: {
          left: '3%',
          right: '3%',
          bottom: '3%',
          containLabel: true
        },

        xAxis: {
          type: 'value',
        },
        yAxis: {
          type: 'category',
          data: echartStackedHorizontalBarOptionEntity?.yAxis
        },
        series: series
      };
      if (currentIndex.value === '0') {
        weekCostRankChart = echarts.init(weekCostRankRef.value);
        weekCostRankChart.setOption(option)
      } else if (currentIndex.value === '1') {
        monthCostRankChart = echarts.init(monthCostRankRef.value);
        monthCostRankChart.setOption(option)
      }

    }
  }).catch(e => {
    layer.confirm(e.message)
  })
}

/*FUNCTION*/
</script>

<style scoped>

</style>