<template>
  <lay-container :fluid="true" style="padding: 10px">
    <BudgetStatistic :dataSource="echartCapacityOptionEntityList"/>
    <lay-row space="20">
      <lay-col md="12">
        <CostTypeRatePie></CostTypeRatePie>
      </lay-col>
      <lay-col md="12">
        <AccountBudget :dataSource="echartCapacityOptionEntityList"/>
      </lay-col>
    </lay-row>
    <lay-row :space="20">
      <lay-col md="24">
        <CostCountCalendarNearYear/>
      </lay-col>
    </lay-row>
    <lay-row :space="20">
      <lay-col md="24" sm="16" xs="24">
        <lay-row :space="20">
          <lay-col :md="24">
            <CostLine></CostLine>
          </lay-col>
          <lay-col :md="24">
            <lay-card>
              <lay-tab type="brief" v-model="currentIndex" @change="doWeekCostRank">
                <lay-tab-item title="周消费排名" id="0">
                  <div ref="weekCostRankRef" style="height: 600px"></div>
                </lay-tab-item>
                <lay-tab-item title="月消费排名" id="1">
                  <div ref="monthCostRankRef" style="height: 600px"></div>
                </lay-tab-item>
              </lay-tab>
            </lay-card>
          </lay-col>
        </lay-row>
      </lay-col>
      <!--      <lay-col md="8" sm="8" xs="12">-->
      <!--        <lay-row :space="20">-->
      <!--          <lay-col :md="24">-->
      <!--            <lay-card>-->
      <!--              <template #title>效果报告</template>-->
      <!--              <div class="task-progress">-->
      <!--                <span>80%</span>-->
      <!--                <span class="task-progress-title">转化率</span>-->
      <!--                <lay-progress percent="80"></lay-progress>-->
      <!--              </div>-->
      <!--              <div class="task-progress">-->
      <!--                <span>80%</span>-->
      <!--                <span class="task-progress-title">签到率</span>-->
      <!--                <lay-progress percent="80"></lay-progress>-->
      <!--              </div>-->
      <!--            </lay-card>-->
      <!--          </lay-col>-->
      <!--          <lay-col :md="24">-->
      <!--            <lay-card>-->
      <!--              <template #title>效果报告</template>-->
      <!--              <div class="task-progress">-->
      <!--                <span>80%</span>-->
      <!--                <span class="task-progress-title">转化率</span>-->
      <!--                <lay-progress percent="80"></lay-progress>-->
      <!--              </div>-->
      <!--              <div class="task-progress">-->
      <!--                <span>80%</span>-->
      <!--                <span class="task-progress-title">转化率</span>-->
      <!--                <lay-progress percent="80"></lay-progress>-->
      <!--              </div>-->
      <!--            </lay-card>-->
      <!--          </lay-col>-->
      <!--          <lay-col :md="24">-->
      <!--            <lay-card>-->
      <!--              <template #title>作者寄语</template>-->
      <!--              <p style="line-height: 40px">-->
      <!--                原想将澎湃的爱平平稳稳放置你手心，奈何我徒有一股蛮劲，只顾向你跑去，一个不稳跌的满身脏兮兮。试图爬起的我，-->
      <!--                心想你会不会笑我 " 献爱献的这样笨拙, 怎么不知避开爱里的埋伏 "-->
      <!--              </p>-->
      <!--            </lay-card>-->
      <!--          </lay-col>-->
      <!--        </lay-row>-->
      <!--      </lay-col>-->
    </lay-row>
  </lay-container>
</template>
<script lang="ts">
import CostTypeRatePie from "./CostTypeRatePie.vue";
import CostCountCalendarNearYear from "./CostCountCalendarNearYear.vue";
import CostLine from "./CostLine.vue";
import AccountBudget from "./BudgetCapacity.vue";

export default {
  name: "Accounts",
  components: {AccountBudget, CostLine, CostCountCalendarNearYear, CostTypeRatePie},
};
</script>
<script lang="ts" setup>
import {onBeforeUnmount, onMounted, ref} from 'vue'
import {findBudgetCapacity} from "@/api/account/AccountBudget";
import {AccountBudgetVo, EchartCapacityOptionEntity} from "@/types/account/AccountBudget";
import * as echarts from "echarts";
import {findRankByCostType} from "@/api/account/Account";
import {DateScope, EchartStackedHorizontalBarOptionEntity, FindRankByCostTypeVo} from "@/types/account/Account";
import {R} from "@/types/Result";
import {add} from "@layui/layui-vue/types/component/inputNumber/math";
import {layer} from "@layui/layui-vue";

/*INIT*/
onMounted(() => {
  findBudgetCapacity(accountBudgetVo.value).then((res: any) => {
    if (res.code === 200) {
      echartCapacityOptionEntityList.value = res.data
    }
  })
  doWeekCostRank();
})

onBeforeUnmount(() => {
  if (weekCostRankChart) {
    weekCostRankChart.dispose();
  }
  if (monthCostRankChart) {
    monthCostRankChart.dispose();
  }
});
/*INIT*/

/* VAR*/
const currentIndex = ref('0')
const accountBudgetVo = ref<AccountBudgetVo>({});
const findRankByCostTypeVo = ref<FindRankByCostTypeVo>({});
const echartCapacityOptionEntityList = ref<Array<EchartCapacityOptionEntity>>([]);
const weekCostRankRef = ref();
const monthCostRankRef = ref();
let weekCostRankChart: echarts.ECharts | null = null;
let monthCostRankChart: echarts.ECharts | null = null;
/* VAR*/

/*FUNCTION*/
function doWeekCostRank() {
  if (currentIndex.value === '0') {
    if (weekCostRankChart !== null) return;
    findRankByCostTypeVo.value.dateScope = DateScope.WEEK
  } else if (currentIndex.value === '1') {
    if (monthCostRankChart !== null) return;
    findRankByCostTypeVo.value.dateScope = DateScope.MONTH
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

<style lang="less" scoped>
#main {
  width: 100%;
  height: 400px;
}

.shortcut {
  text-align: center;

  i {
    display: inline-block;
    width: 100%;
    height: 60px;
    line-height: 60px;
    text-align: center;
    border-radius: 2px;
    font-weight: 500;
    font-size: 30px;
    background-color: #f8f8f8;
    color: #333;
    transition: all 0.3s;
    -webkit-transition: all 0.3s;
  }

  cite {
    position: relative;
    top: 2px;
    display: block;
    color: #666;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    font-size: 14px;
  }
}

.shortcut:hover {
  i {
    font-weight: 700;
    background-color: #009b410f;
    color: #009688;
    box-shadow: 1px 1px 4px #cccccc53;
  }

  cite {
    font-weight: 600;
    color: #009688;
  }
}

.agency {
  display: block;
  padding: 10.5px 16px;
  background-color: #f8f8f8;
  color: #999;
  border-radius: 2px;

  h3 {
    padding-bottom: 10px;
    font-size: 12px;
  }

  p cite {
    font-style: normal;
    font-size: 30px;
    font-weight: 300;
    color: #009688;
  }
}

.task-progress {
  padding: 10px 5px;

  .task-progress-title {
    right: 20px;
    position: absolute;
    color: #999;
  }

  .layui-progress {
    margin-top: 10px;
  }
}
</style>
