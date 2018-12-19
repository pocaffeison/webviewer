package com.rsupport.webviewer.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"PMD.UseSingleton", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
public final class ExcelToSql {

    private static final String DEFAULT_DATA_CELL_COLOR_HEX = "FFFFFF00";

    public static void generateSql(String fileName) throws Exception {
        generateSql("src/main/resources/", fileName, fileName, false);
    }

    public static void generateSqlIgnoreTestData(String fileName) throws Exception {
        generateSql("src/main/resources/", "default-data", fileName, true);
    }

    private static void generateSql(String destFilePath, String fileName, String excelFileName, boolean ignoreTestData) throws Exception {
        String excelFilePath = "src/test/resources/".concat(excelFileName).concat(".xlsx");
        InputStream inputStream = new FileInputStream(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        StringBuilder sb = new StringBuilder();

        int sheetCount = wb.getNumberOfSheets();
        if (sheetCount == 0)
            return;

        for (int i = 0; i < sheetCount; i++) {
            String sql = getInsertSql(wb, i, ignoreTestData);
            sb.append(sql);
        }


        File file = new File(destFilePath.concat(fileName).concat(".sql"));
        PrintWriter out = new PrintWriter(file, "UTF-8");
        out.println(sb.toString());
        out.close();
    }

    private static String getInsertSql(XSSFWorkbook wb, int i, boolean ignoreTestData) {
        XSSFSheet sheet = wb.getSheetAt(i);
        String tableName = wb.getSheetName(i);

        int rowCount = sheet.getPhysicalNumberOfRows();
        if (rowCount == 0)
            return "";

        String sql = getInsertSql(tableName, sheet, ignoreTestData);
        if (StringUtils.isBlank(sql))
            return "";

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("-- ").append(tableName).append(" START").append("\n");
        sb.append(sql);
        sb.append("-- ").append(tableName).append(" END").append("\n");
        return sb.toString();
    }

    private static String getInsertSql(String tableName, XSSFSheet sheet, boolean ignoreTestData) {
        List<String> columns = getColumns(sheet, ignoreTestData);
        if (CollectionUtils.isEmpty(columns))
            return "";


        String sql = getInsertSql(tableName, sheet, columns, ignoreTestData);
        if (StringUtils.isBlank(sql))
            return "";

        return sql;
    }

    private static List<String> getColumns(XSSFSheet sheet, boolean ignoreTestData) {
        List<String> columns = new ArrayList<>();
        String columnName;
        XSSFRow headerRow = sheet.getRow(0);
        int cellCount = headerRow.getPhysicalNumberOfCells();

        for (int ii = 0; ii < cellCount; ii++) {
            XSSFCell cell = headerRow.getCell(ii);
            if (ii == 0 && ignoreTestData) {
                if (cell == null)
                    return columns;
                try {
                    if (ii == 0 && ignoreTestData)
                        checkIgnoreTestData(cell);
                } catch (NoDefaultDataColorException ex) {
                    return columns;
                }
            }
            columnName = getStringCellValue(cell);
            if (StringUtils.isBlank(columnName))
                continue;
            columns.add(columnName);
        }

        return columns;
    }

    private static String getInsertSql(String tableName, XSSFSheet sheet, List<String> columns, boolean ignoreTestData) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        StringBuilder sb = new StringBuilder();
        for (int j = 1; j < rowCount; j++) {
            List<String> values = new ArrayList<>();

            XSSFRow row = sheet.getRow(j);
            if (row == null)
                break;

            boolean isValidRow = false;
            String value;

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
                    values.add("NULL");
                    continue;
                }

                if (value.startsWith("<![CDATA[") || value.endsWith("]]>")) {
                    value = StringUtils.removeStart(value, "<![CDATA[");
                    value = StringUtils.removeEnd(value, "]]>");
                }

                if (isNumericCellValue(cell)) {
                    values.add(value);
                } else {
                    values.add("'" + value + "'");
                }

                isValidRow = true;
            }

            if (isValidRow) {
                sb.append("INSERT INTO ");
                sb.append(tableName);
                sb.append(" (");
                sb.append(StringUtils.join(columns, ","));
                sb.append(") VALUES (");
                sb.append(StringUtils.join(values, ","));
                sb.append(");\n");
            }
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
        if (!DEFAULT_DATA_CELL_COLOR_HEX.equals(color.getARGBHex())) {
            throw new NoDefaultDataColorException();
        }
    }

    public static String getStringCellValue(XSSFCell cell) {
        if (cell == null)
            return null;
        String result;
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            result = String.valueOf(cell.getNumericCellValue());
            result = StringUtils.removeEnd(result, ".0");
        } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            result = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            result = String.valueOf((int) cell.getNumericCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            result = String.valueOf(cell.getBooleanCellValue());
        } else {
            result = null;
        }
        return result;
    }

    public static class NoDefaultDataColorException extends Exception {
        private static final long serialVersionUID = -8826410309764387724L;
    }

    public static boolean isNumericCellValue(XSSFCell cell) {
        boolean numeric = false;
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            numeric = true;
        }
        return numeric;
    }
}
