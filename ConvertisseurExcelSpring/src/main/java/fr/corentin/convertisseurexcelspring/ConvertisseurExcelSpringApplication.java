
package com.converter;

import com.converter.model.ConversionResult;
import com.converter.service.ExcelToJsonConverterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@SpringBootApplication
public class ExcelToJsonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelToJsonApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ExcelToJsonConverterService converterService) {
        return args -> {
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║      CONVERTISSEUR EXCEL vers JSON - v1.0.0             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println();

            if (args.length < 2) {
                printUsage();
                System.exit(1);
            }

            String inputPath = args[0];
            String outputPath = args[1];

            Instant start = Instant.now();

            try {
                // Validation des paramètres
                validateInputFile(inputPath);
                validateOutputPath(outputPath);

                System.out.println("  Fichier d'entrée  : " + inputPath);
                System.out.println("  Fichier de sortie : " + outputPath);
                System.out.println();
                System.out.println("  Traitement en cours...");
                System.out.println();

                // Conversion
                ConversionResult result = converterService.convert(inputPath, outputPath);

                Instant end = Instant.now();
                Duration duration = Duration.between(start, end);

                // Affichage des résultats
                printResults(result, duration);

                System.exit(0);

            } catch (Exception e) {
                System.err.println();
                System.err.println("  ERREUR : " + e.getMessage());
                log.error("Erreur lors de la conversion", e);
                System.exit(1);
            }
        };
    }

    private void validateInputFile(String path) throws Exception {
        File file = new File(path);

        if (!file.exists()) {
            throw new Exception("Le fichier d'entrée n'existe pas : " + path);
        }

        if (!file.isFile()) {
            throw new Exception("Le chemin d'entrée n'est pas un fichier : " + path);
        }

        if (!file.canRead()) {
            throw new Exception("Impossible de lire le fichier : " + path);
        }

        if (!path.toLowerCase().endsWith(".xlsx")) {
            throw new Exception("Le fichier doit avoir l'extension .xlsx");
        }
    }

    private void validateOutputPath(String path) throws Exception {
        if (!path.toLowerCase().endsWith(".json")) {
            throw new Exception("Le fichier de sortie doit avoir l'extension .json");
        }

        File outputFile = new File(path);
        File parentDir = outputFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new Exception("Impossible de créer le répertoire de sortie : " + parentDir);
            }
        }
    }

    private void printResults(ConversionResult result, Duration duration) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              RÉSUMÉ DU TRAITEMENT                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Conversion réussie !");
        System.out.println();
        System.out.println("  STATISTIQUES :");
        System.out.println("  • Nombre de feuilles   : " + result.getSheetsProcessed());
        System.out.println("  • Total de lignes      : " + result.getTotalRows());
        System.out.println("  • Lignes converties    : " + result.getRowsConverted());
        System.out.println("  • Lignes vides ignorées: " + result.getEmptyRowsSkipped());
        System.out.println("  • Colonnes détectées   : " + result.getColumnsDetected());
        System.out.println();
        System.out.println("    TEMPS D'EXÉCUTION :");
        System.out.println("  • Durée totale         : " + formatDuration(duration));
        System.out.println();
        System.out.println("FICHIER DE SORTIE :");
        System.out.println("  • Chemin               : " + result.getOutputPath());
        System.out.println("  • Taille               : " + formatFileSize(result.getOutputFileSize()));
        System.out.println();

        if (result.getWarnings() != null && !result.getWarnings().isEmpty()) {
            System.out.println("⚠️  AVERTISSEMENTS :");
            result.getWarnings().forEach(w -> System.out.println("  • " + w));
            System.out.println();
        }
    }

    private String formatDuration(Duration duration) {
        long millis = duration.toMillis();
        if (millis < 1000) {
            return millis + " ms";
        } else {
            return String.format("%.2f s", millis / 1000.0);
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    private void printUsage() {
        System.err.println("Usage incorrect !");
        System.err.println();
        System.err.println("Usage:");
        System.err.println("  java -jar excel-converter.jar <fichier_excel.xlsx> <fichier_sortie.json>");
        System.err.println();
        System.err.println("Exemple:");
        System.err.println("  java -jar excel-converter.jar data/input.xlsx output/result.json");
    }
}