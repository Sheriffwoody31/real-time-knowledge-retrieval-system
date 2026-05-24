package com.rag.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextExtractionServiceTest {

    private final TextExtractionService textExtractionService = new TextExtractionService();

    @TempDir
    Path tempDir;

    @Test
    void extractsTextFiles() throws Exception {
        Path textFile = tempDir.resolve("notes.txt");
        Files.writeString(textFile, "alpha beta gamma");

        String extracted = textExtractionService.extract(textFile, "txt");

        assertThat(extracted).isEqualTo("alpha beta gamma");
    }

    @Test
    void extractsCsvFilesAsText() throws Exception {
        Path csvFile = tempDir.resolve("records.csv");
        Files.writeString(csvFile, "id,name\n1,Ada");

        String extracted = textExtractionService.extract(csvFile, "csv");

        assertThat(extracted).contains("id,name").contains("1,Ada");
    }

    @Test
    void rejectsUnsupportedExtensions() {
        Path unknownFile = tempDir.resolve("image.png");

        assertThatThrownBy(() -> textExtractionService.extract(unknownFile, "png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported file extension");
    }
}
