package com.rsupport.webviewer.core.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ExcelToXmlDataSet {

    private static final String DEFALUTDATA_CELL_COLOR_HEX = "FFFFFF00"; //yellow:FFFFFF00, red:FFFF0000, green:FF00B050, blue:FF00B0F0
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static void generateDataSetXmlToDestinationPath(String destPath, String fileName) throws Exception {
        generateDataSetXml(destPath, fileName, fileName, false);
    }

    public static void generateDataSetXmlToDestinationPath(String destPath, String destFileName, String sourceFileName)
            throws Exception {
        generateDataSetXml("src/test/resources/", destFileName, sourceFileName, false);
    }

    public static void generateDataSetXml(String fileName) throws Exception {
        generateDataSetXml("src/test/resources/", fileName, fileName, false);
    }

    public static void generateDataSetXml(String destFileName, String sourceFileName) throws Exception {
        generateDataSetXml("src/test/resources/", destFileName, sourceFileName, false);
    }

    public static void generateDataSetXmlIgnoreTestData(String fileName) throws Exception {
        generateDataSetXml("src/test/resources/", "default-data", fileName, true);
    }

    public static void generateDataSetXmlIgnoreTestData(String destPath, String fileName) throws Exception {
        generateDataSetXml(destPath, "default-data", fileName, true);
    }

    private static void generateDataSetXml(String destFilePath, String fileName, String excelFileName, boolean ignoreTestData)
            throws Exception {
        String excelFilePath = "src/test/resources/".concat(excelFileName).concat(".xlsx");
        InputStream inputStream = new FileInputStream(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n<dataset>");
        int sheetCount = wb.getNumberOfSheets();
        if (sheetCount == 0)
            return;

        for (int i = 0; i < sheetCount; i++) {
            String tableXml = getTableXml(wb, i, ignoreTestData);
            sb.append(tableXml);
        }
        sb.append("\n</dataset>");

        File file = new File(destFilePath.concat(fileName).concat(".xml"));
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), Charset.forName(DEFAULT_CHARSET)));
        out.println(sb.toString());
        out.close();
    }

    private static String getTableXml(XSSFWorkbook wb, int i, boolean ignoreTestData) {
        XSSFSheet sheet = null;
        String tableName;
        tableName = wb.getSheetName(i);
        sheet = wb.getSheetAt(i);
        int rowCount = sheet.getPhysicalNumberOfRows();
        if (rowCount == 0)
            return "";

        String tableDataXml = getTableDataXml(sheet, ignoreTestData);
        if (StringUtils.isBlank(tableDataXml))
            return "";

        StringBuilder sb = new StringBuilder();
        sb.append("\n<table name=\"").append(tableName).append("\">").append(tableDataXml).append("\n</table>");
        return sb.toString();
    }

    private static String getTableDataXml(XSSFSheet sheet, boolean ignoreTestData) {
        List<String> columns = new ArrayList<String>();

        String columnString = getColumnXml(sheet, columns, ignoreTestData);
        if (StringUtils.isBlank(columnString))
            return "";

        String rowString = getRowXml(sheet, columns, ignoreTestData);
        if (StringUtils.isBlank(rowString))
            return "";

        StringBuilder sb = new StringBuilder();
        sb.append(columnString).append(rowString);
        return sb.toString();
    }

    private static String getColumnXml(XSSFSheet sheet, List<String> columns, boolean ignoreTestData) {
        String columnName = "";
        XSSFRow headerRow = sheet.getRow(0);
        int cellCount = headerRow.getPhysicalNumberOfCells();

        StringBuilder sb = new StringBuilder();
        for (int ii = 0; ii < cellCount; ii++) {
            XSSFCell cell = headerRow.getCell(ii);
            if (ii == 0 && ignoreTestData) {
                if (cell == null)
                    return "";
                try {
                    if (ii == 0 && ignoreTestData)
                        checkIgnoreTestData(cell);
                } catch (NoDefaultDataColorException ex) {
                    return "";
                }
            }
            columnName = getStringCellValue(cell);
            if (StringUtils.isBlank(columnName))
                continue;
            columns.add(columnName);
            sb.append("\n    ").append("<column>").append(columnName).append("</column>");
        }
        return sb.toString();
    }

    private static String getRowXml(XSSFSheet sheet, List<String> columns, boolean ignoreTestData) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        StringBuilder sb = new StringBuilder();
        for (int j = 1; j < rowCount; j++) {
            XSSFRow row = sheet.getRow(j);
            if (row == null)
                break;
            StringBuilder rowTag = new StringBuilder("\n    <row>");
            boolean isValidRow = false;
            String value = "";

            for (int k = 0; k < columns.size(); k++) {
                XSSFCell cell = row.getCell(k);

                if (k == 0 && ignoreTestData) {
                    if (cell == null) {
                        isValidRow = false;
                        break;
                    }
                    try {
                        checkIgnoreTestData(cell);
                    } catch (NoDefaultDataColorException ex) {
                        isValidRow = false;
                        break;
                    }
                }
                value = getStringCellValue(cell);
                if (k == 0 && StringUtils.isBlank(value)) {
                    isValidRow = false;
                    break;
                }
                if (value == null || "NULL".equalsIgnoreCase(value)) {
                    rowTag.append("\n         ").append("<null />");
                    continue;
                }

                if (!value.startsWith("<![CDATA[") || !value.endsWith("]]>"))
                    value = StringEscapeUtils.escapeXml(value);
                rowTag.append("\n         ").append("<value description=\"").append(columns.get(k)).append("\">").append(value)
                        .append("</value>");
                isValidRow = true;
            }
            rowTag.append("\n    </row>");
            if (isValidRow)
                sb.append(rowTag);
        }
        return sb.toString();
    }

    private static void checkIgnoreTestData(XSSFCell cell) throws NoDefaultDataColorException {
        XSSFCellStyle cellStyle = cell.getCellStyle();
        if (cellStyle == null)
            throw new NoDefaultDataColorException();

        XSSFColor color = cellStyle.getFillForegroundXSSFColor();
        if (color == null)
            throw new NoDefaultDataColorException();
        if (!DEFALUTDATA_CELL_COLOR_HEX.equals(color.getARGBHex())) {
            throw new NoDefaultDataColorException();
        }
    }

    private static String getStringCellValue(XSSFCell cell) {
        return ExcelUtils.getStringCellValue(null, cell);
    }

    public static class NoDefaultDataColorException extends Exception {

        private static final long serialVersionUID = -8826410309764387724L;
    }
}
