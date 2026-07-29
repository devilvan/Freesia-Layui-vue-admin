<template>
  <view>
    <MessageBell />
  </view>
</template>

<script>
export default {
  name: 'AddEntry',
  onShow() {
    // Set global flag so mine page knows to open add modal
    const app = getApp()
    if (!app.globalData) app.globalData = {}
    app.globalData.showAddModal = true
    uni.switchTab({url: '/pages/enrollee/accounts/mine/index'})
  }
}
</script>
