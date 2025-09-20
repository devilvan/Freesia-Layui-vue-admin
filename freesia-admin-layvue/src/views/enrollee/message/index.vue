<template>
  <lay-tab
      type="brief"
      style="
      background: #fff;
      padding: 20px 0;
      border: 1px solid #eee;
      border-radius: 2px;
      height: 70%;
      margin: 10px;
    "
      v-model="currentTab"
      tabPosition="left"
  >
    <lay-tab-item id="system">
      <template #title>
        系统公告
        <div style="width: 40px; margin-left: 20px; display: inline-block">
          <div v-if="announcementUnreadCount > 0" class="corner-mark">
            {{ announcementUnreadCount }}
          </div>
        </div>
      </template>
      <Announcement @callback="changeAnnouncementUnreadCount"/>
    </lay-tab-item>
    <lay-tab-item id="user">
      <template #title>
        消息通知
        <div style="width: 40px; margin-left: 20px; display: inline-block">
          <div v-if="noticeUnreadCount > 0" class="corner-mark">
            {{ noticeUnreadCount }}
          </div>
        </div>
      </template>
      <Notice @callback="changeNoticeUnreadCount"/>
    </lay-tab-item>
    <!--    <lay-tab-item id="todo">-->
    <!--      <template #title>-->
    <!--        待办事项-->
    <!--        <div style="width: 40px; margin-left: 20px; display: inline-block">-->
    <!--          <div v-if="messageInfo.todo > 0" class="corner-mark">-->
    <!--            {{ messageInfo.todo }}-->
    <!--          </div>-->
    <!--        </div>-->
    <!--      </template>-->
    <!--      <table-content key="todo"/>-->
    <!--    </lay-tab-item>-->
  </lay-tab>
</template>

<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "Message",
};
</script>
<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import tableContent from './table.vue'
import {SysNoticeType, SysNoticeVo} from "@/types/system/Notice";
import {findUnreadCount} from "@/api/system/Notice";
import Notice from "@/views/enrollee/message/notice.vue";
import Announcement from "@/views/enrollee/message/announcement.vue";

/*INIT*/
onMounted(() => {
  doFindAnnouncementUnreadCount();
  doFindNoticeUnreadCount()
})
/*INIT*/

/*VAR*/
const currentTab = ref('system')
const messageInfo = ref({
  system: 3,
  user: 0,
  todo: 11
})
const announcementUnreadCount = ref<number>();
const noticeUnreadCount = ref<number>();
/*VAR*/

/*FUNCTION*/

function doFindAnnouncementUnreadCount() {
  let params: SysNoticeVo = {
    type: SysNoticeType.ANNOUNCEMENT
  }
  findUnreadCount(params).then((res: any) => {
    if (res.code === 200) {
      announcementUnreadCount.value = res.data
    }
  })
}

function doFindNoticeUnreadCount() {
  let params: SysNoticeVo = {
    type: SysNoticeType.NOTICE
  }
  findUnreadCount(params).then((res: any) => {
    if (res.code === 200) {
      noticeUnreadCount.value = res.data
    }
  })
}

function changeAnnouncementUnreadCount(count: number) {
  announcementUnreadCount.value = count;
}

function changeNoticeUnreadCount(count: number) {
  noticeUnreadCount.value = count;
}

/*FUNCTION*/
</script>

<style scoped>
.corner-mark {
  height: 16px;
  display: inline-block;
  padding: 0 5px;
  color: #fff;
  line-height: 16px;
  background-color: var(--global-primary-color);
  border-radius: 14px;
}
</style>