<script>
export default {
  onLaunch: function () {
    console.log('App Launch')
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
