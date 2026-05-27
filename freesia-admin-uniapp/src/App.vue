<script>
import {useUserStore} from '@/store/user'

export default {
  onLaunch: async function () {
    console.log('App Launch')
    await uni.$getPublicKey()
    const token = uni.getStorageSync('token')
    if (token) {
      try {
        await useUserStore().getInfo()
      } catch (e) {
        console.error('加载用户信息失败', e)
      }
    }
  },
  onShow: function () {
    console.log('App Show')
  },
  onHide: function () {
    console.log('App Hide')
  },
}
</script>

<style lang="scss">
@import "./uni.scss";
@import "./styles/layui-mobile.scss";

/* 全局重置 */
page {
  background-color: #f5f5f5;
}

/* 滚动条样式 - 仅 H5 */
::-webkit-scrollbar {
  width: 4px;
}
::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.12);
  border-radius: 2px;
}
</style>
