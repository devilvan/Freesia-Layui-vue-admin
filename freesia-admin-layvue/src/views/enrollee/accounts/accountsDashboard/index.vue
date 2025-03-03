<template>
  <lay-container :fluid="true" style="padding: 10px">
    <lay-row space="10">
      <lay-col md="10">
        <lay-card>
          <template #title>{{ costTypeRatePieTitle }}</template>
          <lay-row :space="10">
            <lay-col>
              <lay-form :model="findCostTypeRatePieQueryVo" ref="findCostTypeRatePieQueryRef" label-position="left">
                <lay-form-item label="按时间：" prop="paymentTimeRange">
                  <lay-date-picker style="width: 100%" @change="doChangeFindCostTypeRatePie"
                                   v-model="findCostTypeRatePieQueryVo.paymentTimeRange" allow-clear range
                                   type="datetime"
                                   :format="sdf_YMDHMS" :inputFormat="sdf_YMDHMS" :shortcuts="defaultShortcuts"
                                   simple></lay-date-picker>
                </lay-form-item>
              </lay-form>
            </lay-col>
          </lay-row>
          <div ref="costTypeRatePieRef" style="height: 450px"></div>
        </lay-card>
      </lay-col>
      <lay-col md="8">
        <AccountBudget></AccountBudget>
      </lay-col>
      <lay-col md="6">
        <lay-card>
          <template #title>{{ costCountCalendarLastYearTitle }}</template>
          <lay-row :space="10">
            <lay-col>
              <lay-form :model="findCostSumCalendarNearYearQueryVo" ref="findCostSumCalendarNearYearQueryRef"
                        label-position="left">
              </lay-form>
              <div ref="costSumCalendarNearYearRef" style="height: 500px"></div>
            </lay-col>
          </lay-row>
        </lay-card>
      </lay-col>
      <lay-col md="16" sm="16" xs="24">
        <lay-row :space="10">
          <lay-col :md="24">
            <lay-card>
              <template #title>{{ costLineChartTitle }}</template>
              <lay-form :model="findCostLineChartQueryVo" ref="findCostLineChartQueryRef" label-position="left">
                <lay-row :space="10">
                  <lay-col :md="12">
                    <lay-form-item label="时间范围：" prop="dateScope">
                      <lay-radio v-model="findCostLineChartQueryVo.dateScope" name="action" :value="DateScope.WEEK"
                                 label="近一周" @change="changeDateScope(DateScope.WEEK)"></lay-radio>
                      <lay-radio v-model="findCostLineChartQueryVo.dateScope" name="action" :value="DateScope.MONTH"
                                 label="月" @change="changeDateScope(DateScope.MONTH)"></lay-radio>
                      <lay-radio v-model="findCostLineChartQueryVo.dateScope" name="action" :value="DateScope.YEAR"
                                 label="年" @change="changeDateScope(DateScope.YEAR)"></lay-radio>
                    </lay-form-item>
                  </lay-col>
                  <lay-col :md="12">
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
                </lay-row>
              </lay-form>
              <div v-if="findCostLineChartQueryVo.dateScope === DateScope.WEEK" ref="weekCostLineChartRef"
                   style="height: 500px"></div>
              <div v-if="findCostLineChartQueryVo.dateScope === DateScope.MONTH" ref="monthCostLineChartRef"
                   style="height: 500px"></div>
              <div v-if="findCostLineChartQueryVo.dateScope === DateScope.YEAR" ref="yearCostLineChartRef"
                   style="height: 500px"></div>
            </lay-card>
          </lay-col>
          <lay-col :md="24">
            <lay-card>
              <lay-tab type="brief" v-model="currentIndex">
                <lay-tab-item title="今日热搜" id="1">
                  <lay-table
                      :columns="columns21"
                      :data-source="dataSource21"
                  ></lay-table>
                </lay-tab-item>
                <lay-tab-item title="今日热帖" id="2">
                  <lay-table
                      :columns="columns21"
                      :data-source="dataSource21"
                  ></lay-table>
                </lay-tab-item>
              </lay-tab>
            </lay-card>
          </lay-col>
        </lay-row>
      </lay-col>
      <lay-col md="8" sm="8" xs="12">
        <lay-row :space="10">
          <lay-col :md="24">
            <lay-card>
              <template #title>效果报告</template>
              <div class="task-progress">
                <span>80%</span>
                <span class="task-progress-title">转化率</span>
                <lay-progress percent="80"></lay-progress>
              </div>
              <div class="task-progress">
                <span>80%</span>
                <span class="task-progress-title">签到率</span>
                <lay-progress percent="80"></lay-progress>
              </div>
            </lay-card>
          </lay-col>
          <lay-col :md="24">
            <lay-card>
              <template #title>效果报告</template>
              <div class="task-progress">
                <span>80%</span>
                <span class="task-progress-title">转化率</span>
                <lay-progress percent="80"></lay-progress>
              </div>
              <div class="task-progress">
                <span>80%</span>
                <span class="task-progress-title">转化率</span>
                <lay-progress percent="80"></lay-progress>
              </div>
            </lay-card>
          </lay-col>
          <lay-col :md="24">
            <lay-card>
              <template #title>作者寄语</template>
              <p style="line-height: 40px">
                原想将澎湃的爱平平稳稳放置你手心，奈何我徒有一股蛮劲，只顾向你跑去，一个不稳跌的满身脏兮兮。试图爬起的我，
                心想你会不会笑我 " 献爱献的这样笨拙, 怎么不知避开爱里的埋伏 "
              </p>
            </lay-card>
          </lay-col>
        </lay-row>
      </lay-col>
    </lay-row>
  </lay-container>
