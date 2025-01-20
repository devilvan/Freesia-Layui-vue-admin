<template>
  <lay-container :fluid="true" style="padding: 10px">
    <lay-row space="10">
      <lay-col md="8" sm="8" xs="12">
        <lay-card>
          <template #title> 快捷方式</template>
          <lay-row :space="10">
            <lay-col :md="6">
              <a class="shortcut" @click="changeAddExpensesModal">
                <i class="layui-icon layui-icon-rate"></i>
                <cite>支出</cite>
              </a>
            </lay-col>
            <lay-col :md="6">
              <a class="shortcut" @click="changeAddIncomeModal">
                <i class="layui-icon layui-icon-rate-solid"></i>
                <cite>收入</cite>
              </a>
            </lay-col>
            <lay-col :md="6">
              <router-link to="/workspace/analysis" class="shortcut">
                <i class="layui-icon layui-icon-template-one"></i>
                <cite>主页三</cite>
              </router-link>
            </lay-col>
            <lay-col :md="6">
              <a lay-href="home/homepage1" @click="changePage" class="shortcut">
                <i class="layui-icon layui-icon-chat"></i>
                <cite>主页四</cite>
              </a>
            </lay-col>
            <lay-col :md="6">
              <a lay-href="home/homepage1" class="shortcut">
                <i class="layui-icon layui-icon-find-fill"></i>
                <cite>主页五</cite>
              </a>
            </lay-col>
            <lay-col :md="6">
              <a lay-href="home/homepage1" class="shortcut">
                <i class="layui-icon layui-icon-survey"></i>
                <cite>主页六</cite>
              </a>
            </lay-col>
            <lay-col :md="6">
              <a lay-href="home/homepage1" class="shortcut">
                <i class="layui-icon layui-icon-user"></i>
                <cite>主页七</cite>
              </a>
            </lay-col>
            <lay-col :md="6">
              <a lay-href="home/homepage1" class="shortcut">
                <i class="layui-icon layui-icon-set"></i>
                <cite>主页八</cite>
              </a>
            </lay-col>
          </lay-row>
        </lay-card>
      </lay-col>
      <lay-col md="8" sm="8" xs="12">
        <lay-card>
          <template #title> 代办事项</template>
          <lay-row :space="10">
            <lay-col :md="12">
              <a class="agency">
                <h3>待审评论</h3>
                <p>
                  <cite>66</cite>
                </p>
              </a>
            </lay-col>
            <lay-col :md="12">
              <a class="agency">
                <h3>待审帖子</h3>
                <p>
                  <cite>12</cite>
                </p>
              </a>
            </lay-col>
            <lay-col :md="12">
              <a class="agency">
                <h3>待审商品</h3>
                <p>
                  <cite>99</cite>
                </p>
              </a>
            </lay-col>
            <lay-col :md="12">
              <a class="agency">
                <h3>等待发货</h3>
                <p>
                  <cite>20</cite>
                </p>
              </a>
            </lay-col>
          </lay-row>
        </lay-card>
      </lay-col>
      <lay-col md="8" sm="8" xs="12">
        <lay-card>
          <template #title> 版本信息</template>
          <table class="layui-table">
            <tr>
              <td>页面模式</td>
              <td>单页面</td>
            </tr>
            <tr>
              <td>涉及技术</td>
              <td>vue / layui-vue</td>
            </tr>
            <tr>
              <td>主要特色</td>
              <td>单页面 / 响应式 / 清爽 / 极简</td>
            </tr>
          </table>
        </lay-card>
      </lay-col>
      <lay-col md="16" sm="16" xs="24">
        <lay-row :space="10">
          <lay-col :md="24">
            <lay-card>
              <template #title> 数据概览</template>
              <div id="main" ref="mainRef"></div>
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

  <lay-layer v-model="addExpensesModalFlag" :title="title">
    <div style="padding: 20px">
      <lay-form :model="sysDirVo" ref="addExpensesRef" label-position="top" size="md">
        <lay-row space="20">
          <lay-col md="6">
            <lay-form-item label="父目录" prop="parentId">
              <lay-tree-select v-model="sysDirVo.parentId" :data="treeMenuSelectList"
                               @change="changeDirModalParentIdSelect"
                               :allow-clear="true" :disabled="proceedCode === PROCEED_CODE.UPDATE"></lay-tree-select>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="路由路径" prop="path" required>
              <lay-input v-model="sysDirVo.path" placeholder="例如：workspace"
                         :disabled="proceedCode === PROCEED_CODE.UPDATE"></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="目录名称" prop="menuName" required>
              <lay-input v-model="sysDirVo.menuName"></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col md="6">
            <lay-form-item label="是否显示" prop="status" required>
              <lay-select v-model="sysDirVo.status" :options="isShowOptions" :items="sysDirVo.status"
                          style="width: 100%">
              </lay-select>
            </lay-form-item>
          </lay-col>
          <lay-row space="20">
            <lay-col md="6">
              <lay-form-item label="图标" prop="icon">
                <lay-icon-picker v-model="sysDirVo.icon" allow-clear></lay-icon-picker>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="排序" prop="orderNum" required>
                <lay-input-number
                    style="width: 100%"
                    v-model="sysDirVo.orderNum"
                    position="right"
                    :min="0"
                    :step="10"
                ></lay-input-number>
              </lay-form-item>
            </lay-col>
            <lay-col md="6">
              <lay-form-item label="备注" prop="remark" required>
                <lay-textarea v-model="sysDirVo.remark" :allow-clear="true" show-count
                              :maxlength="127"></lay-textarea>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-row>
      </lay-form>
      <div style="width: 97%; text-align: right">
        <lay-button size="sm" type="primary" @click="saveMenuSubmit(MenuType.DIR)">保存</lay-button>
        <lay-button size="sm" type="primary" @click="resetModal(MenuType.DIR)"
                    :style="proceedCode === PROCEED_CODE.UPDATE ? 'display: none' : ''">重置
        </lay-button>
        <lay-button size="sm" @click="toCancel">取消</lay-button>
      </div>
    </div>
  </lay-layer>
</template>
<script lang="ts">
export default {
  name: "Accounts",
};
</script>
<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import * as echarts from 'echarts'
import router from "../../../../router";

const mainRef = ref()
const currentIndex = ref('1')
const $router = router
const addExpensesModalFlag = ref(false)
const addIncomeModalFlag = ref(false)
const addExpensesRef = ref()

/* INIT*/
onMounted(() => {
  var chartDom = mainRef.value
  // @ts-ignore
  var myChart = echarts.init(chartDom)
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
  option && myChart.setOption(option)
})
/* INIT*/

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
/* VAR*/

/* FUNCTION*/
const changePage = () => {
  $router.push({path: '/form/base', query: {id: '1111'}})
}
/* FUNCTION*/

function changeAddExpensesModal() {
  addExpensesModalFlag.value = !addExpensesModalFlag.value
}

function changeAddIncomeModal() {
  addIncomeModalFlag.value = !addIncomeModalFlag.value
}
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
