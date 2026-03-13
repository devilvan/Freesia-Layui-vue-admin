package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.vo.SysThirdpartyAuthVo;
import com.freesia.dto.SysThirdpartyAuthDto;
import com.freesia.po.SysThirdpartyAuthPo;
import com.freesia.service.SysThirdpartyAuthService;
import com.freesia.converter.SysThirdpartyAuthConverter;
import com.freesia.mapper.SysThirdpartyAuthMapper;
import com.freesia.repository.SysThirdpartyAuthRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 业务逻辑类
 * @date 2026-03-13
 */
@Service
@RequiredArgsConstructor
public class SysThirdpartyAuthServiceImpl extends BaseServiceImpl<SysThirdpartyAuthMapper, SysThirdpartyAuthVo, SysThirdpartyAuthDto, SysThirdpartyAuthPo> implements SysThirdpartyAuthService {
    private final SysThirdpartyAuthRepository sysThirdpartyAuthRepository;
    private final SysThirdpartyAuthMapper sysThirdpartyAuthMapper;
    private final SysThirdpartyAuthConverter sysThirdpartyAuthConverter;

    @Override
    protected MapStructConverter<SysThirdpartyAuthVo, SysThirdpartyAuthDto, SysThirdpartyAuthPo> getMapStructConverter() {
        return sysThirdpartyAuthConverter;
    }

    @Override
    protected JpaRepository<SysThirdpartyAuthPo, Long> getRepository() {
    return sysThirdpartyAuthRepository;
    }

    @Override
    protected Class<SysThirdpartyAuthDto> getDtoClass() {
        return SysThirdpartyAuthDto.class;
    }

    @Override
    protected Class<SysThirdpartyAuthPo> getPoClass() {
        return SysThirdpartyAuthPo.class;
    }

    @Override
    protected Wrapper<SysThirdpartyAuthPo> buildQueryWrapper(@NonNull SysThirdpartyAuthDto sysThirdpartyAuthDto) {
        return new LambdaQueryWrapper<SysThirdpartyAuthPo>()
                .eq(SysThirdpartyAuthPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getId()), SysThirdpartyAuthPo::getId, sysThirdpartyAuthDto.getId())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getAuthId()), SysThirdpartyAuthPo::getAuthId, sysThirdpartyAuthDto.getAuthId())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getSource()), SysThirdpartyAuthPo::getSource, sysThirdpartyAuthDto.getSource())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getOpenId()), SysThirdpartyAuthPo::getOpenId, sysThirdpartyAuthDto.getOpenId())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getUserName()), SysThirdpartyAuthPo::getUserName, sysThirdpartyAuthDto.getUserName())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getNickName()), SysThirdpartyAuthPo::getNickName, sysThirdpartyAuthDto.getNickName())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getEmail()), SysThirdpartyAuthPo::getEmail, sysThirdpartyAuthDto.getEmail())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getAvatar()), SysThirdpartyAuthPo::getAvatar, sysThirdpartyAuthDto.getAvatar())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getAccessToken()), SysThirdpartyAuthPo::getAccessToken, sysThirdpartyAuthDto.getAccessToken())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getExpireTimeout()), SysThirdpartyAuthPo::getExpireTimeout, sysThirdpartyAuthDto.getExpireTimeout())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getRefreshToken()), SysThirdpartyAuthPo::getRefreshToken, sysThirdpartyAuthDto.getRefreshToken())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getAccessCode()), SysThirdpartyAuthPo::getAccessCode, sysThirdpartyAuthDto.getAccessCode())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getUnionId()), SysThirdpartyAuthPo::getUnionId, sysThirdpartyAuthDto.getUnionId())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getScope()), SysThirdpartyAuthPo::getScope, sysThirdpartyAuthDto.getScope())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getTokenType()), SysThirdpartyAuthPo::getTokenType, sysThirdpartyAuthDto.getTokenType())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getIdToken()), SysThirdpartyAuthPo::getIdToken, sysThirdpartyAuthDto.getIdToken())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getMacAlgorithm()), SysThirdpartyAuthPo::getMacAlgorithm, sysThirdpartyAuthDto.getMacAlgorithm())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getMacKey()), SysThirdpartyAuthPo::getMacKey, sysThirdpartyAuthDto.getMacKey())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getCode()), SysThirdpartyAuthPo::getCode, sysThirdpartyAuthDto.getCode())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getOauthToken()), SysThirdpartyAuthPo::getOauthToken, sysThirdpartyAuthDto.getOauthToken())
                .eq(UEmpty.isNotEmpty(sysThirdpartyAuthDto.getOauthTokenSecret()), SysThirdpartyAuthPo::getOauthTokenSecret, sysThirdpartyAuthDto.getOauthTokenSecret())
                ;
    }

    @Override
    public TableResult<SysThirdpartyAuthDto> findPage(SysThirdpartyAuthDto dto, PageQuery pageQuery) {
        Page<SysThirdpartyAuthPo> page = sysThirdpartyAuthMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sysThirdpartyAuthConverter.convertPagePo2Dto(page));
    }

    @Override
    public List<SysThirdpartyAuthDto> findList(SysThirdpartyAuthDto dto) {
        return sysThirdpartyAuthMapper.findList(dto);
    }

    @Override
    public SysThirdpartyAuthDto findOne(SysThirdpartyAuthDto dto) {
        return sysThirdpartyAuthConverter.convertPo2Dto(sysThirdpartyAuthMapper.findOne(dto));
    }
}
