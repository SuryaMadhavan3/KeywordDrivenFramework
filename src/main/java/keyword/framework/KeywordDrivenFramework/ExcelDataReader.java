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

		try (FileInputStream file = new FileInputStream(excelPath); XSSFWorkbook workbook = new XSSFWorkbook(file)) {

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				sheetNames.add(workbook.getSheetName(i));
			}
		}

		return sheetNames;
	}

	public List<Map<String, String>> readSheet(String excelPath, String sheetName) throws IOException {
	    List<Map<String, String>> data = new ArrayList<>();

	    try (FileInputStream file = new FileInputStream(excelPath);
	         XSSFWorkbook workbook = new XSSFWorkbook(file)) {

	        Sheet sheet = workbook.getSheet(sheetName);
	        if (sheet == null)
	            throw new IOException("❌ Sheet not found: " + sheetName);

	        Row headerRow = sheet.getRow(0);
	        if (headerRow == null)
	            return data;

	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            if (row == null) continue;

	            boolean isRowEmpty = true;
	            Map<String, String> rowData = new LinkedHashMap<>();

	            for (int j = 0; j < headerRow.getLastCellNum(); j++) {
	                Cell headerCell = headerRow.getCell(j);
	                if (headerCell == null) continue;

	                String header = headerCell.getStringCellValue().trim();
	                if (header.isEmpty()) continue;

	                String cellValue = new DataFormatter().formatCellValue(row.getCell(j)).trim();

	                if (!cellValue.isEmpty()) {
	                    isRowEmpty = false;  // Found a non-empty cell
	                }

	                rowData.put(header, cellValue);
	            }

	            // Only add the row if it has at least one non-empty cell
	            if (!isRowEmpty) {
	                data.add(rowData);
	            }
	        }
	    }

	    System.out.println("✅ Loaded " + data.size() + " rows from sheet: " + sheetName);
	    return data;
	}

	// ✅ Read all sheets from a workbook (sheet-by-sheet)
	public Map<String, List<Map<String, String>>> readAllSheets(String excelPath) throws IOException {
		Map<String, List<Map<String, String>>> allSheetData = new LinkedHashMap<>();

		// Step 1: Get all sheet names
		List<String> sheetNames = getSheetNames(excelPath);

		// Step 2: Read each sheet using readSheet()
		for (String sheetName : sheetNames) {
			List<Map<String, String>> sheetData = readSheet(excelPath, sheetName);
			allSheetData.put(sheetName, sheetData);
		}

		System.out.println("📘 Total Sheets Loaded: " + allSheetData.size());
		return allSheetData;
	}

public List<Map<String, String>> getKeywordSteps(String moduleName) throws IOException {
    return readSheet(FrameworkPaths.KEYWORD_PATH, moduleName);
}

public List<Map<String, String>> getTestDataRows(String sheetName) throws IOException {
    return readSheet(FrameworkPaths.TESTDATA_PATH, sheetName);
}

}