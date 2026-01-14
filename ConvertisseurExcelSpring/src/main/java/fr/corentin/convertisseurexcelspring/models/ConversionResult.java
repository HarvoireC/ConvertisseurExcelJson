package fr.corentin.convertisseurexcelspring.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionResult {
    private boolean success;
    private String outputPath;
    private long outputFileSize;
    private int sheetsProcessed;
    private int totalRows;
    private int rowsConverted;
    private int emptyRowsSkipped;
    private int columnsDetected;
    private List<String> warnings;

    public void addWarning(String warning) {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        warnings.add(warning);
    }
}