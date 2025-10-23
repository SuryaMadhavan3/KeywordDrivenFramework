package keyword.framework.KeywordDrivenFramework;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BrowserReader {
	
	 public Map<String, String> getConfiguration(String excelPath, String sheetName) throws IOException {
	        FileInputStream file = new FileInputStream(excelPath);
	        XSSFWorkbook workbook = new XSSFWorkbook(file);
	        XSSFSheet sheet = workbook.getSheet(sheetName);

	        if (sheet == null) {
	            workbook.close();
	            throw new IOException("Sheet '" + sheetName + "' not found!");
	        }

	        Row header = sheet.getRow(0);
	        Row dataRow = sheet.getRow(1);
	        Map<String, String> config = new HashMap<>();

	        if (header != null && dataRow != null) {
	            for (int i = 0; i < header.getLastCellNum(); i++) {
	                Cell keyCell = header.getCell(i);
	                Cell valueCell = dataRow.getCell(i);

	                if (keyCell != null && valueCell != null) {
	                    config.put(keyCell.getStringCellValue(), valueCell.getStringCellValue());
	                }
	            }
	        }

	        workbook.close();
	        return config;
	    }
	}