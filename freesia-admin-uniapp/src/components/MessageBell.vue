<template>
  <Teleport to="body">
    <view class="message-bell" v-if="isLoggedIn" @click="openPopup">
      <image class="bell-icon" src="@/static/tabbar/notice.svg" mode="aspectFit" />
      <view v-if="totalUnread > 0" class="unread-badge">{{ totalUnread > 99 ? '99+' : totalUnread }}</view>
    </view>

    <view class="message-overlay" v-if="popupVisible" @click="closePopup">
      <view class="message-popup" @click.stop>
        <view class="popup-header">
          <text class="popup-title">消息中心</text>
          <text class="popup-close" @click="closePopup">×</text>
        </view>

        <view class="popup-tabs">
          <view class="popup-tab-item" :class="{ active: currentTab === 'notice' }" @click="switchTab('notice')">
            消息通知
            <text v-if="userStore.state.noticeCount > 0" class="tab-badge">{{ userStore.state.noticeCount }}</text>
          </view>
          <view
              class="popup-tab-item"
              :class="{ active: currentTab === 'announcement' }"
              @click="switchTab('announcement')"
          >
            系统公告
            <text v-if="userStore.state.announcementCount > 0" class="tab-badge">
              {{ userStore.state.announcementCount }}
            </text>
          </view>
        </view>

        <scroll-view scroll-y class="popup-body" v-if="currentTab === 'notice'">
          <view class="batch-bar">
            <button
                class="lay-btn lay-btn-sm lay-btn-warm mark-all-read-btn"
                :disabled="userStore.state.noticeCount <= 0"
                @click.stop="doMarkAllRead(SysNoticeType.NOTICE)"
            >
              全部已读
            </button>
          </view>
          <view v-if="noticeLoading" class="lay-empty">
            <text class="empty-text">加载中...</text>
          </view>
          <view v-else-if="noticeList.length === 0" class="lay-empty">
            <text class="empty-icon">🔔</text>
            <text class="empty-text">暂无消息通知</text>
          </view>
          <view
              v-else
              v-for="item in noticeList"
              :key="item.id"
              class="msg-card"
              :class="{ unread: !item.readFlag }"
              @click="viewNotice(item)"
          >
            <view class="flex-row justify-between mb-sm">
              <text class="msg-title ellipsis" style="flex: 1">{{ item.title || '无标题' }}</text>
              <view v-if="!item.readFlag" class="dot-badge"></view>
            </view>
            <text class="msg-content ellipsis text-muted">{{ item.excerpt || item.content || '暂无内容' }}</text>
            <view class="flex-row justify-between mt-sm">
              <text class="text-muted msg-time">{{ formatTime(item.createTime || item.effectiveTimeFrom) }}</text>
              <text class="text-muted msg-time">{{ item.category || '' }}</text>
            </view>
          </view>
          <view v-if="noticeTotal > noticePage.limit" class="pagination-row">
            <text class="pagination-info">共 {{ noticeTotal }} 条</text>
            <view class="lay-btn-group gap-xs">
              <button class="lay-btn lay-btn-sm" :disabled="noticePage.current <= 1" @click="prevNoticePage">
                上一页
              </button>
              <button
                  class="lay-btn lay-btn-sm"
                  :disabled="noticePage.current * noticePage.limit >= noticeTotal"
                  @click="nextNoticePage"
              >
                下一页
              </button>
            </view>
          </view>
        </scroll-view>

        <scroll-view scroll-y class="popup-body" v-if="currentTab === 'announcement'">
          <view class="batch-bar">
            <button
                class="lay-btn lay-btn-sm lay-btn-warm mark-all-read-btn"
                :disabled="userStore.state.announcementCount <= 0"
                @click.stop="doMarkAllRead(SysNoticeType.ANNOUNCEMENT)"
            >
              全部已读
            </button>
          </view>
          <view v-if="announceLoading" class="lay-empty">
            <text class="empty-text">加载中...</text>
          </view>
          <view v-else-if="announceList.length === 0" class="lay-empty">
            <text class="empty-icon">📢</text>
            <text class="empty-text">暂无系统公告</text>
          </view>
          <view
              v-else
              v-for="item in announceList"
              :key="item.id"
              class="msg-card"
              :class="{ unread: !item.readFlag }"
              @click="viewAnnouncement(item)"
          >
            <view class="flex-row justify-between mb-sm">
              <text class="msg-title ellipsis" style="flex: 1">{{ item.title || '无标题' }}</text>
              <view v-if="!item.readFlag" class="dot-badge"></view>
            </view>
            <text class="msg-content ellipsis text-muted">{{ item.excerpt || item.content || '暂无内容' }}</text>
            <view class="flex-row justify-between mt-sm">
              <text class="text-muted msg-time">{{ formatTime(item.effectiveTimeFrom) }}</text>
              <text v-if="item.effectiveTimeTo" class="text-muted msg-time">
                至 {{ formatTime(item.effectiveTimeTo) }}
              </text>
            </view>
          </view>
          <view v-if="announceTotal > announcePage.limit" class="pagination-row">
            <text class="pagination-info">共 {{ announceTotal }} 条</text>
            <view class="lay-btn-group gap-xs">
              <button class="lay-btn lay-btn-sm" :disabled="announcePage.current <= 1" @click="prevAnnouncePage">
                上一页
              </button>
              <button
                  class="lay-btn lay-btn-sm"
                  :disabled="announcePage.current * announcePage.limit >= announceTotal"
                  @click="nextAnnouncePage"
              >
                下一页
              </button>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <view class="detail-overlay" v-if="detailVisible" @click="detailVisible = false">
      <view class="detail-modal" @click.stop>
        <view class="detail-header">
          <text class="detail-title ellipsis">{{ currentDetail.title || '详情' }}</text>
          <text class="detail-close" @click="detailVisible = false">×</text>
        </view>
        <scroll-view scroll-y class="detail-body">
          <view class="detail-meta">
            <text class="text-muted" style="font-size:24rpx">发布时间: {{ formatTime(currentDetail.createTime) }}</text>
            <text v-if="currentDetail.category" class="text-muted" style="font-size:24rpx">
              分类: {{ currentDetail.category }}
            </text>
          </view>
          <view class="detail-text">
            <text>{{ currentDetail.content || currentDetail.excerpt || '暂无内容' }}</text>
          </view>
        </scroll-view>
        <view class="detail-footer">
          <button class="lay-btn lay-btn-sm" @click="detailVisible = false">关闭</button>
        </view>
      </view>
    </view>
  </Teleport>
