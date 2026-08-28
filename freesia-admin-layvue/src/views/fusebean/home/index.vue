<template>
  <lay-container :fluid="true">
    <!-- 生成拼豆像素风图片 -->
    <lay-card title="拼豆项目 - 生成拼豆像素风图片">
      <lay-form class="fusebean-form" label-position="top" label-width="auto" style="max-width: 720px">
        <lay-form-item label="输入提示词">
          <lay-textarea
              v-model="prompt"
              placeholder="请输入更详细的提示词，例如：生成一个拼豆像素风的图片，拼豆像素风的图片中包含一个拼豆像素风的图片"
              :rows="3"
          ></lay-textarea>
        </lay-form-item>

        <lay-form-item label="上传图片">
          <div class="upload-row">
            <lay-button size="sm" type="primary" @click="chooseImage">
              <lay-icon class="layui-icon-upload"></lay-icon>
              上传图片
            </lay-button>
            <span class="upload-tip">点击选择图片，选择后点击「生成拼豆像素风图片」即可上传并生成</span>
          </div>
          <input
              ref="fileInputRef"
              type="file"
              accept="image/*"
              style="display: none"
              @change="onFileChange"
          />
          <div v-if="sourceImageUrl" class="source-preview">
            <img :src="sourceImageUrl" alt="原图" class="source-img"/>
            <span class="source-file-name">{{ sourceFile?.name }}</span>
          </div>
        </lay-form-item>

        <lay-row :space="20">
          <lay-col :md="6">
            <lay-form-item label="图纸格数（最大边长）">
              <lay-input v-model="gridSize" type="number" :min="8" :max="200"></lay-input>
            </lay-form-item>
          </lay-col>
          <lay-col :md="6">
            <lay-form-item label="最大颜色数">
              <lay-input v-model="maxColors" type="number" :min="2" :max="64"></lay-input>
            </lay-form-item>
          </lay-col>
        </lay-row>

        <lay-form-item>
          <lay-button size="sm" type="primary" :disabled="generating" @click="handleGenerate">
            <lay-icon class="layui-icon-createtemplate"></lay-icon>
            {{ generating ? '生成中...' : '生成拼豆像素风图片' }}
          </lay-button>
          <lay-button size="sm" v-if="sourceImageUrl" @click="clearSource">清除图片</lay-button>
        </lay-form-item>
      </lay-form>

      <template v-if="generateResp">
        <div class="section-title">生成的拼豆像素风图片</div>
        <div class="preview-box">
          <img :src="generateResp.previewBase64" alt="拼豆像素风图片" class="preview-img"/>
        </div>
        <div class="preview-meta">
          <lay-tag>{{ generateResp.gridWidth }} × {{ generateResp.gridHeight }}</lay-tag>
          <lay-tag>共 {{ generateResp.palette?.length || 0 }} 色</lay-tag>
          <span class="preview-message">{{ generateResp.message }}</span>
        </div>
      </template>
    </lay-card>

    <!-- 确认生成拼豆图纸 -->
    <lay-card v-if="generateResp" title="拼豆项目 - 生成拼豆图纸">
      <lay-form class="fusebean-form" label-position="top" label-width="auto" style="max-width: 720px">
        <lay-form-item label="作品名称（可选）">
          <lay-input v-model="patternName" placeholder="请输入作品名称，将显示在图纸图例与文件名中"></lay-input>
        </lay-form-item>
        <lay-form-item>
          <lay-button size="sm" type="primary" @click="openConfirm">
            <lay-icon class="layui-icon-template-1"></lay-icon>
            确认生成拼豆图纸
          </lay-button>
        </lay-form-item>
      </lay-form>

      <template v-if="confirmResp">
        <div class="section-title">拼豆图纸</div>
        <div class="result-grid">
          <div>
            <div class="sub-title">网格图纸</div>
            <div class="pattern-box">
              <img :src="confirmResp.patternPngBase64" alt="拼豆图纸" class="pattern-img"/>
            </div>
          </div>
          <div>
            <div class="sub-title">矢量图（SVG，可编辑缩放）</div>
            <div class="svg-box" v-html="confirmResp.patternSvg"></div>
          </div>
        </div>

        <div class="section-title">色号清单（购豆清单）</div>
        <div class="color-stat-list">
          <div
              v-for="stat in confirmResp.colorStats"
              :key="stat.index"
              class="color-stat-item"
          >
            <span class="color-index">{{ stat.code || `#${stat.index}` }}</span>
            <span class="color-swatch" :style="{background: stat.hex}"></span>
            <span class="color-hex">{{ stat.hex }}</span>
            <span class="color-count">{{ stat.count }} 颗</span>
          </div>
        </div>

        <div class="download-row">
          <lay-button size="sm" border="green" @click="downloadPng">
            <lay-icon class="layui-icon-download-circle"></lay-icon>
            下载图纸 PNG
          </lay-button>
          <lay-button size="sm" border="green" @click="downloadSvg">
            <lay-icon class="layui-icon-download-circle"></lay-icon>
            下载图纸 SVG
          </lay-button>
        </div>
      </template>
    </lay-card>
  </lay-container>
</template>
<script lang="ts">
/**
 * 创建组件时要添加name，否则在使用keep-alive时就会失效
 */
export default {
  name: "FuseBeanHome",
};
</script>
<script setup lang="ts">
import {ref} from 'vue'
import {layer} from '@layui/layui-vue'
import {confirmGenerate, generateImage} from "@/api/fusebean/FuseBean";
import {FuseBeanConfirmResp, FuseBeanGenerateResp} from "@/types/fusebean/FuseBean";

