import { BaseVo } from '../../../Common'
import { FindCommonIconEntity } from '../Icon'

/**
 * 图标树节点类型，包含分组名(name) 和 children(图标列表)
 */
export interface FindTreeIconTreeTypeEntity {
    /** 分组名称，如"餐饮"、"交通" */
    name?: string
    /** 分组下的图标列表 */
    children?: FindCommonIconEntity[]
    /** 分组 ID */
    id?: string
    /** 父分组 ID */
    parentId?: string
    /** 排序号 */
    orderNum?: number
    /** 分组标识 */
    grouping?: string
}

export interface CommonIconTemplateDetailVo extends BaseVo {
    headerId?: string
    name?: string
    grouping?: string
    orderNum?: number
    remark?: string
    iconTreeType?: string
    iconId?: string
    parentId?: string
    originName?: string
    url?: string
    multipleIconList?: FindCommonIconEntity[]
    idList?: string[]
}

export interface FindMaxOrderNumVo extends BaseVo {
    headerId?: string
    name?: string
    grouping?: string
    iconTreeType?: string
    parentId?: string
}
