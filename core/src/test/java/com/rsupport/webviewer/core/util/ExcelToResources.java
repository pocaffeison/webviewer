package com.rsupport.webviewer.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"PMD.UseSingleton"})
public final class ExcelToResources {

    private static final String DEFAULT_LANGUAGE = "en";

    public static void generate(String sourceExcelFilePath, String targetDirPath) {
        try {
            process(sourceExcelFilePath, targetDirPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void process(String excelFilePath, String targetDirPath) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelFilePath));
        int sheetCount = wb.getNumberOfSheets();
        if (sheetCount == 0)
            return;

        for (int i = 0; i < sheetCount; i++) {
            generateResources(wb.getSheetAt(i), targetDirPath);
        }
    }

    private static void generateResources(XSSFSheet sheet, String targetDirPath) throws Exception {
        String sheetName = sheet.getSheetName();
        List<ResourceVO> resourceVOs = getResourceVOs(sheet);
        generateResource(sheetName, resourceVOs, targetDirPath);
    }

    private static void generateResource(String sheetName, List<ResourceVO> resourceVOs, String targetDirPath) throws Exception {
        for (ResourceVO vo : resourceVOs) {
            generatePropertiesResource(sheetName, vo, targetDirPath);
        }

        ResourceVO defaultResourcesVO = getDefaultResourceVO(resourceVOs);
        generatePropertiesResource(sheetName, defaultResourcesVO, targetDirPath, "");
    }

    private static ResourceVO getDefaultResourceVO(List<ResourceVO> resourceVOs) {
        for (ResourceVO vo : resourceVOs) {
            if (DEFAULT_LANGUAGE.equals(vo.getLanguage())) {
                return vo;
            }
        }
        return null;
    }

    private static void generatePropertiesResource(String sheetName, ResourceVO vo, String targetDirPath) throws Exception {
        if (StringUtils.isBlank(vo.getLanguage()))
            return;
        generatePropertiesResource(sheetName, vo, targetDirPath, vo.getLanguage());
    }

    private static void generatePropertiesResource(String sheetName, ResourceVO vo, String targetDirPath, String targetLanguage) throws Exception {
        String resourceFilePath = getResourceFilePath(sheetName, targetLanguage, targetDirPath);
        PropertiesConfiguration config = new PropertiesConfiguration();

        config.setDelimiterParsingDisabled(true);
        config.setEncoding("utf-8");
        for (Map.Entry<String, String> entry : vo.getDataMap().entrySet()) {
            if (entry != null && entry.getKey() != null)
                config.addProperty(entry.getKey(), entry.getValue());
        }
        config.save(resourceFilePath);
    }

    private static List<ResourceVO> getResourceVOs(XSSFSheet sheet) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        XSSFRow header = sheet.getRow(0);
        List<ResourceVO> resourceVOs = createResourceVOs(header);
        int languageCount = resourceVOs.size();
        for (int i = 1; i < rowCount; i++) {
            XSSFRow row = sheet.getRow(i);
            String key = "";
            for (int j = 0; j < languageCount + 1; j++) {
                if (row == null)
                    break;
                XSSFCell cell = row.getCell(j);
                if (j == 0) {
                    key = getStringCellValue(cell);
                    if (StringUtils.isBlank(key))
                        break;
                } else {
                    String value = getStringCellValue(key, cell);
                    try {
                        if (StringUtils.isBlank(resourceVOs.get(j - 1).getLanguage()))
                            continue;
                        resourceVOs.get(j - 1).getDataMap().put(key, value);
                    } catch (NullPointerException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        return resourceVOs;
    }

    private static List<ResourceVO> createResourceVOs(XSSFRow header) {
        int cellCount = header.getPhysicalNumberOfCells();
        List<ResourceVO> resourceVOs = new ArrayList<ResourceVO>();
        for (int i = 1; i < cellCount; i++) {
            ResourceVO vo = new ResourceVO();
            XSSFCell cell = header.getCell(i);
            vo.setLanguage(getStringCellValue(cell));
            resourceVOs.add(vo);
        }
        return resourceVOs;
    }

    private static String getResourceFilePath(String sheetName, String language, String targetDirPath) {
        if (StringUtils.isBlank(language)) {
            return String.format("%s/%s.properties", targetDirPath, sheetName);
        } else {
            return String.format("%s/%s_%s.properties", targetDirPath, sheetName, language);
        }
    }

    private static String getStringCellValue(XSSFCell cell) {
        return getStringCellValue(null, cell);
    }

    private static String getStringCellValue(String key, XSSFCell cell) {
        return ExcelUtils.getStringCellValue(key, cell);
    }

    @ToString(exclude = "dataMap")
    public static class ResourceVO {

        private static final long serialVersionUID = 5311392181701516259L;

        private String language;

        private SortedMap<String, String> dataMap = new TreeMap<String, String>();

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public SortedMap<String, String> getDataMap() {
            return dataMap;
        }

        public void setDataMap(SortedMap<String, String> dataMap) {
            this.dataMap = dataMap;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((language == null) ? 0 : language.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ResourceVO other = (ResourceVO) obj;
            if (language == null) {
                if (other.language != null)
                    return false;
            } else if (!language.equals(other.language))
                return false;
            return true;
        }
    }
}
