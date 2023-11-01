package com.freesia.vo;

import com.freesia.util.UString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 路由器显示信息 值对象
 * @date 2023-08-21
 */
@Data
@NoArgsConstructor
public class MetaVo {
    @Schema(description = "设置该路由在侧边栏和面包屑中展示的名字")
    private String title;
    @Schema(description = "设置该路由的图标，对应路径src/assets/icons/svg")
    private String icon;
    @Schema(description = "设置为true，则不会被 <keep-alive>缓存")
    private boolean noCache;
    @Schema(description = "内链地址（http(s)://开头）")
    private String link;

    public MetaVo(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public MetaVo(String title, String icon, boolean noCache) {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
    }

    public MetaVo(String title, String icon, String link) {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }

    public MetaVo(String title, String icon, boolean noCache, String link) {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
        if (UString.isHttp(link)) {
            this.link = link;
        }
    }

}

