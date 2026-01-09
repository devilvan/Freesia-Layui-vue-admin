package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.constant.FlagConstant;
import com.freesia.dto.CommonTodoDto;
import com.freesia.mapper.CommonTodoMapper;
import com.freesia.po.CommonTodoPo;
import com.freesia.repository.CommonTodoRepository;
import com.freesia.service.CommonTodoService;
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
public class CommonTodoServiceImpl extends BaseServiceImpl<CommonTodoMapper, CommonTodoPo, CommonTodoDto> implements CommonTodoService {
    private final CommonTodoRepository commonTodoRepository;

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
    protected LambdaQueryWrapper<CommonTodoPo> buildLambdaQueryWrapper(@NonNull CommonTodoDto dto) {
        return new LambdaQueryWrapper<CommonTodoPo>()
                .eq(CommonTodoPo::getLogicDel, FlagConstant.DISABLED);
    }
}
