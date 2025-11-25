package keyword.framework.KeywordDrivenFramework;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

    private static final String EXCEL_PATH = FrameworkPaths.TESTDATA_PATH;
    private static final String SHEET_NAME = "Purchase";

    public static synchronized void updateStatus(String tcId, Double actualTotal, String status) {

        if (tcId == null || (actualTotal == null && status == null)) {
            System.out.println("❌ Nothing to update. Provide at least a value for Status or Actual Total.");
            return;
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(EXCEL_PATH);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(SHEET_NAME);

            if (sheet == null) {
                workbook.close();
                throw new RuntimeException("❌ Sheet '" + SHEET_NAME + "' not found!");
            }

            int tcIdCol = -1;
            int actualCol = -1;
            int statusCol = -1;

            Row header = sheet.getRow(0);

            // 🔍 Find column indexes dynamically
            for (int i = 0; i < header.getLastCellNum(); i++) {
                String column = header.getCell(i).getStringCellValue().trim().toLowerCase();

                if (column.equals("testcaseid")) tcIdCol = i;
                if (column.equals("actual total amount")) actualCol = i;
                if (column.equals("status")) statusCol = i;
            }

            if (tcIdCol == -1) {
                workbook.close();
                throw new RuntimeException("❌ TestCaseID column missing in sheet!");
            }

            boolean found = false;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Cell tcCell = row.getCell(tcIdCol);
                if (tcCell == null) continue;

                String value = tcCell.getStringCellValue().trim();
                if (value.equalsIgnoreCase(tcId)) {

                    // ✍ Update Actual Total if provided
                    if (actualTotal != null && actualCol != -1) {
                        Cell actualCell = row.getCell(actualCol);
                        if (actualCell == null)
                            actualCell = row.createCell(actualCol);
                        actualCell.setCellValue(actualTotal);
                    }

                    // ✍ Update Status if provided
                    if (status != null && statusCol != -1) {
                        Cell statusCell = row.getCell(statusCol);
                        if (statusCell == null)
                            statusCell = row.createCell(statusCol);
                        statusCell.setCellValue(status);
                    }

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
                    (actualTotal != null ? " | Actual=" + actualTotal : "") +
                    (status != null ? " | Status=" + status : ""));

        } catch (Exception e) {
            System.out.println("❌ Excel update failed: " + e.getMessage());

        } finally {
            try { if (fis != null) fis.close(); } catch (IOException ignored) {}
            try { if (fos != null) fos.close(); } catch (IOException ignored) {}
        }
    }

    // ✅ Convenience overload: Update only Status
    public static synchronized void updateStatus(String tcId, String status) {
        updateStatus(tcId, null, status);
    }

    // ✅ Convenience overload: Update only Actual Total
    public static synchronized void updateStatus(String tcId, Double actualTotal) {
        updateStatus(tcId, actualTotal, null);
    }
}