</template>
<script lang="ts">
export default {
  name: "Accounts",
};
</script>
<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, ref} from 'vue'
import * as echarts from 'echarts'
import router from "../../../../router";
import {findCostTypeRatePie} from "@/api/account/Account";
import {
  AccountCostVo,
  FindCostLineChartVo,
  DateScope,
  FindCostSumCalendarNearYearVo
} from "../../../../types/account/Account";
import {buildRange, defaultShortcuts, getDaysInMonth} from "../../../../util/UDate";
import {findCostLineChart, findCostSumCalendarNearYear} from "../../../../api/account/Account";
import AccountBudget from "@/views/enrollee/accounts/accountsDashboard/AccountBudget.vue";

const mainRef = ref()
const currentIndex = ref('1')
const $router = router
const addExpensesModalFlag = ref(false)
const addIncomeModalFlag = ref(false)
const addExpensesRef = ref()
/* INIT*/
onMounted(() => {
  // var chartDom = mainRef.value
  // @ts-ignore
  // var myChart = echarts.init(chartDom)
  var option

  let color = [
    '#0090FF',
    '#36CE9E',
    '#FFC005',
    '#FF515A',
    '#8B5CFF',
    '#00CA69'
  ]
  let echartData = [
    {
      name: '1',
      value1: 100,
      value2: 233
    },
    {
      name: '2',
      value1: 138,
      value2: 233
    },
    {
      name: '3',
      value1: 350,
      value2: 200
    },
    {
      name: '4',
      value1: 173,
      value2: 180
    },
    {
      name: '5',
      value1: 180,
      value2: 199
    },
    {
      name: '6',
      value1: 150,
      value2: 233
    },
    {
      name: '7',
      value1: 180,
      value2: 210
    },
    {
      name: '8',
      value1: 230,
      value2: 180
    }
  ]
  let xAxisData = echartData.map((v) => v.name)
  let yAxisData1 = echartData.map((v) => v.value1)
  let yAxisData2 = echartData.map((v) => v.value2)
  const hexToRgba = (hex: any, opacity: any) => {
    let rgbaColor = ''
    let reg = /^#[\da-f]{6}$/i
    if (reg.test(hex)) {
      rgbaColor = `rgba(${parseInt('0x' + hex.slice(1, 3))},${parseInt(
          '0x' + hex.slice(3, 5)
      )},${parseInt('0x' + hex.slice(5, 7))},${opacity})`
    }
    return rgbaColor
  }

  option = {
    color: color,
    legend: {
      right: 10,
      top: 10
    },
    tooltip: {
      trigger: 'axis',
      formatter: function (params: any) {
        let html = ''
        params.forEach((v: any) => {
          html += `<div style="color: #666;font-size: 14px;line-height: 24px">
                      <span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${
              color[v.componentIndex]
          };"></span>
                      ${v.seriesName}.${v.name}
                      <span style="color:${
              color[v.componentIndex]
          };font-weight:700;font-size: 18px">${v.value}</span>
                      万元`
        })
        return html
      },
      extraCssText:
          'background: #fff; border-radius: 0;box-shadow: 0 0 3px rgba(0, 0, 0, 0.2);color: #333;',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      x: '50px',
      y: '50px',
      x2: '50px',
      y2: '50px'
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        axisLabel: {
          formatter: '{value}月',
          textStyle: {
            color: '#333'
          }
        },
        axisLine: {
          lineStyle: {
            color: '#D9D9D9'
          }
        },
        data: xAxisData
      }
    ],
    yAxis: [
      {
        type: 'value',
        axisLabel: {
          textStyle: {
            color: '#666'
          }
        },
        nameTextStyle: {
          color: '#666',
          fontSize: 12,
          lineHeight: 40
        },
        splitLine: {
          lineStyle: {
            type: 'dashed',
            color: '#E9E9E9'
          }
        },
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        }
      }
    ],
    series: [
      {
        name: '2018',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        zlevel: 3,
        lineStyle: {
          normal: {
            color: color[0],
            shadowBlur: 3,
            shadowColor: hexToRgba(color[0], 0.5),
            shadowOffsetY: 8
          }
        },
        areaStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
                0,
                0,
                0,
                1,
                [
                  {
                    offset: 0,
                    color: hexToRgba(color[0], 0.3)
                  },
                  {
                    offset: 1,
                    color: hexToRgba(color[0], 0.1)
                  }
                ],
                false
            ),
            shadowColor: hexToRgba(color[0], 0.1),
            shadowBlur: 10
          }
        },
        data: yAxisData1
      },
      {
        name: '2019',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        zlevel: 3,
        lineStyle: {
          normal: {
            color: color[1],
            shadowBlur: 3,
            shadowColor: hexToRgba(color[1], 0.5),
            shadowOffsetY: 8
          }
        },
        areaStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
                0,
                0,
                0,
                1,
                [
                  {
                    offset: 0,
                    color: hexToRgba(color[1], 0.3)
                  },
                  {
                    offset: 1,
                    color: hexToRgba(color[1], 0.1)
                  }
                ],
                false
            ),
            shadowColor: hexToRgba(color[1], 0.1),
            shadowBlur: 10
          }
        },
        data: yAxisData2
      }
    ]
  }
  // option && myChart.setOption(option)

  findCostTypeRatePieQueryVo.value.paymentTimeRange = buildRange(7)
  doFindCostTypeRatePie()

  findCostLineChartQueryVo.value.dateScope = DateScope.WEEK
  doFindCostLineChart()

  doFindCostSumCalendarNearYear();

})
/* INIT*/

