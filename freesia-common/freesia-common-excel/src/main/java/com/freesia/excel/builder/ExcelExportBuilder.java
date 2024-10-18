package com.freesia.excel.builder;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.freesia.excel.constant.ExcelCellWriteStyle;
import com.freesia.excel.constant.ExcelSuffix;
import com.freesia.excel.pojo.ExcelExportWriter;
import com.freesia.util.UPath;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;


/**
 * @author Evad.Wu
 * @Description easyexcel 建造器
 * @date 2022-11-23
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExcelExportBuilder {
    private final ExcelWriterBuilder writerBuilder = EasyExcelFactory.write();
    private final ExcelWriterSheetBuilder sheetBuilder = new ExcelWriterSheetBuilder();

    /**
     * 默认表单样式
     *
     * @return {@link HorizontalCellStyleStrategy}
     */
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

    /**
     * 步骤一：构造器创建
     *
     * @return 构造器
     */
    public static ExcelExportBuilder create() {
        return new ExcelExportBuilder();
    }

    /**
     * 步骤二：根据建造器收集的成员对象参数，构造一个 {@link ExcelExportWriter} 对象
     *
     * @return {@link ExcelExportWriter}
     */
    public ExcelExportWriter build() {
        com.alibaba.excel.ExcelWriter excelWriter = this.writerBuilder.build();
        writerBuilder.registerWriteHandler(getBasicWriteStrategy());
        writerBuilder.registerWriteHandler(new ExcelCellWriteStyle<>());
        WriteSheet writeSheet = this.sheetBuilder.build();
        return new ExcelExportWriter(excelWriter, writeSheet);
    }

    /**
     * 设置导出excel的完整路径
     *
     * @param exportPath 导出文件路径前缀
     * @param fileName   文件名
     * @param suffix     后缀
     * @return builder对象
     */
    public ExcelExportBuilder setCompletePath(
            @Validated @NotNull(message = "导出路径不能为空！") String exportPath,
            @Validated @NotNull(message = "导出文件名不能为空！") String fileName,
            @Validated @NotNull(message = "文件后缀不能为空！") ExcelSuffix suffix) {
        writerBuilder.file(UPath.validExportPath(exportPath, fileName, suffix.getSuffix()));
        return this;
    }

    /**
     * 设置表格字段对应的bean类型
     *
     * @param classType 类型
     * @return builder对象
     */
    public ExcelExportBuilder setClassType(@Validated @NotNull(message = "导出类型不能为空！") Class<?> classType) {
        writerBuilder.head(classType);
        return this;
    }

    /**
     * 设置sheet的下标，默认0
     *
     * @param sheetNo 表单下标
     * @return builder对象
     */
    public ExcelExportBuilder setSheetNo(Integer sheetNo) {
        sheetBuilder.sheetNo(sheetNo);
        return this;
    }

    /**
     * 设置sheet的名字
     *
     * @param sheetName 表单名字
     * @return builder对象
     */
    public ExcelExportBuilder setSheetName(String sheetName) {
        sheetBuilder.sheetName(sheetName);
        return this;
    }

}
