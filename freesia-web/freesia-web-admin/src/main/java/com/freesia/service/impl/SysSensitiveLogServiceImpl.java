package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.UserModule;
import com.freesia.dto.SysSensitiveLogDto;
import com.freesia.mapper.SysSensitiveLogMapper;
import com.freesia.po.SysSensitiveLogPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SysSensitiveLogRepository;
import com.freesia.service.SysSensitiveLogService;
import com.freesia.util.UCalendar;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 业务逻辑类
 * @date 2023-08-13
 */
@Service
@RequiredArgsConstructor
public class SysSensitiveLogServiceImpl extends BaseServiceImpl<SysSensitiveLogMapper, SysSensitiveLogPo, SysSensitiveLogDto> implements SysSensitiveLogService {
    private final SysSensitiveLogRepository sysSensitiveLogRepository;
    private final SysSensitiveLogMapper sysSensitiveLogMapper;


    @Override
    protected JpaRepository<SysSensitiveLogPo, Long> getRepository() {
        return sysSensitiveLogRepository;
    }

    @Override
    protected Class<SysSensitiveLogDto> getDtoClass() {
        return SysSensitiveLogDto.class;
    }

    @Override
    protected Class<SysSensitiveLogPo> getPoClass() {
        return SysSensitiveLogPo.class;
    }

    @Override
    protected Wrapper<SysSensitiveLogPo> buildQueryWrapper(@NonNull SysSensitiveLogDto sysSensitiveLogDto) {
        return Wrappers.<SysSensitiveLogPo>query()
                .eq("SL.LOGIC_DEL", FlagConstant.DISABLED)
                .eq("SL.MODULE", UserModule.USER_MANAGEMENT)
                .eq(UEmpty.isNotEmpty(sysSensitiveLogDto.getOperatorName()), "SL.OPERATOR_NAME", sysSensitiveLogDto.getOperatorName())
                .eq(UEmpty.isNotEmpty(sysSensitiveLogDto.getDeptName()), "SL.DEPT_NAME", sysSensitiveLogDto.getDeptName())
                .like(UEmpty.isNotEmpty(sysSensitiveLogDto.getUrl()), "SL.URL", sysSensitiveLogDto.getUrl())
                .like(UEmpty.isNotEmpty(sysSensitiveLogDto.getModule()), "SL.MODULE", sysSensitiveLogDto.getModule())
                .like(UEmpty.isNotEmpty(sysSensitiveLogDto.getSubModule()), "SL.SUB_MODULE", sysSensitiveLogDto.getSubModule())
                .like(UEmpty.isNotEmpty(sysSensitiveLogDto.getType()), "SL.TYPE", sysSensitiveLogDto.getType())
                .like(UEmpty.isNotEmpty(sysSensitiveLogDto.getResult()), "SL.RESULT", sysSensitiveLogDto.getResult())
                .eq(UEmpty.isNotEmpty(sysSensitiveLogDto.getSign()), "SL.SIGN", sysSensitiveLogDto.getSign())
                .between(UCalendar.checkValidDate(sysSensitiveLogDto.getOperateTimeFrom(), sysSensitiveLogDto.getOperateTimeTo()),
                        "SL.OPERATE_TIME",
                        sysSensitiveLogDto.getOperateTimeFrom(),
                        sysSensitiveLogDto.getOperateTimeTo())
                .orderByDesc("SL.OPERATE_TIME");
    }

    @Override
    public TableResult<SysSensitiveLogDto> findPageLoginLog(SysSensitiveLogDto sysSensitiveLogDto, PageQuery pageQuery) {
        Wrapper<SysSensitiveLogPo> queryWrapper = buildQueryWrapper(sysSensitiveLogDto);
        Page<SysSensitiveLogPo> sysSensitiveLogPoPage = sysSensitiveLogMapper.findPageLoginLog(pageQuery.build(), queryWrapper);
        Page<SysSensitiveLogDto> sysSensitiveLogDtoPage = UCopy.convertPage(sysSensitiveLogPoPage, SysSensitiveLogDto.class);
        return TableResult.build(sysSensitiveLogDtoPage);
    }

    @Override
    public TableResult<SysSensitiveLogDto> findPageOptionLog(SysSensitiveLogDto sysSensitiveLogDto, PageQuery pageQuery) {
        Wrapper<SysSensitiveLogPo> queryWrapper = buildQueryWrapper(sysSensitiveLogDto);
        Page<SysSensitiveLogPo> sysSensitiveLogPoPage = sysSensitiveLogMapper.findPageOptionLog(pageQuery.build(), queryWrapper);
        Page<SysSensitiveLogDto> sysSensitiveLogDtoPage = UCopy.convertPage(sysSensitiveLogPoPage, SysSensitiveLogDto.class);
        return TableResult.build(sysSensitiveLogDtoPage);
    }
}
