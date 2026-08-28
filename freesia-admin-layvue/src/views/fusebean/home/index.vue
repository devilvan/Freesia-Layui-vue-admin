<template>
  <lay-container :fluid="true" class="fusebean-page">
    <lay-card class="hero-card">
      <div class="hero-content">
        <div>
          <div class="hero-title">拼豆图纸生成</div>
          <div class="hero-desc">
            按照「上传图片与提示词 - 生成拼豆像素风预览 - 确认输出图纸」的流程完成生成，并支持坐标标注、MARD 291 色码和原图对比。
          </div>
        </div>
        <div class="hero-note">
          右侧参数会直接影响预览结果。建议先用默认参数生成，再微调背景、翻转和处理模式。
        </div>
      </div>
    </lay-card>

    <lay-card class="workflow-card">
      <lay-step :active="activeStep" current-status="primary" center>
        <lay-step-item title="第一步" content="上传图片与提示词"></lay-step-item>
        <lay-step-item title="第二步" content="生成拼豆像素风预览"></lay-step-item>
        <lay-step-item title="第三步" content="确认生成拼豆图纸"></lay-step-item>
      </lay-step>
    </lay-card>

    <lay-card v-if="activeStep === 0" class="content-card">
      <div class="step-panel">
        <div class="panel-header">
          <div>
            <div class="panel-title">上传图片与提示词</div>
            <div class="panel-desc">
              上传原图，或直接输入更详细的提示词。进入下一步后，可在右侧继续调整处理模式、背景和翻转参数。
            </div>
          </div>
        </div>

        <lay-form class="fusebean-form" label-position="top" label-width="auto">
          <lay-row :space="20">
            <lay-col :md="12">
              <lay-form-item label="输入提示词">
                <lay-textarea
                    v-model="prompt"
                    placeholder="例如：生成一个拼豆像素风头像，保留蓝色主色调，背景简洁，五官清晰"
                    :rows="7"
                ></lay-textarea>
              </lay-form-item>
            </lay-col>
            <lay-col :md="12">
              <lay-form-item label="上传图片">
                <div class="upload-row">
                  <lay-button size="sm" type="primary" @click="chooseImage">
                    <lay-icon class="layui-icon-upload"></lay-icon>
                    上传图片
                  </lay-button>
                  <span class="upload-tip">选择图片后会显示原图预览，支持继续修改参数。</span>
                </div>
                <input
                    ref="fileInputRef"
                    type="file"
                    accept="image/*"
                    style="display: none"
                    @change="onFileChange"
                />
                <div v-if="sourceImageUrl" class="source-preview">
                  <img :src="sourceImageUrl" alt="原图预览" class="source-img"/>
                  <div class="source-meta">
                    <div class="source-name">{{ sourceFile?.name }}</div>
                    <div class="source-size">{{ formatFileSize(sourceFile?.size) }}</div>
                  </div>
                </div>
              </lay-form-item>
            </lay-col>
          </lay-row>

          <div class="step-actions">
            <lay-button size="sm" type="primary" :disabled="generating" @click="handleGenerate">
              <lay-icon class="layui-icon-createtemplate"></lay-icon>
              {{ generating ? '正在生成...' : '下一步生成预览' }}
            </lay-button>
            <span class="step-actions-tip">如果只输入提示词，也可以直接生成预览。</span>
          </div>
        </lay-form>
      </div>
    </lay-card>

    <lay-card v-else-if="activeStep === 1" class="content-card">
      <div class="step-panel step-two-layout">
        <div class="preview-column">
          <div class="panel-header preview-header">
            <div>
              <div class="panel-title">拼豆像素风预览</div>
              <div class="panel-desc">
                预览图上方和左侧会显示坐标，图中会输出 MARD 291 色码。你可以先对比原图，再调整右侧参数重新生成。
              </div>
            </div>
            <div class="panel-actions">
              <lay-button size="sm" @click="backToUpload">返回上一步</lay-button>
              <lay-button size="sm" type="primary" :disabled="!generateResp" @click="openConfirm">
                确认生成图纸
              </lay-button>
            </div>
          </div>

          <template v-if="generateResp">
            <div class="preview-meta">
              <lay-tag>{{ generateResp.gridWidth }} × {{ generateResp.gridHeight }}</lay-tag>
              <lay-tag>共 {{ generateResp.palette?.length || 0 }} 色</lay-tag>
              <lay-tag>{{ processingModeLabel }}</lay-tag>
              <lay-tag v-if="removeBackground">去背景</lay-tag>
              <lay-tag v-if="flipHorizontal">水平翻转</lay-tag>
            </div>

            <div class="compare-toolbar">
              <div class="compare-title">原图对比</div>
              <lay-switch v-model="compareMode" onswitch-text="对比模式" unswitch-text="单图模式"></lay-switch>
            </div>

            <div v-if="compareMode" class="compare-grid">
              <div class="compare-item">
                <div class="sub-title">原图</div>
                <div class="compare-box">
                  <img v-if="sourceImageUrl" :src="sourceImageUrl" alt="原图" class="compare-img"/>
                  <div v-else class="empty-compare">未上传图片</div>
                </div>
              </div>
              <div class="compare-item">
                <div class="sub-title">拼豆像素风预览</div>
                <div class="compare-box preview-box">
                  <img :src="generateResp.previewBase64" alt="拼豆像素风预览" class="preview-img"/>
                </div>
              </div>
            </div>
            <div v-else class="single-preview">
              <div class="sub-title">拼豆像素风预览</div>
              <div class="compare-box preview-box">
                <img :src="generateResp.previewBase64" alt="拼豆像素风预览" class="preview-img"/>
              </div>
            </div>

            <div class="coord-hint">
              横坐标显示在上方，纵坐标显示在左侧。坐标字号会随图纸尺寸自动缩放。
            </div>

            <div class="summary-box">
              <div class="summary-title">本次生成摘要</div>
              <div class="summary-list">
                <div class="summary-item">
                  <span class="summary-label">原图</span>
                  <span class="summary-value">{{ sourceFile?.name || '未上传文件' }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">提示词</span>
                  <span class="summary-value">{{ prompt.trim() || '未填写' }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">生成说明</span>
                  <span class="summary-value">{{ generateResp.message || '本地生成完成' }}</span>
                </div>
              </div>
            </div>
          </template>

          <div v-else class="empty-state">
            当前没有预览结果，请返回上一步重新生成。
          </div>
        </div>

        <aside class="settings-column">
          <div class="settings-card">
            <div class="panel-header settings-header">
              <div>
                <div class="panel-title">调参面板</div>
                <div class="panel-desc">修改参数后点击重新生成，预览和图纸都会同步更新。</div>
              </div>
            </div>

            <lay-form class="fusebean-form settings-form" label-position="top" label-width="auto">
              <lay-row :space="16">
                <lay-col :md="12">
                  <lay-form-item label="处理模式">
                    <lay-select
                        v-model="processingMode"
                        :items="processingModeOptions"
                        :options="processingModeOptions"
                        style="width: 100%"
                    ></lay-select>
                  </lay-form-item>
                </lay-col>
                <lay-col :md="12">
                  <lay-form-item label="豆板格数（最长边）">
                    <lay-input v-model="gridSize" type="number" :min="16" :max="192"></lay-input>
                  </lay-form-item>
                </lay-col>
              </lay-row>

              <lay-row :space="16">
                <lay-col :md="12">
                  <lay-form-item label="颜色上限">
                    <lay-input v-model="maxColors" type="number" :min="1" :max="291"></lay-input>
                  </lay-form-item>
                </lay-col>
                <lay-col :md="12">
                  <lay-form-item label="作品名称（可选）">
                    <lay-input
                        v-model="patternName"
                        placeholder="用于确认生成后的文件命名"
                    ></lay-input>
                  </lay-form-item>
                  <div class="settings-actions">
                    <lay-button size="sm" type="primary" :disabled="generating" @click="handleGenerate">
                      <lay-icon class="layui-icon-refresh"></lay-icon>
                      {{ generating ? '正在重新生成...' : '重新生成预览' }}
                    </lay-button>
                  </div>
                </lay-col>
              </lay-row>

              <lay-row :space="16">
                <lay-col :md="12">
                  <lay-form-item label="去除图片背景">
                    <lay-switch v-model="removeBackground"></lay-switch>
                  </lay-form-item>
                </lay-col>
                <lay-col :md="12">
                  <lay-form-item label="水平翻转">
                    <lay-switch v-model="flipHorizontal"></lay-switch>
                  </lay-form-item>
                </lay-col>
              </lay-row>

              <div class="settings-note">
                处理模式说明: edge 轮廓增强, average 自然平均, dominant 纯色块。
              </div>
            </lay-form>
          </div>
        </aside>
      </div>
    </lay-card>

    <lay-card v-else class="content-card">
      <div class="step-panel">
        <div class="panel-header">
          <div>
            <div class="panel-title">拼豆图纸结果</div>
            <div class="panel-desc">
              PNG 和 SVG 都包含坐标与编码。SVG 适合进一步编辑，PNG 适合快速打印或查看。
            </div>
          </div>
          <div class="panel-actions">
            <lay-button size="sm" @click="backToPreview">返回预览</lay-button>
            <lay-button size="sm" type="primary" @click="backToUpload">重新生成</lay-button>
          </div>
        </div>

        <template v-if="confirmResp">
          <div class="result-meta">
            <lay-tag>{{ confirmResp.gridWidth }} × {{ confirmResp.gridHeight }}</lay-tag>
            <lay-tag>共 {{ confirmResp.colorStats?.length || 0 }} 种颜色</lay-tag>
            <lay-tag>单元格 {{ confirmResp.cellSize }} px</lay-tag>
          </div>

          <div class="result-grid">
            <div>
              <div class="sub-title">图纸 PNG</div>
              <div class="pattern-box">
                <img :src="confirmResp.patternPngBase64" alt="拼豆图纸 PNG" class="pattern-img"/>
              </div>
            </div>
            <div>
              <div class="sub-title">图纸 SVG（可缩放编辑）</div>
              <div class="svg-box" v-html="confirmResp.patternSvg"></div>
            </div>
          </div>

          <div class="sub-title color-title">颜色清单</div>
          <div class="color-stat-list">
            <div v-for="stat in confirmResp.colorStats" :key="stat.index" class="color-stat-item">
              <span class="color-index">{{ stat.code || `#${stat.index}` }}</span>
              <span class="color-swatch" :style="{ background: stat.hex }"></span>
              <span class="color-hex">{{ stat.hex }}</span>
              <span class="color-count">{{ stat.count }} 颗</span>
            </div>
          </div>

          <div class="download-row">
            <lay-button size="sm" border="green" @click="downloadPng">
              <lay-icon class="layui-icon-download-circle"></lay-icon>
              下载 PNG
            </lay-button>
            <lay-button size="sm" border="green" @click="downloadSvg">
              <lay-icon class="layui-icon-download-circle"></lay-icon>
              下载 SVG
            </lay-button>
          </div>
        </template>

        <div v-else class="empty-state">
          还没有生成最终图纸，请先返回第二步进行确认。
        </div>
      </div>
    </lay-card>
  </lay-container>
</template>

<script lang="ts">
export default {
  name: 'FuseBeanHome',
};
</script>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue';
import { layer } from '@layui/layui-vue';
import { confirmGenerate, generateImage, FuseBeanGenerateOptions } from '@/api/fusebean/FuseBean';
import { FuseBeanConfirmResp, FuseBeanGenerateResp } from '@/types/fusebean/FuseBean';

const prompt = ref('');
const patternName = ref('');
const gridSize = ref<number>(50);
const maxColors = ref<number>(18);
const processingMode = ref<'edge' | 'average' | 'dominant'>('edge');
const removeBackground = ref(true);
const flipHorizontal = ref(false);
const compareMode = ref(true);

const fileInputRef = ref<HTMLInputElement>();
const sourceFile = ref<File | null>(null);
const sourceImageUrl = ref('');

const activeStep = ref(0);
const generating = ref(false);
const generateResp = ref<FuseBeanGenerateResp | null>(null);
const confirmResp = ref<FuseBeanConfirmResp | null>(null);

const processingModeOptions = [
  { value: 'edge', label: 'edge - 轮廓增强' },
  { value: 'average', label: 'average - 自然平均' },
  { value: 'dominant', label: 'dominant - 纯色块' },
];

const processingModeLabel = computed(() => {
  return processingModeOptions.find(item => item.value === processingMode.value)?.label ?? 'edge - 轮廓增强';
});

function chooseImage() {
  fileInputRef.value?.click();
}

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) {
    return;
  }
  if (!file.type.startsWith('image/')) {
    layer.msg('请选择图片文件', { icon: 2 });
    input.value = '';
    return;
  }
  clearSourceUrl();
  sourceFile.value = file;
  sourceImageUrl.value = URL.createObjectURL(file);
  input.value = '';
}

