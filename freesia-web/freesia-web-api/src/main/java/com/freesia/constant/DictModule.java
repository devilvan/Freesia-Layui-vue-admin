package com.freesia.constant;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 字典模块 静态类
 * @date 2024-01-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DictModule extends SysModule {
    /**
     * 主模块 字典管理模块
     */
    public static final String DICT_MANAGEMENT = "dict_management";

    @Data
    public static class SubModule {
        /**
         * 子模块 保存数据字典键
         */
        public static final String SAVE_DICT_KEY = "save_dict_key";
        /**
         * 子模块 保存数据字典值
         */
        public static final String SAVE_DICT_VALUE = "save_dict_value";
    }
}