</template>

<script setup>
import {computed, onMounted, reactive, ref, watch} from 'vue'
import {findPageSysNotice, findUnreadCount, markAllRead, markRead} from '@/api/system/Notice'
import {SysNoticeType} from '@/types/system/Notice'
import {useUserStore} from '@/store/user'

const userStore = useUserStore()
const isLoggedIn = computed(() => !!userStore.state.token)
const popupVisible = ref(false)
const currentTab = ref('notice')
const noticeList = ref([])
const announceList = ref([])
const noticeLoading = ref(false)
const announceLoading = ref(false)
const noticeTotal = ref(0)
const announceTotal = ref(0)
const detailVisible = ref(false)
const currentDetail = ref({})

const noticePage = reactive({current: 1, limit: 10})
const announcePage = reactive({current: 1, limit: 10})

const totalUnread = computed(() => {
  return (userStore.state.noticeCount || 0) + (userStore.state.announcementCount || 0)
})

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  if (isNaN(d.getTime())) return ''
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const loadUnreadCount = async () => {
  try {
    const noticeRes = await findUnreadCount({type: SysNoticeType.NOTICE})
    if (noticeRes.code === 200) {
      userStore.state.noticeCount = noticeRes.data || 0
    }
    const announceRes = await findUnreadCount({type: SysNoticeType.ANNOUNCEMENT})
    if (announceRes.code === 200) {
      userStore.state.announcementCount = announceRes.data || 0
    }
  } catch (e) {
    console.error('加载未读消息数失败', e)
  }
}

const switchTab = (tab) => {
  currentTab.value = tab
  if (tab === 'notice') {
    loadNoticeList()
  } else {
    loadAnnounceList()
  }
}

const loadNoticeList = async () => {
  noticeLoading.value = true
  try {
    const res = await findPageSysNotice({type: SysNoticeType.NOTICE}, noticePage)
    if (res.code === 200) {
      noticeList.value = res.rows || res.records || []
      noticeTotal.value = res.total || 0
    }
  } catch (e) {
    console.error('加载通知失败', e)
  } finally {
    noticeLoading.value = false
  }
}

const loadAnnounceList = async () => {
  announceLoading.value = true
  try {
    const res = await findPageSysNotice({type: SysNoticeType.ANNOUNCEMENT}, announcePage)
    if (res.code === 200) {
      announceList.value = res.rows || res.records || []
      announceTotal.value = res.total || 0
    }
  } catch (e) {
    console.error('加载公告失败', e)
  } finally {
    announceLoading.value = false
  }
}

const viewNotice = async (item) => {
  currentDetail.value = item
  detailVisible.value = true
  if (!item.readFlag) {
    try {
      const res = await markRead({idList: [item.id], type: SysNoticeType.NOTICE})
      item.readFlag = true
      userStore.state.noticeCount = res.data || 0
    } catch (e) {
      console.error('标记通知已读失败', e)
    }
  }
}

const viewAnnouncement = async (item) => {
  currentDetail.value = item
  detailVisible.value = true
  if (!item.readFlag) {
    try {
      const res = await markRead({idList: [item.id], type: SysNoticeType.ANNOUNCEMENT})
      item.readFlag = true
      userStore.state.announcementCount = res.data || 0
    } catch (e) {
      console.error('标记公告已读失败', e)
    }
  }
}

