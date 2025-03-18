<template>
  <lay-card>
    <template #title>{{ props.title }}</template>
    <lay-row space="10">
      <lay-col v-for="(item, index) in accountBudgetEntityList" :key="index" :md="8">
        <div style="margin-top: 20px">
          <div style="font-size: 12pt;text-align: center;height: 50px">
            {{ item.name }}
          </div>
          <div style="font-size: 32pt">
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
import {onMounted, ref} from "vue";
import {findBudgetCapacity} from "@/api/account/AccountBudget";
import {AccountBudgetVo, EchartCapacityOptionEntity} from "@/types/account/AccountBudget";

const props = defineProps({
  title: {
    required: false,
    default: '开支预算统计'
  }
})

onMounted(() => {
  doFindBudgetCapacity();
})
/*INIT*/

/*VAR*/
const accountBudgetVo = ref<AccountBudgetVo>({});
const accountBudgetEntityList = ref<Array<EchartCapacityOptionEntity>>([]);
/*VAR*/

/*FUNCTION*/
function doFindBudgetCapacity() {
  findBudgetCapacity(accountBudgetVo.value).then((res: any) => {
    if (res.code === 200) {
      accountBudgetEntityList.value = res.data;
    }
  })
}

/*FUNCTION*/
</script>

<style scoped>
</style>