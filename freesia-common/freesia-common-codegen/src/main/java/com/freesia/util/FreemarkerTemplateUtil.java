package com.freesia.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * @author Evad.Wu
 * @Description Freemarker生成模板 工具类
 * @date 2022-09-12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreemarkerTemplateUtil {
    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_31);

    static {
        //这里比较重要，用来指定加载模板所在的路径
        CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreemarkerTemplateUtil.class,
                "/static/ftl"));
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    public static Template getTemplate(String templateName) throws IOException {
        return CONFIGURATION.getTemplate(templateName);
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }
}
