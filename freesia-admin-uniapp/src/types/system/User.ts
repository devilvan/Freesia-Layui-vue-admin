import {BaseEntity, BaseVo} from "../Common";

export interface SysUserVo extends BaseVo {
    userName?: string;
    nickName?: string;
    gender?: string;
    telNo?: string;
    email?: string;
    remark?: string;
    avatar?: string;
    deptId?: string;
    userType?: string;
    status?: string;
    roles?: string[];
    permissions?: string[];
}

export interface SysUserEntity extends BaseEntity {
    userName?: string;
    nickName?: string;
    gender?: string;
    telNo?: string;
    email?: string;
    remark?: string;
    avatar?: string;
    deptId?: string;
    deptName?: string;
    userType?: string;
    status?: string;
}

export interface FindPageSysUserListEntity extends BaseEntity {
    userName?: string;
    nickName?: string;
    userType?: string;
}

export interface AssignRoleVo {
    userId?: string;
    roleIdList?: string[];
}

export interface AssignDeptVo {
    userId?: string;
    deptId?: string;
}
