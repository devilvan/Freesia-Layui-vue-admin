import {buildUrlParam} from "@/util/URequest";
import Http from "../Http";
import {R} from "@/types/Result";
import {FindCitySunriseSunsetEntity, FindCitySunriseSunsetReqDto} from "@/types/workSpace/WorldClock";

export function findCitySunriseSunset(searchQuery: FindCitySunriseSunsetReqDto): Promise<R<FindCitySunriseSunsetEntity[]>> {
    let params = buildUrlParam(searchQuery);
    return Http.get("/api/worldClockCityController/findCitySunriseSunset", params);
}
