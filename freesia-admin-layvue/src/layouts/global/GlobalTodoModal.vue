<template>
  <lay-tab type="brief" v-model="currentIndex">
    <lay-tab-item :title="`待办事项(${commonTodoList.length})`" id="1">
      <lay-card class="inform-item todo-item">
        <lay-button
            border="green"
            border-style="dashed"
            :fluid="true"
            style="margin: 10px;width: 98%;height: 50px"
            @click="changeModal(Operate.ADD, null)">
          <lay-icon style="font-size: 24pt" type="layui-icon-add-one"></lay-icon>
        </lay-button>
      </lay-card>
      <lay-card class="inform-item todo-item"
                v-for="(item, index) in commonTodoList"
                :key="index">
        <lay-dropdown :trigger="['click']" alignPoint>
          <div class="todo-title">
            <div style="line-height: 60px">
              <lay-checkbox name="like" skin="primary" v-model="item.status"
                            :value="1"
                            @change="toggleStatus(item)">
              </lay-checkbox>
            </div>
            <div style="flex: 1">
              <div class="oneRow" :title="item.title">
                <h3>{{ item.title }}</h3>
              </div>
              <div class="oneRow desc" :title="item.title">
                <h4>{{ item.content }}</h4>
              </div>
              <div v-show="item.dueTime" class="oneRow" :title="item.title">
<!--                <span>提醒时间：{{ item.dueTime ? formatDateTime(new Date(item.dueTime), "yyyy-MM-dd HH:mm") : null }}</span>-->
                <span>提醒时间：{{ item.dueTime }}</span>
              </div>
            </div>
            <div v-if="item.status" class="todo-tags">
              <lay-tag color="#16b777" variant="light">已完成</lay-tag>
            </div>
            <div v-else class="todo-tags">
              <lay-tag color="#393D49" variant="light">未开始</lay-tag>
            </div>
          </div>
          <template #content>
            <lay-dropdown-menu>
              <lay-dropdown-menu-item @click="changeModal(Operate.EDIT, item)">编辑</lay-dropdown-menu-item>
            </lay-dropdown-menu>
            <lay-dropdown-menu>
              <lay-dropdown-menu-item @click="doDeleteCommonTodo(item)">删除</lay-dropdown-menu-item>
            </lay-dropdown-menu>
          </template>
        </lay-dropdown>
      </lay-card>
    </lay-tab-item>
    <lay-layer v-model="showCommonTodoAddModalFlag" :title="commonTodoAddModalTitle" :area="['600px']">
      <div style="padding: 20px" v-esc-close="toCancel">
        <lay-form ref="commonTodoAddModalRef" :model="commonTodoAddVo" :rules="commonTodoAddFromRules"
                  label-position="top">
          <lay-row space="20">
            <lay-col :md="24">
              <lay-form-item label="标题" prop="title">
                <lay-input v-model="commonTodoAddVo.title" :allow-clear="true" show-count :maxlength="128"
                           ref="todoModalFocusInputRef"></lay-input>
              </lay-form-item>
              <lay-form-item label="内容" prop="content" required>
                <lay-textarea v-model="commonTodoAddVo.content" :allow-clear="true"></lay-textarea>
              </lay-form-item>
              <lay-form-item label="完成状态" prop="status">
                <lay-select
                    size="sm"
                    style="width: 100%"
                    v-model="commonTodoAddVo.status"
                    :options="todoStatusSelectList"
                    :items="todoStatusSelectList"
                    :allow-clear="true"
                    placeholder="完成状态状态（默认【未开始】）"
                ></lay-select>
              </lay-form-item>
              <lay-form-item label="提醒时间" prop="dueTime">
                <lay-date-picker style="width: 100%" size="sm" type="datetime" v-model="commonTodoAddVo.dueTime"
                                 :format="sdf_YMDHM" :inputFormat="sdf_YMDHM" placeholder="设置提醒时间"
                                 allow-clear></lay-date-picker>
              </lay-form-item>
              <lay-form-item label="优先级" prop="priority">
                <lay-radio v-model="commonTodoAddVo.priority" name="action" :value="null">无</lay-radio>
                <lay-radio v-model="commonTodoAddVo.priority" name="action" :value="0">高</lay-radio>
                <lay-radio v-model="commonTodoAddVo.priority" name="action" :value="1">中</lay-radio>
                <lay-radio v-model="commonTodoAddVo.priority" name="action" :value="2">低</lay-radio>
              </lay-form-item>
            </lay-col>
          </lay-row>
        </lay-form>
        <div style="width: 100%; text-align: right">
          <lay-button size="sm" type="primary" @click="doSaveCommonTodo">确定</lay-button>
          <lay-button size="sm" @click="toCancel">取消</lay-button>
        </div>
      </div>
    </lay-layer>
  </lay-tab>
