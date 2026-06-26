import Http from '../../../Http'
import { buildUrlParam } from '../../../../util/URequest'
import { R } from '../../../../types/Result'
import { CommonIconTemplateDetailVo, FindTreeIconTreeTypeEntity, FindMaxOrderNumVo } from '../../../../types/common/icon/template/IconTemplateDetail'

/**
 * 查询自定义图标分组树（用于图标选择器）
 */
export function findCustomIconTemplateDetail(vo: CommonIconTemplateDetailVo): Promise<R<FindTreeIconTreeTypeEntity[]>> {
    const params = buildUrlParam(vo)
    return Http.get('/common/commonIconTemplateDetailController/findCustomIconTemplateDetail', params)
}

/**
 * 查询图标分组下拉数据
 */
export function findGrouping(vo: CommonIconTemplateDetailVo): Promise<R<Record<string, string>>> {
    const params = buildUrlParam(vo)
    return Http.get('/common/commonIconTemplateDetailController/findGrouping', params)
}

/**
 * 保存/更新图标模板明细
 */
export function saveUpdateIconTemplateDetail(vo: CommonIconTemplateDetailVo): Promise<R<void>> {
    return Http.post('/common/commonIconTemplateDetailController/saveUpdate', vo)
}

/**
 * 查询最大排序号
 */
export function findMaxOrderNum(vo: FindMaxOrderNumVo): Promise<R<number>> {
    const params = buildUrlParam(vo)
    return Http.get('/common/commonIconTemplateDetailController/findMaxOrderNum', params)
}
