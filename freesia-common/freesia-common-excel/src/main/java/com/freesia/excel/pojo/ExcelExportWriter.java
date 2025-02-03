package com.freesia.excel.pojo;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author Evad.Wu
 * @Description 由 {@link com.freesia.excel.handler.ExcelExportHandler} 构建出的对象
 * @date 2022-11-23
 */
@Data
@AllArgsConstructor
public class ExcelExportWriter {
    /**
     * {@link ExcelWriter}
     */
    private ExcelWriter excelWriter;
    /**
     * {@link WriteSheet}
     */
    private WriteSheet writeSheet;
}
