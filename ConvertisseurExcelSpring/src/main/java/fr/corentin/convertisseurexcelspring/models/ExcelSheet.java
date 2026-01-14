package fr.corentin.convertisseurexcelspring.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelSheet {
    private String sheetName;
    private List<String> headers;
    private List<Map<String, Object>> rows;
    private int rowCount;
    private int columnCount;
}