package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.convert.ConverterFactory;
import com.freesia.dto.BaseDto;
import com.freesia.po.BasePo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description （模板模式）ServiceImpl的通用父类
 * @date 2026-01-04
 */
@Component
@SuppressWarnings("unused")
public abstract class BaseServiceImpl<MAPPER extends BaseMapper<PO>, PO extends BasePo, DTO extends BaseDto> extends ServiceImpl<MAPPER, PO> {
    @Resource
    private ConverterFactory converterFactory;

    /**
     * 单个保存（模板方法）
     *
     * @param dto DTO
     * @return 保存后的DTO
     */
    public DTO saveUpdate(DTO dto) {
        PO po = converterFactory.getConverter(getDtoClass(), getPoClass()).convert(dto);
        // 钩子方法，子类可以重写以添加额外逻辑
        beforeSave(po, dto);
        PO saved = getRepository().saveAndFlush(po);
        afterSave(saved, dto);
        return converterFactory.getConverter(getPoClass(), getDtoClass()).convert(saved);
    }

    /**
     * 批量保存（模板方法）
     *
     * @param list 待保存DTO集合
     * @return 保存后的DTO集合
     */
    public List<DTO> saveUpdateBatch(List<DTO> list) {
        List<PO> commonTodoPoList = converterFactory.getConverter(getDtoClass(), getPoClass()).convertBatch(list);
        return converterFactory.getConverter(getPoClass(), getDtoClass()).convertBatch(getRepository().saveAllAndFlush(commonTodoPoList));
    }


    /**
     * 批量删除（模板方法）
     *
     * @param idList 主键集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> idList) {
        removeBatchByIds(idList);
    }

    /**
     * 分页查询（模板方法）
     *
     * @param dto       DTO
     * @param pageQuery 分页参数
     * @return 分页格式响应
     */
    public TableResult<DTO> findPage(DTO dto, PageQuery pageQuery) {
        return findPage(dto, pageQuery, buildQueryWrapper(dto));
    }

    /**
     * 分页查询（模板方法）
     *
     * @param dto       DTO
     * @param pageQuery 分页参数
     * @param wrapper   查询条件
     * @return 分页格式响应
     */
    public TableResult<DTO> findPage(DTO dto, PageQuery pageQuery, Wrapper<PO> wrapper) {
        if (wrapper != null) {
            Page<PO> page = page(pageQuery.build(), wrapper);
            return TableResult.build(converterFactory.getConverter(getPoClass(), getDtoClass()).convertPage(page));
        }
        return TableResult.build();
    }

    /**
     * 单个查询（模板方法）
     *
     * @param dto DTO
     * @return 查询结果
     */
    public DTO findOne(DTO dto) {
        return findOne(dto, buildQueryWrapper(dto));
    }

    /**
     * 单个查询（模板方法）
     *
     * @param dto     DTO
     * @param wrapper 查询条件
     * @return 查询结果
     */
    public DTO findOne(DTO dto, Wrapper<PO> wrapper) {
        if (wrapper != null) {
            return converterFactory.getConverter(getPoClass(), getDtoClass()).convert(getOne(wrapper));
        }
        return null;
    }

    /**
     * 列表查询（模板方法）
     *
     * @param dto DTO
     * @return 查询结果
     */
    public List<DTO> findList(DTO dto) {
        return findList(dto, buildQueryWrapper(dto));
    }

    /**
     * 列表查询（模板方法）
     *
     * @param dto     DTO
     * @param wrapper 查询条件
     * @return 查询结果
     */
    public List<DTO> findList(DTO dto, Wrapper<PO> wrapper) {
        if (wrapper != null) {
            return converterFactory.getConverter(getPoClass(), getDtoClass()).convertBatch(list(wrapper));
        }
        return null;
    }

    /**
     * 获取持久层实例
     *
     * @return 持久层实例
     */
    protected abstract JpaRepository<PO, Long> getRepository();

    /**
     * 获取DTO的Class
     *
     * @return DTO的Class
     */
    protected abstract Class<DTO> getDtoClass();

    /**
     * 获取PO的Class
     *
     * @return PO的Class
     */
    protected abstract Class<PO> getPoClass();

    protected abstract Wrapper<PO> buildQueryWrapper(@NonNull DTO dto);

    /**
     * 保存前置钩子函数，子类可选择重写
     *
     * @param po  PO
     * @param dto DTO
     */
    protected void beforeSave(PO po, DTO dto) {
    }

    /**
     * 保存后置钩子函数，子类可选择重写
     *
     * @param po  PO
     * @param dto DTO
     */
    protected void afterSave(PO po, DTO dto) {
    }

    /**
     * DTO->PO
     *
     * @param dto DTO
     * @return PO
     */
    protected PO convertDto2Po(DTO dto) {
        return converterFactory.getConverter(getDtoClass(), getPoClass()).convert(dto);
    }

    /**
     * PO->DTO
     *
     * @param po PO
     * @return DTO
     */
    protected DTO convertPo2Dto(PO po) {
        return converterFactory.getConverter(getPoClass(), getDtoClass()).convert(po);
    }
}
