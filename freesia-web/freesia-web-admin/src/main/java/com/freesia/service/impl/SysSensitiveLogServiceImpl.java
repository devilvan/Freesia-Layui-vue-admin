package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 业务逻辑类
 * @date 2023-08-13
 */
@Service
@RequiredArgsConstructor
public class SysSensitiveLogServiceImpl extends ServiceImpl<SysSensitiveLogMapper, SysSensitiveLogPo> implements SysSensitiveLogService {
    private final SysSensitiveLogRepository sysSensitiveLogRepository;
    private final SysSensitiveLogMapper sysSensitiveLogMapper;

    @Override
    public SysSensitiveLogPo saveUpdate(SysSensitiveLogDto sysSensitiveLogDto) {
        SysSensitiveLogPo sysSensitiveLogPo = new SysSensitiveLogPo();
        UCopy.fullCopy(sysSensitiveLogDto, sysSensitiveLogPo);
        return sysSensitiveLogRepository.saveAndFlush(sysSensitiveLogPo);
    }

    @Override
    public List<SysSensitiveLogPo> saveUpdateBatch(List<SysSensitiveLogDto> list) {
        List<SysSensitiveLogPo> sysSensitiveLogPoList = UCopy.fullCopyList(list, SysSensitiveLogPo.class);
        return sysSensitiveLogRepository.saveAllAndFlush(sysSensitiveLogPoList);
    }

    @Override
    public TableResult<SysSensitiveLogDto> findPageLoginLog(SysSensitiveLogDto sysSensitiveLogDto, PageQuery pageQuery) {
        Wrapper<SysSensitiveLogPo> queryWrapper = Wrappers.<SysSensitiveLogPo>query()
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
        Page<SysSensitiveLogPo> sysSensitiveLogPoPage = sysSensitiveLogMapper.findPageLoginLog(pageQuery.build(), queryWrapper);
        Page<SysSensitiveLogDto> sysSensitiveLogDtoPage = UCopy.convertPagePo2Dto(sysSensitiveLogPoPage, SysSensitiveLogDto.class);
        return TableResult.build(sysSensitiveLogDtoPage);
    }

    @Override
    public TableResult<SysSensitiveLogDto> findPageOptionLog(SysSensitiveLogDto sysSensitiveLogDto, PageQuery pageQuery) {
        Wrapper<SysSensitiveLogPo> queryWrapper = Wrappers.<SysSensitiveLogPo>query()
                .eq("SL.LOGIC_DEL", FlagConstant.DISABLED)
                .ne("SL.MODULE", UserModule.USER_MANAGEMENT)
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
        Page<SysSensitiveLogPo> sysSensitiveLogPoPage = sysSensitiveLogMapper.findPageOptionLog(pageQuery.build(), queryWrapper);
        Page<SysSensitiveLogDto> sysSensitiveLogDtoPage = UCopy.convertPagePo2Dto(sysSensitiveLogPoPage, SysSensitiveLogDto.class);
        return TableResult.build(sysSensitiveLogDtoPage);
    }
}
