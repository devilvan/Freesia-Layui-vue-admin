<template>
  <lay-dropdown
      updateAtScroll
      ref="manualRef"
      :clickOutsideToClose="true"
      :clickToClose="false"
      :blurToClose="true"
      placement="bottom"
      :trigger="'hover'"
  >
    <slot></slot>
    <template #content>
      <div class="dropdownContainer">
        <lay-scroll height="500px">
          <lay-tab type="brief" style="margin: 5px" v-model="currentIndex">
            <lay-tab-item :title="`通知(${userStore.noticeCount})`" id="1">
              <div class="tab-action-bar">
                <lay-button
                    size="sm"
                    type="warm"
                    :disabled="!noticeList.length"
                    @click="doMarkAllRead(SysNoticeType.NOTICE)"
                >
                  全部已读
                </lay-button>
              </div>
              <div
                  class="inform-item"
                  v-for="(item, index) in noticeList"
                  :key="index"
                  @click="doMarkRead(item, index)"
              >
                <div class="inform-item-icon">
                  <img src="@/assets/messageSlot/info1.png" alt=""/>
                </div>
                <div class="inform-item-text" :style="getRowStyle(item)">
                  <div>{{ item.title }}</div>
                  <div class="oneRow" :title="item.content">{{ item.content }}</div>
                  <div class="inform-item-time">
                    {{ item.createTime }}
                  </div>
                </div>
                <div class="inform-item-readFlag">
                  <div v-show="item.readFlag">
                    <lay-tag :color="'#c2c2c2'" variant="light">已读</lay-tag>
                  </div>
                  <div v-show="!item.readFlag">
                    <lay-tag :color="'#31BDEC'" variant="light">未读</lay-tag>
                  </div>
                </div>
              </div>
            </lay-tab-item>
            <lay-tab-item :title="`公告(${userStore.announcementCount})`" id="2">
              <div class="tab-action-bar">
                <lay-button
                    size="sm"
                    type="warm"
                    :disabled="!announcementList.length"
                    @click="doMarkAllRead(SysNoticeType.ANNOUNCEMENT)"
                >
                  全部已读
                </lay-button>
              </div>
              <div style="width: 100%; height: 100%; overflow: hidden">
                <div
                    class="inform-item privateLette-item"
                    v-for="(item, index) in announcementList"
                    :key="index"
                    @click="doMarkRead(item, index)"
                >
                  <div class="inform-item-icon">
                    <img src="@/assets/messageSlot/info2.png" alt=""/>
                  </div>
                  <div class="inform-item-text" :style="getRowStyle(item)">
                    <div>{{ item.title }}</div>
                    <div class="oneRow" :title="item.content">{{ item.content }}</div>
                    <div class="inform-item-time">
                      {{ item.createTime }}
                    </div>
                  </div>
                  <div class="inform-item-readFlag">
                    <div v-show="item.readFlag">
                      <lay-tag :color="'#c2c2c2'" variant="light">已读</lay-tag>
                    </div>
                    <div v-show="!item.readFlag">
                      <lay-tag :color="'#31BDEC'" variant="light">未读</lay-tag>
                    </div>
                  </div>
                </div>
              </div>
            </lay-tab-item>
          </lay-tab>
        </lay-scroll>
      </div>
    </template>
  </lay-dropdown>
</template>

<script lang='ts'>
export default {
  name: 'MessageTab'
}
</script>
<script setup lang="ts">
import {onMounted, ref, watch} from 'vue'
import {MarkReadVo, SysNoticeEntity, SysNoticeType, SysNoticeVo} from "@/types/system/Notice";
import {findListSysNotice, findUnreadCount, markAllRead, markRead} from "@/api/system/Notice";
import {R} from "@/types/Result";
import {layer} from "@layui/layui-vue";
import {buildRange} from "@/util/UDate";
import {useUserStore} from "@/store/user";

interface MessageTabProps {
  flag: boolean
}

const props = withDefaults(defineProps<MessageTabProps>(), {
  flag: false
})

const emit = defineEmits(['callback']);

const userStore = useUserStore()
const manualRef = ref()
const noticeList = ref<SysNoticeEntity[]>([])
const announcementList = ref<SysNoticeEntity[]>([])
const currentIndex = ref('1')

onMounted(async () => {
  doFindAnnouncementUnreadCount()
  doFindNoticeUnreadCount()
  loadDataSource()
})

watch(
    () => props.flag,
    (newVal) => {
      if (newVal) {
        manualRef.value.show()
      } else {
        manualRef.value.hide()
      }
    }
)

