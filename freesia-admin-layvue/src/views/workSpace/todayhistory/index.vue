<template>
  <div class="today-history-page">
    <lay-container :fluid="true" class="today-history-shell">
      <div class="today-history-dashboard">
        <section class="query-panel">
          <lay-card class="panel-card query-card">
            <template #title>
              <div class="card-title">
                <span>全局关键词查询</span>
                <lay-badge type="rim">{{ globalSearchResults.length }}</lay-badge>
              </div>
            </template>

            <div class="query-head">
              <p class="query-tip">
                这里查询的是全量历史词条，会跨日期检索标题、内容、年份和关联链接。
              </p>

              <div class="query-actions">
                <div class="query-input-row">
                  <lay-input
                    v-model="globalSearchKeyword"
                    prefix-icon="layui-icon-search"
                    :allow-clear="true"
                    size="sm"
                    placeholder="输入关键词进行全局查询"
                    @keydown.enter.prevent="applyGlobalSearch"
                  ></lay-input>
                </div>
                <div class="query-button-row">
                  <lay-button type="primary" size="sm" :loading="globalSearchLoading" @click="applyGlobalSearch">
                    查询
                  </lay-button>
                  <lay-button type="normal" size="sm" :disabled="globalSearchLoading" @click="clearGlobalSearch">
                    清空
                  </lay-button>
                </div>
              </div>

              <div class="query-meta">
                <span v-if="hasAppliedGlobalKeyword" class="query-meta-keyword">关键词：{{ appliedGlobalKeyword }}</span>
                <span v-else>等待输入关键词</span>
              </div>
            </div>

            <div class="query-result-area">
              <div v-if="hasAppliedGlobalKeyword" class="query-result-scroll">
                <div v-if="globalSearchResults.length > 0" class="query-result-list">
                  <div
                    v-for="item in globalSearchResults"
                    :key="item.itemHash || `${item.pageId}-${item.sortNo}-${item.historyKey}`"
                    class="query-result-card"
                  >
                    <div class="query-result-head">
                      <div class="query-result-title">{{ formatItemTitle(item) }}</div>
                      <span class="query-result-tag" :class="`query-result-tag--${getItemTypeTheme(item.itemType)}`">
                        {{ getItemTypeLabel(item.itemType) }}
                      </span>
                    </div>

                    <div class="query-result-source">
                      {{ getSourceLabel(item) }}
                    </div>

                    <p class="query-result-summary">
                      {{ buildSummary(item, 110) }}
                    </p>

                    <div class="query-result-foot">
                      <span class="query-result-foot-left">{{ getItemMetaText(item) }}</span>
                      <lay-button size="xs" type="normal" class="query-detail-button" @click="openDetail(item)">
                        详情
                      </lay-button>
                    </div>
                  </div>
                </div>
                <div v-else class="empty-state">
                  未找到匹配词条
                </div>
              </div>

              <div v-else class="query-placeholder">
                <div class="query-placeholder-title">全局查询面板</div>
                <p>输入关键词并点击查询后，这里会显示跨日期的摘要结果。</p>
                <p>每条结果都可以点“详情”查看完整词条。</p>
              </div>
            </div>
          </lay-card>
        </section>

        <section class="history-panel">
          <section class="hero">
            <div class="hero-copy">
              <p class="eyebrow">历史上的今天</p>
              <h1>{{ pageTitle }}</h1>
              <p class="hero-description">
                三列展示大事记、出生、逝世，底部集中呈现节假日与习俗信息。右侧框体中的查询只筛选当天内容。
              </p>
            </div>

            <div class="hero-meta">
              <div class="meta-grid">
                <div class="meta-card">
                  <span class="meta-label">总数</span>
                  <span class="meta-value">{{ totalCount }}</span>
                </div>
                <div class="meta-card">
                  <span class="meta-label">状态</span>
                  <span class="meta-value">{{ loading ? '加载中' : '就绪' }}</span>
                </div>
                <div class="date-nav">
                  <lay-button
                    type="normal"
                    size="sm"
                    class="nav-button"
                    :disabled="loading"
                    @click="goHistoryByOffset(-1)"
                  >
                    <lay-icon type="layui-icon-left"></lay-icon>
                  </lay-button>

                  <div class="meta-card meta-card--date">
                    <span class="meta-label">日期</span>
                    <span class="meta-value">{{ displayDate }}</span>
                  </div>

                  <lay-button
                    type="normal"
                    size="sm"
                    class="nav-button"
                    :disabled="loading"
                    @click="goHistoryByOffset(1)"
                  >
                    <lay-icon type="layui-icon-right"></lay-icon>
                  </lay-button>
                </div>
              </div>

              <lay-button type="primary" size="sm" :loading="loading" @click="reload">
                刷新当前
              </lay-button>
            </div>
          </section>

          <lay-card class="history-search-card">
            <template #title>
              <div class="card-title">
                <span>当天关键词筛选</span>
                <lay-badge type="rim">{{ localMatchCount }}</lay-badge>
              </div>
            </template>

            <div class="query-head query-head--compact">
              <p class="query-tip">
                这里筛选的是当前日期的数据，逻辑和你原先左侧的筛选一致。
              </p>

              <div class="query-actions">
                <div class="query-input-row">
                  <lay-input
                    v-model="localSearchKeyword"
                    prefix-icon="layui-icon-search"
                    :allow-clear="true"
                    size="sm"
                    placeholder="输入关键词筛选当天词条"
                    @keydown.enter.prevent="applyLocalSearch"
                  ></lay-input>
                </div>
                <div class="query-button-row">
                  <lay-button type="primary" size="sm" :disabled="loading" @click="applyLocalSearch">
                    筛选
                  </lay-button>
                  <lay-button type="normal" size="sm" :disabled="loading" @click="clearLocalSearch">
                    清空
                  </lay-button>
                </div>
              </div>

              <div class="query-meta">
                <span>仅筛选当前日期的词条</span>
                <span v-if="hasAppliedLocalKeyword" class="query-meta-keyword">关键词：{{ appliedLocalKeyword }}</span>
              </div>
            </div>
          </lay-card>

          <lay-row :space="8" class="history-grid">
            <lay-col v-for="section in sectionCards" :key="section.key" :md="8" :sm="24" :xs="24">
              <lay-card class="history-card" :class="section.cardClass">
                <template #title>
                  <div class="card-title">
                    <span>{{ section.title }}</span>
                    <lay-badge type="rim">{{ section.items.length }}</lay-badge>
                  </div>
                </template>

                <div v-if="section.items.length > 0" class="timeline-wrap">
                  <lay-timeline>
                    <lay-timeline-item
                      v-for="item in section.items"
                      :key="item.itemHash || `${item.itemType}-${item.sortNo}-${item.eventYear}`"
                      :title="formatItemTitle(item)"
                      simple
                    >
                      <template #dot>
                        <span class="timeline-dot" :class="section.dotClass"></span>
                      </template>

                      <div class="timeline-content">
                        <div v-if="item.itemType === 'EVENT' && getEraLabel(item)" class="era-pill">
                          {{ getEraLabel(item) }}
                        </div>
                        <p class="timeline-text">{{ getContentBody(item) }}</p>
                        <div v-if="item.links && item.links.length > 0" class="link-group">
                          <span class="link-label">相关链接</span>
                          <div class="link-list">
                            <a
                              v-for="link in item.links"
                              :key="link.linkHash || `${link.linkUrl}-${link.sortNo}`"
                              :href="link.linkUrl"
                              class="link-item"
                              :title="link.linkTitle || link.linkText || ''"
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              {{ link.linkText || link.linkTitle || '详情' }}
                            </a>
                          </div>
                        </div>
                      </div>
                    </lay-timeline-item>
                  </lay-timeline>
                </div>

                <div v-else class="empty-state">
                  暂无匹配数据
                </div>
              </lay-card>
            </lay-col>
          </lay-row>

          <lay-card class="holiday-card">
            <template #title>
              <div class="card-title">
                <span>节假日和习俗</span>
                <lay-badge theme="green">{{ holidayItems.length }}</lay-badge>
              </div>
            </template>

            <div class="holiday-summary">
              <p>{{ holidaySummary }}</p>
            </div>

            <template #footer>
              <div class="holiday-footer">
                <span class="holiday-footer-label">节假日习俗</span>
                <div v-if="holidayItems.length > 0" class="holiday-chip-list">
                  <span
                    v-for="item in holidayItems"
                    :key="item.itemHash || `${item.sortNo}-${item.content}`"
                    class="holiday-chip"
                  >
                    {{ item.content }}
                  </span>
                </div>
                <div v-else class="empty-state">
                  暂无节假日习俗信息
                </div>
              </div>
            </template>
          </lay-card>
        </section>
      </div>
    </lay-container>

    <lay-layer v-model="detailVisible" :title="detailTitle" :area="['760px', '620px']">
      <div v-if="detailItem" class="detail-layer-body">
        <div class="detail-meta">
          <span class="detail-pill">{{ getItemTypeLabel(detailItem.itemType) }}</span>
          <span class="detail-pill detail-pill--muted">{{ getDetailYearLabel(detailItem) }}</span>
          <span class="detail-pill detail-pill--muted">{{ getSourceLabel(detailItem) }}</span>
          <span v-if="getEraLabel(detailItem)" class="detail-pill detail-pill--muted">{{ getEraLabel(detailItem) }}</span>
        </div>

        <div class="detail-section">
          <div class="detail-section-label">摘要</div>
          <div class="detail-section-body detail-summary">{{ buildSummary(detailItem, 260) }}</div>
        </div>

        <div class="detail-section">
          <div class="detail-section-label">详情</div>
          <div class="detail-section-body detail-content">{{ detailItem.content || '暂无内容' }}</div>
        </div>

        <div v-if="detailItem.links && detailItem.links.length > 0" class="detail-section">
          <div class="detail-section-label">关联链接</div>
          <div class="detail-link-list">
            <a
              v-for="link in detailItem.links"
              :key="link.linkHash || `${link.linkUrl}-${link.sortNo}`"
              :href="link.linkUrl"
              class="detail-link-item"
              :title="link.linkTitle || link.linkText || ''"
              target="_blank"
              rel="noopener noreferrer"
            >
              {{ link.linkText || link.linkTitle || '详情' }}
            </a>
          </div>
        </div>
      </div>
    </lay-layer>
  </div>
