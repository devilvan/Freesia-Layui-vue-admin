import Http from '../../Http'
import { buildUrlParam } from '../../../util/URequest'
import { R } from '../../../types/Result'
import { FindCommonIconEntity } from '../../../types/common/icon/Icon'

/**
 * 查询通用图标选择器数据（按分区/分组返回）
 */
export function findCommonIconPicker(vo: Record<string, any>): Promise<R<Record<string, FindCommonIconEntity[]>>> {
    const params = buildUrlParam(vo)
    return Http.get('/common/commonIconController/findCommonIconPicker', params)
}
