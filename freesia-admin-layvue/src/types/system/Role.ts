import {BaseEntity, BaseVo} from "../Common";
import {SysUserEntity} from "./User";

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

export interface FindDeptRolesByDeptIdEntity extends BaseEntity {
    roleId?: string,
    roleName?: string,
    selectedDept?: string[]
}