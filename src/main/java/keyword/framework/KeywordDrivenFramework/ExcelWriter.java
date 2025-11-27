package keyword.framework.KeywordDrivenFramework;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelWriter {

    public static void updateStatus(String tcId, double actualTotal, String status) {
        String excelPath = FrameworkPaths.TESTDATA_PATH;
        String sheetName = "Purchase";

        try (FileInputStream fis = new FileInputStream(excelPath);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row header = sheet.getRow(0);
            if (header == null) {
                throw new RuntimeException("Header row missing in sheet: " + sheetName);
            }

            int tcCol = -1, actualCol = -1, statusCol = -1;

            for (int c = 0; c < header.getLastCellNum(); c++) {
                Cell cell = header.getCell(c);
                if (cell == null) continue;
                String name = cell.getStringCellValue().trim();

                if (name.equalsIgnoreCase("TestCaseID")) {
                    tcCol = c;
                } else if (name.equalsIgnoreCase("Actual total amount")) {
                    actualCol = c;
                } else if (name.equalsIgnoreCase("Status")) {
                    statusCol = c;
                }
            }

            if (tcCol == -1 || actualCol == -1 || statusCol == -1) {
                throw new RuntimeException("❌ Required columns missing in sheet!");
            }

            // Find row with matching TestCaseID
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Cell tcCell = row.getCell(tcCol);
                if (tcCell == null) continue;

                String id = tcCell.getStringCellValue().trim();
                if (!tcId.equalsIgnoreCase(id)) continue;

                Cell actCell = row.getCell(actualCol);
                if (actCell == null) actCell = row.createCell(actualCol);
                actCell.setCellValue(actualTotal);

                Cell stCell = row.getCell(statusCol);
                if (stCell == null) stCell = row.createCell(statusCol);
                stCell.setCellValue(status);

                break;
            }

            try (FileOutputStream fos = new FileOutputStream(excelPath)) {
                wb.write(fos);
            }

            System.out.println("✅ Excel updated for " + tcId +
                    " | ActualTotal=" + actualTotal + " | Status=" + status);

        } catch (Exception e) {
            System.out.println("❌ Excel update failed: " + e.getMessage());
        }
    }    
}
