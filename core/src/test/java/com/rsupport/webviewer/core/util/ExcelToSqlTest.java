package com.rsupport.webviewer.core.util;


import org.junit.Ignore;
import org.junit.Test;


public class ExcelToSqlTest {

    @Ignore
    @Test
    public void testGenerateSql() throws Exception {
        ExcelToSql.generateSql("sample-data");
        ExcelToSql.generateSqlIgnoreTestData("sample-data");
    }
}
