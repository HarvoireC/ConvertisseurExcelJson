package fr.corentin.convertisseurexcelspring.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelData {
    private List<ExcelSheet> sheets;
    private String fileName;
    private int totalSheets;
    private int totalRows;
}