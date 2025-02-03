package com.freesia.excel.handler;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.freesia.excel.constant.ExcelCellWriteStyle;
import com.freesia.excel.pojo.BaseExportEntity;
import com.freesia.excel.pojo.ExcelExportDto;
import com.freesia.util.UPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description Excel导出 处理类
 * @date 2022-09-07
 */
@Slf4j
public class ExcelExportHandler {
    /**
     * 步骤一：根据路径、文件名、后缀、导出类型、生成excelWriter对象
     *
     * @param excelExportDto {@link ExcelExportDto}
     * @return {@link ExcelWriter}
     */
    public static <T extends BaseExportEntity> ExcelWriter buildExcelWriter(ExcelExportDto<T> excelExportDto) {
        String exportPath = UPath.buildExportPath(excelExportDto.getExportPath(), excelExportDto.getFileName(), excelExportDto.getSuffix().getValue());
        return EasyExcelFactory.write(exportPath)
                .head(excelExportDto.getClassType())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(getBasicWriteStrategy())
                .registerWriteHandler(new ExcelCellWriteStyle<>())
                .build();
    }

    /**
     * 步骤二：根据sheet生成WriteSheet对象
     *
     * @param sheetName 表单名称
     * @return {@link WriteSheet}
     */
    public static WriteSheet buildWriteSheet(String sheetName) {
        return EasyExcelFactory.writerSheet(sheetName).build();
    }

    /**
     * 步骤二：根据sheet生成WriteSheet对象
     *
     * @param sheetNo 表单下标
     * @return {@link WriteSheet}
     */
    public static WriteSheet buildWriteSheet(int sheetNo) {
        return EasyExcelFactory.writerSheet(sheetNo).build();
    }

    /**
     * 步骤二：根据sheet生成WriteSheet对象
     *
     * @param sheetName 表单名称
     * @param sheetNo   表单下标
     * @return {@link WriteSheet}
     */
    public static WriteSheet buildWriteSheet(int sheetNo, String sheetName) {
        return EasyExcelFactory.writerSheet(sheetNo, sheetName).build();
    }

    /**
     * 步骤三：装载数据导出
     *
     * @param excelWriter 导出excel的基本信息对象
     * @param writeSheet  导出excel的表单对象
     * @param exportList  导出的数据
     * @param <T>         导出数据的类型
     */
    public static <T extends BaseExportEntity> void doExport(List<T> exportList, ExcelWriter excelWriter, WriteSheet writeSheet) {
        excelWriter.write(exportList, writeSheet);
    }

    /**
     * 最后：关闭ExcelWriter
     *
     * @param excelWriter 导出流
     */
    public static void doFinish(ExcelWriter excelWriter) {
        if (null != excelWriter) {
            excelWriter.close();
        }
    }


    public static HorizontalCellStyleStrategy getBasicWriteStrategy() {
        // 头字体策略
        WriteFont writeHeadFont = new WriteFont();
        writeHeadFont.setFontName("微软雅黑");
        writeHeadFont.setFontHeightInPoints((short) 12);
        writeHeadFont.setBold(false);
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        headWriteCellStyle.setWriteFont(writeHeadFont);
        // 内容字体策略
        WriteFont writeContentFont = new WriteFont();
        writeContentFont.setFontName("黑体");
        writeContentFont.setFontHeightInPoints((short) 12);
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setWriteFont(writeContentFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
}
