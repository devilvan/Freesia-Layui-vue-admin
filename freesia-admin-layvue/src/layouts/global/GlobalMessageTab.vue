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
      <lay-tab type="brief" style="margin: 5px" v-model="currentIndex">
        <lay-tab-item :title="`通知(${noticeUnreadCount})`" id="1">
          <div style="width: 100%; height: 100%; overflow: hidden">
            <div
                class="inform-item"
                v-for="(item, index) in noticeList"
                :key="index"
                @click="doMarkRead(item, index)"
            >
              <div class="inform-item-icon">
                <img src="@/assets/messageSlot/info1.png" alt=""/>
              </div>
              <div class="inform-item-text" :style="getRowStyle(item, index)">
                <div>{{ item.title }}</div>
                <lay-tooltip :visible="false" :content="item.content">
                  <div class="oneRow" :title="item.content">{{ item.content }}</div>
                </lay-tooltip>
                <div class="inform-item-time">
                  {{ item.createTime }}
                </div>
              </div>
              <div class="inform-item-readFlag">
                <div v-show="noticeList[index].readFlag">
                  <lay-tag :color="'#c2c2c2'" variant="light">已读</lay-tag>
                </div>
                <div v-show="!noticeList[index].readFlag">
                  <lay-tag :color="'#31BDEC'" variant="light">未读</lay-tag>
                </div>
              </div>
            </div>
          </div>
        </lay-tab-item>
        <lay-tab-item :title="`私信(${privateLetteList.length})`" id="2">
          <div style="width: 100%; height: 100%; overflow: hidden">
            <div
                class="inform-item privateLette-item"
                v-for="(item, index) in privateLetteList"
                :key="index"
            >
              <div class="inform-item-icon">
                <img src="../../assets/messageSlot/avatar1.png" alt=""/>
              </div>
              <div class="inform-item-text">
                <div>{{ item.title }}</div>
                <div class="inform-item-time">
                  {{ item.content }}
                </div>
                <div class="inform-item-time">
                  {{ item.time }}
                </div>
              </div>
            </div>
          </div>
        </lay-tab-item>
        <lay-tab-item :title="`待办(${todoList.length})`" id="3">
          <div style="width: 100%; height: 100%; overflow: hidden">
            <div
                class="inform-item todo-item"
                v-for="(item, index) in todoList"
                :key="index"
            >
              <div class="todo-title">
                <div style="flex: 1">
                  {{ item.title }}
                  <div class="inform-item-time todo-item-time">
                    {{ item.time }}
                  </div>
                </div>
                <div v-show="item.type == '未开始'" class="todo-tags">
                  <lay-tag color="#6e6e6e" variant="light">未开始</lay-tag>
                </div>
                <div v-show="item.type == '进行中'" class="todo-tags">
                  <lay-tag color="#2dc570" variant="light">进行中</lay-tag>
                </div>
                <div v-show="item.type == '即将到期'" class="todo-tags">
                  <lay-tag color="#F5319D" variant="light">即将到期</lay-tag>
                </div>
              </div>
            </div>
          </div>
        </lay-tab-item>
      </lay-tab>
    </template>
  </lay-dropdown>
</template>

<script lang='ts'>
export default {
  name: 'MessageTab'
}
</script>
<script setup lang="ts">
import {onMounted, reactive, ref, watch} from 'vue'
import {MarkReadVo, SysNoticeEntity, SysNoticeType, SysNoticeVo} from "@/types/system/Notice";
import {findListSysNotice, findPageSysNotice, findUnreadCount, markRead} from "@/api/system/Notice";
import {R, TableResult} from "@/types/Result";
import {PageQuery} from "@/types/Common";
import {useAppStore} from "@/store/app";
import {layer} from "@layui/layui-vue";
import {buildRange} from "@/util/UDate";

interface MessageTabProps {
  flag: boolean
}

/*INIT*/
onMounted(() => {
  doFindAnnouncementUnreadCount();
  doFindNoticeUnreadCount()
  loadDataSource()
})

