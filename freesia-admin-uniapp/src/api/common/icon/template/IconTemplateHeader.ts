import Http from '../../../Http'
import { R } from '../../../../types/Result'

export interface LaySelectEntity {
    label?: string
    value?: string
    defaultFlag?: boolean
}

/**
 * 查询可选图标模板头列表（含默认标识）
 */
export function findSelectCommonIconHeader(): Promise<R<LaySelectEntity[]>> {
    return Http.get('/common/commonIconTemplateHeaderController/findSelectCommonIconHeader')
}
