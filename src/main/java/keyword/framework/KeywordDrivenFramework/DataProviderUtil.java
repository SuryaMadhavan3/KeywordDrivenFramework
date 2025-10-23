package keyword.framework.KeywordDrivenFramework;

import java.io.IOException;
import java.util.*;
import org.testng.annotations.DataProvider;

public class DataProviderUtil {

	 private static final ExcelDataReader reader = new ExcelDataReader();

    /**
     * Generic reusable method to get test data for any sheet
     */
    private static Iterator<Object[]> getData(String sheetName) throws IOException {
        List<Map<String, String>> testDataList = reader.getTestDataRows(sheetName);
        List<Object[]> data = new ArrayList<>();

        for (Map<String, String> row : testDataList) {
            data.add(new Object[]{ row });
        }
        return data.iterator();
    }

    // 🔹 One-liner DataProviders
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
    @DataProvider(name = "InvalidLoginData")
    public static Iterator<Object[]> invalidlogindata() throws IOException {
        return getData("InvalidLogin");
    }
    
    @DataProvider(name = "InvalidMailData")
    public static Iterator<Object[]> invalidmaildata() throws IOException {
        return getData("InvalidMailLogin");
    }

    

    // 💡 When you add new modules, just add one line above!
}