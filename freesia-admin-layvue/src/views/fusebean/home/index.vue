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

<!--          <lay-row :space="20">-->
<!--            <lay-col :md="24">-->
<!--              <lay-form-item label="AI 风格提示词（可选）">-->
<!--                <lay-textarea-->
<!--                    v-model="aiStylePrompt"-->
<!--                    placeholder="例如：重绘成卡图风格 / 水彩插画风 / 复古像素海报，保留主体轮廓与构图"-->
<!--                    :rows="3"-->
<!--                ></lay-textarea>-->
<!--                <div class="ai-hint">-->
<!--                  填写后，将先用 gpt-image-2 按提示词重绘已上传的原图，再生成拼豆像素风预览；留空则直接处理原图。-->
<!--                </div>-->
<!--              </lay-form-item>-->
<!--            </lay-col>-->
<!--          </lay-row>-->

          <lay-row :space="20">
            <lay-col :md="12">
              <lay-form-item label="豆板格数（最长边）">
                <lay-input v-model="gridSize" type="number" :min="16" :max="192"></lay-input>
              </lay-form-item>
            </lay-col>
            <lay-col :md="12">
              <lay-form-item label="颜色上限">
                <lay-input v-model="maxColors" type="number" :min="1" :max="291"></lay-input>
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
                预览图按图片尺寸叠加上坐标系，可直接点击或拖拽框选豆格，再在下方色板中替换颜色。坐标与 MARD 色码会在第三步确认生成图纸时标注。
              </div>
            </div>
          </div>
          <div class="panel-actions">
            <lay-button size="sm" @click="backToUpload">返回上一步</lay-button>
            <lay-button size="sm" :disabled="!sourceImageUrl" title="请先上传原图" @click="openCompare">
              <lay-icon class="layui-icon-picture"></lay-icon>
              对比原图
            </lay-button>
            <lay-button size="sm" type="primary" :disabled="!generateResp" @click="openConfirm">
              确认生成图纸
            </lay-button>
          </div>

          <template v-if="generateResp">
            <div class="preview-meta">
              <lay-tag>{{ generateResp.gridWidth }} × {{ generateResp.gridHeight }}</lay-tag>
              <lay-tag>共 {{ generateResp.palette?.length || 0 }} 色</lay-tag>
              <lay-tag>{{ processingModeLabel }}</lay-tag>
              <lay-tag v-if="removeBackground">去背景</lay-tag>
              <lay-tag v-if="flipHorizontal">水平翻转</lay-tag>
            </div>

            <div class="cell-editor">
              <div class="cell-editor-header">
                <div class="sub-title">豆格颜色调整</div>
                <div class="cell-editor-desc">
                  在预览图中点击或拖拽框选一个或多个豆格，再从下方色板选择颜色替换。色板颜色数由当前颜色上限（{{ maxColors }}）决定；白色（T1）可随时作为底色追加。空豆格（已去除的背景区域）不会被标色。
                </div>
              </div>
              <div class="cell-selection-info">
                <div class="cell-mode-switch">
                  <lay-button size="xs" :type="paintMode ? 'primary' : 'default'" @click="setPaintMode(true)">
                    画笔模式
                  </lay-button>
                  <lay-button size="xs" :type="!paintMode ? 'primary' : 'default'" @click="setPaintMode(false)">
                    框选模式
                  </lay-button>
                </div>
                <div v-if="paintMode" class="paint-tool-switch">
                  <lay-button size="xs" :type="paintTool === 'brush' ? 'primary' : 'default'" @click="setPaintTool('brush')">
                    画笔
                  </lay-button>
                  <lay-button size="xs" :type="paintTool === 'bucket' ? 'primary' : 'default'" @click="setPaintTool('bucket')">
                    油漆桶
                  </lay-button>
                </div>
                <span class="sel-count" v-if="!paintMode">
                  {{ selectedCells.length ? `已选 ${selectedCells.length} 个豆格` : '尚未选择豆格' }}
                </span>
                <lay-button v-if="selectedCells.length && !paintMode" size="xs" border="red" @click="clearSelection">清除选择</lay-button>
              </div>
              <div v-if="generateResp.palette?.length" class="cell-color-picker">
                <div class="picker-swatches">
                  <div
                      v-for="(color, i) in generateResp.palette"
                      :key="color.index ?? i"
                      class="picker-swatch"
                      :class="{ active: lastAppliedColor === i }"
                      :title="`${color.code} · ${color.hex}`"
                      @click="handlePaletteClick(i)"
                  >
                    <span class="swatch-block" :style="{ background: color.hex }"></span>
                    <span class="swatch-code">{{ color.code }}</span>
                  </div>
                  <div
                      v-if="!hasWhitePaletteColor"
                      class="picker-swatch"
                      :class="{ active: lastAppliedColor === whiteSwatchIndex }"
                      title="T1 · #FFFFFF（白色底色）"
                      @click="handleWhitePaletteClick"
                  >
                    <span class="swatch-block white-swatch"></span>
                    <span class="swatch-code">T1</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="single-preview">
              <div class="sub-title">拼豆像素风预览（点击/拖拽框选豆格，Ctrl+滚轮缩放，空格+拖拽上下左右平移）</div>
              <div ref="interactiveWrapRef" class="compare-box preview-box interactive-wrap" @wheel="onWrapWheel">
                <canvas
                    ref="interactiveCanvasRef"
                    class="interactive-canvas"
                    :class="{ 'space-pan': spaceDown, panning: isPanning }"
                    @pointerdown="onCanvasPointerDown"
                    @pointermove="onCanvasPointerMove"
                    @pointerup="onCanvasPointerUp"
                    @pointerleave="onCanvasPointerLeave"
                ></canvas>
              </div>
            </div>

            <div v-if="hoverInfo" class="cell-tooltip" :style="{ left: hoverInfo.tipLeft + 'px', top: hoverInfo.tipTop + 'px' }">
              <div class="cell-tooltip-coord">第 {{ hoverInfo.gridX + 1 }} 列 · 第 {{ hoverInfo.gridY + 1 }} 行</div>
              <div v-if="hoverInfo.code || hoverInfo.hex" class="cell-tooltip-row">
                <span class="cell-tooltip-swatch" :style="{ background: hoverInfo.hex || '#ffffff' }"></span>
                <span class="cell-tooltip-code">{{ hoverInfo.code || '--' }}</span>
                <span class="cell-tooltip-hex">{{ hoverInfo.hex || '--' }}</span>
              </div>
              <div v-else class="cell-tooltip-empty">该豆格无颜色</div>
            </div>

            <div class="coord-hint">
              预览图中可用 Ctrl+滚轮缩放，按住空格键拖拽可上下左右平移画布；坐标系用于定位豆格，第三步图纸会标注坐标与 MARD 色码。
            </div>

            <div class="sub-title color-title">颜色清单（共 {{ step2ColorStats.length }} 种颜色，共 {{ step2ColorStats.reduce((acc, cur) => acc + cur.count, 0) }} 颗）</div>
            <div class="color-stat-list">
              <div v-for="stat in step2ColorStats" :key="stat.index" class="color-stat-item">
                <span class="color-index">{{ stat.code || `#${stat.index}` }}</span>
                <span class="color-swatch" :style="{ background: stat.hex }"></span>
                <span class="color-hex">{{ stat.hex }}</span>
                <span class="color-count">{{ stat.count }} 颗</span>
              </div>
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
                  <span class="summary-label">AI 风格</span>
                  <span class="summary-value">{{ aiStylePrompt.trim() || '未使用' }}</span>
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
                <div class="panel-title">参数面板</div>
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

