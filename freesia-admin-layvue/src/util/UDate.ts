export const YMS = 'yyyy-MM-dd';
export const YMS_HMS = 'yyyy-MM-dd HH:mm:ss';

/**
 * 日期格式化为 yyyy-MM-dd HH:mm:ss
 * @param date
 */
export function formatDateTime(date: Date, format: string) {
    const o = {
        'M+': date.getMonth() + 1, // 月份
        'd+': date.getDate(), // 日
        'h+': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 小时
        'H+': date.getHours(), // 小时
        'm+': date.getMinutes(), // 分
        's+': date.getSeconds(), // 秒
        'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
        S: date.getMilliseconds(), // 毫秒
        a: date.getHours() < 12 ? '上午' : '下午', // 上午/下午
        A: date.getHours() < 12 ? 'AM' : 'PM', // AM/PM
    };
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (let k in o) {
        if (new RegExp('(' + k + ')').test(format)) {
            format = format.replace(
                RegExp.$1,
                RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
            );
        }
    }
    return format;
}

/**
 * 构造时间范围
 * @param days 间隔日期
 */
export function buildRange(days: number) {
    const end = new Date()
    end.setHours(23, 59, 59)
    end.setDate(end.getDate())
    const start = new Date()
    // start.setTime(start.getTime() - 3600 * 1000 * 24 * days)
    start.setHours(0, 0, 0)
    start.setDate(start.getDate() - days)
    return [formatDateTime(start, YMS_HMS), formatDateTime(end, YMS_HMS)]
}

/**
 * 构造datePicker组件的快捷生成时间范围对象
 * @param text 描述
 * @param days 日期间隔
 */
export function within(text: string, days: number) {
    return {
        text: text,
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * days)
            return [start, end]
        },
    }
}

/**
 * 构造datePicker组件的跳转到日期对象
 * @param text 描述
 * @param days 日期间隔
 */

export function toDay(text: string, days: number) {
    let now = new Date()
    now.setHours(0, 0, 0)
    now.setDate(now.getDate() + days)
    return {
        text: text,
        value: now
    }
}

/**
 * 构造默认datePicker快捷时间范围 对象
 */
export const defaultShortcuts = [
    within("近三天", 3),
    within("近一周", 7),
    within("近一个月", 30),
    within("近两个月", 60),
    within("近三个月", 90)
]

/**
 * 构造默认datePicker单一时间 对象
 */
export const singleShortcuts = [
    toDay("昨天", -1),
    toDay("明天", +1),
]