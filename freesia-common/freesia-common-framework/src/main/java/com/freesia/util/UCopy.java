package com.freesia.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.dto.BaseDto;
import com.freesia.po.BasePo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Evad.Wu
 * @Description 值复制工具类
 * @date 2022-07-13
 */
@SuppressWarnings(value = {"unused", "DuplicatedCode"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCopy {
    /**
     * PO转DTO
     *
     * @param po       po对象
     * @param dtoClz   dto的Class
     * @param excludes 排除字段
     * @param <DTO>    dto类型
     * @param <PO>     po类型
     * @return po转dto后的对象
     */
    public static <DTO extends BaseDto, PO extends BasePo> DTO copyPo2Dto(PO po, Class<DTO> dtoClz, String... excludes) {
        DTO dto = null;
        try {
            dto = dtoClz.getConstructor().newInstance();
            fullCopy(po, dto, excludes);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return dto;
    }

    /**
     * DTO转PO
     *
     * @param dto      dto对象
     * @param poClz    po的Class
     * @param excludes 排除的字段
     * @param <DTO>    DTO类型
     * @param <PO>     PO类型
     * @return DTO赋值PO后的对象
     */
    public static <DTO extends BaseDto, PO extends BasePo> PO copyDto2Po(DTO dto, Class<PO> poClz, String... excludes) {
        PO po = null;
        try {
            po = poClz.getConstructor().newInstance();
            fullCopy(dto, po, excludes);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return po;
    }

    /**
     * VO转DTO
     *
     * @param vo       vo对象
     * @param dtoClz   dto的Class
     * @param excludes 排除字段
     * @param <VO>     VO类型
     * @param <DTO>    DTO类型
     * @return VO赋值DTO后的对象
     */
    public static <VO, DTO extends BaseDto> DTO copyVo2Dto(VO vo, Class<DTO> dtoClz, String... excludes) {
        DTO dto = null;
        try {
            dto = dtoClz.getConstructor().newInstance();
            fullCopy(vo, dto, excludes);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return dto;
    }

    /**
     * Page BO转DTO
     *
     * @param page     BO分页对象
     * @param dtoClass DTO的class
     * @param <ENTITY> ENTITY类型
     * @param <DTO>    DTO类型
     * @return DTO分页对象
     */
    public static <ENTITY, DTO extends BaseDto> Page<DTO> convertPageEntity2Dto(Page<ENTITY> page, Class<DTO> dtoClass) {
        Page<DTO> pageDto = new Page<>();
        List<DTO> sysUserDtoList = UCopy.fullCopyList(page.getRecords(), dtoClass);
        pageDto.setRecords(sysUserDtoList);
        pageDto.setSize(page.getSize());
        pageDto.setCurrent(page.getCurrent());
        pageDto.setTotal(page.getTotal());
        pageDto.setPages(page.getPages());
        return pageDto;
    }

    /**
     * Page PO转DTO
     *
     * @param page     PO分页对象
     * @param dtoClass DTO的class
     * @param <PO>     PO类型
     * @param <DTO>    DTO类型
     * @return DTO分页对象
     */
    public static <PO extends BasePo, DTO extends BaseDto> Page<DTO> convertPagePo2Dto(Page<PO> page, Class<DTO> dtoClass) {
        Page<DTO> pageDto = new Page<>();
        List<DTO> sysUserDtoList = UCopy.fullCopyList(page.getRecords(), dtoClass);
        pageDto.setRecords(sysUserDtoList);
        pageDto.setSize(page.getSize());
        pageDto.setCurrent(page.getCurrent());
        pageDto.setTotal(page.getTotal());
        pageDto.setPages(page.getPages());
        return pageDto;
    }

    /**
     * 自定义函数去重（采用 Predicate函数式判断，采用 Function获取比较key）
     * 内部维护一个 ConcurrentHashMap，并采用 putIfAbsent特性实现
     *
     * @param keyExtractor 判断去重的字段
     * @param <T>          去重字段的类型
     * @return 去重的集合
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>(16);
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 根据唯一字段校验两个集合需要新增的部分
     *
     * @param source       源集合
     * @param isExistList  已存在的集合
     * @param isExistParam 两集合共同校验的字段
     * @param <T>          函数入参类型
     * @param <E>          函数出参类型
     * @return 返回需要新增的集合
     */
    public static <T extends BasePo, E> List<T> checkAssemble(
            List<T> source, List<T> isExistList, Function<T, E> isExistParam) {
        return checkAssemble(source, isExistList, isExistParam, 0);
    }

    /**
     * 根据唯一字段校验两个集合需要新增的部分
     *
     * @param source       源集合
     * @param isExistList  已存在的集合
     * @param isExistParam 两集合共同校验的字段
     * @param passDay      日期范围
     * @param <T>          函数入参类型
     * @param <E>          函数出参类型
     * @return 返回需要新增的集合
     */
    public static <T extends BasePo, E> List<T> checkAssemble(
            List<T> source, List<T> isExistList, Function<T, E> isExistParam, int passDay) {
        List<T> result = new ArrayList<>();
        List<E> paramList = isExistList.stream().map(isExistParam).toList();
        // 宾客为上
        for (T s : source) {
            E apply = isExistParam.apply(s);
            int indexOf = paramList.indexOf(apply);
            if (-1 == indexOf) {
                // 不存在就新增
                result.add(s);
            } else {
                T t = isExistList.get(indexOf);
                Date modifyTime = t.getModifyTime();
                // 存在但超过日期范围，迭代冗余
                if (passDay != 0 && UCalendar.isPassDay(modifyTime, passDay)) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    /**
     * 覆盖复制，source将完全覆盖target
     *
     * @param source   源
     * @param target   目标
     * @param <E>      源类型
     * @param <T>      目标类型
     * @param excludes 排除的属性
     */
    public static <E, T> void fullCopy(E source, T target, String... excludes) {
        BeanUtils.copyProperties(source, target, excludes);
    }

    /**
     * （Set类型）批量覆盖复制，source将完全覆盖target
     *
     * @param <E>        源类型
     * @param <T>        目标类型
     * @param source     源集合
     * @param targetType 目标类型
     * @param excludes   排除的属性
     */
    public static <E, T> Set<T> fullCopySet(Set<E> source, Class<T> targetType, String... excludes) {
        Iterator<E> sourceIter = source.iterator();
        Set<T> set = UCollection.optimizeInitialCapacitySet(source.size());
        try {
            while (sourceIter.hasNext()) {
                E s = sourceIter.next();
                T t = targetType.getConstructor().newInstance();
                fullCopy(s, t);
                set.add(t);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return set;
    }

    /**
     * （List类型）批量覆盖复制，source将完全覆盖target
     *
     * @param <E>        源类型
     * @param <T>        目标类型
     * @param source     源集合
     * @param targetType 目标类型
     * @param excludes   排除的属性
     */
    public static <E, T> List<T> fullCopyList(List<E> source, Class<T> targetType, String... excludes) {
        Iterator<E> sourceIter = source.iterator();
        List<T> list = UCollection.optimizeInitialCapacityArrayList(source.size());
        try {
            while (sourceIter.hasNext()) {
                E s = sourceIter.next();
                T t = targetType.getConstructor().newInstance();
                fullCopy(s, t);
                list.add(t);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 半复制，target比source不同的值才会覆盖，其余保留
     *
     * @param source   源
     * @param target   目标
     * @param <E>      源类型
     * @param <T>      目标类型
     * @param excludes 排除的字段
     */
    public static <E, T> void halfCopy(E source, T target, String... excludes) {
        BeanUtil.copyProperties(source, target, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true).setIgnoreProperties(excludes));
    }

    /**
     * 批量半复制，target比source不同的值才会覆盖，其余保留
     *
     * @param source     源集合
     * @param targetType 目标类型
     * @param <E>        源类型
     * @param <T>        目标类型
     * @param excludes   排除的属性
     */
    public static <E, T> List<T> halfCopyCollections(Collection<E> source, Class<T> targetType, String... excludes) {
        Iterator<E> sourceIter = source.iterator();
        List<T> list = new ArrayList<>(source.size());
        try {
            while (sourceIter.hasNext()) {
                E s = sourceIter.next();
                T t = targetType.getConstructor().newInstance();
                halfCopy(s, t);
                list.add(t);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return list;
    }
}
