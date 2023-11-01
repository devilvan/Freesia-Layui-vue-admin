/**
 * system/user 查询参数
 */
import {BaseVo} from "../Common";

export interface SysUserVo extends BaseVo {
    deptId?: string;
    nickName?: string;
    userName?: string;
    email?: string;
    telNo?: string;
    gender?: string;
    remark?: string;
    createTimeFrom?: Date
    createTimeTo?: Date
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
