import {BaseEntity, BaseVo} from "../Common";

export interface UrlConfigVo extends BaseVo {
    code?: string;
    url?: string;
    requestType?: string;
    header?: string;
    param?: string;
    contentType?: string;
    remark?: string;
}

export interface UrlConfigEntity extends BaseEntity {
    code?: string;
    url?: string;
    requestType?: string;
    header?: string;
    param?: string;
    contentType?: string;
    remark?: string;
}
