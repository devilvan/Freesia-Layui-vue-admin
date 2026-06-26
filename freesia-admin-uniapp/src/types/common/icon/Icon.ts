import { BaseEntity, BaseVo } from '../../Common'

export interface FindCommonIconEntity extends BaseEntity {
    name?: string
    fileId?: string
    fileName?: string
    iconPartition?: string
    remark?: string
    url?: string
}

export interface CommonIconVo extends BaseVo {
    name?: string
    fileId?: string
    iconPartition?: string
    remark?: string
    idList?: string[]
}
