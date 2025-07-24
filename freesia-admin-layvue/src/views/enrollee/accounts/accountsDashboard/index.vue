<template>
  <lay-container :fluid="true" style="padding: 10px">
    <BudgetStatistic :dataSource="echartCapacityOptionEntityList"></BudgetStatistic>
    <lay-row space="20">
      <lay-col md="12">
        <CostTypeRatePie></CostTypeRatePie>
      </lay-col>
      <lay-col md="12">
        <AccountBudget :dataSource="echartCapacityOptionEntityList"></AccountBudget>
      </lay-col>
    </lay-row>
    <lay-row :space="20">
      <lay-col md="24">
        <CostCountCalendarNearYear></CostCountCalendarNearYear>
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
              <lay-tab type="brief" v-model="currentIndex">
                <lay-tab-item title="周消费排名" id="0">
                  <div ref="weekCostRankRef" style="height: 600px"></div>
                </lay-tab-item>
                <lay-tab-item title="月消费排名" id="1">
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
import BudgetStatistic from "./BudgetStatistic.vue";
import {findBudgetCapacity} from "@/api/account/AccountBudget";
import {AccountBudgetVo, EchartCapacityOptionEntity} from "@/types/account/AccountBudget";
import * as echarts from "echarts";
import {findRankByCostType} from "@/api/account/Account";

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
});
/*INIT*/

/* VAR*/
const currentIndex = ref('0')
const accountBudgetVo = ref<AccountBudgetVo>({});
const echartCapacityOptionEntityList = ref<Array<EchartCapacityOptionEntity>>([]);
const weekCostRankRef = ref();
let weekCostRankChart: echarts.ECharts | null = null;
/* VAR*/

/*FUNCTION*/
function doWeekCostRank() {
  let option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        // Use axis to trigger tooltip
        type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
      }
    },
    legend: {},
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    series: [
      {
        name: 'Direct',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: [320, 302, 301, 334, 390, 330, 320]
      },
      {
        name: 'Mail Ad',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: [120, 132, 101, 134, 90, 230, 210]
      },
      {
        name: 'Affiliate Ad',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: [220, 182, 191, 234, 290, 330, 310]
      },
      {
        name: 'Video Ad',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: [150, 212, 201, 154, 190, 330, 410]
      },
      {
        name: 'Search Engine',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: [820, 832, 901, 934, 1290, 1330, 1320]
      },
      {
        name: 'Direct7',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: [320, 302]
      },
    ]
  };
  weekCostRankChart = echarts.init(weekCostRankRef.value);
  weekCostRankChart.setOption(option)
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
