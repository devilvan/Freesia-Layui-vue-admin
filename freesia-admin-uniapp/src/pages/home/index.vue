<template>
  <view class="mine-wrap">
    <view class="user-header">
      <view class="avatar-wrap">
        <view class="avatar">
          <text class="avatar-text">{{ userInfo?.nickName?.charAt(0) || 'U' }}</text>
        </view>
        <view class="user-info">
          <text class="user-name">{{ userInfo?.nickName || '用户名' }}</text>
          <text class="user-role">{{ userInfo?.roleNames || '普通用户' }}</text>
        </view>
      </view>
    </view>

    <view class="menu-section">
      <view class="section-title">账户管理</view>
      <view class="menu-list">
        <view class="menu-item" @click="goToPage('/pages/enrollee/accounts/mine/index')">
          <text class="menu-icon">💰</text>
          <text class="menu-text">我的账单</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goToPage('/pages/enrollee/accounts/budget/index')">
          <text class="menu-icon">📊</text>
          <text class="menu-text">预算管理</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <view class="menu-section">
      <view class="section-title">消息中心</view>
      <view class="menu-list">
        <view class="menu-item" @click="goToPage('/pages/enrollee/message/index')">
          <text class="menu-icon">📩</text>
          <text class="menu-text">通知消息</text>
          <view class="badge" v-if="unreadCount > 0">{{ unreadCount }}</view>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goToPage('/pages/enrollee/message/announcement')">
          <text class="menu-icon">📢</text>
          <text class="menu-text">系统公告</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <view class="menu-section">
      <view class="section-title">系统设置</view>
      <view class="menu-list">
        <view class="menu-item" @click="goToPage('/pages/enrollee/profile/index')">
          <text class="menu-icon">👤</text>
          <text class="menu-text">个人资料</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="logout">
          <text class="menu-icon">🚪</text>
          <text class="menu-text">退出登录</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

export default {
  name: 'Mine',
  setup() {
    const userStore = useUserStore()
    const userInfo = ref({})
    const unreadCount = ref(0)

    onMounted(async () => {
      await loadUserInfo()
    })

    const loadUserInfo = async () => {
      try {
        const info = await userStore.getInfo()
        if (info) {
          userInfo.value = info
        }
      } catch (e) {
        console.error('获取用户信息失败', e)
      }
    }

    const goToPage = (url) => {
      uni.navigateTo({ url })
    }

    const logout = () => {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            userStore.logout()
            uni.redirectTo({ url: '/pages/login/index' })
          }
        }
      })
    }

    return {
      userInfo,
      unreadCount,
      goToPage,
      logout
    }
  }
}
</script>

<style lang="scss">
.mine-wrap {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.user-header {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  padding: 60rpx 30rpx 40rpx;
}

.avatar-wrap {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 30rpx;
}

.avatar-text {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.user-role {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.menu-section {
  margin: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}

.section-title {
  padding: 24rpx 30rpx;
  font-size: 26rpx;
  color: #999;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-list {
  padding: 0 30rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  position: relative;

  &:last-child {
    border-bottom: none;
  }
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.menu-arrow {
  font-size: 36rpx;
  color: #ccc;
}

.badge {
  background: #ff4d4f;
  color: #fff;
  font-size: 22rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
}
</style>