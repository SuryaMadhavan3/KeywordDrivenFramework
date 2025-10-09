package keyword.framework.KeywordDrivenFramework;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KeywordDataReader extends ExcelDataReader {

    public List<Map<String, String>> getKeywordSteps(String moduleName) throws IOException {
        return readSheet(FrameworkPaths.KEYWORD_PATH, moduleName);
    }

    // ✅ Reads all test data rows from a given sheet
    public List<Map<String, String>> getTestDataRows(String sheetName) throws IOException {
        List<Map<String, String>> allData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FrameworkPaths.TESTDATA_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("⚠️ Sheet " + sheetName + " not found in TestData Excel.");
                return allData;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                System.out.println("⚠️ No header row in sheet " + sheetName);
                return allData;
            }

            int lastCellNum = headerRow.getLastCellNum();
            int lastRowNum = sheet.getLastRowNum();

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < lastCellNum; j++) {
                    Cell headerCell = headerRow.getCell(j);
                    if (headerCell == null) continue;

                    String key = headerCell.getStringCellValue().trim();
                    String value = "";

                    Cell dataCell = row.getCell(j);
                    if (dataCell != null) {
                        switch (dataCell.getCellType()) {
                            case STRING:  value = dataCell.getStringCellValue(); break;
                            case NUMERIC: value = String.valueOf((long) dataCell.getNumericCellValue()); break;
                            case BOOLEAN: value = String.valueOf(dataCell.getBooleanCellValue()); break;
                            default: value = "";
                        }
                    }

                    rowData.put(key, value.trim());
                }

                if (!rowData.isEmpty())
                    allData.add(rowData);
            }
        }

        System.out.println("✅ Loaded " + allData.size() + " rows from Test Data sheet: " + sheetName);
        return allData;
    }
}
