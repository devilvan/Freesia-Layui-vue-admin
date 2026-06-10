<template>
  <view class="page-wrap">
    <!-- 租户选择器 -->
    <view class="tenant-bar">
      <view class="filter-item">
        <text class="tenant-label">当前账本：</text>
        <picker mode="selector" :range="tenantNames" @change="onTenantChange" :value="currentTenantIndex">
          <view class="tenant-picker">
            <text>{{ currentTenantName }}</text>
            <text class="arrow">▼</text>
          </view>
        </picker>
      </view>
      <view class="filter-item">
        <!-- 多租户开关 -->
        <text class="tenant-label">统计所有账本：</text>
        <view style="padding: 8rpx 20rpx;margin-bottom: 0">
          <view class="lay-switch" :class="{ active: allTenantFlag }" @click="doChangeAllTenantFlag"></view>
        </view>
      </view>
    </view>

    <!-- 预算统计 -->
    <view class="lay-card" v-if="capacityList.length > 0">
      <view class="lay-card-header">预算概览</view>
      <view v-for="item in capacityList" :key="item.id" class="budget-item">
        <view class="flex-row justify-between mb-sm">
          <text class="ellipsis" style="flex:1">{{ item.name || item.tenantName || '--' }}</text>
          <text class="text-primary" :style="{ color: getBudgetColor(item.value) }">
            {{ formatMoney(item.outlay) }} / {{ formatMoney(item.budget) }}（{{ item.value || 0 }}%）
          </text>
        </view>
        <view class="progress-bar">
          <view class="progress-fill" :style="{
            width: getPercent(item.outlay, item.budget) + '%',
            backgroundColor: getProgressColor(item.outlay, item.budget)
          }"></view>
        </view>
        <text class="text-muted" style="font-size:22rpx">{{ item.durationFrom || '--' }} ~ {{
            item.durationTo || '--'
          }}
        </text>
        <view class="budget-actions">
          <text class="budget-action-btn" @click="openBudgetModal(item)">设置预算</text>
          <text class="budget-action-btn" @click="openReportModal(item)">历史数据</text>
        </view>
      </view>
    </view>

    <!-- 设置预算弹窗 -->
    <view class="lay-modal-mask" v-if="showBudgetModal" @click="closeBudgetModal">
      <view class="lay-modal-expense" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">设置预算</text>
          <text class="lay-modal-close" @click="closeBudgetModal">✕</text>
        </view>
        <view class="lay-modal-body">
          <view class="lay-form modal-form">
            <view class="lay-form-item">
              <text class="lay-form-label required">预算描述</text>
              <input class="lay-input modal-input" placeholder="请输入预算描述" v-model="budgetForm.budgetDesc"/>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label required">预算金额</text>
              <input class="lay-input modal-input" type="digit" placeholder="请输入预算金额"
                     v-model="budgetForm.outlay"/>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label required">预算日期类型</text>
              <picker mode="selector" :range="budgetTypeRange" @change="onBudgetTypeChange">
                <view class="lay-select modal-input">
                  <text :class="{ placeholder: !budgetForm.budgetTypeName }">{{
                      budgetForm.budgetTypeName || '请选择'
                    }}
                  </text>
                  <text class="arrow">▼</text>
                </view>
              </picker>
            </view>
            <view v-if="budgetForm.budgetType === 'CUSTOM'">
              <view class="lay-form-item">
                <text class="lay-form-label required">时间范围从</text>
                <picker mode="date" :value="budgetForm.durationFrom" @change="onBudgetFromChange">
                  <view class="lay-select modal-input">
                    <text :class="{ placeholder: !budgetForm.durationFrom }">{{
                        budgetForm.durationFrom || '请选择'
                      }}
                    </text>
                    <text class="arrow">▼</text>
                  </view>
                </picker>
              </view>
              <view class="lay-form-item">
                <text class="lay-form-label required">时间范围到</text>
                <picker mode="date" :value="budgetForm.durationTo" @change="onBudgetToChange">
                  <view class="lay-select modal-input">
                    <text :class="{ placeholder: !budgetForm.durationTo }">{{
                        budgetForm.durationTo || '请选择'
                      }}
                    </text>
                    <text class="arrow">▼</text>
                  </view>
                </picker>
              </view>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label">备注</text>
              <textarea class="lay-textarea modal-input" placeholder="请输入备注"
                        v-model="budgetForm.remark"></textarea>
            </view>
          </view>
        </view>
        <view class="lay-modal-footer">
          <button class="lay-btn lay-btn-sm" @click="closeBudgetModal">取消</button>
          <button class="lay-btn lay-btn-sm lay-btn-primary" @click="submitBudget">保存</button>
        </view>
      </view>
    </view>

    <!-- 历史数据弹窗 -->
    <view class="lay-modal-mask" v-if="showReportModal" @click="closeReportModal">
      <view class="lay-modal-expense" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">历史数据</text>
          <text class="lay-modal-close" @click="closeReportModal">✕</text>
        </view>
        <view class="lay-modal-body">
          <view class="filter-row" style="margin-bottom: 16rpx">
            <view class="filter-item">
              <text class="lay-form-label">开始时间</text>
              <picker mode="date" :value="reportQuery.billingTimeFrom" @change="onReportFromChange">
                <view class="lay-select">
                  <text :class="{ placeholder: !reportQuery.billingTimeFrom }">{{
                      reportQuery.billingTimeFrom || '请选择'
                    }}
                  </text>
                  <text class="arrow">▼</text>
                </view>
              </picker>
            </view>
            <view class="filter-item">
              <text class="lay-form-label">结束时间</text>
              <picker mode="date" :value="reportQuery.billingTimeTo" @change="onReportToChange">
                <view class="lay-select">
                  <text :class="{ placeholder: !reportQuery.billingTimeTo }">{{
                      reportQuery.billingTimeTo || '请选择'
                    }}
                  </text>
                  <text class="arrow">▼</text>
                </view>
              </picker>
            </view>
          </view>
          <view class="lay-btn-group gap-xs" style="margin-bottom: 16rpx">
            <button class="lay-btn lay-btn-sm lay-btn-primary" @click="loadReportData">查询</button>
            <button class="lay-btn lay-btn-sm" @click="resetReportQuery">重置</button>
          </view>
          <view v-if="reportLoading" class="lay-empty">
            <text class="empty-text">加载中...</text>
          </view>
          <view v-else-if="reportList.length === 0" class="lay-empty">
            <text class="empty-text">暂无数据</text>
          </view>
          <view v-else class="report-list">
            <view v-for="(r, i) in reportList" :key="i" class="report-item">
              <view class="flex-row justify-between">
                <text style="font-size:24rpx;color:#666">{{ r.billingTimeFrom || '--' }} ~ {{
                    r.billingTimeTo || '--'
                  }}
                </text>
                <text style="font-size:22rpx;color:#999">{{ getBudgetTypeName(r.budgetType) }}</text>
              </view>
              <view class="report-amount-grid">
                <view class="report-amount-cell">
                  <text class="report-amount-label">预算支出</text>
                  <text class="report-amount-value">{{ formatMoney(r.budgetAmount) }}</text>
                </view>
                <view class="report-amount-cell">
                  <text class="report-amount-label">支出</text>
                  <text class="report-amount-value" style="color:#ff5722">{{ formatMoney(r.outlay) }}</text>
                </view>
                <view class="report-amount-cell">
                  <text class="report-amount-label">收入</text>
                  <text class="report-amount-value" style="color:#5fb878">{{ formatMoney(r.incomeAmount) }}</text>
                </view>
                <view class="report-amount-cell">
                  <text class="report-amount-label">攒钱</text>
                  <text class="report-amount-value" style="color:#1e9fff">{{ formatMoney(r.saveAmount) }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        <view class="lay-modal-footer">
          <button class="lay-btn lay-btn-sm" @click="updateOneBudgetAmount" :disabled="reportList.length === 0">
            更新报表预算金额
          </button>
          <button class="lay-btn lay-btn-sm" @click="closeReportModal">关闭</button>
        </view>
      </view>
    </view>

    <!-- 消费排名（带柱状图可视化） -->
    <view class="lay-card">
      <view class="lay-card-header">
        <text>消费排名</text>
        <view class="lay-btn-group gap-xs">
          <button class="lay-btn lay-btn-sm" :class="{ 'lay-btn-primary': rankType === 'WEEK' }"
                  @click="loadRank('WEEK')">周
          </button>
          <button class="lay-btn lay-btn-sm" :class="{ 'lay-btn-primary': rankType === 'MONTH' }"
                  @click="loadRank('MONTH')">月
          </button>
        </view>
      </view>

      <view v-if="rankLoading" class="lay-empty">
        <text class="empty-text">加载中...</text>
      </view>
      <view v-else-if="rankList.length === 0" class="lay-empty">
        <text class="empty-icon">📊</text>
        <text class="empty-text">暂无排名数据</text>
      </view>
      <view v-else>
        <view v-for="(item, index) in rankList" :key="index" class="rank-item" @click="showRankDetail(index)">
          <!-- 背景柱状条 -->
          <view class="rank-bar-bg" :style="{
            width: getRankBarWidth(item) + '%',
            backgroundColor: getRankBarColor(index)
          }"></view>
          <view class="rank-item-content">
            <view class="rank-badge" :class="'rank-' + (index + 1)">{{ index + 1 }}</view>
            <text class="ellipsis" style="flex:1">{{ item.name }}</text>
            <view class="rank-amounts">
              <text class="text-expense" style="font-size:24rpx">支 {{ formatMoney(item.totalExpenses) }}</text>
              <text class="text-income" style="font-size:24rpx; margin-left:10rpx">收 {{
                  formatMoney(item.totalIncome)
                }}
              </text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 排名详情弹窗 -->
    <view class="lay-modal-mask" v-if="rankDetailVisible" @click="rankDetailVisible = false">
      <view class="lay-modal" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">{{ rankDetailName }}</text>
          <text class="lay-modal-close" @click="rankDetailVisible = false">✕</text>
        </view>
        <view class="lay-modal-body">
          <view class="rank-detail-section" v-if="expenseDetails.length > 0">
            <text class="rank-detail-label">支出明细</text>
            <view v-for="(d, i) in expenseDetails" :key="'e'+i" class="rank-detail-row">
              <view class="detail-dot" :style="{ backgroundColor: getDetailColor(i) }"></view>
              <text style="flex:1;font-size:26rpx">{{ d.name }}</text>
              <text style="font-size:26rpx;font-weight:bold;color:#ff5722">{{ formatMoney(d.value) }}</text>
              <text style="font-size:22rpx;color:#999;margin-left:8rpx;min-width:70rpx;text-align:right">
                {{ getDetailPercent(d, rankDetailExpenseTotal) }}%
              </text>
            </view>
            <view class="rank-detail-total">
              <text>支出合计</text>
              <text style="font-weight:bold;color:#ff5722">{{ formatMoney(rankDetailExpenseTotal) }}元</text>
            </view>
          </view>
          <view class="rank-detail-section" v-if="incomeDetails.length > 0" style="margin-top:20rpx">
            <text class="rank-detail-label">收入明细</text>
            <view v-for="(d, i) in incomeDetails" :key="'i'+i" class="rank-detail-row">
              <view class="detail-dot" :style="{ backgroundColor: getDetailColor(expenseDetails.length + i) }"></view>
              <text style="flex:1;font-size:26rpx">{{ d.name }}</text>
              <text style="font-size:26rpx;font-weight:bold;color:#5fb878">{{ formatMoney(d.value) }}</text>
              <text style="font-size:22rpx;color:#999;margin-left:8rpx;min-width:70rpx;text-align:right">
                {{ getDetailPercent(d, rankDetailIncomeTotal) }}%
              </text>
            </view>
            <view class="rank-detail-total">
              <text>收入合计</text>
              <text style="font-weight:bold;color:#5fb878">{{ formatMoney(rankDetailIncomeTotal) }}元</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 消费类型分布（饼图替代：横向进度条） -->
    <view class="lay-card" v-if="pieList.length > 0">
      <view class="lay-card-header">
        <text>消费类型分布</text>
      </view>
      <!-- 时间范围选择 -->
      <view class="pie-date-row">
        <view class="pie-date-item">
          <text class="pie-date-label">开始时间</text>
          <picker mode="date" :value="pieDateStart" @change="onPieStartChange">
            <view class="lay-select">
              <text :class="{ placeholder: !pieDateStart }">{{ pieDateStart || '请选择' }}</text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
        <view class="pie-date-item">
          <text class="pie-date-label">结束时间</text>
          <picker mode="date" :value="pieDateEnd" @change="onPieEndChange">
            <view class="lay-select">
              <text :class="{ placeholder: !pieDateEnd }">{{ pieDateEnd || '请选择' }}</text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
      </view>
      <!-- 简易饼图（CSS conic-gradient） -->
      <view class="pie-visual">
        <view class="pie-ring" :style="{ background: pieGradient }">
          <view class="pie-center">
            <text class="pie-total">{{ formatMoney(pieTotal) }}</text>
            <text class="text-muted" style="font-size:20rpx">总支出</text>
          </view>
        </view>
      </view>
      <!-- 图例列表 -->
      <view v-for="item in pieList" :key="item.name" class="pie-legend-row">
        <view class="flex-row align-center gap-sm" style="flex:1">
          <view class="legend-dot" :style="{ backgroundColor: getPieColor(item) }"></view>
          <image v-if="item.icon" :src="item.icon" mode="aspectFit"
                 style="width:32rpx;height:32rpx;border-radius:6rpx"/>
          <text class="ellipsis">{{ item.name }}</text>
        </view>
        <view class="flex-row align-center gap-sm">
          <text class="text-expense">{{ formatMoney(item.value) }}</text>
          <text class="text-muted" style="font-size:20rpx">{{ getPiePercent(item) }}%</text>
        </view>
        <!-- 迷你进度条 -->
        <view class="pie-mini-bar">
          <view class="pie-mini-fill" :style="{
            width: getPiePercent(item) + '%',
            backgroundColor: getPieColor(item)
          }"></view>
        </view>
      </view>
    </view>
    <view style="height: 40rpx"></view>
  </view>