const doMarkAllRead = async (type) => {
  const isNotice = type === SysNoticeType.NOTICE
  const unreadCount = isNotice ? userStore.state.noticeCount : userStore.state.announcementCount
  if (!unreadCount || unreadCount <= 0) {
    uni.showToast({
      title: '当前没有未读消息',
      icon: 'none'
    })
    return
  }
  try {
    const res = await markAllRead({type})
    if (res.code !== 200) {
      return
    }
    if (isNotice) {
      userStore.state.noticeCount = res.data || 0
      noticeList.value = noticeList.value.map(item => ({...item, readFlag: true}))
    } else {
      userStore.state.announcementCount = res.data || 0
      announceList.value = announceList.value.map(item => ({...item, readFlag: true}))
    }
    uni.showToast({
      title: '全部已读成功',
      icon: 'none'
    })
  } catch (e) {
    console.error('全部已读失败', e)
  }
}

const prevNoticePage = () => {
  noticePage.current--
  loadNoticeList()
}

const nextNoticePage = () => {
  noticePage.current++
  loadNoticeList()
}

const prevAnnouncePage = () => {
  announcePage.current--
  loadAnnounceList()
}

const nextAnnouncePage = () => {
  announcePage.current++
  loadAnnounceList()
}

const openPopup = () => {
  popupVisible.value = true
  loadUnreadCount()
  if (currentTab.value === 'notice') {
    loadNoticeList()
  } else {
    loadAnnounceList()
  }
}

const closePopup = () => {
  popupVisible.value = false
}

watch(
    () => userStore.state.token,
    (token) => {
      if (!token) {
        popupVisible.value = false
      }
    }
)

onMounted(() => {
  loadUnreadCount()
})
</script>

<style lang="scss" scoped>
.message-bell {
  position: fixed;
  top: calc(var(--status-bar-height, 0px) + 5px);
  right: 20px;
  width: 40px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  cursor: pointer;
}

.bell-icon {
  width: 44rpx;
  height: 44rpx;
}

.unread-badge {
  position: absolute;
  top: -2px;
  right: -4px;
  min-width: 28rpx;
  height: 28rpx;
  padding: 0 6rpx;
  border-radius: 14rpx;
  background: var(--global-danger-color, #ff5722);
  color: #fff;
  font-size: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.message-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 10000;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.message-popup {
  margin-top: calc(var(--status-bar-height, 0px) + 44px);
  width: 95vw;
  max-height: calc(100vh - var(--status-bar-height, 0px) - 88px);
  background: #fff;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.15);
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.popup-close {
  font-size: 36rpx;
  color: #999;
  width: 50rpx;
  height: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.popup-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.popup-tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;

  &.active {
    color: var(--global-primary-color, #009688);
    font-weight: 500;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60rpx;
      height: 4rpx;
      background: var(--global-primary-color, #009688);
      border-radius: 2rpx;
    }
  }
}

.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  border-radius: 16rpx;
  background: var(--global-danger-color, #ff5722);
  color: #fff;
  font-size: 20rpx;
  margin-left: 6rpx;
}

.popup-body {
  flex: 1;
  overflow-y: auto;
  padding: 8rpx 12rpx;
}

.batch-bar {
  display: flex;
  justify-content: flex-end;
  padding: 12rpx 0 16rpx;
}

.mark-all-read-btn {
  background: linear-gradient(135deg, #ffd666 0%, #ffb800 100%);
  border-color: #f0a800;
  color: #4a2c00 !important;
  font-weight: 600;
  box-shadow: 0 6rpx 16rpx rgba(255, 184, 0, 0.24);
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.35);
}

.mark-all-read-btn[disabled] {
  background: #ffe7a3;
  border-color: #ffd666;
  color: #8c6a12 !important;
  box-shadow: none;
  text-shadow: none;
}

.msg-card {
  width: 90%;
  background: #fff;
  padding: 20rpx 24rpx;
  margin-bottom: 12rpx;
  border-radius: 12rpx;
  border: 1px solid #f0f0f0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;

  &.unread {
    border-left: 4rpx solid var(--global-primary-color, #009688);
    background: #f9fffe;
  }
}

.msg-title {
  font-size: 26rpx;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-content {
  font-size: 24rpx;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-right: 16rpx;
}

.dot-badge {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: var(--global-danger-color, #ff5722);
  flex-shrink: 0;
}

.msg-time {
  font-size: 22rpx;
}

.pagination-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 0;
}

.pagination-info {
  font-size: 24rpx;
  color: #999;
}

.detail-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 10001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-modal {
  width: 86vw;
  max-width: 700rpx;
  max-height: 70vh;
  background: #fff;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.detail-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  flex: 1;
}

.detail-close {
  font-size: 36rpx;
  color: #999;
  width: 50rpx;
  height: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-body {
  flex: 1;
  overflow-y: auto;
  padding: 24rpx 30rpx;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1px solid #f0f0f0;
}

.detail-text {
  font-size: 28rpx;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.detail-footer {
  padding: 20rpx 30rpx;
  text-align: right;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}
</style>
