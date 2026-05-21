<template>
  <view class="account-mine-wrap">
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input class="search-input" placeholder="请输入描述或备注" v-model="searchQuery.keyword" @confirm="toSearch"/>
        <text v-if="searchQuery.keyword" class="clear-icon" @click="searchQuery.keyword = ''">✕</text>
      </view>
      <button class="search-btn" @click="toSearch">查询</button>
      <button class="reset-btn" @click="queryFormReset">重置</button>
    </view>

    <view class="filter-section">
      <view class="filter-row">
        <view class="filter-item">
          <text class="filter-label">类型</text>
          <picker mode="selector" :range="typeOptions" @change="onTypeChange">
            <view class="picker-value">
              {{ searchQuery.costType ? searchQuery.costType : '请选择类型' }}
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>
        <view class="filter-item">
          <text class="filter-label">标识</text>
          <picker mode="selector" :range="signOptions" :range-key="'label'" @change="onSignChange">
            <view class="picker-value">
              {{ searchQuery.paymentSignName || '请选择标识' }}
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>
      </view>
      <view class="filter-row">
        <view class="filter-item">
          <text class="filter-label">时间范围</text>
          <picker mode="date" :value="searchQuery.startDateDisplay" @change="onStartDateChange">
            <view class="picker-value">
              {{ searchQuery.startDateDisplay || '开始日期' }}
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>
        <view class="filter-item">
          <text class="filter-label">-</text>
          <picker mode="date" :value="searchQuery.endDateDisplay" @change="onEndDateChange">
            <view class="picker-value">
              {{ searchQuery.endDateDisplay || '结束日期' }}
              <text class="picker-arrow">▼</text>
            </view>
          </picker>
        </view>
      </view>
    </view>

    <view class="table-container">
      <view class="table-header">
        <view class="th" style="width: 40rpx">选择</view>
        <view class="th" style="width: 100rpx">图标</view>
        <view class="th" style="width: 120rpx">类型</view>
        <view class="th" style="flex: 1">描述</view>
        <view class="th" style="width: 140rpx">金额</view>
        <view class="th" style="width: 120rpx">标识</view>
        <view class="th" style="width: 160rpx">时间</view>
        <view class="th" style="width: 100rpx">操作</view>
      </view>
      <scroll-view scroll-y class="table-body">
        <view v-for="(row, index) in dataSource" :key="row.id" class="table-row" :class="{ 'even': index % 2 === 0 }">
          <view class="td" style="width: 40rpx">
            <view class="checkbox" :class="{ checked: selectedKeys.includes(row.id) }"
                  @click="toggleSelect(row.id)"></view>
          </view>
          <view class="td" style="width: 100rpx">
            <image v-if="row.icon" :src="row.icon" class="icon-img" mode="aspectFit"/>
            <text v-else class="icon-placeholder">📄</text>
          </view>
          <view class="td" style="width: 120rpx">
            <text class="cell-text">{{ row.costType }}</text>
          </view>
          <view class="td" style="flex: 1">
            <text class="cell-text ellipsis">{{ row.costDesc }}</text>
          </view>
          <view class="td" style="width: 140rpx">
            <text class="cell-text" :class="row.paymentSign === 'INCOME' ? 'income' : 'expense'">
              {{ row.paymentSign === 'INCOME' ? '+' : '-' }}{{ row.outlay?.toFixed(2) }}
            </text>
          </view>
          <view class="td" style="width: 120rpx">
            <view class="sign-tag" :class="row.paymentSign.toLowerCase()">
              {{ row.paymentSign === 'INCOME' ? '收入' : '支出' }}
            </view>
          </view>
          <view class="td" style="width: 160rpx">
            <text class="cell-text">{{ formatDate(row.paymentTime) }}</text>
          </view>
          <view class="td" style="width: 100rpx">
            <view class="actions">
              <text class="action-btn edit" @click="showExpenseModal('EDIT', row)">编辑</text>
              <text class="action-btn delete" @click="toDelete(row.id)">删除</text>
            </view>
          </view>
        </view>
        <view v-if="dataSource.length === 0" class="empty-state">
          <text class="empty-icon">📋</text>
          <text class="empty-text">暂无数据</text>
        </view>
      </scroll-view>
    </view>

    <view class="pagination" v-if="pagination.total > 0">
      <view class="pagination-info">
        共 {{ pagination.total }} 条，第 {{ pagination.current }}/{{ pagination.pages }} 页
      </view>
      <view class="pagination-btns">
        <button class="page-btn" :disabled="pagination.current <= 1" @click="prevPage">上一页</button>
        <button class="page-btn" :disabled="pagination.current >= pagination.pages" @click="nextPage">下一页</button>
      </view>
    </view>

    <view class="bottom-actions">
      <view class="selected-info" v-if="selectedKeys.length > 0">
        已选择 {{ selectedKeys.length }} 项
        <text class="clear-selection" @click="selectedKeys = []">取消选择</text>
      </view>
      <view class="action-btns">
        <button class="action-btn primary" @click="showExpenseModal('ADD', null)">新增</button>
        <button class="action-btn danger" v-if="selectedKeys.length > 0" @click="batchDelete">批量删除</button>
      </view>
    </view>

    <view class="modal-overlay" v-if="showModal" @click="closeModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ operate === 'ADD' ? '新增账单' : '编辑账单' }}</text>
          <text class="modal-close" @click="closeModal">✕</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">描述 *</text>
            <input class="form-input" placeholder="请输入描述" v-model="accountCostVo.costDesc"/>
          </view>
          <view class="form-item">
            <text class="form-label">金额 *</text>
            <input class="form-input" type="digit" placeholder="请输入金额" v-model="accountCostVo.outlay"/>
          </view>
          <view class="form-item">
            <text class="form-label">类型</text>
            <picker mode="selector" :range="typeOptions" @change="onFormTypeChange">
              <view class="form-picker">
                {{ accountCostVo.costType || '请选择类型' }}
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">标识</text>
            <view class="sign-radio-group">
              <view class="radio-item" :class="{ active: accountCostVo.paymentSign === 'EXPENSES' }"
                    @click="accountCostVo.paymentSign = 'EXPENSES'">
                <view class="radio-dot"></view>
                <text>支出</text>
              </view>
              <view class="radio-item" :class="{ active: accountCostVo.paymentSign === 'INCOME' }"
                    @click="accountCostVo.paymentSign = 'INCOME'">
                <view class="radio-dot"></view>
                <text>收入</text>
              </view>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">时间</text>
            <picker mode="date" :value="accountCostVo.paymentTimeStr" @change="onFormDateChange">
              <view class="form-picker">
                {{ accountCostVo.paymentTimeStr || '请选择时间' }}
                <text class="picker-arrow">▼</text>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <text class="form-label">备注</text>
            <textarea class="form-textarea" placeholder="请输入备注" v-model="accountCostVo.remark"></textarea>
          </view>
        </view>
        <view class="modal-footer">
          <button class="modal-btn cancel" @click="closeModal">取消</button>
          <button class="modal-btn confirm" @click="submitForm">确认</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {ref, reactive, onMounted} from 'vue'
