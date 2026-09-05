<template>
  <div class="today-history-page">
    <lay-container :fluid="true" class="today-history-shell">
      <section class="hero">
        <div class="hero-copy">
          <p class="eyebrow">历史上的今天</p>
          <h1>{{ pageTitle }}</h1>
          <p class="hero-description">
            三列展示大事记、出生、逝世，底部集中呈现节假日和习俗信息。
          </p>
        </div>

        <div class="hero-meta">
          <div class="meta-card">
            <span class="meta-label">日期</span>
            <span class="meta-value">{{ displayDate }}</span>
          </div>
          <div class="meta-card">
            <span class="meta-label">条目</span>
            <span class="meta-value">{{ totalCount }}</span>
          </div>
          <div class="meta-card">
            <span class="meta-label">状态</span>
            <span class="meta-value">{{ loading ? '加载中' : '已完成' }}</span>
          </div>
          <lay-button type="primary" size="sm" @click="reload">刷新今天</lay-button>
        </div>
      </section>

      <lay-row :space="12" class="history-grid">
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
              暂无数据
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
    </lay-container>
  </div>
</template>

<script lang="ts">
export default {
  name: "TodayHistory",
};
</script>

<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import {layer} from '@layui/layui-vue';
import {formatDateTime, getWeekdayCn} from "@/util/UDate";
import {findTodayHistoryDetail} from "@/api/todayhistory/TodayHistory";
import {R} from "@/types/Result";
import {
  TodayHistoryEraTypeCode,
  TodayHistoryItemDto,
  TodayHistoryPageDto
} from "@/types/workSpace/TodayHistory";

type HistorySectionKey = 'EVENT' | 'BIRTH' | 'DEATH'

interface SectionCard {
  key: HistorySectionKey;
  title: string;
  cardClass: string;
  dotClass: string;
  items: TodayHistoryItemDto[];
}

const loading = ref(false);
const pageData = ref<TodayHistoryPageDto | null>(null);

const historyKey = computed(() => formatDateTime(new Date(), 'MM-dd'));
const displayDate = computed(() => `${formatDateTime(new Date(), 'yyyy年MM月dd日')} ${getWeekdayCn(new Date())}`);
const pageTitle = computed(() => pageData.value?.pageTitle || '历史上的今天');

const totalCount = computed(() => {
  return pageData.value?.itemCount ?? pageData.value?.items?.length ?? 0;
});

const items = computed(() => pageData.value?.items ?? []);

const eventItems = computed(() => sortItems(items.value.filter(item => item.itemType === 'EVENT')));
const birthItems = computed(() => sortItems(items.value.filter(item => item.itemType === 'BIRTH')));
const deathItems = computed(() => sortItems(items.value.filter(item => item.itemType === 'DEATH')));
const holidayItems = computed(() => sortItems(items.value.filter(item => item.itemType === 'HOLIDAY')));

const holidaySummary = computed(() => {
  if (holidayItems.value.length === 0) {
    return '当前日期未返回节假日和习俗信息。';
  }
  return holidayItems.value
    .slice(0, 4)
    .map(item => item.content || '')
    .filter(Boolean)
    .join('；');
});

const sectionCards = computed<SectionCard[]>(() => {
  return [
    {
      key: 'EVENT',
      title: '大事记',
      cardClass: 'history-card--event',
      dotClass: 'timeline-dot--event',
      items: eventItems.value
    },
    {
      key: 'BIRTH',
      title: '出生',
      cardClass: 'history-card--birth',
      dotClass: 'timeline-dot--birth',
      items: birthItems.value
    },
    {
      key: 'DEATH',
      title: '逝世',
      cardClass: 'history-card--death',
      dotClass: 'timeline-dot--death',
      items: deathItems.value
    }
  ];
});

onMounted(() => {
  void reload();
});

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
    NONE: 4
  };
  return eraType ? eraOrderMap[eraType] : eraOrderMap.NONE;
}

function getEraLabel(item: TodayHistoryItemDto): string {
  const eraLabelMap: Record<TodayHistoryEraTypeCode, string> = {
    PRE_19TH: '19世纪前',
    NINETEENTH: '19世纪',
    TWENTIETH: '20世纪',
    TWENTY_FIRST: '21世纪',
    NONE: ''
  };
  return item.eraType ? eraLabelMap[item.eraType] : '';
}

function formatItemTitle(item: TodayHistoryItemDto): string {
  return item.eventYear == null ? (item.sectionTitle || '') : formatYearLabel(item);
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

function reload() {
  loading.value = true;
  findTodayHistoryDetail(historyKey.value)
    .then((res: R<TodayHistoryPageDto>) => {
      if (res.code === 200) {
        pageData.value = res.data ?? null;
        if (!pageData.value) {
          layer.msg('未找到历史上的今天数据', {icon: 2});
        }
        return;
      }
      layer.msg(res.msg || '加载历史上的今天失败', {icon: 2});
    })
    .catch(() => {
      layer.msg('加载历史上的今天失败', {icon: 2});
    })
    .finally(() => {
      loading.value = false;
    });
}
</script>

<style scoped>
.today-history-page {
  min-height: 100%;
  background:
    radial-gradient(circle at top left, rgba(11, 165, 175, 0.18), transparent 34%),
    radial-gradient(circle at top right, rgba(241, 146, 56, 0.14), transparent 28%),
    linear-gradient(180deg, #f7fbfc 0%, #edf4f6 100%);
}

.today-history-shell {
  max-width: 1280px;
  margin: 0 auto;
  padding: 12px 14px 18px;
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-end;
  padding: 14px 18px;
  margin-bottom: 12px;
  border: 1px solid rgba(14, 41, 53, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 16px 36px rgba(22, 43, 58, 0.08);
  backdrop-filter: blur(10px);
}

.hero-copy {
  min-width: 0;
}

.eyebrow {
  margin: 0 0 8px;
  color: #0b7a84;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.hero h1 {
  margin: 0;
  color: #10212d;
  font-size: 30px;
  line-height: 1.15;
  font-weight: 800;
}

.hero-description {
  margin: 8px 0 0;
  color: #51606b;
  font-size: 13px;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: flex-end;
}

.meta-card {
  min-width: 96px;
  padding: 10px 12px;
  border-radius: 14px;
  background: #f4f8fa;
  border: 1px solid rgba(16, 33, 45, 0.08);
}

.meta-label {
  display: block;
  font-size: 12px;
  color: #6c7a86;
  margin-bottom: 6px;
}

.meta-value {
  display: block;
  color: #10212d;
  font-size: 16px;
  font-weight: 700;
}

.history-grid {
  margin-bottom: 10px;
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
  padding-top: 10px;
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
  max-height: 56vh;
  overflow: auto;
  padding-right: 6px;
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
  padding: 5px 9px;
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

@media (max-width: 992px) {
  .hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .hero h1 {
    font-size: 28px;
  }

  .timeline-wrap {
    max-height: none;
  }

  .holiday-footer {
    flex-direction: column;
  }

  .holiday-chip-list {
    flex-basis: 100%;
  }
}
</style>