<!--              <lay-form-item label="AI 风格提示词（可选）">-->
<!--                <lay-textarea-->
<!--                    v-model="aiStylePrompt"-->
<!--                    placeholder="例如：重绘成卡图风格 / 水彩插画风，保留主体轮廓与构图"-->
<!--                    :rows="2"-->
<!--                ></lay-textarea>-->
<!--                <div class="ai-hint">填写后重新生成，将先用 gpt-image-2 重绘原图再像素化；留空则直接处理原图。</div>-->
<!--              </lay-form-item>-->

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
              普通 PNG 和 SVG 包含坐标与编码，纯净 PNG 仅含色块。SVG 适合进一步编辑，PNG 适合快速打印或查看。
            </div>
          </div>
          <div class="panel-actions">
            <lay-button size="sm" @click="backToPreview">返回预览</lay-button>
            <lay-button size="sm" type="primary" @click="backToUpload">重新生成</lay-button>
            <lay-button size="sm" border="green" @click="downloadPngClean">
              <lay-icon class="layui-icon-download-circle"></lay-icon>
              下载 PNG（纯净）
            </lay-button>
            <lay-button size="sm" border="green" @click="downloadPng">
              <lay-icon class="layui-icon-download-circle"></lay-icon>
              下载 PNG
            </lay-button>
            <lay-button size="sm" border="green" @click="downloadSvg">
              <lay-icon class="layui-icon-download-circle"></lay-icon>
              下载 SVG
            </lay-button>
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
              <div class="sub-title">图纸 SVG</div>
              <div class="svg-box" v-html="confirmResp.patternSvg"></div>
            </div>
          </div>

          <div class="sub-title color-title">颜色清单（共 {{ confirmResp.colorStats?.length || 0 }} 种颜色， 共 {{ confirmResp.colorStats?.reduce((acc, cur) => acc + cur.count, 0) || 0 }} 颗）</div>
          <div class="color-stat-list">
            <div v-for="stat in confirmResp.colorStats" :key="stat.index" class="color-stat-item">
              <span class="color-index">{{ stat.code || `#${stat.index}` }}</span>
              <span class="color-swatch" :style="{ background: stat.hex }"></span>
              <span class="color-hex">{{ stat.hex }}</span>
              <span class="color-count">{{ stat.count }} 颗</span>
            </div>
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
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue';
import { layer } from '@layui/layui-vue';
import { confirmGenerate, generateImage, FuseBeanGenerateOptions } from '@/api/fusebean/FuseBean';
import { FuseBeanColor, FuseBeanConfirmResp, FuseBeanGenerateResp } from '@/types/fusebean/FuseBean';

const prompt = ref('');
const aiStylePrompt = ref('');
const patternName = ref('');
const gridSize = ref<number>(50);
const maxColors = ref<number>(18);
const processingMode = ref<'edge' | 'average' | 'dominant'>('edge');
const removeBackground = ref(false);
const flipHorizontal = ref(false);

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
    aiStylePrompt: aiStylePrompt.value.trim() || undefined,
  };
}

