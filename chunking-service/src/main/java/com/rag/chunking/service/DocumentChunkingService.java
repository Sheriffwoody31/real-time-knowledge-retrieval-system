package com.rag.chunking.service;

import com.rag.chunking.config.ChunkingProperties;
import com.rag.chunking.exception.DocumentChunkingException;
import com.rag.common.model.DocumentTextExtractedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DocumentChunkingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentChunkingService.class);

    private final Path outputRoot;
    private final String defaultStrategy;

    public DocumentChunkingService(ChunkingProperties chunkingProperties) {
        this.outputRoot = Path.of(chunkingProperties.getOutputRoot()).toAbsolutePath().normalize();
        this.defaultStrategy = chunkingProperties.getDefaultStrategy();
        log.info("Chunk storage root configured path={} defaultStrategy={}", outputRoot, defaultStrategy);
    }

    public void process(DocumentTextExtractedEvent event) {
        validateEvent(event);

        Path extractedTextPath = Path.of(event.getExtractedTextPath()).toAbsolutePath().normalize();
        log.info(
                "Chunking service skeleton accepted documentId={} extractedTextPath={} strategy={}",
                event.getDocumentId(),
                extractedTextPath,
                defaultStrategy
        );
    }

    private void validateEvent(DocumentTextExtractedEvent event) {
        if (event == null) {
            throw new DocumentChunkingException("Document text extracted event must not be null.");
        }
        if (!StringUtils.hasText(event.getDocumentId())) {
            throw new DocumentChunkingException("Document text extracted event documentId must not be blank.");
        }
        if (!StringUtils.hasText(event.getExtractedTextPath())) {
            throw new DocumentChunkingException("Document text extracted event extractedTextPath must not be blank.");
        }

        Path extractedTextPath = Path.of(event.getExtractedTextPath()).toAbsolutePath().normalize();
        if (!Files.isRegularFile(extractedTextPath)) {
            throw new DocumentChunkingException("Extracted text file does not exist: " + extractedTextPath);
        }
    }
}
