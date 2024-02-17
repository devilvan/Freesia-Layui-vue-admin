import {PageQuery} from "../types/Common";

export function buildUrlParam(obj: any): any {
    const params: any = {};
    for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
            params[key] = obj[key];
        }
    }
    return params;
}


export function buildPageUrlParam(obj: any, page: PageQuery): any {
    const params: any = {};
    for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
            params[key] = obj[key];
        }
    }
    params['current'] = page.current;
    params['limit'] = page.limit;
    return params;
}