/**
 * 依据网格与色板在本地 canvas 重绘纯色块预览图。
 * 网格中每格存储的是色板下标（0 基），与后端 renderPreview 约定一致。
 */
function renderPreviewFromGrid(grid: Array<Array<number | null>>, palette: FuseBeanColor[]): string {
  const height = grid.length;
  const width = grid[0]?.length || 0;
  if (!width || !height) {
    return '';
  }
  const maxSide = Math.max(width, height);
  const cellSize = Math.max(3, Math.min(10, Math.floor(600 / maxSide)));
  const canvas = document.createElement('canvas');
  canvas.width = width * cellSize;
  canvas.height = height * cellSize;
  const ctx = canvas.getContext('2d');
  if (!ctx) {
    return '';
  }
  ctx.fillStyle = '#f2f4f7';
  ctx.fillRect(0, 0, canvas.width, canvas.height);
  for (let y = 0; y < height; y++) {
    const row = grid[y];
    if (!row) {
      continue;
    }
    for (let x = 0; x < width; x++) {
      const paletteIndex = row[x];
      if (paletteIndex == null || paletteIndex < 0 || paletteIndex >= palette.length) {
        continue;
      }
      ctx.fillStyle = palette[paletteIndex].hex || '#ffffff';
      ctx.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }
  }
  return canvas.toDataURL('image/png');
}

const interactiveCanvasRef = ref<HTMLCanvasElement | null>(null);
const interactiveWrapRef = ref<HTMLElement | null>(null);
const containerWidth = ref(0);
const selectedCells = ref<Array<{ x: number; y: number }>>([]);
const paintMode = ref(true);
const paintTool = ref<'brush' | 'bucket'>('brush');
const canvasUndoStack = ref<Array<{
  grid: Array<Array<number | null>>;
  palette: FuseBeanColor[];
  previewBase64: string;
  lastAppliedColor: number | null;
}>>([]);
const hoverCell = ref<{ x: number; y: number } | null>(null);
const hoverInfo = ref<{
  tipLeft: number;
  tipTop: number;
  gridX: number;
  gridY: number;
  code?: string;
  hex?: string;
} | null>(null);
const dragStartCell = ref<{ x: number; y: number } | null>(null);
const isDragging = ref(false);
const isPainting = ref(false);
const paintLastCell = ref<{ x: number; y: number } | null>(null);
const paintStrokeUndoCaptured = ref(false);
const lastAppliedColor = ref<number | null>(null);
const previewZoom = ref(1);
const spaceDown = ref(false);
const isPanning = ref(false);
const panStart = ref<{ x: number; y: number; sl: number; st: number } | null>(null);

const canvasLeftMargin = 34;
const canvasTopMargin = 24;
// 预留纵向滚动条宽度，使缩放 1 倍时画布宽度贴合框体内容区、不出现横向滚动条
const scrollbarReserve = 17;
const zoomMin = 0.5;
const zoomMax = 8;

const interactiveCellSize = computed(() => {
  const resp = generateResp.value;
  if (!resp) {
    return 0;
  }
  const width = resp.gridWidth || 0;
  if (!width || !containerWidth.value) {
    return 0;
  }
  // 画布可用宽度 = 容器宽度 - 左右内边距(12px*2) - 坐标轴左边距 - 纵向滚动条预留；
  // 按格宽均分，使画布宽度填充到参数栏一侧，高度随格距等比缩放
  const availW = containerWidth.value - canvasLeftMargin - 24 - scrollbarReserve;
  return Math.max(4, Math.floor(availW / width));
});

const displayCellSize = computed(() => {
  const resp = generateResp.value;
  if (!resp) {
    return 0;
  }
  const maxSide = Math.max(resp.gridWidth || 0, resp.gridHeight || 0);
  if (!maxSide) {
    return 0;
  }
  const base = interactiveCellSize.value;
  // 限制画布最长边不超过 4096px，避免缩放过大导致 canvas 内存溢出
  const cap = Math.max(1, Math.floor(4096 / maxSide));
  // 量化为整数，避免相邻豆格间出现亚像素缝隙
  return Math.max(1, Math.min(Math.round(base * previewZoom.value), cap));
});

const hasWhitePaletteColor = computed(() => {
  return (generateResp.value?.palette || []).some(color => (color.hex || '').toUpperCase() === '#FFFFFF');
});

const whiteSwatchIndex = computed(() => {
  const palette = generateResp.value?.palette || [];
  const existing = palette.findIndex(color => (color.hex || '').toUpperCase() === '#FFFFFF');
  return existing >= 0 ? existing : palette.length;
});

// 第二步的色豆用量清单，与第三步颜色清单同构；随网格/色板实时更新
const step2ColorStats = computed(() => {
  const resp = generateResp.value;
  if (!resp?.grid || !resp.palette?.length) {
    return [];
  }
  const counts = new Array<number>(resp.palette.length).fill(0);
  for (const row of resp.grid) {
    if (!row) {
      continue;
    }
    for (const v of row) {
      if (v != null && v >= 0 && v < counts.length) {
        counts[v] += 1;
      }
    }
  }
  return resp.palette.map((color, i) => ({
    index: color.index ?? i + 1,
    code: color.code,
    hex: color.hex,
    count: counts[i],
  }));
});

