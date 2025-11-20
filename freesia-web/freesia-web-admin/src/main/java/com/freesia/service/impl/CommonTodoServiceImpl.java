package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.CommonTodoDto;
import com.freesia.po.CommonTodoPo;
import com.freesia.service.CommonTodoService;
import com.freesia.mapper.CommonTodoMapper;
import com.freesia.repository.CommonTodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 待办事项表 业务逻辑类
 * @date 2025-11-20
 */
@Service
@RequiredArgsConstructor
public class CommonTodoServiceImpl extends ServiceImpl<CommonTodoMapper, CommonTodoPo> implements CommonTodoService {
    private final CommonTodoRepository commonTodoRepository;

    @Override
    public CommonTodoDto saveUpdate(CommonTodoDto commonTodoDto) {
        CommonTodoPo commonTodoPo = new CommonTodoPo();
        UCopy.fullCopy(commonTodoDto, commonTodoPo);
        CommonTodoDto resultDto = new CommonTodoDto();
        UCopy.fullCopy(commonTodoRepository.saveAndFlush(commonTodoPo), resultDto);
        return resultDto;
    }

    @Override
    public List<CommonTodoDto> saveUpdateBatch(List<CommonTodoDto> list) {
        List<CommonTodoPo> commonTodoPoList = UCopy.fullCopyList(list, CommonTodoPo.class);
        return UCopy.fullCopyList(commonTodoRepository.saveAllAndFlush(commonTodoPoList), CommonTodoDto.class);
    }

    @Override
    public TableResult<CommonTodoDto> findPageCommonTodo(CommonTodoDto commonTodoDto, PageQuery pageQuery) {
        LambdaQueryWrapper<CommonTodoPo> wrapper = new LambdaQueryWrapper<CommonTodoPo>()
                .eq(CommonTodoPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(commonTodoDto.getId()), CommonTodoPo::getId, commonTodoDto.getId());
        Page<CommonTodoPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, CommonTodoDto.class));
    }

    @Override
    public CommonTodoDto findCommonTodo(CommonTodoDto commonTodoDto) {
        LambdaQueryWrapper<CommonTodoPo> wrapper = new LambdaQueryWrapper<CommonTodoPo>()
            .eq(CommonTodoPo::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(commonTodoDto.getId()), CommonTodoPo::getId, commonTodoDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), CommonTodoDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommonTodo(List<Long> idList) {
        removeBatchByIds(idList);
    }
}