</template>

<script lang='ts'>
export default {
  name: 'GlobalTodoModal'
}
</script>
<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {R} from "@/types/Result";
import {useAppStore} from "@/store/app";
import {defaultShortcuts, formatDateTime,} from "@/util/UDate";
import {useUserStore} from "@/store/user";
import {CommonTodoEntity, CommonTodoVo} from "@/types/common/todo/Todo";
import {deleteCommonTodo, findListCommonTodo, saveUpdate} from "@/api/common/todo/Todo";
import {Flag, Operate} from "@/types/Constants";
import {SysDictValueEntity} from "@/types/system/Dict";
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
const commonTodoList = ref<CommonTodoEntity[]>([])
const currentIndex = ref('1')
const searchQuery = ref<CommonTodoVo>({});
const commonTodoAddVo = ref<CommonTodoVo>({})
const commonTodoAddFromRules = ref()
const commonTodoAddModalRef = ref()
const commonTodoAddModalTitle = ref('新增')
const showCommonTodoAddModalFlag = ref<Boolean>(false);
const todoStatusSelect = ref<Array<SysDictValueEntity>>([]);
const todoStatusSelectList = ref<any[]>([
  {label: '未完成', value: false},
  {label: '已完成', value: true}
]);
const todoModalFocusInputRef = ref()

/*VAR*/

/*FUNCTION*/
function loadDataSource() {
  findListCommonTodo(searchQuery.value).then((res: R<CommonTodoEntity[]>) => {
    if (res.code === 200) {
      commonTodoList.value = res.data
    }
  })
}

function toggleStatus(item: CommonTodoEntity) {
  saveUpdate(item).then((res: R<void>) => {
    if (res.code === 200) {
      layer.msg(res.msg, {icon: 1, time: 1000})
      loadDataSource();
    }
  })
}

function changeModal(title: Operate, row: CommonTodoVo) {
  commonTodoAddVo.value = {}
  commonTodoAddModalTitle.value = Operate.ADD === title ? "新增" : Operate.EDIT === title ? "编辑" : Operate.COPY === title ? "复制" : "";
  showCommonTodoAddModalFlag.value = !showCommonTodoAddModalFlag.value
  if (title === Operate.ADD) {
    todoModalFocusInputRef.value.focus()
  } else if (title === Operate.EDIT) {
    commonTodoAddVo.value = {...row}
  } else if (title === Operate.COPY) {
    commonTodoAddVo.value.userId = row.userId;
    commonTodoAddVo.value.title = row.title;
    commonTodoAddVo.value.desc = row.desc;
    commonTodoAddVo.value.content = row.content;
    commonTodoAddVo.value.status = row.status;
    commonTodoAddVo.value.dueTime = row.dueTime;
    commonTodoAddVo.value.reminderSendFlag = row.reminderSendFlag;
    commonTodoAddVo.value.priority = row.priority;
    commonTodoAddVo.value.remark = row.remark;
  }
}

function toCancel() {
  showCommonTodoAddModalFlag.value = false
}

function addHandler() {
  commonTodoList.value.unshift({
    title: '考试监管',
    desc: '考试监管',
    dueTime: new Date()
  },);
}

function doSaveCommonTodo() {
  saveUpdate(commonTodoAddVo.value).then((res: R<void>) => {
    if (res.code === 200) {
      commonTodoAddVo.value = {}
      loadDataSource()
      toCancel();
    }
  })
}

function doDeleteCommonTodo(item: CommonTodoVo) {
  layer.confirm('您将删除所有选中的数据？', {
    title: '提示',
    btn: [
      {
        text: '确定',
        callback: (id: any) => {
          let idList: Array<string> = []
          if (item && item.id) {
            idList.push(item.id);
          }
          if (idList && idList.length > 0) {
            deleteCommonTodo(idList).then((res: any) => {
              if (res.code === 200) {
                layer.msg("删除成功", {icon: 1, time: 2000})
                loadDataSource();
              }
            })
          }
          layer.close(id)
        }
      },
      {
        text: '取消',
        callback: (id: any) => {
          layer.close(id)
        }
      }
    ]
  })
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
