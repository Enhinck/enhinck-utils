package com.enhinck.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import com.enhinck.person.ChineseName;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CommonExcelUtil {

    private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");

    private static String getTel() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String thrid = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + thrid;
    }

    public static int getNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    public static void main(String[] args) {
        List<Object[]> errors = new ArrayList<>();


        // 如心小镇·香樟园
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 3; j++) {
                createHouse(errors, "如心小镇·香樟园", i + "幢", j + "单元");
            }
        }
        // 如心小镇·留香园
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 3; j++) {
                createHouse(errors, "如心小镇·留香园", i + "幢", j + "单元");
            }
        }

        // 如心小镇·香枫园
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 3; j++) {
                createHouse(errors, "如心小镇·香枫园", i + "幢", j + "单元");
            }
        }


        dealError(errors, new File("C:\\hs_house.xlsx"));
    }

    private static void createHouse(List<Object[]> errors, String projectName, String buiding, String unit) {
        for (int i = 0; i < 20; i++) {
            Object[] objects = new Object[6];
            objects[0] = projectName;
            objects[1] = buiding;
            objects[2] = unit;
            objects[3] = i + 1 + "01";
            objects[4] = ChineseName.getRandomName();
            objects[5] = getTel();
            errors.add(objects);
        }
    }


    /**
     * 文件处理，和原文件相对路径保持一致，分别存到error，right，errorName文件夹内
     */
    public static void dealError(List<Object[]> errors, File excelFile) {
        try {
            if (errors.size() > 0) {
                InputStream inForOut = new FileInputStream(excelFile);
                Workbook wbForOut = new XSSFWorkbook(inForOut);
                String outFilePath = "TEST.xlsx";
                File file = new File(outFilePath);
                FileOutputStream out = new FileOutputStream(outFilePath);
                Sheet sheetForOut = wbForOut.getSheetAt(0);
                int last = sheetForOut.getLastRowNum();
                int first = 1;
                //删除原数据
                for (int i = first; i <= last; i++) {
                    Row row = sheetForOut.getRow(i);
                    if (row != null) {
                        sheetForOut.removeRow(row);
                    }
                }
                //添加错误数据
                for (int i = 0; i < errors.size(); ++i) {
                    Object[] dataArray = errors.get(i);
                    Row row = sheetForOut.createRow(i + 1);
                    for (int j = 0; j < dataArray.length; ++j) {
                        if (dataArray[j] != null) {
                            String value = String.valueOf(dataArray[j]);
                            row.createCell(j).setCellValue(value);
                        }
                    }
                }
                //另存为目标文件
                wbForOut.write(out);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Workbook create(InputStream in) throws IOException,
            InvalidFormatException {
        if (!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(in)) {
            return new HSSFWorkbook(in);
        }
        throw new IllegalArgumentException("你的excel版本目前poi解析不了");
    }

    /**
     * @param file      文件
     * @param startline 开始读取行数
     * @return
     * @throws InvalidFormatException 文件错误 poi未支持
     * @throws IOException            文件读取错误
     */
    public static List<Object[]> getDataFromExcel(File file, int startline)
            throws InvalidFormatException, IOException {
        List<Object[]> arraylist = new ArrayList<Object[]>();
        FileInputStream inputstream = new FileInputStream(file);
        Workbook xssfWorkbook = create(inputstream);
        //for (int i = 0; i < 1; i++) {
        //只读第一个Sheet
        Sheet xssfSheet = xssfWorkbook.getSheetAt(0);
        for (int j = 0; j < xssfSheet.getPhysicalNumberOfRows(); j++)
            if (j >= startline) {
                Row hssfrow = xssfSheet.getRow(j);
                if (hssfrow == null) {
                    break;
                }
                short word0 = hssfrow.getLastCellNum();
                if (word0 < 0)
                    continue;
                Object aobj[] = new Object[word0];
                for (int k = 0; k < word0; k++) {
                    Cell xssfCell = hssfrow.getCell((short) k);
                    if (xssfCell != null)
                        switch (xssfCell.getCellType()) {
                            case HSSFCell.CELL_TYPE_NUMERIC: // '\0'
                                double d = xssfCell.getNumericCellValue();
                                if (HSSFDateUtil.isCellDateFormatted(xssfCell)) {
                                    aobj[k] = new java.sql.Date(
                                            (HSSFDateUtil.getJavaDate(d))
                                                    .getTime());
                                } else {
                                    int d_int = (int) d;
                                    if (d_int == d)
                                        aobj[k] = (new StringBuffer(
                                                String.valueOf(d_int)))
                                                .toString();
                                    else
                                        aobj[k] = (new StringBuffer(
                                                String.valueOf(d))).toString();
                                }
                                break;
                            case HSSFCell.CELL_TYPE_STRING: // '\001'
                                aobj[k] = xssfCell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA: // '\002'
                                aobj[k] = xssfCell.getCellFormula();
                                break;
                            case HSSFCell.CELL_TYPE_BLANK: // '\003'
                                aobj[k] = null;
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN: // '\004'
                                aobj[k] = new Boolean(
                                        xssfCell.getBooleanCellValue());
                                break;
                            case HSSFCell.CELL_TYPE_ERROR: // '\005'
                                aobj[k] = new Byte(xssfCell.getErrorCellValue());
                                break;
                            default:
                                aobj[k] = null;
                                break;
                        }
                    else {
                        aobj[k] = null;
                    }

                }
                arraylist.add(aobj);
            }
        //}

        return arraylist;
    }
}
