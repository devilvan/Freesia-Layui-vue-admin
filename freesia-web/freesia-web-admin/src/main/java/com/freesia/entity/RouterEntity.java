package com.freesia.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 路由信息 持久层传输对象
 * @date 2023-08-21
 */
@Data
@Schema(description = "路由信息 持久层传输对象")
public class RouterEntity {
    @Schema(description = "路由地址")
    private String id;
    @Schema(description = "设置该路由在侧边栏和面包屑中展示的名字")
    private String title;
    @Schema(description = "设置该路由的图标，对应路径src/assets/icons/svg")
    private String icon;
    @Schema(description = "组件地址")
    private String component;
    @Schema(description = "子路由")
    private List<RouterEntity> children;
}
