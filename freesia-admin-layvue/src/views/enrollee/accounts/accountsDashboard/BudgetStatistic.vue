<template>
  <lay-card>
    <template #title>{{ props.title }}</template>
    <lay-row space="10">
      <lay-col v-for="(item, index) in accountBudgetEntityList" :key="index" :md="6">
        <lay-row :space="20">
          <lay-col :md="24" style="font-size: 24pt">
            {{ item.name }}
          </lay-col>
        </lay-row>
        <lay-row :space="100">
          <lay-col :md="24" style="font-size: 32pt">
            {{ item.outlay }}/{{ item.budget }}
          </lay-col>
        </lay-row>
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