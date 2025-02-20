package com.freesia.entity;

import com.freesia.dto.RedissonPropertiesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 获取Redis面板信息 持久层传输类
 * @date 2023-12-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindRedisDashboardInfoEntity {
    @Schema(description = "Redis内置信息")
    private RedissonPropertiesDto redissonPropertiesDto;
    @Schema(description = "Redis指令信息")
    private List<Map<String, String>> commandStats;
    @Schema(description = "Redis DB容量")
    private Long dbSize;

}
