package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 路由信息 值对象
 * @date 2023-08-21
 */
@Data
@Schema(description = "路由信息 值对象")
public class RouterVo {
    @Schema(description = "路由名字")
    private String name;
    @Schema(description = "路由地址")
    private String path;
    @Schema(description = "是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现")
    private boolean hidden;
    @Schema(description = "重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击")
    private String redirect;
    @Schema(description = "组件地址")
    private String component;
    @Schema(description = "路由参数JSON串")
    private String query;
    @Schema(description = "当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面")
    private Boolean alwaysShow;
    @Schema(description = "其他元素")
    private MetaVo meta;
    @Schema(description = "子路由")
    private List<RouterVo> children;
}
