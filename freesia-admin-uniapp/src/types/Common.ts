export interface BaseVo {
    id?: string;
    creator?: string;
    createTime?: Date;
    modifier?: string;
    modifyTime?: Date;
    logicDel?: 0;
    recVer?: number;
    buildIn?: boolean | true;
    tenantId?: string;
}

export interface BaseEntity {
    id?: string;
    creator?: string;
    createTime?: Date;
    modifier?: string;
    modifyTime?: Date;
    logicDel?: 0;
    recVer?: number;
    buildIn?: boolean | true;
    tenantId?: string;
}

export interface PageQuery {
    current?: number;
    limit?: number;
    total?: number;
    pageNum?: number;
    pageSize?: number;
    limits?: number[];
    ellipsisTooltip?: boolean;
    layout?: string[];
    hideOnSinglePage?: boolean;
}

export interface LaySelectEntity {
    label?: string;
    value?: string;
    defaultFlag?: boolean;
    disabled?: boolean;
}