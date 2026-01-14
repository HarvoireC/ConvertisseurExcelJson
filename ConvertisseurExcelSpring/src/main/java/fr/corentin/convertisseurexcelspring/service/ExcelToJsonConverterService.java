package fr.corentin.convertisseurexcelspring.service;

import com.converter.model.ConversionResult;
import com.converter.model.ExcelData;
import com.converter.model.ExcelSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelToJsonConverterService {

    private final ExcelReaderService excelReaderService;

    public ConversionResult convert(String inputPath, String outputPath) throws IOException {
        log.info("Début de la conversion : {} -> {}", inputPath, outputPath);

        ConversionResult result = ConversionResult.builder()
                .success(false)
                .outputPath(outputPath)
                .build();

        try {
            // Étape 1 : Lecture du fichier Excel
            ExcelData excelData = excelReaderService.readExcelFile(inputPath);

            // Étape 2 : Calcul des statistiques
            int totalRows = 0;
            int rowsConverted = 0;
            int emptyRowsSkipped = 0;
            int maxColumns = 0;

            for (ExcelSheet sheet : excelData.getSheets()) {
                totalRows += sheet.getRowCount();
                rowsConverted += sheet.getRows().size();

                // Compter les lignes vides ignorées
                int potentialRows = sheet.getRowCount();
                int actualRows = sheet.getRows().size();
                emptyRowsSkipped += (potentialRows - actualRows);

                if (sheet.getColumnCount() > maxColumns) {
                    maxColumns = sheet.getColumnCount();
                }
            }

            // Étape 3 : Préparation de la structure JSON
            Map<String, Object> jsonOutput = new HashMap<>();
            jsonOutput.put("fileName", excelData.getFileName());
            jsonOutput.put("totalSheets", excelData.getTotalSheets());
            jsonOutput.put("totalRows", rowsConverted);
            jsonOutput.put("sheets", excelData.getSheets());

            // Étape 4 : Écriture du fichier JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            File outputFile = new File(outputPath);
            objectMapper.writeValue(outputFile, jsonOutput);

            log.info("Fichier JSON créé avec succès : {}", outputPath);

            // Étape 5 : Construction du résultat
            result.setSuccess(true);
            result.setSheetsProcessed(excelData.getTotalSheets());
            result.setTotalRows(totalRows);
            result.setRowsConverted(rowsConverted);
            result.setEmptyRowsSkipped(emptyRowsSkipped);
            result.setColumnsDetected(maxColumns);
            result.setOutputFileSize(outputFile.length());

            // Avertissements si nécessaire
            if (emptyRowsSkipped > 0) {
                result.addWarning(emptyRowsSkipped + " ligne(s) vide(s) ont été ignorées");
            }

            if (excelData.getSheets().stream().anyMatch(s -> s.getRowCount() == 0)) {
                long emptySheets = excelData.getSheets().stream()
                        .filter(s -> s.getRowCount() == 0)
                        .count();
                result.addWarning(emptySheets + " feuille(s) vide(s) détectée(s)");
            }

            return result;

        } catch (IOException e) {
            log.error("Erreur lors de la conversion", e);
            result.setSuccess(false);
            throw e;
        }
    }
}