</template>

<script lang="ts">
export default {
  name: 'TodayHistory',
};
</script>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { layer } from '@layui/layui-vue';
import { formatDateTime, getWeekdayCn } from '@/util/UDate';
import { findTodayHistoryDetail, searchTodayHistory } from '@/api/todayhistory/TodayHistory';
import { R } from '@/types/Result';
import {
  TodayHistoryEraTypeCode,
  TodayHistoryItemDto,
  TodayHistoryItemTypeCode,
  TodayHistoryPageDto,
  TodayHistorySearchResultDto,
} from '@/types/workSpace/TodayHistory';

type HistorySectionKey = 'EVENT' | 'BIRTH' | 'DEATH';

interface SectionCard {
  key: HistorySectionKey;
  title: string;
  cardClass: string;
  dotClass: string;
  items: TodayHistoryItemDto[];
}

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const globalSearchLoading = ref(false);
const pageData = ref<TodayHistoryPageDto | null>(null);
const globalSearchKeyword = ref('');
const appliedGlobalKeyword = ref('');
const globalSearchResults = ref<TodayHistorySearchResultDto[]>([]);
const localSearchKeyword = ref('');
const appliedLocalKeyword = ref('');
const currentHistoryKey = ref(resolveInitialHistoryKey());
const detailVisible = ref(false);
const detailItem = ref<TodayHistorySearchResultDto | null>(null);

