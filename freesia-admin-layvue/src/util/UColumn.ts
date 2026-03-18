// 系统自定义列工具类
import {h, VNode} from "vue";
import {LayCheckbox, LayDropdown} from "@layui/layui-vue";
import {LayIcon} from "@layui/icons-vue";
import {findSysColumnHeader} from "@/api/system/ColumnHeader";
import {TableColumn} from "@layui/layui-vue/types/component/table/typing";
import {SysColumnHeaderEntity, SysColumnHeaderVo} from "@/types/system/ColumnHeader";
import {R} from "@/types/Result";
import {SysColumnDetailEntity} from "@/types/system/ColumnDetail";
import {Sorted} from "@/types/Constants";
import {saveUpdate} from "@/api/system/ColumnDetail";

/**
 * 构建系统自定义列
 * @param component 组件名
 */
export function handleFindSysColumn(component: string, defaultColumns: TableColumn[]): VNode {
    let param: SysColumnHeaderVo = {
        component: component
    }
    let columns: TableColumn[] = []
    findSysColumnHeader(param).then((res: R<SysColumnHeaderEntity>) => {
            if (res.code === 200 && res.data) {
                let sysColumnDetailList = res.data.sysColumnDetailList;
                let sysColumnHeader: SysColumnHeaderEntity = res.data || {};
                if (sysColumnDetailList && sysColumnDetailList.length > 0) {
                    sysColumnDetailList.forEach((item: SysColumnDetailEntity) => {
                        let sorted = "";
                        if (item.sorted) {
                            if (item.sorted === "A") {
                                sorted = Sorted.A;
                            } else if (item.sorted === "D") {
                                sorted = Sorted.D;
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