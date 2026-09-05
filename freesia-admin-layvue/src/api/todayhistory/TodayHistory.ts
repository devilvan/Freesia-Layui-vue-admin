import Http from "../Http";
import {R} from "@/types/Result";
import {TodayHistoryPageDto} from "@/types/workSpace/TodayHistory";

export function findTodayHistoryDetail(historyKey: string): Promise<R<TodayHistoryPageDto>> {
    return Http.get(`/api/todayHistoryController/findTodayHistoryDetail/${encodeURIComponent(historyKey)}`);
}
