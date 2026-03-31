// 系统自定义列工具类
import {h, VNode} from "vue";
import {LayCheckbox, LayDropdown} from "@layui/layui-vue";
import {LayIcon} from "@layui/icons-vue";
import {findSysColumnHeader} from "@/api/system/ColumnHeader";
import {TableColumn} from "@layui/layui-vue/types/component/table/typing";
import {DefaultColumnVo, SysColumnHeaderEntity, SysColumnHeaderVo} from "@/types/system/ColumnHeader";
import {R} from "@/types/Result";
import {SysColumnDetailEntity} from "@/types/system/ColumnDetail";
import {Sorted} from "@/types/Constants";
import {saveUpdate} from "@/api/system/ColumnDetail";

/**
 * 构建系统自定义列
 * @param name 组件名称
 * @param defaultColumns 默认展示列表
 */
export function handleFindSysColumn(name: string, defaultColumns: TableColumn[]): VNode {
    let param: SysColumnHeaderVo = {
        name: name,
        defaultColumnList: convertToDefaultColumn(defaultColumns),
    }
    let columns: TableColumn[] = []
    findSysColumnHeader(param).then((res: R<SysColumnHeaderEntity>) => {
            if (res.code === 200 && res.data) {
                let sysColumnDetailList = res.data.sysColumnDetailList;
                let sysColumnHeader: SysColumnHeaderEntity = res.data || {};
                if (sysColumnDetailList && sysColumnDetailList.length > 0) {
                    sysColumnDetailList.forEach((item: SysColumnDetailEntity) => {
                        let sorted: boolean | string = false;
                        if (item.sorted) {
                            if (item.sorted === Sorted.ASC) {
                                sorted = Sorted.ASC;
                            } else if (item.sorted === Sorted.DESC) {
                                sorted = Sorted.DESC;
                            }
                        }
                        columns.push({
                            key: item.name || '',
                            title: item.title || '',
                            hide: item.enabled || true,
                            width: item.width + 'px' || "0px",
                            minWidth: item.minWidth + 'px' || "200px",
                            sort: sorted,
                            ellipsisTooltip: item.ellipsisTooltip || false,
                            fixed: item.fixed ? "left" : (item.fixed || undefined),
                            resize: sysColumnHeader.resizeFlag || false,
                        })
                    })
                }
            } else {
                columns = defaultColumns;
            }
        }
    )
    return h(LayDropdown, {placement: "bottom-end"}, {
        default: () => h(
            "div",
            {
                class: "layui-table-toolbar-item",
                title: '自定义列',
            },
            h(LayIcon, {type: "layui-icon-slider"}),
        ),

        content: () => h(
            "div",
            {class: "layui-table-tool-checkbox"},
            columns.map((column, columnIndex) => h(LayCheckbox, {
                skin: "primary",
                key: column.key || column.type || columnIndex,
                value: columnIndex,
                modelValue: !column.hide,
                // disabled: isValueArray(column.children),

                onChange: () => {
                    column.hide = !column.hide
                    saveUpdate({enabled: column.hide}).then(r => r)
                },
            }, () => column.title)),
        ),
    });
}

function convertToDefaultColumn(defaultColumns: TableColumn[]): DefaultColumnVo[] {
    return defaultColumns?.map(item => {
        let sorted = null;
        if (item.sort) {
            if (item.sort === Sorted.ASC) {
                sorted = Sorted.ASC;
            } else if (item.sort === Sorted.DESC) {
                sorted = Sorted.DESC;
            }
        }
        return {
            key: item.key,
            title: item.title || '',
            hide: item.hide || false,
            width: parseWidth2Number(item.width) || 120,
            minWidth: parseWidth2Number(item.minWidth) || 20,
            sorted: sorted,
            ellipsisTooltip: item.ellipsisTooltip || false,
            fixed: item.fixed || null,
            resizeFlag: item.resize || '',
        }
    })
}

function parseWidth2Number(width: string | undefined): number {
    return Number(width?.replace('px', ''));
}
