package com.rsupport.webviewer.core.util;

import org.junit.Ignore;
import org.junit.Test;

public class ExcelToXmlDataSetTest {

    /**
     * sample-data.xml 파일 생성 - 생성 후 @Igonore 해줘야 한다. 빌드시 제외하기 위해 평소에는 @Ignore할것. -
     * default-data는 sample-data.xlsx에서 cell color가 yellow인것만 생성한다.
     */
    @Ignore
    @Test
    public void testGenerateDataSetXml() throws Exception {
        ExcelToXmlDataSet.generateDataSetXml("sample-data");
        ExcelToXmlDataSet.generateDataSetXmlIgnoreTestData("sample-data");
    }
}
