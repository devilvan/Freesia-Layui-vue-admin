import {BaseVo} from "../Common";
import {SysMenuEntity} from "@/types/system/Menu";
import {Tree} from "@/types/Result";

/**
 * 查询参数
 */

export interface SysRoleVo extends BaseVo {
    roleName?: string;
    roleKey?: string;
    status?: string;
    remark?: string;
    createTimeFrom?: Date;
    createTimeTo?: Date;
}

/**
 * 查询角色列表 结果集
 */
export interface FindPageSysRoleListEntity {
    id: number;
    roleName: string;
    roleKey: string;
    status: string;
    orderNum: number;
    dataScope: string;
    menuCheckStrictly: string;
    deptCheckStrictly: string;
    remark: string;
    logicDel: boolean;
    createTime: Date;
}

export interface SaveRoleMenuPrivilegeVo extends BaseVo {
    roleName?: string;
    roleId?: string,
    dataScope?: string;
    treeSelectedIdList?: Array<string>;
}

export interface SysRoleEntity {
    id?: string,
    roleKey?: string,
    roleName?: string,
    dataScope?: string,
    status?: string,
    remark?: string,
}

export interface AssignUserVo {
    roleId: string;
    userIdList: string[]
}

export interface AssignDeptVo {
    roleId?: string,
    deptIdList?: Array<string>,
}

export interface SaveRoleVo extends BaseVo {
    roleName?: string;
    roleKey?: string;
    status?: string;
    orderNum?: number;
    dataScope?: string;
    remark?: string;
}

export interface FindSelectedMenuListByRoleIdEntity extends Tree<FindSelectedMenuListByRoleIdEntity> {
    id?: string;
    menuName?: string;
    orderNum?: number;
    visible?: string;
}