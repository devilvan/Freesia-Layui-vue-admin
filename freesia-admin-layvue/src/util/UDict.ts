import {findCacheSysDictValueList} from "../api/system/Dict";
import {MatchDictValueModel, SysDictValueEntity} from "../types/system/Dict";

/**
 * 可选数据字典
 */
export enum Constants {
    /**
     * 菜单类型
     */
    SYS_MENU_TYPE = "SYS_MENU_TYPE",
    /**
     * 性别
     */
    SYS_GENDER = "SYS_GENDER",
    /**
     * 操作结果
     */
    SYS_OPERATE_RESULT = "SYS_OPERATE_RESULT",
    /**
     * 数据范围
     */
    SYS_DATA_SCOPE = "SYS_DATA_SCOPE",
    /**
     * 请求类型
     */
    REQUEST_TYPE = "REQUEST_TYPE"
}

export const loadSysDictValue = async (dictKey: string): Promise<Array<SysDictValueEntity>> => {
    let {data} = await findCacheSysDictValueList({dictKey: dictKey});
    let sysMenuTypeArr: Array<SysDictValueEntity> = []
    if (data && data.length > 0) {
        sysMenuTypeArr = data.map((item: SysDictValueEntity) => {
            return {
                ...item
            }
        })
    }
    return sysMenuTypeArr;
}

export function findDictInListByValue(list: Array<SysDictValueEntity>, value: string): SysDictValueEntity {
    const result = list.find(f => value === f.value);
    if ("undefined" === typeof (result)) {
        return {}
    }
    return result;
}

export function matchDictValue(list: Array<SysDictValueEntity>, value: string): MatchDictValueModel {
    if ("undefined" === typeof (value)) {
        return {};
    }
    let valueName: string | undefined = "";
    let cssStyle: string | undefined = "";
    for (let i = 0; i < list.length; i++) {
        let sysDictValueEntity = list[i];
        if (sysDictValueEntity.value === value) {
            valueName = sysDictValueEntity.valueName;
            cssStyle = sysDictValueEntity.cssStyle;
            return {valueName, cssStyle};
        }
    }
    return {};
}

export function isMatchDictValue(list: Array<SysDictValueEntity>, value: string): boolean {
    if (!list || list.length < 1) {
        return false;
    }
    if ("undefined" === typeof (value)) {
        return false;
    }
    for (let i = 0; i < list.length; i++) {
        let sysDictValueEntity = list[i];
        if (sysDictValueEntity.value === value) {
            return true;
        }
    }
    return false;
}


export const sysDictValueSelect = async (list: Array<SysDictValueEntity>) : Promise<any[]> => {
    let sysDictValueSelect: any[] = [];
    if (list && list.length > 0) {
        list?.forEach(sysMenuType => {
            let obj = {
                label: sysMenuType.valueName,
                value: sysMenuType.value,
                disabled: sysMenuType.status === '1'
            }
            sysDictValueSelect.push(obj)
        })
    }
    return sysDictValueSelect;
}