const prompt = ref('')
const gridSize = ref<number>(50)
const maxColors = ref<number>(18)
const patternName = ref('')

const fileInputRef = ref<HTMLInputElement>()
const sourceFile = ref<File | null>(null)
const sourceImageUrl = ref('')

const generating = ref(false)
const generateResp = ref<FuseBeanGenerateResp | null>(null)
const confirmResp = ref<FuseBeanConfirmResp | null>(null)

function chooseImage() {
  fileInputRef.value?.click()
}

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) {
    return
  }
  if (!file.type.startsWith('image/')) {
    layer.msg('请选择图片文件', {icon: 2})
    return
  }
  if (sourceImageUrl.value) {
    URL.revokeObjectURL(sourceImageUrl.value)
  }
  sourceFile.value = file
  sourceImageUrl.value = URL.createObjectURL(file)
  input.value = ''
}

function clearSource() {
  if (sourceImageUrl.value) {
    URL.revokeObjectURL(sourceImageUrl.value)
  }
  sourceFile.value = null
  sourceImageUrl.value = ''
}

async function handleGenerate() {
  if (!sourceFile.value && !prompt.value.trim()) {
    layer.msg('请上传图片或输入提示词', {icon: 2})
    return
  }
  generating.value = true
  try {
    const res = await generateImage(sourceFile.value, prompt.value, gridSize.value, maxColors.value)
    if (res.code === 200) {
      generateResp.value = res.data || null
      confirmResp.value = null
      layer.msg('生成成功', {icon: 1})
    } else if (res.code !== 500) {
      // code 500 已由 Http 拦截器统一弹窗提示
      layer.msg(res.msg || '生成失败', {icon: 2})
    }
  } catch (e: any) {
    layer.msg(e?.msg || '生成失败，请稍后重试', {icon: 2})
  } finally {
    generating.value = false
  }
}

function openConfirm() {
  const preview = generateResp.value?.previewBase64
  if (!preview) {
    return
  }
  layer.open({
    type: 1,
    title: '确认拼豆像素风图片',
    content: `<div style="padding: 14px; text-align: center">
        <img src="${preview}" style="max-width: 360px; max-height: 360px; border: 1px solid #eee; border-radius: 4px"/>
        <div style="margin-top: 8px; color: #999; font-size: 12px">确认后生成拼豆图纸</div>
      </div>`,
    shade: true,
    isHtmlFragment: true,
    area: '420px',
    btn: [
      {
        text: '确认生成',
        callback: (index: number) => {
          layer.close(index)
          doConfirmGenerate()
        }
      },
      {
        text: '取消',
        callback: (index: number) => {
          layer.close(index)
        }
      }
    ]
  })
}

async function doConfirmGenerate() {
  const resp = generateResp.value
  if (!resp) {
    return
  }
  try {
    const res = await confirmGenerate({
      name: patternName.value,
      gridWidth: resp.gridWidth,
      gridHeight: resp.gridHeight,
      cellSize: 14,
      palette: resp.palette,
      grid: resp.grid
    })
    if (res.code === 200) {
      confirmResp.value = res.data || null
      layer.msg('拼豆图纸生成成功', {icon: 1})
    } else if (res.code !== 500) {
      layer.msg(res.msg || '生成失败', {icon: 2})
    }
  } catch (e: any) {
    layer.msg(e?.msg || '生成失败，请稍后重试', {icon: 2})
  }
}

function downloadPng() {
  const dataUrl = confirmResp.value?.patternPngBase64
  if (!dataUrl) {
    return
  }
  const a = document.createElement('a')
  a.href = dataUrl
  a.download = `${patternName.value || 'pindou'}-pattern.png`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

function downloadSvg() {
  const svg = confirmResp.value?.patternSvg
  if (!svg) {
    return
  }
  const blob = new Blob([svg], {type: 'image/svg+xml;charset=utf-8'})
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${patternName.value || 'pindou'}-pattern.svg`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.upload-tip {
  color: #999;
  font-size: 12px;
}

.source-preview {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.source-img {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border: 1px solid #eee;
  border-radius: 4px;
}

.source-file-name {
  color: #666;
  font-size: 13px;
  word-break: break-all;
}

.section-title {
  margin: 16px 0 12px;
  font-weight: 600;
  color: #333;
}

.sub-title {
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}

.preview-box,
.pattern-box {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 12px;
  display: inline-block;
  background: #fff;
}

.preview-img {
  max-width: 480px;
  width: 100%;
  image-rendering: pixelated;
}

.preview-meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-message {
  color: #999;
  font-size: 12px;
}

.result-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.pattern-img {
  max-width: 520px;
  width: 100%;
  image-rendering: pixelated;
}

.svg-box {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 12px;
  background: #fff;
  max-width: 520px;
  max-height: 560px;
  overflow: auto;
}

.svg-box :deep(svg) {
  max-width: 100%;
  height: auto;
}

.color-stat-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.color-stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 6px 10px;
  background: #fff;
}

.color-index {
  min-width: 28px;
  font-size: 12px;
  color: #666;
  font-weight: 600;
}

.color-swatch {
  width: 18px;
  height: 18px;
  border-radius: 3px;
  border: 1px solid #ddd;
}

.color-hex {
  font-family: monospace;
  font-size: 13px;
  color: #333;
}

.color-count {
  font-size: 12px;
  color: #999;
}

.download-row {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.fusebean-form :deep(.layui-form-item-top > .layui-form-label) {
  width: 100% !important;
  min-width: 0;
  box-sizing: border-box;
  padding: 0 0 6px;
  white-space: normal;
  overflow: visible;
  text-overflow: clip;
  line-height: 1.4;
  height: auto;
}
</style>
