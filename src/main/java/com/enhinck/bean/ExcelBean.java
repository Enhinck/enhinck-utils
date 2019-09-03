package com.enhinck.bean;

import com.enhinck.excel.ExcelColumn;
import lombok.Data;

@Data
public class ExcelBean {
    @ExcelColumn(name = "field")
    private String field;
    @ExcelColumn(name = "desc")
    private String desc;
    @ExcelColumn(name = "type")
    private String type;
    @ExcelColumn(name = "mark")
    private String mark;
}
