import {BaseEntity, BaseVo} from "../Common";

export interface SysDeptVo extends BaseVo {
    deptName?: string;
    parentId?: string;
    ancestors?: string;
    orderNum?: number;
    leader?: string;
    phone?: string;
    email?: string;
    status?: string;
}

export interface SysDeptEntity extends BaseEntity {
    deptName?: string;
    parentId?: string;
    ancestors?: string;
    orderNum?: number;
    leader?: string;
    phone?: string;
    email?: string;
    status?: string;
    children?: SysDeptEntity[];
}
