package com.freesia.excel.handler;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.freesia.excel.builder.ExcelExportBuilder;
import com.freesia.excel.constant.ExcelCellWriteStyle;
import com.freesia.excel.pojo.BaseExportEntity;
import com.freesia.excel.pojo.ExcelExportDto;
import com.freesia.util.UPath;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
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
    public ExcelWriter doExcelWriter(@Valid ExcelExportDto excelExportDto) {
        ExcelWriter excelWriter = null;
        try {
            String exportPath = UPath.validExportPath(excelExportDto.getExportPath(), excelExportDto.getFileName(), excelExportDto.getSuffix().getValue());
            // 可选：.registerWriteHandler(new SimpleColumnWidthStyleStrategy(20))
            excelWriter = EasyExcelFactory.write(exportPath)
                    .head(excelExportDto.getClassType())
                    .registerWriteHandler(ExcelExportBuilder.getBasicWriteStrategy())
                    .registerWriteHandler(new ExcelCellWriteStyle<>())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelWriter;
    }

    /**
     * 步骤二：根据sheet生成WriteSheet对象
     *
     * @param sheet 表单名称
     * @return {@link WriteSheet}
     */
    public WriteSheet doWriteSheet(String sheet) {
        return EasyExcelFactory.writerSheet(sheet).build();
    }

    /**
     * 步骤三：装载数据导出
     *
     * @param excelWriter 导出excel的基本信息对象
     * @param writeSheet  导出excel的表单对象
     * @param exportList  导出的数据
     * @param <T>         导出数据的类型
     */
    public <T extends BaseExportEntity> void doExport(ExcelWriter excelWriter, WriteSheet writeSheet, List<T> exportList) {
        excelWriter.write(exportList, writeSheet);
    }

    /**
     * 最后：关闭ExcelWriter
     *
     * @param excelWriter 导出流
     */
    public void doFinish(ExcelWriter excelWriter) {
        if (null != excelWriter) {
            excelWriter.close();
        }
    }
}
