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
        <text class="tenant-label" >统计所有账本：</text>
        <view style="padding: 8rpx 20rpx;margin-bottom: 0">
          <view class="lay-switch" :class="{ active: allTenantFlag }" @click="toggleAllTenant"></view>
        </view>
      </view>
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
          <text class="lay-form-label">开始时间</text>
          <picker mode="date" :value="searchQuery.startDateDisplay" @change="onStartDateChange">
            <view class="lay-select">
              <text :class="{ placeholder: !searchQuery.startDateDisplay }">{{ searchQuery.startDateDisplay || '请选择' }}</text>
              <text class="arrow">▼</text>
            </view>
          </picker>
        </view>
        <view class="filter-item">
          <text class="lay-form-label">结束时间</text>
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
                  <view class="icon-pick-left">
                    <view class="icon-pick-box" @click="openIconGrid">
                      <image v-if="accountCostVo.icon" :src="accountCostVo.icon" mode="aspectFit" class="icon-pick-img"/>
                      <text v-else class="icon-pick-empty">?</text>
                    </view>
                    <text class="text-muted" style="font-size:24rpx">点击图标选择</text>
                  </view>
                  <view class="icon-pick-actions">
                    <button class="lay-btn lay-btn-sm lay-btn-normal" @click="openAddGroupModal">新增分组</button>
                    <button class="lay-btn lay-btn-sm lay-btn-primary" @click="openAddIconModal">新增图标</button>
                  </view>
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
          <button class="lay-btn lay-btn-sm" @click="closeModal">取消</button>
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
          <view v-else>
            <view v-for="group in iconGridList" :key="group.name" class="icon-group">
              <!-- 分组标题（可折叠） -->
              <view class="icon-group-header" @click="toggleGroup(group.name)">
                <text class="icon-group-arrow" :class="{ collapsed: collapsedGroups[group.name] }">▶</text>
                <text class="icon-group-title">{{ group.name }}</text>
                <text class="icon-group-count">{{ (group.children || []).length }}个</text>
              </view>
              <!-- 分组图标网格 -->
              <view class="icon-grid" v-show="!collapsedGroups[group.name]">
                <view v-for="item in group.children" :key="item.name"
                      class="icon-grid-item" :class="{ selected: accountCostVo.costType === item.name }"
                      @click="pickIcon(item)">
                  <image v-if="item.url" :src="item.url" mode="aspectFit" class="icon-grid-img"/>
                  <view v-else class="icon-grid-placeholder"></view>
                  <text class="icon-grid-name ellipsis">{{ item.name }}</text>
                </view>
              </view>
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

    <!-- 新增图标弹窗 -->
    <view class="lay-modal-mask" v-if="showAddIconModal" @click="hideAddIconModal">
      <view class="lay-modal" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">新增图标</text>
          <text class="lay-modal-close" @click="hideAddIconModal">✕</text>
        </view>
        <view class="lay-modal-body" style="max-height:600rpx;overflow-y:auto">
          <view class="lay-form modal-form">
            <!-- 分组选择 -->
            <view class="lay-form-item">
              <text class="lay-form-label required">所属分组</text>
              <picker mode="selector" :range="groupingList" range-key="label" @change="onAddIconGroupChange">
                <view class="lay-select">
                  <text :class="{ placeholder: !addIconForm.parentId }">{{ findGroupLabel(addIconForm.parentId) || '请选择分组' }}</text>
                  <text class="arrow">▼</text>
                </view>
              </picker>
            </view>
            <!-- 图标名称 -->
            <view class="lay-form-item">
              <text class="lay-form-label required">图标名称</text>
              <input class="lay-input modal-input" placeholder="请输入图标名称" v-model="addIconForm.name"/>
            </view>
            <!-- 图标选择 -->
            <view class="lay-form-item">
              <text class="lay-form-label required">选择图标</text>
              <view class="icon-pick-row">
                <view class="icon-pick-box" @click="showAddIconPicker = !showAddIconPicker">
                  <image v-if="addIconForm.iconUrl" :src="addIconForm.iconUrl" mode="aspectFit" class="icon-pick-img"/>
                  <text v-else class="icon-pick-empty">?</text>
                </view>
                <text v-if="addIconForm.originName" class="text-muted" style="font-size:24rpx">{{ addIconForm.originName }}</text>
                <text v-else class="text-muted" style="font-size:24rpx">点击图标选择</text>
              </view>
              <!-- 内嵌图标选择器 -->
              <view v-if="showAddIconPicker" class="add-icon-picker-wrap">
                <view v-for="(icons, key) in addIconPickerList" :key="key" class="add-icon-group">
                  <view class="add-icon-group-header" @click="toggleAddIconGroup(key)">
                    <text class="icon-group-arrow" :class="{ collapsed: addIconCollapsed[key] }">▶</text>
                    <text class="icon-group-title">{{ key }}</text>
                  </view>
                  <view class="icon-grid" v-show="!addIconCollapsed[key]">
                    <view v-for="item in icons" :key="item.id"
                          class="icon-grid-item icon-grid-item-square" :class="{ selected: addIconForm.iconId === item.id }"
                          @click="pickAddIcon(item)">
                      <image v-if="item.url" :src="item.url" mode="aspectFit" class="icon-grid-img"/>
                      <view v-else class="icon-grid-placeholder"></view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
            <!-- 排序 -->
            <view class="lay-form-item">
              <text class="lay-form-label">排序</text>
              <input class="lay-input modal-input" type="number" placeholder="排序号" v-model="addIconForm.orderNum"/>
            </view>
            <!-- 备注 -->
            <view class="lay-form-item">
              <text class="lay-form-label">备注</text>
              <textarea class="lay-textarea modal-input" placeholder="请输入备注" v-model="addIconForm.remark"></textarea>
            </view>
          </view>
        </view>
        <view class="lay-modal-footer">
          <button class="lay-btn lay-btn-sm" @click="hideAddIconModal">取消</button>
          <button class="lay-btn lay-btn-sm lay-btn-primary" :disabled="addIconLoading" @click="submitAddIcon">保存</button>
        </view>
      </view>
    </view>

    <!-- 新增图标分组弹窗 -->
    <view class="lay-modal-mask" v-if="showAddGroupModal" @click="hideAddGroupModal">
      <view class="lay-modal" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title">新增图标分组</text>
          <text class="lay-modal-close" @click="hideAddGroupModal">✕</text>
        </view>
        <view class="lay-modal-body">
          <view class="lay-form modal-form">
            <view class="lay-form-item">
              <text class="lay-form-label required">分区名称</text>
              <input class="lay-input modal-input" placeholder="请输入分区名称" v-model="addGroupForm.name"/>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label">排序</text>
              <input class="lay-input modal-input" type="number" placeholder="排序号" v-model="addGroupForm.orderNum"/>
            </view>
            <view class="lay-form-item">
              <text class="lay-form-label">备注</text>
              <textarea class="lay-textarea modal-input" placeholder="请输入备注" v-model="addGroupForm.remark"></textarea>
            </view>
          </view>
        </view>
        <view class="lay-modal-footer">
          <button class="lay-btn lay-btn-sm" @click="hideAddGroupModal">取消</button>
          <button class="lay-btn lay-btn-sm lay-btn-primary" :disabled="addGroupLoading" @click="submitAddGroup">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {ref, reactive, onMounted, computed} from 'vue'
