package com.freesia.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Date;

@Data
public class ExportSleepEntity {
    @ExcelProperty(value = "来源")
    private String source;
    @ExcelProperty(value = "评论人ID")
    private String userId;
    @ExcelProperty(value = "评论人名称")
    private String userName;
    @ExcelProperty(value = "标题")
    private String title;
    @ExcelProperty(value = "评论内容")
    private String content;
    @ExcelProperty(value = "评分")
    private String level;
    @ExcelProperty(value = "发布时间")
    @ColumnWidth(value = 200)
    private Date operateTime;
    @ExcelProperty(value = "URL")
    private String url;
    @ExcelProperty(value = "分页数")
    private Integer page;
}
