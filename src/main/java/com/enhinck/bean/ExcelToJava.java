package com.enhinck.bean;

import com.enhinck.excel.CommonExcelUtilNew;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ExcelToJava {
    public static void main(String[] args) throws FileNotFoundException {
        List<ExcelBean> excelBeans = CommonExcelUtilNew.getDataFromExcel(new FileInputStream(new File("D:\\TEST.xlsx")), ExcelBean.class);
        StringBuilder builder = new StringBuilder();
        for (ExcelBean excelBean : excelBeans) {
            builder.append("/**").append(excelBean.getDesc()).append(" ").append(excelBean.getMark()).append("*/").append("\n");
            builder.append(" @ApiModelProperty(value = \"").append(excelBean.getDesc()).append("\", name = \"").append(excelBean.getField()).append("\")").append("\n");
            builder.append("@JSONField(name = \"").append(excelBean.getField()).append("\")").append("\n");
            builder.append("private").append(" ").append(excelBean.getType()).append(" ").append(firstLowCase(excelBean.getField())).append(";").append("\n");
        }
        System.out.println(builder.toString());
    }


    public static String firstLowCase(String value) {
        return value.substring(0, 1).toLowerCase() + value.substring(1, value.length());
    }
}