const hasAppliedGlobalKeyword = computed(() => appliedGlobalKeyword.value.trim().length > 0);
const hasAppliedLocalKeyword = computed(() => appliedLocalKeyword.value.trim().length > 0);
const normalizedGlobalKeyword = computed(() => appliedGlobalKeyword.value.trim().toLowerCase());
const normalizedLocalKeyword = computed(() => appliedLocalKeyword.value.trim().toLowerCase());
const currentDate = computed(() => parseHistoryKey(currentHistoryKey.value) ?? new Date());
const displayDate = computed(() => `${formatDateTime(currentDate.value, 'yyyy年MM月dd日')} ${getWeekdayCn(currentDate.value)}`);
const pageTitle = computed(() => pageData.value?.pageTitle || '历史上的今天');

const items = computed(() => pageData.value?.items ?? []);
const totalCount = computed(() => pageData.value?.itemCount ?? items.value.length ?? 0);

const eventItems = computed(() => getLocalFilteredItems('EVENT'));
const birthItems = computed(() => getLocalFilteredItems('BIRTH'));
const deathItems = computed(() => getLocalFilteredItems('DEATH'));
const holidayItems = computed(() => getLocalFilteredItems('HOLIDAY'));
const localMatchCount = computed(() => eventItems.value.length + birthItems.value.length + deathItems.value.length + holidayItems.value.length);

const holidaySummary = computed(() => {
  if (holidayItems.value.length === 0) {
    return hasAppliedLocalKeyword.value ? '未找到匹配的节假日习俗信息' : '当前日期未返回节假日习俗信息';
  }

  return holidayItems.value
    .slice(0, 4)
    .map(item => item.content || '')
    .filter(Boolean)
    .join('，');
});

const detailTitle = computed(() => {
  if (!detailItem.value) {
    return '词条详情';
  }
  return `${getSourceLabel(detailItem.value)} · ${formatItemTitle(detailItem.value)}`;
});

