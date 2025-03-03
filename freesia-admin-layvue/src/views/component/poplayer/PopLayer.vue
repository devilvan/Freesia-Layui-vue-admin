<template>
  <lay-layer v-model="showModalFlag" :title="'关联用户'" :area="['1200px', '700px']">
    <lay-table
        ref="modalTableRef"
        class="table-box table-style"
        :page="pageQuery"
        :columns="columns"
        :loading="loading"
        :data-source="dataSource"
        v-model:selected-keys="selectKeys"
        @change="modalChange"
    >
      <template #accountStatus="{ row }">
        <div v-show="row.accountStatus === '1'">
          <lay-tag color="#2dc570" variant="light">启用</lay-tag>
        </div>
        <div v-show="row.accountStatus === '0'">
          <lay-tag color="#F5319D" variant="light">禁用</lay-tag>
        </div>
      </template>
      <template #remark="{ row }">
        <lay-tooltip :visible="false" trigger="hover" :content="row.remark">
          <div class="oneRow">{{ row.remark }}</div>
        </lay-tooltip>
      </template>
      <template v-slot:toolbar>
        <lay-button size="sm" type="normal" @click="modalChange">
          <lay-icon class="layui-icon-addition"></lay-icon>
          查询
        </lay-button>
        <lay-button size="sm" type="danger" @click="modalConfirm">
          <lay-icon class="layui-icon-addition"></lay-icon>
          确认
        </lay-button>
      </template>
    </lay-table>
  </lay-layer>

</template>

<script lang="ts">
export default {
  name: "PopLayer"
}
</script>
<script lang="ts" setup>
import {reactive, ref} from "vue";
import {SysUserEntity, SysUserVo} from "../../../types/system/User";
import {PageQuery} from "../../../types/Common";

/*INIT*/
const props = defineProps({
  // 使用 v-model 绑定的值
  modelValue: {
    type: Array as () => [],
    required: true,
  },
  columns: {
    type: Array as () => [],
    required: true
  }
});
/*INIT*/

/*VAR*/
const showModalFlag = ref<Boolean>(false)
const dataSource = ref<Array>()
const loading = ref(false)
const selectKeys = ref<Array<string>>([])
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const modalSearchQuery = ref<SysUserVo>({})
const modalTableRef = ref();
/*VAR*/

/*FUNCTION*/
function modalChange() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 200)
}

function changeShowUserModalFlag() {
  modalChange()
  showModalFlag.value = !showModalFlag.value
}

function modalConfirm() {
  let checkDataList = modalTableRef.value.getCheckData();
  accountCostVo.value.accountCostUserNameList = checkDataList?.map(v => v.nickName);
  accountCostVo.value.accountCostUserIdList = selectKeys.value
  selectKeys.value = []
  showModalFlag.value = !showModalFlag.value
}
/*FUNCTION*/

</script>

<style scoped>

</style>