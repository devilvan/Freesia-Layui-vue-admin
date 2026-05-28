<template>
  <view class="page-wrap">
    <!-- 租户选择器 -->
    <view class="tenant-bar">
      <text class="tenant-label">当前账本：</text>
      <picker mode="selector" :range="tenantNames" @change="onTenantChange" :value="currentTenantIndex">
        <view class="tenant-picker">
          <text>{{ currentTenantName }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
    </view>

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
            class="lay-table-row-wrap" :class="{ stripe: index % 2 === 0 }"
            :style="getRowStyle(row)" @click="onRowTap(row)">
        <view class="lay-table-row">
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
        <!-- 分摊明细 -->
        <view v-if="row.accountCostUserAllocDtoList && row.accountCostUserAllocDtoList.length > 0" class="alloc-detail" @click.stop>
          <view class="alloc-detail-tag">
            <text :class="'alloc-badge ' + getAllocStatusClass(row)">{{ getAllocStatus(row) }}</text>
          </view>
          <view v-for="(alloc, ai) in row.accountCostUserAllocDtoList" :key="ai" class="alloc-detail-item">
            <text class="alloc-detail-user">{{ alloc.sysUserDto?.nickName || alloc.nickName || alloc.userName || '--' }}</text>
            <text class="alloc-detail-amount">¥{{ formatMoney(alloc.amount) }}</text>
            <text :class="alloc.allocFlag ? 'alloc-flag-true' : 'alloc-flag-false'">{{ alloc.allocFlag ? '已分摊' : '未分摊' }}</text>
          </view>
        </view>
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
          <!-- 步骤指示器 -->
          <view class="step-indicator" v-if="accountCostVo.accountCostUserIdList && accountCostVo.accountCostUserIdList.length > 0">
            <view class="step-item" :class="{ active: addExpenseActive === 0, done: addExpenseActive > 0 }">
              <view class="step-dot">1</view>
              <text class="step-label">记账</text>
            </view>
            <view class="step-line" :class="{ done: addExpenseActive > 0 }"></view>
            <view class="step-item" :class="{ active: addExpenseActive === 1 }">
              <view class="step-dot">2</view>
              <text class="step-label">分摊</text>
            </view>
          </view>

          <!-- 步骤一：记账 -->
          <view v-show="addExpenseActive === 0">
            <view class="lay-form modal-form">
              <view class="lay-form-item">
                <text class="lay-form-label required">描述</text>
                <input class="lay-input modal-input" placeholder="请输入描述" v-model="accountCostVo.costDesc"
                       @input="onCostDescInput"/>
                <view v-if="showSuggestion && suggestionList.length > 0" class="suggestion-dropdown">
                  <view v-for="item in suggestionList" :key="item.value" class="suggestion-item"
                        @click="selectSuggestion(item)">
                    <image v-if="item.iconUrl" :src="item.iconUrl" mode="aspectFit"
                           style="width:36rpx;height:36rpx;border-radius:6rpx"/>
                    <text style="flex:1;font-size:26rpx">{{ item.value }}</text>
                  </view>
                </view>
              </view>
              <view class="lay-form-item">
                <text class="lay-form-label required">金额</text>
                <input class="lay-input modal-input" type="digit" placeholder="请输入金额" v-model="accountCostVo.outlay"/>
              </view>
              <view class="lay-form-item">
                <text class="lay-form-label required">图标</text>
                <view class="icon-pick-row">
                  <view class="icon-pick-box" @click="openIconGrid">
                    <image v-if="accountCostVo.icon" :src="accountCostVo.icon" mode="aspectFit" class="icon-pick-img"/>
                    <text v-else class="icon-pick-empty">?</text>
                  </view>
                  <text class="text-muted" style="font-size:24rpx">点击图标选择</text>
                </view>
              </view>
              <view class="lay-form-item">
                <text class="lay-form-label required">类型</text>
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
                <text class="lay-form-label required">时间</text>
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

          <!-- 步骤二：分摊 -->
          <view v-show="addExpenseActive === 1">
            <view class="alloc-summary">
              <text class="alloc-total-label">总金额：</text>
              <text class="alloc-total-amount">¥{{ formatMoney(accountCostVo.outlay) }}</text>
            </view>
            <view class="alloc-hint">提示：若所有费用金额为0或不填写，则默认平分金额</view>
            <view class="alloc-list">
              <view v-for="(item, idx) in accountCostVo.accountCostUserAllocVoList" :key="idx" class="alloc-item">
                <view class="alloc-user-info">
                  <text class="alloc-user-name">{{ item.nickName || item.userName }}</text>
                </view>
                <view class="alloc-input-row">
                  <text class="alloc-label">金额</text>
                  <input class="lay-input alloc-amount-input" type="digit" v-model="item.amount" placeholder="0.00"/>
                </view>
                <view class="alloc-switch-row">
                  <text class="alloc-label">已分摊</text>
                  <view class="lay-switch-wrap" style="transform: scale(0.7)">
                    <view class="lay-switch" :class="{ active: item.allocFlag }" @click="item.allocFlag = !item.allocFlag"></view>
                  </view>
                </view>
              </view>
            </view>
            <view style="margin-top: 16rpx">
              <button class="lay-btn lay-btn-sm lay-btn-normal" @click="allocRetainAmount">分摊剩余金额</button>
            </view>
          </view>
        </view>
        <view class="lay-modal-footer">
          <button class="lay-btn lay-btn-sm" @click="closeModal">取消</button>
          <button v-if="accountCostVo.accountCostUserIdList && accountCostVo.accountCostUserIdList.length > 0 && addExpenseActive === 0"
                  class="lay-btn lay-btn-sm lay-btn-primary" @click="toNext">下一步</button>
          <button v-if="addExpenseActive === 1" class="lay-btn lay-btn-sm" @click="toPrevious">上一步</button>
          <button v-if="!accountCostVo.accountCostUserIdList || accountCostVo.accountCostUserIdList.length === 0 || addExpenseActive === 1"
                  class="lay-btn lay-btn-sm lay-btn-primary" @click="submitForm">保存</button>
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

    <!-- 图标选择弹窗 -->
    <view class="lay-modal-mask" v-if="showIconGrid" @click="showIconGrid = false">
      <view class="lay-modal" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">选择图标</text>
          <text class="lay-modal-close" @click="showIconGrid = false">✕</text>
        </view>
        <view class="lay-modal-body" style="max-height:600rpx;overflow-y:auto">
          <view v-if="iconGridLoading" class="lay-empty"><text class="empty-text">加载中...</text></view>
          <view v-else class="icon-grid">
            <view v-for="item in iconGridList" :key="item.value"
                  class="icon-grid-item" :class="{ selected: accountCostVo.costType === item.value }"
                  @click="pickIcon(item)">
              <image v-if="item.iconUrl" :src="item.iconUrl" mode="aspectFit" class="icon-grid-img"/>
              <view v-else class="icon-grid-placeholder"></view>
              <text class="icon-grid-name ellipsis">{{ item.value }}</text>
            </view>
          </view>
        </view>
        <view class="lay-modal-footer">
          <text class="text-muted" style="flex:1;font-size:24rpx" v-if="accountCostVo.costType">
            已选：{{ accountCostVo.costType }}
          </text>
          <button class="lay-btn lay-btn-sm" @click="showIconGrid = false">取消</button>
          <button class="lay-btn lay-btn-sm lay-btn-primary" @click="showIconGrid = false">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {ref, reactive, onMounted, computed} from 'vue'
import {findPageAccountCost, saveUpdate, deleteAccountCost, findListSelectCostType, findCacheCostType, findListSysUserById, findListAllocByCostId} from '@/api/account/Account'
import {PaymentSign} from '@/types/account/Account'
import {findPageSysUserWithoutDataScope} from '@/api/system/User'
import {useUserStore} from '@/store/user'

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
    const addExpenseActive = ref(0)

    const accountCostVo = reactive({
      id: '', costDesc: '', outlay: '', costType: '',
      paymentSign: 'EXPENSES', paymentTimeStr: '', paymentTime: null, remark: '',
      accountCostUserIdList: [], accountCostUserNameList: []
    })

    const userStore = useUserStore()
    const tenantList = computed(() => userStore.state.sysTenantDtoList || [])
    const tenantNames = computed(() => tenantList.value.map(t => t.name || ''))
    const currentTenantName = computed(() => {
      const found = tenantList.value.find(t => t.id === userStore.state.currentTenantId)
      return found ? found.name : (tenantList.value[0]?.name || '默认账本')
    })
    const currentTenantIndex = computed(() => {
      const idx = tenantList.value.findIndex(t => t.id === userStore.state.currentTenantId)
      return idx >= 0 ? idx : 0
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

    // 自动补全
    const showSuggestion = ref(false)
    const suggestionList = ref([])
    // 图标选择
    const showIconGrid = ref(false)
    const iconGridList = ref([])
    const iconGridLoading = ref(false)

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

    // 租户切换
    const onTenantChange = (e) => {
      const idx = e.detail.value
      const tenant = tenantList.value[idx]
      if (tenant && tenant.id) {
        userStore.setCurrentTenant(tenant.id)
        // 切换租户后刷新页面（模拟PC端的 window.location.reload）
        doFindPageAccountCost()
      }
    }
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
      addExpenseActive.value = 0
      if (op === 'ADD') {
        Object.assign(accountCostVo, {
          id: '', costDesc: '', outlay: '', costType: '', icon: '',
          paymentSign: 'EXPENSES', paymentTimeStr: '', paymentTime: null, remark: '',
          accountCostUserIdList: [], accountCostUserNameList: [], accountCostUserAllocVoList: []
        })
        selectedUserTags.value = []
        showSuggestion.value = false
      } else if (op === 'EDIT') {
        Object.assign(accountCostVo, {
          id: row.id || '', costDesc: row.costDesc || '',
          outlay: row.outlay != null ? String(row.outlay) : '',
          costType: row.costType || '', icon: row.icon || '',
          paymentSign: row.paymentSign || 'EXPENSES',
          paymentTimeStr: formatDate(row.paymentTime), paymentTime: row.paymentTime,
          remark: row.remark || '',
          accountCostUserIdList: row.accountCostUserId ? row.accountCostUserId.split(',') : [],
          accountCostUserNameList: row.accountCostUserName ? row.accountCostUserName.split(',') : [],
          accountCostUserAllocVoList: []
        })
        selectedUserTags.value = [...(accountCostVo.accountCostUserNameList || [])]
        showSuggestion.value = false
      }
      showModal.value = true
    }

    const closeModal = () => { showModal.value = false; addExpenseActive.value = 0 }

    const submitForm = async () => {
      if (!accountCostVo.costDesc) { uni.showToast({title: '请输入描述', icon: 'none'}); return }
      if (!accountCostVo.outlay || Number(accountCostVo.outlay) <= 0) { uni.showToast({title: '请输入有效金额', icon: 'none'}); return }
      if (!accountCostVo.paymentSign) { uni.showToast({title: '请选择标识', icon: 'none'}); return }

      // 如果选择了关联用户且在第一步，跳到下一步
      if (accountCostVo.accountCostUserIdList && accountCostVo.accountCostUserIdList.length > 0 && addExpenseActive.value === 0) {
        toNext()
        return
      }

      // 分摊金额校验
      const allocList = accountCostVo.accountCostUserAllocVoList
      if (allocList && allocList.length > 0) {
        const totalAmount = allocList.reduce((sum, item) => sum + (parseFloat(item.amount) || 0), 0)
        const outlay = parseFloat(accountCostVo.outlay) || 0
        if (totalAmount > outlay) {
          uni.showToast({title: '费用分摊的合计金额不能超过总金额！', icon: 'none'})
          return
        }
      }

      loading.value = true
      try {
        const params = {
          id: accountCostVo.id || undefined,
          costDesc: accountCostVo.costDesc,
          outlay: Number(accountCostVo.outlay),
          costType: accountCostVo.costType || undefined,
          icon: accountCostVo.icon || undefined,
          paymentSign: accountCostVo.paymentSign,
          paymentTime: accountCostVo.paymentTime || formatDateTime(new Date()),
          remark: accountCostVo.remark || undefined,
          accountCostUserIdList: accountCostVo.accountCostUserIdList || [],
          accountCostUserNameList: accountCostVo.accountCostUserNameList || [],
          accountCostUserAllocVoList: accountCostVo.accountCostUserAllocVoList || []
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

    // ==================== 分摊步骤 ====================
    const toNext = async () => {
      if (!accountCostVo.costDesc) { uni.showToast({title: '请输入描述', icon: 'none'}); return }
      if (!accountCostVo.outlay || Number(accountCostVo.outlay) <= 0) { uni.showToast({title: '请输入有效金额', icon: 'none'}); return }
      if (!accountCostVo.paymentSign) { uni.showToast({title: '请选择标识', icon: 'none'}); return }

      if (accountCostVo.accountCostUserIdList && accountCostVo.accountCostUserIdList.length > 0) {
        if (accountCostVo.paymentSign !== PaymentSign.EXPENSES) {
          uni.showToast({title: '关联用户后进入费用分摊要求标识为【支出】', icon: 'none'})
          return
        }
        addExpenseActive.value = 1
        if (operate.value === 'ADD') {
          try {
            const res = await findListSysUserById(accountCostVo.accountCostUserIdList)
            if (res.code === 200 && res.data) {
              accountCostVo.accountCostUserAllocVoList = res.data.map(item => ({...item, allocFlag: true}))
            }
          } catch (e) { console.error('查询用户失败', e) }
        } else if (operate.value === 'EDIT' && accountCostVo.id) {
          try {
            const res = await findListAllocByCostId(accountCostVo.id)
            if (res.code === 200) {
              if (res.data && res.data.length > 0) {
                accountCostVo.accountCostUserAllocVoList = res.data
              } else {
                const userRes = await findListSysUserById(accountCostVo.accountCostUserIdList)
                if (userRes.code === 200 && userRes.data) {
                  accountCostVo.accountCostUserAllocVoList = userRes.data.map(item => ({...item, allocFlag: true}))
                }
              }
            }
          } catch (e) { console.error('查询分摊数据失败', e) }
        }
      }
    }

    const toPrevious = () => { addExpenseActive.value = addExpenseActive.value - 1 }

    const splitNumber = (total, parts) => {
      if (parts <= 0 || !Number.isInteger(parts)) return []
      if (total < 0) return []
      const totalCents = Math.round(total * 100)
      const baseValue = Math.floor(totalCents / parts)
      const remainder = totalCents % parts
      const result = []
      for (let i = 0; i < parts; i++) {
        if (i < remainder) { result.push((baseValue + 1) / 100) }
        else { result.push(baseValue / 100) }
      }
      return result
    }

    const allocRetainAmount = () => {
      const outlay = parseFloat(accountCostVo.outlay) || 0
      const list = accountCostVo.accountCostUserAllocVoList
      if (!list || list.length === 0) {
        uni.showToast({title: '分摊数据不合法，请联系管理员', icon: 'none'})
        return
      }
      // 检查已填金额
      const totalAmount = list.reduce((sum, item) => sum + (parseFloat(item.amount) || 0), 0)
      if (totalAmount > outlay) {
        uni.showToast({title: '费用分摊的合计金额不能超过总金额！', icon: 'none'})
        return
      }
      const existAllocList = list.filter(item => item.amount && parseFloat(item.amount) > 0)
      if (existAllocList.length === 0) {
        // 都没填写，平分
        const numbers = splitNumber(outlay, list.length)
        list.forEach((item, i) => { item.amount = numbers[i] })
      } else if (existAllocList.length !== list.length) {
        // 部分填写，剩余金额平分给未填的
        let remain = outlay - existAllocList.reduce((sum, item) => sum + (parseFloat(item.amount) || 0), 0)
        const notExistList = list.filter(item => !item.amount || parseFloat(item.amount) === 0)
        const numbers = splitNumber(remain, notExistList.length)
        notExistList.forEach((item, i) => { item.amount = numbers[i] })
      }
      // 触发响应式更新
      accountCostVo.accountCostUserAllocVoList = [...list]
    }

    // ==================== 自动补全 ====================
    const onCostDescInput = () => {
      const val = accountCostVo.costDesc
      if (!val || val.trim() === '') { showSuggestion.value = false; return }
      findCacheCostType({ costDesc: val }).then((res) => {
        if (res.code === 200 && res.data && res.data.length > 0) {
          suggestionList.value = res.data.map(item => ({
            value: item.value, iconUrl: item.iconUrl
          })).filter(item => item.iconUrl) // 只显示有图标的
          showSuggestion.value = suggestionList.value.length > 0
        } else { showSuggestion.value = false }
      }).catch(() => { showSuggestion.value = false })
    }

    const selectSuggestion = (item) => {
      accountCostVo.icon = item.iconUrl || ''
      accountCostVo.costType = item.value || ''
      accountCostVo.costDesc = item.value || ''
      showSuggestion.value = false
    }

    // ==================== 图标选择 ====================
    const openIconGrid = () => {
      iconGridLoading.value = true
      findCacheCostType({ costDesc: '' }).then((res) => {
        if (res.code === 200 && res.data) {
          iconGridList.value = res.data.filter(item => item.iconUrl)
        }
        iconGridLoading.value = false
      }).catch(() => { iconGridLoading.value = false })
      showIconGrid.value = true
    }

    const pickIcon = (item) => {
      accountCostVo.icon = item.iconUrl || ''
      accountCostVo.costType = item.value || ''
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
    const getAllocStatus = (row) => {
      const list = row.accountCostUserAllocDtoList
      if (!list || list.length === 0) return ''
      const allTrue = list.every(item => item.allocFlag)
      const allFalse = list.every(item => !item.allocFlag)
      if (allTrue) return '已分摊'
      if (allFalse) return '未分摊'
      return '部分分摊'
    }
    const getAllocStatusClass = (row) => {
      const list = row.accountCostUserAllocDtoList
      if (!list || list.length === 0) return ''
      const allTrue = list.every(item => item.allocFlag)
      const allFalse = list.every(item => !item.allocFlag)
      if (allTrue) return 'alloc-done'
      if (allFalse) return 'alloc-pending'
      return 'alloc-partial'
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
      tenantList, tenantNames, currentTenantName, currentTenantIndex,
      toSearch, queryFormReset, prevPage, nextPage,
      onRowTap, showExpenseModal, closeModal,
      submitForm, confirmDelete, onTenantChange,
      onTypeChange, onSignChange, onStartDateChange, onEndDateChange,
      onFormTypeChange, onFormDateChange,
      formatDate, formatMoney, getRowStyle, toggleAllTenant,
      getAllocStatus, getAllocStatusClass,
      showUserPicker, userList, userListLoading, userSearchKeyword,
      pickedUserIds, selectedUserTags,
      openUserPicker, doUserSearch, toggleUserPick, confirmUserPick, removeUserTag,
      showSuggestion, suggestionList, onCostDescInput, selectSuggestion,
      showIconGrid, iconGridList, iconGridLoading, openIconGrid, pickIcon,
      addExpenseActive, toNext, toPrevious, allocRetainAmount, splitNumber
    }
  }
}
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
  padding-bottom: 120rpx;
}

/* 租户选择栏 */
.tenant-bar {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  background: #fff;
  gap: 16rpx;
  border-bottom: 1px solid #f0f0f0;
}

.tenant-label {
  font-size: 26rpx;
  color: #666;
  flex-shrink: 0;
}

.tenant-picker {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 8rpx 20rpx;
  background: rgba(0, 150, 136, 0.08);
  border: 1px solid rgba(0, 150, 136, 0.3);
  border-radius: 4rpx;
  font-size: 26rpx;
  color: #009688;
  font-weight: 500;
}

.filter-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.filter-item { flex: 1; }

.clear-icon { font-size: 24rpx; color: #999; padding: 8rpx; }

/* 自动补全下拉 */
.suggestion-dropdown {
  position: relative; z-index: 600;
  background: #fff; border: 1px solid #e6e6e6; border-radius: 4px;
  max-height: 300rpx; overflow-y: auto;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
  margin-top: 4rpx;
}
.suggestion-item {
  display: flex; align-items: center; gap: 16rpx;
  padding: 16rpx 20rpx; border-bottom: 1px solid #f5f5f5;
  &:last-child { border-bottom: none; }
  &:active { background: #f5f7fa; }
}

/* 图标选择 */
.icon-pick-row { display: flex; align-items: center; gap: 20rpx; }
.icon-pick-box {
  width: 80rpx; height: 80rpx;
  border: 2rpx dashed #ddd; border-radius: 8rpx;
  display: flex; align-items: center; justify-content: center;
  background: #fafafa;
}
.icon-pick-img { width: 64rpx; height: 64rpx; border-radius: 6rpx; }
.icon-pick-empty { font-size: 40rpx; color: #ccc; }

/* 图标网格 */
.icon-grid { display: flex; flex-wrap: wrap; }
.icon-grid-item {
  width: 120rpx; display: flex; flex-direction: column; align-items: center;
  padding: 16rpx 8rpx; border-radius: 8rpx; border: 2rpx solid transparent;
  &.selected { border-color: #009688; background: rgba(0,150,136,0.06); }
}
.icon-grid-img { width: 56rpx; height: 56rpx; border-radius: 8rpx; }
.icon-grid-placeholder { width: 56rpx; height: 56rpx; border-radius: 8rpx; background: #f0f0f0; }
.icon-grid-name { font-size: 20rpx; color: #666; margin-top: 8rpx; max-width: 110rpx; text-align: center; }

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
  max-height: 120rpx;
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

/* 步骤指示器 */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0 30rpx;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.step-dot {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #e8e8e8;
  color: #999;
  font-size: 24rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.step-item.active .step-dot {
  background: #009688;
  color: #fff;
}

.step-item.done .step-dot {
  background: #009688;
  color: #fff;
}

.step-label {
  font-size: 22rpx;
  color: #999;
}

.step-item.active .step-label,
.step-item.done .step-label {
  color: #009688;
  font-weight: 500;
}

.step-line {
  width: 80rpx;
  height: 2rpx;
  background: #e8e8e8;
  margin: 0 16rpx;
  margin-bottom: 28rpx;
}

.step-line.done {
  background: #009688;
}

/* 分摊金额 */
.alloc-summary {
  display: flex;
  justify-content: center;
  align-items: baseline;
  padding: 20rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  margin-bottom: 12rpx;
}

.alloc-total-label {
  font-size: 28rpx;
  color: #666;
}

.alloc-total-amount {
  font-size: 36rpx;
  font-weight: 700;
  color: #009688;
  font-family: sans-serif;
}

.alloc-hint {
  text-align: center;
  font-size: 22rpx;
  color: #bbb;
  padding: 10rpx 0 20rpx;
}

.alloc-list {
  max-height: 500rpx;
  overflow-y: auto;
}

.alloc-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1px solid #f5f5f5;
  gap: 12rpx;
}

.alloc-user-info {
  flex: 0 0 140rpx;
  min-width: 0;
}

.alloc-user-name {
  font-size: 24rpx;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alloc-input-row {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.alloc-label {
  font-size: 22rpx;
  color: #999;
  flex-shrink: 0;
}

.alloc-amount-input {
  flex: 1;
  height: 56rpx;
  font-size: 24rpx;
  padding: 0 12rpx;
}

.alloc-switch-row {
  display: flex;
  align-items: center;
  gap: 6rpx;
  flex-shrink: 0;
}

/* 列表中的分摊明细 */
.lay-table-row-wrap {
  border-bottom: 1px solid #f0f0f0;
}

.alloc-detail {
  padding: 8rpx 20rpx 16rpx 20rpx;
  border-top: 1px dashed #e8e8e8;
}

.alloc-detail-tag {
  margin-bottom: 8rpx;
}

.alloc-badge {
  display: inline-block;
  padding: 2rpx 12rpx;
  border-radius: 4rpx;
  font-size: 20rpx;
  font-weight: 500;
}

.alloc-badge.alloc-done {
  background: rgba(95, 184, 120, 0.12);
  color: #5fb878;
}

.alloc-badge.alloc-pending {
  background: rgba(255, 87, 34, 0.1);
  color: #ff5722;
}

.alloc-badge.alloc-partial {
  background: rgba(255, 184, 0, 0.12);
  color: #ffb800;
}

.alloc-detail-item {
  display: flex;
  align-items: center;
  padding: 4rpx 0;
  gap: 12rpx;
}

.alloc-detail-user {
  flex: 1;
  font-size: 22rpx;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alloc-detail-amount {
  font-size: 22rpx;
  font-weight: 600;
  color: #333;
  min-width: 120rpx;
  text-align: right;
}

.alloc-flag-true {
  font-size: 20rpx;
  color: #5fb878;
}

.alloc-flag-false {
  font-size: 20rpx;
  color: #ff5722;
}
</style>