watch(globalSearchKeyword, value => {
  if (!value.trim()) {
    appliedGlobalKeyword.value = '';
    globalSearchResults.value = [];
  }
});

watch(localSearchKeyword, value => {
  if (!value.trim()) {
    appliedLocalKeyword.value = '';
  }
});

const sectionCards = computed<SectionCard[]>(() => [
  {
    key: 'EVENT',
    title: '大事记',
    cardClass: 'history-card--event',
    dotClass: 'timeline-dot--event',
    items: eventItems.value,
  },
  {
    key: 'BIRTH',
    title: '出生',
    cardClass: 'history-card--birth',
    dotClass: 'timeline-dot--birth',
    items: birthItems.value,
  },
  {
    key: 'DEATH',
    title: '逝世',
    cardClass: 'history-card--death',
    dotClass: 'timeline-dot--death',
    items: deathItems.value,
  },
]);

onMounted(() => {
  void syncRouteHistoryKey(currentHistoryKey.value).finally(() => {
    void reload();
  });
});

function resolveInitialHistoryKey(): string {
  const queryValue = route.query.historyKey;
  if (typeof queryValue === 'string') {
    return normalizeHistoryKey(queryValue);
  }
  if (Array.isArray(queryValue) && queryValue.length > 0 && typeof queryValue[0] === 'string') {
    return normalizeHistoryKey(queryValue[0]);
  }
  return formatDateTime(new Date(), 'MM-dd');
}

function normalizeHistoryKey(value: string): string {
  const trimmed = value.trim();
  return /^\d{2}-\d{2}$/.test(trimmed) ? trimmed : formatDateTime(new Date(), 'MM-dd');
}

function parseHistoryKey(historyKey: string): Date | null {
  const match = /^(\d{2})-(\d{2})$/.exec(historyKey);
  if (!match) {
    return null;
  }

  const month = Number(match[1]);
  const day = Number(match[2]);
  if (!Number.isFinite(month) || !Number.isFinite(day)) {
    return null;
  }

  const date = new Date();
  date.setMonth(month - 1, day);
  date.setHours(0, 0, 0, 0);
  return date;
}

function getRouteHistoryKey(): string {
  const queryValue = route.query.historyKey;
  if (typeof queryValue === 'string') {
    return queryValue;
  }
  if (Array.isArray(queryValue) && queryValue.length > 0 && typeof queryValue[0] === 'string') {
    return queryValue[0];
  }
  return '';
}

async function syncRouteHistoryKey(nextKey: string) {
  const currentKey = getRouteHistoryKey();
  if (currentKey === nextKey) {
    return;
  }

  await router.replace({
    query: {
      ...route.query,
      historyKey: nextKey,
    },
  });
}

async function goHistoryByOffset(offset: number) {
  if (loading.value) {
    return;
  }

  const nextDate = new Date(currentDate.value);
  nextDate.setDate(nextDate.getDate() + offset);
  currentHistoryKey.value = formatDateTime(nextDate, 'MM-dd');
  await syncRouteHistoryKey(currentHistoryKey.value);
  await reload();
}

async function applyGlobalSearch() {
  const keyword = globalSearchKeyword.value.trim();
  if (!keyword) {
    clearGlobalSearch();
    return;
  }

  appliedGlobalKeyword.value = keyword;
  globalSearchLoading.value = true;
  try {
    const res = await searchTodayHistory(keyword);
    if (res.code === 200) {
      globalSearchResults.value = res.data ?? [];
      return;
    }
    globalSearchResults.value = [];
    layer.msg(res.msg || '全局关键词查询失败', { icon: 2 });
  } catch {
    globalSearchResults.value = [];
    layer.msg('全局关键词查询失败', { icon: 2 });
  } finally {
    globalSearchLoading.value = false;
  }
}

function clearGlobalSearch() {
  globalSearchKeyword.value = '';
  appliedGlobalKeyword.value = '';
  globalSearchResults.value = [];
}

function applyLocalSearch() {
  appliedLocalKeyword.value = localSearchKeyword.value.trim();
}

function clearLocalSearch() {
  localSearchKeyword.value = '';
  appliedLocalKeyword.value = '';
}

function getLocalFilteredItems(type: HistorySectionKey | 'HOLIDAY'): TodayHistoryItemDto[] {
  const filtered = items.value.filter(item => item.itemType === type);
  const keyword = normalizedLocalKeyword.value;
  const matched = keyword ? filtered.filter(item => matchesKeyword(item, keyword)) : filtered;
  return sortItems(matched);
}