import {findPageAccountCost, findAccountCost, saveUpdate, deleteAccountCost, findListSelectCostType, findCacheCostType, findListSysUserById, findListAllocByCostId} from '@/api/account/Account'
import {PaymentSign} from '@/types/account/Account'
import {findPageSysUserWithoutDataScope} from '@/api/system/User'
import {useUserStore} from '@/store/user'
import {findCustomIconTemplateDetail, findGrouping, saveUpdateIconTemplateDetail, findMaxOrderNum} from '@/api/common/icon/template/IconTemplateDetail'
import {findSelectCommonIconHeader} from '@/api/common/icon/template/IconTemplateHeader'
import {findCommonIconPicker} from '@/api/common/icon/Icon'

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
    const iconGridList = ref([])        // 树形分组数据
    const iconGridLoading = ref(false)
    const collapsedGroups = ref({})     // 分组折叠状态：{ groupName: true/false }
    const selectedIconName = ref('')    // 当前选中的图标名称

    // 新增图标（默认模板）
    const defaultHeaderId = ref('')     // 默认图标模板头ID
    const showAddIconModal = ref(false)
    const addIconForm = reactive({
      parentId: '',                     // 分组ID
      name: '',                         // 图标名称
      iconId: '',                       // 选中的通用图标ID
      iconUrl: '',                      // 选中的图标URL（展示用）
      originName: '',                   // 图标原始名称
      orderNum: 0,
      remark: ''
    })
    const groupingList = ref([])        // 分组下拉数据
    const addIconPickerList = ref({})   // 通用图标选择器数据：{ partition: [...] }
    const addIconPickerOpenKeys = ref([]) // 图标选择器折叠keys
    const addIconCollapsed = ref({})    // 图标选择器分组折叠状态
    const addIconLoading = ref(false)   // 新增图标提交loading

    // 新增图标分组
    const showAddGroupModal = ref(false)
    const addGroupForm = reactive({
      name: '',
      orderNum: 0,
      remark: ''
    })
    const addGroupLoading = ref(false)

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
        const now = new Date()
        const pad = (n) => String(n).padStart(2, '0')
        const todayStr = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`
        Object.assign(accountCostVo, {
          id: '', costDesc: '', outlay: '', costType: '', icon: '',
          paymentSign: 'EXPENSES',
          paymentTimeStr: todayStr,
          paymentTime: formatDateTime(now),
          remark: '',
          accountCostUserIdList: [], accountCostUserNameList: [], accountCostUserAllocVoList: []
        })
        selectedUserTags.value = []
        showSuggestion.value = false
      } else if (op === 'EDIT') {
        // 先以列表数据快速填充，再异步加载完整记录（含分摊信息）
        Object.assign(accountCostVo, {
          id: row.id || '', costDesc: row.costDesc || '',
          outlay: row.outlay != null ? String(row.outlay) : '',
          costType: row.costType || '', icon: row.icon || '',
          paymentSign: row.paymentSign || 'EXPENSES',
          paymentTimeStr: formatDate(row.paymentTime), paymentTime: row.paymentTime,
          remark: row.remark || '',
          accountCostUserIdList: row.accountCostUserId ? row.accountCostUserId.split(',') : [],
          accountCostUserNameList: row.accountCostUserName ? row.accountCostUserName.split(',') : [],
          accountCostUserAllocVoList: row.accountCostUserAllocDtoList || []
        })
        selectedUserTags.value = [...(accountCostVo.accountCostUserNameList || [])]
        showSuggestion.value = false
        // 异步加载完整记录（含 recVer 等后端需要的字段）
        findAccountCost({id: row.id}).then((res) => {
          if (res.code === 200 && res.data) {
            const d = res.data
            Object.assign(accountCostVo, d, {
              outlay: d.outlay != null ? String(d.outlay) : accountCostVo.outlay,
              paymentTimeStr: formatDate(d.paymentTime) || accountCostVo.paymentTimeStr,
              accountCostUserIdList: d.accountCostUserId ? d.accountCostUserId.split(',') : (d.accountCostUserIdList || accountCostVo.accountCostUserIdList),
              accountCostUserNameList: d.accountCostUserName ? d.accountCostUserName.split(',') : (d.accountCostUserNameList || accountCostVo.accountCostUserNameList),
              accountCostUserAllocVoList: d.accountCostUserAllocVoList || d.accountCostUserAllocDtoList || []
            })
            selectedUserTags.value = [...(accountCostVo.accountCostUserNameList || [])]
          }
        }).catch(e => { console.error('加载记账详情失败', e) })
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
        const totalAmount = parseFloat(allocList.reduce((sum, item) => sum + (parseFloat(item.amount) || 0), 0).toFixed(2))
        const outlay = parseFloat(parseFloat(accountCostVo.outlay || 0).toFixed(2))
        if (totalAmount > outlay) {
          uni.showToast({title: '费用分摊的合计金额不能超过总金额！', icon: 'none'})
          return
        }
      }

      loading.value = true
      try {
        const params = {
          ...accountCostVo,
          outlay: Number(accountCostVo.outlay),
          paymentTime: accountCostVo.paymentTime || formatDateTime(new Date()),
          accountCostUserIdList: accountCostVo.accountCostUserIdList || [],
          accountCostUserNameList: accountCostVo.accountCostUserNameList || [],
          accountCostUserAllocVoList: accountCostVo.accountCostUserAllocVoList || []
        }
        if (!params.id) delete params.id
        const res = await saveUpdate(params)
        if (res.code === 200) {
          uni.showToast({title: operate.value === 'ADD' ? '新增成功' : '修改成功', icon: 'success'})
          doFindPageAccountCost()
          if (operate.value === 'ADD') {
            // 保存后不关闭弹窗，清空除日期外的输入信息
            const savedDate = accountCostVo.paymentTimeStr
            const savedTime = accountCostVo.paymentTime
            Object.assign(accountCostVo, {
              id: '', costDesc: '', outlay: '', costType: '', icon: '',
              paymentSign: 'EXPENSES',
              paymentTimeStr: savedDate,
              paymentTime: savedTime,
              remark: '',
              accountCostUserIdList: [], accountCostUserNameList: [], accountCostUserAllocVoList: []
            })
            selectedUserTags.value = []
            showSuggestion.value = false
            addExpenseActive.value = 0
          } else {
            closeModal()
          }
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
          // 如果已从 findAccountCost 加载了分摊数据则直接使用
          if (accountCostVo.accountCostUserAllocVoList && accountCostVo.accountCostUserAllocVoList.length > 0) {
            return
          }
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
      const totalAmount = parseFloat(list.reduce((sum, item) => sum + (parseFloat(item.amount) || 0), 0).toFixed(2))
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
    const loadIconGridData = async (headerId) => {
      try {
        const res = await findCustomIconTemplateDetail({ headerId })
        if (res.code === 200 && res.data) {
          iconGridList.value = res.data
          const state = {}
          res.data.forEach((group) => {
            if (group.name) state[group.name] = false
          })
          collapsedGroups.value = state
          return true
        }
      } catch (e) {
        // 失败时回退到旧 API
        try {
          const res = await findCacheCostType({ costDesc: '' })
          if (res.code === 200 && res.data) {
            iconGridList.value = [{
              name: '全部',
              children: res.data.filter((item) => item.iconUrl).map((item) => ({
                name: item.value,
                url: item.iconUrl
              }))
            }]
            collapsedGroups.value = { '全部': false }
            return true
          }
        } catch (e2) { /* ignore */ }
      }
      return false
    }

    const openIconGrid = async () => {
      iconGridLoading.value = true
      showIconGrid.value = true
      let headerId = ''
      try {
        const headerRes = await findSelectCommonIconHeader()
        if (headerRes.code === 200 && headerRes.data) {
          const defaultHeader = headerRes.data.find((item) => item.defaultFlag)
          if (defaultHeader && defaultHeader.value) {
            headerId = defaultHeader.value
          } else if (headerRes.data.length > 0) {
            headerId = headerRes.data[0].value || ''
          }
        }
      } catch (e) { /* ignore */ }
      await loadIconGridData(headerId)
      iconGridLoading.value = false
    }

    const toggleGroup = (groupName) => {
      collapsedGroups.value[groupName] = !collapsedGroups.value[groupName]
    }

    const pickIcon = (item) => {
      accountCostVo.icon = item.url || ''
      accountCostVo.costType = item.name || ''
      selectedIconName.value = item.name || ''
    }

    // ==================== 新增图标 ====================
    // 内嵌图标选择器的展开状态
    const showAddIconPicker = ref(false)

    const initDefaultHeaderId = async () => {
      if (defaultHeaderId.value) return
      try {
        const headerRes = await findSelectCommonIconHeader()
        if (headerRes.code === 200 && headerRes.data) {
          const defaultHeader = headerRes.data.find((item) => item.defaultFlag)
          if (defaultHeader && defaultHeader.value) {
            defaultHeaderId.value = defaultHeader.value
          } else if (headerRes.data.length > 0) {
            defaultHeaderId.value = headerRes.data[0].value || ''
          }
        }
      } catch (e) { /* ignore */ }
    }

    const openAddIconModal = async () => {
      // 重置表单
      addIconForm.parentId = ''
      addIconForm.name = ''
      addIconForm.iconId = ''
      addIconForm.iconUrl = ''
      addIconForm.originName = ''
      addIconForm.orderNum = 0
      addIconForm.remark = ''
      showAddIconPicker.value = false

      // 确保有默认 headerId
      await initDefaultHeaderId()
      if (!defaultHeaderId.value) {
        uni.showToast({ title: '未找到默认图标模板，无法新增', icon: 'none' })
        return
      }

      // 加载分组
      const groupRes = await findGrouping({ headerId: defaultHeaderId.value })
      if (groupRes.code === 200 && groupRes.data) {
        // findGrouping 返回数组 [{label, value}, ...]
        groupingList.value = Array.isArray(groupRes.data) ? groupRes.data : []
      }

      // 加载通用图标选择器数据
      const pickerRes = await findCommonIconPicker({})
      if (pickerRes.code === 200 && pickerRes.data) {
        addIconPickerList.value = pickerRes.data
        const state = {}
        Object.keys(pickerRes.data).forEach((key) => { state[key] = false })
        addIconCollapsed.value = state
      }

      showAddIconModal.value = true
    }

    const hideAddIconModal = () => {
      showAddIconModal.value = false
      showAddIconPicker.value = false
    }

    const onAddIconGroupChange = (e) => {
      const idx = e.detail.value
      const item = groupingList.value[idx]
      if (item) {
        addIconForm.parentId = item.value
        // 查询该分组下的最大排序号
        findMaxOrderNum({
          parentId: item.value,
          headerId: defaultHeaderId.value,
          iconTreeType: 'L'
        }).then((res) => {
          if (res.code === 200 && res.data != null) {
            addIconForm.orderNum = res.data
          }
        })
      }
    }

    const findGroupLabel = (value) => {
      const found = groupingList.value.find((g) => g.value === value)
      return found ? found.label : ''
    }

    const toggleAddIconGroup = (key) => {
      addIconCollapsed.value[key] = !addIconCollapsed.value[key]
    }

    const pickAddIcon = (item) => {
      addIconForm.iconId = item.id || ''
      addIconForm.iconUrl = item.url || ''
      addIconForm.originName = item.name || ''
    }

    const submitAddIcon = async () => {
      if (!addIconForm.parentId) { uni.showToast({ title: '请选择所属分组', icon: 'none' }); return }
      if (!addIconForm.name) { uni.showToast({ title: '请输入图标名称', icon: 'none' }); return }
      if (!addIconForm.iconId) { uni.showToast({ title: '请选择图标', icon: 'none' }); return }

      addIconLoading.value = true
      try {
        const res = await saveUpdateIconTemplateDetail({
          headerId: defaultHeaderId.value,
          parentId: addIconForm.parentId,
          name: addIconForm.name,
          iconId: addIconForm.iconId,
          iconTreeType: 'L',
          orderNum: addIconForm.orderNum || 0,
          remark: addIconForm.remark
        })
        if (res.code === 200) {
          uni.showToast({ title: '新增图标成功', icon: 'success' })
          hideAddIconModal()
          // 静默刷新图标选择器数据
          loadIconGridData(defaultHeaderId.value)
        } else {
          uni.showToast({ title: res.msg || '新增失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '新增图标失败', icon: 'none' })
      } finally {
        addIconLoading.value = false
      }
    }

    // ==================== 新增图标分组 ====================
    const openAddGroupModal = async () => {
      addGroupForm.name = ''
      addGroupForm.orderNum = 0
      addGroupForm.remark = ''

      await initDefaultHeaderId()
      if (!defaultHeaderId.value) {
        uni.showToast({ title: '未找到默认图标模板，无法新增', icon: 'none' })
        return
      }

      // 查询最大排序号
      findMaxOrderNum({
        headerId: defaultHeaderId.value,
        iconTreeType: 'R'
      }).then((res) => {
        if (res.code === 200 && res.data != null) {
          addGroupForm.orderNum = res.data
        }
      })

      showAddGroupModal.value = true
    }

    const hideAddGroupModal = () => {
      showAddGroupModal.value = false
    }

    const submitAddGroup = async () => {
      if (!addGroupForm.name) { uni.showToast({ title: '请输入分区名称', icon: 'none' }); return }

      addGroupLoading.value = true
      try {
        const res = await saveUpdateIconTemplateDetail({
          headerId: defaultHeaderId.value,
          parentId: '-1',
          name: addGroupForm.name,
          grouping: addGroupForm.name,
          iconTreeType: 'R',
          orderNum: addGroupForm.orderNum || 0,
          remark: addGroupForm.remark
        })
        if (res.code === 200) {
          uni.showToast({ title: '新增分组成功', icon: 'success' })
          hideAddGroupModal()
          // 静默刷新图标选择器数据
          loadIconGridData(defaultHeaderId.value)
        } else {
          uni.showToast({ title: res.msg || '新增失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '新增分组失败', icon: 'none' })
      } finally {
        addGroupLoading.value = false
      }
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
      showIconGrid, iconGridList, iconGridLoading, collapsedGroups, openIconGrid, toggleGroup, pickIcon,
      showAddIconModal, addIconForm, groupingList, addIconPickerList, addIconCollapsed,
      showAddIconPicker, addIconLoading,
      openAddIconModal, hideAddIconModal, onAddIconGroupChange, findGroupLabel,
      toggleAddIconGroup, pickAddIcon, submitAddIcon,
      showAddGroupModal, addGroupForm, addGroupLoading,
      openAddGroupModal, hideAddGroupModal, submitAddGroup,
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

.tenant-switch-wrap {
  display: flex;
  align-items: center;
  gap: 20rpx;
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
.icon-pick-row { display: flex; align-items: center; justify-content: space-between; }
.icon-pick-left { display: flex; align-items: center; gap: 20rpx; }
.icon-pick-actions { display: flex; flex-shrink: 0; gap: 12rpx; }
.icon-pick-box {
  width: 80rpx; height: 80rpx;
  border: 2rpx dashed #ddd; border-radius: 8rpx;
  display: flex; align-items: center; justify-content: center;
  background: #fafafa;
}
.icon-pick-img { width: 64rpx; height: 64rpx; border-radius: 6rpx; }
.icon-pick-empty { font-size: 40rpx; color: #ccc; }

/* 新增图标-内嵌图标选择器 */
.add-icon-picker-wrap {
  margin-top: 16rpx;
  border: 1rpx solid #eee;
  border-radius: 8rpx;
  padding: 12rpx;
  max-height: 400rpx;
  overflow-y: auto;
  background: #fafafa;
}

.add-icon-picker-wrap .icon-grid-item {
  width: 25%;
  padding: 10rpx 6rpx;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-icon-picker-wrap .icon-grid-img {
  width: 72rpx;
  height: 72rpx;
  border-radius: 8rpx;
}

.add-icon-picker-wrap .icon-grid-placeholder {
  width: 72rpx;
  height: 72rpx;
  border-radius: 8rpx;
  background: #e8e8e8;
}

.add-icon-group {
  margin-bottom: 4rpx;
}

.add-icon-group-header {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 10rpx 14rpx;
  font-size: 24rpx;
  background: #f0f0f0;
  border-radius: 4rpx;
}

/* 图标分组 */
.icon-group {
  margin-bottom: 8rpx;
}

.icon-group-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 20rpx;
  background: #f8f8f8;
  border-radius: 6rpx;
  font-size: 26rpx;
}

.icon-group-arrow {
  font-size: 20rpx;
  color: #999;
  transition: transform 0.25s;
  display: inline-block;
}

.icon-group-arrow.collapsed {
  transform: rotate(90deg);
}

.icon-group-title {
  font-weight: 600;
  color: #333;
  flex: 1;
}

.icon-group-count {
  font-size: 22rpx;
  color: #bbb;
}

/* 图标网格 */
.icon-grid { display: flex; flex-wrap: wrap; padding: 12rpx 0; }
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
