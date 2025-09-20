import {findCacheSysDictValueList} from "../api/system/Dict";
import {MatchDictValueModel, SysDictValueEntity} from "../types/system/Dict";
import {Flag} from "../types/Constants";

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
    REQUEST_TYPE = "REQUEST_TYPE",
    /**
     * 租户类型
     */
    SYS_TENANT_TYPE = "SYS_TENANT_TYPE",
    /**
     * 开支标识
     */
    PAYMENT_SIGN = "PAYMENT_SIGN",
    /**
     * 启用标识
     */
    ENABLED_STATUS = "ENABLED_STATUS",
    /**
     * 预算日期类型
     */
    ACCOUNT_BUDGET_DURATION_TYPE = "ACCOUNT_BUDGET_DURATION_TYPE",
    /**
     * 通用图标所属分区
     */
    COMMON_ICON_PARTITION = "COMMON_ICON_PARTITION",
    /**
     * 通用图标节点类型
     */
    ICON_TREE_TYPE = "ICON_TREE_TYPE",
    /**
     * 通知类型
     */
    SYS_NOTICE_TYPE = "SYS_NOTICE_TYPE",
    /**
     * 消息所属模块
     */
    SYS_NOTICE_CATEGORY = "SYS_NOTICE_CATEGORY",
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


export const sysDictValueSelect = async (list: Array<SysDictValueEntity>): Promise<any[]> => {
    let sysDictValueSelect: any[] = [];
    if (list && list.length > 0) {
        list?.forEach(sysDictValueEntity => {
            let obj = {
                label: sysDictValueEntity.valueName,
                value: sysDictValueEntity.value,
                disabled: sysDictValueEntity.status === Flag.DISABLED,
                isDefault: sysDictValueEntity.isDefault
            }
            sysDictValueSelect.push(obj)
        })
    }
    return sysDictValueSelect;
}