function renderInteractiveGrid() {
  const canvas = interactiveCanvasRef.value;
  const resp = generateResp.value;
  if (!canvas || !resp?.grid || !resp.palette?.length) {
    return;
  }
  const cellSize = displayCellSize.value;
  const w = resp.gridWidth || 0;
  const h = resp.gridHeight || 0;
  if (!cellSize || !w || !h) {
    return;
  }
  canvas.width = canvasLeftMargin + w * cellSize;
  canvas.height = canvasTopMargin + h * cellSize;
  canvas.style.width = `${canvas.width}px`;
  canvas.style.height = `${canvas.height}px`;
  const ctx = canvas.getContext('2d');
  if (!ctx) {
    return;
  }
  ctx.fillStyle = '#f2f4f7';
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  ctx.font = '11px Arial, sans-serif';
  ctx.fillStyle = '#7d869f';
  ctx.textBaseline = 'middle';
  ctx.textAlign = 'center';
  for (let x = 0; x < w; x++) {
    ctx.fillText(String(x + 1), canvasLeftMargin + x * cellSize + cellSize / 2, canvasTopMargin - 10);
  }
  ctx.textAlign = 'right';
  for (let y = 0; y < h; y++) {
    ctx.fillText(String(y + 1), canvasLeftMargin - 6, canvasTopMargin + y * cellSize + cellSize / 2);
  }

  const palette = resp.palette;
  for (let y = 0; y < h; y++) {
    const row = resp.grid[y];
    if (!row) {
      continue;
    }
    for (let x = 0; x < w; x++) {
      const index = row[x];
      if (index == null || index < 0 || index >= palette.length) {
        continue;
      }
      ctx.fillStyle = palette[index].hex || '#ffffff';
      ctx.fillRect(canvasLeftMargin + x * cellSize, canvasTopMargin + y * cellSize, cellSize, cellSize);
    }
  }

  ctx.strokeStyle = '#d7dce6';
  ctx.lineWidth = 1;
  ctx.beginPath();
  for (let x = 0; x <= w; x++) {
    const px = Math.round(canvasLeftMargin + x * cellSize) + 0.5;
    ctx.moveTo(px, canvasTopMargin);
    ctx.lineTo(px, canvasTopMargin + h * cellSize);
  }
  for (let y = 0; y <= h; y++) {
    const py = Math.round(canvasTopMargin + y * cellSize) + 0.5;
    ctx.moveTo(canvasLeftMargin, py);
    ctx.lineTo(canvasLeftMargin + w * cellSize, py);
  }
  ctx.stroke();

  if (hoverCell.value && !isDragging.value) {
    drawCellHighlight(ctx, hoverCell.value.x, hoverCell.value.y, 'rgba(22, 32, 51, 0.10)');
  }
  for (const c of selectedCells.value) {
    drawCellHighlight(ctx, c.x, c.y, 'rgba(64, 128, 255, 0.28)');
  }
}

function syncPreviewBase64(resp: FuseBeanGenerateResp) {
  if (!resp.grid || !resp.palette?.length) {
    return;
  }
  resp.previewBase64 = renderPreviewFromGrid(resp.grid, resp.palette);
}

function cloneGrid(grid: Array<Array<number | null>>): Array<Array<number | null>> {
  return grid.map(row => row.slice());
}

function clonePalette(palette: FuseBeanColor[]): FuseBeanColor[] {
  return palette.map(color => ({ ...color }));
}

function captureCanvasUndoState() {
  const resp = generateResp.value;
  if (!resp?.grid || !resp.palette?.length) {
    return;
  }
  canvasUndoStack.value.push({
    grid: cloneGrid(resp.grid),
    palette: clonePalette(resp.palette),
    previewBase64: resp.previewBase64 || renderPreviewFromGrid(resp.grid, resp.palette),
    lastAppliedColor: lastAppliedColor.value,
  });
  if (canvasUndoStack.value.length > 10) {
    canvasUndoStack.value.shift();
  }
}

function restoreCanvasUndoState(snapshot: {
  grid: Array<Array<number | null>>;
  palette: FuseBeanColor[];
  previewBase64: string;
  lastAppliedColor: number | null;
}) {
  const resp = generateResp.value;
  if (!resp) {
    return;
  }
  resp.grid = cloneGrid(snapshot.grid);
  resp.palette = clonePalette(snapshot.palette);
  resp.previewBase64 = snapshot.previewBase64 || renderPreviewFromGrid(resp.grid, resp.palette);
  lastAppliedColor.value = snapshot.lastAppliedColor;
  selectedCells.value = [];
  hoverCell.value = null;
  hoverInfo.value = null;
  dragStartCell.value = null;
  isDragging.value = false;
  isPainting.value = false;
  paintLastCell.value = null;
  paintStrokeUndoCaptured.value = false;
  renderInteractiveGrid();
}

function undoCanvasEdit() {
  const snapshot = canvasUndoStack.value.pop();
  if (!snapshot) {
    layer.msg('没有可撤销的操作', { icon: 2 });
    return;
  }
  restoreCanvasUndoState(snapshot);
  layer.msg('已撤销', { icon: 1 });
}

