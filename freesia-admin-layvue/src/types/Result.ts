import {BaseEntity} from "./Common";

/**
 * 表格数据返回类
 */
export interface TableResult<T> {
    total?: number;
    rows?: Array<T>;
    code?: 200;
    msg?: string;
}

/**
 * 树结构父类
 */
export interface Tree<T> extends BaseEntity {
    parentName: string;
    parentId: string;
    children?: Array<T>;
}
