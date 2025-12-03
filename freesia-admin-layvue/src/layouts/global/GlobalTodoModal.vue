<template>
  <lay-tab type="brief" v-model="currentIndex">
    <lay-tab-item :title="`待办事项(${commonTodoList.length})`" id="1">
      <lay-card class="inform-item todo-item">
        <lay-button
            border="green"
            border-style="dashed"
            :fluid="true"
            style="margin: 10px;width: 100%;height: 100px"
        >
          <lay-icon style="font-size: 36pt" type="layui-icon-add-one"></lay-icon>
        </lay-button>
      </lay-card>
      <lay-card class="inform-item todo-item"
                v-for="(item, index) in commonTodoList"
                :key="index">
        <div class="todo-title">
          <div style="line-height: 60px">
            <lay-checkbox name="like" skin="primary" v-model="item.title" value="1"
                          @change="toggleStatus">
            </lay-checkbox>
          </div>
          <div style="flex: 1">
            <div class="oneRow" :title="item.title">
              <h3>{{ item.title }}</h3>
            </div>
            <div class="oneRow desc" :title="item.title">
              <h4>{{ item.desc }}</h4>
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
                                   size="sm"
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
  name: 'GlobalTodoModal'
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
import {CommonTodoEntity, CommonTodoVo} from "@/types/common/todo/Todo";
import {findListCommonTodo} from "@/api/common/todo/Todo";
import {findConfigByKey} from "@/api/system/Config";
import {SysConfigKey} from "@/types/system/Config";
import Http from "@/api/Http";
import {layer} from "@layui/layui-vue";


/*INIT*/
onMounted(async () => {
  loadDataSource()
})


// const emit = defineEmits(['callback']);
/*INIT*/

/*VAR*/
const sdf_YMDHM = 'YYYY-MM-DD HH:mm'
const appStore = useAppStore()
const userStore = useUserStore()
const commonTodoList = ref<CommonTodoEntity[]>([
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
const searchQuery = ref<CommonTodoVo>({});
const addIconUrl = ref('')
/*VAR*/

/*FUNCTION*/
function loadDataSource() {
  findListCommonTodo(searchQuery.value).then((res: R<CommonTodoEntity[]>) => {
    if (res.code === 200) {
    }
  })
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
