package keyword.framework.core;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigReader {

    /**
     * Reads configuration key-value pairs from the "Configuration" sheet of ConfigData.xlsx
     * @return Map<String, String> containing config key-value pairs
     */
    public Map<String, String> getConfigData() throws IOException {

        String configPath = FrameworkPaths.CONFIG_PATH;
        Map<String, String> configMap = new LinkedHashMap<>();

        try (FileInputStream fis = new FileInputStream(configPath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Configuration");
            if (sheet == null) {
                throw new RuntimeException("❌ Missing sheet 'Configuration' in ConfigData.xlsx");
            }

            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {

                Row row = sheet.getRow(r);
                if (row == null) continue;

                Cell keyCell = row.getCell(0);
                Cell valueCell = row.getCell(1);

                if (keyCell == null || valueCell == null) continue;

                String key = formatter.formatCellValue(keyCell, evaluator).trim();
                String value = formatter.formatCellValue(valueCell, evaluator).trim();

                if (!key.isEmpty()) {
                    configMap.put(key, value);
                }
            }
        }

        System.out.println("⚙ Loaded ConfigData.xlsx → " + configMap.size() + " entries");
        return configMap;
    }
}
