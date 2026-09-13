package com.rag.chunking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChunkingApplication {

    private static final Logger log = LoggerFactory.getLogger(ChunkingApplication.class);

    public static void main(String[] args) {
        log.info("Starting chunking-service");
        SpringApplication.run(ChunkingApplication.class, args);
    }
}