import {findPageAccountCost, saveUpdate, deleteAccountCost} from '@/api/account/Account'
import {findListSelectCostType} from '@/api/account/Account'

export default {
  name: 'AccountMine',
  setup() {
    const searchQuery = reactive({
      keyword: '',
      costType: '',
      paymentSign: '',
      paymentSignName: '',
      startDate: '',
      endDate: '',
      startDateDisplay: '',
      endDateDisplay: ''
    })

    const pageQuery = reactive({
      current: 1,
      limit: 10
    })

    const pagination = reactive({
      total: 0,
      current: 1,
      pages: 0
    })

    const dataSource = ref([])
    const selectedKeys = ref([])
    const loading = ref(false)
    const expandCollapseFlag = ref(false)
    const showModal = ref(false)
    const operate = ref('ADD')

    const accountCostVo = reactive({
      id: '',
      costDesc: '',
      outlay: '',
      costType: '',
      paymentSign: 'EXPENSES',
      paymentTimeStr: '',
      remark: ''
    })

    const typeOptions = ref(['餐饮', '交通', '购物', '娱乐', '医疗', '教育', '工资', '奖金', '其他'])
    const signOptions = ref([
      {label: '支出', value: 'EXPENSES'},
      {label: '收入', value: 'INCOME'}
    ])

    const formatDateTime = (date) => {
      const pad = (n) => String(n).padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
    }

    const initDefaultDateRange = () => {
      const endDate = new Date()
      const startDate = new Date()
      startDate.setDate(startDate.getDate() - 7)

      searchQuery.startDate = formatDateTime(new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate(), 0, 0, 0))
      searchQuery.endDate = formatDateTime(new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate(), 23, 59, 59))

      searchQuery.startDateDisplay = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}`
      searchQuery.endDateDisplay = `${endDate.getFullYear()}-${String(endDate.getMonth() + 1).padStart(2, '0')}-${String(endDate.getDate()).padStart(2, '0')}`
    }

    const toSearch = () => {
      pageQuery.current = 1
      doFindPageAccountCost()
    }

    const queryFormReset = () => {
      searchQuery.keyword = ''
      searchQuery.costType = ''
      searchQuery.paymentSign = ''
      searchQuery.paymentSignName = ''
      pageQuery.current = 1
      initDefaultDateRange()
      doFindPageAccountCost()
    }

    const doFindPageAccountCost = async () => {
      loading.value = true
      try {
        const params = {}

        if (searchQuery.keyword) {
          params.costDesc = searchQuery.keyword
          params.remark = searchQuery.keyword
        }
        if (searchQuery.costType) {
          params.costType = searchQuery.costType
        }
        if (searchQuery.paymentSign) {
          params.paymentSign = searchQuery.paymentSign
        }
        if (searchQuery.startDate && searchQuery.endDate) {
          params.paymentTimeRange = [searchQuery.startDate, searchQuery.endDate]
        }

        const res = await findPageAccountCost(params, pageQuery)
        if (res.code === 200) {
          dataSource.value = res.rows || []
          pagination.total = res.total || 0
          pagination.current = res.current || 1
          pagination.pages = Math.ceil(res.total / pageQuery.limit)
        }
      } catch (e) {
        console.error('查询失败', e)
        uni.showToast({title: '查询失败', icon: 'none'})
      } finally {
        loading.value = false
      }
    }

    const prevPage = () => {
      if (pageQuery.current > 1) {
        pageQuery.current -= 1
        doFindPageAccountCost()
      }
    }

    const nextPage = () => {
      if (pageQuery.current < pagination.pages) {
        pageQuery.current += 1
        doFindPageAccountCost()
      }
    }

    const toggleSelect = (id) => {
      const index = selectedKeys.value.indexOf(id)
      if (index > -1) {
        selectedKeys.value.splice(index, 1)
      } else {
        selectedKeys.value.push(id)
      }
    }

    const showExpenseModal = (op, row) => {
      operate.value = op
      if (op === 'ADD') {
        accountCostVo.id = ''
        accountCostVo.costDesc = ''
        accountCostVo.outlay = ''
        accountCostVo.costType = ''
        accountCostVo.paymentSign = 'EXPENSES'
        accountCostVo.paymentTimeStr = ''
        accountCostVo.remark = ''
      } else {
        accountCostVo.id = row.id
        accountCostVo.costDesc = row.costDesc
        accountCostVo.outlay = row.outlay
        accountCostVo.costType = row.costType
        accountCostVo.paymentSign = row.paymentSign
        accountCostVo.paymentTimeStr = formatDate(row.paymentTime)
        accountCostVo.remark = row.remark
      }
      showModal.value = true
    }

    const closeModal = () => {
      showModal.value = false
    }

    const submitForm = async () => {
      if (!accountCostVo.costDesc) {
        uni.showToast({title: '请输入描述', icon: 'none'})
        return
      }
      if (!accountCostVo.outlay) {
        uni.showToast({title: '请输入金额', icon: 'none'})
        return
      }
      loading.value = true
      try {
        const params = {
          id: accountCostVo.id || undefined,
          costDesc: accountCostVo.costDesc,
          outlay: Number(accountCostVo.outlay),
          costType: accountCostVo.costType || undefined,
          paymentSign: accountCostVo.paymentSign,
          paymentTime: accountCostVo.paymentTimeStr ? formatDateTime(new Date(accountCostVo.paymentTimeStr)) : formatDateTime(new Date()),
          remark: accountCostVo.remark || undefined
        }
        const res = await saveUpdate(params)
        if (res.code === 200) {
          uni.showToast({title: operate.value === 'ADD' ? '新增成功' : '修改成功', icon: 'success'})
          closeModal()
          doFindPageAccountCost()
        } else {
          uni.showToast({title: res.msg || '操作失败', icon: 'none'})
        }
      } catch (e) {
        uni.showToast({title: '操作失败', icon: 'none'})
      } finally {
        loading.value = false
      }
    }

    const toDelete = async (id) => {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条记录吗？',
        success: async (res) => {
          if (res.confirm) {
            loading.value = true
            try {
              const result = await deleteAccountCost([id])
              if (result.code === 200) {
                uni.showToast({title: '删除成功', icon: 'success'})
                doFindPageAccountCost()
              } else {
                uni.showToast({title: result.msg || '删除失败', icon: 'none'})
              }
            } catch (e) {
              uni.showToast({title: '删除失败', icon: 'none'})
            } finally {
              loading.value = false
            }
          }
        }
      })
    }

    const batchDelete = async () => {
      uni.showModal({
        title: '批量删除',
        content: `确定要删除选中的 ${selectedKeys.value.length} 条记录吗？`,
        success: async (res) => {
          if (res.confirm) {
            loading.value = true
            try {
              const result = await deleteAccountCost(selectedKeys.value)
              if (result.code === 200) {
                uni.showToast({title: '删除成功', icon: 'success'})
                selectedKeys.value = []
                doFindPageAccountCost()
              } else {
                uni.showToast({title: result.msg || '删除失败', icon: 'none'})
              }
            } catch (e) {
              uni.showToast({title: '删除失败', icon: 'none'})
            } finally {
              loading.value = false
            }
          }
        }
      })
    }

    const onTypeChange = (e) => {
      searchQuery.costType = typeOptions.value[e.detail.value]
    }

    const onSignChange = (e) => {
      const selected = signOptions.value[e.detail.value]
      searchQuery.paymentSign = selected.value
      searchQuery.paymentSignName = selected.label
    }

    const onStartDateChange = (e) => {
      const dateStr = e.detail.value
      searchQuery.startDateDisplay = dateStr
      if (dateStr) {
        searchQuery.startDate = `${dateStr} 00:00:00`
      } else {
        searchQuery.startDate = ''
      }
    }

    const onEndDateChange = (e) => {
      const dateStr = e.detail.value
      searchQuery.endDateDisplay = dateStr
      if (dateStr) {
        searchQuery.endDate = `${dateStr} 23:59:59`
      } else {
        searchQuery.endDate = ''
      }
    }

    const onFormTypeChange = (e) => {
      accountCostVo.costType = typeOptions.value[e.detail.value]
    }

    const onFormDateChange = (e) => {
      accountCostVo.paymentTimeStr = e.detail.value
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }

    const loadCostTypes = async () => {
      try {
        const res = await findListSelectCostType()
        if (res.code === 200 && res.data) {
          typeOptions.value = res.data.map(item => item.value || item.label)
        }
      } catch (e) {
        console.log('加载类型失败', e)
      }
    }

    onMounted(() => {
      loadCostTypes()
      initDefaultDateRange()
      doFindPageAccountCost()
    })

    return {
      searchQuery,
      pageQuery,
      pagination,
      dataSource,
      selectedKeys,
      loading,
      expandCollapseFlag,
      showModal,
      operate,
      accountCostVo,
      typeOptions,
      signOptions,
      toSearch,
      queryFormReset,
      prevPage,
      nextPage,
      toggleSelect,
      showExpenseModal,
      closeModal,
      submitForm,
      toDelete,
      batchDelete,
      onTypeChange,
      onSignChange,
      onStartDateChange,
      onEndDateChange,
      onFormTypeChange,
      onFormDateChange,
      formatDate
    }
  }
}
</script>

<style lang="scss">
.account-mine-wrap {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.search-bar {
  display: flex;
  padding: 20rpx;
  background: #fff;
  gap: 16rpx;
}

.search-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 8rpx;
  padding: 0 20rpx;
  height: 72rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
}

.clear-icon {
  font-size: 24rpx;
  color: #999;
  padding: 8rpx;
}

.search-btn {
  padding: 0 32rpx;
  height: 72rpx;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.filter-section {
  background: #fff;
  margin: 20rpx;
  border-radius: 8rpx;
  padding: 20rpx;
}

.filter-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.filter-item {
  flex: 1;
}

.filter-label {
  display: block;
  font-size: 24rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.picker-value {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20rpx;
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.picker-arrow {
  font-size: 20rpx;
  color: #999;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
}

.reset-btn, .expand-btn {
  padding: 0 24rpx;
  height: 64rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  font-size: 26rpx;
  background: #fff;
}

.table-container {
  margin: 20rpx;
  background: #fff;
  border-radius: 8rpx;
  overflow: hidden;
}

.table-header {
  display: flex;
  background: #fafafa;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #e8e8e8;
}

.th {
  text-align: center;
  font-size: 26rpx;
  font-weight: bold;
  color: #666;
}

.table-body {
  max-height: 700rpx;
}

.table-row {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &.even {
    background: #fafafa;
  }
}

.td {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #ddd;
  border-radius: 4rpx;

  &.checked {
    background: #1890ff;
    border-color: #1890ff;

    &::after {
      content: '✓';
      display: block;
      color: #fff;
      font-size: 20rpx;
      text-align: center;
      line-height: 32rpx;
    }
  }
}

.icon-img {
  width: 48rpx;
  height: 48rpx;
  border-radius: 8rpx;
}

.icon-placeholder {
  font-size: 36rpx;
}

.cell-text {
  font-size: 26rpx;
  color: #333;

  &.ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &.income {
    color: #52c41a;
  }

  &.expense {
    color: #ff4d4f;
  }
}

.sign-tag {
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;

  &.expenses {
    background: rgba(255, 77, 79, 0.1);
    color: #ff4d4f;
  }

  &.income {
    background: rgba(82, 196, 26, 0.1);
    color: #52c41a;
  }
}

.actions {
  display: flex;
  gap: 12rpx;
}

.action-btn {
  font-size: 24rpx;

  &.edit {
    color: #1890ff;
  }

  &.delete {
    color: #ff4d4f;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
}

.empty-icon {
  font-size: 64rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: #fff;
  margin: 0 20rpx;
  border-radius: 8rpx;
}

.pagination-info {
  font-size: 24rpx;
  color: #666;
}

.pagination-btns {
  display: flex;
  gap: 16rpx;
}

.page-btn {
  padding: 12rpx 24rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  font-size: 26rpx;
  background: #fff;

  &[disabled] {
    opacity: 0.5;
    color: #999;
  }
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 20rpx;
  background: #fff;
  border-top: 1rpx solid #e8e8e8;
}

.selected-info {
  font-size: 26rpx;
  color: #666;
}

.clear-selection {
  color: #1890ff;
  margin-left: 16rpx;
}

.action-btns {
  display: flex;
  gap: 16rpx;
}

.action-btns .action-btn {
  padding: 0 32rpx;
  height: 72rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  border: none;

  &.primary {
    background: #1890ff;
    color: #fff;
  }

  &.danger {
    background: #ff4d4f;
    color: #fff;
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 600rpx;
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
}

.modal-close {
  font-size: 32rpx;
  color: #999;
  padding: 8rpx;
}

.modal-body {
  padding: 24rpx 30rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 12rpx;
}

.form-input {
  width: 100%;
  height: 72rpx;
  padding: 0 20rpx;
  border: 1rpx solid #e8e8e8;
  border-radius: 8rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.form-picker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 72rpx;
  padding: 0 20rpx;
  border: 1rpx solid #e8e8e8;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.form-textarea {
  width: 100%;
  height: 120rpx;
  padding: 16rpx 20rpx;
  border: 1rpx solid #e8e8e8;
  border-radius: 8rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.sign-radio-group {
  display: flex;
  gap: 40rpx;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 28rpx;

  &.active .radio-dot {
    background: #1890ff;

    &::after {
      content: '';
      display: block;
      width: 16rpx;
      height: 16rpx;
      background: #fff;
      border-radius: 50%;
      margin: 4rpx;
    }
  }
}

.radio-dot {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #f0f0f0;
}

.modal-btn {
  flex: 1;
  height: 72rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
  border: none;

  &.cancel {
    background: #f5f5f5;
    color: #666;
  }

  &.confirm {
    background: #1890ff;
    color: #fff;
  }
}
</style>