package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.RedissonPropertiesDto;
import com.freesia.entity.FindRedisDashboardInfoEntity;
import com.freesia.exception.DashboardException;
import com.freesia.util.UCollection;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description Redis面板 控制器
 * @date 2023-12-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/dashboard/redisDashboardController")
@Tag(name = "RedisDashboardController", description = "Redis面板 控制器")
public class RedisDashboardController extends BaseController {
    final String COMMAND_STATS = "commandstats";
    final String PING = "ping";
    private final RedisTemplate<String, Object> freesiaRedisTemplate;

    @SaCheckLogin
    @Operation(summary = "获取Redis面板信息")
    @GetMapping("findRedisDashboardInfo")
    public R<FindRedisDashboardInfoEntity> findRedisDashboardInfo() {
        // RedisTemplate获取到的是Redisson的连接
        Optional<RedisConnection> redisConnection = Optional.ofNullable(freesiaRedisTemplate.getConnectionFactory())
                .map(RedisConnectionFactory::getConnection);
        // Redis内置信息
        RedissonPropertiesDto redissonPropertiesDto = redisConnection.map(RedisConnection::info)
                .map(properties -> JSONObject.parseObject(JSONObject.toJSONString(properties), RedissonPropertiesDto.class))
                .orElseThrow(() -> new DashboardException("dashboard.redis.info.find.failed"));
        // Redis指令信息
        List<Map<String, String>> commandStats = redisConnection.map(m -> m.info(COMMAND_STATS))
                .map(commandStat -> {
                    List<Map<String, String>> pieList = new ArrayList<>();
                    commandStat.stringPropertyNames().forEach(key -> {
                        String property = commandStat.getProperty(key);
                        String name = StringUtils.removeStart(key, "cmdstat_");
                        if (!PING.equals(name)) {
                            String value = StringUtils.substringBetween(property, "calls=", ",usec");
                            Map<String, String> data = UCollection.optimizeInitialCapacityMap(2);
                            data.put("name", name);
                            data.put("value", value);
                            pieList.add(data);
                        }
                    });
                    return pieList;
                })
                .orElseThrow(() -> new DashboardException("dashboard.redis.info.find.failed"));
        // Redis DB容量
        Long dbSize = redisConnection.map(RedisServerCommands::dbSize)
                .orElseThrow(() -> new DashboardException("dashboard.redis.info.find.failed"));
        FindRedisDashboardInfoEntity findRedisDashboardInfoEntity = new FindRedisDashboardInfoEntity(redissonPropertiesDto, commandStats, dbSize);
        return R.ok(findRedisDashboardInfoEntity);
    }
}