/* DESTROY*/
onBeforeUnmount(() => {
  if (costTypeRatePieChart) {
    costTypeRatePieChart.dispose();
  }
  if (weekCostLineChart) {
    weekCostLineChart.dispose();
  }
  if (monthCostLineChart) {
    monthCostLineChart.dispose();
  }
  if (yearCostLineChart) {
    yearCostLineChart.dispose();
  }
  if (costSumCalendarNearYear) {
    costSumCalendarNearYear.dispose();
  }
});
/* DESTROY*/

/* VAR*/
const columns21 = [
  {
    type: 'number'
  },
  {
    title: '标题',
    key: 'username'
  },
  {
    title: '作者',
    key: 'password'
  },
  {
    title: '类别',
    key: 'sex'
  },
  {
    title: '点击率',
    key: 'age'
  },
  {
    title: '发布时间',
    key: 'remark',
    ellipsisTooltip: true
  }
]
const dataSource21 = [
  {
    username: 'root',
    password: 'root',
    sex: '男',
    age: '18',
    remark: 'layui - vue（谐音：类 UI) '
  },
  {
    username: 'root',
    password: 'root',
    sex: '男',
    age: '18',
    remark: 'layui - vue（谐音：类 UI) '
  },
  {
    username: 'woow',
    password: 'woow',
    sex: '男',
    age: '20',
    remark: 'layui - vue（谐音：类 UI) '
  },
  {
    username: 'woow',
    password: 'woow',
    sex: '男',
    age: '20',
    remark: 'layui - vue（谐音：类 UI) '
  },
  {
    username: 'woow',
    password: 'woow',
    sex: '男',
    age: '20',
    remark: 'layui - vue（谐音：类 UI) '
  }
]
const costTypeRatePieRef = ref();
const costTypeRatePieTitle = "支出类型占比"
const findCostTypeRatePieQueryVo = ref<AccountCostVo>({});
const findCostTypeRatePieQueryRef = ref(null)
const sdf_YMDHMS = 'YYYY-MM-DD HH:mm:ss'
const sdf_YM = 'YYYY-MM'
const sdf_Y = 'YYYY'
const weekCostLineChartRef = ref();
const monthCostLineChartRef = ref();
const yearCostLineChartRef = ref();
const costLineChartTitle = "开销折线图"
const findCostLineChartQueryVo = ref<FindCostLineChartVo>({});
const findCostLineChartQueryRef = ref(null)

