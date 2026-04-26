// 系统自定义列工具类
import {h, ref, Ref, VNode} from "vue";
import {LayCheckbox, LayDropdown} from "@layui/layui-vue";
import {LayIcon} from "@layui/icons-vue";
import {findSysColumnHeader} from "@/api/system/ColumnHeader";
import {TableColumn, TableDefaultToolbar} from "@layui/layui-vue/types/component/table/typing";
import {DefaultColumnVo, SysColumnHeaderEntity, SysColumnHeaderVo} from "@/types/system/ColumnHeader";
import {R} from "@/types/Result";
import {SysColumnDetailEntity} from "@/types/system/ColumnDetail";
import {Sorted} from "@/types/Constants";
import {saveUpdate, toggleEnabled} from "@/api/system/ColumnDetail";
import {useDraggable} from "vue-draggable-plus";
import DraggableLayCheckbox from "@/layouts/global/draggableLayCheckbox/DraggableLayCheckbox.vue";

export function buildTableDefaultToolbar(columns: Ref<TableColumn[]>): TableDefaultToolbar[] {
    return [
        {
            title: '自定义列',
            icon: 'layui-icon-slider',
            render: () => handleFindSysColumn(columns)
        }, "export", "print"
    ]
}

export function buildItem(item: SysColumnDetailEntity, sysColumnHeader: SysColumnHeaderEntity): TableColumn {
    let sorted: boolean | string = false;
    if (item.sorted) {
        if (item.sorted === Sorted.ASC) {
            sorted = Sorted.ASC;
        } else if (item.sorted === Sorted.DESC) {
            sorted = Sorted.DESC;
        }
    }
    return {
        id: item.id || '',
        key: item.name || '',
        hide: !item.enabled || false,
        title: item.title || '',
        width: item.width + 'px' || "0px",
        minWidth: item.minWidth + 'px' || "200px",
        sort: sorted,
        ellipsisTooltip: item.ellipsisTooltip || false,
        fixed: item.fixed ? "left" : (item.fixed || undefined),
        resize: sysColumnHeader.resizeFlag || false,
        customSlot: item.customSlot || '',
    };
}

/**
 * 构建系统自定义列
 * @param columns 默认展示列表
 */
function handleFindSysColumn(columns: Ref<TableColumn[]>) {
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
            columns.value.map((column, columnIndex) => {
                let find = columns.value.find((item) => item.key === column.key);
                return h(DraggableLayCheckbox, {
                    skin: "primary",
                    key: column.key || column.type || columnIndex,
                    value: columnIndex,
                    modelValue: !find?.hide,
                    onChange: () => {
                        column.hide = !column.hide
                        if (find && find.id) {
                            toggleEnabled(find.id).then((res: R<void>) => res)
                        }
                    },
                }, () => column.title)
            })
        )
    });
}

export function convertToDefaultColumn(defaultColumns: TableColumn[]): DefaultColumnVo[] {
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
            customSlot: item.customSlot || '',
        }
    })
}

function parseWidth2Number(width: string | undefined): number {
    return Number(width?.replace('px', ''));
}
