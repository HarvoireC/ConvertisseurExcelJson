package fr.corentin.convertisseurexcelspring.service;

import com.converter.model.ExcelData;
import com.converter.model.ExcelSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class ExcelReaderService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public ExcelData readExcelFile(String filePath) throws IOException {
        log.info("Lecture du fichier Excel : {}", filePath);

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            List<ExcelSheet> sheets = new ArrayList<>();
            int totalRows = 0;

            int numberOfSheets = workbook.getNumberOfSheets();
            log.info("Nombre de feuilles détectées : {}", numberOfSheets);

            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                ExcelSheet excelSheet = processSheet(sheet);
                sheets.add(excelSheet);
                totalRows += excelSheet.getRowCount();
                log.info("Feuille '{}' traitée : {} lignes, {} colonnes",
                        excelSheet.getSheetName(),
                        excelSheet.getRowCount(),
                        excelSheet.getColumnCount());
            }

            return ExcelData.builder()
                    .sheets(sheets)
                    .fileName(new File(filePath).getName())
                    .totalSheets(numberOfSheets)
                    .totalRows(totalRows)
                    .build();
        }
    }

    private ExcelSheet processSheet(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        if (lastRowNum < firstRowNum) {
            log.warn("Feuille '{}' est vide", sheet.getSheetName());
            return ExcelSheet.builder()
                    .sheetName(sheet.getSheetName())
                    .headers(headers)
                    .rows(rows)
                    .rowCount(0)
                    .columnCount(0)
                    .build();
        }

        // Lecture des en-têtes (première ligne)
        Row headerRow = sheet.getRow(firstRowNum);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                String header = getCellValueAsString(cell);
                headers.add(header.isEmpty() ? "Column_" + cell.getColumnIndex() : header);
            }
        }

        int columnCount = headers.size();

        // Lecture des données (lignes suivantes)
        for (int rowIndex = firstRowNum + 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowEmpty(row)) {
                continue;
            }

            Map<String, Object> rowData = new LinkedHashMap<>();
            for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Object value = getCellValue(cell);
                rowData.put(headers.get(colIndex), value);
            }

            rows.add(rowData);
        }

        return ExcelSheet.builder()
                .sheetName(sheet.getSheetName())
                .headers(headers)
                .rows(rows)
                .rowCount(rows.size())
                .columnCount(columnCount)
                .build();
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (!value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return DATE_FORMAT.format(cell.getDateCellValue());
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // Si c'est un nombre entier, retourner un long
                    if (numericValue == Math.floor(numericValue)) {
                        return (long) numericValue;
                    }
                    return numericValue;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return evaluateFormula(cell);
            case BLANK:
                return null;
            default:
                return cell.toString();
        }
    }

    private Object evaluateFormula(Cell cell) {
        try {
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(cell);

            switch (cellValue.getCellType()) {
                case NUMERIC:
                    double numValue = cellValue.getNumberValue();
                    if (numValue == Math.floor(numValue)) {
                        return (long) numValue;
                    }
                    return numValue;
                case STRING:
                    return cellValue.getStringValue();
                case BOOLEAN:
                    return cellValue.getBooleanValue();
                default:
                    return null;
            }
        } catch (Exception e) {
            log.warn("Impossible d'évaluer la formule : {}", e.getMessage());
            return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return DATE_FORMAT.format(cell.getDateCellValue());
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
}