function drawCellHighlight(ctx: CanvasRenderingContext2D, x: number, y: number, fill: string) {
  const cellSize = displayCellSize.value;
  const rx = canvasLeftMargin + x * cellSize;
  const ry = canvasTopMargin + y * cellSize;
  ctx.fillStyle = fill;
  ctx.fillRect(rx, ry, cellSize, cellSize);
  ctx.strokeStyle = '#2f6bff';
  ctx.lineWidth = 2;
  ctx.strokeRect(rx + 1, ry + 1, cellSize - 2, cellSize - 2);
}

function cellFromPointer(e: PointerEvent): { x: number; y: number } | null {
  const canvas = interactiveCanvasRef.value;
  const resp = generateResp.value;
  if (!canvas || !resp) {
    return null;
  }
  const rect = canvas.getBoundingClientRect();
  const mx = e.clientX - rect.left;
  const my = e.clientY - rect.top;
  const x = Math.floor((mx - canvasLeftMargin) / displayCellSize.value);
  const y = Math.floor((my - canvasTopMargin) / displayCellSize.value);
  if (x < 0 || y < 0 || x >= (resp.gridWidth || 0) || y >= (resp.gridHeight || 0)) {
    return null;
  }
  return { x, y };
}

function cellsInRect(a: { x: number; y: number }, b: { x: number; y: number }): Array<{ x: number; y: number }> {
  const list: Array<{ x: number; y: number }> = [];
  const minX = Math.min(a.x, b.x);
  const maxX = Math.max(a.x, b.x);
  const minY = Math.min(a.y, b.y);
  const maxY = Math.max(a.y, b.y);
  for (let y = minY; y <= maxY; y++) {
    for (let x = minX; x <= maxX; x++) {
      list.push({ x, y });
    }
  }
  return list;
}

function setPaintMode(enabled: boolean) {
  paintMode.value = enabled;
  if (enabled) {
    selectedCells.value = [];
  } else {
    isPainting.value = false;
    paintLastCell.value = null;
    paintStrokeUndoCaptured.value = false;
  }
  renderInteractiveGrid();
}

function setPaintTool(tool: 'brush' | 'bucket') {
  if (!paintMode.value) {
    return;
  }
  paintTool.value = tool;
}

function setGridCellColor(cell: { x: number; y: number }, paletteIndex: number) {
  const resp = generateResp.value;
  if (!resp?.grid || !resp.palette) {
    return false;
  }
  if (paletteIndex < 0 || paletteIndex >= resp.palette.length) {
    return false;
  }
  const row = resp.grid[cell.y];
  if (!row || cell.x < 0 || cell.x >= row.length) {
    return false;
  }
  row[cell.x] = paletteIndex;
  lastAppliedColor.value = paletteIndex;
  syncPreviewBase64(resp);
  return true;
}

function paintCell(cell: { x: number; y: number }, paletteIndex: number) {
  if (!setGridCellColor(cell, paletteIndex)) {
    return;
  }
  selectedCells.value = [];
  hoverInfo.value = null;
  renderInteractiveGrid();
}

function beginPaintStrokeUndo() {
  if (!paintStrokeUndoCaptured.value) {
    captureCanvasUndoState();
    paintStrokeUndoCaptured.value = true;
  }
}

function fillCellRegion(seed: { x: number; y: number }, paletteIndex: number) {
  const resp = generateResp.value;
  if (!resp?.grid || !resp.palette?.length) {
    return false;
  }
  if (paletteIndex < 0 || paletteIndex >= resp.palette.length) {
    return false;
  }
  const startRow = resp.grid[seed.y];
  if (!startRow || seed.x < 0 || seed.x >= startRow.length) {
    return false;
  }
  const targetValue = startRow[seed.x];
  if (targetValue === paletteIndex) {
    return false;
  }

  const width = resp.gridWidth || startRow.length;
  const height = resp.gridHeight || resp.grid.length;
  const queue: Array<{ x: number; y: number }> = [seed];
  const visited = new Set<number>();
  let filled = false;

  while (queue.length) {
    const current = queue.pop()!;
    if (current.x < 0 || current.y < 0 || current.x >= width || current.y >= height) {
      continue;
    }
    const key = current.y * width + current.x;
    if (visited.has(key)) {
      continue;
    }
    const row = resp.grid[current.y];
    if (!row || row[current.x] !== targetValue) {
      continue;
    }
    visited.add(key);
    row[current.x] = paletteIndex;
    filled = true;
    queue.push(
      { x: current.x - 1, y: current.y },
      { x: current.x + 1, y: current.y },
      { x: current.x, y: current.y - 1 },
      { x: current.x, y: current.y + 1 },
    );
  }

  if (!filled) {
    return false;
  }
  lastAppliedColor.value = paletteIndex;
  selectedCells.value = [];
  hoverInfo.value = null;
  syncPreviewBase64(resp);
  renderInteractiveGrid();
  return true;
}

