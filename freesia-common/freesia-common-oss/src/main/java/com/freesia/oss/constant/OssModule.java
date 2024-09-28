package com.freesia.oss.constant;

import com.freesia.constant.SysModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description OSS对象存储 模块类
 * @date 2024-02-27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OssModule extends SysModule {
    /**
     * 主模块 字典管理模块
     */
    public static final String OSS_MANAGEMENT = "oss_management";


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SubModule {
        /**
         * 子模块 上传文件
         */
        public static final String OSS_UPLOAD = "oss_upload";
        /**
         * 子模块 下载文件
         */
        public static final String OSS_DOWNLOAD = "oss_download";
        /**
         * 子模块 保存OSS配置信息
         */
        public static final String SAVE_OSS_CONFIG = "save_oss_config";
        /**
         * 子模块 删除OSS配置信息
         */
        public static final String DELETE_OSS_CONFIG = "delete_oss_config";
        /**
         * 子模块 保存OSS信息
         */
        public static final String SAVE_OSS = "save_oss";
        /**
         * 子模块 删除OSS信息
         */
        public static final String DELETE_OSS = "delete_oss";
    }
}
