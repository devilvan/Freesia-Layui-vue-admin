package com.freesia.service;

import com.freesia.dto.SysDictDto;
import com.freesia.dto.SysDictKeyDto;
import com.freesia.entity.FindPageSysDictKeyEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 业务逻辑接口
 * @date 2023-09-08
 */
public interface SysDictKeyService {
    /**
     * 保存
     *
     * @param sysDictKeyDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysDictKeyDto saveUpdate(SysDictKeyDto sysDictKeyDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysDictKeyDto> saveUpdateBatch(List<SysDictKeyDto> list);

    /**
     * 查询字典数据的分页信息
     *
     * @param sysDictDto 查询参数
     * @param pageQuery  分页参数
     * @return 分页对象
     */
    TableResult<FindPageSysDictKeyEntity> findPageSysDictList(SysDictDto sysDictDto, PageQuery pageQuery);

    /**
     * 查询字典键列表
     *
     * @param sysDictKeyDto 查询参数
     * @return 字典键列表
     */
    List<SysDictKeyDto> findList(SysDictKeyDto sysDictKeyDto);

    /**
     * 保存字典键
     *
     * @param sysDictKeyDto 字典键数据
     * @return 保存后的字典键
     */
    SysDictKeyDto saveSysDictKey(SysDictKeyDto sysDictKeyDto);
}