function buildHoverInfo(cell: { x: number; y: number }, e: PointerEvent) {
  const resp = generateResp.value;
  let code: string | undefined;
  let hex: string | undefined;
  if (resp?.grid && resp.palette) {
    const row = resp.grid[cell.y];
    const paletteIndex = row?.[cell.x];
    if (paletteIndex != null && paletteIndex >= 0 && paletteIndex < resp.palette.length) {
      code = resp.palette[paletteIndex].code;
      hex = resp.palette[paletteIndex].hex;
    }
  }
  const tipWidth = 200;
  const tipHeight = 62;
  const tipLeft = e.clientX + 14 + tipWidth > window.innerWidth ? e.clientX - tipWidth - 14 : e.clientX + 14;
  const tipTop = e.clientY + 16 + tipHeight > window.innerHeight ? e.clientY - tipHeight - 16 : e.clientY + 16;
  return { tipLeft, tipTop, gridX: cell.x, gridY: cell.y, code, hex };
}

function onWrapWheel(e: WheelEvent) {
  // 仅 Ctrl+滚轮缩放；普通滚轮保持容器/页面默认滚动
  if (!e.ctrlKey) {
    return;
  }
  e.preventDefault();
  const canvas = interactiveCanvasRef.value;
  const resp = generateResp.value;
  const cellSize = displayCellSize.value;
  if (!canvas || !resp || !cellSize) {
    return;
  }
  const rect = canvas.getBoundingClientRect();
  const mx = e.clientX - rect.left;
  const my = e.clientY - rect.top;
  // 光标落在画布外的空白区域：仅阻止浏览器缩放，不缩放画布
  if (mx < 0 || my < 0 || mx > rect.width || my > rect.height) {
    return;
  }
  // 用旧缩放比计算光标对应的网格坐标，缩放后让该网格点保持在光标下方
  const gx = (mx - canvasLeftMargin) / cellSize;
  const gy = (my - canvasTopMargin) / cellSize;
  const factor = Math.pow(1.0015, -e.deltaY);
  previewZoom.value = Math.min(zoomMax, Math.max(zoomMin, previewZoom.value * factor));
  renderInteractiveGrid();
  const wrap = canvas.parentElement as HTMLElement | null;
  if (wrap) {
    const rect2 = canvas.getBoundingClientRect();
    const nx = canvasLeftMargin + gx * displayCellSize.value;
    const ny = canvasTopMargin + gy * displayCellSize.value;
    wrap.scrollLeft += rect2.left + nx - e.clientX;
    wrap.scrollTop += rect2.top + ny - e.clientY;
  }
}

function isTypingTarget(e: KeyboardEvent) {
  const target = e.target as HTMLElement | null;
  if (!target) {
    return false;
  }
  const tag = target.tagName;
  return tag === 'INPUT' || tag === 'TEXTAREA' || target.isContentEditable;
}

function onWindowKeyDown(e: KeyboardEvent) {
  if ((e.ctrlKey || e.metaKey) && (e.key === 'z' || e.key === 'Z') && activeStep.value === 1 && generateResp.value && !isTypingTarget(e)) {
    e.preventDefault();
    undoCanvasEdit();
    return;
  }
  if (e.code !== 'Space' || isTypingTarget(e)) {
    return;
  }
  e.preventDefault();
  spaceDown.value = true;
}

function onWindowKeyUp(e: KeyboardEvent) {
  if (e.code !== 'Space') {
    return;
  }
  spaceDown.value = false;
  isPanning.value = false;
  panStart.value = null;
}

function onCanvasPointerDown(e: PointerEvent) {
  if (spaceDown.value) {
    const wrap = interactiveCanvasRef.value?.parentElement as HTMLElement | null;
    isPanning.value = true;
    panStart.value = { x: e.clientX, y: e.clientY, sl: wrap?.scrollLeft ?? 0, st: wrap?.scrollTop ?? 0 };
    (e.currentTarget as HTMLElement | null)?.setPointerCapture?.(e.pointerId);
    return;
  }
  const cell = cellFromPointer(e);
  if (!cell) {
    selectedCells.value = [];
    dragStartCell.value = null;
    isDragging.value = false;
    isPainting.value = false;
    paintLastCell.value = null;
    hoverInfo.value = null;
    renderInteractiveGrid();
    return;
  }
  if (paintMode.value) {
    const paletteIndex = lastAppliedColor.value;
    if (paletteIndex == null) {
      layer.msg('请先选择颜色', { icon: 2 });
      return;
    }
    if (paintTool.value === 'bucket') {
      captureCanvasUndoState();
      isPainting.value = false;
      paintLastCell.value = null;
      paintStrokeUndoCaptured.value = false;
      fillCellRegion(cell, paletteIndex);
      (e.currentTarget as HTMLElement | null)?.setPointerCapture?.(e.pointerId);
      return;
    }
    beginPaintStrokeUndo();
    isPainting.value = true;
    paintLastCell.value = cell;
    paintCell(cell, paletteIndex);
    (e.currentTarget as HTMLElement | null)?.setPointerCapture?.(e.pointerId);
    return;
  }
  isDragging.value = true;
  dragStartCell.value = cell;
  selectedCells.value = [cell];
  hoverInfo.value = null;
  renderInteractiveGrid();
}

