package com.rag.processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ProcessingApplication {

    private static final Logger log = LoggerFactory.getLogger(ProcessingApplication.class);

    public static void main(String[] args) {
        log.info("Starting processing-service");
        SpringApplication.run(ProcessingApplication.class, args);
    }
}
