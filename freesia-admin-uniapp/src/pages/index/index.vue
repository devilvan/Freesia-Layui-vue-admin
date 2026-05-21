<template>
  <view class="content">
    <view class="header">
      <text class="welcome">🎉 欢迎登录 Freesia-Admin</text>
    </view>
    <view class="card">
      <view class="card-title">功能列表</view>
      <view class="menu-grid">
        <view class="menu-item" @click="showToast('用户管理')">
          <view class="menu-icon">👥</view>
          <text class="menu-text">用户管理</text>
        </view>
        <view class="menu-item" @click="showToast('权限管理')">
          <view class="menu-icon">🔒</view>
          <text class="menu-text">权限管理</text>
        </view>
        <view class="menu-item" @click="showToast('系统设置')">
          <view class="menu-icon">⚙️</view>
          <text class="menu-text">系统设置</text>
        </view>
        <view class="menu-item" @click="showToast('数据统计')">
          <view class="menu-icon">📊</view>
          <text class="menu-text">数据统计</text>
        </view>
      </view>
    </view>
    <view class="logout-btn" @click="logout">
      <text>🔓 退出登录</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      title: '首页'
    }
  },
  onLoad() {
    const token = uni.getStorageSync('token')
    if (!token) {
      uni.redirectTo({ url: '/pages/login/index' })
    }
  },
  methods: {
    showToast(text) {
      uni.showToast({ title: `即将进入${text}`, icon: 'none' })
    },
    logout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync('token')
            uni.redirectTo({ url: '/pages/login/index' })
          }
        }
      })
    }
  }
}
</script>

<style>
.content {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 30rpx;
  box-sizing: border-box;
}

.header {
  padding: 40rpx 0;
  text-align: center;
}

.welcome {
  font-size: 40rpx;
  font-weight: bold;
  color: #1890ff;
}

.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30rpx;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 20rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  transition: transform 0.2s;
}

.menu-item:active {
  transform: scale(0.95);
}

.menu-icon {
  font-size: 64rpx;
  margin-bottom: 20rpx;
}

.menu-text {
  font-size: 28rpx;
  color: #666;
}

.logout-btn {
  margin-top: 40rpx;
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}

.logout-btn:active {
  opacity: 0.8;
}
</style>
