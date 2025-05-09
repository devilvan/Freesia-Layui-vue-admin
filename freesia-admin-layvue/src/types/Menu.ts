export enum RouterComponent {
    BASE_LAYOUT = "BaseLayout",
    BLANK_LAYOUT = "BlankLayout",
    INNER_LINK = "InnerLink"
}

export enum LinkComponentType {
    MODAL = "modal",
    BLANK = "blank",
    INNER_LINK = "innerLink"
}

export interface MenuTree {
    id?: string;
    icon?: string;
    menuName?: string;
    orderNum?: number;
    parentId?: string;
    visible?: string;
    children?: MenuTree[]
}