const props = withDefaults(defineProps<MessageTabProps>(), {
  flag: false
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

const emit = defineEmits(['callback']);
/*INIT*/

/*VAR*/
const appStore = useAppStore()
const manualRef = ref()
const noticeList = ref<SysNoticeEntity[]>()
const privateLetteList = ref([
  {
    img: 'avatar1.png',
    title: '速尔 评论了 你的日志',
    content: '写的不错，以后向你学习哦~',
    time: '2021-08-09 12:00:00'
  },
  {
    img: 'avatar2.png',
    title: '速尔 评论了 你的日志',
    content: '写的不错，以后向你学习哦~',
    time: '2021-08-09 12:00:00'
  },
  {
    img: 'avatar3.png',
    title: '速尔 评论了 你的日志',
    content: '写的不错，以后向你学习哦~',
    time: '2021-08-09 12:00:00'
  },
  {
    img: 'avatar4.png',
    title: '速尔 评论了 你的日志',
    content: '写的不错，以后向你学习哦~',
    time: '2021-08-09 12:00:00'
  },
  {
    img: 'avatar5.png',
    title: '速尔 评论了 你的日志',
    content: '写的不错，以后向你学习哦~',
    time: '2021-08-09 12:00:00'
  },
  {
    img: 'avatar6.png',
    title: '速尔 评论了 你的日志',
    content: '写的不错，以后向你学习哦~',
    time: '2021-08-09 12:00:00'
  }
])
const todoList = ref([
  {
    title: '张三的请假审批',
    type: '未开始',
    time: '张三在 08-09 12:00:00 提交的请假...'
  },
  {
    title: '考试监管',
    type: '进行中',
    time: '考试监管在 08-09 12:00:00 之前打卡'
  },
  {
    title: '注册新仓库',
    type: '即将到期',
    time: '需要在 08-09 12:00:00 之前完成'
  }
])

const currentIndex = ref('1')
const announcementUnreadCount = ref<number>();
const noticeUnreadCount = ref<number>();
const searchQuery = ref<SysNoticeVo>({});
/*VAR*/

/*FUNCTION*/
function loadDataSource() {
  let createTime: string[] = buildRange(7)
  searchQuery.value.type = SysNoticeType.NOTICE
  searchQuery.value.createTimeFrom = new Date(createTime[0])
  searchQuery.value.createTimeTo = new Date(createTime[1])
  findListSysNotice(searchQuery.value).then((res: R<SysNoticeEntity[]>) => {
    if (res.code === 200) {
      noticeList.value = res.data;
    }
  });
}

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

function doMarkRead(item: any, idx: number) {
  if (item.readFlag) {
    layer.notify({
      title: "成功",
      content: "标记已读成功",
      time: 5000,
      icon: 1,
    })
    return ;
  }
  let type = item.type;
  let param: MarkReadVo = {
    idList: new Array(item.id),
    type: type
  }
  markRead(param).then((res: any) => {
    if (SysNoticeType.NOTICE === type) {
      noticeUnreadCount.value = res.data;
      noticeList.value[idx].readFlag = true
    } else if (SysNoticeType.ANNOUNCEMENT === type) {
      announcementUnreadCount.value = res.data
    }
    emit('callback', calculateSumCount())
    layer.notify({
      title: "成功",
      content: "标记已读成功",
      time: 5000,
      icon: 1,
    })
  })
}

function calculateSumCount(): number {
  let sumCount = 0;
  if (noticeUnreadCount && noticeUnreadCount.value) {
    sumCount += noticeUnreadCount.value
  }
  return sumCount
}

function getRowStyle(row: any, rowIndex: number) {
  if (row.readFlag) return 'color:' + '#c2c2c2';
  return ''
}

/*FUNCTION*/
</script>


<style lang="less" scoped>
.inform-item {
  box-sizing: border-box;
  display: flex;
  width: 600px;
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

.todo-item {
  box-sizing: border-box;
  padding: 0 10px;
}

.todo-title {
  width: 100%;
  display: flex;
  line-height: 30px;
}

.todo-tags {
  width: 100px;
  text-align: right;
  line-height: 60px;
}

.todo-item-time {
  line-height: 20px;
  color: #ada4a4;
  font-size: 12px;
}

.oneRow {
  width: 450px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}
</style>
