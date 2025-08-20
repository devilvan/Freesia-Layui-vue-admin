<template>
  <lay-card shadow="hover">
    <template #title>{{ costTypeRatePieTitle }}</template>
    <lay-row :space="20">
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
    <div ref="costTypeRatePieRef" style="height: 440px"></div>
  </lay-card>
</template>

<script lang="ts">
export default {
  name: "CostTypeRatePie"
}
</script>
<script lang="ts" setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import {AccountCostVo} from "@/types/account/Account";
import {buildRange, defaultShortcuts} from "@/util/UDate";
import * as echarts from "echarts";
import {findCostTypeRatePie} from "@/api/account/Account";
import {useAccountCostStore} from "@/store/accountCost";

/*INIT*/
const props = defineProps({
  // 数据
  title: {
    type: String,
    default: "支出类型占比",
  },
});
onMounted(() => {
  findCostTypeRatePieQueryVo.value.paymentTimeRange = buildRange(7)
  doFindCostTypeRatePie()
})
/*INIT*/

/*DESTROY*/
onBeforeUnmount(() => {
  if (costTypeRatePieChart) {
    costTypeRatePieChart.dispose();
  }
})
/*DESTROY*/

/*VAR*/
const accountCostStore = useAccountCostStore()
const costTypeRatePieTitle = props.title
const costTypeRatePieRef = ref();
const findCostTypeRatePieQueryVo = ref<AccountCostVo>({});
const findCostTypeRatePieQueryRef = ref(null)
const sdf_YMDHMS = 'YYYY-MM-DD HH:mm:ss'
let costTypeRatePieChart: echarts.ECharts | null = null;
/*VAR*/

/*FUNCTION*/
/**
 * 刷新图表
 */
function doChangeFindCostTypeRatePie() {
  costTypeRatePieChart?.dispose()
  doFindCostTypeRatePie();
}

/**
 * 查询饼图
 */
function doFindCostTypeRatePie() {
  findCostTypeRatePieQueryVo.value.allTenantFlag = accountCostStore.allTenantFlag
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

/*FUNCTION*/
</script>


<style scoped>

</style>