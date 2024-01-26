<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: auto">
      <lay-container fluid="true" class="user-box">
        <lay-card title="Redis面板信息">
          <lay-row>
            <lay-col :md="6">
              Redis版本：{{ redissonPropertiesEntity.redisVersion }}
            </lay-col>
            <lay-col :md="6">
              运行模式：{{
                redissonPropertiesEntity.redisMode === 'standalone' ? '单点模式'
                    : redissonPropertiesEntity.redisMode === 'cluster' ? '集群模式'
                    : '哨兵模式'
              }}
            </lay-col>
            <lay-col :md="6">
              端口：{{ redissonPropertiesEntity.tcpPort }}
            </lay-col>
            <lay-col :md="6">
              客户端数：{{ redissonPropertiesEntity.connectedClients }}
            </lay-col>
          </lay-row>
          <div class="row-padding">
            <lay-row>
              <lay-col :md="6">
                运行天数：{{ redissonPropertiesEntity.uptimeInDays }}
              </lay-col>
              <lay-col :md="6">
                使用内存：{{ redissonPropertiesEntity.usedMemoryHuman }}
              </lay-col>
              <lay-col :md="6">
                使用CPU：{{ redissonPropertiesEntity.usedCpuUser }}
              </lay-col>
              <lay-col :md="6">
                操作系统：{{ redissonPropertiesEntity.os }}
              </lay-col>
            </lay-row>
          </div>
          <div class="row-padding">
            <lay-row>
              <lay-col :md="6">
                AOF是否开启：{{ redissonPropertiesEntity.aofEnabled == '0' ? '开启' : '关闭' }}
              </lay-col>
              <lay-col :md="6">
                RDB是否成功：{{ redissonPropertiesEntity.rdbLastBgsaveStatus == 'ok' ? '成功' : '失败' }}
              </lay-col>
              <lay-col :md="6">
                Key数量：{{ dbSize }}
              </lay-col>
              <lay-col :md="6">
                网络入口/出口：{{ redissonPropertiesEntity.instantaneousInputKbps }}KBps /
                {{ redissonPropertiesEntity.instantaneousOutputKbps }}KBps
              </lay-col>
            </lay-row>
          </div>
        </lay-card>
        <lay-row space="10">
          <lay-col :sm="12">
            <lay-card title="命令统计">
              <div>
                <!--                <PieChart :id="`commandStats`" :height="'600px'" :data="commandStats"/>-->
                <div ref="commandStatsChartRef" style="height: 500px"></div>
              </div>
            </lay-card>
          </lay-col>
          <lay-col :sm="12">
            <lay-card title="内存使用">
              <div>
                <div ref="usedMemoryChartRef" style="height: 500px"></div>
              </div>
            </lay-card>
          </lay-col>
        </lay-row>
      </lay-container>
    </div>
    <!--    <lay-layer v-model="openAssignUserModalFlag" :title="`Redis面板信息`" :area="['1000px', '500px']">-->
    <!--    </lay-layer>-->
  </div>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "Redis",
};
</script>
<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from "vue-router";
import {useTab} from "../../../layouts/composable/useTab";
import {findRedisDashboardInfo} from "../../../api/dashboard/Redis";
import * as echarts from "echarts";
/* INIT*/
const $route = useRoute();
const $router = useRouter();
const {closeOpen} = useTab();

onMounted(() => {
  loadDataSource();
  // showCharts()
})
/* INIT*/
/* VAR*/
const redissonPropertiesEntity = ref({});
const commandStats = ref([]);
const commandStatsChartRef = ref(null);
const usedMemoryChartRef = ref(null);
const dbSize = ref<number>();

/* VAR*/
const loadDataSource = () => {
  findRedisDashboardInfo().then((res: any) => {
    if (res.code == 200) {
      let data = res.data;
      redissonPropertiesEntity.value = data.redissonPropertiesDto;
      commandStats.value = data.commandStats
      dbSize.value = data.dbSize;
      const commandStatOptions = {
        legend: {
          top: 'bottom'
        },
        tooltip: {
          trigger: "item",
          formatter: "{b} : {c} ({d}%)"
        },
        toolbox: {
          show: true,
          feature: {
            mark: {show: true},
            restore: {show: true},
            saveAsImage: {show: true}
          }
        },
        series: [
          {
            name: '命令统计',
            type: 'pie',
            radius: [50, 150],
            center: ['48%', '45%'],
            roseType: 'area',
            itemStyle: {
              borderRadius: 8
            },
            animationEasing: "cubicInOut",
            data: data.commandStats
          }
        ]
      };
      const usedMemoryOption = {
        tooltip: {
          trigger: 'item',
          formatter: "{b} : {c} ({d}%)"
        },
        legend: {
          top: 'bottom'
        },
        series: [
          {
            name: 'Access From',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            labelLine: {
              show: false
            },
            data: [
              {
                value: data.redissonPropertiesDto.usedMemoryPeak - data.redissonPropertiesDto.usedMemory,
                name: 'Redis消耗的最大内存峰值（KBps）'
              },
              {value: data.redissonPropertiesDto.usedMemory, name: 'Redis分配的字节数（KBps）'},
            ]
          }
        ],
        tooltip: {
          formatter: '{a} <br/>{b} : {c}%'
        },
        series: [
          {
            name: 'Redis消耗的内存与最大内存峰值比',
            type: 'gauge',
            progress: {
              show: true
            },
            detail: {
              valueAnimation: true,
              formatter: '{value}'
            },
            data: [
              {
                value: formatNumber(100 * data.redissonPropertiesDto.usedMemory / (data.redissonPropertiesDto.usedMemoryPeak), 2),
                name: '内存消耗比'
              }
            ]
          }
        ]
      };
      let commandStatsChart = echarts.init(commandStatsChartRef.value)
      commandStatsChart.setOption(commandStatOptions)
      let usedMemoryChart = echarts.init(usedMemoryChartRef.value)
      usedMemoryChart.setOption(usedMemoryOption)
    }
  })
}

function formatNumber(num: number, decimalPlaces: number) {
  const pattern = `^-?\\d+(?:\.\\d{0,${decimalPlaces}})?`;
  const regex = new RegExp(pattern);
  const formatted = num.toString().match(regex);
  return formatted ? formatted[0] : "";
}

</script>

<style scoped>
.user-box {
  height: calc(100vh - 110px);
  margin-top: 10px;
  box-sizing: border-box;
  overflow: hidden;
}

.top-search {
  margin-top: 10px;
  padding: 10px;
  height: 40px;
  border-radius: 4px;
  background-color: #fff;
}

.table-box {
  margin-top: 10px;
  padding: 10px;
  height: 700px;
  width: 100%;
  border-radius: 4px;
  box-sizing: border-box;
  background-color: #fff;
}

.search-input {
  display: inline-block;
  width: 98%;
  margin-right: 10px;
}

.table-style {
  margin-top: 10px;
}

.isChecked {
  display: inline-block;
  background-color: #e8f1ff;
  color: red;
}

.oneRow {
  width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.footer {
  width: 100%;
  display: flex;
  align-items: center;
  padding: 10px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-top: 1px solid whitesmoke;
  line-height: 60px;
  height: 60px;
}

.footer-button {
  right: 50px;
  position: absolute;
}

.row-padding {
  margin-top: 20px;
}
</style>
