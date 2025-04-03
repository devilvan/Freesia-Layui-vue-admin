import {useAppStore} from "../store/app";

const appStore = useAppStore();

/**
 * 根据文件的字节大小转换为可读的文本
 * @param fileSize 文件大小
 * @param decimals 精度
 * @return 可读的文本
 */
export function formatFileSize(fileSize: number, decimals: number = 2): string {
    if (!fileSize) return '0 Bytes'

    fileSize = Number(fileSize)
    if (isNaN(fileSize)) return '0 Bytes'

    const k = 1024
    const dm = decimals < 0 ? 0 : decimals
    const units = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']

    let i = 0
    while (fileSize >= k && i < units.length - 1) {
        fileSize /= k
        i++
    }

    return `${parseFloat(fileSize.toFixed(dm))} ${units[i]}`
}