function onCanvasPointerMove(e: PointerEvent) {
  if (isPanning.value && panStart.value) {
    const wrap = interactiveCanvasRef.value?.parentElement as HTMLElement | null;
    if (wrap) {
      wrap.scrollLeft = panStart.value.sl - (e.clientX - panStart.value.x);
      wrap.scrollTop = panStart.value.st - (e.clientY - panStart.value.y);
    }
    return;
  }
  const cell = cellFromPointer(e);
  if (isPainting.value) {
    hoverInfo.value = null;
    if (!cell || !paintLastCell.value || (cell.x === paintLastCell.value.x && cell.y === paintLastCell.value.y)) {
      return;
    }
    const paletteIndex = lastAppliedColor.value;
    if (paletteIndex == null) {
      return;
    }
    paintLastCell.value = cell;
    paintCell(cell, paletteIndex);
    return;
  }
  if (isDragging.value && cell && dragStartCell.value) {
    hoverInfo.value = null;
    selectedCells.value = cellsInRect(dragStartCell.value, cell);
    renderInteractiveGrid();
    return;
  }
  hoverInfo.value = cell ? buildHoverInfo(cell, e) : null;
  const prev = hoverCell.value;
  hoverCell.value = cell;
  if (cell?.x !== prev?.x || cell?.y !== prev?.y) {
    renderInteractiveGrid();
  }
}

function onCanvasPointerUp(e: PointerEvent) {
  if (isPanning.value) {
    isPanning.value = false;
    panStart.value = null;
  }
  isDragging.value = false;
  isPainting.value = false;
  paintLastCell.value = null;
  paintStrokeUndoCaptured.value = false;
}

function onCanvasPointerLeave() {
  hoverCell.value = null;
  hoverInfo.value = null;
  if (!isPainting.value) {
    paintStrokeUndoCaptured.value = false;
  }
  if (!isDragging.value && !isPainting.value && !isPanning.value) {
    renderInteractiveGrid();
  }
}

window.addEventListener('keydown', onWindowKeyDown);
window.addEventListener('keyup', onWindowKeyUp);

function clearSelection() {
  selectedCells.value = [];
  renderInteractiveGrid();
}

function applyColorToSelected(paletteIndex: number) {
  const resp = generateResp.value;
  if (!resp?.grid || !resp.palette) {
    return;
  }
  if (!selectedCells.value.length) {
    layer.msg('请先在预览图中点击或拖拽选择豆格', { icon: 2 });
    return;
  }
  if (paletteIndex < 0 || paletteIndex >= resp.palette.length) {
    return;
  }
  captureCanvasUndoState();
  for (const c of selectedCells.value) {
    const row = resp.grid[c.y];
    if (row && c.x >= 0 && c.x < row.length) {
      row[c.x] = paletteIndex;
    }
  }
  lastAppliedColor.value = paletteIndex;
  renderInteractiveGrid();
  syncPreviewBase64(resp);
}

function applyWhiteToSelected() {
  const resp = generateResp.value;
  if (!resp?.grid || !resp.palette) {
    return;
  }
  if (!selectedCells.value.length) {
    layer.msg('请先在预览图中点击或拖拽选择豆格', { icon: 2 });
    return;
  }
  captureCanvasUndoState();
  const index = whitePaletteIndex();
  for (const c of selectedCells.value) {
    const row = resp.grid[c.y];
    if (row && c.x >= 0 && c.x < row.length) {
      row[c.x] = index;
    }
  }
  lastAppliedColor.value = index;
  renderInteractiveGrid();
  syncPreviewBase64(resp);
}

function handlePaletteClick(paletteIndex: number) {
  if (paintMode.value) {
    lastAppliedColor.value = paletteIndex;
    return;
  }
  applyColorToSelected(paletteIndex);
}

function handleWhitePaletteClick() {
  const index = whitePaletteIndex();
  if (paintMode.value) {
    lastAppliedColor.value = index;
    return;
  }
  applyWhiteToSelected();
}

/**
 * 返回白色在色板中的下标；若色板尚无白色，则在末尾追加 T1 白色。
 * 空豆格（去背景的外围）仍保持 null，不会被标色或计入用量。
 */
function whitePaletteIndex(): number {
  const resp = generateResp.value;
  if (!resp?.palette) {
    return -1;
  }
  const existing = resp.palette.findIndex(color => (color.hex || '').toUpperCase() === '#FFFFFF');
  if (existing >= 0) {
    return existing;
  }
  const index = resp.palette.length;
  resp.palette.push({ index: index + 1, code: 'T1', hex: '#FFFFFF' });
  return index;
}

// 一次性测量容器宽度后固定画布大小，不再随窗口变化自动缩放。
// 测量外层 .single-preview 的宽度，避免画布撑高后 wrap 的纵向滚动条占位导致测宽偏小
function measureInteractiveWrap() {
  const wrap = interactiveWrapRef.value;
  const el = wrap?.parentElement ?? wrap;
  if (el) {
    containerWidth.value = el.clientWidth || 0;
  }
}

watch([activeStep, generateResp], () => {
  if (activeStep.value === 1 && generateResp.value) {
    previewZoom.value = 1;
    hoverInfo.value = null;
    hoverCell.value = null;
    selectedCells.value = [];
    canvasUndoStack.value = [];
    isDragging.value = false;
    isPainting.value = false;
    paintLastCell.value = null;
    paintMode.value = true;
    paintTool.value = 'brush';
    lastAppliedColor.value = null;
    nextTick(() => {
      measureInteractiveWrap();
      renderInteractiveGrid();
    });
  }
});

