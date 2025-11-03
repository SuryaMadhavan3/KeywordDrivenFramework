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

    @DataProvider(name = "LoginData")
    public static Iterator<Object[]> loginData() throws IOException {
        return getData("Login");
    }

    @DataProvider(name = "PurchaseData")
    public static Iterator<Object[]> purchaseData() throws IOException {
        return getData("Purchase");
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
