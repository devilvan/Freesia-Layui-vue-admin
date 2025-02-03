package com.freesia.controller;

import com.freesia.dto.SysSensitiveLogDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysSensitiveLogService;
import com.freesia.util.UCopy;
import com.freesia.vo.SysSensitiveLogVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 控制器
 * @date 2023-09-12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysSensitiveLogController")
@Tag(name = "SysSensitiveLogController", description = "敏感操作信息表 控制器")
public class SysSensitiveLogController extends BaseController {
    private final SysSensitiveLogService sysSensitiveLogService;

    /**
     * 查询登录日志分页数据
     *
     * @param sysSensitiveLogVo 查询参数
     * @param pageQuery         分页参数
     * @return 分页返回
     */
    @Operation(summary = "查询登录日志分页数据")
    @GetMapping(value = "findPageLoginLog")
    public TableResult<SysSensitiveLogDto> findPageLoginLog(SysSensitiveLogVo sysSensitiveLogVo, PageQuery pageQuery) {
        SysSensitiveLogDto sysSensitiveLogDto = new SysSensitiveLogDto();
        UCopy.fullCopy(sysSensitiveLogVo, sysSensitiveLogDto);
        return sysSensitiveLogService.findPageLoginLog(sysSensitiveLogDto, pageQuery);
    }

    /**
     * 查询操作日志分页数据
     *
     * @param sysSensitiveLogVo 查询参数
     * @param pageQuery         分页参数
     * @return 分页返回
     */
    @Operation(summary = "查询操作日志分页数据")
    @GetMapping(value = "findPageOptionLog")
    public TableResult<SysSensitiveLogDto> findPageOptionLog(SysSensitiveLogVo sysSensitiveLogVo, PageQuery pageQuery) {
        SysSensitiveLogDto sysSensitiveLogDto = new SysSensitiveLogDto();
        UCopy.fullCopy(sysSensitiveLogVo, sysSensitiveLogDto);
        return sysSensitiveLogService.findPageOptionLog(sysSensitiveLogDto, pageQuery);
    }
}
