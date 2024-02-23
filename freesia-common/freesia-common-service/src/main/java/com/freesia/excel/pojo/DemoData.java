package com.freesia.excel.pojo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.freesia.constant.Constants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class DemoData extends BaseImportEntity {
    private String string;
    @JSONField(format = Constants.YMD_HMS_SSS)
    private Date date;
    private Double doubleData;
}
