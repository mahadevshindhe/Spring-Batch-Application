package com.shindhe.app;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableBatchProcessing
@ComponentScan({"com.shindhe.config", "com.shindhe.service", "com.shindhe.listner",
        "com.shindhe.reader", "com.shindhe.writer", "com.shindhe.processor"
        , "com.shindhe.controller"})
@EnableAsync
public class SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }

}


//run application with different program arguments
// eg: run=one
// name=mahadev country=India