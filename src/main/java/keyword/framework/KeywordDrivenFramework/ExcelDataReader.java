package keyword.framework.KeywordDrivenFramework;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelDataReader {

    // ✅ Get all sheet names in a workbook
    public List<String> getSheetNames(String excelPath) throws IOException {
        List<String> sheetNames = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
        }
        return sheetNames;
    }

    // ✅ Read one sheet into list of maps
    public List<Map<String, String>> readSheet(String excelPath, String sheetName) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) throw new IOException("❌ Sheet not found: " + sheetName);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return data;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell headerCell = headerRow.getCell(j);
                    if (headerCell == null) continue;

                    String header = headerCell.getStringCellValue().trim();
                    if (header.isEmpty()) continue;

                    String value = new DataFormatter().formatCellValue(row.getCell(j)).trim();
                    rowData.put(header, value);
                }
                data.add(rowData);
            }
        }

        System.out.println("✅ Loaded " + data.size() + " rows from sheet: " + sheetName);
        return data;
    }
}
