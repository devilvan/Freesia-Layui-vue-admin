import app from "../main";
import {layer} from "@layui/layui-vue";


const $SRC_ASSETS = app.config.globalProperties.$SRC_ASSETS;

/**
 * 解析图片路径（本地/线上）
 * @param imgPath 图片路径
 */
export function parseImgPath(imgPath: string) {
    if (!imgPath || imgPath == '') {
        return;
    } else if (imgPath.startsWith("http") || imgPath.startsWith("https")) {
        return imgPath;
    } else if (imgPath.startsWith("avatar")) {
        return $SRC_ASSETS + imgPath;
    }
}

/**
 * 预览图片
 */
export function preview(path: any) {
    let option = {
        imgList: [{src: path, alt: 'Do you like what you see?'}]
    };
    layer.photos(option)
}