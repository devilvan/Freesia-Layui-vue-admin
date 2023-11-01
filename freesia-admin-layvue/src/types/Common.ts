/**
 * 基本审计属性
 */
export interface BaseVo {
    id?: string;
    creator?: string;
    createTime?: Date;
    modifier?: string;
    modifyTime?: Date;
    logicDel?: 0;
    recVer?: number;
}

/**
 * 基本审计属性 结果集
 */
export interface BaseEntity {
    id?: string;
    creator?: string;
    createTime?: Date;
    modifier?: string;
    modifyTime?: Date;
    logicDel?: 0;
    recVer?: number;
}

/**
 * 分页参数 值对象
 */
export interface PageQuery {
    current?: number;
    limit?: number;
    total?: number;
}