async function handleGenerate() {
  if (aiStylePrompt.value.trim() && !sourceFile.value) {
    layer.msg('AI 风格重绘需要先上传图片', { icon: 2 });
    return;
  }
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
      canvasUndoStack.value = [];
      activeStep.value = 1;
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

function openCompare() {
  if (!sourceImageUrl.value) {
    layer.msg('暂无原图可对比', { icon: 2 });
    return;
  }
  const canvas = interactiveCanvasRef.value;
  let preview = generateResp.value?.previewBase64 || '';
  // 优先截取当前画布内容，反映用户已做的颜色调整
  if (canvas && canvas.width > 1) {
    preview = canvas.toDataURL('image/png');
  }
  layer.open({
    type: 1,
    title: '原图与拼豆预览对比',
    content: `<div style="display: flex; gap: 16px; padding: 16px; align-items: flex-start; flex-wrap: wrap;">
        <div style="flex: 1 1 260px; min-width: 240px; text-align: center;">
          <div style="font-weight: 600; margin-bottom: 8px; color: #162033;">原图</div>
          <img src="${sourceImageUrl.value}" style="max-width: 100%; max-height: 420px; border: 1px solid #edf1f7; border-radius: 8px; background: #fff; object-fit: contain;" />
        </div>
        <div style="flex: 1 1 260px; min-width: 240px; text-align: center;">
          <div style="font-weight: 600; margin-bottom: 8px; color: #162033;">拼豆预览</div>
          ${preview
            ? `<img src="${preview}" style="max-width: 100%; max-height: 420px; border: 1px solid #edf1f7; border-radius: 8px; background: #fff; object-fit: contain;" />`
            : '<div style="color: #7d869f; padding: 48px 0;">暂无预览</div>'}
        </div>
      </div>`,
    shade: true,
    isHtmlFragment: true,
    area: '860px',
    btn: [
      {
        text: '关闭',
        callback(id: string) {
          layer.close(id);
        },
      },
    ],
  });
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

function downloadPngClean() {
  const dataUrl = confirmResp.value?.patternPngCleanBase64;
  if (!dataUrl) {
    layer.msg('暂无可下载的纯净 PNG 文件', { icon: 2 });
    return;
  }
  const a = document.createElement('a');
  a.href = dataUrl;
  a.download = `${sanitizeDownloadName(patternName.value)}-pattern-clean.png`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
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
  window.removeEventListener('keydown', onWindowKeyDown);
  window.removeEventListener('keyup', onWindowKeyUp);
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
  flex-wrap: nowrap;
  gap: 10px;
  justify-content: flex-end;
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
}

.panel-actions :deep(.layui-btn) {
  flex: 0 0 auto;
  white-space: nowrap;
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

.ai-hint {
  margin-top: 6px;
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
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
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

.interactive-wrap {
  overflow: auto;
  align-items: flex-start;
  justify-content: flex-start;
  min-height: 320px;
  max-height: min(62vh, 600px);
  scrollbar-width: thin;
}

.interactive-canvas {
  display: block;
  border-radius: 6px;
  flex-shrink: 0;
  cursor: crosshair;
  touch-action: none;
}

.interactive-canvas.space-pan {
  cursor: grab;
}

.interactive-canvas.panning {
  cursor: grabbing;
}

.cell-tooltip {
  position: fixed;
  z-index: 1000;
  pointer-events: none;
  min-width: 150px;
  padding: 8px 10px;
  border: 1px solid #dbe3ef;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.97);
  box-shadow: 0 4px 16px rgba(22, 32, 51, 0.14);
  font-size: 12px;
  color: #162033;
}

.cell-tooltip-coord {
  font-weight: 600;
  color: #41506d;
}

.cell-tooltip-row {
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cell-tooltip-swatch {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  border: 1px solid #d9e1ee;
  flex-shrink: 0;
}

.cell-tooltip-code {
  font-family: monospace;
  font-weight: 700;
}

.cell-tooltip-hex {
  font-family: monospace;
  color: #7d869f;
}

.cell-tooltip-empty {
  margin-top: 6px;
  color: #7d869f;
}

.cell-editor {
  margin-top: 14px;
  padding: 16px;
  border: 1px solid #edf1f7;
  border-radius: 12px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfcff 100%);
}

.cell-editor-header {
  margin-bottom: 10px;
}

.cell-editor-desc {
  margin-top: 6px;
  color: #7d869f;
  font-size: 12px;
  line-height: 1.6;
}

.cell-selection-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 10px;
}

.cell-mode-switch {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.paint-tool-switch {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.sel-count {
  color: #41506d;
  font-size: 13px;
  font-weight: 600;
}

.cell-color-picker {
  padding-top: 10px;
  border-top: 1px solid #edf1f7;
}

.picker-swatches {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.picker-swatch {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  width: 52px;
  padding: 6px 4px;
  border: 1px solid #edf1f7;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.picker-swatch:hover {
  border-color: #b6c6e0;
}

.picker-swatch.active {
  border-color: #2f6bff;
  box-shadow: 0 0 0 2px rgba(47, 107, 255, 0.18);
}

.swatch-block {
  width: 26px;
  height: 26px;
  border-radius: 5px;
  border: 1px solid #d9e1ee;
}

.white-swatch {
  background: #ffffff;
  box-shadow: inset 0 0 0 1px #e3e8f0;
}

.swatch-code {
  color: #162033;
  font-family: monospace;
  font-size: 11px;
  font-weight: 700;
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
