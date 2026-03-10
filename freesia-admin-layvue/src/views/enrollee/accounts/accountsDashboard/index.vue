<template>
  <lay-container :fluid="true" style="padding: 10px">
    <lay-card :shadow="'hover'">
      <lay-form ref="accountCostDashboardFormRef" :model="accountCostDashboardQuery" label-position="top">
        <lay-row :space="20">
          <lay-col :md="2">
            <lay-form-item label="统计所有账本" prop="allTenantFlag">
              <lay-switch v-model="accountCostStore.allTenantFlag"
                          @change="doChangeAllTenantFlag"></lay-switch>
            </lay-form-item>
          </lay-col>
          <lay-col :md="2">
            <lay-form-item label="刷新缓存" props="44">
              <lay-button type="normal" @click="doRefreshCache" v-esc-close="closeRefreshConfirm">刷新缓存</lay-button>
            </lay-form-item>
          </lay-col>
        </lay-row>
      </lay-form>
    </lay-card>
    <BudgetStatistic :dataSource="echartCapacityOptionEntityList"/>
    <lay-row space="20">
      <lay-col md="12">
        <CostTypeRatePie/>
      </lay-col>
      <lay-col md="12">
        <AccountBudget v-if="!isMobile()" :dataSource="echartCapacityOptionEntityList"/>
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
            <CostLine/>
          </lay-col>
          <lay-col :md="24">
            <RankStackHorizontal/>
          </lay-col>
        </lay-row>
      </lay-col>
    </lay-row>
  </lay-container>
</template>
<script lang="ts">
import CostTypeRatePie from "./CostTypeRatePie.vue";
import CostCountCalendarNearYear from "./CostCountCalendarNearYear.vue";
import CostLine from "./CostLine.vue";
import AccountBudget from "./BudgetCapacity.vue";
import BudgetStatistic from "@/views/enrollee/accounts/accountsDashboard/BudgetStatistic.vue";
import RankStackHorizontal from "@/views/enrollee/accounts/accountsDashboard/RankStackHorizontal.vue";

export default {
  name: "Accounts",
  components: {
    AccountBudget,
    CostLine,
    CostCountCalendarNearYear,
    CostTypeRatePie,
    BudgetStatistic,
    RankStackHorizontal
  },
};
</script>
<script lang="ts" setup>
import {onBeforeUnmount, onMounted, ref} from 'vue'
import {findBudgetCapacity} from "@/api/account/AccountBudget";
import {AccountBudgetVo, EchartCapacityOptionEntity} from "@/types/account/AccountBudget";
import {defaultShortcuts} from "@/util/UDate";
import {useAccountCostStore} from "@/store/accountCost";
import {refreshCache} from "@/api/account/Account";
import {layer} from "@layui/layui-vue";
import {cancelAssignUser} from "@/api/system/Role";
import {isMobile} from "@/api/Http";

/*INIT*/
onMounted(() => {
  accountBudgetVo.value.allTenantFlag = accountCostStore.allTenantFlag || false
  findBudgetCapacity(accountBudgetVo.value).then((res: any) => {
    if (res.code === 200) {
      echartCapacityOptionEntityList.value = res.data
    }
  })
})

onBeforeUnmount(() => {
});
/*INIT*/

/* VAR*/
const accountBudgetVo = ref<AccountBudgetVo>({});
const echartCapacityOptionEntityList = ref<Array<EchartCapacityOptionEntity>>([]);
const accountCostDashboardQuery = ref()
const accountCostDashboardFormRef = ref()
const accountCostStore = useAccountCostStore()
/* VAR*/

/*FUNCTION*/
function doChangeAllTenantFlag() {
  accountCostStore.changeAllTenantFlag()
  window.location.reload()
}

function doRefreshCache() {
  layer.confirm('确定刷新缓存吗？', {
    btn: [
      {
        text: '确定',
        callback: () => {
          refreshCache().then((res: any) => {
            if (res.code === 200) {
              window.location.reload()
            }
          }).catch(e => {
            layer.confirm(e, {icon: 2})
          })
          layer.closeAll()
        }
      },
      {
        text: '取消',
        callback: (id) => {
          layer.close(id)
        }
      }
    ]
  })
}

function closeRefreshConfirm() {
  layer.closeAll();
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
