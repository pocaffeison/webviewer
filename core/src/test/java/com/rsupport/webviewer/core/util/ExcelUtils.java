package com.rsupport.webviewer.core.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = { "PMD.UseSingleton" })
public class ExcelUtils {

    public static String getStringCellValue(String key, XSSFCell cell) {
        if (cell == null)
            return StringUtils.isBlank(key) ? null : key;
        String result = null;
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            result = String.valueOf((double) cell.getNumericCellValue());
            result = StringUtils.removeEnd(result, ".0");
        } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            result = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            result = String.valueOf((int) cell.getNumericCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            result = String.valueOf(cell.getBooleanCellValue());
        } else {
            result = key;
        }
        return result;
    }
}
