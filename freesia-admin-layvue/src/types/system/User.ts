/**
 * system/user 查询参数
 */
import {BaseEntity, BaseVo} from "../Common";

export interface SysUserVo extends BaseVo {
    deptId?: string;
    nickName?: string;
    userName?: string;
    email?: string;
    telNo?: string;
    gender?: string;
    remark?: string;
    createTimeFrom?: Date
    createTimeTo?: Date;
    avatar?: string
}

export interface SysUserEntity extends BaseEntity {
    deptId? : string;
    userName? : string;
    nickName? : string;
    userType? : string;
    email? : string;
    telNo? : string;
    gender? : string;
    avatar? : string;
    password? : string;
    accountStatus? : string;
    remark? : string;
}


export interface FindPageSysUserListEntity {
    id?: string;
    deptId?: string;
    nickName?: string;
    userName?: string;
    email?: string;
    telNo?: string;
    gender?: string;
    accountStatus?: string;
    avatar?: string,
    logicDel?: boolean;
    createTime?: Date
    creator?: string;
    remark?: string;
    deptName?: string;
    leader?: string;
}

export interface FindUserRolesByUserIdEntity {
    userId?: string,
    userName?: string,
    selectedRoles?: string[]
}

export interface AssignRoleVo {
    userId: string,
    afterRoleIdSet: string[]
}

export interface AssignDeptVo {
    userIdList?: Array<string>,
    deptId?: string,
}