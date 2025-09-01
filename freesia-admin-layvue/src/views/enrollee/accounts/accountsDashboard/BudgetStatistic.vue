<template>
  <lay-card shadow="hover">
    <template #title>{{ props.title }}</template>
    <lay-row space="20">
      <lay-col v-for="(item, index) in accountBudgetEntityList" :key="index" :md="8">
        <lay-card shadow="hover">
          <div style="padding-bottom: 20px">
            <div style="font-size: 12pt;text-align: center;height: 50px">
              {{ item.name }}
            </div>
            <div style="font-size: 20pt">
              <div style="display: flex;justify-content: center" :style="
                    item.value >= 0 && item.value <= 50 ?
                    'color: #36b368' :
                    item.value > 50 && item.value <= 80 ?
                    'color: #FFB800' :
                    item.value > 80 && item.value <= 100 ?
                    'color: #FF9B2D' :
                    item.value > 100 ?
                    'color: #FF5722' : 'color: #393D49'">
                <div>
                  <lay-count-up :end-val="item.outlay" prefix="¥" decimalPlaces="2"></lay-count-up>
                  /
                  <lay-count-up :end-val="item.budget"></lay-count-up>
                </div>
                <div style="margin-left: 30px">
                  {{ item.value }}%
                </div>
              </div>
            </div>
          </div>
          <template #footer>
            <div class="button-list">
              <div>
                <lay-icon type="layui-icon-edit"></lay-icon>设置预算
              </div>
              <div>
                <lay-icon type="layui-icon-chart-screen"></lay-icon>历史数据
              </div>
            </div>
          </template>
        </lay-card>
      </lay-col>
    </lay-row>
  </lay-card>
</template>

<script lang="ts">
export default {
  name: "BudgetStatistic"
};
</script>
<script lang="ts" setup>

/*INIT*/
import {ref, watch} from "vue";
import {EchartCapacityOptionEntity} from "@/types/account/AccountBudget";

const props = defineProps({
  title: {
    required: false,
    default: '开支预算统计'
  },
  dataSource: {
    required: true,
    type: Array<EchartCapacityOptionEntity>
  }
})

watch(
    () => props.dataSource,
    (val) => {
      accountBudgetEntityList.value = val;
    },
);

/*INIT*/

/*VAR*/
const accountBudgetEntityList = ref<Array<EchartCapacityOptionEntity>>([]);
/*VAR*/

/*FUNCTION*/
/*FUNCTION*/
</script>

<style scoped>
.button-list {
  display: flex;
}

.button-list > div {
  flex: 1;
  text-align: center;
  color: #909399;
}
</style>