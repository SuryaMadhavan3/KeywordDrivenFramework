package keyword.framework.KeywordDrivenFramework;

import java.io.IOException;
import java.util.*;
import org.testng.annotations.DataProvider;

public class DataProviderUtil {

    private static final ExcelDataReader reader;

    static {
        ExcelDataReader temp = null;
        try {
            temp = new ExcelDataReader();
            System.out.println("✅ ExcelDataReader initialized successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to initialize ExcelDataReader: " + e.getMessage());
        }
        reader = temp;
    }

    private static Iterator<Object[]> getData(String sheetName) throws IOException {
        if (reader == null) {
            System.err.println("⚠️ ExcelDataReader not initialized; skipping sheet: " + sheetName);
            return Collections.emptyIterator();
        }

        try {
            List<Map<String, String>> testDataList = reader.getTestDataRows(sheetName);
            List<Object[]> data = new ArrayList<>();
            for (Map<String, String> row : testDataList) {
                data.add(new Object[]{row});
            }
            System.out.println("✅ Loaded " + data.size() + " rows from sheet: " + sheetName);
            return data.iterator();
        } catch (Exception e) {
            System.err.println("⚠️ Sheet not found or failed to load: " + sheetName + " → " + e.getMessage());
            return Collections.emptyIterator();
        }
    }
    
 // inside DataProviderUtil class (add these methods)

    private static Iterator<Object[]> getDataWide(String sheetName, String productPrefix) throws IOException {
        if (reader == null) {
            System.err.println("⚠️ ExcelDataReader not initialized; skipping sheet: " + sheetName);
            return Collections.emptyIterator();
        }

        List<Object[]> data = new ArrayList<>();
        List<Map<String, String>> rows = reader.getTestDataRows(sheetName);

        for (Map<String, String> row : rows) {
            // find all keys that start with the productPrefix (case-insensitive)
            for (String key : new ArrayList<>(row.keySet())) {
                if (key != null && key.toLowerCase().startsWith(productPrefix.toLowerCase())) {
                    String productValue = row.get(key);
                    if (productValue != null && !productValue.trim().isEmpty()) {
                        // create a new map for this product preserving other metadata (like Test Case id)
                        Map<String, String> single = new HashMap<>(row);
                        single.put("Product", productValue.trim()); // canonical key your steps expect
                        data.add(new Object[]{single});
                    }
                }
            }
        }
        System.out.println("✅ Expanded wide rows to " + data.size() + " single-product rows for sheet: " + sheetName);
        return data.iterator();
    }
    
    @DataProvider(name = "PurchaseGroupData", parallel = true)
    public static Iterator<Object[]> groupedPurchaseData() throws IOException {
        Map<String, List<String>> grouped = reader.getGroupedTestData("Purchase");
        List<Object[]> data = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
            Map<String, Object> testCase = new HashMap<>();
            testCase.put("TestCaseID", entry.getKey());
            testCase.put("Products", entry.getValue());
            data.add(new Object[]{testCase});
        }
        return data.iterator();
    }


    @DataProvider(name = "LoginData")
    public static Iterator<Object[]> loginData() throws IOException {
        return getData("Login");
    }

    @DataProvider(name = "PurchaseData")
    public static Iterator<Object[]> purchaseData() throws IOException {
        return getDataWide("Purchase", "Product");
    }

    @DataProvider(name = "RemoveData")
    public static Iterator<Object[]> removedata() throws IOException {
        return getData("RemoveProduct");
    }

    // Disabled until sheets exist
    // @DataProvider(name = "InvalidLoginData")
    // public static Iterator<Object[]> invalidlogindata() throws IOException {
    //     return getData("InvalidLogin");
    // }

    // @DataProvider(name = "InvalidMailData")
    // public static Iterator<Object[]> invalidmaildata() throws IOException {
    //     return getData("InvalidMailLogin");
    // }
}
