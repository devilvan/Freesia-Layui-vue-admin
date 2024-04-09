package com.freesia.excel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.freesia.excel.listener.BaseImportEntityListener;
import com.freesia.excel.pojo.BaseImportEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.InputStream;

/**
 * @author Evad.Wu
 * @Description Excel相关 工具类
 * @date 2024-02-23
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UExcel {
    /**
     * 导入excel，使用默认导入监听器
     *
     * @param fileName 文件名
     * @param dataType 数据类型
     * @param <T>      数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(String fileName, Class<T> dataType) {
        read(fileName, dataType, new BaseImportEntityListener<>(), null, null);
    }

    /**
     * 导入excel，使用默认导入监听器
     *
     * @param fileName 文件名
     * @param dataType 数据类型
     * @param sheetNo  表单编号
     * @param <T>      数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(String fileName, Class<T> dataType, Integer sheetNo) {
        read(fileName, dataType, new BaseImportEntityListener<>(), sheetNo, null);
    }

    /**
     * 导入excel，使用默认导入监听器
     *
     * @param fileName  文件名
     * @param dataType  数据类型
     * @param sheetName 表单名称
     * @param <T>       数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(String fileName, Class<T> dataType, String sheetName) {
        read(fileName, dataType, new BaseImportEntityListener<>(), null, sheetName);
    }

    /**
     * 导入excel，使用默认导入监听器
     *
     * @param fileName  文件名
     * @param dataType  数据类型
     * @param sheetNo   表单编号
     * @param sheetName 表单名称
     * @param <T>       数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(String fileName, Class<T> dataType, Integer sheetNo, String sheetName) {
        read(fileName, dataType, new BaseImportEntityListener<>(), sheetNo, sheetName);
    }

    /**
     * 导入excel
     *
     * @param fileName     文件名
     * @param dataType     数据类型
     * @param readListener 导入监听器
     * @param <T>          数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(String fileName, Class<T> dataType, ReadListener<T> readListener) {
        read(fileName, dataType, readListener, null, null);
    }

    /**
     * 导入excel
     *
     * @param fileName     文件名
     * @param dataType     数据类型
     * @param readListener 导入监听器
     * @param sheetNo      表单编号
     * @param <T>          数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(String fileName, Class<T> dataType, ReadListener<T> readListener, Integer sheetNo) {
        read(fileName, dataType, readListener, sheetNo, null);
    }

    /**
     * 导入excel
     *
     * @param fileName     文件名
     * @param dataType     数据类型
     * @param readListener 导入监听器
     * @param sheetName    表单名称
     * @param <T>          数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(String fileName, Class<T> dataType, ReadListener<T> readListener, String sheetName) {
        read(fileName, dataType, readListener, null, sheetName);
    }

    /**
     * 导入excel，使用默认导入监听器
     *
     * @param inputStream 文件名
     * @param dataType    数据类型
     * @param <T>         数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(InputStream inputStream, Class<T> dataType) {
        read(inputStream, dataType, new BaseImportEntityListener<>(), null, null);
    }

    /**
     * 导入excel，使用默认导入监听器
     *
     * @param inputStream 输入流
     * @param dataType    数据类型
     * @param sheetNo     表单编号
     * @param <T>         数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(InputStream inputStream, Class<T> dataType, Integer sheetNo) {
        read(inputStream, dataType, new BaseImportEntityListener<>(), sheetNo, null);
    }

    /**
     * 导入excel，使用默认导入监听器
     *
     * @param inputStream 输入流
     * @param dataType    数据类型
     * @param sheetName   表单名称
     * @param <T>         数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(InputStream inputStream, Class<T> dataType, String sheetName) {
        read(inputStream, dataType, new BaseImportEntityListener<>(), null, sheetName);
    }

    /**
     * 导入excel，使用默认导入监听器
     *
     * @param inputStream 输入流
     * @param dataType    数据类型
     * @param sheetNo     表单编号
     * @param sheetName   表单名称
     * @param <T>         数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(InputStream inputStream, Class<T> dataType, Integer sheetNo, String sheetName) {
        read(inputStream, dataType, new BaseImportEntityListener<>(), sheetNo, sheetName);
    }

    /**
     * 导入excel
     *
     * @param fileName     文件名
     * @param dataType     数据类型
     * @param readListener 导入监听器
     * @param sheetNo      表单编号
     * @param sheetName    表单名称
     * @param <T>          数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(
            @Validated @NotEmpty(message = "文件路径不能为空！") String fileName,
            @Validated @NotNull(message = "数据类型！") Class<T> dataType,
            ReadListener<T> readListener,
            Integer sheetNo,
            String sheetName) {
        EasyExcel.read(fileName, dataType, readListener).sheet(sheetNo, sheetName).doRead();
    }

    /**
     * 导入excel
     *
     * @param inputStream  输入流
     * @param dataType     数据类型
     * @param readListener 导入监听器
     * @param sheetNo      表单编号
     * @param sheetName    表单名称
     * @param <T>          数据类型泛型
     */
    public static <T extends BaseImportEntity> void read(
            @Validated @NotNull(message = "文件路径不能为空！") InputStream inputStream,
            @Validated @NotNull(message = "数据类型！") Class<T> dataType,
            ReadListener<T> readListener,
            Integer sheetNo,
            String sheetName) {
        EasyExcel.read(inputStream, dataType, readListener).sheet(sheetNo, sheetName).doRead();
    }
}