function matchesKeyword(item: TodayHistoryItemDto, keyword: string): boolean {
  return buildSearchText(item).includes(keyword);
}

function buildSearchText(item: TodayHistoryItemDto): string {
  const linkText = (item.links ?? []).flatMap(link => [link.linkText, link.linkTitle, link.linkUrl]);
  return [
    pageData.value?.pageTitle,
    item.historyKey,
    item.pageTitle,
    item.sectionTitle,
    item.content,
    item.eventYear == null ? '' : String(item.eventYear),
    getEraLabel(item),
    getItemTypeLabel(item.itemType),
    ...linkText,
  ]
    .filter((part): part is string => typeof part === 'string' && part.trim().length > 0)
    .join(' ')
    .toLowerCase();
}

function sortItems(list: TodayHistoryItemDto[]): TodayHistoryItemDto[] {
  return [...list].sort((left, right) => {
    if ((left.itemType || '') === 'EVENT' && (right.itemType || '') === 'EVENT') {
      const leftEra = getEraOrder(left.eraType);
      const rightEra = getEraOrder(right.eraType);
      if (leftEra !== rightEra) {
        return leftEra - rightEra;
      }
    }

    const leftYear = left.eventYear ?? Number.MAX_SAFE_INTEGER;
    const rightYear = right.eventYear ?? Number.MAX_SAFE_INTEGER;
    if (leftYear !== rightYear) {
      return leftYear - rightYear;
    }

    return (left.sortNo ?? 0) - (right.sortNo ?? 0);
  });
}

function getEraOrder(eraType?: TodayHistoryEraTypeCode): number {
  const eraOrderMap: Record<TodayHistoryEraTypeCode, number> = {
    PRE_19TH: 0,
    NINETEENTH: 1,
    TWENTIETH: 2,
    TWENTY_FIRST: 3,
    NONE: 4,
  };
  return eraType ? eraOrderMap[eraType] : eraOrderMap.NONE;
}

function getEraLabel(item: TodayHistoryItemDto): string {
  const eraLabelMap: Record<TodayHistoryEraTypeCode, string> = {
    PRE_19TH: '19世纪前',
    NINETEENTH: '19世纪',
    TWENTIETH: '20世纪',
    TWENTY_FIRST: '21世纪',
    NONE: '',
  };
  return item.eraType ? eraLabelMap[item.eraType] : '';
}

function getItemTypeLabel(itemType?: TodayHistoryItemTypeCode): string {
  const itemTypeMap: Record<TodayHistoryItemTypeCode, string> = {
    EVENT: '大事记',
    BIRTH: '出生',
    DEATH: '逝世',
    HOLIDAY: '节假日',
    UNKNOWN: '其他',
  };
  return itemType ? itemTypeMap[itemType] : '其他';
}

function getItemTypeTheme(itemType?: TodayHistoryItemTypeCode): string {
  const themeMap: Record<TodayHistoryItemTypeCode, string> = {
    EVENT: 'event',
    BIRTH: 'birth',
    DEATH: 'death',
    HOLIDAY: 'holiday',
    UNKNOWN: 'unknown',
  };
  return itemType ? themeMap[itemType] : 'unknown';
}

function getItemMetaText(item: TodayHistoryItemDto): string {
  const parts = [getItemTypeLabel(item.itemType)];
  const yearLabel = getDetailYearLabel(item);
  if (yearLabel) {
    parts.push(yearLabel);
  }
  const eraLabel = getEraLabel(item);
  if (eraLabel) {
    parts.push(eraLabel);
  }
  return parts.filter(Boolean).join(' · ');
}

function getSourceLabel(item: { pageTitle?: string; historyKey?: string; monthValue?: number; dayValue?: number }): string {
  const sourceTitle = item.pageTitle || pageTitle.value;
  const sourceKey = item.historyKey || '';
  if (sourceKey) {
    return `${sourceTitle} · ${sourceKey}`;
  }
  return sourceTitle;
}

function formatItemTitle(item: TodayHistoryItemDto): string {
  return item.eventYear == null ? (item.sectionTitle || '') : formatYearLabel(item);
}

function getDetailYearLabel(item: TodayHistoryItemDto): string {
  if (item.eventYear == null) {
    return item.sectionTitle || '';
  }
  return formatYearLabel(item);
}

function formatYearLabel(item: TodayHistoryItemDto): string {
  if (item.eventYear == null) {
    return item.sectionTitle || '';
  }

  if ((item.itemType || '') === 'EVENT' && item.eventYear < 0) {
    return `前${Math.abs(item.eventYear)}年`;
  }

  return `${item.eventYear}年`;
}

