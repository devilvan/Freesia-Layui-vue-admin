package com.freesia.dto;

import com.freesia.util.UString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 路由器显示信息 数据传输对象
 * @date 2023-08-21
 */
@Data
@NoArgsConstructor
public class MetaDto {
    @Schema(description = "设置该路由在侧边栏和面包屑中展示的名字")
    private String title;
    @Schema(description = "设置该路由的图标")
    private String icon;
    @Schema(description = "设置为true，则不会被 keep-alive缓存")
    private boolean cache;
    @Schema(description = "内链地址")
    private String link;
    @Schema(description = "固定项")
    private boolean affix = false;
    @Schema(description = "是否可关闭")
    private boolean closable = true;

    public MetaDto(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public MetaDto(String title, String icon, boolean cache) {
        this.title = title;
        this.icon = icon;
        this.cache = cache;
    }

    public MetaDto(String title, String icon, String link) {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }

    public MetaDto(String title, String icon, boolean cache, String link) {
        this.title = title;
        this.icon = icon;
        this.cache = cache;
        if (UString.isHttp(link)) {
            this.link = link;
        }
    }
}
