package com.rag.processing.service;

import com.rag.common.model.DocumentEvent;
import com.rag.common.model.DocumentTextExtractedEvent;
import com.rag.common.model.SupportedDocumentType;
import com.rag.processing.config.ProcessingStorageProperties;
import com.rag.processing.exception.DocumentProcessingException;
import com.rag.processing.producer.DocumentTextExtractedEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;

@Service
public class DocumentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentProcessingService.class);

    private final TextExtractionService textExtractionService;
    private final DocumentTextExtractedEventProducer extractedEventProducer;
    private final Path extractedTextRoot;

    public DocumentProcessingService(
            TextExtractionService textExtractionService,
            DocumentTextExtractedEventProducer extractedEventProducer,
            ProcessingStorageProperties storageProperties
    ) {
        this.textExtractionService = textExtractionService;
        this.extractedEventProducer = extractedEventProducer;
        this.extractedTextRoot = Path.of(storageProperties.getExtractedTextRoot()).toAbsolutePath().normalize();
        log.info("Extracted text storage root configured path={}", extractedTextRoot);
    }

    public DocumentTextExtractedEvent process(DocumentEvent event) {
        validateEvent(event);

        Path sourcePath = Path.of(event.getFilePath()).toAbsolutePath().normalize();
        String fileExtension = resolveFileExtension(event, sourcePath);

        log.info(
                "Processing document text extraction documentId={} path={} extension={}",
                event.getDocumentId(),
                sourcePath,
                fileExtension
        );

        String extractedText = textExtractionService.extract(sourcePath, fileExtension);
        Path extractedTextPath = writeExtractedText(event.getDocumentId(), fileExtension, extractedText);
        Instant extractedAt = Instant.now();

        DocumentTextExtractedEvent extractedEvent = new DocumentTextExtractedEvent(
                event.getDocumentId(),
                event.getOriginalFileName(),
                sourcePath.toString(),
                extractedTextPath.toString(),
                event.getFileType(),
                fileExtension,
                extractedText.length(),
                event.getUploadedAt(),
                extractedAt,
                event.getSource()
        );

        extractedEventProducer.publish(extractedEvent);
        log.info(
                "Document text extraction completed documentId={} extractedTextPath={} characters={}",
                event.getDocumentId(),
                extractedTextPath,
                extractedText.length()
        );
        return extractedEvent;
    }

    private void validateEvent(DocumentEvent event) {
        if (event == null) {
            throw new DocumentProcessingException("Document event must not be null.");
        }
        if (!StringUtils.hasText(event.getDocumentId())) {
            throw new DocumentProcessingException("Document event documentId must not be blank.");
        }
        if (!StringUtils.hasText(event.getFilePath())) {
            throw new DocumentProcessingException("Document event filePath must not be blank.");
        }
        Path sourcePath = Path.of(event.getFilePath()).toAbsolutePath().normalize();
        if (!Files.isRegularFile(sourcePath)) {
            throw new DocumentProcessingException("Document file does not exist: " + sourcePath);
        }
    }

    private String resolveFileExtension(DocumentEvent event, Path sourcePath) {
        String extension = event.getFileExtension();
        if (!StringUtils.hasText(extension)) {
            extension = extractExtension(sourcePath.getFileName().toString());
        }
        extension = extension.toLowerCase(Locale.ROOT);
        if (!SupportedDocumentType.isSupported(extension)) {
            throw new DocumentProcessingException("Unsupported document extension: " + extension);
        }
        return extension;
    }

    private Path writeExtractedText(String documentId, String fileExtension, String extractedText) {
        Path outputDirectory = extractedTextRoot.resolve(fileExtension);
        Path outputPath = outputDirectory.resolve(documentId + ".txt");

        try {
            Files.createDirectories(outputDirectory);
            Files.writeString(outputPath, extractedText, StandardCharsets.UTF_8);
            return outputPath.toAbsolutePath().normalize();
        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to write extracted text artifact.", e);
        }
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1);
    }
}
