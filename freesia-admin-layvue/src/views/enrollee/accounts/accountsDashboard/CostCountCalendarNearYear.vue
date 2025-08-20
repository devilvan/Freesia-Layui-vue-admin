<template>
  <lay-card shadow="hover">
    <template #title>{{ costCountCalendarNearYearTitle }}</template>
    <lay-row :space="20">
      <lay-col>
        <lay-form :model="findCostSumCalendarNearYearQueryVo" ref="findCostSumCalendarNearYearQueryRef"
                  label-position="left">
        </lay-form>
        <div ref="costSumCalendarNearYearRef" style="height: 300px"></div>
      </lay-col>
    </lay-row>
  </lay-card>
</template>

<script lang="ts">
export default {
  name: "CostCountCalendarNearYear"
}
</script>
<script lang="ts" setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import {FindCostSumCalendarNearYearVo} from "@/types/account/Account";
import * as echarts from "echarts";
import {findCostSumCalendarNearYear} from "@/api/account/Account";
import {useAccountCostStore} from "@/store/accountCost";

/*INIT*/
const props = defineProps({
  // 数据
  title: {
    type: String,
    default: "近一年支付",
  },
});
onMounted(() => {
  doFindCostSumCalendarNearYear();
})
/*INIT*/

/*DESTROY*/
onBeforeUnmount(() => {
  if (costSumCalendarNearYear) {
    costSumCalendarNearYear.dispose();
  }
})
/*DESTROY*/

/*VAR*/
const accountCostStore = useAccountCostStore()
const costCountCalendarNearYearTitle = props.title
const findCostSumCalendarNearYearQueryVo = ref<FindCostSumCalendarNearYearVo>({});
const costSumCalendarNearYearRef = ref(null);
const findCostSumCalendarNearYearQueryRef = ref(null)
let costSumCalendarNearYear: echarts.ECharts | null = null;

/*VAR*/


/*FUNCTION*/
/**
 * 查询日历图
 */
function doFindCostSumCalendarNearYear() {
  findCostSumCalendarNearYearQueryVo.value.allTenantFlag = accountCostStore.allTenantFlag
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
          orient: 'horizontal',
          left: 'center',
          top: 'top',
          bottom: 20
        },
        calendar: [
          {
            top: 120,
            left: 30,
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

/*FUNCTION*/
</script>

<style scoped>

</style>