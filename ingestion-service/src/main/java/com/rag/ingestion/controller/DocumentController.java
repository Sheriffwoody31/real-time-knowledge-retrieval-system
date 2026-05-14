package com.rag.ingestion.controller;

import com.rag.ingestion.model.DocumentEvent;
import com.rag.ingestion.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentEvent> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "source", required = false) String source
    ) {
        String originalName = file != null ? file.getOriginalFilename() : null;
        log.info(
                "Received document upload request originalFileName={} size={} bytes sourcePresent={}",
                originalName,
                file != null ? file.getSize() : 0,
                source != null && !source.isBlank()
        );

        DocumentEvent event = documentService.upload(file, source);

        log.info(
                "Document upload completed documentId={} storedPath={}",
                event.getDocumentId(),
                event.getFilePath()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
}
