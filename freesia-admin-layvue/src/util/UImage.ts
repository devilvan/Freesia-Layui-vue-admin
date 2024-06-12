import app from "../main";


const $SRC_ASSETS = app.config.globalProperties.$SRC_ASSETS;

/**
 * 解析图片路径（本地/线上）
 * @param imgPath 图片路径
 */
export function parseImgPath(imgPath: string) {
    if (!imgPath || imgPath == '') {
        return '#';
    }
    else if (imgPath.startsWith("http") || imgPath.startsWith("https")) {
        return imgPath;
    } else if (imgPath.startsWith("avatar")) {
        return $SRC_ASSETS + imgPath;
    }
}