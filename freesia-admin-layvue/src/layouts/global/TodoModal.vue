<template>
  <lay-tab type="brief" style="margin: 5px" v-model="currentIndex">
    <lay-tab-item :title="`待办事项(${todoList.length})`" id="1">
      <lay-card class="inform-item todo-item"
                v-for="(item, index) in todoList"
                :key="index">
        <div class="todo-title">
          <div style="line-height: 60px">
            <lay-checkbox name="like" skin="primary" v-model="item.title" value="1"
                          @change="toggleStatus">
            </lay-checkbox>
          </div>
          <div style="flex: 1">
            <div class="oneRow" :title="item.title">
              <h2>{{ item.title }}</h2>
            </div>
            <div class="oneRow desc" :title="item.title">
              <h3>{{ item.desc }}</h3>
            </div>
            <div class="inform-item-time todo-item-time">
              <lay-tooltip content="设置提醒时间" position="top">
                <lay-dropdown
                    updateAtScroll
                    :clickOutsideToClose="true"
                    :clickToClose="false"
                    :blurToClose="true"
                    placement="bottom"
                    :trigger="'click'"
                >
                  <lay-date-picker style="width: 60%"
                                   :static="true"
                                   type="datetime"
                                   v-model="item.dueTime"
                                   :format="sdf_YMDHM" :inputFormat="sdf_YMDHM"
                                   placeholder="设置提醒时间"
                                   allow-clear></lay-date-picker>
                </lay-dropdown>
              </lay-tooltip>
            </div>
          </div>
          <div v-show="item.status == '未开始'" class="todo-tags">
            <lay-tag color="#6e6e6e" variant="light">未开始</lay-tag>
          </div>
          <div v-show="item.status == '进行中'" class="todo-tags">
            <lay-tag color="#2dc570" variant="light">进行中</lay-tag>
          </div>
          <div v-show="item.status == '即将到期'" class="todo-tags">
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
import {onMounted, ref, watch} from 'vue'
import {SysNoticeEntity, SysNoticeType, SysNoticeVo} from "@/types/system/Notice";
import {findListSysNotice} from "@/api/system/Notice";
import {R} from "@/types/Result";
import {useAppStore} from "@/store/app";
import {buildRange,} from "@/util/UDate";
import {useUserStore} from "@/store/user";
import {CommonTodoVo} from "@/types/common/todo/Icon";


/*INIT*/
onMounted(async () => {
  // loadDataSource()
})


// const emit = defineEmits(['callback']);
/*INIT*/

/*VAR*/
const sdf_YMDHM = 'YYYY-MM-DD HH:mm'
const appStore = useAppStore()
const userStore = useUserStore()
const noticeList = ref<SysNoticeEntity[]>()
const announcementList = ref<SysNoticeEntity[]>()
const todoList = ref<CommonTodoVo[]>([
  {
    title: '张三的请假审批AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaa',
    desc: '张三的请假审批AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaa',
    status: '未开始',
    dueTime: new Date()
  },
  {
    title: '考试监管',
    desc: '考试监管',
    status: '进行中',
    dueTime: new Date()
  },
  {
    title: '注册新仓库',
    desc: '注册新仓库',
    status: '即将到期',
    dueTime: new Date()
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
  color: #222222;
  font-size: 14px;
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
      margin-top: 10px;
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
  margin: 0 10px;
}

.oneRow {
  width: 350px;
  margin: 0 10px;
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

.title {
  font-size: 16pt;
}

.desc {
  color: grey;
}
</style>
