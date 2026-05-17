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
  FindRankByCostTypeVo, PaymentSign
} from "@/types/account/Account";
import * as echarts from "echarts";
import {findRankByCostType} from "@/api/account/Account";
import {R} from "@/types/Result";
import {layer} from "@layui/layui-vue";
import {useAccountCostStore} from "@/store/accountCost";

/*INIT*/
const props = defineProps({
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
const accountCostStore = useAccountCostStore()
const currentIndex = ref('0')
const weekCostRankRef = ref();
const monthCostRankRef = ref();
let weekCostRankChart: echarts.ECharts | null = null;
let monthCostRankChart: echarts.ECharts | null = null;
const findRankByCostTypeVo = ref<FindRankByCostTypeVo>({});
let currentHoverSeriesIndex = ref<number | null>(null);
let currentHoverDataIndex = ref<number | null>(null);
let seriesData = ref<any[]>([]);
let yAxisData = ref<any[]>([]);
/*VAR*/


/*FUNCTION*/
function doFindCostRank() {
  if (currentIndex.value === '0') {
    if (weekCostRankChart !== null) return;
    findRankByCostTypeVo.value.dateScope = DateScope.WEEK
  } else if (currentIndex.value === '1') {
    if (monthCostRankChart !== null) return;
    findRankByCostTypeVo.value.dateScope = DateScope.MONTH
  } else {
    return;
  }
  findRankByCostTypeVo.value.allTenantFlag = accountCostStore.allTenantFlag
  findRankByCostType(findRankByCostTypeVo.value).then((res: R<EchartStackedHorizontalBarOptionEntity>) => {
    if (res.code === 200) {
      let echartStackedHorizontalBarOptionEntity: EchartStackedHorizontalBarOptionEntity | undefined = res.data;
      seriesData.value = echartStackedHorizontalBarOptionEntity?.series || [];
      yAxisData.value = echartStackedHorizontalBarOptionEntity?.yAxis || [];
      let series = echartStackedHorizontalBarOptionEntity?.series?.map(item => {
        if (PaymentSign.EXPENSES === item.stack) {
          return {
            name: item.name,
            type: 'bar',
            stack: 'expenses',
            label: {
              show: false
            },
            emphasis: {
              focus: 'series'
            },
            data: item.value,
            paymentSign: PaymentSign.EXPENSES
          }
        } else if (PaymentSign.INCOME === item.stack) {
          return {
            name: item.name,
            type: 'bar',
            stack: 'income',
            label: {
              show: false
            },
            emphasis: {
              focus: 'series'
            },
            data: item.value,
            paymentSign: PaymentSign.INCOME
          }
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
            params = params.filter((item: any) => item.value);
            
            const isHoveringOnBar = currentHoverSeriesIndex.value !== null && currentHoverDataIndex.value !== null;
            
            if (isHoveringOnBar) {
              const hoveredItem = params.find((p: any) => 
                p.seriesIndex === currentHoverSeriesIndex.value && 
                p.dataIndex === currentHoverDataIndex.value
              );
              if (hoveredItem) {
                const hoveredSeries = series.find((s: any, idx: number) => idx === hoveredItem.seriesIndex);
                if (hoveredSeries && hoveredSeries.stack) {
                  const targetStack = hoveredSeries.stack;
                  params = params.filter((p: any) => {
                    const pSeries = series.find((s: any, idx: number) => idx === p.seriesIndex);
                    return pSeries && pSeries.stack === targetStack;
                  });
                }
              }
            }
            
            params = params.sort((i1: any, i2: any) => i2.value - i1.value);
            
            let out = `<div style="width: 200px;font-size: 12pt">${params[0]?.axisValue}</div></br>`;
            
            if (isHoveringOnBar) {
              let totalAmount = params.reduce((accumulator: any, currentValue: any) => accumulator + currentValue.value, 0);
              params.forEach((item: any) => {
                const percent = ((item.data / totalAmount) * 100).toFixed(2);
                const isHighlighted = currentHoverSeriesIndex.value === item.seriesIndex && currentHoverDataIndex.value === item.dataIndex;
                const borderStyle = isHighlighted ? `border: 2px solid ${item.color}; padding: 2px 4px; border-radius: 4px; margin: 2px 0; background-color: rgba(${hexToRgb(item.color)}, 0.1);` : '';
                out += `<div style="${borderStyle}">${item.marker} ${item.seriesName}：
                          <span style="display:inline-block;margin-left:4px;margin-right:2px;border-radius:10px;font-weight:bold;color:${item.color}">${item.data?.toFixed(2)}</span>
                          <span style="border-radius:10px;font-weight:bold;color:${item.color}">(${percent}%)</span>
                        </div>`
              })
              const hoveredSeries = series.find((s: any, idx: number) => idx === currentHoverSeriesIndex.value);
              const amountLabel = hoveredSeries?.stack === 'expenses' ? '支出金额' : '收入金额';
              out += `</br><div style="font-weight:bold">${amountLabel}：${totalAmount.toFixed(2)}元</div>`;
            } else {
              const expensesParams = params.filter((p: any) => {
                const pSeries = series.find((s: any, idx: number) => idx === p.seriesIndex);
                return pSeries && pSeries.stack === 'expenses';
              });
              const incomeParams = params.filter((p: any) => {
                const pSeries = series.find((s: any, idx: number) => idx === p.seriesIndex);
                return pSeries && pSeries.stack === 'income';
              });
              
              const expensesTotal = expensesParams.reduce((acc: any, curr: any) => acc + curr.value, 0);
              const incomeTotal = incomeParams.reduce((acc: any, curr: any) => acc + curr.value, 0);
              
              params.forEach((item: any) => {
                const pSeries = series.find((s: any, idx: number) => idx === item.seriesIndex);
                const totalAmount = pSeries?.stack === 'expenses' ? expensesTotal : incomeTotal;
                const percent = totalAmount > 0 ? ((item.data / totalAmount) * 100).toFixed(2) : '0.00';
                out += `<div>${item.marker} ${item.seriesName}：
                          <span style="display:inline-block;margin-left:4px;margin-right:2px;border-radius:10px;font-weight:bold;color:${item.color}">${item.data?.toFixed(2)}</span>
                          <span style="border-radius:10px;font-weight:bold;color:${item.color}">(${percent}%)</span>
                        </div>`
              })
              out += `</br><div style="font-weight:bold">支出金额：${expensesTotal.toFixed(2)}元</div>`;
              out += `<div style="font-weight:bold">收入金额：${incomeTotal.toFixed(2)}元</div>`;
            }
            
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

        weekCostRankChart.group = 'weekCostRank';

        weekCostRankChart.on('mouseover', function (params: any) {
          if (params.dataIndex !== undefined) {
            currentHoverSeriesIndex.value = params.seriesIndex;
            currentHoverDataIndex.value = params.dataIndex;
          }
        });

        weekCostRankChart.on('mouseout', function () {
          currentHoverSeriesIndex.value = null;
          currentHoverDataIndex.value = null;
        });
      } else if (currentIndex.value === '1') {
        monthCostRankChart = echarts.init(monthCostRankRef.value);
        monthCostRankChart.setOption(option)

        monthCostRankChart.group = 'monthCostRank';

        monthCostRankChart.on('mouseover', function (params: any) {
          if (params.dataIndex !== undefined) {
            currentHoverSeriesIndex.value = params.seriesIndex;
            currentHoverDataIndex.value = params.dataIndex;
          }
        });

        monthCostRankChart.on('mouseout', function () {
          currentHoverSeriesIndex.value = null;
          currentHoverDataIndex.value = null;
        });
      }

    }
  }).catch(e => {
    layer.confirm(e.message)
  })
}

function hexToRgb(hex: string): string {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  if (result) {
    return `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}`;
  }
  return '0, 0, 0';
}

/*FUNCTION*/
</script>

<style scoped>

</style>