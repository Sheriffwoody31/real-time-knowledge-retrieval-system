package com.rag.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IngestionApplication {

    private static final Logger log = LoggerFactory.getLogger(IngestionApplication.class);

    public static void main(String[] args) {
        log.info("Starting ingestion-service");
        SpringApplication.run(IngestionApplication.class, args);
    }
}