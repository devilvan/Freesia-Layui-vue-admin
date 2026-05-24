<template>
  <view class="page-wrap">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input class="search-input" placeholder="搜索描述或备注" v-model="searchQuery.costDesc" @confirm="toSearch"/>
        <text v-if="searchQuery.costDesc" class="clear-icon" @click="searchQuery.costDesc = ''; toSearch()">✕</text>
      </view>
      <button class="lay-btn lay-btn-primary lay-btn-sm" @click="toSearch">查询</button>
      <button class="lay-btn lay-btn-sm" @click="queryFormReset">重置</button>
    </view>

    <!-- 筛选区 -->
    <view class="lay-card">
      <view class="filter-row">
        <view class="filter-item">
          <text class="lay-form-label">开始日期</text>
          <picker mode="date" :value="searchQuery.startDateDisplay" @change="onStartDateChange">
            <view class="lay-select">
              <text :class="{ placeholder: !searchQuery.startDateDisplay }">{{ searchQuery.startDateDisplay || '请选择' }}</text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
        <view class="filter-item">
          <text class="lay-form-label">结束日期</text>
          <picker mode="date" :value="searchQuery.endDateDisplay" @change="onEndDateChange">
            <view class="lay-select">
              <text :class="{ placeholder: !searchQuery.endDateDisplay }">{{ searchQuery.endDateDisplay || '请选择' }}</text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
      </view>
      <view class="filter-row">
        <view class="filter-item">
          <text class="lay-form-label">类型</text>
          <picker mode="selector" :range="typeRange" @change="onTypeChange">
            <view class="lay-select">
              <text :class="{ placeholder: !searchQuery.costType }">{{ searchQuery.costType || '全部类型' }}</text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
        <view class="filter-item">
          <text class="lay-form-label">标识</text>
          <picker mode="selector" :range="signRange" range-key="label" @change="onSignChange">
            <view class="lay-select">
              <text :class="{ placeholder: !searchQuery.paymentSign }">{{ signRange.find(s => s.value === searchQuery.paymentSign)?.label || '全部标识' }}</text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
      </view>
      <!-- 多租户开关 -->
      <view class="flex-row align-center justify-between" style="margin-top: 10rpx">
        <text class="text-muted" style="font-size: 24rpx">统计所有账本</text>
        <view class="lay-switch-wrap">
          <view class="lay-switch" :class="{ active: allTenantFlag }" @click="toggleAllTenant"></view>
        </view>
      </view>
    </view>

    <!-- 数据表格 -->
    <view class="lay-card" style="padding: 0">
      <view class="lay-table-header">
        <view class="lay-table-th" style="width: 60rpx">图标</view>
        <view class="lay-table-th" style="width: 100rpx">类型</view>
        <view class="lay-table-th" style="width: 150rpx">金额</view>
        <view class="lay-table-th" style="width: 150rpx">时间</view>
        <view class="lay-table-th" style="width: 150rpx">备注</view>
      </view>

      <view v-if="loading" class="lay-empty"><text class="empty-text">加载中...</text></view>
      <view v-else-if="dataSource.length === 0" class="lay-empty">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无数据</text>
      </view>
      <view v-else v-for="(row, index) in dataSource" :key="row.id"
            class="lay-table-row" :class="{ stripe: index % 2 === 0 }"
            :style="getRowStyle(row)" @click="onRowTap(row)">
        <view class="lay-table-td" style="width: 60rpx">
          <image v-if="row.icon" :src="row.icon" mode="aspectFit" style="width: 52rpx; height: 52rpx; border-radius: 8rpx"/>
          <text v-else class="text-muted">--</text>
        </view>
        <view class="lay-table-td" style="width: 100rpx">
          <text class="ellipsis">{{ row.costType || '--' }}</text>
        </view>
        <view class="lay-table-td" style="width: 150rpx">
          <text :class="row.paymentSign === 'INCOME' ? 'text-income' : 'text-expense'" style="font-weight: 600">
            {{ row.paymentSign === 'INCOME' ? '+' : '-' }}{{ formatMoney(row.outlay) }}
          </text>
        </view>
        <view class="lay-table-td" style="width: 150rpx; font-size: 22rpx">{{ formatDate(row.paymentTime) }}</view>
        <view class="lay-table-td" style="width: 150rpx; font-size: 22rpx">{{ row.remark || '--' }}</view>
      </view>
    </view>

    <!-- 分页 -->
    <view class="lay-card" v-if="pagination.total > 0">
      <view class="lay-pagination">
        <text class="pagination-info">共 {{ pagination.total }} 条，第 {{ pagination.current }}/{{ pagination.pages }} 页</text>
        <view class="lay-btn-group gap-xs">
          <button class="lay-btn lay-btn-sm" :disabled="pagination.current <= 1" @click="prevPage">上一页</button>
          <button class="lay-btn lay-btn-sm" :disabled="pagination.current >= pagination.pages" @click="nextPage">下一页</button>
        </view>
      </view>
    </view>

    <!-- 新增/编辑弹窗 -->
    <view class="lay-modal-mask" v-if="showModal" @click="closeModal">
      <view class="lay-modal-expense" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">{{ operate === 'ADD' ? '新增账单' : '编辑账单' }}</text>
          <text class="lay-modal-close" @click="closeModal">✕</text>
        </view>
        <view class="lay-modal-body">
          <view class="lay-form modal-form">
            <view class="lay-form-item">
              <text class="lay-form-label required">描述</text>
              <input class="lay-input modal-input" placeholder="请输入描述" v-model="accountCostVo.costDesc"/>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label required">金额</text>
              <input class="lay-input modal-input" type="digit" placeholder="请输入金额" v-model="accountCostVo.outlay"/>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label">类型</text>
              <picker mode="selector" :range="typeOptions" @change="onFormTypeChange">
                <view class="lay-select modal-input">
                  <text :class="{ placeholder: !accountCostVo.costType }">{{ accountCostVo.costType || '请选择类型' }}</text>
                  <text class="arrow">▼</text>
                </view>
              </picker>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label required">标识</text>
              <view class="lay-radio-group">
                <view class="lay-radio" :class="{ active: accountCostVo.paymentSign === 'EXPENSES' }"
                      @click="accountCostVo.paymentSign = 'EXPENSES'">
                  <view class="radio-dot"></view><text>支出</text>
                </view>
                <view class="lay-radio" :class="{ active: accountCostVo.paymentSign === 'INCOME' }"
                      @click="accountCostVo.paymentSign = 'INCOME'">
                  <view class="radio-dot"></view><text>收入</text>
                </view>
              </view>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label">时间</text>
              <picker mode="date" :value="accountCostVo.paymentTimeStr" @change="onFormDateChange">
                <view class="lay-select modal-input">
                  <text :class="{ placeholder: !accountCostVo.paymentTimeStr }">{{ accountCostVo.paymentTimeStr || '请选择时间' }}</text>
                  <text class="arrow">▼</text>
                </view>
              </picker>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label">备注</text>
              <textarea class="lay-textarea modal-input" placeholder="请输入备注" v-model="accountCostVo.remark"></textarea>
            </view>
            <!-- 关联用户 -->
            <view class="lay-form-item">
              <text class="lay-form-label">关联用户</text>
              <view>
                <button class="lay-btn lay-btn-sm lay-btn-normal" @click="openUserPicker">选择用户</button>
                <text v-if="selectedUserTags.length === 0" class="text-muted" style="margin-left: 12rpx; font-size:24rpx">可选择多人进行费用分摊</text>
              </view>
              <view v-if="selectedUserTags.length > 0" class="user-tags">
                <view v-for="(tag, idx) in selectedUserTags" :key="idx" class="user-tag-item">
                  <text>{{ tag }}</text>
                  <text class="tag-close" @click="removeUserTag(idx)">✕</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        <view class="lay-modal-footer">
          <button class="lay-btn lay-btn-sm" @click="closeModal">取消</button>
          <button class="lay-btn lay-btn-sm lay-btn-primary" @click="submitForm">确认</button>
        </view>
      </view>
    </view>

    <!-- 关联用户选择弹窗 -->
    <view class="lay-modal-mask" v-if="showUserPicker" @click="showUserPicker = false">
      <view class="lay-modal" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">选择关联用户</text>
          <text class="lay-modal-close" @click="showUserPicker = false">✕</text>
        </view>
        <view class="search-bar" style="padding: 10rpx 0">
          <view class="search-input-wrap">
            <input class="search-input" placeholder="搜索用户" v-model="userSearchKeyword" @confirm="doUserSearch"/>
          </view>
          <button class="lay-btn lay-btn-sm lay-btn-primary" @click="doUserSearch">搜索</button>
        </view>
        <view class="lay-modal-body" style="max-height: 500rpx; overflow-y: auto">
          <view v-if="userListLoading" class="lay-empty"><text class="empty-text">加载中...</text></view>
          <view v-else-if="userList.length === 0" class="lay-empty">
            <text class="empty-text">暂无可选用户</text>
          </view>
          <view v-else v-for="user in userList" :key="user.id" class="user-pick-row"
                @click="toggleUserPick(user)">
            <view class="lay-checkbox" :class="{ checked: pickedUserIds.includes(user.id) }"></view>
            <text style="flex:1">{{ user.nickName || user.userName }}</text>
            <text class="text-muted" style="font-size:22rpx">{{ user.userName }}</text>
          </view>
        </view>
        <view class="lay-modal-footer">
          <text class="text-muted" style="flex:1; font-size:24rpx">已选 {{ pickedUserIds.length }} 人</text>
          <button class="lay-btn lay-btn-sm" @click="showUserPicker = false">取消</button>
          <button class="lay-btn lay-btn-sm lay-btn-primary" @click="confirmUserPick">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {ref, reactive, onMounted, computed} from 'vue'
