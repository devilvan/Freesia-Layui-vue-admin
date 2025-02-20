/**
 * 查询参数
 */
import {Tree} from "../Result";
import {BaseEntity, BaseVo} from "../Common";

export interface SysDeptVo extends BaseVo {
    parentId?: string;
    ancestors?: string;
    deptName?: string;
    orderNum?: string;
    leader?: string;
    telNo?: string;
    email?: string;
    deptStatus?: string;
    remark?: string;
    createTimeFrom?: Date;
    createTimeTo?: Date;
}

/**
 * 部门下拉树结构
 */
export interface FindPageSysDeptListEntity extends Tree<FindPageSysDeptListEntity> {
    ancestors: string;
    deptName: string;
    orderNum: number;
    leader: string;
    telNo: string;
    email: string;
    deptStatus: string;
    remark: string;
}

export interface FindPageSysUserByDeptEntity {
    id?: string;
    deptId?: string;
    nickName?: string;
    userName?: string;
    email?: string;
    telNo?: string;
    gender?: string;
    accountStatus?: string;
    logicDel?: boolean;
    createTime?: Date
    creator?: string;
    remark?: string;
    deptName?: string;
    leader?: string;
}

export interface SysDeptEntity extends BaseEntity {
    parentId?: string;
    ancestors?: string;
    deptName?: string;
    orderNum?: string;
    leader?: string;
    telNo?: string;
    email?: string;
    deptStatus?: string;
    remark?: string;
    createTimeFrom?: Date;
    createTimeTo?: Date;
}

export interface SysDeptSelectEntity extends Tree<SysDeptSelectEntity> {
    title?: string;
    field?: string;
}

export interface FindDeptRolesByDeptIdEntity extends BaseEntity {
    deptId?: string,
    deptName?: string,
    selectedRoles?: string[]
}

export interface AssignRoleVo extends BaseVo {
    deptId: string,
    afterRoleIdSet: string[]
}