function loadDataSource() {
  const createTime: string[] = buildRange(6)
  const announcementQuery: SysNoticeVo = {
    type: SysNoticeType.ANNOUNCEMENT,
    createTimeFrom: new Date(createTime[0]),
    createTimeTo: new Date(createTime[1])
  }
  findListSysNotice(announcementQuery).then((res: R<SysNoticeEntity[]>) => {
    if (res.code === 200) {
      announcementList.value = res.data || []
    }
    const noticeQuery: SysNoticeVo = {
      type: SysNoticeType.NOTICE,
      createTimeFrom: new Date(createTime[0]),
      createTimeTo: new Date(createTime[1])
    }
    findListSysNotice(noticeQuery).then((res1: R<SysNoticeEntity[]>) => {
      if (res1.code === 200) {
        noticeList.value = res1.data || []
      }
      userStore.calculateSumCount()
    })
  })
}

function doFindAnnouncementUnreadCount() {
  const params: SysNoticeVo = {
    type: SysNoticeType.ANNOUNCEMENT
  }
  findUnreadCount(params).then((res: any) => {
    if (res.code === 200) {
      userStore.announcementCount = res.data
    }
  })
}

function doFindNoticeUnreadCount() {
  const params: SysNoticeVo = {
    type: SysNoticeType.NOTICE
  }
  findUnreadCount(params).then((res: any) => {
    if (res.code === 200) {
      userStore.noticeCount = res.data
    }
  })
}

function doMarkRead(item: SysNoticeEntity, idx: number) {
  if (item.readFlag) {
    layer.notify({
      title: "成功",
      content: "标记已读成功",
      time: 5000,
      icon: 1,
    })
    return
  }
  const type = item.type as SysNoticeType
  const param: MarkReadVo = {
    idList: [String(item.id)],
    type
  }
  markRead(param).then((res: any) => {
    if (res.code !== 200) {
      return
    }
    if (SysNoticeType.NOTICE === type) {
      userStore.noticeCount = res.data
      if (noticeList.value[idx]) {
        noticeList.value[idx].readFlag = true
      }
    } else if (SysNoticeType.ANNOUNCEMENT === type) {
      userStore.announcementCount = res.data
      if (announcementList.value[idx]) {
        announcementList.value[idx].readFlag = true
      }
    }
    userStore.calculateSumCount()
    emit('callback', userStore.unreadCount)
    layer.notify({
      title: "成功",
      content: "标记已读成功",
      time: 5000,
      icon: 1,
    })
  })
}

function doMarkAllRead(type: SysNoticeType) {
  const isNotice = type === SysNoticeType.NOTICE
  const unreadCount = isNotice ? userStore.noticeCount : userStore.announcementCount
  if (!unreadCount || unreadCount <= 0) {
    layer.msg("当前没有未读消息", {icon: 3})
    return
  }
  markAllRead({type}).then((res: any) => {
    if (res.code !== 200) {
      return
    }
    if (isNotice) {
      userStore.noticeCount = res.data
      noticeList.value = noticeList.value.map((item) => ({...item, readFlag: true}))
    } else {
      userStore.announcementCount = res.data
      announcementList.value = announcementList.value.map((item) => ({...item, readFlag: true}))
    }
    userStore.calculateSumCount()
    emit('callback', userStore.unreadCount)
    layer.notify({
      title: "成功",
      content: "全部已读成功",
      time: 5000,
      icon: 1,
    })
  })
}

function getRowStyle(row: SysNoticeEntity) {
  if (row.readFlag) return 'color:#c2c2c2'
  return ''
}
</script>


<style lang="less" scoped>
.inform-item {
  box-sizing: border-box;
  display: flex;
  width: 500px;
  height: 80px;
  color: #222222;
  font-size: 14px;
  padding: 0 20px;
  border-bottom: 1px solid #f3f3f3;

  .inform-item-icon {
    display: inline-block;
    width: 40px;
    height: 100%;
    line-height: 60px;
    text-align: center;

    > img {
      width: 28px;
      height: 28px;
      vertical-align: middle;
    }
  }

  .inform-item-readFlag {
    display: inline-block;
    width: 60px;
    height: 100%;
    line-height: 60px;
    text-align: center;
  }

  .inform-item-text {
    width: 60%;
    box-sizing: border-box;
    display: inline-block;
    flex: 1;
    padding: 10px 0 0 10px;

    .inform-item-time {
      margin-top: 6px;
      color: #ada4a4;
      font-size: 12px;
    }
  }
}

.inform-item:hover {
  background-color: #fafafa;
}

.privateLette-item {
  height: 80px;
}

.oneRow {
  width: 350px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.dropdownContainer {
  width: 500px;
  height: 500px
}

.tab-action-bar {
  display: flex;
  justify-content: flex-start;
  padding: 4px 10px 10px;
}
</style>
