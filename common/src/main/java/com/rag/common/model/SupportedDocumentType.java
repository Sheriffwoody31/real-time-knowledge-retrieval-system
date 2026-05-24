package com.rag.common.model;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public enum SupportedDocumentType {
    PDF("pdf"),
    CSV("csv"),
    TEXT("txt"),
    TEXT_FILE("text"),
    PARQUET("parquet"),
    XLS("xls"),
    XLSX("xlsx"),
    XLSV("xlsv");

    private static final Set<String> SUPPORTED_EXTENSIONS =
            Set.of("pdf", "csv", "txt", "text", "parquet", "xls", "xlsx", "xlsv");

    private final String extension;

    SupportedDocumentType(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }

    public static boolean isSupported(String extension) {
        return normalize(extension).map(SUPPORTED_EXTENSIONS::contains).orElse(false);
    }

    public static Optional<SupportedDocumentType> fromExtension(String extension) {
        return normalize(extension)
                .flatMap(normalized -> {
                    for (SupportedDocumentType type : values()) {
                        if (type.extension.equals(normalized)) {
                            return Optional.of(type);
                        }
                    }
                    return Optional.empty();
                });
    }

    public static Set<String> supportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    private static Optional<String> normalize(String extension) {
        if (extension == null || extension.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(extension.trim().toLowerCase(Locale.ROOT));
    }
}