function getContentBody(item: TodayHistoryItemDto): string {
  const content = item.content || '';
  const yearLabel = formatYearLabel(item);
  if (yearLabel && content.startsWith(`${yearLabel}：`)) {
    return content.slice(yearLabel.length + 1).trim();
  }
  if (yearLabel && content.startsWith(`${yearLabel}:`)) {
    return content.slice(yearLabel.length + 1).trim();
  }

  const chineseColonIndex = content.indexOf('：');
  if (chineseColonIndex >= 0) {
    return content.slice(chineseColonIndex + 1).trim();
  }

  const asciiColonIndex = content.indexOf(':');
  if (asciiColonIndex >= 0) {
    return content.slice(asciiColonIndex + 1).trim();
  }

  return content;
}

function buildSummary(item: TodayHistoryItemDto, maxLength: number): string {
  const body = getContentBody(item).replace(/\s+/g, ' ').trim();
  if (!body) {
    return '暂无摘要';
  }
  if (body.length <= maxLength) {
    return body;
  }
  return `${body.slice(0, maxLength).trimEnd()}...`;
}

function openDetail(item: TodayHistorySearchResultDto) {
  detailItem.value = item;
  detailVisible.value = true;
}

async function reload() {
  loading.value = true;
  try {
    const res = await findTodayHistoryDetail(currentHistoryKey.value);
    if (res.code === 200) {
      pageData.value = res.data ?? null;
      if (!pageData.value) {
        layer.msg('未找到历史上的今天数据', { icon: 2 });
      }
      return;
    }

    layer.msg(res.msg || '加载历史上的今天失败', { icon: 2 });
  } catch {
    layer.msg('加载历史上的今天失败', { icon: 2 });
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.today-history-page {
  height: 100%;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(11, 165, 175, 0.18), transparent 34%),
    radial-gradient(circle at top right, rgba(241, 146, 56, 0.14), transparent 28%),
    linear-gradient(180deg, #f7fbfc 0%, #edf4f6 100%);
}

.today-history-shell {
  height: 100%;
  max-width: 1420px;
  margin: 0 auto;
  padding: 10px 12px 14px;
  box-sizing: border-box;
}

.today-history-dashboard {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 10px;
  height: 100%;
  min-height: 0;
}

.query-panel,
.history-panel {
  min-width: 0;
  min-height: 0;
}

.query-panel {
  display: flex;
  min-height: 0;
}

.query-panel > .panel-card {
  flex: 1;
  min-height: 0;
}

.panel-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid rgba(16, 33, 45, 0.08);
  box-shadow: 0 12px 28px rgba(22, 43, 58, 0.07);
}

.panel-card :deep(.layui-card-body) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;
}

.query-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(247, 250, 251, 0.98));
}

.history-search-card {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(16, 33, 45, 0.08);
  box-shadow: 0 10px 22px rgba(22, 43, 58, 0.05);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(248, 250, 251, 0.98));
}

.history-search-card :deep(.layui-card-body) {
  padding: 10px 12px 12px;
}

.query-head {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.query-head--compact {
  gap: 6px;
}

.query-tip {
  margin: 0;
  color: #51606b;
  font-size: 12px;
  line-height: 1.5;
}

.query-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: stretch;
}

.query-input-row {
  width: 100%;
}

.query-input-row :deep(.layui-input) {
  width: 100%;
}

.query-button-row {
  display: flex;
  gap: 8px;
}

.query-button-row :deep(.layui-btn) {
  flex: 1 1 0;
}

.query-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 10px;
  color: #6c7a86;
  font-size: 12px;
}

.query-meta-keyword {
  color: #0b7a84;
  font-weight: 700;
}

.query-result-area {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  overflow: hidden;
}

.query-result-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  max-height: calc(100vh - 480px);
  padding-right: 2px;
  scrollbar-gutter: stable;
  scrollbar-width: thin;
  scrollbar-color: rgba(11, 122, 132, 0.45) rgba(11, 122, 132, 0.08);
}

.query-result-scroll::-webkit-scrollbar {
  width: 10px;
}

.query-result-scroll::-webkit-scrollbar-track {
  background: rgba(11, 122, 132, 0.08);
  border-radius: 999px;
}

.query-result-scroll::-webkit-scrollbar-thumb {
  background: rgba(11, 122, 132, 0.42);
  border-radius: 999px;
  border: 2px solid rgba(255, 255, 255, 0.78);
}

.query-result-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(11, 122, 132, 0.58);
}

