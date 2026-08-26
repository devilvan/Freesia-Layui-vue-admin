<template>
  <view class="page-wrap">
    <view class="lay-card" style="padding: 0">
      <view class="lay-tab-header">
        <view class="lay-tab-item" :class="{ active: currentTab === 'notice' }" @click="switchTab('notice')">
          消息通知
          <text v-if="userStore.state.noticeCount > 0" class="badge">{{ userStore.state.noticeCount }}</text>
        </view>
        <view class="lay-tab-item" :class="{ active: currentTab === 'announcement' }" @click="switchTab('announcement')">
          系统公告
          <text v-if="userStore.state.announcementCount > 0" class="badge">{{ userStore.state.announcementCount }}</text>
        </view>
      </view>
    </view>

    <view v-if="currentTab === 'notice'">
      <view class="message-action-row">
        <button
            class="lay-btn lay-btn-sm lay-btn-warm"
            :disabled="userStore.state.noticeCount <= 0"
            @click="doMarkAllRead(SysNoticeType.NOTICE)"
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
      <view v-else v-for="item in noticeList" :key="item.id" class="lay-card msg-card" :class="{ unread: !item.readFlag }"
            @click="viewNotice(item)">
        <view class="flex-row justify-between mb-sm">
          <text class="msg-title ellipsis" style="flex: 1">{{ item.title || '无标题' }}</text>
          <view v-if="!item.readFlag" class="lay-badge dot"></view>
        </view>
        <text class="msg-content ellipsis text-muted">{{ item.excerpt || item.content || '暂无内容' }}</text>
        <view class="flex-row justify-between mt-sm">
          <text class="text-muted" style="font-size: 22rpx">{{ formatTime(item.createTime || item.effectiveTimeFrom) }}</text>
          <text class="text-muted" style="font-size: 22rpx">{{ item.category || '' }}</text>
        </view>
      </view>

      <view class="lay-card" v-if="noticeTotal > noticePage.limit">
        <view class="lay-pagination">
          <text class="pagination-info">共 {{ noticeTotal }} 条</text>
          <view class="lay-btn-group gap-xs">
            <button class="lay-btn lay-btn-sm" :disabled="noticePage.current <= 1" @click="prevNoticePage">上一页</button>
            <button
                class="lay-btn lay-btn-sm"
                :disabled="noticePage.current * noticePage.limit >= noticeTotal"
                @click="nextNoticePage"
            >
              下一页
            </button>
          </view>
        </view>
      </view>
    </view>

    <view v-if="currentTab === 'announcement'">
      <view class="message-action-row">
        <button
            class="lay-btn lay-btn-sm lay-btn-warm"
            :disabled="userStore.state.announcementCount <= 0"
            @click="doMarkAllRead(SysNoticeType.ANNOUNCEMENT)"
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
      <view v-else v-for="item in announceList" :key="item.id" class="lay-card msg-card" :class="{ unread: !item.readFlag }"
            @click="viewAnnouncement(item)">
        <view class="flex-row justify-between mb-sm">
          <text class="msg-title ellipsis" style="flex: 1">{{ item.title || '无标题' }}</text>
          <view v-if="!item.readFlag" class="lay-badge dot"></view>
        </view>
        <text class="msg-content ellipsis text-muted">{{ item.excerpt || item.content || '暂无内容' }}</text>
        <view class="flex-row justify-between mt-sm">
          <text class="text-muted" style="font-size: 22rpx">{{ formatTime(item.effectiveTimeFrom) }}</text>
          <text v-if="item.effectiveTimeTo" class="text-muted" style="font-size: 22rpx">
            至 {{ formatTime(item.effectiveTimeTo) }}
          </text>
        </view>
      </view>

      <view class="lay-card" v-if="announceTotal > announcePage.limit">
        <view class="lay-pagination">
          <text class="pagination-info">共 {{ announceTotal }} 条</text>
          <view class="lay-btn-group gap-xs">
            <button class="lay-btn lay-btn-sm" :disabled="announcePage.current <= 1" @click="prevAnnouncePage">上一页</button>
            <button
                class="lay-btn lay-btn-sm"
                :disabled="announcePage.current * announcePage.limit >= announceTotal"
                @click="nextAnnouncePage"
            >
              下一页
            </button>
          </view>
        </view>
      </view>
    </view>

    <view class="lay-modal-mask" v-if="detailVisible" @click="detailVisible = false">
      <view class="lay-modal" @click.stop>
        <view class="lay-modal-header">
          <text class="lay-modal-title ellipsis">{{ currentDetail.title || '详情' }}</text>
          <text class="lay-modal-close" @click="detailVisible = false">×</text>
        </view>
        <scroll-view scroll-y class="lay-modal-body">
          <view class="detail-header">
            <text class="text-muted" style="font-size:24rpx">发布时间: {{ formatTime(currentDetail.createTime) }}</text>
            <text v-if="currentDetail.category" class="text-muted" style="font-size:24rpx">
              分类: {{ currentDetail.category }}
            </text>
          </view>
          <view class="detail-content">
            <text>{{ currentDetail.content || currentDetail.excerpt || '暂无内容' }}</text>
          </view>
        </scroll-view>
        <view class="lay-modal-footer">
          <button class="lay-btn lay-btn-sm lay-btn-primary" @click="detailVisible = false">关闭</button>
        </view>
      </view>
    </view>

    <view style="height: 40rpx"></view>
  </view>
</template>

<script>
import {onMounted, reactive, ref} from 'vue'
import {findPageSysNotice, findUnreadCount, markAllRead, markRead} from '@/api/system/Notice'
import {SysNoticeType} from '@/types/system/Notice'
import {useUserStore} from '@/store/user'

export default {
  name: 'Message',
  setup() {
    const userStore = useUserStore()
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
        console.error('加载未读数量失败', e)
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

    onMounted(() => {
      loadUnreadCount()
      loadNoticeList()
    })

    return {
      userStore,
      currentTab,
      noticeList,
      announceList,
      noticeLoading,
      announceLoading,
      noticeTotal,
      announceTotal,
      noticePage,
      announcePage,
      detailVisible,
      currentDetail,
      formatTime,
      switchTab,
      viewNotice,
      viewAnnouncement,
      prevNoticePage,
      nextNoticePage,
      prevAnnouncePage,
      nextAnnouncePage,
      doMarkAllRead,
      SysNoticeType
    }
  }
}
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
}

.message-action-row {
  display: flex;
  justify-content: flex-end;
  padding: 16rpx 20rpx 0;
}

.msg-card {
  margin-bottom: 2rpx;

  &.unread {
    border-left: 4rpx solid var(--global-primary-color);
  }
}

.msg-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

.msg-content {
  font-size: 26rpx;
  line-height: 1.5;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  border-radius: 16rpx;
  background: var(--global-danger-color);
  color: #fff;
  font-size: 20rpx;
  margin-left: 8rpx;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1px solid #f0f0f0;
}

.detail-content {
  font-size: 28rpx;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}
</style>
