package com.rsupport.webviewer.core.util;


import org.junit.Ignore;
import org.junit.Test;

public class ExcelToResourcesTest {

    @Ignore
    @Test
    public void testGenerateAll() {
        ExcelToResources.generate("src/test/resources/message.xlsx", "src/main/resources/messages");
    }
}