function clearSourceUrl() {
  if (sourceImageUrl.value) {
    URL.revokeObjectURL(sourceImageUrl.value);
  }
  sourceFile.value = null;
  sourceImageUrl.value = '';
}

function backToUpload() {
  activeStep.value = 0;
}

function backToPreview() {
  if (generateResp.value) {
    activeStep.value = 1;
  } else {
    activeStep.value = 0;
  }
}

function clampNumber(value: number, min: number, max: number, fallback: number) {
  if (!Number.isFinite(value)) {
    return fallback;
  }
  return Math.min(max, Math.max(min, Math.trunc(value)));
}

function formatFileSize(size?: number) {
  if (!size || size <= 0) {
    return '';
  }
  if (size < 1024) {
    return `${size} B`;
  }
  if (size < 1024 * 1024) {
    return `${(size / 1024).toFixed(1)} KB`;
  }
  return `${(size / (1024 * 1024)).toFixed(1)} MB`;
}

function sanitizeDownloadName(value: string) {
  const name = value.trim().replace(/[<>:"/\\|?*\u0000-\u001F]/g, '_');
  return name || 'pindou';
}

function buildGenerateOptions(): FuseBeanGenerateOptions {
  return {
    processingMode: processingMode.value,
    removeBackground: removeBackground.value,
    flipHorizontal: flipHorizontal.value,
  };
}

async function handleGenerate() {
  if (!sourceFile.value && !prompt.value.trim()) {
    layer.msg('请先上传图片或输入提示词', { icon: 2 });
    return;
  }
  generating.value = true;
  try {
    const normalizedGridSize = clampNumber(Number(gridSize.value), 16, 192, 50);
    const normalizedMaxColors = clampNumber(Number(maxColors.value), 1, 291, 18);
    gridSize.value = normalizedGridSize;
    maxColors.value = normalizedMaxColors;

    const res = await generateImage(
        sourceFile.value,
        prompt.value.trim(),
        normalizedGridSize,
        normalizedMaxColors,
        buildGenerateOptions()
    );
    if (res.code === 200) {
      generateResp.value = res.data || null;
      confirmResp.value = null;
      activeStep.value = 1;
      compareMode.value = true;
      layer.msg('预览生成成功', { icon: 1 });
    } else if (res.code !== 500) {
      layer.msg(res.msg || '生成失败', { icon: 2 });
    }
  } catch (e: any) {
    layer.msg(e?.msg || '生成失败，请稍后重试', { icon: 2 });
  } finally {
    generating.value = false;
  }
}

function openConfirm() {
  const preview = generateResp.value?.previewBase64;
  if (!preview) {
    layer.msg('请先生成预览图', { icon: 2 });
    return;
  }
  layer.open({
    type: 1,
    title: '确认生成拼豆图纸',
    content: `<div style="padding: 14px; text-align: center">
        <img src="${preview}" style="max-width: 360px; max-height: 360px; border: 1px solid #e8eef8; border-radius: 8px; background: #fff" />
        <div style="margin-top: 10px; color: #666; font-size: 13px">确认后将生成包含坐标和 MARD 色码的拼豆图纸</div>
      </div>`,
    shade: true,
    isHtmlFragment: true,
    area: '460px',
    btn: [
      {
        text: '确认生成',
        callback(id: string) {
          layer.close(id);
          doConfirmGenerate();
        },
      },
      {
        text: '取消',
        callback(id: string) {
          layer.close(id);
        },
      },
    ],
  });
}

async function doConfirmGenerate() {
  const resp = generateResp.value;
  if (!resp) {
    layer.msg('请先生成预览图', { icon: 2 });
    return;
  }
  try {
    const res = await confirmGenerate({
      name: patternName.value.trim(),
      gridWidth: resp.gridWidth,
      gridHeight: resp.gridHeight,
      cellSize: 14,
      palette: resp.palette,
      grid: resp.grid,
    });
    if (res.code === 200) {
      confirmResp.value = res.data || null;
      activeStep.value = 2;
      layer.msg('拼豆图纸生成成功', { icon: 1 });
    } else if (res.code !== 500) {
      layer.msg(res.msg || '生成失败', { icon: 2 });
    }
  } catch (e: any) {
    layer.msg(e?.msg || '生成失败，请稍后重试', { icon: 2 });
  }
}

function downloadPng() {
  const dataUrl = confirmResp.value?.patternPngBase64;
  if (!dataUrl) {
    layer.msg('暂无可下载的 PNG 文件', { icon: 2 });
    return;
  }
  const a = document.createElement('a');
  a.href = dataUrl;
  a.download = `${sanitizeDownloadName(patternName.value)}-pattern.png`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}

function downloadSvg() {
  const svg = confirmResp.value?.patternSvg;
  if (!svg) {
    layer.msg('暂无可下载的 SVG 文件', { icon: 2 });
    return;
  }
  const blob = new Blob([svg], { type: 'image/svg+xml;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${sanitizeDownloadName(patternName.value)}-pattern.svg`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

onBeforeUnmount(() => {
  if (sourceImageUrl.value) {
    URL.revokeObjectURL(sourceImageUrl.value);
  }
});
</script>

<style scoped>
.fusebean-page {
  padding-bottom: 16px;
}

.hero-card {
  margin-bottom: 14px;
  border: 1px solid #e8eef8;
  background: linear-gradient(135deg, #f7fbff 0%, #ffffff 56%, #f5f7ff 100%);
}

.hero-content {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.hero-title {
  font-size: 22px;
  font-weight: 700;
  color: #162033;
  line-height: 1.2;
}

.hero-desc {
  margin-top: 8px;
  max-width: 760px;
  color: #5f6b85;
  line-height: 1.7;
}

.hero-note {
  max-width: 320px;
  padding: 12px 14px;
  border-radius: 10px;
  background: rgba(22, 32, 51, 0.04);
  color: #41506d;
  font-size: 13px;
  line-height: 1.65;
}

.workflow-card {
  margin-bottom: 14px;
}

.content-card {
  min-height: 520px;
}

.step-panel {
  min-height: 420px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.panel-title {
  font-size: 18px;
  font-weight: 700;
  color: #162033;
}

.panel-desc {
  margin-top: 6px;
  color: #5f6b85;
  font-size: 13px;
  line-height: 1.65;
}

.panel-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.upload-tip,
.step-actions-tip {
  display: block;
  color: #7d869f;
  font-size: 12px;
  line-height: 1.5;
}

.source-preview {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #edf1f7;
  border-radius: 10px;
  background: #fff;
}

.source-img {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border: 1px solid #edf1f7;
  border-radius: 8px;
  background: #f8fafc;
}

.source-meta {
  min-width: 0;
}

.source-name {
  color: #162033;
  font-size: 13px;
  font-weight: 600;
  word-break: break-all;
}

.source-size {
  margin-top: 4px;
  color: #7d869f;
  font-size: 12px;
}

.step-actions {
  margin-top: 8px;
  width: 100%;
}

.step-actions-tip {
  margin-top: 8px;
}

.step-two-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.7fr);
  gap: 18px;
}

.preview-column {
  min-width: 0;
}

.settings-column {
  min-width: 0;
}

.settings-card {
  padding: 6px 0 0;
}

.preview-header,
.settings-header {
  margin-bottom: 14px;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.compare-toolbar {
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.compare-title {
  font-size: 14px;
  font-weight: 600;
  color: #162033;
}

.compare-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.compare-item {
  min-width: 0;
}

.sub-title {
  margin-bottom: 10px;
  color: #4f5b76;
  font-size: 13px;
  font-weight: 600;
}

.compare-box,
.pattern-box,
.svg-box {
  border: 1px solid #edf1f7;
  border-radius: 12px;
  background: #fff;
}

.compare-box {
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}

.preview-box {
  align-items: flex-start;
}

.compare-img,
.preview-img,
.pattern-img {
  width: 100%;
  height: auto;
  max-height: 640px;
  object-fit: contain;
}

.coord-hint {
  margin-top: 12px;
  color: #7d869f;
  font-size: 12px;
  line-height: 1.6;
}

.summary-box {
  margin-top: 14px;
  padding: 18px;
  border: 1px solid #edf1f7;
  border-radius: 12px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfcff 100%);
}

.summary-title {
  font-size: 15px;
  font-weight: 700;
  color: #162033;
}

.summary-list {
  margin-top: 14px;
  display: grid;
  gap: 10px;
}

.summary-item {
  display: grid;
  gap: 4px;
}

.summary-label {
  color: #7d869f;
  font-size: 12px;
}

.summary-value {
  color: #162033;
  font-size: 13px;
  font-weight: 600;
  word-break: break-all;
}

.settings-form :deep(.layui-form-item-top > .layui-form-label) {
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

.settings-note {
  margin-top: 10px;
  color: #7d869f;
  font-size: 12px;
  line-height: 1.6;
}

.settings-actions {
  margin-top: 14px;
  display: flex;
  justify-content: flex-start;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 240px;
  color: #7d869f;
  font-size: 13px;
  text-align: center;
  border: 1px dashed #dbe3ef;
  border-radius: 12px;
  background: #fbfcff;
}

.result-meta {
  margin-bottom: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.result-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
  gap: 18px;
  align-items: start;
}

.pattern-box {
  padding: 14px;
}

.svg-box {
  max-height: 540px;
  padding: 14px;
  overflow: auto;
}

.svg-box :deep(svg) {
  max-width: 100%;
  height: auto;
}

.color-title {
  margin-top: 20px;
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
  border: 1px solid #edf1f7;
  border-radius: 10px;
  padding: 8px 10px;
  background: #fff;
}

.color-index {
  min-width: 30px;
  color: #7d869f;
  font-size: 12px;
  font-weight: 600;
}

.color-swatch {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  border: 1px solid #d9e1ee;
}

.color-hex {
  color: #162033;
  font-family: monospace;
  font-size: 13px;
}

.color-count {
  color: #7d869f;
  font-size: 12px;
}

.download-row {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
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

@media (max-width: 1100px) {
  .step-two-layout,
  .compare-grid,
  .result-grid {
    grid-template-columns: 1fr;
  }

  .panel-header,
  .compare-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
  }

  .compare-box {
    min-height: 240px;
  }

  .download-row,
  .panel-actions {
    justify-content: flex-start;
  }
}
</style>
