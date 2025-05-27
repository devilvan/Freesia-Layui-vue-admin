<template>
  <div style="height: 100%; width: 100%">
    <div style="height: calc(100% - 60px); width: 100%; overflow: auto">
      <lay-container :fluid="true">
        <lay-form label-position="top">
          <lay-card shadow="hover">
            <lay-row :space="20">
              <lay-col :md="5">
                <lay-form-item label="文件名称" label-width="80">
                  <lay-input
                      v-model="searchQuery.flieName"
                      placeholder="请输入"
                      size="sm"
                      :allow-clear="true"
                      style="width: 100%"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="5">
                <lay-form-item label="文件路径" label-width="80">
                  <lay-input
                      v-model="searchQuery.filePath"
                      placeholder="请输入"
                      size="sm"
                      :allow-clear="true"
                      style="width: 100%"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
              <lay-col :md="5">
                <lay-form-item label="上传人" label-width="80">
                  <lay-input
                      v-model="searchQuery.uploadUser"
                      placeholder="请输入"
                      size="sm"
                      :allow-clear="true"
                      style="width: 100%"
                  ></lay-input>
                </lay-form-item>
              </lay-col>
            </lay-row>
          </lay-card>
        </lay-form>
        <!-- table -->
        <div>
          <lay-table
              :page="pageQuery"
              :columns="columns"
              :loading="loading"
              :default-toolbar="true"
              :data-source="dataSource"
              v-model:selected-keys="selectedKeys"
              @change="change"
              @sortChange="sortChange"
          >
            <template #originalName="{ row }">
              <lay-tooltip
                  :visible="false"
                  trigger="hover"
                  :content="row.originalName"
              >
                <div>{{ row.originalName }}</div>
              </lay-tooltip>
            </template>
            <template #fileName="{ row }">
              <lay-tooltip
                  :visible="false"
                  trigger="hover"
                  :content="row.fileName"
              >
                <div>{{ row.fileName }}</div>
              </lay-tooltip>
            </template>
            <template #fileSize="{ row }">
              <lay-tooltip
                  :visible="false"
                  trigger="hover"
                  :content="row.fileSize"
              >
                <div>{{ formatFileSize(row.fileSize) }}</div>
              </lay-tooltip>
            </template>
            <template #url="{ row }">
              <lay-avatar v-if="pictureType.includes(row.fileSuffix)" :src="row.url" @click="preview(row)"
                          size="lg"></lay-avatar>
            </template>

            <template v-slot:toolbar>
              <lay-button type="normal" size="sm" @click="toSearch"
                          v-permission="[$MENU_PERMISSION.SYSTEM_OSS_INDEX]">查询
              </lay-button>
              <lay-button size="sm" @click="toReset"> 重置</lay-button>
              <lay-button size="sm" type="primary" @click="toImport">
                <lay-icon class="layui-icon-upload-drag"></lay-icon>
                上传
              </lay-button>
              <lay-button size="sm" @click="toRemove" v-permission="[$MENU_PERMISSION.SYSTEM_OSS_DELETE]">
                <lay-icon class="layui-icon-delete"></lay-icon>
                删除
              </lay-button>
            </template>
            <template v-slot:operator="{ row }">
              <lay-button
                  size="xs"
                  border="green"
                  border-style="dashed"
                  @click="toDownload(row)"
                  v-permission="[$MENU_PERMISSION.SYSTEM_OSS_DOWNLOAD]"
              >下载
              </lay-button
              >
              <lay-popconfirm
                  content="确定要删除此文件吗?"
                  @confirm="confirm(row)"
                  @cancel="cancel"
                  v-permission="[$MENU_PERMISSION.SYSTEM_OSS_DELETE]"
              >
                <lay-button size="xs" border="red" border-style="dashed"
                >删除
                </lay-button
                >
              </lay-popconfirm>
            </template>
          </lay-table>
        </div>

        <lay-layer
            v-model="visibleImport"
            title="导入文件"
            :area="['380px', '450px']"
        >
          <lay-upload
              style="margin: 60px"
              :url="ossPath"
              v-model="fileList"
              field="file"
              :auto="false"
              :drag="true"
              :multiple="true"
          >
            <template #preview>
              <div v-if="fileList.length > 0" v-for="(file, index) in fileList">
                {{ file.name }}
              </div>
            </template>
          </lay-upload>
          <div style="width: 100%; text-align: center">
            只能上传小于10M的文件
          </div>
          <div style="width: 100%;margin-top: 20px; text-align: center">
            <lay-button size="sm" type="primary" @click="toUpload()">上传</lay-button>
          </div>
        </lay-layer>
      </lay-container>
    </div>
  </div>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "Oss",
};
</script>
<script setup lang="ts">
import {ref, reactive, onMounted} from 'vue'
import {layer} from '@layui/layui-vue'
import {TableResult} from "@/types/Result";
import {SysOssEntity} from "@/types/system/Oss";
import {deleteSysOss, findPageSysOss, upload} from "@/api/system/Oss";
import {PageQuery} from "@/types/Common";
import Http from "@/api/Http";
import app from "@/main";
import {formatFileSize} from "../../../util/UFile";

