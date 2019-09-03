package com.enhinck.excel;


import com.monitorjbl.xlsx.StreamingReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommonExcelUtilNew {

    @Data
    public static class ColumnObject {
        private Field field;
        private ExcelColumn excelColumn;
    }

    /**
     * @param file      文件
     * @param classType 类型
     * @return
     */
    public static <T> List<T> getDataFromExcel(MultipartFile file, Class<T> classType) throws IOException {
        return getDataFromExcel(file.getInputStream(), classType);
    }


    static Map<Class<?>, IExcelCellValueAdapt> cache = new HashedMap();

    /**
     * @param is        流
     * @param classType 类型
     * @return
     */
    public static <T> List<T> getDataFromExcel(InputStream is, Class<T> classType) {
        List<T> objects = new ArrayList<>();
        try {
            // 缓存读取 低内存占用
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(is);
            int sheetCount = 0;
            Field[] fields = classType.getDeclaredFields();
            Map<Integer, ColumnObject> colums = new HashMap<>();
            processCellIndexColumn(workbook, fields, colums);
            for (Sheet sheet : workbook) {
                int rowCount = 0;
                for (Row r : sheet) {
                    int cellIndex = 0;
                    T object = classType.newInstance();
                    for (Cell cell : r) {
                        ColumnObject columnObject = colums.get(cellIndex);
                        ExcelColumn excelColumn = columnObject.getExcelColumn();
                        IExcelCellValueAdapt iExcelCellValueAdapt = cache.get(excelColumn.valueAdapt());
                        if (iExcelCellValueAdapt == null) {
                            iExcelCellValueAdapt = excelColumn.valueAdapt().newInstance();
                            cache.put(excelColumn.valueAdapt(), iExcelCellValueAdapt);
                        }
                        Object value = iExcelCellValueAdapt.getCellValue(cell);
                        columnObject.getField().setAccessible(true);
                        columnObject.getField().set(object, value);
                        cellIndex++;
                    }
                    objects.add(object);
                    rowCount++;
                }
                sheetCount++;
            }
        } catch (Exception e) {
            log.info("读取excel异常:{}", e);
        }
        return objects;
    }


    /**
     * 获取excel标题列
     *
     * @param workbook
     * @param fields
     * @param colums
     */
    private static void processCellIndexColumn(Workbook workbook, Field[] fields, Map<Integer, ColumnObject> colums) {
        // 反射获取对应字段值
        out:
        for (Sheet sheet : workbook) {
            for (Row r : sheet) {
                int cellIndex = 0;
                for (Cell cell : r) {
                    String columnName = cell.getStringCellValue();
                    for (int i = 0; i < fields.length; i++) {
                        Field field = fields[i];
                        ExcelColumn excelColumn = fields[i].getAnnotation(ExcelColumn.class);
                        if (excelColumn != null) {
                            if (excelColumn.name().equalsIgnoreCase(columnName)) {
                                ColumnObject columnObject = new ColumnObject();
                                columnObject.setExcelColumn(excelColumn);
                                columnObject.setField(field);
                                colums.put(cellIndex, columnObject);
                            }
                        }
                    }
                    cellIndex++;
                }
                break out;
            }
        }
    }


    /**
     * @param file     文件
     * @param startRow 开始读取行数
     * @return
     */
    public static List<List<Object>> getDataFromExcel(MultipartFile file, int startRow) {
        List<List<Object>> objects = new ArrayList<>();
        try {
            InputStream is = file.getInputStream();
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(is);
            int sheetCount = 0;
            for (Sheet sheet : workbook) {
                int rowCount = 0;
                for (Row r : sheet) {
                    if (rowCount >= startRow) {
                        int cellCount = 0;
                        List<Object> rows = new ArrayList<>();
                        for (Cell cell : r) {
                            IExcelCellValueAdapt iExcelCellValueAdapt = cache.get(DefaultExcelCellValueAdapt.class);
                            if (iExcelCellValueAdapt == null) {
                                iExcelCellValueAdapt = new DefaultExcelCellValueAdapt();
                                cache.put(DefaultExcelCellValueAdapt.class, iExcelCellValueAdapt);
                            }
                            Object object = iExcelCellValueAdapt.getCellValue(cell);
                            rows.add(object);
                            cellCount++;
                        }
                        objects.add(rows);
                    }
                    rowCount++;
                }
                sheetCount++;
            }
        } catch (Exception e) {
        }
        return objects;
    }
}
