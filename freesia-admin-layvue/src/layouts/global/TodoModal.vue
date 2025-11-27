<template>
  <lay-tab type="brief" style="margin: 5px" v-model="currentIndex">
    <lay-tab-item :title="`待办事项(${todoList.length})`" id="1">
      <lay-card class="inform-item todo-item"
                v-for="(item, index) in todoList"
                :key="index">
        <div class="todo-title">
          <div style="line-height: 60px">
            <lay-checkbox name="like" skin="primary" v-model="item.title" value="1"
                          @change="toggleStatus"></lay-checkbox>
          </div>
          <div style="flex: 1">
            <div class="oneRow" :title="item.title">
              {{ item.title }}
            </div>
            <div class="inform-item-time todo-item-time">
              <lay-tooltip content="设置提醒时间" position="bottom">
                <lay-icon class="notice" type="layui-icon-notice" size="lg"></lay-icon>
              </lay-tooltip>
              {{ item.time }}
            </div>
            <lay-date-picker v-model="item.paymentTime" allow-clear type="datetime"
                             :shortcuts="singleShortcuts" :inputFormat="sdf_YMDHM"
                             style="width: 100%" simple></lay-date-picker>
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
      </lay-card>
    </lay-tab-item>
  </lay-tab>
</template>

<script lang='ts'>
export default {
  name: 'TodoModal'
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
import {buildRange, defaultShortcuts, singleShortcuts, YMD_HMS} from "@/util/UDate";
import {useUserStore} from "@/store/user";

interface MessageTabProps {
  flag: boolean
}

/*INIT*/
onMounted(async () => {
  loadDataSource()
})

const emit = defineEmits(['callback']);
/*INIT*/

/*VAR*/
const sdf_YMDHM = 'YYYY-MM-DD HH:mm'
const appStore = useAppStore()
const userStore = useUserStore()
const noticeList = ref<SysNoticeEntity[]>()
const announcementList = ref<SysNoticeEntity[]>()
const todoList = ref([
  {
    title: '张三的请假审批AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaa',
    type: '未开始',
    time: '张三在 08-09 12:00:00 提交的请假...',
    paymentTime: '2025-11-27 23:01'
  },
  {
    title: '考试监管',
    type: '进行中',
    time: '考试监管在 08-09 12:00:00 之前打卡',
    paymentTime: '2025-11-27 23:01'
  },
  {
    title: '注册新仓库',
    type: '即将到期',
    time: '需要在 08-09 12:00:00 之前完成',
    paymentTime: '2025-11-27 23:01'
  }
])

const currentIndex = ref('1')
const searchQuery = ref<SysNoticeVo>({});
/*VAR*/

/*FUNCTION*/
function loadDataSource() {
  let createTime: string[] = buildRange(6)
  // 查询公告
  searchQuery.value.type = SysNoticeType.ANNOUNCEMENT
  searchQuery.value.createTimeFrom = new Date(createTime[0])
  searchQuery.value.createTimeTo = new Date(createTime[1])
  findListSysNotice(searchQuery.value).then((res: R<SysNoticeEntity[]>) => {
    if (res.code === 200) {
      announcementList.value = res.data;
    }
    // 查询消息
    searchQuery.value.type = SysNoticeType.NOTICE
    findListSysNotice(searchQuery.value).then((res1: R<SysNoticeEntity[]>) => {
      if (res1.code === 200) {
        noticeList.value = res1.data;
      }
      userStore.calculateSumCount()
    });
  });
}

function toggleStatus() {

}

/*FUNCTION*/
</script>


<style lang="less" scoped>
.inform-item {
  box-sizing: border-box;
  width: 100%;
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
  line-height: 30px;
  color: #ada4a4;
  font-size: 12px;
}

.oneRow {
  width: 350px;
  margin-top: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.notice {
  margin-right: 10px;
}

.notice:hover {
  color: var(--global-primary-color) !important;
  cursor: pointer;
}
</style>