.query-result-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.query-result-card {
  padding: 10px 11px;
  border-radius: 14px;
  border: 1px solid rgba(16, 33, 45, 0.08);
  background: #f7fbfc;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.query-result-card:hover {
  transform: translateY(-1px);
  border-color: rgba(11, 122, 132, 0.2);
  box-shadow: 0 8px 18px rgba(22, 43, 58, 0.08);
}

.query-result-head {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  justify-content: space-between;
}

.query-result-title {
  min-width: 0;
  color: #10212d;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.35;
}

.query-result-tag {
  flex: 0 0 auto;
  padding: 2px 7px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
}

.query-result-tag--event {
  background: rgba(11, 122, 132, 0.1);
  color: #0b7a84;
}

.query-result-tag--birth {
  background: rgba(47, 139, 70, 0.1);
  color: #2f8b46;
}

.query-result-tag--death {
  background: rgba(196, 91, 50, 0.1);
  color: #c45b32;
}

.query-result-tag--holiday {
  background: rgba(35, 120, 160, 0.1);
  color: #2378a0;
}

.query-result-tag--unknown {
  background: rgba(107, 119, 130, 0.12);
  color: #5f6c77;
}

.query-result-source {
  margin-top: 5px;
  color: #6b7782;
  font-size: 11px;
}

.query-result-summary {
  margin: 8px 0 0;
  color: #25313c;
  font-size: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.query-result-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: 8px;
  color: #6b7782;
  font-size: 11px;
}

.query-result-foot-left {
  min-width: 0;
}

.query-detail-button {
  flex: 0 0 auto;
}

.query-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  min-height: 220px;
  padding: 18px 14px;
  border-radius: 16px;
  border: 1px dashed rgba(16, 33, 45, 0.12);
  background: linear-gradient(180deg, rgba(244, 248, 250, 0.95), rgba(239, 244, 246, 0.92));
}

.query-placeholder-title {
  color: #10212d;
  font-size: 14px;
  font-weight: 800;
}

.query-placeholder p {
  margin: 0;
  color: #6c7a86;
  font-size: 12px;
  line-height: 1.5;
}

.history-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-end;
  padding: 12px 16px;
  border: 1px solid rgba(14, 41, 53, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 14px 32px rgba(22, 43, 58, 0.08);
  backdrop-filter: blur(10px);
}

.hero-copy {
  min-width: 0;
  flex: 1 1 520px;
}

.eyebrow {
  margin: 0 0 6px;
  color: #0b7a84;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.hero h1 {
  margin: 0;
  color: #10212d;
  font-size: 27px;
  line-height: 1.15;
  font-weight: 800;
}

.hero-description {
  margin: 8px 0 0;
  color: #51606b;
  font-size: 12px;
  line-height: 1.5;
}

.hero-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
  justify-content: flex-end;
  flex: 0 0 auto;
}

.date-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-button {
  width: 34px;
  min-width: 34px;
  padding: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.meta-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}

.meta-card {
  min-width: 88px;
  padding: 9px 11px;
  border-radius: 14px;
  background: #f4f8fa;
  border: 1px solid rgba(16, 33, 45, 0.08);
}

.meta-card--date {
  min-width: 160px;
  padding-left: 12px;
  padding-right: 12px;
}

.meta-label {
  display: block;
  font-size: 11px;
  color: #6c7a86;
  margin-bottom: 5px;
}

.meta-value {
  display: block;
  color: #10212d;
  font-size: 15px;
  font-weight: 700;
}

.history-grid {
  margin-bottom: 8px;
  flex: 1 1 auto;
  min-height: 0;
}

.history-card,
.holiday-card {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(16, 33, 45, 0.08);
  box-shadow: 0 10px 22px rgba(22, 43, 58, 0.06);
}

.history-card :deep(.layui-card-body),
.holiday-card :deep(.layui-card-body) {
  padding-top: 8px;
}

