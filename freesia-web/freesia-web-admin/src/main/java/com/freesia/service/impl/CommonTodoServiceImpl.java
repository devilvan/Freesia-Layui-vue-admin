package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.CommonTodoConverter;
import com.freesia.dto.CommonTodoDto;
import com.freesia.mapper.CommonTodoMapper;
import com.freesia.po.CommonTodoPo;
import com.freesia.repository.CommonTodoRepository;
import com.freesia.service.CommonTodoService;
import com.freesia.vo.CommonTodoVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author Evad.Wu
 * @Description 待办事项表 业务逻辑类
 * @date 2026-01-04
 */
@Service
@RequiredArgsConstructor
public class CommonTodoServiceImpl extends BaseServiceImpl<CommonTodoMapper, CommonTodoVo, CommonTodoDto, CommonTodoPo> implements CommonTodoService {
    private final CommonTodoRepository commonTodoRepository;
    private final CommonTodoConverter commonTodoConverter;

    @Override
    protected MapStructConverter<CommonTodoVo, CommonTodoDto, CommonTodoPo> getMapStructConverter() {
        return commonTodoConverter;
    }

    @Override
    protected JpaRepository<CommonTodoPo, Long> getRepository() {
        return commonTodoRepository;
    }

    @Override
    protected Class<CommonTodoDto> getDtoClass() {
        return CommonTodoDto.class;
    }

    @Override
    protected Class<CommonTodoPo> getPoClass() {
        return CommonTodoPo.class;
    }

    @Override
    protected LambdaQueryWrapper<CommonTodoPo> buildQueryWrapper(@NonNull CommonTodoDto dto) {
        return new LambdaQueryWrapper<CommonTodoPo>()
                .eq(CommonTodoPo::getLogicDel, FlagConstant.DISABLED);
    }
}
