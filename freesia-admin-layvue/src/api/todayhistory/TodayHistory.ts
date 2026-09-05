import Http from "../Http";
import {R} from "@/types/Result";
import {TodayHistoryPageDto, TodayHistorySearchResultDto} from "@/types/workSpace/TodayHistory";

export function findTodayHistoryDetail(historyKey: string): Promise<R<TodayHistoryPageDto>> {
    return Http.get(`/api/todayHistoryController/findTodayHistoryDetail/${encodeURIComponent(historyKey)}`);
}

export function searchTodayHistory(keyword: string): Promise<R<TodayHistorySearchResultDto[]>> {
    return Http.get(`/api/todayHistoryController/searchTodayHistory`, {keyword});
}
