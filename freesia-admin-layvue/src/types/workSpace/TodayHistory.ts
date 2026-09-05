export type TodayHistoryItemTypeCode = 'EVENT' | 'BIRTH' | 'DEATH' | 'HOLIDAY' | 'UNKNOWN'

export type TodayHistoryEraTypeCode = 'PRE_19TH' | 'NINETEENTH' | 'TWENTIETH' | 'TWENTY_FIRST' | 'NONE'

export interface TodayHistoryLinkDto {
    id?: number;
    pageId?: number;
    itemId?: number;
    historyKey?: string;
    linkText?: string;
    linkUrl?: string;
    linkTitle?: string;
    internalFlag?: boolean;
    sortNo?: number;
    linkHash?: string;
}

export interface TodayHistoryItemDto {
    id?: number;
    pageId?: number;
    historyKey?: string;
    itemType?: TodayHistoryItemTypeCode;
    eraType?: TodayHistoryEraTypeCode;
    sectionTitle?: string;
    eventYear?: number | null;
    sortNo?: number;
    itemHash?: string;
    content?: string;
    links?: TodayHistoryLinkDto[];
}

export interface TodayHistoryPageDto {
    id?: number;
    monthValue?: number;
    dayValue?: number;
    historyKey?: string;
    pageTitle?: string;
    pageUrl?: string;
    contentHash?: string;
    lastSyncTime?: string;
    itemCount?: number;
    rawHtml?: string;
    items?: TodayHistoryItemDto[];
}
