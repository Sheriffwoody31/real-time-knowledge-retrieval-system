package com.rag.processing.service;

import com.rag.common.model.SupportedDocumentType;
import com.rag.processing.exception.TextExtractionException;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

@Service
public class TextExtractionService {

    private static final Logger log = LoggerFactory.getLogger(TextExtractionService.class);

    public String extract(java.nio.file.Path filePath, String fileExtension) {
        String normalizedExtension = normalizeExtension(fileExtension);
        SupportedDocumentType type = SupportedDocumentType.fromExtension(normalizedExtension)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file extension: " + fileExtension));

        try {
            log.info("Starting text extraction path={} documentType={}", filePath, type);
            String extractedText = switch (type) {
                case PDF -> extractPdf(filePath);
                case CSV, TEXT, TEXT_FILE -> Files.readString(filePath);
                case XLS, XLSX -> extractSpreadsheet(filePath);
                case PARQUET -> extractParquet(filePath);
            };
            log.info(
                    "Completed text extraction path={} documentType={} characters={}",
                    filePath,
                    type,
                    extractedText.length()
            );
            return extractedText;
        } catch (IOException e) {
            log.error("Text extraction failed path={} documentType={}", filePath, type, e);
            throw new TextExtractionException("Failed to extract text from " + filePath, e);
        }
    }

    private String extractPdf(java.nio.file.Path filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractSpreadsheet(java.nio.file.Path filePath) throws IOException {
        DataFormatter formatter = new DataFormatter();
        StringBuilder text = new StringBuilder();

        try (Workbook workbook = WorkbookFactory.create(filePath.toFile())) {
            for (Sheet sheet : workbook) {
                text.append("# Sheet: ").append(sheet.getSheetName()).append(System.lineSeparator());
                for (Row row : sheet) {
                    String rowText = extractSpreadsheetRow(row, formatter);
                    if (!rowText.isBlank()) {
                        text.append(rowText).append(System.lineSeparator());
                    }
                }
            }
        }

        return text.toString();
    }

    private String extractSpreadsheetRow(Row row, DataFormatter formatter) {
        StringBuilder rowText = new StringBuilder();
        for (Cell cell : row) {
            if (rowText.length() > 0) {
                rowText.append('\t');
            }
            rowText.append(formatter.formatCellValue(cell));
        }
        return rowText.toString();
    }

    private String extractParquet(java.nio.file.Path filePath) throws IOException {
        StringBuilder text = new StringBuilder();
        Path parquetPath = new Path(filePath.toUri());

        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                .<GenericRecord>builder(parquetPath)
                .withConf(new Configuration())
                .build()) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                text.append(record)
                        .append(System.lineSeparator());
            }
        }

        return text.toString();
    }

    private String normalizeExtension(String fileExtension) {
        if (fileExtension == null || fileExtension.isBlank()) {
            return "";
        }
        return fileExtension.trim().toLowerCase(Locale.ROOT);
    }
}
