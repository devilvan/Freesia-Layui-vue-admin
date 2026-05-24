<template>
  <view class="page-wrap">
    <!-- 统计所有账本开关 -->
    <view class="lay-card">
      <view class="flex-row align-center justify-between">
        <text class="lay-form-label" style="margin-bottom: 0">统计所有账本</text>
        <view class="lay-switch-wrap">
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
          <text class="text-primary">{{ formatMoney(item.outlay) }} / {{ formatMoney(item.budget) }}</text>
        </view>
        <view class="progress-bar">
          <view class="progress-fill" :style="{
            width: getPercent(item.outlay, item.budget) + '%',
            backgroundColor: getProgressColor(item.outlay, item.budget)
          }"></view>
        </view>
        <text class="text-muted" style="font-size:22rpx">{{ item.durationFrom || '--' }} ~ {{ item.durationTo || '--' }}</text>
      </view>
    </view>

    <!-- 消费类型分布（饼图替代：横向进度条） -->
    <view class="lay-card" v-if="pieList.length > 0">
      <view class="lay-card-header">消费类型分布</view>
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
          <image v-if="item.icon" :src="item.icon" mode="aspectFit" style="width:32rpx;height:32rpx;border-radius:6rpx"/>
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

    <!-- 消费排名（带柱状图可视化） -->
    <view class="lay-card">
      <view class="lay-card-header">
        <text>消费排名</text>
        <view class="lay-btn-group gap-xs">
          <button class="lay-btn lay-btn-sm" :class="{ 'lay-btn-primary': rankType === 'WEEK' }"
                  @click="loadRank('WEEK')">周</button>
          <button class="lay-btn lay-btn-sm" :class="{ 'lay-btn-primary': rankType === 'MONTH' }"
                  @click="loadRank('MONTH')">月</button>
        </view>
      </view>

      <view v-if="rankLoading" class="lay-empty"><text class="empty-text">加载中...</text></view>
      <view v-else-if="rankList.length === 0" class="lay-empty">
        <text class="empty-icon">📊</text>
        <text class="empty-text">暂无排名数据</text>
      </view>
      <view v-else>
        <view v-for="(item, index) in rankList" :key="index" class="rank-item">
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
              <text class="text-income" style="font-size:24rpx; margin-left:10rpx">收 {{ formatMoney(item.totalIncome) }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view style="height: 40rpx"></view>
  </view>
</template>

<script>
import {ref, onMounted, computed} from 'vue'
import {findBudgetCapacity} from '@/api/account/AccountBudget'
import {findRankByCostType, findCostTypeRatePie} from '@/api/account/Account'

export default {
  name: 'AccountsDashboard',
  setup() {
    const allTenantFlag = ref(true)
    const capacityList = ref([])
    const rankList = ref([])
    const pieList = ref([])
    const pieTotal = ref(0)
    const rankType = ref('WEEK')
    const rankLoading = ref(false)

    // 饼图配色
    const pieColors = ['#ff5722', '#ffb800', '#1e9fff', '#5fb878', '#009688', '#3963bc', '#998adb', '#ff9a9e', '#36b368', '#2d8cf0']

    const formatMoney = (val) => {
      if (val == null) return '0.00'; return Number(val).toFixed(2)
    }
    const getPercent = (outlay, budget) => {
      if (!budget || budget === 0) return 0
      return Math.min(100, Math.round((outlay / budget) * 100))
    }
    const getProgressColor = (outlay, budget) => {
      const pct = getPercent(outlay, budget)
      if (pct >= 90) return '#ff5722'; if (pct >= 70) return '#ffb800'; return '#5fb878'
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

    const loadBudget = async () => {
      try {
        const res = await findBudgetCapacity({allTenantFlag: allTenantFlag.value})
        if (res.code === 200 && res.data) { capacityList.value = res.data || [] }
      } catch (e) { console.error('加载预算失败', e) }
    }

    const loadRank = async (type) => {
      rankType.value = type; rankLoading.value = true
      try {
        const res = await findRankByCostType({dateScope: type, allTenantFlag: allTenantFlag.value})
        if (res.code === 200 && res.data) {
          const yAxis = res.data.yAxis || []
          const series = res.data.series || []
          rankList.value = yAxis.map((name, i) => {
            const item = {name, totalExpenses: 0, totalIncome: 0}
            series.forEach(s => {
              if (s.name && s.value) {
                if (s.stack === 'EXPENSES') item.totalExpenses += (s.value[i] || 0)
                else item.totalIncome += (s.value[i] || 0)
              }
            })
            return item
          }).sort((a, b) => (b.totalExpenses + b.totalIncome) - (a.totalExpenses + a.totalIncome))
        }
      } catch (e) { console.error('加载排名失败', e) }
      finally { rankLoading.value = false }
    }

    const loadPie = async () => {
      try {
        const res = await findCostTypeRatePie({allTenantFlag: allTenantFlag.value})
        if (res.code === 200) {
          // 接口返回格式可能是 {series: [{name, value}]} 或直接是数组
          const data = res.data || []
          const list = Array.isArray(data) ? data : (data.series || [])
          pieList.value = list.filter(item => item.value > 0).sort((a, b) => (b.value || 0) - (a.value || 0))
          pieTotal.value = pieList.value.reduce((sum, item) => sum + (item.value || 0), 0)
        }
      } catch (e) { console.error('加载分布失败', e) }
    }

    const doChangeAllTenantFlag = () => {
      allTenantFlag.value = !allTenantFlag.value
      loadBudget(); loadRank(rankType.value); loadPie()
    }

    onMounted(() => { loadBudget(); loadRank('WEEK'); loadPie() })

    return {
      allTenantFlag, capacityList, rankList, pieList, pieTotal,
      rankType, rankLoading, pieGradient,
      formatMoney, getPercent, getProgressColor,
      getPieColor, getPiePercent,
      getRankBarWidth, getRankBarColor,
      loadRank, doChangeAllTenantFlag
    }
  }
}
</script>

<style lang="scss" scoped>
.page-wrap { min-height: 100vh; padding-bottom: 40rpx; }

.budget-item {
  margin-bottom: 24rpx; padding-bottom: 20rpx;
  border-bottom: 1px solid #f0f0f0;
  &:last-child { margin-bottom: 0; padding-bottom: 0; border-bottom: none; }
}

.progress-bar {
  height: 16rpx; background: #f0f0f0;
  border-radius: 8rpx; margin-bottom: 8rpx; overflow: hidden;
}
.progress-fill { height: 100%; border-radius: 8rpx; transition: width 0.3s; min-width: 4rpx; }

/* 饼图 */
.pie-visual {
  display: flex; justify-content: center; padding: 30rpx 0;
}
.pie-ring {
  width: 260rpx; height: 260rpx; border-radius: 50%;
  background: conic-gradient(#f0f0f0 0deg, #f0f0f0 360deg);
  position: relative; overflow: hidden;
  display: flex; align-items: center; justify-content: center;
}
.pie-center {
  position: absolute; z-index: 2;
  display: flex; flex-direction: column; align-items: center;
  background: #fff; width: 160rpx; height: 160rpx;
  border-radius: 50%; justify-content: center;
}
.pie-total { font-size: 28rpx; font-weight: 700; color: #333; }

.pie-legend-row {
  padding: 14rpx 0; border-bottom: 1px solid #f5f5f5;
  &:last-child { border-bottom: none; }
}
.legend-dot { width: 16rpx; height: 16rpx; border-radius: 50%; flex-shrink: 0; }
.pie-mini-bar {
  height: 8rpx; background: #f5f5f5; border-radius: 4rpx;
  margin-top: 8rpx; overflow: hidden;
}
.pie-mini-fill { height: 100%; border-radius: 4rpx; transition: width 0.3s; min-width: 4rpx; }

/* 排名 */
.rank-item {
  position: relative; overflow: hidden;
  margin-bottom: 4rpx; border-radius: 4rpx;
}
.rank-bar-bg {
  position: absolute; top: 0; left: 0; bottom: 0;
  border-radius: 4rpx; transition: width 0.3s; z-index: 0;
}
.rank-item-content {
  position: relative; z-index: 1;
  display: flex; align-items: center; gap: 16rpx;
  padding: 16rpx 12rpx;
}
.rank-amounts { display: flex; flex-direction: column; align-items: flex-end; }
.rank-badge {
  width: 44rpx; height: 44rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 22rpx; font-weight: bold; color: #fff; background: #999;
  flex-shrink: 0;
  &.rank-1 { background: #ff5722; }
  &.rank-2 { background: #ffb800; }
  &.rank-3 { background: #1e9fff; }
}
</style>
