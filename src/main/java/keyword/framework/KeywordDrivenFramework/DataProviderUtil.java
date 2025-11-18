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
				data.add(new Object[] { row });
			}
			System.out.println("✅ Loaded " + data.size() + " rows from sheet: " + sheetName);
			return data.iterator();
		} catch (Exception e) {
			System.err.println("⚠️ Sheet not found or failed to load: " + sheetName + " → " + e.getMessage());
			return Collections.emptyIterator();
		}
	}

	// PURCHASE GROUPED LOADER → LOAD ONCE INTO SHARED QUEUE
	@DataProvider(name = "PurchaseGroupData", parallel = true)
	public static Object[][] groupedPurchaseData() throws IOException {

		if (reader == null) {
			return new Object[][] { { "USE_QUEUE" } };
		}

		Map<String, List<String>> grouped = reader.getGroupedTestData("Purchase");

		List<Map<String, Object>> data = new ArrayList<>();

		for (Map.Entry<String, List<String>> e : grouped.entrySet()) {
			Map<String, Object> tc = new HashMap<>();
			tc.put("TestCaseID", e.getKey());
			tc.put("Products", e.getValue());
			data.add(tc);
		}
		// Load into shared queue ONCE
		TestCaseQueue.loadPurchaseCases(data);
		System.out.println("✅ PurchaseGroupData provided: " + data.size() + " testcases");
		return new Object[][] { { "USE_QUEUE" } };
	}

	// REMOVE PRODUCT GROUPED LOADER → LOAD ONCE INTO SHARED QUEUE
	/*@DataProvider(name = "RemoveGroupData", parallel = true)
	public static Object[][] removeGroupData() throws IOException {

		if (reader == null) {
			return new Object[][] { { "USE_QUEUE" } };
		}

		Map<String, List<String>> grouped = reader.getGroupedTestData("RemoveProduct");

		List<Map<String, Object>> finalList = new ArrayList<>();

		for (Map.Entry<String, List<String>> e : grouped.entrySet()) {
			Map<String, Object> tc = new HashMap<>();
			tc.put("TestCaseID", e.getKey());
			tc.put("Products", e.getValue());
			finalList.add(tc);
		}

		// Load into shared queue ONCE
		TestCaseQueue.loadRemoveCases(finalList);

		// Dummy → test runs once per user
		return new Object[][] { { "USE_QUEUE" } };
	}*/

	// Login provider unchanged
    @DataProvider(name = "LoginData")
    public static Iterator<Object[]> loginData() throws IOException {
        List<Map<String, String>> list = reader.getTestDataRows("Login");
        List<Object[]> out = new ArrayList<>();
        for (Map<String, String> m : list) {
            out.add(new Object[] { m });
        }
        return out.iterator();
    }
}
