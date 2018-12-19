package com.rsupport.webviewer.viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.rsupport")
public class ViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViewerApplication.class, args);
    }

}

