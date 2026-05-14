package com.rag.ingestion.service;

import com.rag.ingestion.exception.DocumentStorageException;
import com.rag.ingestion.model.DocumentEvent;
import com.rag.ingestion.producer.DocumentEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    /**
     * File extension (lowercase, no dot) → subfolder under the configured storage root.
     * Supported: pdf, csv, txt, parquet, xlsx (covers Excel; "xlsv" in requirements read as xlsx).
     */
    private static final Map<String, String> EXTENSION_TO_SUBROOT = Map.ofEntries(
            Map.entry("pdf", "pdf"),
            Map.entry("csv", "csv"),
            Map.entry("txt", "txt"),
            Map.entry("parquet", "parquet"),
            Map.entry("xlsx", "xlsx")
    );

    private static final String SUPPORTED_EXTENSIONS_MESSAGE =
            "Supported file extensions: pdf, csv, txt, parquet, xlsx.";

    private final DocumentEventProducer documentEventProducer;
    private final Path storageRoot;

    @Autowired
    public DocumentService(
            DocumentEventProducer documentEventProducer,
            @Value("${ingestion.document.storage-root}") String storageRoot
    ) {
        this.documentEventProducer = documentEventProducer;
        if (!StringUtils.hasText(storageRoot)) {
            throw new IllegalArgumentException("ingestion.document.storage-root must not be blank.");
        }
        this.storageRoot = Path.of(storageRoot.trim()).toAbsolutePath().normalize();
        log.info("Document storage root configured path={}", this.storageRoot);
    }

    public DocumentEvent upload(MultipartFile file, String source) {
        if (file == null || file.isEmpty()) {
            log.warn(
                    "Upload rejected: fileNull={} empty={} sizeBytes={} originalFilename={}. "
                            + "If using curl, use -F \"file=@/absolute/path.pdf\" (no space after @) and normal ASCII quotes.",
                    file == null,
                    file != null && file.isEmpty(),
                    file != null ? file.getSize() : null,
                    file != null ? file.getOriginalFilename() : null
            );
            throw new IllegalArgumentException("Uploaded file must not be empty.");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "uploaded-file");
        String typeSubRoot = resolveTypeSubRoot(originalFileName);
        if (typeSubRoot == null) {
            log.warn("Upload rejected: unsupported file name extension name={}", originalFileName);
            throw new IllegalArgumentException("Unsupported file type. " + SUPPORTED_EXTENSIONS_MESSAGE);
        }

        String documentId = UUID.randomUUID().toString();
        Instant uploadedAt = Instant.now();

        log.debug(
                "Processing upload documentId={} originalFileName={} typeSubRoot={} contentType={}",
                documentId,
                originalFileName,
                typeSubRoot,
                file.getContentType()
        );

        Path typeDir = storageRoot.resolve(typeSubRoot);
        try {
            Files.createDirectories(typeDir);
        } catch (IOException e) {
            log.error("Failed to create type storage directory path={}", typeDir.toAbsolutePath(), e);
            throw new DocumentStorageException("Could not create storage directory.", e);
        }

        String storedFileName = documentId + "_" + originalFileName;
        Path destination = typeDir.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(
                    "Failed to copy uploaded file documentId={} destination={}",
                    documentId,
                    destination.toAbsolutePath(),
                    e
            );
            throw new DocumentStorageException("Failed to persist uploaded file.", e);
        }

        log.info(
                "File stored documentId={} typeSubRoot={} path={}",
                documentId,
                typeSubRoot,
                destination.toAbsolutePath()
        );

        String fileType = resolveFileType(file);
        DocumentEvent event = new DocumentEvent(
                documentId,
                destination.toAbsolutePath().toString(),
                fileType,
                uploadedAt,
                normalizeSource(source)
        );

        documentEventProducer.publish(event);
        return event;
    }

    private String resolveFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (StringUtils.hasText(contentType)) {
            return contentType;
        }
        log.debug("No Content-Type on upload; defaulting to application/octet-stream");
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private String normalizeSource(String source) {
        return StringUtils.hasText(source) ? source.trim() : null;
    }

    /**
     * Maps the original filename extension to a subfolder name (e.g. {@code report.pdf} → {@code pdf}).
     *
     * @return subfolder segment, or {@code null} if extension is missing or not supported
     */
    private String resolveTypeSubRoot(String originalFileName) {
        String ext = extractExtension(originalFileName);
        if (!StringUtils.hasText(ext)) {
            return null;
        }
        return EXTENSION_TO_SUBROOT.get(ext.toLowerCase());
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }
        return filename.substring(dot + 1);
    }
}