</template>

<script>
import {ref, reactive, onMounted, computed} from 'vue'
import {findBudgetCapacity, findAccountBudget, saveUpdate as saveUpdateBudget} from '@/api/account/AccountBudget'
import {findRankByCostType, findCostTypeRatePie} from '@/api/account/Account'
import {findPageAccountReport, updateBudgetAmount} from '@/api/account/AccountReport'
import {findCacheSysDictValueList} from '@/api/system/Dict'
import {useUserStore} from '@/store/user'

export default {
  name: 'AccountsDashboard',
  setup() {
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

    const allTenantFlag = ref(true)
    const capacityList = ref([])
    const rankList = ref([])
    const rawRankYAxis = ref([])
    const rawRankSeries = ref([])
    const pieList = ref([])
    const pieTotal = ref(0)
    const pieDateStart = ref('')
    const pieDateEnd = ref('')
    const rankType = ref('WEEK')
    const rankLoading = ref(false)

    // 排名详情弹窗
    const rankDetailVisible = ref(false)
    const rankDetailIndex = ref(-1)
    const rankDetailName = ref('')
    const expenseDetails = computed(() => {
      if (rankDetailIndex.value < 0) return []
      return rawRankSeries.value
          .filter(s => s.stack !== 'INCOME')
          .map(s => ({name: s.name, value: s.value?.[rankDetailIndex.value] || 0}))
          .filter(d => d.value > 0)
          .sort((a, b) => b.value - a.value)
    })
    const incomeDetails = computed(() => {
      if (rankDetailIndex.value < 0) return []
      return rawRankSeries.value
          .filter(s => s.stack === 'INCOME')
          .map(s => ({name: s.name, value: s.value?.[rankDetailIndex.value] || 0}))
          .filter(d => d.value > 0)
          .sort((a, b) => b.value - a.value)
    })
    const rankDetailExpenseTotal = computed(() => expenseDetails.value.reduce((sum, d) => sum + d.value, 0))
    const rankDetailIncomeTotal = computed(() => incomeDetails.value.reduce((sum, d) => sum + d.value, 0))

    // 设置预算弹窗
    const showBudgetModal = ref(false)
    const budgetForm = reactive({
      id: '', budgetDesc: '', outlay: '', budgetType: '', budgetTypeName: '',
      durationFrom: '', durationTo: '', remark: ''
    })
    const budgetTypeOptions = ref([])
    const budgetTypeRange = computed(() => budgetTypeOptions.value.map(d => d.valueName || d.dictLabel || d.label || ''))

    // 历史数据弹窗
    const showReportModal = ref(false)
    const reportList = ref([])
    const reportLoading = ref(false)
    const reportBudgetId = ref('')
    const reportQuery = reactive({billingTimeFrom: '', billingTimeTo: ''})

    // 饼图配色
    const pieColors = ['#ff5722', '#ffb800', '#1e9fff', '#5fb878', '#009688', '#3963bc', '#998adb', '#ff9a9e', '#36b368', '#2d8cf0']

    const formatMoney = (val) => {
      if (val == null) return '0.00';
      return Number(val).toFixed(2)
    }
    const getPercent = (outlay, budget) => {
      if (!budget || budget === 0) return 0
      return Math.min(100, Math.round((outlay / budget) * 100))
    }
    const getProgressColor = (outlay, budget) => {
      const pct = getPercent(outlay, budget)
      if (pct >= 90) return '#ff5722';
      if (pct >= 70) return '#ffb800';
      return '#5fb878'
    }

    const getPieColor = (item) => {
      const idx = pieList.value.indexOf(item)
      return pieColors[idx % pieColors.length]
    }
    const getPiePercent = (item) => {
      if (!pieTotal.value || pieTotal.value === 0) return 0
      return Math.round((item.value / pieTotal.value) * 100)
    }
    const pieGradient = computed(() => {
      if (pieList.value.length === 0) return 'conic-gradient(#f0f0f0 0deg, #f0f0f0 360deg)'
      let cumulative = 0
      const stops = pieList.value.map((item, idx) => {
        const pct = getPiePercent(item)
        const color = pieColors[idx % pieColors.length]
        const start = cumulative
        cumulative += pct * 3.6
        return `${color} ${start}deg ${cumulative}deg`
      })
      return `conic-gradient(${stops.join(', ')})`
    })

    const getRankBarWidth = (item) => {
      const maxVal = Math.max(...rankList.value.map(r => Math.max(r.totalExpenses || 0, r.totalIncome || 0)), 1)
      return Math.round((Math.max(item.totalExpenses || 0, item.totalIncome || 0) / maxVal) * 100)
    }
    const getRankBarColor = (idx) => {
      const colors = ['rgba(255, 87, 34, 0.15)', 'rgba(255, 184, 0, 0.12)', 'rgba(30, 159, 255, 0.12)',
        'rgba(95, 184, 120, 0.12)', 'rgba(0, 150, 136, 0.12)']
      return colors[idx] || 'rgba(0, 0, 0, 0.05)'
    }

    const showRankDetail = (index) => {
      rankDetailIndex.value = index
      rankDetailName.value = rankList.value[index]?.name || ''
      rankDetailVisible.value = true
    }
    const getDetailColor = (idx) => {
      return ['#ff5722', '#ffb800', '#1e9fff', '#5fb878', '#009688', '#3963bc', '#998adb'][idx % 7]
    }
    const getDetailPercent = (d, total) => {
      if (!total) return '0.00'
      return ((d.value / total) * 100).toFixed(2)
    }

    const loadBudget = async () => {
      try {
        const res = await findBudgetCapacity({allTenantFlag: allTenantFlag.value})
        if (res.code === 200 && res.data) {
          capacityList.value = res.data || []
        }
      } catch (e) {
        console.error('加载预算失败', e)
      }
    }

    const getBudgetColor = (val) => {
      if (!val || val <= 50) return '#36b368'
      if (val <= 80) return '#FFB800'
      if (val <= 100) return '#FF9B2D'
      return '#FF5722'
    }

    const getBudgetTypeName = (value) => {
      if (!value) return '--'
      const item = budgetTypeOptions.value.find(t => (t.value || t.dictValue) === value)
      return item ? (item.valueName || item.dictLabel || item.label || value) : value
    }

    // ===== 设置预算弹窗 =====
    const loadBudgetTypes = async () => {
      try {
        const res = await findCacheSysDictValueList('ACCOUNT_BUDGET_DURATION_TYPE')
        if (res.code === 200 && res.data) {
          budgetTypeOptions.value = res.data
        }
      } catch (e) {
        console.error('加载预算类型失败', e)
      }
    }

    const openBudgetModal = async (item) => {
      try {
        const res = await findAccountBudget({id: item.id})
        if (res.code === 200 && res.data) {
          const d = res.data
          const bt = budgetTypeOptions.value.find(t => (t.value || t.dictValue) === d.budgetType)
          Object.assign(budgetForm, {
            id: d.id || '',
            recVer: d.recVer || '',
            logicDel: d.logicDel || false,
            budgetDesc: d.budgetDesc || '',
            outlay: d.outlay != null ? String(d.outlay) : '',
            budgetType: d.budgetType || '',
            budgetTypeName: bt ? (bt.valueName || bt.dictLabel || bt.label || '') : '',
            durationFrom: d.durationFrom || '',
            durationTo: d.durationTo || '',
            remark: d.remark || ''
          })
        }
      } catch (e) {
        console.error('加载预算详情失败', e)
      }
      showBudgetModal.value = true
    }

    const closeBudgetModal = () => {
      showBudgetModal.value = false
      Object.assign(budgetForm, {
        id: '',
        recVer: '',
        logicDel: false,
        budgetDesc: '',
        outlay: '',
        budgetType: '',
        budgetTypeName: '',
        durationFrom: '',
        durationTo: '',
        remark: ''
      })
    }

    const onBudgetTypeChange = (e) => {
      const idx = e.detail.value
      const item = budgetTypeOptions.value[idx]
      budgetForm.budgetType = item?.value || item?.dictValue || ''
      budgetForm.budgetTypeName = item?.valueName || item?.dictLabel || item?.label || ''
      if (budgetForm.budgetType !== 'CUSTOM') {
        budgetForm.durationFrom = ''
        budgetForm.durationTo = ''
      }
    }

    const onBudgetFromChange = (e) => {
      budgetForm.durationFrom = e.detail.value
    }
    const onBudgetToChange = (e) => {
      budgetForm.durationTo = e.detail.value
    }

    const submitBudget = async () => {
      if (!budgetForm.budgetDesc) {
        uni.showToast({title: '请输入预算描述', icon: 'none'});
        return
      }
      if (!budgetForm.outlay || Number(budgetForm.outlay) <= 0) {
        uni.showToast({title: '请输入有效预算金额', icon: 'none'});
        return
      }
      if (!budgetForm.budgetType) {
        uni.showToast({title: '请选择预算日期类型', icon: 'none'});
        return
      }
      if (budgetForm.budgetType === 'CUSTOM' && (!budgetForm.durationFrom || !budgetForm.durationTo)) {
        uni.showToast({title: '自定义类型请选择时间范围', icon: 'none'});
        return
      }
      try {
        const params = {
          id: budgetForm.id || undefined,
          recVer: budgetForm.recVer || undefined,
          logicDel: budgetForm.logicDel || false,
          budgetDesc: budgetForm.budgetDesc,
          outlay: Number(budgetForm.outlay),
          budgetType: budgetForm.budgetType,
          durationFrom: budgetForm.durationFrom || undefined,
          durationTo: budgetForm.durationTo || undefined,
          remark: budgetForm.remark || undefined
        }
        const res = await saveUpdateBudget(params)
        if (res.code === 200) {
          uni.showToast({title: '保存成功', icon: 'success'})
          closeBudgetModal()
          loadBudget()
        }
      } catch (e) {
        uni.showToast({title: '保存失败', icon: 'none'})
      }
    }

    // ===== 历史数据弹窗 =====
    const openReportModal = (item) => {
      reportBudgetId.value = item.id
      reportQuery.billingTimeFrom = ''
      reportQuery.billingTimeTo = ''
      reportList.value = []
      showReportModal.value = true
      loadReportData()
    }

    const closeReportModal = () => {
      showReportModal.value = false
    }

    const onReportFromChange = (e) => {
      reportQuery.billingTimeFrom = e.detail.value
    }
    const onReportToChange = (e) => {
      reportQuery.billingTimeTo = e.detail.value
    }

    const resetReportQuery = () => {
      reportQuery.billingTimeFrom = ''
      reportQuery.billingTimeTo = ''
      loadReportData()
    }

    const loadReportData = async () => {
      reportLoading.value = true
      try {
        const params = {budgetId: reportBudgetId.value}
        if (reportQuery.billingTimeFrom && reportQuery.billingTimeTo) {
          params.billingTimeRange = [reportQuery.billingTimeFrom + ' 00:00:00', reportQuery.billingTimeTo + ' 23:59:59']
        }
        const res = await findPageAccountReport(params, {current: 1, limit: 50})
        if (res.code === 200) {
          reportList.value = res.rows || res.records || []
        }
      } catch (e) {
        console.error('加载历史数据失败', e)
      } finally {
        reportLoading.value = false
      }
    }

    const updateOneBudgetAmount = async () => {
      if (!reportBudgetId.value) return
      try {
        const res = await updateBudgetAmount({budgetId: reportBudgetId.value})
        if (res.code === 200) {
          uni.showToast({title: '更新成功', icon: 'success'})
          loadReportData()
          loadBudget()
        }
      } catch (e) {
        uni.showToast({title: '更新失败', icon: 'none'})
      }
    }

    const onPieStartChange = (e) => {
      pieDateStart.value = e.detail.value
      loadPie()
    }
    const onPieEndChange = (e) => {
      pieDateEnd.value = e.detail.value
      loadPie()
    }

    const loadRank = async (type) => {
      rankType.value = type;
      rankLoading.value = true
      try {
        const res = await findRankByCostType({dateScope: type, allTenantFlag: allTenantFlag.value})
        if (res.code === 200 && res.data) {
          const yAxis = res.data.yAxis || []
          const series = res.data.series || []
          rawRankYAxis.value = yAxis
          rawRankSeries.value = series
          rankList.value = yAxis.map((name, i) => {
            const item = {name, totalExpenses: 0, totalIncome: 0}
            series.forEach(s => {
              if (s.name && s.value) {
                if (s.stack === 'EXPENSES') item.totalExpenses += (s.value[i] || 0)
                else item.totalIncome += (s.value[i] || 0)
              }
            })
            return item
          })
        }
      } catch (e) {
        console.error('加载排名失败', e)
      } finally {
        rankLoading.value = false
      }
    }

    const loadPie = async () => {
      try {
        const params = {allTenantFlag: allTenantFlag.value}
        if (pieDateStart.value && pieDateEnd.value) {
          params.paymentTimeRange = [pieDateStart.value + ' 00:00:00', pieDateEnd.value + ' 23:59:59'].join(',')
        }
        const res = await findCostTypeRatePie(params)
        if (res.code === 200) {
          // 后端返回: { totalAmount: number, legends: string[], series: [{ name, value: string }] }
          // value 是字符串，必须显式转 Number 否则 reduce 会做字符串拼接导致 NaN
          const data = res.data
          const rawSeries = data.series || []
          pieList.value = rawSeries
              .map(item => ({name: item.name, value: Number(item.value) || 0}))
              .filter(item => item.value > 0)
              .sort((a, b) => b.value - a.value)
          pieTotal.value = data.totalAmount || pieList.value.reduce((sum, item) => sum + item.value, 0)
        }
      } catch (e) {
        console.error('加载分布失败', e)
      }
    }

    const onTenantChange = (e) => {
      const idx = e.detail.value
      const tenant = tenantList.value[idx]
      if (tenant && tenant.id) {
        userStore.setCurrentTenant(tenant.id)
        loadBudget();
        loadRank(rankType.value);
        loadPie()
      }
    }

    const doChangeAllTenantFlag = () => {
      allTenantFlag.value = !allTenantFlag.value
      loadBudget();
      loadRank(rankType.value);
      loadPie()
    }

    const initPieDateRange = () => {
      const now = new Date()
      const pad = n => String(n).padStart(2, '0')
      pieDateEnd.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`
      const start = new Date(now.getTime() - 7 * 24 * 3600 * 1000)
      pieDateStart.value = `${start.getFullYear()}-${pad(start.getMonth() + 1)}-${pad(start.getDate())}`
    }

    onMounted(() => {
      loadBudgetTypes();
      loadBudget();
      loadRank('WEEK');
      initPieDateRange();
      loadPie()
    })

    return {
      allTenantFlag, capacityList, rankList, pieList, pieTotal,
      rankType, rankLoading, pieGradient,
      tenantList, tenantNames, currentTenantName, currentTenantIndex,
      onTenantChange,
      formatMoney, getPercent, getProgressColor,
      getPieColor, getPiePercent,
      getRankBarWidth, getRankBarColor,
      loadRank, doChangeAllTenantFlag,
      rankDetailVisible, rankDetailName,
      expenseDetails, incomeDetails,
      rankDetailExpenseTotal, rankDetailIncomeTotal,
      showRankDetail, getDetailColor, getDetailPercent,
      pieDateStart, pieDateEnd,
      onPieStartChange, onPieEndChange,
      showBudgetModal, budgetForm, budgetTypeRange,
      openBudgetModal, closeBudgetModal, onBudgetTypeChange,
      onBudgetFromChange, onBudgetToChange, submitBudget, getBudgetColor, getBudgetTypeName,
      showReportModal, reportList, reportLoading, reportQuery,
      openReportModal, closeReportModal, onReportFromChange, onReportToChange,
      resetReportQuery, loadReportData, updateOneBudgetAmount
    }
  }
}
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
  padding-bottom: 40rpx;
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

.budget-item {
  margin-bottom: 24rpx;
  padding-bottom: 20rpx;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
}

.progress-bar {
  height: 16rpx;
  background: #f0f0f0;
  border-radius: 8rpx;
  margin-bottom: 8rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 8rpx;
  transition: width 0.3s;
  min-width: 4rpx;
}

/* 预算卡片操作按钮 */
.budget-actions {
  display: flex;
  margin-top: 16rpx;
  padding-top: 12rpx;
  border-top: 1px solid #f0f0f0;
}

.budget-action-btn {
  flex: 1;
  text-align: center;
  font-size: 24rpx;
  color: #999;
  padding: 8rpx 0;
}

.budget-action-btn:active {
  color: #009688;
  background: rgba(0, 150, 136, 0.05);
  border-radius: 4rpx;
}

/* 历史数据列表 */
.report-list {
  max-height: 600rpx;
  overflow-y: auto;
}

.report-item {
  padding: 16rpx 0;
  border-bottom: 1px solid #f5f5f5;
}

.report-item:last-child {
  border-bottom: none;
}

/* 历史数据金额网格 */
.report-amount-grid {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.report-amount-cell {
  width: 50%;
  display: flex;
  align-items: center;
  padding: 4rpx 0;
  box-sizing: border-box;
}

.report-amount-label {
  font-size: 22rpx;
  color: #999;
  width: 100rpx;
  flex-shrink: 0;
}

.report-amount-value {
  font-size: 24rpx;
  font-weight: 600;
  white-space: nowrap;
}

/* 弹窗容器 - 补齐缺失样式 */
.lay-modal-expense {
  width: 88%;
  max-height: 85vh;
  background: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 饼图 */
.pie-visual {
  display: flex;
  justify-content: center;
  padding: 30rpx 0;
}

.pie-ring {
  width: 260rpx;
  height: 260rpx;
  border-radius: 50%;
  background: conic-gradient(#f0f0f0 0deg, #f0f0f0 360deg);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pie-center {
  position: absolute;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  justify-content: center;
}

.pie-total {
  font-size: 28rpx;
  font-weight: 700;
  color: #333;
}

.pie-legend-row {
  padding: 14rpx 0;
  border-bottom: 1px solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }
}

.legend-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.pie-mini-bar {
  height: 8rpx;
  background: #f5f5f5;
  border-radius: 4rpx;
  margin-top: 8rpx;
  overflow: hidden;
}

.pie-mini-fill {
  height: 100%;
  border-radius: 4rpx;
  transition: width 0.3s;
  min-width: 4rpx;
}

/* 排名 */
.rank-item {
  position: relative;
  overflow: hidden;
  margin-bottom: 4rpx;
  border-radius: 4rpx;
}

.rank-bar-bg {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  border-radius: 4rpx;
  transition: width 0.3s;
  z-index: 0;
}

.rank-item-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 12rpx;
}

.rank-amounts {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.rank-badge {
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
  font-weight: bold;
  color: #fff;
  background: #999;
  flex-shrink: 0;

  &.rank-1 {
    background: #ff5722;
  }

  &.rank-2 {
    background: #ffb800;
  }

  &.rank-3 {
    background: #1e9fff;
  }
}

/* 饼图时间选择 - 单行 */
.pie-date-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.pie-date-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.pie-date-label {
  font-size: 24rpx;
  color: #666;
  flex-shrink: 0;
  white-space: nowrap;
}

/* 排名详情弹窗 */
.rank-detail-section {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12rpx;
}

.rank-detail-label {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.rank-detail-row {
  display: flex;
  align-items: center;
  padding: 12rpx 0;
  gap: 10rpx;
}

.detail-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.rank-detail-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14rpx 0 6rpx;
  font-size: 26rpx;
}

.filter-item { flex: 1; }
</style>