const costCountCalendarLastYearRef = ref();
const costCountCalendarLastYearTitle = "近一年支付"
const findCostSumCalendarNearYearQueryVo = ref<FindCostSumCalendarNearYearVo>({});
const costSumCalendarNearYearRef = ref(null);
const findCostSumCalendarNearYearQueryRef = ref(null)
let costTypeRatePieChart: echarts.ECharts | null = null;
let weekCostLineChart: echarts.ECharts | null = null;
let monthCostLineChart: echarts.ECharts | null = null;
let yearCostLineChart: echarts.ECharts | null = null;
let costSumCalendarNearYear: echarts.ECharts | null = null;
/* VAR*/

/* FUNCTION*/
function doFindCostTypeRatePie() {
  findCostTypeRatePie(findCostTypeRatePieQueryVo.value).then((res: any) => {
    if (res.code === 200) {
      let data = res.data
      const costTypeRatePieOption = {
        title: {
          text: costTypeRatePieTitle,
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          right: 10,
          top: 20,
          bottom: 20,
          data: data.legends
        },
        markLine: {
          data: [{type: 'average', name: 'Avg'}]
        },
        series: [
          {
            name: '类型',
            type: 'pie',
            radius: '55%',
            center: ['40%', '50%'],
            data: data.series,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      costTypeRatePieChart = echarts.init(costTypeRatePieRef.value);
      costTypeRatePieChart.setOption(costTypeRatePieOption)
    }
  })
}

function doFindCostLineChart() {
  let dateScope = findCostLineChartQueryVo.value.dateScope;
  if (dateScope === DateScope.MONTH || dateScope === DateScope.YEAR) {
    let dateValue = findCostLineChartQueryVo.value.dateValue;
    if (!dateValue || dateValue === '') {
      return;
    }
  }
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
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
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

function doFindCostSumCalendarNearYear() {
  findCostSumCalendarNearYear(findCostSumCalendarNearYearQueryVo.value).then((res: any) => {
    if (res.code === 200) {
      let data = res.data;
      let option = {
        tooltip: {
          position: 'left',
          formatter: (item: any) => {
            return item.data[0] + '<br>' + item.data[1];
          }
        },
        visualMap: {
          min: 0,
          max: data.maxValue,
          calculable: true,
          orient: 'vertical',
          left: 'center',
          top: 'top',
          bottom: 20
        },
        calendar: [
          {
            orient: 'vertical',
            top: 120,
            left: 20,
            range: data.range,
            cellSize: ['auto', 20],
            dayLabel: {
              nameMap: 'ZH'
            },
            monthLabel: {
              nameMap: 'ZH'
            },
            yearLabel: {
              position: 'top'
            }
          }
        ],
        series: [
          {
            type: 'heatmap',
            coordinateSystem: 'calendar',
            calendarIndex: 0,
            data: data.series
          },
        ]
      };
      costSumCalendarNearYear = echarts.init(costSumCalendarNearYearRef.value)
      costSumCalendarNearYear.setOption(option)
    }
  })
}

function doChangeFindCostTypeRatePie() {
  costTypeRatePieChart?.dispose()
  doFindCostTypeRatePie();
}
/* FUNCTION*/
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
