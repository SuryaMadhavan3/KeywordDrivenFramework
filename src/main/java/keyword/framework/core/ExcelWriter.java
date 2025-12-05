package keyword.framework.core;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelWriter {

    
    /*Generic method to update any Excel sheet.
     
      @param excelPath  Full path of Excel file
      @param sheetName  Sheet to update
      @param identifierColumn  Column name used to find the row (ex: "TestCaseID")
      @param identifierValue   Value to locate correct row (ex: "TC_01")
      @param valuesToUpdate    Map<ColumnName, NewValue> */
	
    public static void updateExcelRow(String excelPath, String sheetName, String identifierColumn, String identifierValue, Map<String, Object> valuesToUpdate) {

        try (FileInputStream fis = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("❌ Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("❌ Header row missing in sheet: " + sheetName);
            }

            // Identify columns
            Map<String, Integer> columnIndexMap = new LinkedHashMap<>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                Cell headerCell = headerRow.getCell(c);
                if (headerCell != null) {
                    columnIndexMap.put(headerCell.getStringCellValue().trim(), c);
                }
            }

            // Check if identifier column exists
            if (!columnIndexMap.containsKey(identifierColumn)) {
                throw new RuntimeException("❌ Identifier column '" + identifierColumn + "' not found.");
            }

            int idColIndex = columnIndexMap.get(identifierColumn);

            // Locate correct row
            Row targetRow = null;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Cell cell = row.getCell(idColIndex);
                if (cell == null) continue;

                String cellValue = new DataFormatter().formatCellValue(cell).trim();
                if (cellValue.equalsIgnoreCase(identifierValue)) {
                    targetRow = row;
                    break;
                }
            }

            if (targetRow == null) {
                throw new RuntimeException("❌ No row found with " +
                        identifierColumn + " = " + identifierValue);
            }

            // Update columns
            for (Map.Entry<String, Object> entry : valuesToUpdate.entrySet()) {
                String colName = entry.getKey();
                Object val = entry.getValue();

                if (!columnIndexMap.containsKey(colName)) {
                    throw new RuntimeException("❌ Column '" + colName + "' not found in sheet.");
                }

                int colIndex = columnIndexMap.get(colName);
                Cell cell = targetRow.getCell(colIndex);
                if (cell == null) cell = targetRow.createCell(colIndex);

                if (val instanceof Number) {
                    cell.setCellValue(((Number) val).doubleValue());
                } else {
                    cell.setCellValue(val.toString());
                }
            }

            // Write back to Excel
            try (FileOutputStream fos = new FileOutputStream(excelPath)) {
                workbook.write(fos);
            }

            System.out.println("✅ Excel Updated Successfully → " + identifierValue);

        } catch (Exception e) {
            System.out.println("❌ Excel update failed: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------
     * Convenience method for status updates (simple use case)
     * ------------------------------------------------------------------ */
    public static void updateStatus(String excelPath, String sheetName, String testCaseID, double actualValue, String status) {

        Map<String, Object> updateMap = new LinkedHashMap<>();
        updateMap.put("Actual", actualValue);
        updateMap.put("Status", status);

        updateExcelRow(excelPath, sheetName, "TestCaseID", testCaseID, updateMap);
    }
}













