import {BaseEntity, BaseVo} from "../Common";

export interface SysTenantVo extends BaseVo {
    code?: string;
    name?: string;
    type?: string;
    status?: boolean | false;
    remark?: string;
    contactName?: string;
    contactTel?: string;
    contactEmail?: string;
    address?: string;
    businessHoursFrom?: string;
}

export interface SysTenantEntity extends BaseEntity {
    code?: string;
    name?: string;
    type?: string;
    status?: boolean | false;
    remark?: string;
    contactName?: string;
    contactTel?: string;
    contactEmail?: string;
    address?: string;
    businessHoursFrom?: Date;
}

export interface AssignTenantVo {
    tenantId: string,
    userIdList: Array<string>,
}
