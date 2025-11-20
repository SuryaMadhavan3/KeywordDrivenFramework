package keyword.framework.KeywordDrivenFramework;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

    private static final String EXCEL_PATH = FrameworkPaths.TESTDATA_PATH;
    private static final String SHEET_NAME = "Purchase";

    public static synchronized void updateStatus(String tcId, double actualTotal, String status) {

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(EXCEL_PATH);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(SHEET_NAME);

            if (sheet == null) {
                workbook.close();
                throw new RuntimeException("❌ Sheet 'Purchase' not found!");
            }

            int tcIdCol = -1;
            int actualCol = -1;
            int statusCol = -1;

            Row header = sheet.getRow(0);

            // 🔍 Find required columns index dynamically
            for (int i = 0; i < header.getLastCellNum(); i++) {
                String column = header.getCell(i).getStringCellValue().trim().toLowerCase();

                if (column.equals("testcaseid")) tcIdCol = i;
                if (column.equals("actual total amount")) actualCol = i;
                if (column.equals("status")) statusCol = i;
            }

            if (tcIdCol == -1 || actualCol == -1 || statusCol == -1) {
                workbook.close();
                throw new RuntimeException("❌ Required columns missing in sheet!");
            }

            // 🔍 Find row by TestCaseID
            boolean found = false;
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {

                Row row = sheet.getRow(r);
                if (row == null) continue;

                Cell tcCell = row.getCell(tcIdCol);
                if (tcCell == null) continue;

                String value = tcCell.getStringCellValue().trim();
                if (value.equalsIgnoreCase(tcId)) {

                    // ✍ Write Actual total amount
                    Cell actualCell = row.getCell(actualCol);
                    if (actualCell == null)
                        actualCell = row.createCell(actualCol);

                    actualCell.setCellValue(actualTotal);

                    // ✍ Write Status
                    Cell statusCell = row.getCell(statusCol);
                    if (statusCell == null)
                        statusCell = row.createCell(statusCol);

                    statusCell.setCellValue(status);

                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("⚠️ TCID not found in Excel: " + tcId);
            }

            fis.close(); // close input stream

            fos = new FileOutputStream(EXCEL_PATH);
            workbook.write(fos);
            workbook.close();

            System.out.println("📌 Excel Updated → TC: " + tcId +
                    " | Actual=" + actualTotal + " | Status=" + status);

        } catch (Exception e) {
            System.out.println("❌ Excel update failed: " + e.getMessage());

        } finally {
            try { if (fis != null) fis.close(); } catch (IOException ignored) {}
            try { if (fos != null) fos.close(); } catch (IOException ignored) {}
        }
    }
}
