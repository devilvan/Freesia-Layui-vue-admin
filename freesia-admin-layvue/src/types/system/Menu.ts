/**
 * 按钮【分配权限】 通过userId查询可分配权限菜单
 */
import {BaseVo} from "../Common";
import {Tree} from "../Result";

export interface FindAllMenuTreeEntity extends Tree<FindAllMenuTreeEntity> {
    menuName?: string;
    orderNum?: number;
    visible?: string;
    status?: string;
    icon?: string;
}

export interface FindMenuListByUserIdEntity extends Tree<FindMenuListByUserIdEntity> {
    menuName?: string;
    menuType?: string;
    orderNum?: number;
    icon?: string;
    path?: string;
    component?: string;
    visible?: string;
    status?: string;
    remark?: string;
}

export interface SysMenuVo extends BaseVo {
    menuName?: string;
    parentId?: number;
    orderNum?: number;
    path?: string;
    component?: string;
    componentType? :string;
    queryParam?: string;
    isFrame?: string;
    isCache?: string;
    menuType?: string;
    visible?: string;
    status?: string;
    perms?: string;
    icon?: string;
}
