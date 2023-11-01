import {BaseVo} from "../Common";

/**
 * 查询参数
 */

export interface SysRoleVo {
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