import {findPageAccountCost, saveUpdate, deleteAccountCost, findListSelectCostType} from '@/api/account/Account'
import {findPageSysUserWithoutDataScope} from '@/api/system/User'

let _triggerAdd = null

export default {
  name: 'AccountMine',
  onShow() {
    const app = getApp()
    if (app.globalData && app.globalData.showAddModal) {
      app.globalData.showAddModal = false
      setTimeout(() => { _triggerAdd && _triggerAdd() }, 50)
    }
  },
  setup() {
    /* VAR */
    const allTenantFlag = ref(true)

    const searchQuery = reactive({
      costDesc: '', costType: '', paymentSign: '',
      startDate: '', endDate: '', startDateDisplay: '', endDateDisplay: ''
    })

    const pageQuery = reactive({ current: 1, limit: 15 })
    const pagination = reactive({ total: 0, current: 1, pages: 0 })

    const dataSource = ref([])
    const loading = ref(false)
    const showModal = ref(false)
    const operate = ref('ADD')

    const accountCostVo = reactive({
      id: '', costDesc: '', outlay: '', costType: '',
      paymentSign: 'EXPENSES', paymentTimeStr: '', paymentTime: null, remark: '',
      accountCostUserIdList: [], accountCostUserNameList: []
    })

    const typeOptions = ref([])
    const typeRange = computed(() => ['全部类型', ...typeOptions.value])
    const signOptions = ref([{label: '支出', value: 'EXPENSES'}, {label: '收入', value: 'INCOME'}])
    const signRange = computed(() => [{label: '全部标识', value: ''}, ...signOptions.value])

    // 关联用户
    const showUserPicker = ref(false)
    const userList = ref([])
    const userListLoading = ref(false)
    const userSearchKeyword = ref('')
    const pickedUserIds = ref([])
    const pickedUserNames = ref([])
    const selectedUserTags = ref([])

    /* FUNCTION */
    const formatDateTime = (date) => {
      const pad = (n) => String(n).padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
    }

    const initDefaultDateRange = () => {
      const endDate = new Date()
      const startDate = new Date()
      startDate.setDate(startDate.getDate() - 7)
      const pad = (n) => String(n).padStart(2, '0')
      searchQuery.startDate = `${startDate.getFullYear()}-${pad(startDate.getMonth() + 1)}-${pad(startDate.getDate())} 00:00:00`
      searchQuery.endDate = `${endDate.getFullYear()}-${pad(endDate.getMonth() + 1)}-${pad(endDate.getDate())} 23:59:59`
      searchQuery.startDateDisplay = `${startDate.getFullYear()}-${pad(startDate.getMonth() + 1)}-${pad(startDate.getDate())}`
      searchQuery.endDateDisplay = `${endDate.getFullYear()}-${pad(endDate.getMonth() + 1)}-${pad(endDate.getDate())}`
    }

    const toggleAllTenant = () => { allTenantFlag.value = !allTenantFlag.value; toSearch() }
    const toSearch = () => { pageQuery.current = 1; doFindPageAccountCost() }

    const queryFormReset = () => {
      searchQuery.costDesc = ''; searchQuery.costType = ''; searchQuery.paymentSign = ''
      initDefaultDateRange(); pageQuery.current = 1; doFindPageAccountCost()
    }

    const doFindPageAccountCost = async () => {
      loading.value = true
      try {
        const params = { allTenantFlag: allTenantFlag.value }
        if (searchQuery.costDesc) params.costDesc = searchQuery.costDesc
        if (searchQuery.costType) params.costType = searchQuery.costType
        if (searchQuery.paymentSign) params.paymentSign = searchQuery.paymentSign
        if (searchQuery.startDate && searchQuery.endDate) {
          params.paymentTimeRange = [searchQuery.startDate, searchQuery.endDate].join(',')
        }
        const res = await findPageAccountCost(params, pageQuery)
        if (res.code === 200) {
          dataSource.value = res.rows || res.records || []
          pagination.total = res.total || 0
          pagination.current = res.current || pageQuery.current
          pagination.pages = Math.max(1, Math.ceil((res.total || 0) / pageQuery.limit))
        }
      } catch (e) {
        console.error('查询失败', e)
        uni.showToast({title: '查询失败', icon: 'none'})
      } finally { loading.value = false }
    }

    const prevPage = () => { if (pageQuery.current > 1) { pageQuery.current--; doFindPageAccountCost() } }
    const nextPage = () => { if (pageQuery.current < pagination.pages) { pageQuery.current++; doFindPageAccountCost() } }

    const onRowTap = (row) => {
      uni.showActionSheet({
        itemList: ['编辑', '删除'],
        success: (res) => {
          if (res.tapIndex === 0) showExpenseModal('EDIT', row)
          else if (res.tapIndex === 1) confirmDelete(row)
        }
      })
    }

    const showExpenseModal = (op, row) => {
      operate.value = op
      if (op === 'ADD') {
        Object.assign(accountCostVo, {
          id: '', costDesc: '', outlay: '', costType: '',
          paymentSign: 'EXPENSES', paymentTimeStr: '', paymentTime: null, remark: '',
          accountCostUserIdList: [], accountCostUserNameList: []
        })
        selectedUserTags.value = []
      } else if (op === 'EDIT') {
        Object.assign(accountCostVo, {
          id: row.id || '', costDesc: row.costDesc || '',
          outlay: row.outlay != null ? String(row.outlay) : '',
          costType: row.costType || '',
          paymentSign: row.paymentSign || 'EXPENSES',
          paymentTimeStr: formatDate(row.paymentTime), paymentTime: row.paymentTime,
          remark: row.remark || '',
          accountCostUserIdList: row.accountCostUserId ? row.accountCostUserId.split(',') : [],
          accountCostUserNameList: row.accountCostUserName ? row.accountCostUserName.split(',') : []
        })
        selectedUserTags.value = [...(accountCostVo.accountCostUserNameList || [])]
      }
      showModal.value = true
    }

    const closeModal = () => { showModal.value = false }

    const submitForm = async () => {
      if (!accountCostVo.costDesc) { uni.showToast({title: '请输入描述', icon: 'none'}); return }
      if (!accountCostVo.outlay || Number(accountCostVo.outlay) <= 0) { uni.showToast({title: '请输入有效金额', icon: 'none'}); return }
      if (!accountCostVo.paymentSign) { uni.showToast({title: '请选择标识', icon: 'none'}); return }

      loading.value = true
      try {
        const params = {
          id: accountCostVo.id || undefined,
          costDesc: accountCostVo.costDesc,
          outlay: Number(accountCostVo.outlay),
          costType: accountCostVo.costType || undefined,
          paymentSign: accountCostVo.paymentSign,
          paymentTime: accountCostVo.paymentTime || formatDateTime(new Date()),
          remark: accountCostVo.remark || undefined,
          accountCostUserIdList: accountCostVo.accountCostUserIdList || [],
          accountCostUserNameList: accountCostVo.accountCostUserNameList || []
        }
        const res = await saveUpdate(params)
        if (res.code === 200) {
          uni.showToast({title: operate.value === 'ADD' ? '新增成功' : '修改成功', icon: 'success'})
          closeModal(); doFindPageAccountCost()
        } else { uni.showToast({title: res.msg || '操作失败', icon: 'none'}) }
      } catch (e) { uni.showToast({title: '操作失败', icon: 'none'}) }
      finally { loading.value = false }
    }

    const confirmDelete = (row) => {
      uni.showModal({
        title: '确认删除',
        content: `确定要删除「${row.costDesc || '这条记录'}」吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              const result = await deleteAccountCost([row.id])
              if (result.code === 200) { uni.showToast({title: '删除成功', icon: 'success'}); doFindPageAccountCost() }
              else { uni.showToast({title: result.msg || '删除失败', icon: 'none'}) }
            } catch (e) { uni.showToast({title: '删除失败', icon: 'none'}) }
          }
        }
      })
    }

    // 关联用户选择器
    const openUserPicker = () => {
      pickedUserIds.value = [...(accountCostVo.accountCostUserIdList || [])]
      pickedUserNames.value = [...(accountCostVo.accountCostUserNameList || [])]
      userSearchKeyword.value = ''
      doUserSearch()
      showUserPicker.value = true
    }

    const doUserSearch = async () => {
      userListLoading.value = true
      try {
        const query = { nickName: userSearchKeyword.value || undefined, userName: userSearchKeyword.value || undefined }
        const res = await findPageSysUserWithoutDataScope(query, { current: 1, limit: 50 })
        if (res.code === 200) {
          userList.value = res.rows || res.records || []
        }
      } catch (e) { console.error('搜索用户失败', e) }
      finally { userListLoading.value = false }
    }

    const toggleUserPick = (user) => {
      const idx = pickedUserIds.value.indexOf(user.id)
      if (idx > -1) {
        pickedUserIds.value.splice(idx, 1)
        pickedUserNames.value.splice(idx, 1)
      } else {
        pickedUserIds.value.push(user.id)
        pickedUserNames.value.push(user.nickName || user.userName)
      }
    }

    const confirmUserPick = () => {
      accountCostVo.accountCostUserIdList = [...pickedUserIds.value]
      accountCostVo.accountCostUserNameList = [...pickedUserNames.value]
      selectedUserTags.value = [...pickedUserNames.value]
      showUserPicker.value = false
    }

    const removeUserTag = (idx) => {
      selectedUserTags.value.splice(idx, 1)
      accountCostVo.accountCostUserIdList.splice(idx, 1)
      accountCostVo.accountCostUserNameList.splice(idx, 1)
    }

    // 筛选选择器
    const onTypeChange = (e) => {
      const idx = e.detail.value; searchQuery.costType = idx === 0 ? '' : typeOptions.value[idx - 1]
    }
    const onSignChange = (e) => {
      const idx = e.detail.value; searchQuery.paymentSign = idx === 0 ? '' : signOptions.value[idx - 1].value
    }
    const onStartDateChange = (e) => {
      const dateStr = e.detail.value; searchQuery.startDateDisplay = dateStr
      searchQuery.startDate = dateStr ? `${dateStr} 00:00:00` : ''
    }
    const onEndDateChange = (e) => {
      const dateStr = e.detail.value; searchQuery.endDateDisplay = dateStr
      searchQuery.endDate = dateStr ? `${dateStr} 23:59:59` : ''
    }
    const onFormTypeChange = (e) => { accountCostVo.costType = typeOptions.value[e.detail.value] }
    const onFormDateChange = (e) => {
      const dateStr = e.detail.value; accountCostVo.paymentTimeStr = dateStr
      accountCostVo.paymentTime = dateStr ? formatDateTime(new Date(dateStr)) : null
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      if (isNaN(date.getTime())) return ''
      const pad = (n) => String(n).padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
    }
    const formatMoney = (val) => {
      if (val == null) return '0.00'; return Number(val).toFixed(2)
    }
    const getRowStyle = (row) => {
      if (!row.paymentTime) return ''
      const day = new Date(row.paymentTime).getDay()
      const colors = [
        'background-color: rgba(255, 154, 158, 0.25)',
        'background-color: rgba(255, 87, 34, 0.25)',
        'background-color: rgba(255, 184, 0, 0.25)',
        'background-color: rgba(54, 179, 104, 0.25)',
        'background-color: rgba(45, 140, 240, 0.25)',
        'background-color: rgba(57, 99, 188, 0.25)',
        'background-color: rgba(153, 138, 219, 0.25)'
      ]
      return colors[day] || ''
    }

    const loadCostTypes = async () => {
      try {
        const res = await findListSelectCostType()
        if (res.code === 200 && res.data) {
          typeOptions.value = res.data.map(item => item.name || item.label || item.value || item)
        }
      } catch (e) { console.log('加载类型失败', e) }
    }

    _triggerAdd = () => { showExpenseModal('ADD', null) }

    onMounted(() => {
      loadCostTypes(); initDefaultDateRange(); doFindPageAccountCost()
      const app = getApp()
      if (app.globalData && app.globalData.showAddModal) {
        app.globalData.showAddModal = false; showExpenseModal('ADD', null)
      }
    })

    return {
      searchQuery, pageQuery, pagination, dataSource,
      loading, showModal, operate, accountCostVo, typeOptions, typeRange,
      signOptions, signRange, allTenantFlag,
      toSearch, queryFormReset, prevPage, nextPage,
      onRowTap, showExpenseModal, closeModal,
      submitForm, confirmDelete,
      onTypeChange, onSignChange, onStartDateChange, onEndDateChange,
      onFormTypeChange, onFormDateChange,
      formatDate, formatMoney, getRowStyle, toggleAllTenant,
      showUserPicker, userList, userListLoading, userSearchKeyword,
      pickedUserIds, selectedUserTags,
      openUserPicker, doUserSearch, toggleUserPick, confirmUserPick, removeUserTag
    }
  }
}
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.filter-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.filter-item { flex: 1; }

.clear-icon { font-size: 24rpx; color: #999; padding: 8rpx; }

/* 弹窗内输入框宽度约束 */
.lay-modal-expense {
  width: 88%;
  max-height: 100vh;
  background: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}

.modal-form {
  max-width: 100%;
}

.modal-input {
  max-height: 60rpx;
  max-width: 100%;
  box-sizing: border-box;
  width: 100%;
}

/* 关联用户标签 */
.user-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 12rpx;
}

.user-tag-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 6rpx 16rpx;
  background: rgba(0, 150, 136, 0.1);
  border: 1px solid rgba(0, 150, 136, 0.3);
  border-radius: 4rpx;
  font-size: 22rpx;
  color: #009688;
}

.tag-close {
  font-size: 20rpx;
  padding: 2rpx 6rpx;
  color: #999;
}

/* 用户选择列表 */
.user-pick-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx;
  border-bottom: 1px solid #f0f0f0;
}
</style>