/* INIT*/
onMounted(() => {
  console.log(pubKey);
  loadDataSource()
})
/* INIT*/

/* VAR*/
const pubKey = app.config.globalProperties.$PUB_KEY;
const ossPath = import.meta.env.VITE_APP_UPLOAD_PATH
const searchQuery = ref<SysOssEntity>({})
const pictureType = ['jpg', 'jpeg', 'png', 'gif', 'svg'];
const visibleImport = ref(false)
const fileList = ref([])
const loading = ref(false)
const selectedKeys = ref([])
const columns = ref([
  {title: '选项', width: '55px', type: 'checkbox', fixed: 'left'},
  {title: '文件路径', key: 'fileName', width: '120px', customSlot: 'fileName'},
  {title: '原名', key: 'originalName', customSlot: 'originalName'},
  {title: '预览', width: '50px', key: 'url', customSlot: 'url'},
  {title: '文件类型', width: '120px', key: 'fileSuffix'},
  {title: '文件大小', width: '120px', key: 'fileSize', customSlot: 'fileSize'},
  {title: '服务商', width: '120px', key: 'service'},
  {title: '上传人', width: '120px', key: 'creator'},
  {title: '上传时间', width: '220px', key: 'createTime', sort: 'desc'},
  {
    title: '操作',
    width: '150px',
    customSlot: 'operator',
    key: 'operator',
    fixed: 'right'
  }
])
const uploadLimitSize = 10 * 1024 * 1024;
/* VAR*/

/* FUNCTION*/
function toImport() {
  // layer.msg('导入')
  fileList.value = []
  visibleImport.value = true
}

function toReset() {
  searchQuery.value = {
    filePath: '',
    flieName: '',
    uploadUser: ''
  }
}

function toSearch() {
  pageQuery.current = 1
  change()
}

async function toDownload(row: any) {
  await Http.getDownload(row.id)
}

const change = () => {
  loading.value = true
  setTimeout(() => {
    loadDataSource()
    loading.value = false
  }, 1000)
}
const sortChange = (key: any, sort: number) => {
  layer.msg(`字段${key} - 排序${sort}, 你可以利用 sort-change 实现服务端排序`)
}
const dataSource = ref<Array<SysOssEntity>>()
const pageQuery = reactive<PageQuery>({
  current: 1,
  limit: 10
})
const loadDataSource = () => {
  findPageSysOss(searchQuery.value, pageQuery).then((res: TableResult<SysOssEntity>) => {
    if (res.code == 200) {
      pageQuery.total = res.total;
      dataSource.value = res.rows
    } else {
      layer.msg(res.msg)
      return;
    }
  }).catch(e => {
    layer.msg(e.msg)
  });
}

function toRemove() {
  if (selectedKeys.value.length == 0) {
    layer.msg('您未选择数据，请先选择要删除的数据', {icon: 3, time: 2000})
    return
  }
  layer.confirm('您将删除所有选中的数据？', {
    title: '提示',
    btn: [
      {
        text: '确定',
        callback: (id: any) => {
          // layer.msg('您已成功删除')
          deleteSysOss(selectedKeys.value).then((res: any) => {
            if (res.code === 200) {
              layer.msg(res.msg, {icon: 1})
              loadDataSource()
            }
          })
          layer.close(id)
        }
      },
      {
        text: '取消',
        callback: (id: any) => {
          layer.msg('您已取消操作')
          layer.close(id)
        }
      }
    ]
  })
}

function confirm(row) {
  let idList = []
  idList.push(row.id)
  deleteSysOss(idList).then((res: any) => {
    if (res.code === 200) {
      loadDataSource()
    }
  })
}

function cancel() {
  layer.msg('您已取消操作')
}


const doneHandler = (result) => {
  let resultData = JSON.parse(result?.data)
  if (resultData.code === 200) {
    layer.msg(resultData.msg, {icon: 1})
  }
  visibleImport.value = false;
  change()
};

/**
 * 预览图片
 */
function preview(row) {
  let option = {
    imgList: [{src: row.url, alt: row.originalName}]
  };
  layer.photos(option)
}

function toUpload() {
  if (!fileList.value || fileList.value.length < 1) {
    layer.confirm('清选择文件', {icon: 3})
    return;
  }
  upload(fileList.value).then((res: any) => {
    if (res.code === 200) {
      layer.msg(res.msg, {icon: 1})
      visibleImport.value = !visibleImport.value
      loadDataSource()
    }
  }).catch(e => {
    layer.confirm(e.message);
  })
}

/* FUNCTION*/

</script>