.history-card {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.history-card--event {
  background: linear-gradient(180deg, rgba(248, 252, 253, 0.96), rgba(245, 250, 251, 0.96));
}

.history-card--birth {
  background: linear-gradient(180deg, rgba(246, 252, 247, 0.96), rgba(242, 249, 244, 0.96));
}

.history-card--death {
  background: linear-gradient(180deg, rgba(255, 248, 246, 0.96), rgba(252, 244, 241, 0.96));
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  font-weight: 700;
  font-size: 13px;
  color: #10212d;
}

.timeline-wrap {
  max-height: 29vh;
  overflow-y: scroll;
  overflow-x: hidden;
  padding-right: 6px;
  scrollbar-gutter: stable;
  scrollbar-width: thin;
  scrollbar-color: rgba(11, 122, 132, 0.45) rgba(11, 122, 132, 0.08);
}

.timeline-wrap::-webkit-scrollbar {
  width: 10px;
}

.timeline-wrap::-webkit-scrollbar-track {
  background: rgba(11, 122, 132, 0.08);
  border-radius: 999px;
}

.timeline-wrap::-webkit-scrollbar-thumb {
  background: rgba(11, 122, 132, 0.42);
  border-radius: 999px;
  border: 2px solid rgba(255, 255, 255, 0.78);
}

.timeline-wrap::-webkit-scrollbar-thumb:hover {
  background: rgba(11, 122, 132, 0.58);
}

.timeline-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 999px;
  box-shadow: 0 0 0 5px rgba(255, 255, 255, 0.7);
}

.timeline-dot--event {
  background: #0b7a84;
}

.timeline-dot--birth {
  background: #2f8b46;
}

.timeline-dot--death {
  background: #c45b32;
}

.timeline-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-bottom: 4px;
}

.era-pill {
  width: fit-content;
  padding: 2px 7px;
  border-radius: 999px;
  background: rgba(11, 122, 132, 0.1);
  color: #0b7a84;
  font-size: 11px;
  font-weight: 700;
}

.timeline-text {
  margin: 0;
  color: #25313c;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.link-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.link-label {
  color: #6b7782;
  font-size: 12px;
  font-weight: 700;
}

.link-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.link-item {
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(11, 122, 132, 0.08);
  color: #0b7a84;
  font-size: 11px;
  text-decoration: none;
  transition: transform 0.18s ease, background 0.18s ease;
}

.link-item:hover {
  transform: translateY(-1px);
  background: rgba(11, 122, 132, 0.14);
}

.holiday-card {
  margin-top: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(248, 250, 251, 0.98));
}

.holiday-summary {
  color: #374754;
  font-size: 13px;
  line-height: 1.5;
}

.holiday-footer {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-items: flex-start;
  gap: 8px 10px;
}

.holiday-footer-label {
  flex: 0 0 auto;
  color: #6b7782;
  font-size: 12px;
  font-weight: 700;
  padding-top: 3px;
}

.holiday-chip-list {
  flex: 1 1 320px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.holiday-chip {
  padding: 0 9px;
  border-radius: 999px;
  background: #eef4f6;
  color: #22313c;
  font-size: 12px;
  border: 1px solid rgba(16, 33, 45, 0.06);
}

.empty-state {
  padding: 12px 0;
  color: #81909b;
  font-size: 13px;
}

.detail-layer-body {
  max-height: 560px;
  overflow: auto;
  padding: 18px 18px 10px;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.detail-pill {
  padding: 4px 9px;
  border-radius: 999px;
  background: rgba(11, 122, 132, 0.1);
  color: #0b7a84;
  font-size: 12px;
  font-weight: 700;
}

.detail-pill--muted {
  background: #eef4f6;
  color: #3a4a55;
}

.detail-section {
  margin-bottom: 14px;
}

.detail-section-label {
  margin-bottom: 6px;
  color: #6b7782;
  font-size: 12px;
  font-weight: 700;
}

.detail-section-body {
  color: #25313c;
  font-size: 13px;
  line-height: 1.7;
}

.detail-summary {
  padding: 10px 12px;
  border-radius: 12px;
  background: #f7fbfc;
  border: 1px solid rgba(16, 33, 45, 0.08);
}

.detail-content {
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px 12px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid rgba(16, 33, 45, 0.08);
}

.detail-link-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-link-item {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(11, 122, 132, 0.08);
  color: #0b7a84;
  font-size: 12px;
  text-decoration: none;
}

.detail-link-item:hover {
  background: rgba(11, 122, 132, 0.14);
}

@media (max-width: 992px) {
  .today-history-page {
    overflow: auto;
  }

  .today-history-shell {
    height: auto;
  }

  .today-history-dashboard {
    grid-template-columns: 1fr;
    height: auto;
  }

  .query-panel > .panel-card {
    height: auto;
  }

  .hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .hero h1 {
    font-size: 26px;
  }

  .hero-meta {
    width: 100%;
    align-items: stretch;
  }

  .date-nav,
  .meta-grid {
    justify-content: flex-start;
  }

  .timeline-wrap {
    max-height: none;
  }

  .query-result-scroll {
    max-height: none;
  }

  .holiday-footer {
    flex-direction: column;
  }

  .holiday-chip-list {
    flex-basis: 60%;
  }
}
</style>
