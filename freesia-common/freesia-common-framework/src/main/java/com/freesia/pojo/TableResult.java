package com.freesia.pojo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freesia.constant.ResultCode;
import com.freesia.util.UMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 表格分页数据 值对象
 * @date 2023-08-30
 */
@Data
@NoArgsConstructor
public class TableResult<T> {
    @Schema(description = "总记录数")
    private long total;
    @Schema(description = "列表数据")
    private List<T> rows;
    @Schema(description = "消息状态码")
    private int code;
    @Schema(description = "消息内容")
    private String msg;
    @Schema(description = "成功标识")
    private boolean success;

    public TableResult(List<T> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public static <T> TableResult<T> build(IPage<T> page) {
        TableResult<T> rspData = new TableResult<>();
        rspData.setCode(ResultCode.SUCCESS_200.getCode());
        rspData.setMsg(UMessage.message("msg.info.query.success"));
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        rspData.setSuccess(true);
        return rspData;
    }

    public static <T> TableResult<T> build(List<T> list) {
        TableResult<T> rspData = new TableResult<>();
        rspData.setCode(ResultCode.SUCCESS_200.getCode());
        rspData.setMsg(UMessage.message("msg.info.query.success"));
        rspData.setRows(list);
        rspData.setTotal(list.size());
        rspData.setSuccess(true);
        return rspData;
    }

    public static <T> TableResult<T> build() {
        TableResult<T> rspData = new TableResult<>();
        rspData.setCode(ResultCode.SUCCESS_200.getCode());
        rspData.setMsg(UMessage.message("msg.info.query.success"));
        rspData.setSuccess(true);
        return rspData;
    }
}
